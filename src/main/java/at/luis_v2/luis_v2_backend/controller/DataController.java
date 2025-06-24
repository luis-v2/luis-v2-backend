package at.luis_v2.luis_v2_backend.controller;

import at.luis_v2.luis_v2_backend.dto.DataRequest;
import at.luis_v2.luis_v2_backend.service.DataService;
import at.luis_v2.luis_v2_backend.utils.FileFormatUtils;
import jakarta.validation.Valid;

import java.io.File;
import java.nio.file.Files;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping
    public ResponseEntity<?> getData(@Valid @RequestBody DataRequest request) {
        try {

            String fileType = request.getFileFormat().toLowerCase();

            switch (fileType) {
                case "csv":
                    return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType("text/csv"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=airdata.csv")
                        .body(FileFormatUtils.exportFlatCsv(dataService.getData(request)));
            
                case "json":
                    return ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(FileFormatUtils.exportFlatJson(dataService.getData(request)));

                case "parquet":
                    // Parquet file temporär erzeugen
                    File parquetFile = FileFormatUtils.exportParquetToTempFile(dataService.getData(request));
                    byte[] parquetBytes = Files.readAllBytes(parquetFile.toPath());
                    parquetFile.delete();

                    return ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=airdata.parquet")
                        .body(parquetBytes);
                default:
                    return ResponseEntity
                        .badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"Unsupported file format. Supported formats are: csv, json.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                .internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }
}
