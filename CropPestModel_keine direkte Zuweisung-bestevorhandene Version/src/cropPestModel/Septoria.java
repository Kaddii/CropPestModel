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
 * @author Uli
 *
 */
public class Septoria {

	private ContinuousSpace<Object> space; // Ort des Sch�dling
	private Grid<Object> grid; // Koordinaten des Sch�dling
	private int inkubation; // Zeitspanne zwischen Geburt und Vermehrung (Inkubationszeit)
	private int zaehler = 0; // z�hlt die Tage seit der letzten "Geburt" neuer Sch�dlinge
	private int resistenz; // gibt Resistenzgrad der Crop an
	private int zeit; // z�hlt die ticks
	private static int birth; // tick bei der Sch�dling geboren wurde
	private static int leaf; //Angabe auf welchem Blatt sich GR befindet
	private static boolean sichtbar; //bevor erste inkubationszeit abgelaufen ist, kann pilz nicht entdeckt werden

	
	//nach1811
	private boolean anfaelligkeit; //gibt an ob pflanze durch spritzmittel gesch�tzt ist
	private int blatt; //gibt an auf welchem blatt sich ST befindet
	private int geburt;
	private double latenttime =0;
	
	//double [] rain = new double[3];


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
	


	
	// ------------------------------------- Konstruktor f�r Septoria
	// --------------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Pest �bertragen
	public Septoria(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrungST,
			int resistance, int birthST, int leafST, int zeit) {
		this.space = space;
		this.grid = grid;
		this.inkubation = vermehrungST;
		this.resistenz = resistance;
		this.birth = birthST;
		this.leaf = leafST;
		this.zeit = zeit;
		// TODO Auto-generated constructor stub
	}

	// ---------------------------------- Beginn des "t�glichen" Ablaufs
	// --------------------------------------------------------------------------\\

	//@ScheduledMethod(start = 1, interval = 2)
	public void start() {
		//�BERNOMMEN VON PEST!!!!!!!!! ANPASSEN!!!!!!!!!!!!!!!
		
		
		if (Data.getZeit() > Farmer.getInDaysST() | Farmer.getInDaysST() == 0) {    // erst wenn Wirkzeit Fungizid abgelaufen ist, kann sich Pilz weiter vermehren
			anfaelligkeit = true;  
			/*/Latenzzeit des Pilzes ist Temperaturabh�ngig
			latenttime += Data.getTemp() + 2.4;
			zaehler++; //z�hlt tage seit geburt
			//zeit++;*/

		}else{
			anfaelligkeit = false;
		}
		//Latenzzeit des Pilzes ist Temperaturabh�ngig
		latenttime += Data.getTemp() + 2.4;
		zaehler++; //z�hlt tage seit geburt
		//zeit++;
		
		//System.out.println("Pest.blatt " + leaf);
		

		
		
		
		if(zaehler <= 1){
			
			geburt = Data.getZeit() - 1;
			
			//System.out.println("ich bin geboren" + geburt);
               //zu Beginn immer auf sonst. Bl�ttern

			Random ort = new Random();
			/*if (Data.getZeit() < Data.getEc30()){
				blatt = 6;

			} else */
			if (Data.getZeit() < Data.getEc31()){
				blatt = ort.nextInt(3)+3;   //nach Anzahl der Bl�tter welche laut Abb.223 vorhanden sind
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
			//System.out.println("ST.blatt" + blatt);
		}
		
		
		// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
		GridPoint pt = grid.getLocation(this); // speichert Standort der Pest

		GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
				Crop.class, 1, 1);
		List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																			// Umgebung der Pest in einer Liste
		List<Crop> agenten = new ArrayList<Crop>();
		
		int anzahlCrop = 0; // AnzahlCrops in Umgebung des Sch�dlings
		for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
			anzahlCrop += cell.size();
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
			int a = ag.nextInt(agenten.size());        // wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
			agenten.get(a).sept.add(this);             //einem der crops in der umgebung gespeichert
			
			if (blatt == 6) {
				agenten.get(a).septfSechs.add(this);
			}
			
		
			if (latenttime > inkubation){                     // wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
				agenten.get(a).septS.add(this);            // Sch�dling fort
				sichtbar = true;                           //sobald Inkubationszeit abgelaufen ist, wird schadorg. sichtbar und farmer spritzt dementsprechend
					if (sichtbar == true){
				//System.out.println("Ich bin Sichtbar  " + sichtbar);
					}
			
			
					if(anfaelligkeit == true) { // Idee: vermehrung nur wenn z�hler gr��er als die protektive Wirkung	
								
						fortpflanzung1();
					}
			}else {
				sichtbar = false;
			}
		} else if (anzahlCrop == 0) {
			if(Data.getZeit() <= 1){
				step();
			}else{
			sterbe();
			}
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
		//System.out.println("Pilz vermehrt sich !!!!!!!!!!!!!!!!!!!");
		GridPoint pt = grid.getLocation(this);

		// Wahrscheinlichkeit, dass Sch�dling die Geburt �berlebt ist je nach
		// Resistenzgrad der Weizenpflanze unterschiedlich hoch
		int j = 0; // j gibt die Wahrscheinlichkeit an mit der der neue Sch�dling die Geburt nicht
					// �berlebt (wenn j=80, dann sterben 80% der neuen Sporen ab)

		if (resistenz == 1) {

			// Annahme, dass bei Befallsh�ufigkeit �ber 80 % der Sch�dling schwerer einen
			// Platz an einer Crop findet und somit die Wahrscheinlichkeit die Pflanze zu
			// befallen sinkt
			//j = 0;
			if (Farmer.getSchaedenprozST() < 85){//95) {
				j = 0;
			} else {
				j = 90; // WAS IST PASSENDE ZAHL??
			}

		} else if (resistenz == 2) {
			if (Farmer.getSchaedenprozST() < 85) {
				j = 93; // Simulation endet damit bei 40-70% BH
			} else {
				j = 95;
			}

		} else if (resistenz == 3) {
			if (Farmer.getSchaedenprozST() < 85) {
				j = 98; // Simulation endet ca. bei 5 % BH, aber exponentielles Wachstum
			} else {
				j = 97;
			}

			// Falls falsche Zahl in GUI Oberfl�che eingegeben wird bricht die Simulation ab
		} else {
			throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
		}

		// aus einem Sch�dling k�nnen max 3 neue entstehen, dh. am ende sind 4 da
		// mit oben festgelegter Wahrscheinlichkeit (j) sterben diese jedoch vor der
		// Geburt schon wieder ab
		// Wahrscheinlichkeit, dass Pest �berlebt h�ngt von Sorte ab
		// Pest stirbt in Modell gleich hier wieder ab (wenn von Crop aus aufgerufen
		// wird irgendeine Pest get�tet und nicht die neue! == nicht gewollt)
		for (int i = 0; i < 6; i++) {
			Random tot = new Random();
			int wahrscheinlichkeit = tot.nextInt(99) + 1; // zuf�llige zahl zwischen 1 und 100 (entsprechen %)

			// Wenn die Zufallszahl h�her ist, als der oben festgelegte Wert f�r j, dann
			// �berlebt die neue Pest
			if (wahrscheinlichkeit > j) {

				// Abfrage der im Umfeld befindlichen Sch�dlinge um von diesen den context f�r
				// den neuen Sch�dling zu �bernehmen.

				List<Object> septoria = new ArrayList<Object>();
				for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {

					if (obj instanceof Septoria) {
						septoria.add(obj);
					}

				}

				// Erstelle zun�chst Spore, welche sich unter geeigneten Witterungsbed zu schadorg. wird
				
				NdPoint spacePt = space.getLocation(this);
				Context<Object> context = ContextUtils.getContext(this);
				
				int geburt = Data.getZeit(); // �ber die Variable geburt wird der Zeitpunkt der Geburt als "birth" variable
				
				
				SeptoriaSpore septoriaSpore = new SeptoriaSpore(space, grid, inkubation, resistenz, geburt, leaf, sichtbar, zeit);
				context.add(septoriaSpore);

				space.moveTo(septoriaSpore, spacePt.getX(), spacePt.getY());

				grid.moveTo(septoriaSpore, pt.getX(), pt.getY());
				
				//step();
				//System.out.println("ich habe sporen ausgeschleudert");
			}
		}
	}

	// -------------------------------------------- Absterben Pest
	// -----------------------------------------------------------------------------\\

	public void sterbe() {

		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);

	}

	// ---------------------------------------- Bewegung zu neuer Pflanze
	// ----------------------------------------------------------------------\\

	public void step() {

		// Anzahl Weizenpflanzen im Umfeld (21x21) des Sch�dlings detektieren
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

	
}

//--------------------------- Nach Fertigstellung l�schen ------------------------------------

/*Random ort = new Random();
if (Data.getZeit() < Data.getEc30()){
	blatt = 6;

} else if (Data.getZeit() < Data.getEc31()){
	blatt = ort.nextInt(7)+3;   //nach Anzahl der Bl�tter welche laut Abb.223 vorhanden sind
									// hier mgl.: f-9(9), f-8(8), f-7(7), f-6(6), f-5(5), f-4(4), f-3 (3) 

} else if (Data.getZeit() < Data.getEc32()){
	blatt = ort.nextInt(8) + 2;

}else if (Data.getZeit() < Data.getEc37()){
	blatt = ort.nextInt(9) + 1;

} else if (Data.getZeit() < Data.getEc47()){
	blatt = ort.nextInt(6);

} else if (Data.getZeit() < Data.getEc59()){
	blatt = ort.nextInt(5);

} else if (Data.getZeit() < Data.getEc71()){
	blatt = ort.nextInt(4);

} else {
	blatt = ort.nextInt(3);
}*/	

/*/System.out.println("septoria");
	if (zeit > Farmer.getInDaysST()) {    // erst wenn Wirkzeit Fungizid abgelaufen ist, kann sich Pilz weiter vermehren
	zaehler++;                          //Zaehler z�hlt Tage bis zur Vermehrung des Pilzes
	}

	zeit++;
	
	//ST ist fakultativer Parasit. lebt auf totem gewebe weiter
	

	
	entscheidungf();
	//weather();
	
}

/*public void weather(){
//wetter

/*DREECKSVERTEILUNG mit a = min; b = max; c =modalwert
 * public double Dreiecksverteilung(double a, double b, double c) {
 * double F = (c - a) / (b - a);
 * double rand = Math.random();
 * if (rand < F) {
 * return a + Math.sqrt(rand * (b - a) * (c - a));
 * } else {
 * return b - Math.sqrt((1-rand) * (b - a) * (b - c));
 * }
 * }
 
entscheidungf();*/



/*public void entscheidungf(){
	
	// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
	GridPoint pt = grid.getLocation(this); // speichert Standort der Pest

	GridCellNgh<Crop> nghCreatorST = new GridCellNgh<Crop>(grid, pt,Crop.class, 1, 1); //sucht Crops in Umgebung
	List<GridCell<Crop>> gridCellsST = nghCreatorST.getNeighborhood(true); // speichert alle Crops in
																		// Umgebung der Pest in einer Liste

	int anzahlCrop = 0; // AnzahlCrops in Umgebung des Sch�dlings
	for (GridCell<Crop> cellST : gridCellsST) { // summiert Liste mit Crops auf
		anzahlCrop += cellST.size();
	}

	// wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
	// Sch�dling fort
	// wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann passiert in
	// diesem Tick nichts
	// wenn keine Crop vorhanden, dann stirbt Sch�dling ab
	
	if (anzahlCrop > 0 && zaehler > inkubation) { // Idee: vermehrung nur wenn z�hler gr��er als die protektive
													// wirkung der Fungizide... hm. Hm, vielleucht so, dass Sprizen
													// den Z�hler um 21
		// zur�cksetzt?
		zaehler = 0; // vermehrungszyklus des Pilzes zur�cksetzen, damit neuer Zyklus beginnt
		sicht = 1;
		fortpflanzung();
	} else if (anzahlCrop == 0){
		sterbe();
	} 
}

// -------------------------------------- Fortpflanzung Sch�dling
// ----------------------------------------------------------------------------\\

public void fortpflanzung() {
	GridPoint pt = grid.getLocation(this);

	// Wahrscheinlichkeit, dass Sch�dling die Geburt �berlebt ist je nach
	// Resistenzgrad der Weizenpflanze unterschiedlich hoch
	int j = 0; // j gibt die Wahrscheinlichkeit an mit der der neue Sch�dling die Geburt nicht
				// �berlebt (wenn j=80, dann sterben 80% der neuen Sporen ab)

	if (resistenz == 1) {

		// Annahme, dass bei Befallsh�ufigkeit �ber 80 % der Sch�dling schwerer einen
		// Platz an einer Crop findet und somit die Wahrscheinlichkeit die Pflanze zu
		// befallen sinkt

		if (Farmer.getSchaedenprozST() < 80) {
			j = 45;
		} else {
			j = 95; // WAS IST PASSENDE ZAHL??
		}

	} else if (resistenz == 2) {
		if (Farmer.getSchaedenprozST() < 80) {
			j = 90; // Simulation endet damit bei 40-70% BH
		} else {
			j = 95;
		}

	} else if (resistenz == 3) {
		if (Farmer.getSchaedenprozST() < 80) {
			j = 97; // Simulation endet ca. bei 5 % BH, aber exponentielles Wachstum
		} else {
			j = 98;
		}

		// Falls falsche Zahl in GUI Oberfl�che eingegeben wird bricht die Simulation ab
	} else {
		throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
	}

	// aus einem Sch�dling k�nnen max 3 neue entstehen, dh. am ende sind 4 da
	// mit oben festgelegter Wahrscheinlichkeit (j) sterben diese jedoch vor der
	// Geburt schon wieder ab
	// Wahrscheinlichkeit, dass Pest �berlebt h�ngt von Sorte ab
	// Pest stirbt in Modell gleich hier wieder ab (wenn von Crop aus aufgerufen
	// wird irgendeine Pest get�tet und nicht die neue! == nicht gewollt)
	for (int i = 0; i < 3; i++) {
		Random tot = new Random();
		int wahrscheinlichkeit = tot.nextInt(99) + 1; // zuf�llige zahl zwischen 1 und 100 (entsprechen %)

		// Wenn die Zufallszahl h�her ist, als der oben festgelegte Wert f�r j, dann
		// �berlebt die neue Pest
		if (wahrscheinlichkeit > j) {

			// Abfrage der im Umfeld befindlichen Sch�dlinge um von diesen den context f�r
			// den neuen Sch�dling zu �bernehmen.

			List<Object> pests = new ArrayList<Object>();
			for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {

				if (obj instanceof Pest) {
					pests.add(obj);
				}

			}

			
			//-------------------Geburt
			//----------------------------------
			
			// Erstelle neuen Sch�dling an bisherigem Ort
			NdPoint spacePt = space.getLocation(this);
			Context<Object> context = ContextUtils.getContext(this);

			int geburt = zeit; // �ber die Variable geburt wird der Zeitpunkt der Geburt als "birth" variable
								// dem neuen Sch�dling �bergeben
			
			int blatt;
			Random ort = new Random();
			if (zeit < Data.getEc30()){
				blatt = 6;
				
			} else if (zeit >= Data.getEc30() && zeit < Data.getEc31()){
				blatt = ort.nextInt(7)+3;   //nach Anzahl der Bl�tter welche laut Abb.223 vorhanden sind
				                            // hier mgl.: f-9(9), f-8(8), f-7(7), f-6(6), f-5(5), f-4(4), f-3 (3) 

			} else if (zeit >= Data.getEc31() && zeit < Data.getEc32()){
				blatt = ort.nextInt(8) + 2;
					
			}else if (zeit >= Data.getEc32() && zeit < Data.getEc37()){
				blatt = ort.nextInt(9) + 1;
				
			} else if (zeit >= Data.getEc37() && zeit < Data.getEc47()){
				blatt = ort.nextInt(6);
				
			} else if (zeit >= Data.getEc47() && zeit < Data.getEc59()){
				blatt = ort.nextInt(5);
				
			} else if (zeit >= Data.getEc59() && zeit < Data.getEc71()){
				blatt = ort.nextInt(4);

			} else {
				blatt = ort.nextInt(3);
			}
			
			//System.out.println("blatt" + blatt);
			

			
			Septoria septoria = new Septoria(space, grid, inkubation, resistenz, geburt, blatt,sichtbar, zeit);
			context.add(septoria);

			space.moveTo(septoria, spacePt.getX(), spacePt.getY());

			grid.moveTo(septoria, pt.getX(), pt.getY());

			step();
		}
	}
}

// -------------------------------------------- Absterben Pest
// -----------------------------------------------------------------------------\\

public void sterbe() {

	Context<Object> context = ContextUtils.getContext(this);
	context.remove(this);

}

// ---------------------------------------- Bewegung zu neuer Pflanze
// ----------------------------------------------------------------------\\

public void step() {

	// Anzahl Weizenpflanzen im Umfeld (21x21) des Sch�dlings detektieren
	// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)

	GridPoint pt = grid.getLocation(this);

	// Crops in der Nachbarschaft bestimmen
	GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, Crop.class, 10, 10);
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

}*/	
	
/*/---------------------------------------ENDE-----------------------------------------------------
int blatt;        // �ber blatt wird bestimmt auf welchem blatt sich GR befindet
                       //zu Beginn immer auf sonst. Bl�ttern
boolean sichtbar = false;

if (zeit < Data.getEc30()){
	blatt = 7;
}else if (zeit >= Data.getEc30() && zeit < Data.getEc31()){
	Random ort = new Random();
	int wahrsch = ort.nextInt(6) + 1;   //nach Anzahl der Bl�tter welche laut Abb.223 vorhanden sind
	if (wahrsch == 1){
		blatt = 6;             //F-5
	} else if (wahrsch == 2){
		blatt = 5;             //F-4
	} else if (wahrsch == 3){
		blatt = 4;             //F-3 ...
	} else if (wahrsch == 4){
		blatt = 3;
	} else {
		blatt = 7;             //sonstige Bl�tter
	}
} else if (zeit >= Data.getEc31() && zeit < Data.getEc32()){
	Random ort = new Random();
	int wahrsch = ort.nextInt(7) + 1;
	if (wahrsch == 1){
		blatt = 6;
	} else if (wahrsch == 2){
		blatt = 5;
	} else if (wahrsch == 3){
		blatt = 4;
	} else if (wahrsch == 4){
		blatt = 3;
	} else if (wahrsch == 5){
		blatt = 2;
	} else {
		blatt = 7;
	}
}else if (zeit >= Data.getEc32() && zeit < Data.getEc37()){
	Random ort = new Random();
	int wahrsch = ort.nextInt(8) + 1;
	if (wahrsch == 1){
		blatt = 6;
	} else if (wahrsch == 2){
		blatt = 5;
	} else if (wahrsch == 3){
		blatt = 4;
	} else if (wahrsch == 4){
		blatt = 3;
	} else if (wahrsch == 5){
		blatt = 2;
	} else if (wahrsch == 6){
		blatt = 1;
	} else {
		blatt = 7;
	}
} else if (zeit >= Data.getEc37() && zeit < Data.getEc47()){
	Random ort = new Random();
	int wahrsch = ort.nextInt(5) + 1;
	if (wahrsch == 1){
		blatt = 5;
	} else if (wahrsch == 2){
		blatt = 4;
	} else if (wahrsch == 3){
		blatt = 3;
	} else if (wahrsch == 4){
		blatt = 2;
	} else if (wahrsch == 5){
		blatt = 1;
	} else if (wahrsch == 6){
		blatt = 0;
	} else {
		blatt = 7;
	}
} else if (zeit >= Data.getEc47() && zeit < Data.getEc59()){
	Random ort = new Random();
	int wahrsch = ort.nextInt(4) + 2;
	if (wahrsch == 2){
		blatt = 4;
	} else if (wahrsch == 3){
		blatt = 3;
	} else if (wahrsch == 4){
		blatt = 2;
	} else if (wahrsch == 5){
		blatt = 1;
	} else if (wahrsch == 6){
		blatt = 0;
	} else {
		blatt = 7;
	}
} else if (zeit >= Data.getEc59() && zeit < Data.getEc71()){
	Random ort = new Random();
	int wahrsch = ort.nextInt(3) + 3;
	if (wahrsch == 3){
		blatt = 3;
	} else if (wahrsch == 4){
		blatt = 2;
	} else if (wahrsch == 5){
		blatt = 1;
	} else if (wahrsch == 6){
		blatt = 0;
	} else {
		blatt = 7;
	}
} else{
	Random ort = new Random();
	int wahrsch = ort.nextInt(2) + 4;
	if (wahrsch == 4){
		blatt = 2;
	} else if (wahrsch == 5){
		blatt = 1;
	} else if (wahrsch == 6){
		blatt = 0;
	} else {
		blatt = 7;
	}
}

//System.out.println("blatt" + blatt);*/