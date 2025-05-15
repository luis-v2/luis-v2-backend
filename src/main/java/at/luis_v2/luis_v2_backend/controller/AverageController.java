package at.luis_v2.luis_v2_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.luis_v2.luis_v2_backend.dto.DataAverage;
import at.luis_v2.luis_v2_backend.service.ScrapeService;

@RestController
@RequestMapping("/api/average")
public class AverageController {

    private final ScrapeService scrapeService;

    public AverageController(ScrapeService scrapeService) {
        this.scrapeService = scrapeService;
    }

    @GetMapping
    public List<DataAverage> getAverages() {
        return scrapeService.getAverages();
    }
    
}
