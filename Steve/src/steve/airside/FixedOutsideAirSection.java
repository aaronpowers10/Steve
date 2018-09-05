/*
 *
 *  Copyright (C) 2017 Aaron Powers
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package steve.airside;

import java.util.ArrayList;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.ReportWriter;
import willie.core.RequiresWeather;
import willie.core.Weather;
import willie.core.WillieObject;
import willie.output.Report;

public class FixedOutsideAirSection implements WillieObject, SingleOutletAirElement, RequiresWeather, ReportWriter {

	private String name;
	private ArrayList<SingleOutletAirElement> upstreamElements;
	private Weather weather;
	private double percentOutsideAir;

	public FixedOutsideAirSection(String name) {
		this.name = name;
		upstreamElements = new ArrayList<SingleOutletAirElement>();
	}

	@Override
	public double airflow() {
		double airflow = 0;
		for (SingleOutletAirElement element : upstreamElements) {
			airflow += element.airflow();
		}
		return airflow;
	}

	private double outsideAirflow() {
		return airflow() * percentOutsideAir;
	}

	private double returnAirflow() {
		return airflow() * (1 - percentOutsideAir);
	}

	@Override
	public double outletTemperature() {
		double outletTemperature = 0;
		double airflow = airflow();
		for (SingleOutletAirElement element : upstreamElements) {
			outletTemperature += element.airflow() * (1 - percentOutsideAir) / airflow * element.outletTemperature();
		}
		outletTemperature += percentOutsideAir * weather.drybulb();
		return outletTemperature;
	}

	@Override
	public double outletHumidityRatio() {
		double outletHumidityRatio = 0;
		double airflow = airflow();
		for (SingleOutletAirElement element : upstreamElements) {
			outletHumidityRatio += element.airflow() * (1 - percentOutsideAir) / airflow
					* element.outletHumidityRatio();
		}
		outletHumidityRatio += percentOutsideAir * weather.humidityRatio();
		return outletHumidityRatio;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		percentOutsideAir = objectData.getReal("Percent Outside Air");
		upstreamElements = new ArrayList<SingleOutletAirElement>();
		for (int i = 0; i < objectData.size("Upstream Elements"); i++) {
			upstreamElements
					.add((SingleOutletAirElement) objectReferences.get(objectData.getAlpha("Upstream Elements", i)));
		}

	}

	@Override
	public void linkToWeather(Weather weather) {
		this.weather = weather;
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,4);
		report.addDataHeader("Outside Airflow", "[CFM]");
		report.addDataHeader("Return Airflow", "[CFM]");
		report.addDataHeader("Outlet Temperature", "[Deg-F]");
		report.addDataHeader("Outlet Humidity Ratio", "[Lb-H2O/Lb-DA]");
	}

	@Override
	public void addData(Report report) {
		report.putReal(outsideAirflow());
		report.putReal(returnAirflow());	
		report.putReal(outletTemperature());
		report.putReal(outletHumidityRatio());
	}
}
