package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

public class TemperatureCsvConverter {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(TemperatureCsvConverter.class.getResourceAsStream("/temperature.csv"))))) {
            String header = reader.readLine(); // 讀欄位名稱
            if (header == null) {
                System.err.println("CSV file is empty");
                return;
            }

            // 輸出新的欄位名稱
            System.out.println("name,time,min,max");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String name1 = "Helsinki";
                String name2 = "Turku";
                String dateStr = parts[0];
                String series1_min = parts[1];
                String series1_max = parts[2];
                String series2_min = String.valueOf(Double.parseDouble(series1_min) + 8 + 3.0 * Math.random());
                String series2_max = String.valueOf(Double.parseDouble(series1_max) + 8 + 3.0 * Math.random());

                // 轉換成 timestamp（毫秒）
                long timestamp = LocalDate.parse(dateStr)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
                        .toEpochMilli();

                System.out.println(name1 + "," + 
                    timestamp + "," + 
                    series1_min + "," + 
                    series1_max
                    );

                System.out.println(name2 + "," +
                    timestamp + "," +
                    series2_min + "," +
                    series2_max
                    );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
