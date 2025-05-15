package at.luis_v2.luis_v2_backend.service;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import at.luis_v2.luis_v2_backend.components.LuisConfig;
import at.luis_v2.luis_v2_backend.dto.DataAverage;
import at.luis_v2.luis_v2_backend.dto.DataComponent;
import at.luis_v2.luis_v2_backend.dto.DataStation;

@Service
public class ScrapeService {
    
    private final LuisConfig config;

    @Autowired
    public ScrapeService(LuisConfig config) {
        this.config = config;
    }

    public List<DataStation> getStations() {

        Document doc = loadDocument(config.getScrapeUrl());

        Element select = doc.selectFirst("select[name=\"station1\"]");
        Elements options = select.select("option");

        List<DataStation> stationList = new ArrayList<>();
        for (Element option : options) {
            String value = option.attr("value");
            String label = option.text().replace(";", "");

            if (value != null && !value.isEmpty()) {
                int id = Integer.parseInt(value);
                DataStation station = new DataStation(id, label, null);
                stationList.add(station);
            }
        }

        return stationList;
    }

    public List<DataComponent> getComponentsForStation(int stationId) {

        Document doc = loadDocument(config.getScrapeUrl() + "?station1=" + stationId);

        Element select = doc.selectFirst("select[name=\"komponente1\"]");
        Elements options = select.select("option");

        List<DataComponent> componentList = new ArrayList<>();
        for (Element option : options) {
            String value = option.attr("value");
            String label = option.text().replace(";", "");

            if (value != null && !value.isEmpty()) {
                int id = Integer.parseInt(value);
                DataComponent station = new DataComponent(id, label, null, null);
                componentList.add(station);
            }
        }

        return componentList;
    }

    public List<DataAverage> getAverages() {

        Document doc = loadDocument(config.getScrapeUrl());

        Element select = doc.selectFirst("select[name=\"mittelwert\"]");
        Elements options = select.select("option");

        List<DataAverage> averageList = new ArrayList<>();
        for (Element option : options) {
            String value = option.attr("value");
            String label = option.text().replace(";", "");

            if (value != null && !value.isEmpty()) {
                int id = Integer.parseInt(value);
                DataAverage station = new DataAverage(id, label);
                averageList.add(station);
            }
        }

        return averageList;
    }

    private Document loadDocument(String url) {
        RestTemplate restTemplate = new RestTemplate();

        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("Windows-1252"));
        restTemplate.setMessageConverters(Collections.singletonList(converter));

        String html = restTemplate.getForObject(url, String.class);

        Document doc = Jsoup.parse(html);
        return doc;
    }
}
