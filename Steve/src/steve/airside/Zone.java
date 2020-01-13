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

import static java.lang.Math.pow;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.Conversions;
import willie.core.ReportWriter;
import willie.core.WillieObject;
import willie.loads.Load;
import willie.output.Report;

public class Zone extends AirElement implements ReportWriter {

	private String name;
	private double area;
	private double height;
	private double weightMultiplier;
	private Load load;
	private double nominalPressureDrop;
	private double pressureExponent;
	private double nominalFlow;

	public Zone(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		super.read(objectData, objectReferences);
		area = objectData.getReal("Area");
		height = objectData.getReal("Height");
		weightMultiplier = objectData.getReal("Weight Multiplier");
		load = (Load) objectReferences.get(objectData.getAlpha("Load"));
		nominalPressureDrop = Conversions.inchesWaterToPsi(objectData.getReal("Nominal Pressure Drop"));
		pressureExponent = objectData.getReal("Pressure Exponent");
		nominalFlow = objectData.getReal("Nominal Flow");
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,5);
		report.addDataHeader("Temperature", "[Deg-F]");
		report.addDataHeader("Humidity Ratio", "[Lb-H2O/Lb-DA]");
		report.addDataHeader("Air Flow","[CFM]");
		report.addDataHeader("Load","[Btu/Hr]");
		report.addDataHeader("Heat Extraction","[Btu/Hr]");
	}

	@Override
	public void addData(Report report) {
		report.putReal(outletTemperature());
		report.putReal(outletHumidityRatio());	
		report.putReal(volumetricFlow());
		report.putReal(heatGain());
		report.putReal(heatAdvection());
	}

	@Override
	public double humidityGain() {
		return load.latentLoad()/1076;
	}

	@Override
	public double volumetricFlow() {
		if (pressureDrop() < 0) {
			return -nominalFlow * pow((-pressureDrop() / nominalPressureDrop), (1 / pressureExponent));
		} else {
			return nominalFlow * pow((pressureDrop() / nominalPressureDrop), (1 / pressureExponent));
		}
	}

	@Override
	public double volume() {
		return area*height*weightMultiplier;
	}

	@Override
	public double heatGain() {
		return load.latentLoad() + load.sensibleLoad();
	}

}
