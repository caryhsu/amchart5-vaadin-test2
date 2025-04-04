import { useEffect, useRef, useState } from 'react';
import * as am5xy from '@amcharts/amcharts5/xy';
import * as am5 from '@amcharts/amcharts5';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';
import React from 'react';

interface TelemetryChartProps {
    dataUrl: string;  // 用來指定資料來源的 URL
    title: string;
    seriesNames: string[];  // 來自外部的 seriesNames prop
}

const TelemetryChart: React.FC<TelemetryChartProps> = ({ dataUrl, title, seriesNames }) => {
    const chartContainerRef = useRef<HTMLDivElement | null>(null);
    const [data, setData] = useState<{ timestamp: number; [key: string]: number }[]>([]);

    // 在 useEffect 中處理 fetch 資料
    useEffect(() => {
        if (!dataUrl) return;

        // 假設資料是從後端 API URL 獲取的
        fetch(dataUrl)
            .then((response) => response.json())
            .then((data) => {
                setData(data);
            })
            .catch((error) => {
                console.error('Error fetching telemetry data:', error);
            });
    }, [dataUrl]);

    useEffect(() => {
        if (!chartContainerRef.current || data.length === 0) return;

        // 创建 amCharts 根对象
        const root = am5.Root.new(chartContainerRef.current);
        
        root.setThemes([am5themes_Animated.new(root)]);

        // 创建折线图 (Line Chart)
        const chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                panX: false,     // 允許使用者透過滑鼠或觸控拖曳來水平移動（平移）圖表的繪圖區域（沿著 X 軸）
                panY: false,     // 允許使用者透過滑鼠或觸控拖曳來垂直移動（平移）圖表的繪圖區域（沿著 Y 軸）
                wheelX: 'panX', // 滾輪滾動時，圖表會沿著 X 軸平移
                wheelY: 'zoomX',// 滾輪滾動時，圖表會沿著 Y 軸縮放
            })
        );

        // Add cursor
        const cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
          behavior: "zoomX"
        }));
        cursor.lineY.set("visible", false);        

        // 创建 X 轴 (时间轴)
        const xAxis = chart.xAxes.push(
            am5xy.DateAxis.new(root, {
                baseInterval: { 
                  timeUnit: 'day', 
                  count: 1 
                },
                renderer: am5xy.AxisRendererX.new(root, {}),
            })
        );

        // 创建 Y 轴 (数值轴)
        const yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {}),
            })
        );

        // 创建 Legend (图例)
        const legend = chart.children.push(
            am5.Legend.new(root, {
              x: am5.percent(0),              // 將圖例的 X 座標設在容器寬度的 0% 處 (即最左邊)
              centerX: am5.percent(0),        // 將圖例本身的左邊緣 (0%) 對齊到 X 座標
              y: am5.percent(100),            // 將圖例的 Y 座標設在容器高度的 100% 處 (底部邊緣)
              centerY: am5.percent(100),      // 將圖例本身的底部邊緣 (100%) 對齊到 Y 座標
              layout: root.horizontalLayout,  // 保持水平排列
              dx: 50,                         // 向右偏移 
              dy: 20,                         // 向上偏移 
            })
        );

        // 使用外部传入的 seriesNames 创建多个 series
        seriesNames.forEach((seriesName, index) => {
            const series = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: seriesName,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: seriesName,
                    valueXField: 'timestamp',
                    tooltip: am5.Tooltip.new(root, {
                        labelText: `{name}: {valueY}`,
                    }),
                })
            );

            // Actual bullet
            series.bullets.push(function () {
              var bulletCircle = am5.Circle.new(root, {
                radius: 2,
                fill: series.get("fill")
              });
              return am5.Bullet.new(root, {
                sprite: bulletCircle
              })
            })            
            series.data.setAll(data);

            // 向图例添加该系列
            legend.data.push(series);

            chart.set("scrollbarX", am5.Scrollbar.new(root, {
              orientation: "horizontal"
            }));

            // 可选：给每个系列设置不同的颜色
            // const colors = [
            //     am5.color(0xff5733),
            //     am5.color(0x33ff57),
            //     am5.color(0x3357ff),
            // ];

            // series.strokes.template.set('fill', colors[index]);
            // series.bullets.push(am5xy.CircleBullet.new(root, { radius: 3 }));
        });

        // 清理图表，防止重复渲染
        return () => {
            root.dispose();
        };
    }, [data, title, seriesNames]);

    return (
        <div>
            <h3>{title}</h3>
            <div ref={chartContainerRef} style={{ width: '100%', height: '450px', overflow: 'visible'  }}></div>
        </div>
    );
};

export default TelemetryChart;
