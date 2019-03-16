package cropPestModel;



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
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class PestSpore {
	
	private ContinuousSpace<Object> space; // Ort des Sch�dling
	private Grid<Object> grid; // Koordinaten des Sch�dling
	//parameter dienen dazu wissen von pest auf spore und dann wieder zur�ck auf pest zu �bertragen
	private int inkubation;    
	private int resistenz; 
	private int birth;
	private int leaf;
	private int zeit;
	
	private int zaehler = 0;
	private int blatt;
	
	
	public PestSpore(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrung,
			int resistance, int birth, int leaf, int zeit) {
		this.space = space;
		this.grid = grid;
		this.inkubation = vermehrung;
		this.resistenz = resistance;
		this.birth = birth;
		this.leaf = leaf;
		this.zeit = zeit;
		// TODO Auto-generated constructor stub
	}

	//@ScheduledMethod(start = 1, interval = 2)
	public void start() {
		
		
		
		if(zaehler <=1){

				// Anzahl Weizenpflanzen im Umfeld (21x21) des Sch�dlings detektieren
				// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)

				GridPoint pt = grid.getLocation(this);

				// Crops in der Nachbarschaft bestimmen
				GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, Crop.class, 8, 8); //alt 10,10
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
				
				/*/Blattzuweisung
				
				Random ort = new Random();
				if (Data.getZeit() < Data.getEc30()){
					blatt = 6;

				} else if (Data.getZeit() < Data.getEc31()){
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
				}*/
			
		}
		
		if (zaehler > 2){ 
			//2 f�r 2017
			//spore kann max. 7 tage �berleben
			//System.out.println("PestSpore.blatt" + leaf);
			sterbe();
		} else{
			if(Data.getTemp() >= 0 & Data.getTemp() <= 25 & Data.getRain() >= 1.5){
				if (Data.getTemp() >= 9 & Data.getTemp() <= 11){
					keimung();
				} else{
					Random keim = new Random();
					int i = keim.nextInt(3);
					if(i >=1){
						keimung();
					}
				}
			}
		zaehler ++;
		}
	}
	
	public void keimung(){


			
			//Ort der Spore bestimmen
			GridPoint pt = grid.getLocation(this); // speichert Standort der Pest
			NdPoint spacePt = space.getLocation(this);
			

			
			
			//Umwandlung einer Spore in einen Pilz
			//Spore entfernen
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			//Pilz hinzuf�gen
			Pest pest = new Pest(space, grid, inkubation, resistenz, birth, blatt, zeit);
			context.add(pest);

			space.moveTo(pest, spacePt.getX(), spacePt.getY());

			grid.moveTo(pest, pt.getX(), pt.getY());
		
		//System.out.println("Ich bin zu einem Pilz geworden");
		
	}
	
	public void sterbe() {
		//System.out.println("pestspore: Ich sterbe");
		
		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);

	}
}


