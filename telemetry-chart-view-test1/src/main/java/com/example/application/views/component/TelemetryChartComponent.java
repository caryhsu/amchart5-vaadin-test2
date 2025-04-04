package com.example.application.views.component;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.Component;

@Tag("telemetry-chart")
@JsModule("./telemetry-chart.jsx")
public class TelemetryChartComponent extends Div {

    public void setDataUrl(String url) {
        getElement().setAttribute("data-url", url);
    }

    public void setTitle(String title) {
        getElement().setAttribute("title", title);
    }

    public void setSeriesNames(String... seriesNames) {
        String joined = String.join(",", seriesNames);
        getElement().setAttribute("series-names", joined);
    }

}
