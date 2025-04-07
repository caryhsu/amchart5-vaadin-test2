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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/temperature")
public class TemperatureController {

    @GetMapping("/{city}/navigator/csv")
    public ResponseEntity<byte[]> getTemperatures_navigator(
            @PathVariable String city) {

        try {
            System.out.println("----------------------------------------------->>>");
            System.out.println("city = " + city + ", ");
            System.out.println("--------------------------------------------------");

            ClassPathResource resource = new ClassPathResource("temperature-t.json");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<List<Number>>> fullData = mapper.readValue(resource.getInputStream(), 
                new TypeReference<Map<String, List<List<Number>>>>() {});
            
            List<List<Number>> cityData = fullData.get(city);
            if (cityData == null) {
                return ResponseEntity.notFound().build();
            }
                
            // 將 result 轉換為 CSV 格式
            StringBuilder csv = new StringBuilder();
            csv.append("timestamp,value\n"); // CSV 標題行
            
            for (List<Number> point : cityData) {
                double low = point.get(1).doubleValue();
                double high = point.get(2).doubleValue();
                double average = (low + high) / 2;
                
                csv.append(String.format("%d,%.1f\n",
                    point.get(0).longValue(), // timestamp
                    average
                ));
            }
            
            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"temperature.csv\"")
                    .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                    .body(csvBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error reading CSV: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/{city}/csv")
    public ResponseEntity<byte[]> getTemperatures_Csv(
            @PathVariable String city,
            @RequestParam(value = "from", required = false) Double from,
            @RequestParam(value = "to", required = false) Double to) {

        try {
            System.out.println("----------------------------------------------->>>");
            System.out.print("city = " + city + ", ");
            System.out.print("from = " + from + 
                (from != null ? " (" + LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(from.longValue()), 
                    ZoneId.systemDefault()) + ")" : "") + ", ");
            System.out.println("to = " + to + 
                (to != null ? " (" + LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(to.longValue()), 
                    ZoneId.systemDefault()) + ")" : ""));

            System.out.println("--------------------------------------------------");


            ClassPathResource resource = new ClassPathResource("temperature-t.json");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<List<Number>>> fullData = mapper.readValue(resource.getInputStream(), 
                new TypeReference<Map<String, List<List<Number>>>>() {});
            
            List<List<Number>> cityData = fullData.get(city);
            if (cityData == null) {
                return ResponseEntity.notFound().build();
            }
    
                
            List<List<Number>> filterList = cityData.stream()
                .filter(point -> {
                    long timestamp = point.get(0).longValue();
                    return (from == null || timestamp >= from) &&
                           (to == null || timestamp <= to);
                })
                .toList();

            List<List<Number>> result;
            
            if (from != null && to != null) {
                Instant fromInstant = Instant.ofEpochMilli(from.longValue());
                Instant toInstant = Instant.ofEpochMilli(to.longValue());

                Duration duration = Duration.between(fromInstant, toInstant);
                long days = duration.toDays();
                long hours = duration.toHours();
                long leftHours = hours % 24;

                if (days > 30 * 6) {
                    // 內差法每6小時一筆
                    long step = 6 * 60 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (days > 14) {
                    // 內差法每兩小時一筆
                    long step = 2 * 60 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (days > 7) {
                    // 內差法每小時一筆
                    long step = 60 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (hours > 30) {
                    // 內差法每五分鐘一筆
                    long step = 5 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (hours > 7) {
                    // 內差法30秒一筆
                    long step = 30 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else {
                    // 內差法每秒鐘一筆
                    long step = 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
            } 
            else {
                result = filterList;
            }

            // 將 result 轉換為 CSV 格式
            StringBuilder csv = new StringBuilder();
            csv.append("timestamp,low,high,average\n"); // CSV 標題行
            
            for (List<Number> point : result) {
                double low = point.get(1).doubleValue();
                double high = point.get(2).doubleValue();
                double average = (low + high) / 2;
                
                csv.append(String.format("%d,%.1f,%.1f,%.1f\n",
                    point.get(0).longValue(), // timestamp
                    low,
                    high,
                    average
                ));
            }
            
            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"temperature.csv\"")
                    .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                    .body(csvBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error reading CSV: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/{city}/json")
    public ResponseEntity<List<List<Number>>> getTemperatures_Json(
            @PathVariable String city,
            @RequestParam(value = "from", required = false) Double from,
            @RequestParam(value = "to", required = false) Double to) {

        try {
            System.out.println("----------------------------------------------->>>");
            System.out.print("city = " + city + ", ");
            System.out.print("from = " + from + 
                (from != null ? " (" + LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(from.longValue()), 
                    ZoneId.systemDefault()) + ")" : "") + ", ");
            System.out.println("to = " + to + 
                (to != null ? " (" + LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(to.longValue()), 
                    ZoneId.systemDefault()) + ")" : ""));

            System.out.println("--------------------------------------------------");

            ClassPathResource resource = new ClassPathResource("temperature-t.json");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<List<Number>>> fullData = mapper.readValue(resource.getInputStream(), 
                new TypeReference<Map<String, List<List<Number>>>>() {});
            
            List<List<Number>> cityData = fullData.get(city);
            if (cityData == null) {
                return ResponseEntity.notFound().build();
            }

            List<List<Number>> filterList = cityData.stream()
                .filter(point -> {
                    long timestamp = point.get(0).longValue();
                    return (from == null || timestamp >= from) &&
                           (to == null || timestamp <= to);
                })
                .toList();

            List<List<Number>> result;
            
            if (from != null && to != null) {
                Instant fromInstant = Instant.ofEpochMilli(from.longValue());
                Instant toInstant = Instant.ofEpochMilli(to.longValue());

                Duration duration = Duration.between(fromInstant, toInstant);
                long days = duration.toDays();
                long hours = duration.toHours();
                long leftHours = hours % 24;

                if (days > 30 * 6) {
                    // 內差法每6小時一筆
                    long step = 6 * 60 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (days > 14) {
                    // 內差法每兩小時一筆
                    long step = 2 * 60 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (days > 7) {
                    // 內差法每小時一筆
                    long step = 60 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (hours > 30) {
                    // 內差法每五分鐘一筆
                    long step = 5 * 60 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (hours > 7) {
                    // 內差法30秒一筆
                    long step = 30 * 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else {
                    // 內差法每秒鐘一筆
                    long step = 1000;
                    result = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
            } 
            else {
                result = filterList;
            }
 
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/{city}/candlestick")
    public ResponseEntity<List<List<Number>>> getTemperatures_Candlesitck(
            @PathVariable String city,
            @RequestParam(value = "from", required = false) Double from,
            @RequestParam(value = "to", required = false) Double to) {

        try {
            System.out.println("----------------------------------------------->>>");
            System.out.print("city = " + city + ", ");
            System.out.print("from = " + from + 
                (from != null ? " (" + LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(from.longValue()), 
                    ZoneId.systemDefault()) + ")" : "") + ", ");
            System.out.println("to = " + to + 
                (to != null ? " (" + LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(to.longValue()), 
                    ZoneId.systemDefault()) + ")" : ""));

            System.out.println("--------------------------------------------------");

            ClassPathResource resource = new ClassPathResource("temperature-t.json");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<List<Number>>> fullData = mapper.readValue(resource.getInputStream(), 
                new TypeReference<Map<String, List<List<Number>>>>() {});
            
            List<List<Number>> cityData = fullData.get(city);
            if (cityData == null) {
                return ResponseEntity.notFound().build();
            }

            List<List<Number>> filterList = cityData.stream()
                .filter(point -> {
                    long timestamp = point.get(0).longValue();
                    return (from == null || timestamp >= from) &&
                           (to == null || timestamp <= to);
                })
                .toList();

            List<List<Number>> interpolateData;
            
            if (from != null && to != null) {
                Instant fromInstant = Instant.ofEpochMilli(from.longValue());
                Instant toInstant = Instant.ofEpochMilli(to.longValue());

                Duration duration = Duration.between(fromInstant, toInstant);
                long days = duration.toDays();
                long hours = duration.toHours();
                long leftHours = hours % 24;
                
                System.out.println("------------------------");
                System.out.println("days = " + days + ", hours = " + hours);
                System.out.println("------------------------");

                if (days > 30 * 6) {
                    // 內差法每6小時一筆
                    long step = 6 * 60 * 60 * 1000;
                    interpolateData = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (days > 14) {
                    // 內差法每兩小時一筆
                    long step = 2 * 60 * 60 * 1000;
                    interpolateData = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (days > 7) {
                    // 內差法每小時一筆
                    long step = 60 * 60 * 1000;
                    interpolateData = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (hours > 30) {
                    // 內差法每五分鐘一筆
                    long step = 5 * 60 * 1000;
                    interpolateData = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else if (hours > 7) {
                    // 內差法30秒一筆
                    long step = 30 * 1000;
                    interpolateData = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
                else {
                    // 內差法每秒鐘一筆
                    long step = 1000;
                    interpolateData = interpolateData(filterList, from.longValue(), to.longValue(), step);
                }
            } 
            else {
                interpolateData = filterList;
            }

            List<List<Number>> result = interpolateData.stream()
                .map(point -> {
                    long timestamp = point.get(0).longValue();
                    double low = point.get(1).doubleValue();
                    double high = point.get(2).doubleValue();
                    double average = (low + high) / 2;
                    double open = average;
                    double close = average;
                    // 使用 ArrayList 代替 List.of()
                    List<Number> newPoint = new ArrayList<>();
                    newPoint.add(timestamp);
                    newPoint.add(open);
                    newPoint.add(high);
                    newPoint.add(low);
                    newPoint.add(close);
                    return newPoint;
                })
                .toList();

            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // @GetMapping("/navigator")
    // public ResponseEntity<Map<String, List<List<Number>>>> getNavigatorData() {
    //     try {
    //         ClassPathResource resource = new ClassPathResource("temperature-t.json");
    //         ObjectMapper mapper = new ObjectMapper();
    //         Map<String, List<List<Number>>> fullData = mapper.readValue(resource.getInputStream(), 
    //             new TypeReference<Map<String, List<List<Number>>>>() {});
            
    //         Map<String, List<List<Number>>> navigatorData = new HashMap<>();
    //         navigatorData.put("helsinki", new ArrayList<>());
    //         navigatorData.put("turku", new ArrayList<>());
    
    //         // 計算平均溫度
    //         for (List<List<Number>> cityData : fullData.values()) {
    //             String cityName = fullData.entrySet()
    //                 .stream()
    //                 .filter(entry -> entry.getValue() == cityData)
    //                 .map(Map.Entry::getKey)
    //                 .findFirst()
    //                 .get();
    
    //             for (List<Number> point : cityData) {
    //                 long timestamp = point.get(0).longValue();
    //                 double avgTemp = (point.get(1).doubleValue() + point.get(2).doubleValue()) / 2;
    //                 navigatorData.get(cityName).add(List.of(timestamp, avgTemp));
    //             }
    //         }
    
    //         return ResponseEntity.ok(navigatorData);
    
    //     } catch (Exception e) {
    //         return ResponseEntity.internalServerError().build();
    //     }
    
    // }
private List<List<Number>> interpolateData(List<List<Number>> originalData, long start, long end, long step) {
        List<List<Number>> result = new ArrayList<>();
        
        // 確保數據至少有兩個點
        if (originalData.size() < 2) {
            return originalData;
        }

        // 對每個時間點進行內差
        for (long timestamp = start; timestamp <= end; timestamp += step) {
            // 找到最近的兩個數據點
            int index = findNearestIndex(originalData, timestamp);
            if (index == -1 || index >= originalData.size() - 1) {
                continue;
            }

            List<Number> point1 = originalData.get(index);
            List<Number> point2 = originalData.get(index + 1);

            // 計算內差值
            double t = (timestamp - point1.get(0).longValue()) / 
                      (double)(point2.get(0).longValue() - point1.get(0).longValue());
            
            double low = interpolate(
                point1.get(1).doubleValue(),
                point2.get(1).doubleValue(),
                t
            );
            
            double high = interpolate(
                point1.get(2).doubleValue(),
                point2.get(2).doubleValue(),
                t
            );

            result.add(Arrays.asList(timestamp, low, high));
        }

        return result;
    }

    private int findNearestIndex(List<List<Number>> data, long timestamp) {
        for (int i = 0; i < data.size() - 1; i++) {
            long t1 = data.get(i).get(0).longValue();
            long t2 = data.get(i + 1).get(0).longValue();
            if (timestamp >= t1 && timestamp <= t2) {
                return i;
            }
        }
        return -1;
    }

    private double interpolate(double y1, double y2, double t) {
        return y1 + (y2 - y1) * t;
    }

}

