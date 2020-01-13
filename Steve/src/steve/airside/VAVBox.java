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
import willie.controls.Controller;
import willie.core.Conversions;
import willie.core.ReportWriter;
import willie.core.WillieObject;
import willie.output.Report;

public class VAVBox extends AirElement implements ReportWriter {

	private String name;
	private double nominalFlow;
	private double peakHeatingDT;
	private Controller coolingController;
	private Controller heatingController;
	private double pressureExponent;
	private double nominalConstant;
	private double nominalPressureDrop;

	public VAVBox(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		super.read(objectData, objectReferences);
		peakHeatingDT = objectData.getReal("Peak Heating DT");
		coolingController = (Controller) objectReferences.get(objectData.getAlpha("Cooling Controller"));
		heatingController = (Controller) objectReferences.get(objectData.getAlpha("Heating Controller"));
		nominalPressureDrop = Conversions.inchesWaterToPsi(objectData.getReal("Nominal Pressure Drop"));
		pressureExponent = objectData.getReal("Pressure Exponent");
		nominalFlow = objectData.getReal("Nominal Flow");
		setConstant();
		
	}

//	@Override
//	public double airflow() {
//		return Math.max(minFlow, coolingController.output() * maxFlow);
//	}
	
	private void setConstant(){
		nominalConstant = nominalFlow / pow(nominalPressureDrop,1/pressureExponent);
	}

	private double peakHeatingOutput() {
		//return 0;
		return 1.08 *0.33 *nominalFlow* peakHeatingDT; //hack
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,6);
		report.addDataHeader("Airflow", "[CFM]");
		report.addDataHeader("Pressure Drop","[PSI]");
		report.addDataHeader("Supply Temperature", "[Deg-F]");
		report.addDataHeader("Heating Output", "[Btu/Hr]");
		report.addDataHeader("Cooling Controller Output", "");
		report.addDataHeader("Heating Controller Output", "");
	}

	@Override
	public void addData(Report report) {
		report.putReal(volumetricFlow());
		report.putReal(pressureDrop());
		report.putReal(outletTemperature());	
		report.putReal(heatGain());
		report.putReal(coolingController.output());
		report.putReal(heatingController.output());
	}

	@Override
	public double humidityGain() {
		return 0;
	}

	@Override
	public double volumetricFlow() {
		if(pressureDrop() < 0){
			return -coolingController.output()*nominalConstant*pow((-pressureDrop()),(1/pressureExponent));
		} else {
			return coolingController.output()*nominalConstant*pow((pressureDrop()),(1/pressureExponent));
		}
	}

	@Override
	public double volume() {
		return 5*5*5;
	}

	@Override
	public double heatGain() {
		return heatingController.output() * peakHeatingOutput();
	}
}
