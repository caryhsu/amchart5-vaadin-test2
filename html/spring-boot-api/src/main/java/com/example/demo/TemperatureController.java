package com.example.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/temperature")
public class TemperatureController {

    @GetMapping("/csv")
    public ResponseEntity<byte[]> getTemperatures_Csv(
            @RequestParam("from") long from,
            @RequestParam("to") long to) {

        try {
            ClassPathResource resource = new ClassPathResource("temperature-t.csv");
            InputStream inputStream = resource.getInputStream();
            byte[] csvBytes = inputStream.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"temperature.csv\"")
                    .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                    .body(csvBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error reading CSV: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/json")
    public ResponseEntity<byte[]> getTemperatures_Json(
            @RequestParam("from") long from,
            @RequestParam("to") long to) {

        try {
            ClassPathResource resource = new ClassPathResource("temperature-t.json");
            InputStream inputStream = resource.getInputStream();
            byte[] jsonBytes = inputStream.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"temperature.json\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error reading JSON: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/navigator")
    public ResponseEntity<Map<String, List<List<Number>>>> getNavigatorData() {
        try {
            ClassPathResource resource = new ClassPathResource("temperature-t.json");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<List<Number>>> fullData = mapper.readValue(resource.getInputStream(), 
                new TypeReference<Map<String, List<List<Number>>>>() {});
            
            Map<String, List<List<Number>>> navigatorData = new HashMap<>();
            navigatorData.put("helsinki", new ArrayList<>());
            navigatorData.put("turku", new ArrayList<>());
    
            // 計算平均溫度
            for (List<List<Number>> cityData : fullData.values()) {
                String cityName = fullData.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == cityData)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .get();
    
                for (List<Number> point : cityData) {
                    long timestamp = point.get(0).longValue();
                    double avgTemp = (point.get(1).doubleValue() + point.get(2).doubleValue()) / 2;
                    navigatorData.get(cityName).add(List.of(timestamp, avgTemp));
                }
            }
    
            return ResponseEntity.ok(navigatorData);
    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

