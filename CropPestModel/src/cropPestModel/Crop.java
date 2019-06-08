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
	
	public boolean fFourST;
	public boolean visibleSeptoria = false;
	public boolean yieldSeptoria = false;
	public boolean yieldSeptoriaNotVisible = false;
	public boolean allSeptoria = false;
	public boolean infestationFsixST = false;
	public boolean visibleGR = false;
	public boolean yieldGR = false;
	public boolean allGelbrost = false;
	public boolean FtoF2GR = false;
	public boolean f5 = false;
	public boolean f4 = false;
	public boolean f3 = false;
	public boolean f2 = false;
	public boolean f1 = false;
	public boolean f0 = false;

	
	
	
	private int schaedlingsAnzahl; // Anzahl an GR an einer Pflanze
	private int septoriaAnzahl;   //Anzahl ST an einer Pflanze
	
	private int a;     //Hilfe um Ertragsmindernde Pilzsporen zu ermitteln
	
	private int visibleInfestationST; //ist 1 sobald Crop von sichtbarem ST befallen
	//private int befallsichtbarGR; //ist 1 sobald Crop von sichtbarem GR befallen
	
	//private int visibleInfestationFSixST;
	
	private int befallGREr; //sichtbarer ertragsrelevanter Befall (entspricht bonituren in Feldversuchen..)
	private int befallSTEr;
	
	//private int befallbFbF2;       // GR auf f-2 bis f
	private int infestationYieldST;
	private int infestationVisibleST;
	
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
	
	private int GRsichtCount1; 
	private int GRertragCount1;
	private int STsichtCount1;
	private int STertragCount1; 
	private int GRCount1;
	private int STCount1;
	
	private int GRsichtCount2; 
	private int GRertragCount2;
	private int STsichtCount2;
	private int STertragCount2; 
	private int GRCount2;
	private int STCount2;

	
	List<Pest> GR = new ArrayList<Pest>();
	
	
	List<Septoria> ST = new ArrayList<Septoria>();

	
	



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
	

	
	public int getInfestationYieldST() {
		return infestationYieldST;	
	}
	public int getInfestationVisibleST() {
		return infestationVisibleST;	
	}
	
	public int getAnzahlSTsicht(){
		return visibleInfestationST;
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
	
	public int getGRsichtCount1(){
		return GRsichtCount1;
	}
	public int getGRertragCount1(){
		return GRertragCount1; 
	}
	public int getSTsichtCount1(){
		return STsichtCount1;
	}
	public int getSTertragCount1(){
		return STertragCount1;
	}
	public int getGRCount1(){
		return GRCount1;
	}
	public int getSTCount1(){
		return STCount1;
	}
	
	public int getGRsichtCount2(){
		return GRsichtCount2;
	}
	public int getGRertragCount2(){
		return GRertragCount2; 
	}
	public int getSTsichtCount2(){
		return STsichtCount2;
	}
	public int getSTertragCount2(){
		return STertragCount2;
	}
	public int getGRCount2(){
		return GRCount2;
	}
	public int getSTCount2(){
		return STCount2;
	}



	// ------------------------------------- Konstruktor für Crop
	// ---------------------------------------------------------------------------\\

	// Daten werden von CropPestModelBuilder auf einzelne Crop übertragen



	public Crop(ContinuousSpace<Object> space, Grid<Object> grid, double ertragspot) {
		// TODO Auto-generated constructor stub
		this.space = space;
		this.grid = grid;
		this.ertragspot = ertragspot;
		this.fFourST = false;

	}

	// ------------------------------------ Beginn des "täglichen" Ablaufs
	// ----------------------------------------------------------------------\\

	public void start() {
		//sicht.clear();

		//Werte null setzen, damit in diesem tick aktueller wert dafür eingesetzt wird
		
		befallGRE = 0; 
		befallFGR = 0; 
		befallSTE = 0; 
		befallFST = 0;
		befallGREr = 0;
		befallSTEr = 0;
		infestationYieldST = 0;
		
		
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
		
		//Booleans auf false setzen, damit neu detektiert werden kann, ob entsprechende Pathogene vorhanden
		allSeptoria = false;
		visibleSeptoria = false;
		yieldSeptoria = false;
		yieldSeptoriaNotVisible = false;
		infestationFsixST = false;
		
		
		allGelbrost = false;
		visibleGR = false;
		yieldGR = false;
		FtoF2GR = false;
		
		
		

		//----Ermitteln der zu Crop gehörenden Pest/GR-------------
		//---------------------------------------------------
			

		int befallFahnenblatt = 0;
		int befallGR = 0;
		int befallGRErtrag = 0;
		int allGR = 0;
		int befallGRsicht = 0;

		
		for (Pest pest : GR){
			if(pest.isAlive == true & pest.isInactive == false){
				allGelbrost = true;
				allGR += 1;
				
				if(pest.getLeaf() <= a){             
					befallGR++;
				}
				
				if (pest.getLeaf() <= 0){		
					// TODO: noch benötigt??
					befallFahnenblatt++;	
				}
				if (pest.isVisible == true){
					befallGRsicht += 1;
					visibleGR = true;
					if(pest.getLeaf() <= a){
						befallGRErtrag++;
						yieldGR = true;
					}
					if(pest.getLeaf() <= 2){
						if (pest.getBirthVisible() > Farmer.getDayFungicideApplication()){
							FtoF2GR = true;
						}
					}
					
					//für Graphen
					if(pest.getLeaf() == 5){
						f5 = true;
					}
					if(pest.getLeaf() == 4){
						f4 = true;
					}
					if(pest.getLeaf() == 3){
						f3 = true;
					}
					if(pest.getLeaf() == 2){
						f2 = true;
					}
					if(pest.getLeaf() == 1){
						f1 = true;
					}
					if(pest.getLeaf() == 0){
						f0 = true;
					}
				}
			}
		}
		


		//DIESE BEEIFLUSSEN MOMENTAN DEN ERTRAG (WIRKLICH) vgl. ERtragsfkt.
		schaedlingsAnzahl = allGR; // Schädlingsanzahl(gesamt) auf private variable übertragen
		befallGRE = befallGR;            //Schädlingsanzahl die ertrag beeinflusst
		befallFGR = befallFahnenblatt;   //Schädlinge auf Fahnenblatt
		
	
	
		
		
	
		
		// -------------------------------- Anzahl ST in Umfeld der Pflanze
		// ----------------------------------------------------------------------\\


		int befallFahnenblattST = 0;
		int befallST = 0;
		int befallSTErtrag = 0;
		int befallSTErtragNichtSichtbar = 0;
		int allST = 0;
		int visibleST = 0;

		List<Septoria> fSixST = new ArrayList<Septoria>();
		
		//alle ST
		for (Septoria septoria : ST){
			if(septoria.isAlive == true & septoria.isInactive == false){
				allST += 1;
				allSeptoria = true;
				
				
				if(septoria.getLeaf() <= (a+1)){             
					befallST++;
					yieldSeptoriaNotVisible = true;
				}
				

					/*TODO: noch benötigt???if (septoria.getLeaf() <= 0){
						//System.out.println("Halloooooo");
						befallFahnenblattST++;	
					}*/
				if (septoria.isVisible == true){
					visibleST += 1;
					visibleSeptoria = true;
					if(septoria.getLeaf() <= a){
						befallSTErtrag++;
						yieldSeptoria = true;
					}
					if(septoria.getLeaf() == 6){
						infestationFsixST = true;

					}
					
					
				}
					//visibleSeptoria = false;
				
			}else {
				//allSeptoria = false;
			}
		}
		
		

		septoriaAnzahl = allST; // Schädlingsanzahl(gesamt) auf private variable übertragen
		befallSTE = befallST;            //Schädlingsanzahl die ertrag beeinflusst !!ACHTUNG!!STIMMT NICHT MEHR = ERTRAG + 1Blatt
		befallFST = befallFahnenblattST;   //Schädlinge auf Fahnenblatt
		infestationYieldST = befallSTErtrag; //zur Übersicht während Kalibrierung genutzt
		infestationVisibleST = visibleST;
		
		
		//----FÜR AUSWERTUNG: Ermittleln der Anzahl an Pathogenen im jew. Stadium der Simulation
		//-----------------------------------------
	
		if(Data.getZeit() > Data.getEc30()){
		GRsichtCount += befallGRsicht;
		GRertragCount += befallGRErtrag;
		STsichtCount += visibleST;
		STertragCount += befallSTErtrag;
		GRCount += allGR;
		STCount += allST;
		if (Data.getZeit() < Data.getEc61()){
			GRsichtCount1 += befallGRsicht;
			GRertragCount1 += befallGRErtrag;
			STsichtCount1 += visibleST;
			STertragCount1 += befallSTErtrag;
			GRCount1 += allGR;
			STCount1 += allST;
		} else {
			GRsichtCount2 += befallGRsicht;
			GRertragCount2 += befallGRErtrag;
			STsichtCount2 += visibleST;
			STertragCount2 += befallSTErtrag;
			GRCount2 += allGR;
			STCount2 += allST;
		}
		}
		
		
		
		
		
		
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
