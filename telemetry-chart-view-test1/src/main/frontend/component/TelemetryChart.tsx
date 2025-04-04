import { useEffect, useRef } from 'react';
import * as am5xy from '@amcharts/amcharts5/xy';
import * as am5 from '@amcharts/amcharts5';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';
import React from 'react';

interface TelemetryChartProps {
    data: { timestamp: number; series1: number; series2: number; series3: number }[];
    title: string;
}

const TelemetryChart: React.FC<TelemetryChartProps> = ({ data, title }) => {
    const chartContainerRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!chartContainerRef.current) return;

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

        // 定义多个 series（3 条线）
        const seriesNames = ['series1', 'series2', 'series3'];
        const colors = [am5.color(0xff5733), am5.color(0x33ff57), am5.color(0x3357ff)];

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

            series.set("stroke", colors[index]); // 设置颜色
            series.data.setAll(data);
        });

        // 清理图表，防止重复渲染
        return () => {
            root.dispose();
        };
    }, [data, title]);

    return (
        <div>
            <h3>{title}</h3>
            <div ref={chartContainerRef} style={{ width: '100%', height: '400px' }}></div>
        </div>
    );
};

export default TelemetryChart;
