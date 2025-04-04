import React, { useState } from 'react';
import TelemetryChart from '../component/TelemetryChart.js';

const TelemetryView = () => {
    const [dataUrl, setDataUrl] = useState<string>('/api/telemetry-data/telemetry1');
    const [seriesNames, setSeriesNames] = useState<string[]>(['series1', 'series2', 'series3']);

    return (
        <div>
            <h2>Telemetry Historical Records</h2>
            <TelemetryChart 
                dataUrl={dataUrl} 
                title="Telemetry Data" 
                seriesNames={seriesNames} 
            />
        </div>
    );
};

export default TelemetryView;
