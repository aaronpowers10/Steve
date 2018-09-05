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
import willie.controls.Setpoint;
import willie.core.Conversions;
import willie.core.Psychrometrics;
import willie.core.ReportWriter;
import willie.core.WillieObject;
import willie.output.Report;

public class CoolingCoilSection extends AirElement implements ReportWriter {

	private String name;
	private Setpoint chwSetpoint;
	private double nominalEffectiveness;
	private Controller valveController;
	private double nominalPressureDrop;
	private double pressureExponent;
	private double nominalFlow;

	public CoolingCoilSection(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		super.read(objectData, objectReferences);
		nominalEffectiveness = objectData.getReal("Nominal Effectiveness");
		chwSetpoint = (Setpoint)objectReferences.get(objectData.getAlpha("CHW Supply Setpoint"));
		valveController = (Controller)objectReferences.get(objectData.getAlpha("Valve Controller"));
		nominalPressureDrop = Conversions.inchesWaterToPsi(objectData.getReal("Nominal Pressure Drop"));
		pressureExponent = objectData.getReal("Pressure Exponent");
		nominalFlow = objectData.getReal("Nominal Flow");
	}

	@Override
	public void addHeader(Report report) {
		report.addTitle(name,2);
		report.addDataHeader("Sensible Cooling Output", "[Btu/Hr]");
		report.addDataHeader("Latent Cooling Output", "[Btu/Hr]");
	}

	@Override
	public void addData(Report report) {
		report.putReal(sensibleCoolingOutput());
		report.putReal(latentCoolingOutput());		
	}
	
	public double sensibleCoolingOutput(){
		return -heatGain() - latentCoolingOutput();
	}
	
	public double latentCoolingOutput(){
		// not correct, use correct equation on piece of paper
		return 4840 * volumetricFlow() * humidityGain();
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
		return 20*10*10;
	}
	
	private double coilSurfaceEnthalpy(){
		return Psychrometrics.enthalpyFDewpoint(chwSetpoint.getSetpoint(), chwSetpoint.getSetpoint(), outletPressure());
	}
	
	private double coilSurfaceHumidityRatio(){
		return Psychrometrics.saturationHumidityRatio(chwSetpoint.getSetpoint(), outletPressure());
	}
	
	private double outletDryCoilDrybulb(){
		return Psychrometrics.drybulbFEnthalpyHumidityRatio(inletEnthalpy() + heatGain(), inletHumidityRatio());
	}
	
	private double outletHumidityRatioEstimate(){
		return Psychrometrics.saturationHumidityRatioFEnthalpy(inletEnthalpy() + heatGain(), inletPressure());
	}

	@Override
	public double heatGain() {
		return valveController.output()*nominalEffectiveness*(coilSurfaceEnthalpy() - inletEnthalpy());
	}
	
	@Override
	public double humidityGain() {
		if(outletDryCoilDrybulb() < inletDewpoint()){
			// Wet coil
			return 60*inletDensity() *( outletHumidityRatioEstimate() - inletHumidityRatio());
		} else {
			// Dry coil
			return 0.0;
		}
	}
}
