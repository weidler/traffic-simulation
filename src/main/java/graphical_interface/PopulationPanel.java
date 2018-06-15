package graphical_interface;

import com.google.common.collect.Lists;
import datastructures.StreetMap;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import road.Road;
import type.ZoneType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PopulationPanel extends JPanel {

	StreetMap street_map;

	CategoryChart population_chart;
	PieChart zone_population_chart;

	JPanel population_chart_panel;
	JPanel zone_population_chart_panel;

	public PopulationPanel(StreetMap street_map) {
		this.street_map = street_map;

		// ROAD POPULATION CHART
		population_chart = new CategoryChartBuilder()
				.width(400)
				.height(400)
				.title("Population per Road")
				.xAxisTitle("Road")
				.yAxisTitle("Population")
				.build();

		// ZONE POPULATION CHART
		zone_population_chart = new PieChartBuilder()
				.width(400)
				.height(400)
				.title("Population per Road")
				.build();

		if (this.street_map.getRoads().size() > 0) {
			ArrayList<Integer> road_ids = new ArrayList<Integer>();
			ArrayList<Integer> populations = new ArrayList<Integer>();
			HashMap<ZoneType, Integer> population_per_zone = new HashMap<ZoneType, Integer>();
			for (ZoneType zone : ZoneType.values()) {
				population_per_zone.put(zone, 0);
			}

			int i = 0;
			for (Road r : street_map.getRoads()) {
				road_ids.add(i);
				populations.add(r.getAvailabePopulation());
				population_per_zone.put(r.getZoneType(), population_per_zone.get(r.getZoneType()) + 1);
				i++;
			}

			population_chart.addSeries(
					"Population",
					road_ids,
					populations
			);

			for (ZoneType zone : ZoneType.values()) {
				zone_population_chart.addSeries(zone.toString(), population_per_zone.get(zone));
			}
		} else {
			population_chart.addSeries(
					"Population",
					Arrays.asList(new Integer[] {0}),
					Arrays.asList(new Integer[] {0})
			);

			for (ZoneType zone : ZoneType.values()) {
				zone_population_chart.addSeries(zone.toString(), 0);
			}
		}

		population_chart.getStyler().setLegendVisible(false);
		zone_population_chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
		zone_population_chart.getStyler().setLegendLayout(Styler.LegendLayout.Vertical);

		population_chart_panel = new XChartPanel(population_chart);
		zone_population_chart_panel = new XChartPanel(zone_population_chart);

		this.add(population_chart_panel);
		this.add(zone_population_chart_panel);

	}

	public void updateCharts() {
		ArrayList<Integer> road_ids = new ArrayList<Integer>();
		ArrayList<Integer> populations = new ArrayList<Integer>();
		HashMap<ZoneType, Integer> population_per_zone = new HashMap<ZoneType, Integer>();
		for (ZoneType zone : ZoneType.values()) {
			population_per_zone.put(zone, 0);
		}

		int i = 0;
		for (Road r : street_map.getRoads()) {
			road_ids.add(i);
			populations.add(r.getAvailabePopulation());
			population_per_zone.put(r.getZoneType(), population_per_zone.get(r.getZoneType()) + r.getAvailabePopulation());
			i++;
		}

		population_chart.updateCategorySeries(
				"Population",
				road_ids,
				populations,
				null
		);

		for (ZoneType zone : ZoneType.values()) {
			System.out.println(zone.toString() + " " + population_per_zone.get(zone));
			zone_population_chart.updatePieSeries(zone.toString(), population_per_zone.get(zone));
		}

		this.repaint();
	}
}
