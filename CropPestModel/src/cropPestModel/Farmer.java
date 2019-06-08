package cropPestModel;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Farmer {
	private ContinuousSpace<Object> space; // Standort des Farmers (hypothetisch, da nicht auf Feld angezeigt)
	private Grid<Object> grid; // hypothetische Koordinaten des Farmers
	//private int zeit; // gibt tick/zeitpunkt an
	private double wpreis; // Erzeugerpreis Weizen (€/dt)
	private double ausbringKosten; // Kosten für Überfahrt,Schlepper.. (€/ha)
	private double fpreis; // Kosten für Fungizid (€/ha)
	private int verhalten; // Info, ob man gerade faulen oder fleißigen Landwirt betrachtet

	private double gesamtErtragsPot = 0; // Summe der Ertragspotentiale der einzelnen Crops zum jeweiligen Zeitpunkt
											// (Ertragspotential pro qm)
	private double pflanzenAnzahl; // Anzahl an Ähren/Triebe in der Simulation
	private boolean schwelleST; //gibt an, ob laut ST gespritzt werden sollte
	private static int dayFungicideApplication;
	
	//private int gesamtGRAnzahl; // Anzahl an GR in der Simulation
	//private int gesamtSTAnzahl; //Anzahl ST in der Simulation
	
	
	private double schaedenGR; // Anzahl der befallenen Ähren (GR)
	private double schaedenST; // Anzahl der befallenen Ähren (ST)
	
	private double schaedenSTsicht; //Anzahl befallene Ähren, deren Befall sichtbar ist
	private double schaedenGRsicht;
	
	private double schaedenSTfSechs; //Anzahl befallene Triebe, auf F-6
	
	private double schaedenGRErtrag;
	private double schaedenSTErtrag;
	private double schaedenSTErtragNotVisible;
	
	
	private static double schaedenprozSTsicht; //s.o.
	private static double schaedenprozGRsicht;
	private double schaedenprozSTfSechs;
	private int schadenGRF2bF;
	//private double schaedenSTfFour;
	//private static double schaedenprozSTfFour;
	
	private static double schaedenprozGR; // Befallshäufigkeit GR (%)
	private static double schaedenprozST; // Befallshäufigkeit ST (%)
	private static double schaedenprozGRErtrag;
	private static double schaedenprozSTErtrag;
	private static double schaedenprozSTErtragNotVisible;
	
	private int anzahlSpritzungen = 0; // Anzahl an Spritzvorgängen
	private int grund; //Gibt Indikation aufgrund der gespritzt wird an (1:GR; 2:ST)
	private int j = 0; // zählt Tage die seit letzter bonitur vergangen sind
	private int i; //zählt Anzahl der Schwellenüberschreitungen und gibt Kommando für PSM
	//private int zaehler = 25; // zählt Tage seit dem letztem Spritzvorgang (für faulen Landwirt)
    //private int abstand;      // abstand zwischen zwei spritzungen bei faulem Landwirt
    int s1; //zeitpunkt 1. Spritzvorgan Fauler LW
    int s2; //zeitpunkt 2. Spritzvorgang Fauler LW
    //private boolean schwelleST = false; //wenn Anford erfüllt, dann wird Schwelle true
    private boolean useCarboxamide;
    private boolean useAzole;
    private String fname;
    private int fungicide1;
    private int fungicide2;
    private int fungicide3;
    private int fungicide4;
    private int fungicideCost;
    private double externalCost;
    private int applicationCost;
    
	private boolean reasonST;
	private boolean reasonGR;
	private boolean reasonNotKnown;
    

    
	//private int inhDays; //Wirkzeit des Fungizids für GR
	private static int inDays = 0; //Zeitpunkt bis zu dem Fungizid wirkt
	private static int inDaysST = 0; //Wirkzeit Fungizid für ST
	private int meanprotectiveGR = 0;
	private int meanprotectiveST = 0;
	
	private List<PestSpore> pestSpore = new ArrayList<PestSpore>();
	private List<SeptoriaSpore> septoriaSpore = new ArrayList<SeptoriaSpore>();
	List<Pest> blattlauszahl = new ArrayList<Pest>();
	List<Septoria> septoriaanzahl = new ArrayList<Septoria>();
	List<Integer> fungicides = new ArrayList<Integer>();
    
	private double gewinn; // erechneter Gewinn in €/qm
	private double output; // gesamtErtragsPot konvertiert in kg/qm
	private double a; // Konversionsfaktor gesamtErtragsPot zu output
	private double pflanzenschutzKosten; // Kosten für Pflanzenschutz (€/qm)
	private double saatgutkosten; //Saatgutkosten pro m2
	private int saatgutk; //saatgutkosten je nach resistenz in Euro pro ha (exkl. beizkosten)
	private int noCarboxamide; //Anzahl an Carboxamiden, die dem Landwirt zur Verfügung stehen
	private int noAzole; //Anzahl an Azolen, die dem Landwirt zur Verfügung stehen
	
	private double tox; // Maß für die Toxizität
	private int risktoxi;

	private double schadenSTBonitur = 0;
	private int letzteBonitur; //merkt sich tick der letzten Bonitur, damit farmer unterscheiden kann ob es zu neuen infektionen gekommen ist


	//private int r = 0; //zähler damit rain und humidity der letzten tage gespeichert werden
	//private int h = 0;
	
	private int proS1;
	private int proS2;
	private boolean fungicidesApplied1;
	
	private int integralBHST;
	private int integralBHSTsicht;
	private int integralBHSTertrag;
	private int integralBHGR;
	private int integralBHGRsicht;
	private int integralBHGRertrag;
	
	private int integralBHST1;
	private int integralBHSTsicht1;
	private int integralBHSTertrag1;
	private int integralBHGR1;
	private int integralBHGRsicht1;
	private int integralBHGRertrag1;
	
	private int integralBHST2;
	private int integralBHSTsicht2;
	private int integralBHSTertrag2;
	private int integralBHGR2;
	private int integralBHGRsicht2;
	private int integralBHGRertrag2;
	
	// Graphen zur Kalibrierung
	private int f5 = 0;
	private int f4 = 0;
	private int f3 = 0;
	private int f2 = 0;
	private int f1 = 0;
	private int f0 = 0;
	//private int gr;
	
	// private int resistenz; --> wird benötigt, falls Saatgutkosten in
	// profitfunktion eingehen sollen
	// private int befallsStaerke; --> momentan nicht genutzt, wird aber mit den
	// neuen Daten wieder eingebaut

	ArrayList[] daten = new ArrayList[350]; // ArrayList speichert relevante Daten
	private int resistance; //gibt Farmer info über Resistenzstatus der Pflanze

	

	// -------------------------------------------- Aufrufe
	// -------------------------------------------------------------------------------------\\
	public static int getDayFungicideApplication(){
		return dayFungicideApplication;
	}
	
	
	public int getSpritzwiederholung() {
		return anzahlSpritzungen;
	}

	public double getGewinn() {
		return gewinn;
	}

	public double getApplkosten() {
		return ausbringKosten;
	}

	public double getWPreis() {
		return wpreis;
	}

	public static double getSchaedenprozGR() {
		return schaedenprozGR;
	}
	
	public static double getSchaedenprozST() {
		return schaedenprozST;
	}
	
	public static double getSchaedenprozSTE() {
		return schaedenprozSTErtrag;
	}
	public static double getSchaedenprozSTENotVisible() {
		return schaedenprozSTErtragNotVisible;
	}
	
	public static double getSchaedenprozGRE() {
		return schaedenprozGRErtrag;
	}
	
	public static double getSchaedenprozSTsicht() {
		return schaedenprozSTsicht;
	}
	public double getSchadenprozGRsicht() {
		return schaedenprozGRsicht;
	}


	public double gettoxi() {
		return tox;
	}	
	public static int getInDays() {
		return inDays;
	}

	public static int getInDaysST() {
		return inDaysST;
	}
	
	public int getResistance() {
		return resistance;
	}
	
	public int getBehaviour() {
		return verhalten;
	}
	public int getIntegralBHST(){
		return integralBHST; 
	}
	public int getIntegralBHSTsicht(){
		return integralBHSTsicht;
	}
	public int getIntegralBHSTertrag(){
		return integralBHSTertrag;
	}
	public int getIntegralBHGR(){
		return integralBHGR;
	}
	public int getintegralBHGR(){
		return integralBHGR;
	}
	public int getIntegralBHGRsicht(){
		return integralBHGRsicht;
	}
	public int getIntegralBHGRErtrag(){
		return integralBHGRertrag;
	}
	public int getIntegralBHST1(){
		return integralBHST1; 
	}
	public int getIntegralBHSTsicht1(){
		return integralBHSTsicht1;
	}
	public int getIntegralBHSTertrag1(){
		return integralBHSTertrag1;
	}
	public int getIntegralBHGR1(){
		return integralBHGR1;
	}
	public int getIntegralBHGRsicht1(){
		return integralBHGRsicht1;
	}
	public int getIntegralBHGRErtrag1(){
		return integralBHGRertrag1;
	}
	public int getIntegralBHST2(){
		return integralBHST2; 
	}
	public int getIntegralBHSTsicht2(){
		return integralBHSTsicht2;
	}
	public int getIntegralBHSTertrag2(){
		return integralBHSTertrag2;
	}
	public int getIntegralBHGR2(){
		return integralBHGR2;
	}
	public int getIntegralBHGRsicht2(){
		return integralBHGRsicht2;
	}
	public int getIntegralBHGRErtrag2(){
		return integralBHGRertrag2;
	}
	public int getFungicide1(){
		  return fungicide1;  
	}
	public int getFungicide2(){
		  return fungicide2;  
	}
	public int getFungicide3(){
		  return fungicide3;  
	}
	public int getFungicide4(){
		  return fungicide4;  
	}
	public int getF5(){
		return f5;
	}
	public int getF4(){
		return f4;
	}
	public int getF3(){
		return f3;
	}
	public int getF2(){
		return f2;
	}
	public int getF1(){
		return f1;
	}
	public int getF0(){
		return f0;
	}
	  
	
	// ------------------------------------ Konstruktor für Farmer
	// ------------------------------------------------------------------------------\\

	public Farmer(ContinuousSpace<Object> space, Grid<Object> grid, double preis, double price, double fpreis,
			double tox, int behaviour, int saatgutk, int risktox, int resistance, int noAzole, int noCarboxamide) {
		this.grid = grid;
		this.space = space;
		this.wpreis = price;
		this.ausbringKosten = preis;
		this.fpreis = fpreis;
		this.verhalten = behaviour;
		this.tox = tox;
		this.risktoxi =risktox;
		this.saatgutk = saatgutk;
		this.resistance = resistance;
		this.noAzole = noAzole;
		this.noCarboxamide = noCarboxamide;
		this.fungicidesApplied1 = false;
	}

// ------------------------------------ Beginn des "täglichen" Ablaufs
	// -------------------------------------------------------------------------\\
	

	public void start() {

		j++;
		pestSpore.clear();
		septoriaSpore.clear();
		blattlauszahl.clear();
		septoriaanzahl.clear();
		//zaehler++;

		

		
//----------Abstand Spritzungen für faulen Landwirt berechnen-----------------------
		
	    
		if (Data.getZeit() <= 1){

			schadenSTBonitur = 0; //Sonst funktionieren BatchRuns nicht
			proS1 = 0; //gibt an ob fauler LW schon gespritzt hat
			proS2  = 0;
			
			//Festlegen der Tage, an denen Fauler LW spritzt
			Random spritz = new Random();
			int n = (Data.getEc33() - Data.getEc31()) + 1;
			int m = (Data.getEc65() - Data.getEc47()) + 1; 
			
			s1 = spritz.nextInt(n) + Data.getEc31();
			s2 = spritz.nextInt(m) + Data.getEc47();
			
			//System.out.println(s1 + "s1 und2" + s2);
			/*int n = (Data.getEc33() + Data.getEc31()) / 2;
			int m = (Data.getEc61() + Data.getEc59()) / 2; 
			//s1 = (int) Math.round(spritz.nextGaussian() * 1 + n);
			//s2 = (int) Math.round(spritz.nextGaussian() * 1 + m);*/
			
			//"Wirkung des Fungizid aus letztem Jahr aufheben" -> nur aus programmiertechnischer sicht wichti
			inDays = 0;
			//ST
			inDaysST = 0;
		}

			
		
		getInfo();
	}
	

// ------------------------------------ Schlagdaten sammeln
	// ---------------------------------------------------------------------------------\\

	// sammelt relevante Infos über den simulierten Schlag
	// Werte stellen immmer Wert von Vortag dar, da die drei Agenten gleichzeitig
	// agieren

	public void getInfo() {
		
		System.out.println("AnzahlSpritzungen " + anzahlSpritzungen);
		
		List<Crop> pflanzen = new ArrayList<Crop>();
		
		int allSeptoria = 0;
		int visibleSeptoria = 0;
		int yieldSeptoria = 0;
		int yieldSeptoriaNotVisible = 0;
		int infestationFsixST = 0;
		int yieldGR = 0;
		int allGR = 0;
		int visibleGR = 0;
		int FtoF2GR = 0;
		double yieldPotential = 0;
		f5 = 0;
		f4 = 0;
		f3 = 0;
		f2 = 0;
		f1 = 0;
		f0 = 0;

		
		
		for (Object obj : grid.getObjects()) { // befüllen der Listen
			if (obj instanceof Crop) {
				pflanzen.add((Crop) obj);
	
	
				
			}
			if (obj instanceof Pest){
				blattlauszahl.add((Pest) obj);
			}
			if (obj instanceof Septoria) {
				septoriaanzahl.add((Septoria) obj);
			}
			if (obj instanceof PestSpore) {
				pestSpore.add((PestSpore) obj);
			}
			if (obj instanceof SeptoriaSpore) {
				septoriaSpore.add((SeptoriaSpore) obj);
			}
			
		}
		

		
		//TODO: bestimmen wie viele Pflanzen von sichtbaren GR befallen sind (absolut) HIER WEITER!!!!!
		for(Crop crop : pflanzen){    
			
			yieldPotential += crop.getErtrag();
			
			//Gelbrost
			if(crop.yieldGR == true){
				yieldGR += 1;
			}
			if (crop.allGelbrost == true){
				allGR += 1;
			}
			if(crop.visibleGR == true){
				visibleGR += 1;
			}
			if(anzahlSpritzungen >= 1){
				if(crop.FtoF2GR == true){
					FtoF2GR += 1;
				}
			}
			if(crop.f5 == true){
				f5 += 1;
			}
			if(crop.f4 == true){
				f4 += 1;
			}
			if(crop.f3 == true){
				f3 += 1;
			}
			if(crop.f2 == true){
				f2 += 1;
			}
			if(crop.f1 == true){
				f1 += 1;
			}
			if(crop.f0 == true){
				f0 += 1;
			}
			
			//Septoria
			if(crop.yieldSeptoria == true){
				yieldSeptoria += 1;
			}
			if(crop.yieldSeptoriaNotVisible == true){
				yieldSeptoriaNotVisible += 1;
			}
			if(crop.visibleSeptoria == true){
				visibleSeptoria += 1;
			}
			if(crop.allSeptoria == true){
				allSeptoria += 1;
			}
			if(crop.infestationFsixST == true){
				infestationFsixST += 1;
			}
				
		}
		
		
		
		

		// Gesamtertragspotential ermitteln(entspricht immer ZEIT-1), da methode
		// gleichzeitig mit anderer beginnt!!
		gesamtErtragsPot = yieldPotential;


		int pflAnzahl = pflanzen.size(); // Anzahl an Weizenflanzen in Simulation
		pflanzenAnzahl = (double) pflAnzahl; // zu double casten, damit befallshäufigkeit berechnet werden kann

		
		//--------------Befallshäufigkeit GR
		//---------------------------------------------
		
	
		
		schaedenGR = (double) allGR; //g; // zu double, damit später befallshäufigkeit berechnet werden kann
		schaedenGRsicht = (double) visibleGR; // m;
		schadenGRF2bF = FtoF2GR;//o;
		schaedenGRErtrag = (double) yieldGR;//grErtrag2.size();//(double) e; //
		

		

		// Befallshäufigkeit berechnen (%)
		schaedenprozGR = (schaedenGR / pflanzenAnzahl) * 100;
		schaedenprozGRsicht = (schaedenGRsicht /pflanzenAnzahl) * 100;
		schaedenprozGRErtrag = (schaedenGRErtrag /pflanzenAnzahl) * 100;
		
	

		
		
		//-------------Befallshäufigkeit SEPTORIA
		//---------------------------------------------
		
		schaedenST = (double) allSeptoria; // zu double, damit später befallshäufigkeit berechnet werden kann
		schaedenSTsicht = (double) visibleSeptoria; //n;
		schaedenSTfSechs = (double) infestationFsixST; //f;
		schaedenSTErtrag = (double) yieldSeptoria; //stErtrag.size();
		schaedenSTErtragNotVisible = (double) yieldSeptoriaNotVisible;
		//schaedenSTfFour = (double) fFourST.size();


		
		// Befallshäufigkeit berechnen (%)
		schaedenprozST = (schaedenST / pflanzenAnzahl) * 100;
		schaedenprozSTsicht = (schaedenSTsicht /pflanzenAnzahl) * 100;
		schaedenprozSTfSechs = (schaedenSTfSechs / pflanzenAnzahl) * 100;
		schaedenprozSTErtrag = (schaedenSTErtrag /pflanzenAnzahl) * 100;
		schaedenprozSTErtragNotVisible = (schaedenSTErtragNotVisible /pflanzenAnzahl) * 100;
		//schaedenprozSTfFour = (schaedenSTfFour /pflanzenAnzahl) * 100;
		

		
		//Berechnen des BH Integrals, evtl. für Ertragsabschätzung genutzt
		if(Data.getZeit() > Data.getEc30()){
			integralBHST += schaedenprozST;
			integralBHSTsicht += schaedenprozSTsicht;
			integralBHSTertrag += schaedenprozSTErtrag;
			integralBHGR += schaedenprozGR;
			integralBHGRsicht += schaedenprozGRsicht;
			integralBHGRertrag += schaedenprozGRErtrag;
			if (Data.getZeit() < Data.getEc61()){
				integralBHST1 += schaedenprozST;
				integralBHSTsicht1 += schaedenprozSTsicht;
				integralBHSTertrag1 += schaedenprozSTErtrag;
				integralBHGR1 += schaedenprozGR;
				integralBHGRsicht1 += schaedenprozGRsicht;
				integralBHGRertrag1 += schaedenprozGRErtrag;
			} else{
				integralBHST2 += schaedenprozST;
				integralBHSTsicht2 += schaedenprozSTsicht;
				integralBHSTertrag2 += schaedenprozSTErtrag;
				integralBHGR2 += schaedenprozGR;
				integralBHGRsicht2 += schaedenprozGRsicht;
				integralBHGRertrag2 += schaedenprozGRErtrag;
			}
			
		}
		
		startBehavior();
	}

		
			
		
		// je nach Verhalten des Landwirts variiert sein Arbeitsablauf
		// verhalten je nach Spritzmethode, festgelegt als Paramter in GUI Oberflueche
		// FN // Zum Verständis: 0=UK, 1=F1=situationsbezogene Behandlung, 2=F2=praxisbezogene Behandlung
	public void startBehavior(){
		
		if (verhalten == 0) {
			// es wird nicht gespritzt
		} else if (verhalten == 3) { //Fauler Landwirt
			reasonST = false;
			reasonGR = false;
			reasonNotKnown = false;
			if (Data.getZeit() >= Data.getEc31() & Data.getZeit() < Data.getEc65()) {
				faulerLandwirt();
			}
		} else if (verhalten == 2) {  //Fleissiger Landwirt
			if (Data.getZeit() >= Data.getEc31() & Data.getZeit() < Data.getEc65()) {
				fleissigerLandwirt();
				/*/schaue nach Wetter (jeden Tag)
				//spritze falls ST Schwelle überschritten und schon detektiert!
				if (Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75)  {
					if (schadenSTBonitur > 50){
						anzahlSpritzungen += 1;
						grund = 2;
						spritzvorgang();
					}
					
					if (Data.getSchwelle2() > 0){
						grund = 2;
						anzahlSpritzungen += 1;
						spritzvorgang();
					}
				}
					
				if (j >= 10 | Data.getZeit() == Data.getEc61()) { // alle 10 Tage wird 
																			// bonitiert
				//fleissigerLandwirtBon();
					System.out.println("ich bnonitiere");
					letzteBonitur = Data.getZeit();  //merkt sich tick der letzten Bonitur, damit überprüft werden kann, ob neue Sporenlager entstanden sind
					j = 0; // Zaehler fuer Boniturhueufigkeit
					i = 0; //Anzahl der Schwellenüberschreitungen (ist Auslöser für Spritzentsscheidung)
					fleissigerLandwirt();
			}*/
			}
			
			
		} else {
			throw new ArithmeticException("es sind nur Vehalten 0 bis 3 definiert");
		}

		// am erntetag wird Gewinn/Profit berechnet
		if (Data.getZeit() == Data.getHarvest()) {
			profitfunktion();
			//toxizitaetberechnen();

		}
	}

	// --------------------------- Spritzverhalten fauler Landwirt
	// -------------------------------------------------------------------------------\\
	
	public void faulerLandwirt() {
		//System.out.println("ProS1" + fungicidesApplied1);
		if (Data.getZeit() <= Data.getEc33() & Data.getZeit() >= Data.getEc31()){
		 //if(Data.getZeit() == 72){		
			if(Data.getZeit() < s1 & Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75 & fungicidesApplied1 == false) {

				//grund = 2;
				reasonST = true;
			
				//anzahlSpritzungen += 1;
				spritzvorgang();
				//proS1 = 1;
				fungicidesApplied1 = true;
				
			} else if (Data.getZeit() == s1 & fungicidesApplied1 == false){//proS1 == 0){
			
				reasonNotKnown = true;
				//grund = 1;
				//anzahlSpritzungen += 1;
				spritzvorgang();
				//System.out.println("ich werde aufgerfue");
				fungicidesApplied1 = true;
				//proS1 = 1;
			}
			
			//System.out.println("proS1 nacher" + fungicidesApplied1);
			
			
		}else if (Data.getZeit() < Data.getEc65() & Data.getZeit() >= Data.getEc47()){
		//}else if(Data.getZeit() == 93){
			//reasonNotKnown = true;
			//spritzvorgang();
			if (Data.getZeit() < s2 & Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75){
				reasonST = true;
				//grund = 2;
				//anzahlSpritzungen += 1;
				spritzvorgang();
				proS2 = 1;
			
			}else if (Data.getZeit() == s2 & proS2 == 0){
				//Random reason = new Random();
				//grund = reason.nextInt(2) + 1; //zufällig 1 oder 2
				reasonNotKnown = true;
				//anzahlSpritzungen += 1;
				spritzvorgang();
				proS2 = 1;
			}
		}
		
		/*ALT!!!!if((Data.getZeit() == s1 & proS1 == 0) | (Data.getZeit() == s2 & proS2 == 0)){
			grund = 1;
			if (Data.getZeit() > Data.getEc37()){
				Random reason = new Random();
				grund = reason.nextInt(2) + 1; //zufällig 1 oder 2
			}
			anzahlSpritzungen += 1;
			spritzvorgang();
		}*/
	}

	
	
	
	// ----------------------------Spritzverhalten fleissiger
	// Landwirt-----------------------------------------------------------------------------\\
	
	
	public void fleissigerLandwirt() {
		i = 0; //Anzahl der Schwellenüberschreitungen (ist Auslöser für Spritzentsscheidung)
				//jeden Tag auf 0 setzen, damit nicht "ausversehen" doppelt gespritzt wird
		//schaue nach Wetter (jeden Tag)
		
		if (Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75)  {
			//spritze falls ST Schwelle überschritten und schon detektiert jeden Tag
			
			if (schwelleST == true){
				//anzahlSpritzungen += 1;
				reasonST = true;
				//grund = 2;
				i++; //gibt an, dass aufgrund diesen Faktors gespritzt werden soll
			}
			
			if((anzahlSpritzungen >= 1) & (Data.getZeit() > meanprotectiveST)){
				reasonST = true;
				i++;
			}
			
			//TODO: ÄNDERN!!!! ST SCHWELLE!!!
			/*if (schadenSTBonitur > 50){
				anzahlSpritzungen += 1;
				reasonST = true;
				//grund = 2;
				i++; //gibt an, dass aufgrund diesen Faktors gespritzt werden soll
			}*/
			
			//KANN LW AUCH OHNE FOLGEBONITUR die .ST Spritzung durchführen?????
			/*if (Data.getSchwelle2() > 0){
				grund = 2;
				anzahlSpritzungen += 1;
				//spritzvorgang();
				i++;
			}*/
		}
		
		
		// Landwirt bonitiert alle 10 Tage 
		
		
		
		if (j >= 10 | Data.getZeit() == (Data.getEc65() - 1)) { // alle 10 Tage wird 
															// bonitiert
			reasonST = false;
			reasonGR = false;
			reasonNotKnown = false;
		//fleissigerLandwirtBon();
			System.out.println("ich bnonitiere " + schaedenprozSTfSechs);
			letzteBonitur = Data.getZeit();  //merkt sich tick der letzten Bonitur, damit überprüft werden kann, ob neue Sporenlager entstanden sind
			j = 0; // Zaehler fuer Boniturhueufigkeit
			
			
			//wenn noch nicht gespritzt wurde
		
		
			if(anzahlSpritzungen < 1){
		
			
				// festlegen der bekuempfungsschwelle            GRGRGRGRGR
			
				//double schwellenwertGR;
				//schwellenwertGR = pflanzenAnzahl * 0.3;

				//if (schaedenGRsicht > schwellenwertGR) { // 1. Schadschwelle ///ÜBERPRÜFEN VON KLAMMERSETZUNG!!!!!
				if(schaedenprozGRsicht >= 30){
					//System.out.println(schaedenprozGRsicht + "SchaedenGRSicht" + schaedenGRsicht);
					reasonGR = true;
					//grund = 1; //Gelbrost
					i++;
				}
			
			
			
			/*/Bonitur ST (alle 10 Tage)  ATL (vor 20.03.2019)
			schadenSTBonitur = schaedenprozSTfSechs; //% Befall an ST auf F-6 zum Zeitpunkt der Bonitur
		
			// festlegen Bekaempfungsschwelle ST
			//wenn
			if(schadenSTBonitur > 50 & Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75) { //3mm Regen und anschließend 2 Tage mit LF über 75 %
				reasonST = true;
				//grund = 2;
				i++; //gibt an, dass vor diesem Hintergrund gespritzt werden sollte
			}*/
			/*System.out.println("SchadenST4" + schaedenprozSTfFour);
				//Bonitur ST (alle 10 Tage)  NEU (nach 20.03.)
				if (Data.getZeit() < Data.getEc47()){
					//Indikationsblattetage F-4
					//schadenSTBonitur = schaedenprozSTfFour;
				} else{*/
				//schadenSTBonitur = schaedenprozSTfSechs; //% Befall an ST auf F-6 zum Zeitpunkt der Bonitur
				
				// festlegen Bekaempfungsschwelle ST
				//wenn
				if(schaedenprozSTfSechs > 50){
					//Schwelle überschritten
					schwelleST = true;
					//reasonST = true;
					//wenn Wetter entsprechend wird sofort gespritzt
					if (Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75) { //3mm Regen und anschließend 2 Tage mit LF über 75 %
						reasonST = true;
				
					
					//grund = 2;
					i++; //gibt an, dass vor diesem Hintergrund gespritzt werden sollte
					}
				}
			
			
			
			
			
			

		}else if (anzahlSpritzungen >= 1) {
			//ST
			//ST ausschließlich Witterungsorientiert (hier nochmal aufgeführt, da reasonST = false gesetzt zu Beginn der Bonitur 
			if(Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75){
				if(Data.getZeit() > meanprotectiveST){
					reasonST = true;
					i++;
				}
			}
				
			/*/ALT	
			//neue Sporen nach Ablauf der Protektiv Wirkung 
			if (Data.getSchwelle2() > 0) {
				//grund = 2;
				reasonST = true;
				i++;
			}*/
			
			//GR
			//gr: befall auf f-2, f-1, f sichtbar
			if(schadenGRF2bF > 0){    //Schadschwelle für Folgebehandlung (erste Sporenlager in oberen 3 Blattetagen)
				//grund = 1;
				System.out.println("schaedenFF2" +schadenGRF2bF);
				if(Data.getZeit() > meanprotectiveGR){
					reasonGR = true;
					i++;
				}
			}
		}

			System.out.println(reasonST + " grund ST + grundGR " + reasonGR + "not known " + reasonNotKnown + " schwelleST " + schwelleST);
	}
		
	if (i >= 1){ //& (Data.getZeit() > inDays| Data.getZeit() > inDaysST)){
		spritzvorgang();
	}
	
	}
	

	



	// ----------------------------------------- Spritzvorgang
	// -------------------------------------------------------------------------------------\\

	public void spritzvorgang() {
		System.out.println(reasonST + " grund ST + grundGR " + reasonGR + "not known " + reasonNotKnown + " schwelleST " + schwelleST);
		
		dayFungicideApplication = Data.getZeit();
		anzahlSpritzungen +=1;
		
		grund = 0;
		schadenSTBonitur = 0; //damit nicht immer weiter gespritzt wird
		//Auswahl Fungizid
		schwelleST = false;
		//Wirkdauer
		
		Random kur = new Random();
		int gk; //Kurativleistung Gelbrost
		int gp; //Protektivleistung Gelbrost
		int sk; //Kurativleistung Septoria
		int sp; //Protektivleistung Septoria
		int gmp; //Durchschn. Protektivleistung Gelbrost
		int smp; //Durchschn. Protektivleistung Septoria
		
		if ((reasonGR == true & reasonST == false) | (reasonGR == true & reasonST == true)){
			if (Data.getZeit() < Data.getEc37()){
				//Azol
				grund = 1;
			}else {
				//Carboxamid
				grund = 2;
			}
			
		} else if(reasonGR == false & reasonST == true){
			//Azol
			grund = 1;
		} else if(reasonNotKnown == true){
			if(Data.getZeit() < Data.getEc37()){
				grund = 1;
			}else{
				Random reason = new Random();
				grund = reason.nextInt(2) + 1;
			}
		}
		System.out.println("Es wird gespritzt" + grund);
		
		
		//if(grund == 1 & Data.getZeit() > Data.getEc37()){
		if(grund == 2){
			//Carboxamid
			gk = (int) Math.round(kur.nextGaussian() * 1.17 + 8.29); //Kurativleistung 8-9 Tage
			gp = (int) Math.round(kur.nextGaussian() * 3.16 + 27.57);//Protektivleistung 23-32 Tage
			sk = (int) Math.round(kur.nextGaussian() * 0.55 + 5.86); //Kurativleistung 6-7 Tage
			sp = (int) Math.round(kur.nextGaussian() * 1.47 + 24); //Protektivleistung 24 - 28 Tage
			gmp = 28;
			smp = 24;
			useCarboxamide = true;
			System.out.println(sk + "Carboxamid" + sp);
		}else {
			//Azol
			gk = (int) Math.round(kur.nextGaussian() * 1.55 + 7); //Kurativleistung 5-9 Tage
			gp = (int) Math.round(kur.nextGaussian() * 3.6 + 16.83); //Protektivleistung 12 - 22 Tage
			sk = (int) Math.round(kur.nextGaussian() * 0.75 + 2.83); //Kurativleistung 2-4 Tage
			sp = (int) Math.round(kur.nextGaussian() * 4.04 + 11.5); //Protektivleistung 6 -16 Tage
			gmp = 17;
			smp = 12;
			useAzole = true;
			System.out.println(gk + "Azol + kur + prot" + gp);
	
		}
		

		//kurative Wirkung (bis zu 4 Tage)
		//Protektivleistung: 
		
		//Versuch: nur 90% sterben
		Random die = new Random();
		
		//Kurativleistung
		for(Pest pest : blattlauszahl) {
			int d = die.nextInt(100) + 1; 
			//stirbt wegen Kurativleistung
			if(pest.getBirth() >= (Data.getZeit() - gk)) {
				if(pest.isAlive){
					//if(d >= 10){
						pest.die();
						pest.isAlive = false;
					
				}
			//stirbt wg. Protektivleistung (kann wieder auferstehen nach Ablauf d. Protektivleistung)
			}else{
				if(pest.isAlive & pest.isVisible){
					Data.gelbrostTot.add(pest);
					//System.out.println("birthV" + pest.birthVisible);
					pest.birthVisible += gp;
					//System.out.println("birthV2" + pest.birthVisible);
					pest.isInactive = true; 
					pest.isAlive = false;
				}
			}
		}
		
		System.out.println("SeptoriaArry" + septoriaanzahl.size());
		for(Septoria septoria : septoriaanzahl) {
			int d = die.nextInt(100) + 1; 
			
			//Stirbt wg. Kurativleistung
			if(septoria.getBirth() >= (Data.getZeit() - sk)) {
				if(septoria.isAlive){
					//if(d >= 10){
						septoria.isAlive = false;
						septoria.die();
						
					
			
				}
			//Sterbe wg. Protektivleistung
			}else{
				if(septoria.isAlive & septoria.isVisible){
					Data.septoriaTot.add(septoria);
					septoria.isInactive = true; 
					septoria.isAlive = false;
				}
			}
				
		}
		
		
		
		//ABSTERBEN aller SPOREN!!!!!!! (da lebenserwartung kürzer als fungizid + syst. wirkung fungizid)
		for (PestSpore pestspore : pestSpore) {
			if(pestspore.isAlive){
			pestspore.die();
			}
		}
		for (SeptoriaSpore septoriaspore : septoriaSpore) {
			if(septoriaspore.isAlive){
			septoriaspore.die();
			}
		}
		
		
		
		
		
		//VAR ALT
		//Wirkdauer (protektiv) 
		//GR
		inDays = Data.getZeit() + gp;
		meanprotectiveGR = Data.getZeit() + gmp;
		//ST
		inDaysST = Data.getZeit() + sp;
		meanprotectiveST = Data.getZeit() + smp;
		
		
		i = 0; //Damit nächster Spritzvorgang erst aufgerufen werden kann, wenn die Schwelle wieder überschritten ist
		
		kostenberechnung();
		//schwelleST = false;
		
	}
	
	//schliesst sich direkt an jeden Spritzvorgang an
	public void kostenberechnung() {
		//System.out.println("ich berechne die kosten" + schwelleST);
		int fungicide = 1; //falls Fehler auftritt wird Azol gespritzt (eig nur da, damit Arraylist erstellt werden kann)
		Random fun = new Random(); //Random zur Auswahl des Fungizids
		if (useCarboxamide){
			//nutze Ceriax
			fungicide = fun.nextInt(noCarboxamide) + 1;
			tox = CropPestModelBuilder.carboxamideArray[fungicide][1];
			fpreis = CropPestModelBuilder.carboxamideArray[fungicide][2];
			fname = "carboxamide";
			
			useCarboxamide = false; //auf false setzen, damit bei nächstem Spritzvorgang wieder ausgewählt werden kann, was gespritzt wird
		}else if (useAzole){
			
			fungicide = fun.nextInt(noAzole) + 1; 
			tox = CropPestModelBuilder.azoleArray[fungicide][1];
			fpreis = CropPestModelBuilder.azoleArray[fungicide][2];
			fname = "azole";
		
			useAzole = false;
			//wähle Input Classic o. Pronto Plus
		}
		
		//System.out.println(tox + fname + fpreis);
		
		if (anzahlSpritzungen == 1){
			fungicide1 = fungicide;
		}else if (anzahlSpritzungen == 2){
			fungicide2 = fungicide;
		}else if (anzahlSpritzungen == 3){
			fungicide3 = fungicide;
		}else if (anzahlSpritzungen == 4){
			fungicide4 = fungicide;
		}
	
		//Externe Kosten
		externalCost += tox;
		
		
		//Spritzmittelaufwand
		fungicideCost += fpreis;
		
		//Ausbringkosten
		applicationCost += ausbringKosten;
	}

// FN // Messung der Umweltbelastung //risktoxi soll Risiko darstellen
	/*public void toxizitaetberechnen() {
		double risikofaktor; //risikofaktor soll die "Schlimme" der PSM-behandlung justieren. Dies geschieht je nach "Schädlichkeit des PSM". 
		risikofaktor = 1;
		if(risktoxi == 2) {
		risikofaktor = Math.random()*1;
		}
		if(risktoxi == 3) {
			risikofaktor = Math.random()*2;
			}
		if(risktoxi == 4) {
			risikofaktor = Math.random()*3;
			}
			
		tox = tox * anzahlSpritzungen*risikofaktor; //besser: 		toxi = toxi * anzahlSpritzungen + risikofaktor*anzahlSpritzungen;
	}*/

	
	
	// --------------------------- berechnen des Profits
	// -----------------------------------------------------------------------------------------\\

	public void profitfunktion() {
		// Gewinn pro 1 qm berechnet
		// MONITORING noch einberechen???
		//NOCH ANPASSEN!!!!!!!!!!!!!!!!!
		
		//je nach Exposition  wird Risiko berechnet
		if(risktoxi == 1){
			externalCost = externalCost * 0.5;
		} else if(risktoxi == 2){
			externalCost = externalCost * 1;
		} else if (risktoxi == 3){
			externalCost = externalCost * 2;
		}
		
		
		//NOCH ANPASSEN!!!!!!!!!!!!!!!!!

		double seedCost = CropPestModelBuilder.seedArray[resistance][1];
		double seedTreatmentCost = (CropPestModelBuilder.seedArray[resistance][3]);
		double seedAmount = (CropPestModelBuilder.seedArray[resistance][2]);
		saatgutkosten = ((seedCost + seedTreatmentCost) * seedAmount)/ 10000; //pro m2
		
		pflanzenschutzKosten = ((ausbringKosten + fpreis) / 10000); // von €/ha zu €/qm
		
		wpreis = wpreis / 100; // in €/kg
		a = 0.00003909; // MUSS NOCH ANGEPASST WERDEN AN DURCHSCHNITTL. ERTRAG
		output = gesamtErtragsPot * a;
		gewinn = (output * wpreis) - (anzahlSpritzungen * pflanzenschutzKosten) - saatgutkosten;
		// System.out.println("Das ist der Gewinn" + gewinn);
		//System.out.println("Das ist der Gewinn" + gewinn);
	}

}








//----------------------- Wenn fertig löschen----------------
// gesammelte Infos in Arraylist speichern(stimmt)
//Witterungsdaten der letzten zwei Tage speichern (für fleißigen LW)
/*rain[r] = Data.getRain();
humidity[h] = Data.getHumidity();

r++;
h++;
if (r > 2){
	r = 0;
}
if (h > 1){
	h = 0;
}

System.out.println("rain  " + rain.toString());
System.out.println(humidity.toString());*/
/*daten[zeit] = new ArrayList();

daten[zeit].add(zeit);
daten[zeit].add((double) anzahlSpritzungen);
daten[zeit].add(gesamtErtragsPot);
daten[zeit].add((double) gesamtGRAnzahl);
daten[zeit].add(schaedenprozGR);*/             //WIRD DIESER CODE VERWENDET??????????????

// Hiermit in zukunft Befallsstuerke mit betrachten! NOCH NICHT FERTIG
/*
 * /Befallsstuerke berechnen (Durschnittl. Anzahl) int b = 0; for(int i=0; i
 * <befall.size(); i++){ b += befall.get(i); } schaedlingsAnzahl =
 * b/pflanzenanzahl; //System.out.println("befallsstuerke" + b);
 */

/*Spritzvorgang BIOTROPH!!!

// erfassen wie viele Pilzsporen sich in der kompletten simulation befinden
List<Object> schaedlinge = new ArrayList<Object>();
for (Object obj : grid.getObjects()) {

	if (obj instanceof Pest) {
		schaedlinge.add(obj);
	}
}

gesamtSchaedlingsAnzahl = schaedlinge.size();


// Spritzvorgang = jede Pest stirbt dadurch mit einer 90%igen Wahrscheinlichkeit
// abb

for (int i = 0; i < gesamtSchaedlingsAnzahl; i++) {
	Object obj = schaedlinge.get(i);
	Random zufallswert = new Random();

	// 20 ist das Maximum und 1 das Minimum
	int ueberlebensrate = zufallswert.nextInt(20) + 1;

	// 20 ist das Maximum und 1 das Minimum (+1 weil auch ein zufallswert von 0
	// sonst herauskommen kann)
	if (ueberlebensrate < 18) {
		Context<Object> context = ContextUtils.getContext(obj);
		context.remove(obj);

	}

}*/