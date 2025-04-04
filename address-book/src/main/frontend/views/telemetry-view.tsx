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

// 將原本的數據格式轉換為每個 timestamp 有多個 series 值
const transformData = (rawData: { timestamp: number; series: string; value: number }[]) => {
    const groupedData: Record<number, { timestamp: number; [key: string]: number }> = {};

    rawData.forEach(({ timestamp, series, value }) => {
        if (!groupedData[timestamp]) {
            groupedData[timestamp] = { timestamp };
        }
        groupedData[timestamp][series] = value;
    });

    return Object.values(groupedData);
};

const TelemetryView = () => {
    const [telemetryData1, setTelemetryData1] = useState<{ timestamp: number; [key: string]: number }[]>([]);
    const [telemetryData2, setTelemetryData2] = useState<{ timestamp: number; [key: string]: number }[]>([]);
    const [telemetryData3, setTelemetryData3] = useState<{ timestamp: number; [key: string]: number }[]>([]);

    useEffect(() => {
        const rawData1 = [
            { timestamp: 1704067200000, series: 'Voltage', value: 120 },
            { timestamp: 1704153600000, series: 'Voltage', value: 150 },
            { timestamp: 1704240000000, series: 'Voltage', value: 130 },
            { timestamp: 1704326400000, series: 'Voltage', value: 160 },
            { timestamp: 1704412800000, series: 'Voltage', value: 170 },
            { timestamp: 1704067200000, series: 'Current', value: 10 },
            { timestamp: 1704153600000, series: 'Current', value: 15 },
            { timestamp: 1704240000000, series: 'Current', value: 13 },
            { timestamp: 1704326400000, series: 'Current', value: 14 },
            { timestamp: 1704412800000, series: 'Current', value: 16 },
        ];

        const rawData2 = [
            { timestamp: 1704067200000, series: 'Speed', value: 80 },
            { timestamp: 1704153600000, series: 'Speed', value: 90 },
            { timestamp: 1704240000000, series: 'Speed', value: 85 },
            { timestamp: 1704326400000, series: 'Speed', value: 95 },
            { timestamp: 1704412800000, series: 'Speed', value: 100 },
            { timestamp: 1704067200000, series: 'Temperature', value: 30 },
            { timestamp: 1704153600000, series: 'Temperature', value: 32 },
            { timestamp: 1704240000000, series: 'Temperature', value: 31 },
            { timestamp: 1704326400000, series: 'Temperature', value: 33 },
            { timestamp: 1704412800000, series: 'Temperature', value: 35 },
        ];

        const rawData3 = [
            { timestamp: 1704067200000, series: 'Power', value: 200 },
            { timestamp: 1704153600000, series: 'Power', value: 220 },
            { timestamp: 1704240000000, series: 'Power', value: 210 },
            { timestamp: 1704326400000, series: 'Power', value: 240 },
            { timestamp: 1704412800000, series: 'Power', value: 250 },
            { timestamp: 1704067200000, series: 'Efficiency', value: 75 },
            { timestamp: 1704153600000, series: 'Efficiency', value: 78 },
            { timestamp: 1704240000000, series: 'Efficiency', value: 77 },
            { timestamp: 1704326400000, series: 'Efficiency', value: 79 },
            { timestamp: 1704412800000, series: 'Efficiency', value: 80 },
        ];

        setTelemetryData1(transformData(rawData1));
        setTelemetryData2(transformData(rawData2));
        setTelemetryData3(transformData(rawData3));
    }, []);

    return (
        <div>
            <h2>Telemetry Historical Records</h2>
            <TelemetryChart data={telemetryData1} title="Telemetry 1" />
            <TelemetryChart data={telemetryData2} title="Telemetry 2" />
            <TelemetryChart data={telemetryData3} title="Telemetry 3" />
        </div>
    );
};

export default TelemetryView;
