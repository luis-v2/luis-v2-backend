package at.luis_v2.luis_v2_backend.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.luis_v2.luis_v2_backend.dto.DataComponent;
import at.luis_v2.luis_v2_backend.dto.DataPoint;
import at.luis_v2.luis_v2_backend.dto.DataRequest;
import at.luis_v2.luis_v2_backend.dto.DataTrend;
import at.luis_v2.luis_v2_backend.service.DataService;
import at.luis_v2.luis_v2_backend.service.ScrapeService;

@RestController
@RequestMapping("/api/trend")
public class TrendController {
    
    private final DataService dataService;
    private final ScrapeService scrapeService;

    public TrendController(DataService dataService, ScrapeService scrapeService) {
        this.dataService = dataService;
        this.scrapeService = scrapeService;
    }

    @GetMapping
    public List<DataTrend> getTrends(@RequestParam(required = true) int stationId,
                                     @RequestParam(required = true) int days) {

        List<DataComponent> components = scrapeService.getComponentsForStation(stationId);
        
        DataRequest request = new DataRequest();
        request.setStation(stationId);
        request.setComponents(components.stream()
            .map(DataComponent::getId)
            .toList());
        request.setEndDate(LocalDate.now());
        request.setStartDate(LocalDate.now().minusDays(days * 2));
        request.setAverage(1);

        List<DataComponent> data = dataService.getData(request);

        List<DataTrend> trends = new ArrayList<>();

        Instant now = Instant.now();
        Instant beginCurrentTimespan = now.minus(days, ChronoUnit.DAYS);
        Instant beginPreviousTimespan = now.minus(days * 2, ChronoUnit.DAYS);

        data.forEach(component -> {
            DataTrend trend = new DataTrend();

            trend.setPreviousValue(component.getDataPoints().stream()
            .filter(dp -> {
                Instant ts = dp.getTimestamp();
                return ts.isAfter(beginPreviousTimespan) && ts.isBefore(beginCurrentTimespan);
            })
            .toList()
            .stream()
            .mapToDouble(DataPoint::getValue)
            .average()
            .orElse(0.0));

            trend.setCurrentValue(component.getDataPoints().stream()
            .filter(dp -> {
                Instant ts = dp.getTimestamp();
                return ts.isAfter(beginCurrentTimespan) && ts.isBefore(now);
            })
            .toList()
            .stream()
            .mapToDouble(DataPoint::getValue)
            .average()
            .orElse(0.0));

            component.setDataPoints(null);

            trend.setComponent(component);

            trends.add(trend);
        });

        return trends;
    }

}
