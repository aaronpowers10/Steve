package steve.airside;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import willie.controls.Sensor;
import willie.core.Conversions;
import willie.core.WillieObject;

public class AirPressureSensor implements WillieObject, Sensor {
	
	private String name;
	private AirNode node;
	
	public AirPressureSensor(String name){
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
		return Conversions.psiToInchesWater(node.pressure() - 14.7); //hack
	}



}