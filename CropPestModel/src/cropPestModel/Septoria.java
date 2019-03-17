package cropPestModel;


/**
 * @author Katrin
 *
 */



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

/**
 * @author Katrin
 *
 */
public class Septoria {

	private ContinuousSpace<Object> space; // Ort des Schädling
	private Grid<Object> grid; // Koordinaten des Schädling
	private int inkubation; // Zeitspanne zwischen Geburt und Vermehrung (Inkubationszeit)
	private int zaehler = 0; // zählt die Tage seit der letzten "Geburt" neuer Schädlinge
	private int resistenz; // gibt Resistenzgrad der Crop an
	private int zeit; // zählt die ticks
	private static int birth; // tick bei der Schädling geboren wurde
	private static int leaf; //Angabe auf welchem Blatt sich GR befindet
	public boolean isVisible; //bevor erste inkubationszeit abgelaufen ist, kann pilz nicht entdeckt werden
	public boolean isAlive; 
	int a; //gibt an, an welcher crop die Pest sitzt
	
	
	private boolean canInfect; //gibt an ob pflanze durch spritzmittel geschützt ist
	private int blatt; //gibt an auf welchem blatt sich ST befindet
	private int geburt;
	private double latenttime = 0;
	
	List<Crop> agenten = new ArrayList<Crop>();
	



	// ------------------------------------------ Aufrufe
	// ---------------------------------------------------------------------------------------\\

	public int getBirth() {
		return geburt;
	}
	
	public int getLeaf(){
		return blatt;
	}
	


	
	// ------------------------------------- Konstruktor für Septoria
	// --------------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Pest übertragen
	public Septoria(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrungST,
			int resistance) {
		this.space = space;
		this.grid = grid;
		this.inkubation = vermehrungST;
		this.resistenz = resistance;
		this.isVisible = false;
		this.isAlive = true;
		// TODO Auto-generated constructor stub
	}

	// ---------------------------------- Beginn des "täglichen" Ablaufs
	// --------------------------------------------------------------------------\\

	public void start() {

		zaehler++; //zählt tage seit geburt

		
		if(zaehler <= 1){
			
			isVisible = false;
			geburt = Data.getZeit() - 1;

			Random ort = new Random();

			if (Data.getZeit() < Data.getEc31()){
				blatt = ort.nextInt(3)+3;   //nach Anzahl der Blätter welche laut Abb.223 vorhanden sind
												// hier mgl.: f-9(9), f-8(8), f-7(7), f-6(6), f-5(5), f-4(4), f-3 (3) 

			} else if (Data.getZeit() < Data.getEc32()){
				blatt = ort.nextInt(3) + 2;

			}else if (Data.getZeit() < Data.getEc37()){
				blatt = ort.nextInt(3) + 1;

			} else if (Data.getZeit() < Data.getEc47()){
				blatt = ort.nextInt(3);

			} else if (Data.getZeit() < Data.getEc59()){
				blatt = ort.nextInt(3);

			} else if (Data.getZeit() < Data.getEc71()){
				blatt = ort.nextInt(3);

			} else {
				blatt = ort.nextInt(3);
			}
		
		
		
				// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
				GridPoint pt = grid.getLocation(this); // speichert Standort der Pest
		
				GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
						Crop.class, 1, 1);
				List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																					// Umgebung der Pest in einer Liste
	
				
				//int anzahlCrop = 0; // AnzahlCrops in Umgebung des Schädlings
				for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
					//anzahlCrop += cell.size();
					for(Crop crop : cell.items()){
						agenten.add(crop);
					}
				}
	
			
				// TODO: wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
				// Schädling fort  
				// wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
					//einem der crops in der umgebung gespeichert
				// wenn keine Crop vorhanden, dann stirbt Schädling ab
				// -------ERKLÄRUNG NOCH GEBRAUCHT????????????????
	
			
			if (agenten.size() > 0){
				Random ag = new Random();
				a = ag.nextInt(agenten.size());        // wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
				agenten.get(a).ST.add(this);             //einem der crops in der umgebung gespeichert
			
			} else {
				allocation();
			}
		}

		//ST überprüft, ob Sie sich weiterentwickeln kann
		if (Data.getZeit() > Farmer.getInDaysST() | Farmer.getInDaysST() == 0) {    // erst wenn Wirkzeit Fungizid abgelaufen ist, kann sich Pilz weiter vermehren
			canInfect = true;  
		}else{
			canInfect = false;
		}
		
		//Latenzzeit des Pilzes ist Temperaturabhängig
		latenttime += (Data.getTemp() + 2.4);
		
		
		
		if (latenttime > inkubation){                     // wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
														// Schädling fort
			isVisible = true;                           //sobald Inkubationszeit abgelaufen ist, wird schadorg. sichtbar und farmer spritzt dementsprechend
	
				if(canInfect == true) { // Idee: vermehrung nur wenn zähler größer als die protektive Wirkung				
					fortpflanzung1();
				}
		}else {
			isVisible = false;
		}
	
	}


	
	
	
	// -------------------------------------- Fortpflanzung Schadorganismus
	// ----------------------------------------------------------------------------\\

	public void fortpflanzung1() {
		
		if (Data.getTemp() >= 2 & Data.getTemp() <= 30 & Data.getHumidity() >= 85 & Data.getRain() > 0.5){
		
			fortpflanzung2();
		}
	}
	
	
	
	public void fortpflanzung2() {
		GridPoint pt = grid.getLocation(this);

		// Wahrscheinlichkeit, dass Schädling die Geburt überlebt ist je nach
		// Resistenzgrad der Weizenpflanze unterschiedlich hoch
		int j = 0; // j gibt die Wahrscheinlichkeit an mit der der neue Schädling die Geburt nicht
					// überlebt (wenn j=80, dann sterben 80% der neuen Sporen ab)

		if (resistenz == 1) {

			// Annahme, dass bei Befallshäufigkeit über 80 % der Schädling schwerer einen
			// Platz an einer Crop findet und somit die Wahrscheinlichkeit die Pflanze zu
			// befallen sinkt
			//j = 0;
			if (Farmer.getSchaedenprozST() < 85){//95)      //niedriger Resistenzstatus
				j = 0;
			} else {
				j = 90; 
			}

		} else if (resistenz == 2) {						//mittlerer Resistenzstatus
			if (Farmer.getSchaedenprozST() < 85) {
				j = 93; 
			} else {
				j = 95;
			}

		} else if (resistenz == 3) {						//hoher Resistenzstatus
			if (Farmer.getSchaedenprozST() < 85) {
				j = 98; 
			} else {
				j = 97;
			}

			// Falls falsche Zahl in GUI Oberfläche eingegeben wird bricht die Simulation ab
		} else {
			throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
		}

		// aus einem Schädling können max 6 neue entstehen, dh. am ende sind 4 da
		// mit oben festgelegter Wahrscheinlichkeit (j) sterben diese jedoch vor der
		// Geburt schon wieder ab
		// Wahrscheinlichkeit, dass Pest überlebt hängt von Sorte ab
		// Pest stirbt in Modell gleich hier wieder ab (wenn von Crop aus aufgerufen
		// wird irgendeine Pest getötet und nicht die neue! == nicht gewollt)
		for (int i = 0; i < 6; i++) {
			Random tot = new Random();
			int wahrscheinlichkeit = tot.nextInt(99) + 1; // zufällige zahl zwischen 1 und 100 (entsprechen %)

			// Wenn die Zufallszahl höher ist, als der oben festgelegte Wert für j, dann
			// überlebt die neue Pest
			if (wahrscheinlichkeit > j) {


				// Erstelle zunächst Spore, welche sich unter geeigneten Witterungsbed zu schadorg. wird
				
				NdPoint spacePt = space.getLocation(this);
				Context<Object> context = ContextUtils.getContext(this);

				SeptoriaSpore septoriaSpore = new SeptoriaSpore(space, grid, inkubation, resistenz);
				context.add(septoriaSpore);

				space.moveTo(septoriaSpore, spacePt.getX(), spacePt.getY());

				grid.moveTo(septoriaSpore, pt.getX(), pt.getY());

			}
		}
	}

	// -------------------------------------------- Absterben Pest
	// -----------------------------------------------------------------------------\\

	public void die() {

		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);
		isAlive = false;

	}

	// ---------------------------------------- Bewegung zu neuer Pflanze
	// ----------------------------------------------------------------------\\

	public void allocation() {
		System.out.println("Zuordnung");
		// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
		GridPoint pt = grid.getLocation(this); // speichert Standort der Pest

		GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
				Crop.class, 1, 1);
		List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																					// Umgebung der Pest in einer Liste
		List<Crop> agents = new ArrayList<Crop>();
				
		for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
			for(Crop crop : cell.items()){
				agents.add(crop);
			}
		}
		
		
		if (agents.size() > 0){
			//Eine der Crops in Umgebung wird ausgewählt
			// zufällig eine Crop aus der liste auswählen
			int index = RandomHelper.nextIntFromTo(0, agents.size() - 1);
			Object wei = agents.get(index);
			GridPoint Point = grid.getLocation(wei); // Standort der ausgewählten Crop bestimmen
		
			// Spore bewegt sich zu Zielpflanze (zielgerichtet)
			if (!Point.equals(grid.getLocation(this))) { // Bewegung findet nur statt, wenn sich Schädling nicht
																		// schon an diesem Ort befindet
				NdPoint myPoint = space.getLocation(this); // aktueller Standort der Pest bestimmen

				NdPoint otherPoint = new NdPoint(Point.getX(), Point.getY()); // Koordinaten der Pflanze bestimmen
				double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint); // Winkel für Bewegung
																							// bestimmen
				double distance = space.getDistance(myPoint, otherPoint); // Entfernung von Schädling zu Crop bestimmen

				space.moveByVector(this, distance, angle, 0); // Bewegung des Schädling auf Space
				myPoint = space.getLocation(this);
				grid.moveTo(this, (int) otherPoint.getX(), (int) otherPoint.getY()); // Bewegung des Schädling auf Grid
			}
			
		} else {
			// Beliebige Crop auf dem Grid wird ausgewählt

			//sucht zufälliuge Weizenpflanze aus
			List<Object> wheat = new ArrayList<Object>();
			for (Object obj : grid.getObjects()) { 
				if (obj instanceof Crop) {
					wheat.add(obj);
				}
			}

			// zufällig eine Crop aus der liste auswählen
			int index = RandomHelper.nextIntFromTo(0, wheat.size() - 1);
			Object wei = wheat.get(index);
			GridPoint Point = grid.getLocation(wei); // Standort der ausgewählten Crop bestimmen
		
			// Spore bewegt sich zu Zielpflanze (zielgerichtet)
			if (!Point.equals(grid.getLocation(this))) { // Bewegung findet nur statt, wenn sich Schädling nicht
																		// schon an diesem Ort befindet
				NdPoint myPoint = space.getLocation(this); // aktueller Standort der Pest bestimmen

				NdPoint otherPoint = new NdPoint(Point.getX(), Point.getY()); // Koordinaten der Pflanze bestimmen
				double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint); // Winkel für Bewegung
																							// bestimmen
				double distance = space.getDistance(myPoint, otherPoint); // Entfernung von Schädling zu Crop bestimmen

				space.moveByVector(this, distance, angle, 0); // Bewegung des Schädling auf Space
				myPoint = space.getLocation(this);
				grid.moveTo(this, (int) otherPoint.getX(), (int) otherPoint.getY()); // Bewegung des Schädling auf Grid
			}
		} 
	}
}

		/*if (agents.size() <= 0){

			// Anzahl Weizenpflanzen im Umfeld (21x21) des Schädlings detektieren
			// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)

			//sucht zufälliuge Weizenpflanze aus
			List<Object> wheat = new ArrayList<Object>();
			for (Object obj : grid.getObjects()) { 
				if (obj instanceof Crop) {
					wheat.add(obj);
				}
			}

			// zufällig eine Crop aus der liste auswählen
			int index = RandomHelper.nextIntFromTo(0, wheat.size() - 1);
			Object wei = wheat.get(index);
			GridPoint Point = grid.getLocation(wei); // Standort der ausgewählten Crop bestimmen

			// Spore bewegt sich zu Zielpflanze (zielgerichtet)
			if (!Point.equals(grid.getLocation(this))) { // Bewegung findet nur statt, wenn sich Schädling nicht
																		// schon an diesem Ort befindet
				NdPoint myPoint = space.getLocation(this); // aktueller Standort der Pest bestimmen

				NdPoint otherPoint = new NdPoint(Point.getX(), Point.getY()); // Koordinaten der Pflanze bestimmen
				double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint); // Winkel für Bewegung
																							// bestimmen
				double distance = space.getDistance(myPoint, otherPoint); // Entfernung von Schädling zu Crop bestimmen

				space.moveByVector(this, distance, angle, 0); // Bewegung des Schädling auf Space
				myPoint = space.getLocation(this);
				grid.moveTo(this, (int) otherPoint.getX(), (int) otherPoint.getY()); // Bewegung des Schädling auf Grid
			}
		}*/

	
	/*public void step() {

		// Anzahl Weizenpflanzen im Umfeld (21x21) des Schädlings detektieren
		// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)

		GridPoint pt = grid.getLocation(this);

		// Crops in der Nachbarschaft bestimmen
		GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, Crop.class, 20, 20);
		List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true);

		// speichert diese Crops in ArrayList
		List<Object> weizen = new ArrayList<Object>();
		for (GridCell<Crop> cell : gridCells) {
			if (cell.size() > 0) {
				Iterator<Crop> iterator = cell.items().iterator();
				while (iterator.hasNext())
					weizen.add(iterator.next());
			}

		}

		// zufällig eine Crop aus der liste auswählen
		int index = RandomHelper.nextIntFromTo(0, weizen.size() - 1);
		Object wei = weizen.get(index);
		GridPoint Point = grid.getLocation(wei); // Standort der ausgewählten Crop bestimmen

		// Spore bewegt sich zu Zielpflanze (zielgerichtet)
		if (!Point.equals(grid.getLocation(this))) { // Bewegung findet nur statt, wenn sich Schädling nicht
														// schon an diesem Ort befindet
			NdPoint myPoint = space.getLocation(this); // aktueller Standort der Pest bestimmen

			NdPoint otherPoint = new NdPoint(Point.getX(), Point.getY()); // Koordinaten der Pflanze bestimmen
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint); // Winkel für Bewegung
																							// bestimmen
			double distance = space.getDistance(myPoint, otherPoint); // Entfernung von Schädling zu Crop bestimmen

			space.moveByVector(this, distance, angle, 0); // Bewegung des Schädling auf Space
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) otherPoint.getX(), (int) otherPoint.getY()); // Bewegung des Schädling auf Grid
		}
		
	
	}*/

	


