import React from 'react';
import { createRoot } from 'react-dom/client';
import TelemetryChart from './component/TelemetryChart.js';

class TelemetryChartWrapper extends HTMLElement {
  connectedCallback() {
    console.log('TelemetryChartWrapper connectedCallback called');
    const dataUrl = this.getAttribute('data-url');
    const title = this.getAttribute('title');
    const seriesNames = (this.getAttribute('series-names') || '').split(',');

    // 用 createRoot，並存成屬性
    this._root = createRoot(this);
    this._root.render(
      <TelemetryChart dataUrl={dataUrl} title={title} seriesNames={seriesNames} />
    );
  }

  disconnectedCallback() {
    if (this._root) {
      this._root.unmount(); // 正確的卸載方式
      this._root = null;
    }
  }
}

customElements.define('telemetry-chart', TelemetryChartWrapper);
