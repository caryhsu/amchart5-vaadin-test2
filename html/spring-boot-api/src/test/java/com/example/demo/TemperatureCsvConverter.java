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
            System.out.println("time,min,max");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String dateStr = parts[0];
                String min = parts[1];
                String max = parts[2];

                // 轉換成 timestamp（毫秒）
                long timestamp = LocalDate.parse(dateStr)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
                        .toEpochMilli();

                System.out.println(timestamp + "," + min + "," + max);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
