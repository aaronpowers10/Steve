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
import willie.output.Report;

public class SimpleFlowPath extends AirElement implements ReportWriter{
	
	private String name;
	private double nominalPressureDrop;
	private double pressureExponent;
	private double nominalFlow;
	
	public SimpleFlowPath(String name){
		this.name = name;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		super.read(objectData, objectReferences);
		nominalFlow = objectData.getReal("Nominal Flow");
		nominalPressureDrop = Conversions.inchesWaterToPsi(objectData.getReal("Nominal Pressure Drop"));
		pressureExponent = objectData.getReal("Pressure Exponent");	
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public double humidityGain() {
		return 0;
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
		return 10;
	}

	@Override
	public double heatGain() {
		return 0;
	}

	public double averageFluidTemperature() {
		return (inletTemperature() + outletTemperature()) * 0.5;
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,2);
		report.addDataHeader("Flow", "CFM");
		report.addDataHeader("Pressure Drop", "PSI");
	}

	@Override
	public void addData(Report report) {
		report.putReal(volumetricFlow());
		report.putReal(pressureDrop());	
	}
}
