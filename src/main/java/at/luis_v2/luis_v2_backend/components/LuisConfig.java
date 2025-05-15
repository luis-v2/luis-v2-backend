package at.luis_v2.luis_v2_backend.components;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "luis")
public class LuisConfig {
    private String exportUrl;
    private String scrapeUrl;

    public String getExportUrl() {
        return exportUrl;
    }

    public void setExportUrl(String exportUrl) {
        this.exportUrl = exportUrl;
    }

    public String getScrapeUrl() {
        return scrapeUrl;
    }
    
    public void setScrapeUrl(String scrapeUrl) {
        this.scrapeUrl = scrapeUrl;
    }
}
