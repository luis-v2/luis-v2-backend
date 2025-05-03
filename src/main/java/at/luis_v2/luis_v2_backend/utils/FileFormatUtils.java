package at.luis_v2.luis_v2_backend.utils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import at.luis_v2.luis_v2_backend.dto.DataComponent;
import at.luis_v2.luis_v2_backend.dto.DataPoint;

public class FileFormatUtils {
    
    public static String exportFlatJson(List<DataComponent> components) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<Instant, Map<String, Double>> grouped = new TreeMap<>();

        for (DataComponent component : components) {
            String componentName = component.getName().toLowerCase();

            for (DataPoint dp : component.getDataPoints()) {
                grouped
                    .computeIfAbsent(dp.getTimestamp(), d -> new LinkedHashMap<>())
                    .put(componentName, dp.getValue());
            }
        }

        // JSON flach erzeugen
        ArrayNode resultArray = mapper.createArrayNode();

        for (Map.Entry<Instant, Map<String, Double>> entry : grouped.entrySet()) {
            ObjectNode row = mapper.createObjectNode();
            row.put("timestamp", entry.getKey().toString());

            for (Map.Entry<String, Double> val : entry.getValue().entrySet()) {
                row.put(val.getKey(), val.getValue());
            }

            resultArray.add(row);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultArray);
    }

    public static String exportFlatCsv(List<DataComponent> components) {
    Map<Instant, Map<String, Double>> grouped = new TreeMap<>();
    Set<String> componentNames = new TreeSet<>(); // sortierte Spaltenüberschriften

    for (DataComponent component : components) {
        String componentName = component.getName().toLowerCase();
        componentNames.add(componentName);

        for (DataPoint dp : component.getDataPoints()) {
            grouped
                .computeIfAbsent(dp.getTimestamp(), d -> new LinkedHashMap<>())
                .put(componentName, dp.getValue());
        }
    }

    StringBuilder sb = new StringBuilder();

    // CSV-Kopf
    sb.append("timestamp");
    for (String compId : componentNames) {
        sb.append(";").append(compId);
    }
    sb.append("\n");

    // CSV-Datenzeilen
    for (Map.Entry<Instant, Map<String, Double>> entry : grouped.entrySet()) {
        sb.append(entry.getKey().toString());

        Map<String, Double> values = entry.getValue();
        for (String compId : componentNames) {
            sb.append(";");
            Double value = values.get(compId);
            if (value != null) {
                sb.append(value);
            }
        }
        sb.append("\n");
    }

    return sb.toString();
}

}
