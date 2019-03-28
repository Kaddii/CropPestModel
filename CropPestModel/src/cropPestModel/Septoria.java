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

	private ContinuousSpace<Object> space; // Ort des Sch�dling
	private Grid<Object> grid; // Koordinaten des Sch�dling
	private int inkubation; // Zeitspanne zwischen Geburt und Vermehrung (Inkubationszeit)
	private int zaehler = 0; // z�hlt die Tage seit der letzten "Geburt" neuer Sch�dlinge
	private int resistenz; // gibt Resistenzgrad der Crop an
	public boolean isVisible; //bevor erste inkubationszeit abgelaufen ist, kann pilz nicht entdeckt werden
	public boolean isAlive; 
	private boolean hasInfected;
	int a; //gibt an, an welcher crop die Pest sitzt
	private int leaf;
	
	
	private boolean canInfect; //gibt an ob pflanze durch spritzmittel gesch�tzt ist
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
	


	
	// ------------------------------------- Konstruktor f�r Septoria
	// --------------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Pest �bertragen
	public Septoria(ContinuousSpace<Object> space, Grid<Object> grid,
			int resistance, int leaf) {
		this.space = space;
		this.grid = grid;
		this.resistenz = resistance;
		this.leaf = leaf;
		this.isVisible = false;
		this.isAlive = true;
		this.hasInfected = false;
		//DREECKSVERTEILUNG mit a = min; b = max; c =modalwert
		int reproductionST;
		double a = 175;
		double b = 440;
		double c = 280;
		 
		double F = (c - a) / (b - a);
		double rand = Math.random();
	
			//Bestimmt ben�tigte Temperatursumme
			if (rand < F) {
			 reproductionST = (int) (a + Math.sqrt(rand * (b - a) * (c - a)));
			 } else {
			 reproductionST = (int) (b - Math.sqrt((1-rand) * (b - a) * (b - c)));
			 }
		this.inkubation = reproductionST;
		

	}
		// TODO Auto-generated constructor stub
	

	// ---------------------------------- Beginn des "t�glichen" Ablaufs
	// --------------------------------------------------------------------------\\

	public void start() {

		zaehler++; //z�hlt tage seit geburt


		
		
		if(zaehler <= 1){
			
			//isVisible = false;
			//System.out.println("hasInfected " + hasInfected);	
				
			
			geburt = Data.getZeit() - 1;

			Random ort = new Random();
			
			if (Data.getZeit() < 3){ //erste Infektionen (�berwintert) befinden sich auf f-6
				blatt = ort.nextInt(2) + 5;
				isVisible = true;
				//System.out.println("ich werde aufgerufen" + blatt);
			}else if (Data.getZeit() < Data.getEc31()){
				blatt = ort.nextInt(4)+3;   //nach Anzahl der Bl�tter welche laut Abb.223 vorhanden sind
												// hier mgl.: f-9(9), f-8(8), f-7(7), f-6(6), f-5(5), f-4(4), f-3 (3) 

			} else if (Data.getZeit() < Data.getEc32()){
				blatt = ort.nextInt(4) + 2;

			}else if (Data.getZeit() < Data.getEc37()){
				blatt = ort.nextInt(4) + 1;

			} else if (Data.getZeit() < Data.getEc47()){
				blatt = ort.nextInt(4);

			} else if (Data.getZeit() < Data.getEc59()){
				blatt = ort.nextInt(4);

			} else if (Data.getZeit() < Data.getEc71()){
				blatt = ort.nextInt(4);

			} else {
				blatt = ort.nextInt(4);
			}
			//leaf = blatt;
			/*if(blatt > leaf){
				blatt = leaf - ort.nextInt(2);
			}*/
		
		
		
				// Anzahl Weizenpflanzen im Umfeld des Pilzes detektieren
				GridPoint pt = grid.getLocation(this); // speichert Standort der Pest
		
				GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, // sucht Crops in Umgebung
						Crop.class, 1, 1);
				List<GridCell<Crop>> gridCells = nghCreator.getNeighborhood(true); // speichert alle Crops in
																					// Umgebung der Pest in einer Liste
	
				
				//int anzahlCrop = 0; // AnzahlCrops in Umgebung des Sch�dlings
				for (GridCell<Crop> cell : gridCells) { // summiert Liste mit Crops auf
					//anzahlCrop += cell.size();
					for(Crop crop : cell.items()){
						agenten.add(crop);
					}
				}
	
			
				// TODO: wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
				// Sch�dling fort  
				// wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
					//einem der crops in der umgebung gespeichert
				// wenn keine Crop vorhanden, dann stirbt Sch�dling ab
				// -------ERKL�RUNG NOCH GEBRAUCHT????????????????
	
			
			if (agenten.size() > 0){
				Random ag = new Random();
				a = ag.nextInt(agenten.size());        // wenn Crop vorhanden, Inkubationszeit nicht abgelaufen, dann wird pest in arraylist von 
				agenten.get(a).ST.add(this);             //einem der crops in der umgebung gespeichert
			
			} else {
				allocation();
			}
		}
		
		

		//ST �berpr�ft, ob Sie sich weiterentwickeln kann
		if (Data.getZeit() > Farmer.getInDaysST() | Farmer.getInDaysST() == 0) {    // erst wenn Wirkzeit Fungizid abgelaufen ist, kann sich Pilz weiter vermehren
			canInfect = true;  
		}else{
			canInfect = false;
		}
		
		//Latenzzeit des Pilzes ist Temperaturabh�ngig
		latenttime += (Data.getTemp() + 2.4);
		
		//Untere Septoria k�nnen nicht mehr zu neuen Infektionen f�hren (Annahme: Weg ist zu weit)
		if (Data.getZeit() >= Data.getEc37()){
			if (leaf > 4){
				canInfect = false;
			}
		} else if (Data.getZeit() >= Data.getEc32()){
			if(leaf > 5){
				canInfect = false;
			}
		}else if(Data.getZeit() >= Data.getEc31()){
			if (leaf > 6){
				canInfect = false;
			}
		}
		
		if(Data.getZeit() >= (geburt + 14)){
			if (latenttime > inkubation){                     // wenn Crops vorhanden und inkubationszeit abgelaufen ist, dann pflanzt sich
														// Sch�dling fort
				isVisible = true;                           //sobald Inkubationszeit abgelaufen ist, wird schadorg. sichtbar und farmer spritzt dementsprechend
	
				if(canInfect == true) { // Idee: vermehrung nur wenn z�hler gr��er als die protektive Wirkung				
					fortpflanzung1();
				}
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
			if (Farmer.getSchaedenprozST() < 90){//95)      //niedriger Resistenzstatus
				j = 0;
			} else {
				j = 85; 
			}

		} else if (resistenz == 2) {						//mittlerer Resistenzstatus
			if (Farmer.getSchaedenprozST() < 80) {
				j = 60; 
			} else {
				j = 90;
			}

		} else if (resistenz == 3) {						//hoher Resistenzstatus
			if (Farmer.getSchaedenprozST() < 80) {
				j = 80; 
			} else {
				j = 96;
			}

			// Falls falsche Zahl in GUI Oberfl�che eingegeben wird bricht die Simulation ab
		} else {
			throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
		}
		
		//Abfrage ob bereits Sporen ausgeso�en wurden
		int s; //gibt Anzahl auszusto�ender Sporen an
		if (hasInfected){
			s = 2;
		}else{
			s = 7;
		}

		// aus einem Sch�dling k�nnen max 6 neue entstehen, dh. am ende sind 6 da (wenn 1. Sporenaussot�), sonst weniger
		// mit oben festgelegter Wahrscheinlichkeit (j) sterben diese jedoch vor der
		// Geburt schon wieder ab
		// Wahrscheinlichkeit, dass Pest �berlebt h�ngt von Sorte ab
		// Pest stirbt in Modell gleich hier wieder ab (wenn von Crop aus aufgerufen
		// wird irgendeine Pest get�tet und nicht die neue! == nicht gewollt)
		for (int i = 0; i < s; i++) {
			Random tot = new Random();
			int wahrscheinlichkeit = tot.nextInt(99) + 1; // zuf�llige zahl zwischen 1 und 100 (entsprechen %)

			// Wenn die Zufallszahl h�her ist, als der oben festgelegte Wert f�r j, dann
			// �berlebt die neue Pest
			if (wahrscheinlichkeit > j) {


				// Erstelle zun�chst Spore, welche sich unter geeigneten Witterungsbed zu schadorg. wird
				
				NdPoint spacePt = space.getLocation(this);
				Context<Object> context = ContextUtils.getContext(this);

				SeptoriaSpore septoriaSpore = new SeptoriaSpore(space, grid, inkubation, resistenz, blatt);
				context.add(septoriaSpore);

				space.moveTo(septoriaSpore, spacePt.getX(), spacePt.getY());

				grid.moveTo(septoriaSpore, pt.getX(), pt.getY());

			}
		}
		hasInfected = true; //gibt an, dass ST bereits Sporen ausgesto�en hat; bei n�chsten mal werden weniger ausgesto�en
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
			//Eine der Crops in Umgebung wird ausgew�hlt
			// zuf�llig eine Crop aus der liste ausw�hlen
			int index = RandomHelper.nextIntFromTo(0, agents.size() - 1);
			Object wei = agents.get(index);
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
			
		} else {*/
			// Beliebige Crop auf dem Grid wird ausgew�hlt

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
				
				
				//Septoria noch dem ST Array zuordnen 
				((Crop) wei).ST.add(this);
			}
		} 
	}
//}

		/*if (agents.size() <= 0){

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
		}*/

	
	/*public void step() {

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
		
	
	}*/

	


