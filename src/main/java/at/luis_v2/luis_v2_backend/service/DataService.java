package at.luis_v2.luis_v2_backend.service;

import at.luis_v2.luis_v2_backend.dto.DataRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class DataService {

    public ByteArrayInputStream generateData(DataRequest request) {

        // TODO: Holt die Daten aus der DB, CSV oder API (noch zu implementieren)
        StringBuilder sb = new StringBuilder();

        if ("csv".equalsIgnoreCase(request.getFileFormat())) {
            sb.append("Station,Component,Date,Value\n");
            sb.append("Graz,NO2,2025-04-18,42\n");
            sb.append("Graz,O3,2025-04-18,55\n");
        } else if ("json".equalsIgnoreCase(request.getFileFormat())) {
            sb.append("[{\"station\":\"Graz\",\"component\":\"NO2\",\"value\":42}]");
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + request.getFileFormat());
        }

        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
