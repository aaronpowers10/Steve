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

import willie.core.ObjectFactory;
import willie.core.WillieObject;

public class AirsideFactory implements ObjectFactory{

	@Override
	public WillieObject create(String type, String name) {
		if (type.equals("Air Pressure Sensor")) {
			return new AirPressureSensor(name);
		}else if (type.equals("Air Temperature Sensor")) {
			return new AirTemperatureSensor(name);
		}else if (type.equals("Ambient Air Node")) {
			return new AmbientAirNode(name);
		}else if (type.equals("Cooling Coil")) {
			return new CoolingCoilSection(name);
		}else if (type.equals("Duct")) {
			return new Duct(name);
		} else if (type.equals("Fan")) {
			return new FanSection(name);
		}else if (type.equals("Fixed OA Section")) {
			return new FixedOutsideAirSection(name);
		}else if (type.equals("Outside Air Node")) {
			return new OutdoorAirNode(name);
		}else if (type.equals("Simple Flow Path")) {
			return new SimpleFlowPath(name);
		} else if (type.equals("Variable Node")) {
			return new VariableAirNode(name);
		}else if (type.equals("VAV Box")) {
			return new VAVBox(name);
		}else if (type.equals("Wetbulb Sensor")) {
			return new WetbulbSensor(name);
		} else if (type.equals("Zone")) {
			return new Zone(name);
		} else {
			return null;
		}
	}
}