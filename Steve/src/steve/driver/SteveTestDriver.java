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
package steve.driver;

import java.io.IOException;
import java.util.ArrayList;

import steve.airside.AirsideFactory;
import willie.core.ObjectFactory;
import willie.core.Project;

public class SteveTestDriver {
	
	public static void main(String[] args) throws IOException{
		
	}
	
	private static void runTest(String fileName) throws IOException{
		System.out.println("RUNNING TEST " + fileName);
		
		ArrayList<ObjectFactory> factories = new ArrayList<ObjectFactory>();
		factories.add(new AirsideFactory());
		
		Project project = new Project(fileName,factories);
		project.simulate();
		System.out.println("ITS OVER!");
}
	
	

}
