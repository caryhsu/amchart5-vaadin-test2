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
                panX: true,
                panY: true,
                wheelX: 'panX',
                wheelY: 'zoomX',
            })
        );

        // 创建 X 轴 (时间轴)
        const xAxis = chart.xAxes.push(
            am5xy.DateAxis.new(root, {
                baseInterval: { timeUnit: 'day', count: 1 },
                renderer: am5xy.AxisRendererX.new(root, {}),
            })
        );

        // 创建 Y 轴 (数值轴)
        const yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {}),
            })
        );

        // 使用外部传入的 seriesNames 创建多个 series
        seriesNames.forEach((seriesName) => {
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

            series.data.setAll(data);
        });

        // 清理图表，防止重复渲染
        return () => {
            root.dispose();
        };
    }, [data, title, seriesNames]);

    return (
        <div>
            <h3>{title}</h3>
            <div ref={chartContainerRef} style={{ width: '100%', height: '400px' }}></div>
        </div>
    );
};

export default TelemetryChart;
