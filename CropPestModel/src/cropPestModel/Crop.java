package cropPestModel;

/**

/**
 * 
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Object;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

/**
 * @author Uli
 *
 */
public class Crop {
	private ContinuousSpace<Object> space; // Ort der Pflanze
	private Grid<Object> grid; // Koordinaten der Pflanze
	private double ertragspot; // Ertragspotential der Pflanze zu entsprechendem tick

	
	private int anzahlGR; // ist 1 sobald Crop von Schädling befallen wird (Info für Farmer)
	private int anzahlST; //s. oben
	
	private int schaedlingsAnzahl; // Anzahl an GR an einer Pflanze
	private int septoriaAnzahl;   //Anzahl ST an einer Pflanze
	
	private int a;     //Hilfe um Ertragsmindernde Pilzsporen zu ermitteln
	
	private int visibleInfestationST; //ist 1 sobald Crop von sichtbarem ST befallen
	private int befallsichtbarGR; //ist 1 sobald Crop von sichtbarem GR befallen
	
	private int visibleInfestationFSixST;
	
	private int befallGREr; //sichtbarer ertragsrelevanter Befall (entspricht bonituren in Feldversuchen..)
	private int befallSTEr;
	
	private int befallbFbF2;       // GR auf f-2 bis f
	
	private int befallGRE; //Ertragsrelevanter Befall Gelbrost
	private int befallFGR; //Befall Fahnenblatt Gelbrost
	private int befallSTE; //Ertragsrelevanter Befall Septoria
	private int befallFST; //Befall Fahnenblatt Septoria 
	
	private int GRsichtCount; //Bilden der Integrale für Ertragsabschätzung
	private int GRertragCount;  //s.o.
	private int STsichtCount;
	private int STertragCount;
	private int GRCount; 
	private int STCount;
	private int sichtb;
	
	List<Pest> GR = new ArrayList<Pest>();
	//List<Pest> gelbS = new ArrayList<Pest>();
	//List<Pest> gelbfbf2 = new ArrayList<Pest>();
	
	List<Septoria> ST = new ArrayList<Septoria>();
	//List<Septoria> septS = new ArrayList<Septoria>();
	//List<Septoria> septfSechs = new ArrayList<Septoria>();
	
	
	//List<Pest> sicht = new ArrayList<Pest>();


	// ------------------------------------------- Aufrufe
	// --------------------------------------------------------------------------------\\

	public double getErtrag() {
		return ertragspot;
	}

	public int getAnzahlGR() {
		return anzahlGR;
	}
	
	public int getAnzahlST() {
		return anzahlST;
	}
	
	public int getAnzahlGRsicht() {
		return befallsichtbarGR;
	}
	
	public int getAnzahlGRbFbF2() {
		return befallbFbF2;
	}
	
	public int getAnzahlSTsicht(){
		return visibleInfestationST;
	}
	
	public int getAnzahlSTfSechs(){
		return visibleInfestationFSixST;
	}

	
	public int getGelbrostAnzahl() {
		return schaedlingsAnzahl;
	}
	
	
	public int getSeptoriaAnzahl() {
		return septoriaAnzahl;
	}
	
	public int getBefallFGR(){
		return befallFGR;
	}
	
	public int getBefallGREr(){
		return befallGREr;
	}
	
	public int getBefallSTEr(){
		return befallSTEr;
	}
	
	public int getGRsichtCount(){
		return GRsichtCount;
	}
	public int getGRertragCount(){
		return GRertragCount; 
	}
	public int getSTsichtCount(){
		return STsichtCount;
	}
	public int getSTertragCount(){
		return STertragCount;
	}
	public int getGRCount(){
		return GRCount;
	}
	public int getSTCount(){
		return STCount;
	}



	// ------------------------------------- Konstruktor für Crop
	// ---------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Crop übertragen



	public Crop(ContinuousSpace<Object> space, Grid<Object> grid, double ertragspot) {
		// TODO Auto-generated constructor stub
		this.space = space;
		this.grid = grid;
		this.ertragspot = ertragspot;

	}

	// ------------------------------------ Beginn des "täglichen" Ablaufs
	// ----------------------------------------------------------------------\\
	//@ScheduledMethod(start = 1, interval = 2)
	public void start() {
		//sicht.clear();

		//Werte null setzen, damit in diesem tick aktueller wert dafür eingesetzt wird
		befallsichtbarGR = 0;
		befallbFbF2 = 0;
		visibleInfestationST = 0; 
		visibleInfestationFSixST = 0;
		befallGRE = 0; 
		befallFGR = 0; 
		befallSTE = 0; 
		befallFST = 0;
		befallGREr = 0;
		befallSTEr = 0;

		
		
		//------------------------------- Momentanes Ec Stadium ermitteln und dementsprechend Ertragsmindernde Blätter----------------
		
		if (Data.getZeit() < Data.getEc30()){
			a = 12;
		} else if (Data.getZeit() < Data.getEc31()){
			a = 5; //f-5
		} else if (Data.getZeit() < Data.getEc32()){
			a = 4; //f-4
		} else if (Data.getZeit() < Data.getEc37()){
			a = 3; //f-3
		} else {
			a = 2; //f-2
		}
		

		//----Ermitteln der zu Crop gehörenden Pest/GR-------------
		//---------------------------------------------------
			

		int befallSichtbar = 0;
		int befallFahnenblatt = 0;
		int befallGR = 0;
		int befallGRErtrag = 0;
		List<Integer> bef = new ArrayList<Integer>();
		//List<Integer> b = new ArrayList<Integer>();
		List<Pest> visibleGR = new ArrayList<Pest>();
		List<Pest> allGR = new ArrayList<Pest>();
		List<Pest> fTof2GR = new ArrayList<Pest>();
		
		for (Pest pest : GR){
			if(pest.isAlive == true){
				allGR.add(pest);
				
				if(pest.getLeaf() <= a){             
					befallGR++;
				}
				if(pest.getLeaf() <= 2){
					fTof2GR.add(pest);
				}
				if (pest.getLeaf() <= 0){		
					// TODO: noch benötigt??
					befallFahnenblatt++;	
				}
				if (pest.isVisible == true){
					visibleGR.add(pest);
					if(pest.getLeaf() <= a){
						befallGRErtrag++;
					}
				}
			}
		}
		

		
		// zählt, wenn Schädling an Crop ist
		// damit kann Farmer die Anzahl der befallenen Haupttriebe ermitteln
	

		if (allGR.size() > 0) {
			anzahlGR = 1;
			
		} else {
			anzahlGR = 0;
		}
		if (visibleGR.size() > 0) {
			befallsichtbarGR = 1;	
		}else{
			befallsichtbarGR = 0;
		}
		
		if (fTof2GR.size() > 0) {
			befallbFbF2 = 1;
		}else {
			befallbFbF2 = 0;
		}
		if (befallGRErtrag > 0){
			befallGREr = 1;
		} else {
			befallGREr = 0;
		}

		
		schaedlingsAnzahl = allGR.size(); // Schädlingsanzahl(gesamt) auf private variable übertragen
		befallGRE = befallGR;            //Schädlingsanzahl die ertrag beeinflusst
		befallFGR = befallFahnenblatt;   //Schädlinge auf Fahnenblatt
	
		
		
	
		
		// -------------------------------- Anzahl ST in Umfeld der Pflanze
		// ----------------------------------------------------------------------\\


		int befallFahnenblattST = 0;
		int befallST = 0;
		int befallSTErtrag = 0;
		List<Septoria> allST = new ArrayList<Septoria>();
		List<Septoria> fSixST = new ArrayList<Septoria>();
		List<Septoria> visibleST = new ArrayList<Septoria>();
		
		//alle ST
		for (Septoria septoria : ST){
			if(septoria.isAlive == true){
				allST.add(septoria);
				
				if(septoria.getLeaf() <= a){             
					befallST++;
				}
				if(septoria.getLeaf() == 6){
					fSixST.add(septoria);
				}
					/*TODO: noch benötigt???if (septoria.getLeaf() <= 0){
						//System.out.println("Halloooooo");
						befallFahnenblattST++;	
					}*/
				if (septoria.isVisible == true){
					visibleST.add(septoria);
					if(septoria.getLeaf() <= a){
						befallSTErtrag++;
					}
				}
			}
		}
		

		// zählt, wenn Schädling an Crop ist
		// damit kann Farmer die Anzahl der befallenen Haupttriebe ermitteln

		if (allST.size() > 0) {
			//System.out.println(gelb.toString());
			anzahlST = 1;
			
		}else {
			anzahlST = 0;
		}
		
		if (visibleST.size() > 0) {
			visibleInfestationST = 1;
		} else {
			visibleInfestationST = 0;
		}
		
		if (fSixST.size() > 0) {
			visibleInfestationFSixST = 1;
		} else {
			visibleInfestationFSixST = 0;
		}
		
		if (befallSTErtrag > 0){
			befallSTEr = 1;
		} else {
			befallSTEr = 0;
		}

		septoriaAnzahl = allST.size(); // Schädlingsanzahl(gesamt) auf private variable übertragen
		befallSTE = befallST;            //Schädlingsanzahl die ertrag beeinflusst
		befallFST = befallFahnenblattST;   //Schädlinge auf Fahnenblatt
		
		//----FÜR AUSWERTUNG: Ermittleln der Anzahl an Pathogenen im jew. Stadium der Simulation
		//-----------------------------------------
	
		GRsichtCount += visibleGR.size();
		GRertragCount += befallGRErtrag;
		STsichtCount += visibleST.size();
		STertragCount += befallSTErtrag;
		GRCount += allGR.size();
		STCount += allST.size();
		
		
		
		
		
		
	//---------------------Veränderung ERTRAGSPOTENTIAL----------------------------------------
	//--------------------------------------------------------
		
		if (Data.getZeit() >= Data.getEc30() && Data.getZeit() < Data.getEc37()) {

			ertragspot = ertragspot - ((befallGRE + befallSTE) * 0.23);
				//bei zu hoher Anzahl an Schädlingen eine feste Abhnahme einfügen (s. "Anhang")

			if (ertragspot < 0) {
				ertragspot = 0;
			}
		} else if (Data.getZeit() >= Data.getEc37() && Data.getZeit() <= Data.getEc61()){
			ertragspot = ertragspot - ((befallGRE * 0.3) + (befallFGR * 0.7) * 0.23) - ((befallSTE * 0.3) + (befallFST * 0.7) * 0.23);
			
			//bei zu hoher Anzahl an Schädlingen eine feste Abhnahme einfügen (s. "Anhang")
			//das gleiche bei zu hoher Anzahl an Pilzbefall auf Fahnenblatt

		} else if (Data.getZeit() > Data.getEc61()) {               //Nach Blüte nimmt Einfluss von Schädlingen zu??????????????!!!
				                                               // Fehlt noch!!!!
				
			ertragspot = ertragspot - ((befallGRE * 0.3) + (befallFGR * 0.7) * 0.23) - ((befallSTE * 0.3) + (befallFST * 0.7) * 0.23);

			if (ertragspot < 0) { 
				ertragspot = 0;
			}

		}
		
		// absterben der Weizenpflanze wenn Ertragspotential 0 ist
		if (ertragspot == 0) {
			Context<Object> context = ContextUtils.getContext(this); // PFLANZE KANN NUR BIS ZUR BLÜTE ABSTERBEN
			context.remove(this);
			//alle Pests & ST an Crop sterben auch ab
			for(Pest pest : GR){
				if(pest.isAlive == true){
				pest.die();
				}
			}
			for(Septoria septoria : ST){
				if(septoria.isAlive == true){
					septoria.die();
				}
			}
		}
		

	}
	

		
	
}
