package cropPestModel;

/**

/**
 * 
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cern.colt.Arrays;
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
 * @author Uli
 *
 */
public class Pest {

	private ContinuousSpace<Object> space; // Ort des Schädling
	private Grid<Object> grid; // Koordinaten des Schädling
	private int inkubation; // Zeitspanne zwischen Geburt und Vermehrung (Inkubationszeit)
	private int zaehler = 0; // Latenzzeit
	private int resistenz; // gibt Resistenzgrad der Crop an
	private boolean canInfect = true; //gibt an ob pest pflanze gerade befallen kann oder ob diese durch ein fungizid geschützt ist
	public boolean isVisible = false; //pest wird für landwirt erst sichtbar, wenn latenzzeit abgelaufen ist
	int a; //gibt an, an welcher crop die Pest sitzt
	public boolean isAlive;
	
	private int blatt; //Angabe auf welchem Blatt sich GR befindet
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
	


	
	// ------------------------------------- Konstruktor für Pest
	// --------------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Pest übertragen
	public Pest(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrung,
			int resistance) {
		this.space = space;
		this.grid = grid;
		Random inku = new Random();
		this.inkubation = inku.nextInt(4) + 12; // Inkubationszeit 12 bis 15 Tage für GR
		this.resistenz = resistance;
		this.isAlive = true;
		this.isVisible = false;


		// TODO Auto-generated constructor stub
	}

	// ---------------------------------- Beginn des "täglichen" Ablaufs
	// --------------------------------------------------------------------------\\

	public void start() {
		
		
		zaehler++; 		//Zählt die Tage seit der Geburt

		if(zaehler <= 1){
			isVisible = false; //TODO: Wird wirklich gebraucht?? auch bei ST
			geburt = Data.getZeit() - 1;

			 //Pilzen wird Ort hier zugewiesen
			
			Random ort = new Random();
 
			if (Data.getZeit() < Data.getEc31()){
				blatt = ort.nextInt(4)+3;   //nach Anzahl der Blätter welche laut Abb.223 vorhanden sind
												// hier mgl.: f-9(9), f-8(8), f-7(7), f-6(6), f-5(5), f-4(4), f-3 (3) 

			} else if (Data.getZeit() < (Data.getEc32())){
				blatt = ort.nextInt(4) + 2;

			}else if (Data.getZeit() < (Data.getEc37())){
				blatt = ort.nextInt(4) + 1;

			} else if (Data.getZeit() < (Data.getEc47())){
				blatt = ort.nextInt(4);

			} else if (Data.getZeit() < (Data.getEc59())){
				blatt = ort.nextInt(4);

			} else if (Data.getZeit() < (Data.getEc71())){
				blatt = ort.nextInt(4);

			} else {
				blatt = ort.nextInt(4);
			}
		
			// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
			GridPoint pt = grid.getLocation(this); // speichert Standort der Pest
	
			GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
					Crop.class, 1, 1);
			List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																				// Umgebung der Pest in einer Liste
			//List<Crop> agenten = new ArrayList<Crop>();
			agenten.clear();
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
			// wenn keine Crop vorhanden, dann stirbt Schädling ab  -->Erklärung löschen???
	
			
			if (agenten.size() > 0){
				Random ag = new Random();
				a = ag.nextInt(agenten.size());        // wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
				agenten.get(a).GR.add(this);             //einem der crops in der umgebung gespeichert
				
			}else {
				allocation();
			}
		}
		


		//Pest überprüft, ob Sie sich weiterentwickeln kann	
		if (Data.getZeit() > Farmer.getInDays() | Farmer.getInDays() == 0) {    // erst wenn Wirkzeit Fungizid abgelaufen ist, kann sich Pilz weiter vermehren
			canInfect = true;
		}else{
			canInfect = false;
		}
		
		//Latenzzeit des Pilzes ist Temperaturabhängig
		if (Data.getTemp() > 10 & Data.getTemp() < 20){       //zaehler zählt Tage bis Latenzzeit abgelaufen ist
			latenttime++;
		}else{
			latenttime += 0.5;
		}
	
	
				
			
		if (latenttime > inkubation){                     // wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
																	// Schädling fort
			isVisible = true;                           //sobald Inkubationszeit abgelaufen ist, wird schadorg. sichtbar und farmer spritzt dementsprechend		
				
			if(canInfect == true) { // Idee: vermehrung nur wenn zähler größer als die protektive Wirkung						
				fortpflanzung1();
			}
			
		} else {
			isVisible = false;
		}
	}
		
		
	

	// -------------------------------------- Fortpflanzung Pathogen
	// ----------------------------------------------------------------------------\\

	public void fortpflanzung1() {
		//es kommt nur zw. 5 und 20 grad zum sporenflug
		if(Data.getTemp() > 5 & Data.getTemp() < 20){
			fortpflanzung2();
		} 
	}
	
	
	
	public void fortpflanzung2() {
		
		GridPoint pt = grid.getLocation(this);

		// Wahrscheinlichkeit, dass Schädling die Geburt überlebt ist je nach
		// Resistenzgrad der Weizenpflanze unterschiedlich hoch
		int j = 0; // j gibt die Wahrscheinlichkeit an mit der der neue Schädling die Geburt nicht
					// überlebt (wenn j=80, dann sterben 80% der neuen Sporen ab)

		// Annahme, dass bei Befallshäufigkeit über 80 % der Schädling schwerer einen
		// Platz an einer Crop findet und somit die Wahrscheinlichkeit die Pflanze zu
		// befallen sinkt
		
		if (resistenz == 1) {							//geringer Resistenzstatus
			if (Farmer.getSchaedenprozGR() < 80) { //alt 80,88 
					j = 0;
				
			} else {
				j = 93;
			}

		} else if (resistenz == 2) {					//mittlerer Resistenzstatus
			if (Farmer.getSchaedenprozGR() < 85) {
				j = 90; 
			} else {
				j = 95;
			}

		} else if (resistenz == 3) {					//hoher Resistenzstatus
			if (Farmer.getSchaedenprozGR() < 85) {
				j = 93; 
			} else {
				j = 97;
			}

			// Falls falsche Zahl in GUI Oberfläche eingegeben wird bricht die Simulation ab
		} else {
			throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
		}

		// aus einem Schädling können max 2 neue PestSporen entstehen
		// mit oben festgelegter Wahrscheinlichkeit (j) sterben diese jedoch vor der
		// Geburt schon wieder ab
		// Wahrscheinlichkeit, dass Pest überlebt hängt von Sorte ab
		// Pest stirbt in Modell gleich hier wieder ab (wenn von Crop aus aufgerufen werden würde
		// wird irgendeine Pest getötet und nicht die neue! == nicht gewollt)
		Random tot = new Random();
		for (int i = 0; i < 2; i++) {

			int wahrscheinlichkeit = tot.nextInt(99) + 1; // zufällige zahl zwischen 1 und 100 (entsprechen %)

			// Wenn die Zufallszahl höher ist, als der oben festgelegte Wert für j, dann
			// überlebt die neue Pest
			if (wahrscheinlichkeit > j) {

				// Erstelle zunächst Spore, welche sich unter geeigneten Witterungsbed zu schadorg. wird
				
				NdPoint spacePt = space.getLocation(this);
				Context<Object> context = ContextUtils.getContext(this);
			
				
				PestSpore pestSpore = new PestSpore(space, grid, inkubation, resistenz);
				context.add(pestSpore);

				space.moveTo(pestSpore, spacePt.getX(), spacePt.getY());

				grid.moveTo(pestSpore, pt.getX(), pt.getY());
				
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

		// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
		GridPoint pt = grid.getLocation(this); // speichert Standort der Pest

		/*GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
				Crop.class, 10, 10);
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
			
		} else {*/
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
//}

/*public void step() {

// Anzahl Weizenpflanzen im Umfeld (21x21) des Schädlings detektieren
// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)

GridPoint pt = grid.getLocation(this);

// Crops in der Nachbarschaft bestimmen
GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, Crop.class, 10, 10); //alt 10,10
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

