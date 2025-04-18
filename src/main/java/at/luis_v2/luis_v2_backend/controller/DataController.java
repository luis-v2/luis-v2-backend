package at.luis_v2.luis_v2_backend.controller;

import at.luis_v2.luis_v2_backend.dto.DataRequest;
import at.luis_v2.luis_v2_backend.service.DataService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping
    public ResponseEntity<InputStreamResource> getData(@Valid @RequestBody DataRequest request) {

        ByteArrayInputStream dataStream = dataService.generateData(request);
        String fileType = request.getFileFormat().toLowerCase();

        MediaType mediaType = switch (fileType) {
            case "csv" -> MediaType.parseMediaType("text/csv");
            case "json" -> MediaType.APPLICATION_JSON;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        String filename = "airdata." + fileType;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(new InputStreamResource(dataStream));
    }
}
