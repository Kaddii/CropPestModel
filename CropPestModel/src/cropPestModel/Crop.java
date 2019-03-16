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
	private int zeit; // gibt den tick an (1 tick = 1 Tag)
	
	private int anzahlGR; // ist 1 sobald Crop von Schädling befallen wird (Info für Farmer)
	private int anzahlST; //s. oben
	
	private int schaedlingsAnzahl; // Anzahl an GR an einer Pflanze
	private int septoriaAnzahl;   //Anzahl ST an einer Pflanze
	
	private int a;     //Hilfe um Ertragsmindernde Pilzsporen zu ermitteln
	
	private int befallsichtbarST; //ist 1 sobald Crop von sichtbarem ST befallen
	private int befallsichtbarGR; //ist 1 sobald Crop von sichtbarem GR befallen
	
	private int befallfSechsST;
	
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
	
	List<Pest> gelb = new ArrayList<Pest>();
	List<Pest> gelbS = new ArrayList<Pest>();
	List<Pest> gelbfbf2 = new ArrayList<Pest>();
	
	List<Septoria> sept = new ArrayList<Septoria>();
	List<Septoria> septS = new ArrayList<Septoria>();
	List<Septoria> septfSechs = new ArrayList<Septoria>();
	
	
	List<Pest> sicht = new ArrayList<Pest>();


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
		return befallsichtbarST;
	}
	
	public int getAnzahlSTfSechs(){
		return befallfSechsST;
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



	public Crop(ContinuousSpace<Object> space, Grid<Object> grid, double ertragspot, int zeit) {
		// TODO Auto-generated constructor stub
		this.space = space;
		this.grid = grid;
		this.ertragspot = ertragspot;
		this.zeit = zeit;
	}

	// ------------------------------------ Beginn des "täglichen" Ablaufs
	// ----------------------------------------------------------------------\\
	//@ScheduledMethod(start = 1, interval = 2)
	public void befall() {
		sicht.clear();
		
		zeit++;
		//Werte null setzen, damit in diesem tick aktueller wert dafür eingesetzt wird
		befallsichtbarGR = 0;
		befallbFbF2 = 0;
		befallsichtbarST = 0; 
		befallfSechsST = 0;
		befallGRE = 0; 
		befallFGR = 0; 
		befallSTE = 0; 
		befallFST = 0;
		befallGREr = 0;
		befallSTEr = 0;
		//System.out.println("Crop");
		
		
		//------------------------------- Momentanes Ec Stadium ermitteln und dementsprechend Ertragsmindernde Blätter----------------
		
		if (zeit < Data.getEc30()){
			a = 12;
		} else if (zeit < Data.getEc31()){
			a = 5; //f-5
		} else if (zeit < Data.getEc32()){
			a = 4; //f-4
		} else if (zeit < Data.getEc37()){
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
		List<Integer> b = new ArrayList<Integer>();

		
		for (Pest pest : gelb){
			//System.out.println("Crop.getLeaf   " + pest.getLeaf() + "   Crop.getGeburt" + pest.getBirth());
			if(pest.getLeaf() <= a){             
				//System.out.println("Halloooooo"); 
				befallGR++;
				b.add(pest.getLeaf());
				
			}
			if (pest.getLeaf() <= 0){
				//System.out.println("Halloooooo");
				befallFahnenblatt++;	
			}
			/*if(pest.getSichtbar() == true){
				sicht.add(pest);
			}
			/*if(Data.getZeit() == 100){
				bef.add(pest.getLeaf());
			}*/
			/*if (pest.getSichtbar() == true){ //LÖSCHEN???? (+ bei ST auch??)
				befallSichtbar++;
				//System.out.println("befallsichtbar  " + befallSichtbar);
			}*/
		}
		
		/*sichtb = sicht.size();
		if(sicht.size() > 0){
		System.out.println("scihtbar" + sicht.size());
		}*/
		/*if(b.size() > 0) {
		System.out.println("befallertrag" + b.toString());
		}*/
		/*if(zeit == 100){
		System.out.println("leaf  " + bef.toString());
		}*/
		
		
		for(Pest pest : gelbS){
			bef.add(pest.getLeaf());
			if(pest.getLeaf() <= a){
				befallGRErtrag++;
				
			}
		}
		/*if(gelbS.size() > 0){
			System.out.println("gelbs.size" + bef.toString());
			}*/
		/*if(bef.size() > 0){
		System.out.println("befallsicht" + bef.toString());
		}
		bef.clear();*/
		
		
		
		
		// zählt, wenn Schädling an Crop ist
		// damit kann Farmer die Anzahl der befallenen Haupttriebe ermitteln
	

		if (gelb.size() > 0) {
			//System.out.println(gelb.toString());
			anzahlGR = 1;
			
		}
		if (gelbS.size() > 0) {
			befallsichtbarGR = 1;
			/*for(Pest pest : gelbS){
				b.add(pest.getLeaf());
			}*/
			
		}else{
			befallsichtbarGR = 0;
		}
		
		if (gelbfbf2.size() >0) {
			befallbFbF2 = 1;
		}
		if (befallGRErtrag > 0){
			befallGREr = 1;
			//System.out.println("befallErtraganz " + befallGREr);
			//System.out.println("befallErtrag " + befallGRErtrag);
		}
		/*if (befallSichtbar > 0) {
			befallsichtbarGR = 1;
			//System.out.println("Sichtbare GR" + befallsichtbarGR);
		}*/
		//System.out.println(befallGRErtrag + "  BefallGRErtrag(sicht  +  BefallGRE " + befallGR);
		
		schaedlingsAnzahl = gelb.size(); // Schädlingsanzahl(gesamt) auf private variable übertragen
		befallGRE = befallGR;            //Schädlingsanzahl die ertrag beeinflusst
		befallFGR = befallFahnenblatt;   //Schädlinge auf Fahnenblatt
		if(gelb.size() > 0){
		//System.out.println("Befallsichtbar  " + befallsichtbarGR);
		}
		

		
		
		
		
	
		
		// -------------------------------- Anzahl ST in Umfeld der Pflanze
		// ----------------------------------------------------------------------\\

		int befallSichtbarST = 0;
		int befallFahnenblattST = 0;
		int befallST = 0;
		int befallSTErtrag = 0;
		
		//alle ST
		for (Septoria septoria : sept){
			//System.out.println("Crop.getLeaf   " + pest.getLeaf() + "   Crop.getGeburt" + pest.getBirth());
			if(septoria.getLeaf() <= a){             
				//System.out.println("Halloooooo"); 
				befallST++;
			}
			if (septoria.getLeaf() <= 0){
				//System.out.println("Halloooooo");
				befallFahnenblattST++;	
			}
			/*if (septoria.getSichtbar() == true){
				befallSichtbarST++;
				//System.out.println("befallsichtbar  " + befallSichtbar);
			}*/
		}
		
		//List<Integer> befallster = new ArrayList<Integer>();
		
		//Auswertung der sichtbaren ST
		for(Septoria septoria : septS){
			if(septoria.getLeaf() <= a){
				befallSTErtrag++;
				//befallster.add(septoria.getLeaf());
			}
		}
		
		
		/*if(befallster.size() > 0){
		System.out.println("befallSTertrag" + befallster.toString());
		}*/
		
		
		
		// zählt, wenn Schädling an Crop ist
		// damit kann Farmer die Anzahl der befallenen Haupttriebe ermitteln

		if (sept.size() > 0) {
			//System.out.println(gelb.toString());
			anzahlST = 1;
			
		}
		if (septS.size() > 0) {
			befallsichtbarST = 1;
		}
		
		if (septfSechs.size() > 0) {
			befallfSechsST = 1;
		}
		
		if (befallSTErtrag > 0){
			befallSTEr = 1;
			//System.out.println("befallErtraganz " + befallSTEr);
			//System.out.println("befallErtrag " + befallGRErtrag);
		}

		septoriaAnzahl = sept.size(); // Schädlingsanzahl(gesamt) auf private variable übertragen
		befallSTE = befallST;            //Schädlingsanzahl die ertrag beeinflusst
		befallFST = befallFahnenblattST;   //Schädlinge auf Fahnenblatt
		
		//----FÜR AUSWERTUNG: Ermittleln der Anzahl an Pathogenen im jew. Stadium der Simulation
		//-----------------------------------------
	
		GRsichtCount = gelbS.size();
		GRertragCount = befallGRErtrag;
		STsichtCount += septS.size();
		STertragCount = befallSTErtrag;
		GRCount = gelb.size();
		STCount += sept.size();
		
		
		
		
		
		
	//---------------------Veränderung ERTRAGSPOTENTIAL----------------------------------------
	//--------------------------------------------------------
		
		if (zeit >= Data.getEc30() && zeit < Data.getEc37()) {

			ertragspot = ertragspot - ((befallGRE + befallSTE) * 0.23);
				//bei zu hoher Anzahl an Schädlingen eine feste Abhnahme einfügen (s. "Anhang")

			if (ertragspot < 0) {
				ertragspot = 0;
			}
		} else if (zeit >= Data.getEc37() && zeit <= Data.getEc61()){
			ertragspot = ertragspot - ((befallGRE * 0.3) + (befallFGR * 0.7) * 0.23) - ((befallSTE * 0.3) + (befallFST * 0.7) * 0.23);
			
			//bei zu hoher Anzahl an Schädlingen eine feste Abhnahme einfügen (s. "Anhang")
			//das gleiche bei zu hoher Anzahl an Pilzbefall auf Fahnenblatt

		} else if (zeit > Data.getEc61()) {               //Nach Blüte nimmt Einfluss von Schädlingen zu??????????????!!!
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
			/*/alle Pests an Crop sterben auch ab!!!!!!!!!!!!!!!!FKTNICHT!!!!!!!!!!!!!!!!!
			for(Pest pest : gelb){
				pest.sterbe();
			}*/
		}
		

	}
	

		
	
}


//------------------- nach Fertigstellung löschen -------------------//

/*public void ertragswirkung() {
	
	if (zeit >= Data.getEc30() && zeit < Data.getEc37()) {

		if (befall <= 10) {
			ertragspot = ertragspot - (befall * 0.23);
		} else if (befall > 10) {
			ertragspot = ertragspot - (10 * 0.07); // weshalb wurde hier darauf verzichtet, schaedlingsAnzahl zu verwenden?
		}

		if (ertragspot < 0) {
			ertragspot = 0;
		}
	} else if (zeit >= Data.getEc37() && zeit < Data.getEc61()){
		if (befall <= 10) { // Wann stirbt Schädling bzw. blatt von Schädling ab (Altersgründe?)
			ertragspot = ertragspot - (((befall * 0.3) + (befallF * 0.7)) * 0.23);
		} else if (schaedlingsAnzahl > 10) {
			int b = 10;
			if (befallF < 10) {
				b = befallF;
			}
			ertragspot = ertragspot - (((10 * 0.3) + (b * 0.7)) * 0.07);

		} else {
			ertragspot = ertragspot - ((befall * 0.3) + (befallF * 0.7)) * 0.07;
		}
		if (ertragspot < 0) { 
			ertragspot = 0;
		}

		// Nach der Blüte ist Einfluss von Schädling auf Ertrag größer!! NOCH EINBAUEN!!!!!
	} else if (zeit >= Data.getEc61()) {

		if (befall <= 10) { 
			ertragspot = ertragspot - (((befall * 0.3) + (befallF * 0.7)) * 0.23);
		} else if (schaedlingsAnzahl > 10) {
			int b = 10;
			if (befallF < 10) {
				b = befallF;
			}
			ertragspot = ertragspot - (((10 * 0.3) + (b * 0.7)) * 0.07);

		} else {
			ertragspot = ertragspot - ((befall * 0.3) + (befallF * 0.7)) * 0.07;
		}
		if (ertragspot < 0) { // AB WANN KEINE WECHSELWIRKUNG PFLANZE/SCHÄDLING MEHR(GELB)????
			ertragspot = 0;
		}


	}
	// absterben der Weizenpflanze wenn Ertragspotential 0 ist
	if (ertragspot == 0) {
		Context<Object> context = ContextUtils.getContext(this); // PFLANZE KANN NUR BIS ZUR BLÜTE ABSTERBEN
		context.remove(this);
	}
}
}*/


/*/ ermitteln ob Pests nach Spitzen des Fahnenblattes entstanden sind, dazu
// werden:

// Pests in Umgebung der Crop in ArrayList speichern
List<Pest> pests = new ArrayList<Pest>();
for (GridCell<Pest> cell : gridCells) {
	if (cell.size() > 0) {
		Iterator<Pest> iterator = cell.items().iterator();
		while (iterator.hasNext())
			pests.add(iterator.next());
	}
}

befall = 0;                  //Anzahl Ertragsrelevante Sporen
befallF = 0;
for (Pest Pest : pests) {
	int blatt = Pest.getLeaf();
	if (blatt <= a) {        //immer obere 3 Blattetagen werden ausewählt
		befall += 1;
	}
	if (blatt == 0) {
		befallF +=1;
	}
}*/

//System.out.println("befallGR " + befall);

/*/---------------ENDE--------------------
		List<Integer> anz = new ArrayList<Integer>();
		
		GridCellNgh<Septoria> nghCreatorST = new GridCellNgh<Septoria>(grid, pt, // 3x3 cm um die Pflanze herum wird geschaut (1
																		// entspricht 1 cm)
				Septoria.class, 1, 1);
		List<GridCell<Septoria>> gridCellsST = nghCreatorST.getNeighborhood(true);

		int anzahlSeptoria = 0;
		anzahlST = 0;
		for (GridCell<Septoria> cellST : gridCellsST) { // Aufsummieren der Schädlinge in Umgebung der Pflanze
			anzahlSeptoria += cellST.size();
	
			//einbauen dass jeder Septoria noch auf alter überprüft wird (erst nach 3-4 Wo sichtbar!!) FRAGE: Wie sieht das mit Ascosporen für Primärbefall asu??????????????????
		}

		// zählt, wenn ST an Crop ist
		// damit kann Farmer die Anzahl der befallenen Haupttriebe ermitteln

		if (anzahlSeptoria > 0) {
			anzahlST = 1;
		}

		septoriaAnzahl = anzahlSeptoria; // Schädlingsanzahl auf private variable übertragen

		// ermitteln ob Pests nach Spitzen des Fahnenblattes entstanden sind, dazu
		// werden:

		// Septoria in Umgebung der Crop in ArrayList speichern
		List<Septoria> septoria = new ArrayList<Septoria>();
		for (GridCell<Septoria> cellST : gridCellsST) {
			if (cellST.size() > 0) {
				Iterator<Septoria> iterator = cellST.items().iterator();
				while (iterator.hasNext())
					septoria.add(iterator.next());
			}
		}
		
		//System.out.println("Septoriaanzahl in Umgebung " + septoria.size());
		
		/* funktioniert NICHT SO!!!/ speichern aller sichtbaren Septorias in einer Liste
	    int sept = 0;
		boolean sichtbar = false;                      //variable damit if statement durchgeführt werden kann
		for (Septoria Septoria : septoria){
			sichtbar = Septoria.getSichtbarkeit();
			if (sichtbar = true){
				sept += 1;
			}
		}
		
		STsichtbar = sept;
		anzahlSTsicht = 0;
		
		if (anzahlSeptoria > 0) {
			anzahlST =1;
			if(STsichtbar > 0) {
				anzahlSTsicht = 1;
		}
		}
		System.out.println("anzahlSTsicht " + anzahlSTsicht);*/
		
		
		
		
		// Ermitteln wie viele Ertragsmindernd sind 
		//und anschließend ermittelt wann Pest entstanden ist; entspricht dem tag an
		// dem Sie Crop befallen hat;
		// Annahme: wenn nach Fahnenblatt spitzen geboren, dann landet Schädling auf
		// Fahnenblatt
		
		/*int befallST = 0;
		int befallFST= 0;
		int anzahlSTsichth = 0;

		for (Septoria Septoria : septoria) {
			int blattST = Septoria.getLeaf();
			int sichtST = Septoria.getSichtST();
			//System.out.println("llllll");
			if (blattST < a) {        //immer obere 3 Blattetagen werden ausewählt
				befallST += 1;
			}
			if (blattST == 0) {
				befallFST +=1;
			}
			
			if (0 < Septoria.getSichtST()){
				anzahlSTsichth += 1;
			}
			
		}
		if (anzahlSTsichth > 0){
			anzahlSTsicht = 1;
		}
		
		//System.out.println("Befall " + befall);
		//System.out.println("scihtbarkeit " + anzahlSTsicht);*/


// -------------------------------- Anzahl Schädlinge (GR) in Umfeld der Pflanze
/*/ ----------------------------------------------------------------------\\
List<Pest> agenten = new ArrayList<Pest>();
List<Pest> befallErtragGR = new ArrayList<Pest>();
List<Object> befallGR = new ArrayList<Object>();
List<Object> befallFahnenblatt = new ArrayList<Object>();

int befallSichtbar = 0; //damit später von farmer nur sichtbare Pests gezählt werden 



GridPoint pt = grid.getLocation(this);

GridCellNgh<Pest> nghCreator = new GridCellNgh<Pest>(grid, pt, // 3x3 cm um die Pflanze herum wird geschaut (1
																// entspricht 1 cm)
		Pest.class, 1, 1);

List<GridCell<Pest>> gridCells = nghCreator.getNeighborhood(true);

int anzahlSchaedling = 0;
anzahlGR = 0;
for (GridCell<Pest> cell : gridCells) { // Aufsummieren der Schädlinge in Umgebung der Pflanze
	anzahlSchaedling += cell.size();
	for(Pest pest : cell.items()){
		agenten.add(pest); 
		//System.out.println("Halloooooo");
	}
}*/
//Alternative zu for(Pest pest: agenten)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//BITE überprüfen!!!!NICHT VERGESSEN!!!!!!!!!!!!!!!!!!!!!!!!!!!1
//System.out.println("Agenten" + agenten.size());
/*int Ertragsrel = 0;
for(int i = 0; i < agenten.size(); i++){
	int b;
	b = agenten.get(i).getLeaf();
	if(b <= a){
		Ertragsrel++;
	}
}*/
/*for (Pest pest : agenten){
	//System.out.println("Crop.getLeaf   " + pest.getLeaf() + "   Crop.getGeburt" + pest.getBirth());
	if(pest.getLeaf() <= a){             
		//System.out.println("Halloooooo"); 
		befallGR.add(pest);
		
	}
	if (pest.getLeaf() <= 0){
		//System.out.println("Halloooooo");
		befallFahnenblatt.add(pest);	
	}
	if (pest.getSichtbar() == true){
		befallSichtbar++;
	}
}
befallGRE = befallGR.size();
befallFGR = befallFahnenblatt.size();

//System.out.println("BefallGRE  " + befallGRE);
//System.out.println("BefallFGR   " + befallFGR);


// zählt, wenn Schädling an Crop ist
// damit kann Farmer die Anzahl der befallenen Haupttriebe ermitteln

if (anzahlSchaedling > 0) {
	System.out.println(gelb.toString());
	anzahlGR = 1;
}

if (befallSichtbar > 0) {
	befallsichtbarGR = 1;
	//System.out.println("Sichtbare GR" + befallsichtbarGR);
}

schaedlingsAnzahl = anzahlSchaedling; // Schädlingsanzahl auf private variable übertragen
*/

/*		List<Septoria> agentenST = new ArrayList<Septoria>();
List<Object> befallST = new ArrayList<Object>();
List<Object> befallFahnenblattST = new ArrayList<Object>();


GridPoint pte = grid.getLocation(this);

GridCellNgh<Septoria> nghCreator2 = new GridCellNgh<Septoria>(grid, pte, // 3x3 cm um die Pflanze herum wird geschaut (1
																// entspricht 1 cm)
		Septoria.class, 1, 1);

List<GridCell<Septoria>> gridCells2 = nghCreator2.getNeighborhood(true);

int anzahlSeptoria = 0;
anzahlST = 0;               
for (GridCell<Septoria> cell : gridCells2) { // Aufsummieren der Schädlinge in Umgebung der Pflanze
	anzahlSeptoria += cell.size();
	for(Septoria septoria : cell.items()){
		agentenST.add(septoria);            	
		//System.out.println("Halloooooo");
	}
}

//System.out.println("Agenten" + agentenST.size());
for (Septoria septoria : agentenST){
	//System.out.println("Crop.getLeaf   " + septoria.getLeaf() + "         Crop.Geburt" + septoria.getBirth());
	if(septoria.getLeaf() <= a){             
		 
		befallST.add(septoria);
		
	}
	if (septoria.getLeaf() <= 0){
		//System.out.println("Halloooooo");
		befallFahnenblattST.add(septoria);
		
	}
}
befallSTE = befallST.size();
befallFST = befallFahnenblattST.size();

//System.out.println("BefallFST  " + befallFST);
//System.out.println("BefallFST   " + befallFST);


// zählt, wenn Schädling an Crop ist
// damit kann Farmer die Anzahl der befallenen Haupttriebe ermitteln

if (anzahlSeptoria > 0) {
	anzahlST = 1;
}

septoriaAnzahl = anzahlSeptoria; // Schädlingsanzahl auf private variable übertragen*/
