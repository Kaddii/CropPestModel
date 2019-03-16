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
	private int zeit; // zählt die ticks
	private static int birth; // tick bei der Schädling geboren wurde
	private static int leaf; //Angabe auf welchem Blatt sich GR befindet//LÖSCHNE!!!
	private boolean anfaelligkeit = true; //gibt an ob pest pflanze gerade befallen kann oder ob diese durch ein fungizid geschützt ist
	private static boolean sichtbar = false; //pest wird für landwirt erst sichtbar, wenn latenzzeit abgelaufen ist
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
	public static boolean getSichtbar(){
		return sichtbar;
	}


	
	// ------------------------------------- Konstruktor für Pest
	// --------------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Pest übertragen
	public Pest(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrung,
			int resistance, int birth, int leaf, int zeit) {
		this.space = space;
		this.grid = grid;
		this.inkubation = vermehrung;
		this.resistenz = resistance;
		this.isAlive = true;
		//this.birth = birth;
		//this.leaf = leaf;
		this.zeit = zeit;
		// TODO Auto-generated constructor stub
	}

	// ---------------------------------- Beginn des "täglichen" Ablaufs
	// --------------------------------------------------------------------------\\

	//@ScheduledMethod(start = 1, interval = 2)
	public void start() {
		//System.out.println("Pest");
		
		zaehler++;
		if (Data.getZeit() > Farmer.getInDays() | Farmer.getInDays() == 0) {    // erst wenn Wirkzeit Fungizid abgelaufen ist, kann sich Pilz weiter vermehren
			anfaelligkeit = true;
			//nur wenn keine wirkung psm kann sich pilz weiterentwickeln
			//Latenzzeit des Pilzes ist Temperaturabhängig
			//System.out.println("ich werde aufgerufen");
			if (Data.getTemp() > 10 & Data.getTemp() < 20){       //zaehler zählt Tage bis Latenzzeit abgelaufen ist
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
			
			sichtbar = false;
			geburt = Data.getZeit() - 1;
			
               //ersten Pilzen wird Ort hier zugewiesen, Rest als Spore
			//if(Data.getZeit() <= 1){
			Random ort = new Random();
			/*if (Data.getZeit() < Data.getEc30()){
				blatt = 6;

			} else*/ 
			if (Data.getZeit() < Data.getEc31()){
				blatt = ort.nextInt(3)+3;   //nach Anzahl der Blätter welche laut Abb.223 vorhanden sind
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
			//System.out.println("PestSpore.blatt" + leaf);
			//System.out.println("geburtGR  " + geburt + "   blattgr  " + blatt);
		//}
		
		//System.out.println("geburtGR  " + geburt);
		
		
		// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
		GridPoint pt = grid.getLocation(this); // speichert Standort der Pest

		GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
				Crop.class, 1, 1);
		List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																			// Umgebung der Pest in einer Liste
		//List<Crop> agenten = new ArrayList<Crop>();
		agenten.clear();
		int anzahlCrop = 0; // AnzahlCrops in Umgebung des Schädlings
		for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
			anzahlCrop += cell.size();
			for(Crop crop : cell.items()){
				agenten.add(crop);
			}
		}

		
		// wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
		// Schädling fort
		// wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
			//einem der crops in der umgebung gespeichert
		// wenn keine Crop vorhanden, dann stirbt Schädling ab

		
		if (agenten.size() > 0){
			Random ag = new Random();
			a = ag.nextInt(agenten.size());        // wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
			agenten.get(a).gelb.add(this);             //einem der crops in der umgebung gespeichert
			//System.out.println(agenten.get(a).gelb.toString());
			
			if (blatt <=2) {
				agenten.get(a).gelbfbf2.add(this);
			}
		}else {
			sterbe();
		}
	}


		
		
			/*if (geburt < 1){                     // wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
				agenten.get(a).gelbS.add(this);            // Schädling fort
				sichtbar = true;                           //sobald Inkubationszeit abgelaufen ist, wird schadorg. sichtbar und farmer spritzt dementsprechend
				if (sichtbar == true){
				//System.out.println("Ich bin Sichtbar  " + sichtbar);
					if(anfaelligkeit == true) { // Idee: vermehrung nur wenn zähler größer als die protektive Wirkung	
						
						fortpflanzung1();
						}
				}
			}*/
		
			
		
			if (latenttime > inkubation){                     // wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
				agenten.get(a).gelbS.add(this);            // Schädling fort
				sichtbar = true;                           //sobald Inkubationszeit abgelaufen ist, wird schadorg. sichtbar und farmer spritzt dementsprechend
				if (sichtbar == true){
				System.out.println("Ich bin Sichtbar  " + sichtbar);
				}
				
			
			
				if(anfaelligkeit == true) { // Idee: vermehrung nur wenn zähler größer als die protektive Wirkung	
								
					fortpflanzung1();
				}
			} else {
				isDone = true;
			}
			

	}
		
		
	

	// -------------------------------------- Fortpflanzung Schädling
	// ----------------------------------------------------------------------------\\

	public void fortpflanzung1() {
		//es kommt nur zw. 5 und 20 grad zum sporenflug
		if(Data.getTemp() > 5 & Data.getTemp() < 20){
			fortpflanzung2();
		} else{
			isDone= true;
		}
	}
	
	
	
	public void fortpflanzung2() {
		//System.out.println("Pilz vermehrt sich !!!!!!!!!!!!!!!!!!!");
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
			if (Farmer.getSchaedenprozGR() < 85) { //alt 80,88
				j = 0; //alt 45
			} else {
				j = 88; // WAS IST PASSENDE ZAHL??
			}

		} else if (resistenz == 2) {
			if (Farmer.getSchaedenprozGR() < 85) {
				j = 90; // Simulation endet damit bei 40-70% BH
			} else {
				j = 95;
			}

		} else if (resistenz == 3) {
			if (Farmer.getSchaedenprozGR() < 85) {
				j = 93; // Simulation endet ca. bei 5 % BH, aber exponentielles Wachstum
			} else {
				j = 97;
			}

			// Falls falsche Zahl in GUI Oberfläche eingegeben wird bricht die Simulation ab
		} else {
			throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
		}

		// aus einem Schädling können max 3 neue entstehen, dh. am ende sind 4 da
		// mit oben festgelegter Wahrscheinlichkeit (j) sterben diese jedoch vor der
		// Geburt schon wieder ab
		// Wahrscheinlichkeit, dass Pest überlebt hängt von Sorte ab
		// Pest stirbt in Modell gleich hier wieder ab (wenn von Crop aus aufgerufen
		// wird irgendeine Pest getötet und nicht die neue! == nicht gewollt)
		for (int i = 0; i < 2; i++) {
			Random tot = new Random();
			int wahrscheinlichkeit = tot.nextInt(99) + 1; // zufällige zahl zwischen 1 und 100 (entsprechen %)

			// Wenn die Zufallszahl höher ist, als der oben festgelegte Wert für j, dann
			// überlebt die neue Pest
			if (wahrscheinlichkeit > j) {
				//System.out.println("ich bin geboren");

				// Abfrage der im Umfeld befindlichen Schädlinge um von diesen den context für
				// den neuen Schädling zu übernehmen.

				/*List<Object> pests = new ArrayList<Object>();
				for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {

					if (obj instanceof Pest) {
						pests.add(obj);
					}

				}*/

				// Erstelle zunächst Spore, welche sich unter geeigneten Witterungsbed zu schadorg. wird
				
				NdPoint spacePt = space.getLocation(this);
				Context<Object> context = ContextUtils.getContext(this);

				int geburt = Data.getZeit();				
				
				
				//Pest pest = new Pest(space, grid, inkubation, resistenz, geburt, blatt, zeit);
				//context.add(pest);
				PestSpore pestSpore = new PestSpore(space, grid, inkubation, resistenz, geburt, leaf, zeit);
				context.add(pestSpore);

				space.moveTo(pestSpore, spacePt.getX(), spacePt.getY());

				grid.moveTo(pestSpore, pt.getX(), pt.getY());
				
				//step();
				//System.out.println("ich habe sporen ausgeschleudert");
			}
		}
		isDone = true;
		}
	

	// -------------------------------------------- Absterben Pest
	// -----------------------------------------------------------------------------\\

	public void sterbe() {
		
		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);
		isAlive = false;
		/*if(agenten.get(a).gelb.size() > 0){
		agenten.get(a).gelb.remove(this); 
		agenten.get(a).gelbS.remove(this);
		agenten.get(a).gelbfbf2.remove(this);
		System.out.println("ich sterbe");
		}*/
		
		isDone = true;
		//System.out.println("gelb");
		//System.out.println(agenten.get(a).gelb.toString());
		
	}

	// ---------------------------------------- Bewegung zu neuer Pflanze
	// ----------------------------------------------------------------------\\

	public void step() {

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
		
	
	}
	
	public void zuordnung() {

		// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
				GridPoint pt = grid.getLocation(this); // speichert Standort der Pest

				GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
						Crop.class, 1, 1);
				List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																					// Umgebung der Pest in einer Liste
				List<Crop> agents = new ArrayList<Crop>();
				
				int anzahlCrop = 0; // AnzahlCrops in Umgebung des Schädlings
				for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
					anzahlCrop += cell.size();
					for(Crop crop : cell.items()){
						agents.add(crop);
					}
				}



				
				if (agents.size() <= 0){

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
				}
		
	
			


	}
}

//--------------------- Am Ende löschen----
/*/Annahme, dass GR ein obligater Parasit ist, überlebt nur auf lebenden/vorhandenen Blätter
// Wenn Blatt abstirbt, stirbt auch Pilz

if(zeit > Data.getEc37() && leaf > 5){
	sterbe();
} else if(zeit > Data.getEc47() && leaf > 4){
	sterbe();
} else if (zeit > Data.getEc59() && leaf > 3){
	sterbe();
} else if (zeit > Data.getEc71() && leaf > 2){
	sterbe();
} */