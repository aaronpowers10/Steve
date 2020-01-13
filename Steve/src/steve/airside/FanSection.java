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
import willie.core.ElectricConsumer;
import willie.core.ReportWriter;
import willie.core.WillieObject;
import willie.output.Report;

public class FanSection extends AirElement implements ReportWriter, ElectricConsumer{
	
	private String name;
	private double nominalFlow;
	private double totalStaticPressure;
	private double fanEfficiency;
	private double motorEfficiency;
	private Controller controller;
	private final double c1 = 1.35348296;
	private final double c2 = 0.0159317;
	private final double c3 = -0.36941442;
	private final double c4 = 0.36977392;
	private final double c5 = 0.84037501;
	private final double c6 = -0.21014881;
	
	public FanSection(String name){
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		super.read(objectData, objectReferences);
		nominalFlow = objectData.getReal("Nominal Flow");
		totalStaticPressure = Conversions.inchesWaterToPsi(objectData.getReal("Total Static Pressure"));
		fanEfficiency = objectData.getReal("Fan Efficiency");
		motorEfficiency = objectData.getReal("Motor Efficiency");
		controller = (Controller)objectReferences.get(objectData.getAlpha("Controller"));
	}
	
	private double peakFanPower(){
		return nominalFlow * totalStaticPressure/6345/fanEfficiency/motorEfficiency*0.7456;
	}
	
	@Override
	public double electricPower(){
		//Temporary
		return peakFanPower();
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name,7);
		report.addDataHeader("Airflow", "[CFM]");
		report.addDataHeader("DP", "[Psi]");
		report.addDataHeader("Inlet P", "[Psi]");
		report.addDataHeader("Outlet P", "[Psi]");
		report.addDataHeader("Fan Power", "[kW]");
		report.addDataHeader("Heat Gain","[Btu/Hr]");
		report.addDataHeader("Controller Output","");
	}

	@Override
	public void addData(Report report) {
		report.putReal(volumetricFlow());
		report.putReal(pressureGain());
		report.putReal(inletPressure());
		report.putReal(outletPressure());
		report.putReal(electricPower());
		report.putReal(heatGain());
		report.putReal(controller.output());
	}

	@Override
	public double humidityGain() {
		return 0;
	}

	@Override
	public double volumetricFlow() {
		if (pressureGain() >= shutoffPressure()) {
			return 0.0;
		} else {
			double pressureRatio = pressureGain() / (totalStaticPressure * controller.output() * controller.output());
			return fanFlowFPressure(pressureRatio)* nominalFlow * controller.output();
		}
	}
	
	private double fanFlowFPressure(double pressureRatio){
		return (-c2 - pow((c2 * c2 - 4 * c3 * (c1 - pressureRatio)), 0.5)) / (2 * c3);
		
	}
	
	private double shutoffPressure(){
		return c1 * totalStaticPressure*controller.output()*controller.output();
	}

	@Override
	public double volume() {
		return 20*20*10;
	}

	@Override
	public double heatGain() {
		return Conversions.kWToBtu(electricPower());
	}

}
