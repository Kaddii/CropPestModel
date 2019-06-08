package cropPestModel;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class SeptoriaSpore {

	private ContinuousSpace<Object> space; // Ort des Schädling
	private Grid<Object> grid; // Koordinaten des Schädling
	private int inkubation;
	private int resistenz;
	private int birth;
	private int leaf;
	private boolean sichtbar;
	private int zeit;
	
	private int zaehler;
	
	public boolean isAlive;


	public SeptoriaSpore(ContinuousSpace<Object> space, Grid<Object> grid, int vermehrungST, int resistance, int leaf) {
		this.leaf = leaf;
		this.space = space;
		this.grid = grid;
		this.inkubation = vermehrungST;
		this.resistenz = resistance;
		this.isAlive = true;
	}
	
	public void start() {
		
		if(zaehler <=1){

			// Anzahl Weizenpflanzen im Umfeld (21x21) des Schädlings detektieren
			// (schaut 10 zellen nach links und 10 nach rechts, ausgehend von seiner)

			GridPoint pt = grid.getLocation(this);

			// Crops in der Nachbarschaft bestimmen
			GridCellNgh<Crop> nghCreator = new GridCellNgh<Crop>(grid, pt, Crop.class, 13, 13); //alt 10,10
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
		//System.out.println("stspore");
		
		if (zaehler > 2){
			die();
		} else{
			keimung();
		}
		zaehler ++;
	}
	
	
	
	public void keimung(){
		//System.out.println("stspore keimt?");
		if(Data.getTemp() >= 2 & Data.getTemp() <= 30 & Data.getHumidity() >= 75){
			//System.out.println("stspore keimt? - JAAAAAAAAAAAAAAAAAAAAAAA");
			//Ort der Spore bestimmen
			GridPoint pt = grid.getLocation(this); // speichert Standort der Pest
			NdPoint spacePt = space.getLocation(this);
			
			
			//Umwandlung einer Spore in einen Pilz
			//Spore entfernen
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			//Pilz hinzufügen
			Septoria septoria = new Septoria(space, grid, resistenz, leaf);
			context.add(septoria);

			space.moveTo(septoria, spacePt.getX(), spacePt.getY());

			grid.moveTo(septoria, pt.getX(), pt.getY());
		
		//System.out.println("Ich bin zu einem Pilz geworden");
		}
	}
	
	public void die() {
		//System.out.println("septoriaspore: Ich sterbe");
		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);
		isAlive = false;

	}
}




