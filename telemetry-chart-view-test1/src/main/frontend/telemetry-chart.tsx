import React from 'react';
import ReactDOM from 'react-dom';
import { createRoot } from 'react-dom/client';
import TelemetryChart from './component/TelemetryChart.js';

class TelemetryChartWrapper extends HTMLElement {
  connectedCallback() {
    const dataUrl = this.getAttribute('data-url') ?? '';
    const title = this.getAttribute('title') ?? '';
    const seriesNames = (this.getAttribute('series-names') || '').split(',');

    const root = createRoot(this); // 使用 createRoot 來創建 React 根
    root.render(
      <TelemetryChart dataUrl={dataUrl} title={title} seriesNames={seriesNames} />,
    );
  }

  disconnectedCallback() {
  }
}

customElements.define('telemetry-chart', TelemetryChartWrapper);
