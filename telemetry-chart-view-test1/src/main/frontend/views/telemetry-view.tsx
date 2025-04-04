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
        { timestamp: number; [key: string]: number }[] // 動態的 key: value 結構
    >([]);

    const [seriesNames, setSeriesNames] = useState<string[]>(['series1', 'series2', 'series3']); // 可以动态修改

    useEffect(() => {
        // 假設資料是從後端 API URL 獲取的
        fetch('/api/telemetry-data/telemetry1')
            .then((response) => response.json())
            .then((data) => {
                setTelemetryData(data);
                // setSeriesNames(Object.keys(data[0]).filter((key) => key !== 'timestamp')); // 排除 timestamp
            })
            .catch((error) => {
                console.error('Error fetching telemetry data:', error);
            });
    }, []);

    return (
        <div>
            <h2>Telemetry Historical Records</h2>
            {/* 将 seriesNames 传递给 TelemetryChart */}
            <TelemetryChart 
                data={telemetryData} 
                title="Telemetry Data" 
                seriesNames={seriesNames} 
            />        
        </div>
    );

};

export default TelemetryView;
