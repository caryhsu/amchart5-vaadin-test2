<!DOCTYPE html>
<html lang="zh-Hant">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>amCharts 5 動態資料載入範例 (修正事件)</title>
    <script src="https://cdn.amcharts.com/lib/5/index.js"></script>
    <script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
    <style>
        #chartdiv {
            width: 100%;
            height: 500px;
        }
        #loading {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: rgba(255, 255, 255, 0.8);
            padding: 10px 20px;
            border-radius: 5px;
            display: none; /* 初始隱藏 */
            z-index: 100;
        }
    </style>
</head>
<body>
<div id="chartdiv"></div>
<div id="loading">正在載入資料...</div>

<script>
    // 防抖動 (Debounce) 函數
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // 初始化 amCharts 5
    am5.ready(function() {
        var root = am5.Root.new("chartdiv");

        function showLoading() {
            document.getElementById("loading").style.display = "block";
        }

        function hideLoading() {
            document.getElementById("loading").style.display = "none";
        }

        var chart = root.container.children.push(am5xy.XYChart.new(root, {
            panX: true,
            panY: false,
            wheelX: "panX",
            wheelY: "zoomX",
            pinchZoomX: true
        }));

        var xAxis = chart.xAxes.push(am5xy.DateAxis.new(root, {
            maxDeviation: 0.1,
            baseInterval: { timeUnit: "second", count: 1 },
            renderer: am5xy.AxisRendererX.new(root, {
                minGridDistance: 50
            }),
            tooltip: am5.Tooltip.new(root, {})
        }));

        var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
            renderer: am5xy.AxisRendererY.new(root, {})
        }));

        var series = chart.series.push(am5xy.LineSeries.new(root, {
            name: "Telemetry Data",
            xAxis: xAxis,
            yAxis: yAxis,
            valueYField: "value",
            valueXField: "time",
            tooltip: am5.Tooltip.new(root, {
                labelText: "{valueY}"
            }),
            connect: false
        }));

        var scrollbarX = chart.set("scrollbarX", am5.Scrollbar.new(root, {
            orientation: "horizontal"
        }));

        chart.set("cursor", am5xy.XYCursor.new(root, {
             behavior: "zoomX",
             xAxis: xAxis
        }));

        // --- 核心修改：改用 chart 的 datarangechanged 事件 ---
        const debouncedFetch = debounce(function() {
            // 從 xAxis 獲取當前實際顯示的最小和最大時間戳
            const minTime = xAxis.get("min");
            const maxTime = xAxis.get("max");

            // 確保 minTime 和 maxTime 是有效的數字
            if (typeof minTime === 'number' && typeof maxTime === 'number' && minTime < maxTime) {
                 console.log("Data range changed (debounced), fetching for:", new Date(minTime), "to", new Date(maxTime));
                 fetchTelemetryData(minTime, maxTime);
            } else {
                 // 這個警告可能在圖表初始化或數據為空時出現，是正常的
                 console.warn("Condition for fetch not met or invalid axis range:", minTime, maxTime);
            }
        }, 500); // 延遲 500 毫秒觸發

        // 監聽圖表數據範圍變化事件
        chart.events.on("datarangechanged", debouncedFetch);

        // **移除之前的事件監聽器 (如果還有的話)**
        // xAxis.get("renderer").events.off("extremeschanged", debouncedFetch); // 確保移除舊的

        // 模擬一個從伺服器端拉取資料的函數 (與之前相同)
        async function fetchTelemetryData(minTime, maxTime) {
            showLoading();
            console.log("Fetching telemetry data from", new Date(minTime), "to", new Date(maxTime));
            try {
                await new Promise(resolve => setTimeout(resolve, 800));
                var newData = [];
                const timeStep = 1000;
                let currentTime = Math.floor(minTime / timeStep) * timeStep;
                let count = 0;
                while (currentTime <= maxTime && count < 500) {
                    newData.push({
                        time: currentTime,
                        value: Math.sin(currentTime * 0.0001) * 15 + Math.random() * 5 + 40
                    });
                    currentTime += timeStep;
                    count++;
                }
                console.log(`Workspaceed ${newData.length} data points.`);
                series.data.setAll(newData);
            } catch (error) {
                console.error("Error fetching telemetry data:", error);
            } finally {
                 hideLoading();
            }
        }

        // 載入初始資料
        const initialEndTime = new Date().getTime();
        const initialStartTime = initialEndTime - 60 * 60 * 1000; // 載入過去一小時
        // 初始載入時，datarangechanged 可能會觸發，確保 fetchTelemetryData 被調用
        // 但為了確保第一次一定載入，我們也手動調用一次
        fetchTelemetryData(initialStartTime, initialEndTime);
        // 注意：初始調用後，圖表設置數據會觸發 datarangechanged，
        // debouncedFetch 會被調用，但由於時間範圍可能相同，
        // 實際的 fetch 可能不會重複執行（取決於你的後端邏輯），
        // 或者因為防抖，只執行一次。

    }); // end am5.ready()
</script>
</body>
</html>