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

	private ContinuousSpace<Object> space; // Ort des Sch�dling
	private Grid<Object> grid; // Koordinaten des Sch�dling
	private int inkubation; // Zeitspanne zwischen Geburt und Vermehrung (Inkubationszeit)
	private int zaehler = 0; // Latenzzeit
	private int resistenz; // gibt Resistenzgrad der Crop an
	private int zeit; // z�hlt die ticks
	private static int birth; // tick bei der Sch�dling geboren wurde
	private static int leaf; //Angabe auf welchem Blatt sich GR befindet//L�SCHNE!!!
	private boolean anfaelligkeit = true; //gibt an ob pest pflanze gerade befallen kann oder ob diese durch ein fungizid gesch�tzt ist
	public boolean isVisible = false; //pest wird f�r landwirt erst sichtbar, wenn latenzzeit abgelaufen ist
	int a; //gibt an, an welcher crop die Pest sitzt
	public boolean isAlive;
	
	private int blatt; //Angabe auf welchem Blatt sich GR befindet
	private int geburt;
	private double latenttime = 0;
	
	
	public boolean isDone; ///wird true, wenn agent sich fertig verhalten hat
	
	List<Crop> agenten = new ArrayList<Crop>();
	
	


	// ------------------------------------------ Aufrufe
	// ---------------------------------------------------------------------------------------\\

	public int getBirth() {
		return geburt;
	}
	
	public int getLeaf(){
		return blatt;
	}
	/*public static boolean getSichtbar(){
		return sichtbar;
	}*/


	
	// ------------------------------------- Konstruktor f�r Pest
	// --------------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Pest �bertragen
	public Pest(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrung,
			int resistance, int birth) {
		this.space = space;
		this.grid = grid;
		this.inkubation = vermehrung;
		this.resistenz = resistance;
		this.isAlive = true;
		this.isVisible = false;

		// TODO Auto-generated constructor stub
	}

	// ---------------------------------- Beginn des "t�glichen" Ablaufs
	// --------------------------------------------------------------------------\\

	//@ScheduledMethod(start = 1, interval = 2)
	public void start() {
		//System.out.println("Pest");
		
		zaehler++;
		if (Data.getZeit() > Farmer.getInDays() | Farmer.getInDays() == 0) {    // erst wenn Wirkzeit Fungizid abgelaufen ist, kann sich Pilz weiter vermehren
			anfaelligkeit = true;
			//nur wenn keine wirkung psm kann sich pilz weiterentwickeln
			//Latenzzeit des Pilzes ist Temperaturabh�ngig
			//System.out.println("ich werde aufgerufen");
			if (Data.getTemp() > 10 & Data.getTemp() < 20){       //zaehler z�hlt Tage bis Latenzzeit abgelaufen ist
				latenttime++;
			}else{
				latenttime += 0.5;
			}
		}else{
			anfaelligkeit = false;
		}
		
		/*if (Data.getZeit() == 30){
			sterbe();
		}*/
		

		
		//System.out.println("Pest.blatt " + leaf);
		

		//latenttime++;
		
		//zeit++;
		
		
		
		if(zaehler <= 1){
			
			isVisible = false;
			geburt = Data.getZeit() - 1;
			
               //ersten Pilzen wird Ort hier zugewiesen, Rest als Spore
			//if(Data.getZeit() <= 1){
			Random ort = new Random();
			/*if (Data.getZeit() < Data.getEc30()){
				blatt = 6;

			} else*/ 
			if (Data.getZeit() < Data.getEc31()){
				blatt = ort.nextInt(3)+3;   //nach Anzahl der Bl�tter welche laut Abb.223 vorhanden sind
												// hier mgl.: f-9(9), f-8(8), f-7(7), f-6(6), f-5(5), f-4(4), f-3 (3) 

			} else if (Data.getZeit() < (Data.getEc32()+3)){
				blatt = ort.nextInt(3) + 2;

			}else if (Data.getZeit() < (Data.getEc37()+3)){
				blatt = ort.nextInt(3) + 1;

			} else if (Data.getZeit() < (Data.getEc47()+3)){
				blatt = ort.nextInt(3);

			} else if (Data.getZeit() < (Data.getEc59()+3)){
				blatt = ort.nextInt(3);

			} else if (Data.getZeit() < (Data.getEc71()+3)){
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
			//List<Crop> agenten = new ArrayList<Crop>();
			agenten.clear();
			//int anzahlCrop = 0; // AnzahlCrops in Umgebung des Sch�dlings
			for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
				//anzahlCrop += cell.size();
				for(Crop crop : cell.items()){
					agenten.add(crop);
				}
			}

		
		// wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
		// Sch�dling fort
		// wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
			//einem der crops in der umgebung gespeichert
		// wenn keine Crop vorhanden, dann stirbt Sch�dling ab

		
		if (agenten.size() > 0){
			Random ag = new Random();
			a = ag.nextInt(agenten.size());        // wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
			agenten.get(a).GR.add(this);             //einem der crops in der umgebung gespeichert
			
		}else {
			sterbe();
			System.out.println("ich bin falsch zugeordnet");
		}
	}


			
		
			if (latenttime > inkubation){                     // wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
																// Sch�dling fort
				isVisible = true;                           //sobald Inkubationszeit abgelaufen ist, wird schadorg. sichtbar und farmer spritzt dementsprechend
				
			
				if(anfaelligkeit == true) { // Idee: vermehrung nur wenn z�hler gr��er als die protektive Wirkung	
								
					fortpflanzung1();
				}
			} else {
				isVisible = false;
			}
	}
		
		
	

	// -------------------------------------- Fortpflanzung Sch�dling
	// ----------------------------------------------------------------------------\\

	public void fortpflanzung1() {
		//es kommt nur zw. 5 und 20 grad zum sporenflug
		if(Data.getTemp() > 5 & Data.getTemp() < 20){
			fortpflanzung2();
		} 
	}
	
	
	
	public void fortpflanzung2() {
		
		GridPoint pt = grid.getLocation(this);

		// Wahrscheinlichkeit, dass Sch�dling die Geburt �berlebt ist je nach
		// Resistenzgrad der Weizenpflanze unterschiedlich hoch
		int j = 0; // j gibt die Wahrscheinlichkeit an mit der der neue Sch�dling die Geburt nicht
					// �berlebt (wenn j=80, dann sterben 80% der neuen Sporen ab)

		// Annahme, dass bei Befallsh�ufigkeit �ber 80 % der Sch�dling schwerer einen
		// Platz an einer Crop findet und somit die Wahrscheinlichkeit die Pflanze zu
		// befallen sinkt
		
		if (resistenz == 1) {							//geringer Resistenzstatus
			if (Farmer.getSchaedenprozGR() < 85) { //alt 80,88 
				j = 0; //alt 45
			} else {
				j = 88; 
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

			// Falls falsche Zahl in GUI Oberfl�che eingegeben wird bricht die Simulation ab
		} else {
			throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
		}

		// aus einem Sch�dling k�nnen max 2 neue PestSporen entstehen
		// mit oben festgelegter Wahrscheinlichkeit (j) sterben diese jedoch vor der
		// Geburt schon wieder ab
		// Wahrscheinlichkeit, dass Pest �berlebt h�ngt von Sorte ab
		// Pest stirbt in Modell gleich hier wieder ab (wenn von Crop aus aufgerufen werden w�rde
		// wird irgendeine Pest get�tet und nicht die neue! == nicht gewollt)
		for (int i = 0; i < 2; i++) {
			Random tot = new Random();
			int wahrscheinlichkeit = tot.nextInt(99) + 1; // zuf�llige zahl zwischen 1 und 100 (entsprechen %)

			// Wenn die Zufallszahl h�her ist, als der oben festgelegte Wert f�r j, dann
			// �berlebt die neue Pest
			if (wahrscheinlichkeit > j) {

				// Erstelle zun�chst Spore, welche sich unter geeigneten Witterungsbed zu schadorg. wird
				
				NdPoint spacePt = space.getLocation(this);
				Context<Object> context = ContextUtils.getContext(this);

				int geburt = Data.getZeit();				
				
				PestSpore pestSpore = new PestSpore(space, grid, inkubation, resistenz, geburt, zeit);
				context.add(pestSpore);

				space.moveTo(pestSpore, spacePt.getX(), spacePt.getY());

				grid.moveTo(pestSpore, pt.getX(), pt.getY());
				
			}
		}
	}
	

	// -------------------------------------------- Absterben Pest
	// -----------------------------------------------------------------------------\\

	public void sterbe() {
		
		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);
		isAlive = false;
		System.out.println("ich bin tot" + isAlive);
		
	}

	// ---------------------------------------- Bewegung zu neuer Pflanze
	// ----------------------------------------------------------------------\\

	public void step() {

		// Anzahl Weizenpflanzen im Umfeld (21x21) des Sch�dlings detektieren
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

		// zuf�llig eine Crop aus der liste ausw�hlen
		int index = RandomHelper.nextIntFromTo(0, weizen.size() - 1);
		Object wei = weizen.get(index);
		GridPoint Point = grid.getLocation(wei); // Standort der ausgew�hlten Crop bestimmen

		// Spore bewegt sich zu Zielpflanze (zielgerichtet)
		if (!Point.equals(grid.getLocation(this))) { // Bewegung findet nur statt, wenn sich Sch�dling nicht
														// schon an diesem Ort befindet
			NdPoint myPoint = space.getLocation(this); // aktueller Standort der Pest bestimmen

			NdPoint otherPoint = new NdPoint(Point.getX(), Point.getY()); // Koordinaten der Pflanze bestimmen
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint); // Winkel f�r Bewegung
																							// bestimmen
			double distance = space.getDistance(myPoint, otherPoint); // Entfernung von Sch�dling zu Crop bestimmen

			space.moveByVector(this, distance, angle, 0); // Bewegung des Sch�dling auf Space
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) otherPoint.getX(), (int) otherPoint.getY()); // Bewegung des Sch�dling auf Grid
		}
		
	
	}
	
	public void zuordnung() {

		// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
				GridPoint pt = grid.getLocation(this); // speichert Standort der Pest

				GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
						Crop.class, 1, 1);
				List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																					// Umgebung der Pest in einer Liste
				List<Crop> agents = new ArrayList<Crop>();
				
				int anzahlCrop = 0; // AnzahlCrops in Umgebung des Sch�dlings
				for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
					anzahlCrop += cell.size();
					for(Crop crop : cell.items()){
						agents.add(crop);
					}
				}



				
				if (agents.size() <= 0){

					// Anzahl Weizenpflanzen im Umfeld (21x21) des Sch�dlings detektieren
					// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)


		
					//sucht zuf�lliuge Weizenpflanze aus
					List<Object> wheat = new ArrayList<Object>();
					for (Object obj : grid.getObjects()) { 
						if (obj instanceof Crop) {
							wheat.add(obj);
						}
					}

					// zuf�llig eine Crop aus der liste ausw�hlen
					int index = RandomHelper.nextIntFromTo(0, wheat.size() - 1);
					Object wei = wheat.get(index);
					GridPoint Point = grid.getLocation(wei); // Standort der ausgew�hlten Crop bestimmen

					// Spore bewegt sich zu Zielpflanze (zielgerichtet)
					if (!Point.equals(grid.getLocation(this))) { // Bewegung findet nur statt, wenn sich Sch�dling nicht
																		// schon an diesem Ort befindet
						NdPoint myPoint = space.getLocation(this); // aktueller Standort der Pest bestimmen

						NdPoint otherPoint = new NdPoint(Point.getX(), Point.getY()); // Koordinaten der Pflanze bestimmen
						double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint); // Winkel f�r Bewegung
																							// bestimmen
						double distance = space.getDistance(myPoint, otherPoint); // Entfernung von Sch�dling zu Crop bestimmen

						space.moveByVector(this, distance, angle, 0); // Bewegung des Sch�dling auf Space
						myPoint = space.getLocation(this);
						grid.moveTo(this, (int) otherPoint.getX(), (int) otherPoint.getY()); // Bewegung des Sch�dling auf Grid
					}
				}
		
	
			


	}
}

