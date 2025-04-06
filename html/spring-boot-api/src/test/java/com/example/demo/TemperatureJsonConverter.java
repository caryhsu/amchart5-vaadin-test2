package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemperatureJsonConverter {
    public static void main(String[] args) {
        Map<String, List<List<Number>>> result = new HashMap<>();
        result.put("helsinki", new ArrayList<>());
        result.put("turku", new ArrayList<>());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(TemperatureJsonConverter.class.getResourceAsStream("/temperature.csv"))))) {
            String header = reader.readLine(); // 讀欄位名稱
            if (header == null) {
                System.err.println("CSV file is empty");
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String dateStr = parts[0];
                double series1_min = Double.parseDouble(parts[1]);
                double series1_max = Double.parseDouble(parts[2]);
                double series2_min = series1_min + 8 + 3.0 * Math.random();
                double series2_max = series1_max + 8 + 3.0 * Math.random();

                // 轉換成 timestamp（毫秒）
                long timestamp = LocalDate.parse(dateStr)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
                        .toEpochMilli();

                // 添加 Helsinki 數據
                result.get("helsinki").add(List.of(timestamp, series1_min, series1_max));

                // 添加 Turku 數據
                result.get("turku").add(List.of(timestamp, series2_min, series2_max));
            }

            // 將結果寫入 JSON 文件
            ObjectMapper mapper = new ObjectMapper();
            try (FileWriter writer = new FileWriter("src/main/resources/temperature-t.json")) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, result);
                System.out.println("JSON file has been created successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}