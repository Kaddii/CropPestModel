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
	
	private ContinuousSpace<Object> space; // Ort des Schädling
	private Grid<Object> grid; // Koordinaten des Schädling
	//parameter dienen dazu wissen von pest auf spore und dann wieder zurück auf pest zu übertragen
	private int inkubation;    
	private int resistenz; 
	private int birth;
	private int l;
	public boolean isAlive;

	
	private int zaehler = 0;

	
	
	public PestSpore(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrung,
			int resistance) {
		this.space = space;
		this.grid = grid;
		this.inkubation = vermehrung;
		this.resistenz = resistance;
		this.isAlive = true;
		// TODO Auto-generated constructor stub
	}
	


	
	public void start() {
		zaehler++;
		
		if(zaehler <=1){
			// Anzahl Weizenpflanzen im Umfeld (21x21) des Schädlings detektieren
			// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)

			GridPoint pt = grid.getLocation(this);

			// Crops in der Nachbarschaft bestimmen
			GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, Crop.class, 10, 10); //alt 7,7
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
			
			/*/Überlebensdauer der Sporen wird zufällig festgelegt
			Random life = new Random();
			l = life.nextInt(8);*/
		}
		
		
		
		if (zaehler > 2){ 
			//2 für 2017
			//spore kann max. 7 tage überleben
			//System.out.println("PestSpore.blatt" + leaf);
			die();
		} else{
			//Keimung ist Temperaturabhängig
			if(Data.getTemp() >= 0 & Data.getTemp() <= 25 & Data.getRain() >= 1.5){
				if (Data.getTemp() >= 9 & Data.getTemp() <= 11){
					keimung();
				} else{
					//außerhalb der optimalen Keimtemperatur kommt es nur zufällig zur Keimung
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
		//Pilz hinzufügen
		Pest pest = new Pest(space, grid, inkubation, resistenz);
		context.add(pest);

		space.moveTo(pest, spacePt.getX(), spacePt.getY());

		grid.moveTo(pest, pt.getX(), pt.getY());
		
		
	}
	
	public void die() {
	
		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);
		isAlive = false;
	}
}


