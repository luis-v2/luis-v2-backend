package at.luis_v2.luis_v2_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.luis_v2.luis_v2_backend.dto.DataComponent;
import at.luis_v2.luis_v2_backend.dto.DataStation;
import at.luis_v2.luis_v2_backend.service.ScrapeService;

@RestController
@RequestMapping("/api/station")
public class StationController {

    private final ScrapeService scrapeService;

    public StationController(ScrapeService scrapeService) {
        this.scrapeService = scrapeService;
    }

    @GetMapping
    public List<DataStation> getStations() {
        return scrapeService.getStations();
    }

    @GetMapping("{id}/component")
    public List<DataComponent> getComponentsForStation(@PathVariable int id) {
        return scrapeService.getComponentsForStation(id);
    }
    
}
