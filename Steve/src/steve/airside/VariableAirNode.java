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

import static steve.airside.ElementMath.sumDryAirMass;
import static steve.airside.ElementMath.sumFlow;
import static steve.airside.ElementMath.sumHeatAdvection;
import static steve.airside.ElementMath.sumHeatCapacity;
import static steve.airside.ElementMath.sumHeatGain;
import static steve.airside.ElementMath.sumHumidityAdvection;
import static steve.airside.ElementMath.sumHumidityGain;

import java.util.ArrayList;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.Psychrometrics;
import willie.core.ReportWriter;
import willie.core.RequiresPostStepProcessing;
import willie.core.RequiresTimeManager;
import willie.core.Simulator;
import willie.core.TimeManager;
import willie.core.WillieObject;
import willie.output.Report;

public class VariableAirNode implements AirNode, WillieObject, Simulator,RequiresPostStepProcessing,RequiresTimeManager,ReportWriter{
	
	private String name;
	private double pressure;
	//private double enthalpy;
	private double temperature;
	private double humidityRatio;
	private double nextTemperature;
	//private double nextEnthalpy;
	private double nextHumidityRatio;
	private double nextPressure;
	private double alpha;
	private TimeManager timeManager;
	private ArrayList<AirElement> inletElements;
	private ArrayList<AirElement> outletElements;
	
	public VariableAirNode(String name){
		this.name = name;
		inletElements = new ArrayList<AirElement>();
		outletElements = new ArrayList<AirElement>();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		pressure = objectData.getReal("Pressure");
		double drybulb = objectData.getReal("Drybulb");
		humidityRatio = objectData.getReal("Humidity Ratio");
		alpha = objectData.getReal("Alpha");
		temperature = drybulb;
		//enthalpy = Psychrometrics.enthalpyFHumidityRatio(drybulb, humidityRatio, pressure);
		
	}

	@Override
	public double pressure() {
		return pressure;
	}

	@Override
	public double drybulb() {
		return temperature;
		//return Psychrometrics.drybulbFEnthalpyHumidityRatio(enthalpy, humidityRatio);
	}

	@Override
	public double enthalpy() {
		return Psychrometrics.enthalpyFHumidityRatio(temperature, humidityRatio, pressure);
		//return enthalpy;
	}

	@Override
	public double humidityRatio() {
		return humidityRatio;
	}
	
	@Override
	public void simulateStep1(){
		/*
		 * Explicit Euler step
		 */
		nextTemperature = temperature - timeManager.dtHours()/sumHeatCapacity(inletElements)*(sumHeatAdvection(inletElements) - sumHeatGain(inletElements));
		//nextEnthalpy = enthalpy - timeManager.dtHours()/sumDryAirMass(inletElements)*(sumHeatAdvection(inletElements) - sumHeatGain(inletElements));
		nextHumidityRatio = humidityRatio - timeManager.dtHours()/sumDryAirMass(inletElements)*(sumHumidityAdvection(inletElements) - sumHumidityGain(inletElements));
		nextPressure = pressure + alpha*(sumFlow(inletElements)-sumFlow(outletElements));
	}

	@Override
	public void processPostStep() {
		//enthalpy = nextEnthalpy;
		temperature = nextTemperature;
		humidityRatio = nextHumidityRatio;
		pressure = nextPressure;
	}
	
	@Override
	public void linkToTimeManager(TimeManager timeManager) {
		this.timeManager = timeManager;
	}
	
	public void addInletElement(AirElement element){
		inletElements.add(element);
	}
	
	public void addOutletElement(AirElement element){
		outletElements.add(element);
	}
	
	@Override
	public double dewpoint() {
		return Psychrometrics.dewpointFHumidityRatio(drybulb(),humidityRatio,pressure);
	}

	@Override
	public double density() {
		return Psychrometrics.densityFHumidityRatio(drybulb(), humidityRatio, pressure);
	}
	
	@Override
	public void addHeader(Report report) {
		report.addTitle(name, 8);
		report.addDataHeader("Pressure", "[Ft.]");
		report.addDataHeader("Temperature", "[Deg-F]");
		report.addDataHeader("Humidity Ratio","[Lbs H2O/Lbs DA]");
		report.addDataHeader("Enthalpy", "[Btu/Lb]");
		report.addDataHeader("Inlet Flow","[CFM]");
		report.addDataHeader("Outlet Flow","[CFM]");
		report.addDataHeader("Overflow","[CFM]");
		report.addDataHeader("Excess Heat", "[Btu/Hr]");
	}

	@Override
	public void addData(Report report) {
		report.putReal(pressure);
		report.putReal(drybulb());	
		report.putReal(humidityRatio);
		report.putReal(enthalpy());	
		report.putReal(sumFlow(inletElements));
		report.putReal(sumFlow(outletElements));
		report.putReal(sumFlow(outletElements) - sumFlow(inletElements));
		report.putReal(sumHeatAdvection(inletElements)-sumHeatGain(inletElements));
	}

}
