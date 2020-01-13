/*
 *
 *  Copyright (C) 2018 Aaron Powers
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
import willie.controls.Sensor;
import willie.core.WillieObject;

public class AirTemperatureSensor implements WillieObject, Sensor {
	
	private String name;
	private AirNode node;
	
	public AirTemperatureSensor(String name){
		this.name = name;
	}
	
	@Override
	public void read(BookerObject objectData, NamespaceList<WillieObject> objectReferences) {
		node = (AirNode)objectReferences.get(objectData.getAlpha("Node"));		
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public double sensorOutput() {
		return node.drybulb();
	}



}
