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
import willie.core.RequiresWeather;
import willie.core.Weather;
import willie.core.WillieObject;

public class OutdoorAirNode implements AirNode, RequiresWeather, WillieObject{
	
	private String name;
	private Weather weather;
	
	public OutdoorAirNode(String name){
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		
	}

	@Override
	public void linkToWeather(Weather weather) {
		this.weather = weather;
	}

	@Override
	public double pressure() {
		return weather.pressure();
	}

	@Override
	public double drybulb() {
		return weather.drybulb();
	}

	@Override
	public double enthalpy() {
		return weather.enthalpy();
	}

	@Override
	public double humidityRatio() {
		return weather.humidityRatio();
	}
	
	@Override
	public double dewpoint() {
		return Psychrometrics.dewpointFHumidityRatio(drybulb(),humidityRatio(),pressure());
	}

	@Override
	public double density() {
		return Psychrometrics.densityFHumidityRatio(drybulb(), humidityRatio(), pressure());
	}

}
