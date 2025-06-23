package at.luis_v2.luis_v2_backend.service;

import at.luis_v2.luis_v2_backend.components.LuisConfig;
import at.luis_v2.luis_v2_backend.dto.DataComponent;
import at.luis_v2.luis_v2_backend.dto.DataPoint;
import at.luis_v2.luis_v2_backend.dto.DataRequest;
import at.luis_v2.luis_v2_backend.dto.Forecast;
import at.luis_v2.luis_v2_backend.repository.ForecastRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.hssf.extractor.OldExcelExtractor;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DataService {

    private final WebClient webClient;
    private final LuisConfig config;
    private final ForecastRepository forecastRepository;

    @Autowired
    public DataService(WebClient.Builder webClientBuilder, LuisConfig config, ForecastRepository forecastRepository) {
        this.webClient = webClientBuilder.build();
        this.config = config;
        this.forecastRepository = forecastRepository;
    }

    public List<DataComponent> getData(DataRequest request) {
        
        // Get date blocks
        List<LocalDate[]> dateBlocks = new ArrayList<>();

        LocalDate currentStartDate = request.getStartDate();
        while (currentStartDate.isBefore(request.getEndDate()) || currentStartDate.equals(request.getEndDate())) {
            LocalDate currentEndDate = currentStartDate.plusDays(59);

            if (currentEndDate.isAfter(request.getEndDate())) {
                currentEndDate = request.getEndDate();
            }
            dateBlocks.add(new LocalDate[] {currentStartDate, currentEndDate});

            currentStartDate = currentEndDate.plusDays(1);

            if (currentStartDate.equals(request.getEndDate())) break;
        }

        // Build the API request URL for each date block
        List<Tuple2<Integer, String>> requestData = new ArrayList<>();

        dateBlocks.forEach(dateBlock -> {
            LocalDate startDate = dateBlock[0];
            LocalDate endDate = dateBlock[1];

            request.getComponents().forEach(component -> {
                try {
                    URIBuilder uriBuilder = new URIBuilder(config.getExportUrl())
                        .addParameter("station1", String.valueOf(request.getStation()))
                        .addParameter("komponente1", component.toString())
                        .addParameter("von_tag", String.valueOf(startDate.getDayOfMonth()))
                        .addParameter("von_monat", String.valueOf(startDate.getMonthValue()))
                        .addParameter("von_jahr", String.valueOf(startDate.getYear()))
                        .addParameter("bis_tag", String.valueOf(endDate.getDayOfMonth()))
                        .addParameter("bis_monat", String.valueOf(endDate.getMonthValue()))
                        .addParameter("bis_jahr", String.valueOf(endDate.getYear()))
                        .addParameter("mittelwert", String.valueOf(request.getAverage()));
                    
                    requestData.add(Tuples.of(component, uriBuilder.build().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        // Fetch data from the API for each URL
        List<DataComponent> dataComponents = Flux.fromIterable(requestData)
            .parallel(10)
            .runOn(Schedulers.boundedElastic())
            .flatMap(req -> webClient.get()
                .uri(req.getT2())
                .retrieve()
                .bodyToMono(byte[].class)
                .map(fileBytes -> parseXlsFileForComponent(req.getT1(), fileBytes))
                .onErrorResume(e -> {
                    return Mono.empty();
                })
            )
            .sequential()
            .collectList()
            .block();

        // Merge all data components into one list
        Map<Integer, List<DataComponent>> dataComponentsGroupedById = dataComponents.stream()
            .collect(Collectors.groupingBy(DataComponent::getId));

        List<DataComponent> mergedComponents = new ArrayList<>();
        for (Map.Entry<Integer, List<DataComponent>> entry : dataComponentsGroupedById.entrySet()) {
            DataComponent mergedComponent = mergeDataComponents(entry.getValue());
            mergedComponents.add(mergedComponent);
        }
        if (request.isAddForecasts() && !mergedComponents.isEmpty()) {
            for (DataComponent component : mergedComponents) {
                List<Forecast> forecasts = forecastRepository.findByStationAndComponentAndTimestampBetween(
                        String.valueOf(request.getStation()), // ACHTUNG: station als String
                        component.getId().toString(),      // oder getComponent(), falls vorhanden
                        request.getEndDate().atStartOfDay(),
                        request.getEndDate().plusDays(2).atTime(23, 59)
                );

                System.out.println("Found " + forecasts.size() + " forecasts for component: " + component.getName());

                if (!forecasts.isEmpty()) {
                    List<DataPoint> forecastPoints = forecasts.stream()
                            .map(f -> new DataPoint(
                                    f.getTimestamp().toInstant(ZoneOffset.UTC),
                                    Double.parseDouble(f.getValue())
                            ))
                            .toList();

                    component.dataPoints.addAll(forecastPoints);
                    component.dataPoints.sort(Comparator.comparing(DataPoint::getTimestamp));
                }
            }
        }

        return mergedComponents;
    }


    private DataComponent parseXlsFileForComponent(Integer componentId, byte[] fileBytes) {
        DataComponent dataComponent = new DataComponent();
        dataComponent.setId(componentId);
        dataComponent.setDataPoints(new ArrayList<>());

        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            // Use OldExcelExtractor to extract text from the XLS file: File format is too old for XSSF
            // and HSSF is not supported in the latest versions of Apache POI

            try (OldExcelExtractor extractor = new OldExcelExtractor(is)) {
                byte[] rawBytes = extractor.getText().getBytes();
                String text = new String(rawBytes, Charset.forName("Windows-1252"));

                // Parse values from the extracted text
                String[] rawSheet = text.split("\n");
               
                // Element at seconds index contains infos about the component and unit
                String metadata = rawSheet[2];

                // Extract the component name
                String componentName = metadata.split(" ")[0]; 

                // Extract the unit from the metadata string using regex
                Matcher unitMatcher = Pattern.compile("\\[(.*?)]").matcher(metadata);
                String unit = null;
                if (unitMatcher.find()) {
                    unit = unitMatcher.group(1).replace(";", "").replace("Â", "");
                }

                dataComponent.setName(componentName);
                dataComponent.setUnit(unit);

                // Assuming the first 6 values are headers or metadata
                String[] rawCellValues = Arrays.copyOfRange(rawSheet, 6, rawSheet.length);

                // Always 3 values per row: Date, Time, Value
                for (int i = 0; i < rawCellValues.length; i += 3) {

                    String rawDate = rawCellValues[i].trim();
                    String rawTime = rawCellValues[i + 1].trim();
                    String rawValue = rawCellValues[i + 2].trim();

                    // Combine date and time
                    String rawDateTime = rawDate + " " + rawTime;

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
                    LocalDateTime localDateTime = LocalDateTime.parse(rawDateTime, formatter);

                    // Assuming the date is in the Europe/Vienna timezone
                    ZoneId zone = ZoneId.of("Europe/Vienna");
                    ZonedDateTime zoned = localDateTime.atZone(zone);

                    // Convert to UTC timestamp
                    Instant timestamp = zoned.toInstant();

                    dataComponent.dataPoints.add(new DataPoint(timestamp, Double.parseDouble(rawValue)));
                }
            } catch (Exception e) {
                // Handle the exception for OldExcelExtractor
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataComponent;
    }

    private DataComponent mergeDataComponents(List<DataComponent> dataComponents) {
        DataComponent mergedComponent = new DataComponent();
        mergedComponent.setId(dataComponents.get(0).getId());
        mergedComponent.setName(dataComponents.get(0).getName());
        mergedComponent.setUnit(dataComponents.get(0).getUnit());
        mergedComponent.setDataPoints(new ArrayList<>());

        for (DataComponent component : dataComponents) {
            mergedComponent.getDataPoints().addAll(component.getDataPoints());
        }

        // Sort the data points by timestamp
        mergedComponent.getDataPoints().sort((dp1, dp2) -> dp1.getTimestamp().compareTo(dp2.getTimestamp()));

        return mergedComponent;

    }
}
