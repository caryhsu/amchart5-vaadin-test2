import React from 'react';
import ReactDOM from 'react-dom';
import TelemetryChart from './component/TelemetryChart.js';

class TelemetryChartWrapper extends HTMLElement {
  connectedCallback() {
    const dataUrl = this.getAttribute('data-url') ?? '';
    const title = this.getAttribute('title') ?? '';
    const seriesNames = (this.getAttribute('series-names') || '').split(',');

    ReactDOM.render(
      <TelemetryChart dataUrl={dataUrl} title={title} seriesNames={seriesNames} />,
      this
    );
  }

  disconnectedCallback() {
    ReactDOM.unmountComponentAtNode(this);
  }
}

customElements.define('telemetry-chart', TelemetryChartWrapper);
