package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

public class TimeSeriesJsonGenerator {

    // 每個 series 的設定
    static class SeriesConfig {
        String name;
        int min;
        int max;

        public SeriesConfig(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
        }
    }

    public static void main(String[] args) throws IOException {
        List<SeriesConfig> seriesConfigs = Arrays.asList(
                new SeriesConfig("series1", 100, 200),
                new SeriesConfig("series2", 80, 120),
                new SeriesConfig("series3", 180, 260)
        );

        int deltaRange = 5; // 每筆最大變動量 +/-5
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        List<Map<String, Object>> resultList = new ArrayList<>();
        Random random = new Random();

        // 儲存上一筆值
        Map<String, Integer> previousValues = new HashMap<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, Object> entry = new LinkedHashMap<>();
            long timestamp = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
            entry.put("timestamp", timestamp);

            for (SeriesConfig config : seriesConfigs) {
                int value;
                if (!previousValues.containsKey(config.name)) {
                    value = random.nextInt(config.max - config.min + 1) + config.min;
                } else {
                    int delta = random.nextInt(deltaRange * 2 + 1) - deltaRange; // -5 ~ 5
                    value = previousValues.get(config.name) + delta;
                    value = Math.max(config.min, Math.min(config.max, value)); // 確保不超出範圍
                }
                previousValues.put(config.name, value);
                entry.put(config.name, value);
            }

            resultList.add(entry);
        }

        // 輸出 JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String jsonOutput = mapper.writeValueAsString(resultList);
        System.out.println(jsonOutput);
    }
}
