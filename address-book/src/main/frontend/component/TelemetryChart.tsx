import { useEffect, useRef } from 'react';
import * as am5xy from '@amcharts/amcharts5/xy';
import * as am5 from '@amcharts/amcharts5';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';
import React from 'react';

interface TelemetryChartProps {
    data: { timestamp: number; [key: string]: number }[];
    title: string;
}

const TelemetryChart: React.FC<TelemetryChartProps> = ({ data, title }) => {
    const chartContainerRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!chartContainerRef.current) return;

        const root = am5.Root.new(chartContainerRef.current);
        root.setThemes([am5themes_Animated.new(root)]);

        const chart = root.container.children.push(
            am5xy.XYChart.new(root, {
                panX: true,
                panY: true,
                wheelX: 'panX',
                wheelY: 'zoomX',
            })
        );

        const xAxis = chart.xAxes.push(
            am5xy.DateAxis.new(root, {
                baseInterval: { timeUnit: 'day', count: 1 },
                renderer: am5xy.AxisRendererX.new(root, {}),
            })
        );

        const yAxis = chart.yAxes.push(
            am5xy.ValueAxis.new(root, {
                renderer: am5xy.AxisRendererY.new(root, {}),
            })
        );

        // 取得所有 series（忽略 timestamp）
        const seriesNames = Object.keys(data[0] || {}).filter((key) => key !== 'timestamp');

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
            series.appear();
        });

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
