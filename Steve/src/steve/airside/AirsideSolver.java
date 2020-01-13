package steve.airside;

import booker.building_data.BookerObject;
import booker.building_data.NamespaceList;
import isaac.nonlinear.BinaryTournamentSelector;
import isaac.nonlinear.GeneticOptimizer;
import isaac.nonlinear.MaxGenerationsStoppingCriteria;
import isaac.nonlinear.NSGACreator;
import isaac.nonlinear.NonlinearSystem;
import willie.core.Simulator;
import willie.core.WillieObject;

public class AirsideSolver implements WillieObject, Simulator{
	
	private String name;

	@Override
	public String name() {
		return "Solver";
	}

	@Override
	public void read(BookerObject data, NamespaceList<WillieObject> references) {
		// TODO Auto-generated method stub
		
	}
	
	private void test() {
		double initialLamda = 5;
		double geoFactor = 5;
		double minGeoFactor = 5;
		double maxGeoFactor = 5;
		int maxIterations = 1;
		int maxTries = 1;
		int initialPopulationSize = 1;
		int generationsToRun = 1;
		double etaC = 5;
		double etaM = 5;
		double mutationProbability = 5;

		NonlinearSystem system = new NonlinearSystem();
		system.setInitialLamda(initialLamda);
		system.setGeometricFactor(geoFactor);
		system.setMaxIterations(maxIterations);

		NSGACreator nsga2 = new NSGACreator(initialPopulationSize / 2, new BinaryTournamentSelector(), etaC, etaM,
				mutationProbability);

		MaxGenerationsStoppingCriteria stoppingCriteria = new MaxGenerationsStoppingCriteria(generationsToRun);

	}

}
