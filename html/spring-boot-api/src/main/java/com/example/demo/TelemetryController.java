package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    @GetMapping
    public List<Map<String, Object>> getTelemetry(
            @RequestParam("from") long from,
            @RequestParam("to") long to) {

        List<Map<String, Object>> data = new ArrayList<>();
        long step = 1000; // 每秒一筆
        for (long t = from; t <= to; t += step) {
            double value = 50 + Math.sin(t / 10000.0) * 10 + Math.random();
            Map<String, Object> point = new HashMap<>();
            point.put("time", t);
            point.put("value", value);
            data.add(point);
        }
        return data;
    }
}

