package com.example.application.views.telemetry;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.example.application.views.component.TelemetryChartComponent;

@PageTitle("Telemetry Flow Chart")
@Route("telemetry-flow-view")
@Menu(order = 2, icon = LineAwesomeIconUrl.CHART_LINE_SOLID)
public class TelemetryFlowView extends VerticalLayout {

    public TelemetryFlowView() {

        add(new H2("Telemetry Flow Chart (Java)"));
        
        TelemetryChartComponent chart = new TelemetryChartComponent();
        chart.setWidthFull();
        chart.setHeight("400px");
        chart.setDataUrl("/api/telemetry-data/telemetry1");
        chart.setTitle("Telemetry Data");
        chart.setSeriesNames("series1", "series2", "series3");

        add(chart);
    }
}
