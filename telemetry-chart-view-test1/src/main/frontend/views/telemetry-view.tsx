import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import React, { useState } from 'react';
import TelemetryChart from '../component/TelemetryChart.js';

export const config: ViewConfig = {
  menu: { 
    order: 5, 
    icon: 'line-awesome/svg/chart-line-solid.svg' 
},
  title: 'Telemetry Chart',
};

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
