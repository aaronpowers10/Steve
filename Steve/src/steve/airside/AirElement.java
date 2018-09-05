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

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.core.Psychrometrics;
import willie.core.WillieObject;

public abstract class AirElement implements WillieObject{
	
	private AirNode inletNode;
	private AirNode outletNode;
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		inletNode = (AirNode)objectReferences.get(objectData.getAlpha("Inlet Node"));
		AirNodeLinker.linkToNodeOutlet(this, inletNode);
		outletNode = (AirNode)objectReferences.get(objectData.getAlpha("Outlet Node"));
		AirNodeLinker.linkToNodeInlet(this,outletNode);
	}
	
	public void setInletNode(AirNode inletNode){
		this.inletNode = inletNode;
	}
	
	public void setOutletNode(AirNode outletNode){
		this.outletNode = outletNode;
	}
	
	public double inletPressure(){
		return inletNode.pressure();
	}
	
	public double outletPressure(){
		return outletNode.pressure();
	}
	
	public double inletTemperature(){
		return inletNode.drybulb();
	}
	
	public double outletTemperature(){
		return outletNode.drybulb();
	}
	
	public double inletDewpoint(){
		return inletNode.dewpoint();
	}
	
	public double outletDewpoint(){
		return outletNode.dewpoint();
	}
	
	public double inletDensity(){
		return inletNode.density();
	}
	
	public double outletDensity(){
		return outletNode.density();
	}
	
	public double heatAdvection(){
		return 60 *density()*specificHeat()* volumetricFlow() * (outletTemperature() - inletTemperature());
	}
	
	public double humidityAdvection(){
		return 60 *density()* volumetricFlow() * (outletHumidityRatio() - inletHumidityRatio());
	}
	
	public double heatCapacity(){
		return density()*specificHeat()*volume();
	}
	
	public double pressureDrop(){
		return inletNode.pressure() - outletNode.pressure();
	}
	
	public double pressureGain(){
		return outletNode.pressure() - inletNode.pressure();
	}
	
	public double density(){
		return Psychrometrics.densityFHumidityRatio(outletTemperature(), outletHumidityRatio(), outletPressure());
	}
	
	public double specificHeat(){
		return (outletEnthalpy() - inletEnthalpy())/(outletTemperature()-inletTemperature());
	}
	
	public double heatCapacityRate(){
		return 60*density()*specificHeat()* volumetricFlow();
	}
	
	public double inletHumidityRatio(){
		return inletNode.humidityRatio();
	}
	
	public double outletHumidityRatio(){
		return outletNode.humidityRatio();
	}
	
	public double inletEnthalpy(){
		return Psychrometrics.enthalpyFHumidityRatio(inletTemperature(), inletHumidityRatio(), inletPressure());
	}
	
	public double outletEnthalpy(){
		return Psychrometrics.enthalpyFHumidityRatio(outletTemperature(), outletHumidityRatio(), outletPressure());
	}
	
	public abstract double humidityGain();
	
	public abstract double volumetricFlow();
	
	public abstract double volume();
	
	public abstract double heatGain();

}
