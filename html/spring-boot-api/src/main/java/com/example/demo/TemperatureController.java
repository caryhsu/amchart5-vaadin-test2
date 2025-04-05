package com.example.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // read from src/main/resources/tempature.csv
    // return the csv data to client
    @GetMapping()
    public ResponseEntity<byte[]> getTemperatures(
            @RequestParam("from") long from,
            @RequestParam("to") long to) {

        // List<Map<String, Object>> data = new ArrayList<>();
        // long step = 1000; // 每秒一筆
        // for (long t = from; t <= to; t += step) {
        //     double value = 50 + Math.sin(t / 10000.0) * 10 + Math.random();
        //     Map<String, Object> point = new HashMap<>();
        //     point.put("time", t);
        //     point.put("value", value);
        //     data.add(point);
        // }
        // return data;

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
}

