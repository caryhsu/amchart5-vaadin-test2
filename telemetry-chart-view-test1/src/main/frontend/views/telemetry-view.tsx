import React, { useEffect, useState } from 'react';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import TelemetryChart from '../component/TelemetryChart.js';

export const config: ViewConfig = {
    menu: {
        order: 3,
        icon: 'line-awesome/svg/chart-bar-solid.svg',
    },
    title: 'Telemetry Charts',
};

const TelemetryView = () => {
    const [telemetryData, setTelemetryData] = useState<
        { timestamp: number; series1: number; series2: number; series3: number }[]
    >([]);

    const [seriesNames, setSeriesNames] = useState<string[]>(['series1', 'series2', 'series3']); // 可以动态修改

    useEffect(() => {
        // 模拟数据，时间单位：毫秒（timestamp）
        setTelemetryData([
            { timestamp: 1704067200000, series1: 120, series2: 80, series3: 200 },
            { timestamp: 1704153600000, series1: 150, series2: 90, series3: 220 },
            { timestamp: 1704240000000, series1: 130, series2: 85, series3: 210 },
            { timestamp: 1704326400000, series1: 160, series2: 95, series3: 240 },
            { timestamp: 1704412800000, series1: 170, series2: 100, series3: 250 },
        ]);
    }, []);

    return (
        <div>
            <h2>Telemetry Historical Records</h2>
            {/* 将 seriesNames 传递给 TelemetryChart */}
            <TelemetryChart data={telemetryData} title="Telemetry Data" seriesNames={seriesNames} />        
        </div>
    );

};

export default TelemetryView;
