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

public class ElementMath {
	
	public static <T extends AirElement> double sumFlow(ArrayList<T> elements){
		double flow = 0.0;
		
		for(int i=0;i<elements.size();i++){
			flow += elements.get(i).volumetricFlow();
		}		
		return flow;
	}
	
	public static <T extends AirElement> double sumHeatGain(ArrayList<T> elements){
		double heatGain = 0.0;
		
		for(int i=0;i<elements.size();i++){
			heatGain += elements.get(i).heatGain();
		}		
		return heatGain;
	}
	
	public static <T extends AirElement> double sumHeatAdvection(ArrayList<T> elements){
		double heatAdvection = 0.0;
		
		for(int i=0;i<elements.size();i++){
			heatAdvection += elements.get(i).heatAdvection();
		}		
		return heatAdvection;
	}
	
	public static <T extends AirElement> double sumHeatCapacity(ArrayList<T> elements){
		double heatCapacity = 0.0;
		
		for(int i=0;i<elements.size();i++){
			heatCapacity += elements.get(i).heatCapacity();
		}		
		return heatCapacity;
	}
	
	public static <T extends AirElement> double sumVolume(ArrayList<T> elements){
		double volume = 0.0;
		
		for(int i=0;i<elements.size();i++){
			volume += elements.get(i).volume();
		}		
		return volume;
	}
	
	public static double sumDryAirMass(ArrayList<AirElement> elements){
		double mass = 0.0;
		for(int i=0;i<elements.size();i++){
			mass += elements.get(i).volume()*elements.get(i).density();
		}		
		return mass;
	}
	
	public static double sumHumidityAdvection(ArrayList<AirElement> elements){
		double advection = 0.0;
		for(int i=0;i<elements.size();i++){
			advection += elements.get(i).humidityAdvection();
		}		
		return advection;
	}
	
	public static double sumHumidityGain(ArrayList<AirElement> elements){
		double gain = 0.0;
		for(int i=0;i<elements.size();i++){
			gain += elements.get(i).humidityGain();
		}		
		return gain;
	}

}
