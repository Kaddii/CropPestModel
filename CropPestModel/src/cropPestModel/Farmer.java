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
	private int zeit; // gibt tick/zeitpunkt an
	private double wpreis; // Erzeugerpreis Weizen (€/dt)
	private double ausbringKosten; // Kosten für Überfahrt,Schlepper.. (€/ha)
	private double fpreis; // Kosten für Fungizid (€/ha)
	private int verhalten; // Info, ob man gerade faulen oder fleißigen Landwirt betrachtet

	private double gesamtErtragsPot = 0; // Summe der Ertragspotentiale der einzelnen Crops zum jeweiligen Zeitpunkt
											// (Ertragspotential pro qm)
	private double pflanzenAnzahl; // Anzahl an Ähren/Triebe in der Simulation
	
	private int gesamtGRAnzahl; // Anzahl an GR in der Simulation
	private int gesamtSTAnzahl; //Anzahl ST in der Simulation
	
	
	private double schaedenGR; // Anzahl der befallenen Ähren (GR)
	private double schaedenST; // Anzahl der befallenen Ähren (ST)
	
	private double schaedenSTsicht; //Anzahl befallene Ähren, deren Befall sichtbar ist
	private double schaedenGRsicht;
	
	private double schaedenSTfSechs; //Anzahl befallene Triebe, auf F-6
	
	private double schaedenGRErtrag;
	private double schaedenSTErtrag;
	
	
	private static double schaedenprozSTsicht; //s.o.
	private static double schaedenprozGRsicht;
	private double schaedenprozSTfSechs;
	private int schadenGRF2bF;
	
	private static double schaedenprozGR; // Befallshäufigkeit GR (%)
	private static double schaedenprozST; // Befallshäufigkeit ST (%)
	private static double schaedenprozGRErtrag;
	private double schaedenprozSTErtrag;
	
	private int anzahlSpritzungen; // Anzahl an Spritzvorgängen
	private int grund; //Gibt Indikation aufgrund der gespritzt wird an (1:GR; 2:ST)
	private int j = 0; // zählt Tage die seit letzter bonitur vergangen sind
	private int i; //zählt Anzahl der Schwellenüberschreitungen und gibt Kommando für PSM
	private int zaehler = 25; // zählt Tage seit dem letztem Spritzvorgang (für faulen Landwirt)
    private int abstand;      // abstand zwischen zwei spritzungen bei faulem Landwirt
    int s1; //zeitpunkt 1. Spritzvorgan Fauler LW
    int s2; //zeitpunkt 2. Spritzvorgang Fauler LW
    private boolean schwelleST = false; //wenn Anford erfüllt, dann wird Schwelle true

    
	//private int inhDays; //Wirkzeit des Fungizids für GR
	private static int inDays = 0; //Zeitpunkt bis zu dem Fungizid wirkt
	private static int inDaysST = 0; //Wirkzeit Fungizid für ST
	
	private List<PestSpore> pestSpore = new ArrayList<PestSpore>();
	private List<SeptoriaSpore> septoriaSpore = new ArrayList<SeptoriaSpore>();
	List<Pest> blattlauszahl = new ArrayList<Pest>();
	List<Septoria> septoriaanzahl = new ArrayList<Septoria>();
    
	private double gewinn; // erechneter Gewinn in €/qm
	private double output; // gesamtErtragsPot konvertiert in kg/qm
	private double a; // Konversionsfaktor gesamtErtragsPot zu output
	private double pflanzenschutzKosten; // Kosten für Pflanzenschutz (€/qm)
	private int saatgutkosten; //Saatgutkosten pro m2
	private int saatgutk; //saatgutkosten je nach resistenz in Euro pro ha (exkl. beizkosten)
	
	private double toxi; // Maß für die Toxizität
	private int risktoxi;

	private double schadenSTBonitur = 0;
	private int letzteBonitur; //merkt sich tick der letzten Bonitur, damit farmer unterscheiden kann ob es zu neuen infektionen gekommen ist

	private int r = 0; //zähler damit rain und humidity der letzten tage gespeichert werden
	private int h = 0;
	
	private int proS1;
	private int proS2;
	
	
	private int gr;
	
	// private int resistenz; --> wird benötigt, falls Saatgutkosten in
	// profitfunktion eingehen sollen
	// private int befallsStaerke; --> momentan nicht genutzt, wird aber mit den
	// neuen Daten wieder eingebaut

	ArrayList[] daten = new ArrayList[350]; // ArrayList speichert relevante Daten
	private int resistance; //gibt Farmer info über Resistenzstatus der Pflanze

	

	// -------------------------------------------- Aufrufe
	// -------------------------------------------------------------------------------------\\

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
	
	public double getSchaedenprozSTE() {
		return schaedenprozSTErtrag;
	}
	
	public double getSchaedenprozGRE() {
		return schaedenprozGRErtrag;
	}
	
	public static double getSchaedenprozSTsicht() {
		return schaedenprozSTsicht;
	}
	public double getSchadenprozGRsicht() {
		return schaedenprozGRsicht;
	}
	//public static double getSchaedenproz() {
		//return schaedenprozGR;
	//}

	public double gettoxi() {
		return toxi;
	}
	//TODO:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ÄNDERN!!!!!!!!!!!!!!!!!!!!
	public double getIntegralBHGRErtrag(){
		return toxi;
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
	
	// ------------------------------------ Konstruktor für Farmer
	// ------------------------------------------------------------------------------\\

	public Farmer(ContinuousSpace<Object> space, Grid<Object> grid, double preis, double price, double fpreis,
			double tox, int behaviour, int saatgutk, int zeit, int risktox, int resistance) {
		this.grid = grid;
		this.space = space;
		this.wpreis = price;
		this.ausbringKosten = preis;
		this.fpreis = fpreis;
		this.verhalten = behaviour;
		this.toxi = tox;
		this.risktoxi =risktox;
		this.saatgutk = saatgutk;
		this.zeit = zeit;
		this.resistance = resistance;
	}

// ------------------------------------ Beginn des "täglichen" Ablaufs
	// -------------------------------------------------------------------------\\
	

	public void start() {

		j++;
		pestSpore.clear();
		septoriaSpore.clear();
		blattlauszahl.clear();
		septoriaanzahl.clear();
		zaehler++;

		

		
//----------Abstand Spritzungen für faulen Landwirt berechnen-----------------------
		
	    
		if (Data.getZeit() <= 1){

			schadenSTBonitur = 0; //Sonst funktionieren BatchRuns nicht
			proS1 = 0; //gibt an ob fauler LW schon gespritzt hat
			proS2  = 0;
			
			//Festlegen der Tage, an denen Fauler LW spritzt
			//Annahme Gleichverteilung in jew. Zeiträumen
	
			Random spritz = new Random();
			int n = (Data.getEc33() - Data.getEc31());
			int m = (Data.getEc61() - Data.getEc59()); 
			s1 = spritz.nextInt(n) + Data.getEc31();
			s2 = spritz.nextInt(m) + Data.getEc59();
			
			
				//	int n = (Data.getEc33() + Data.getEc31()) / 2;
			//int m = (Data.getEc61() + Data.getEc59()) / 2; 
			//s1 = (int) Math.round(spritz.nextGaussian() * 1 + n);
			//s2 = (int) Math.round(spritz.nextGaussian() * 1 + m);
			
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
		List<Double> ertragsPotentiale = new ArrayList<Double>(); // Liste der Ertragspotentiale der einzelnen Pflanzen
		List<Object> weizenanzahl = new ArrayList<Object>(); // Liste aller Crops
		List<Integer> schadenGR = new ArrayList<Integer>(); // Liste aller befallenen Triebe (GR)
		List<Integer> schadensichtGR = new ArrayList<Integer>();
		List<Integer> befallGR = new ArrayList<Integer>(); // Liste der GRanzahlen der einzelnen Crops
		List<Integer> schadenST = new ArrayList<Integer>(); // Liste aller befallenen Triebe (ST)
		List<Integer> befallST = new ArrayList<Integer>(); // Liste der STanzahlen der einzelnen Crops
		List<Integer> schadenSTfSechs = new ArrayList<Integer>();
		List<Integer> schadensichtST = new ArrayList<Integer>();
		List<Integer> schadenGRbFbF2 = new ArrayList<Integer>();
		List<Pest> gelbrost = new ArrayList<Pest>();
		List<Crop> grSicht = new ArrayList<Crop>();
		List<Crop> pflanzen = new ArrayList<Crop>();
		List<Crop> stErtrag = new ArrayList<Crop>();
		List<Crop> grErtrag2 = new ArrayList<Crop>();
		
		schadensichtGR.clear();
		
		
		//gr = 0;
		
		for (Object obj : grid.getObjects()) { // befüllen der Listen
			if (obj instanceof Crop) {
				pflanzen.add((Crop) obj);
				ertragsPotentiale.add(((Crop) obj).getErtrag());
				weizenanzahl.add(obj);
				
				schadenGR.add(((Crop) obj).getAnzahlGR());        // ist 1, wenn Crop befallen, sonst 0
				schadenST.add(((Crop) obj).getAnzahlST());        //ist 1, wenn Crop befallen (ST), sonst 0
				
				schadensichtGR.add(((Crop) obj).getAnzahlGRsicht()); //ist 1, wenn Crop sichtbar befallen, sonst 0
				schadensichtST.add(((Crop) obj).getAnzahlSTsicht());
				
				schadenSTfSechs.add(((Crop) obj).getAnzahlSTfSechs()); //ist1, wenn Crop auf f-6 befallen ist, sonst 0
				schadenGRbFbF2.add(((Crop) obj).getAnzahlGRbFbF2());
				
				
				befallGR.add(((Crop) obj).getGelbrostAnzahl());
				befallST.add(((Crop) obj).getSeptoriaAnzahl());
				
				
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
		
		
		//damit Gesamtanzahl in Textfile gespeichert werden kann (Auswertung)
		gesamtGRAnzahl = blattlauszahl.size();
		gesamtSTAnzahl = septoriaanzahl.size();
		

		
		//bestimmen wie viele Pflanzen von sichtbaren GR befallen sind (absolut)
		for(Crop crop : pflanzen){    //STIMMT
			if(crop.getAnzahlGRsicht() == 1){
				grSicht.add(crop);
			}
			if(crop.getBefallGREr() > 0){
				grErtrag2.add(crop);
			}
			if(crop.getBefallSTEr() > 0){
				stErtrag.add(crop);
			}
		}
		
		
		

		// Gesamtertragspotential ermitteln(entspricht immer ZEIT-1), da methode
		// gleichzeitig mit anderer beginnt!!
		gesamtErtragsPot = 0;
		for (int i = 0; i < ertragsPotentiale.size(); i++) {
			gesamtErtragsPot += ertragsPotentiale.get(i);
		}

		
		int pflAnzahl;
		pflAnzahl = weizenanzahl.size(); // Anzahl an Weizenflanzen in Simulation
		pflanzenAnzahl = (double) pflAnzahl; // zu double casten, damit befallshäufigkeit berechnet werden kann

		
		//--------------Befallshäufigkeit GR
		//---------------------------------------------
		
		int g = 0; // Anzahl an befallenen Trieben/Ähren
		int m = 0; // Anzahl befallene Triebe (sichtbar)
		int o = 0;
		int e = 0;
		
		for (int i = 0; i < schadenGR.size(); i++) {
			g += schadenGR.get(i);
		}
		for (int i = 0; i < schadensichtGR.size(); i++){
			m += schadensichtGR.get(i);
		}
		if(anzahlSpritzungen >= 1){
		for (int i = 0; i < schadenGRbFbF2.size(); i++){
			o += schadenGRbFbF2.get(i);
		}
		}

		
		
		schaedenGR = (double) g; // zu double, damit später befallshäufigkeit berechnet werden kann
		schaedenGRsicht = (double) m;
		schadenGRF2bF = o;
		schaedenGRErtrag = (double) grErtrag2.size();//(double) e; //
		

		

		// Befallshäufigkeit berechnen (%)
		schaedenprozGR = (schaedenGR / pflanzenAnzahl) * 100;
		schaedenprozGRsicht = (schaedenGRsicht /pflanzenAnzahl) * 100;
		schaedenprozGRErtrag = (schaedenGRErtrag /pflanzenAnzahl) * 100;

		
		
		//-------------Befallshäufigkeit SEPTORIA
		//---------------------------------------------
		
		
		int s = 0; // Anzahl an befallenen Trieben/Ähren
		int n = 0; //Anzahl sichtbare befallene Triebe
		int f = 0; //Anzahl befallene triebe auf f-6
		for (int i = 0; i < schadenST.size(); i++) {
			s += schadenST.get(i);
		}
		for (int i = 0; i < schadensichtST.size(); i++){
			n += schadensichtST.get(i);
		}
		for(int i = 0; i < schadenSTfSechs.size(); i++){
			f += schadenSTfSechs.get(i);
		}
		schaedenST = (double) s; // zu double, damit später befallshäufigkeit berechnet werden kann
		schaedenSTsicht = (double) n;
		schaedenSTfSechs = (double) f;
		schaedenSTErtrag = (double) stErtrag.size();


		
		// Befallshäufigkeit berechnen (%)
		schaedenprozST = (schaedenST / pflanzenAnzahl) * 100;
		schaedenprozSTsicht = (schaedenSTsicht /pflanzenAnzahl) * 100;
		schaedenprozSTfSechs = (schaedenSTfSechs / pflanzenAnzahl) * 100;
		schaedenprozSTErtrag = (schaedenSTErtrag /pflanzenAnzahl) * 100;

		
			
		
		// je nach Verhalten des Landwirts variiert sein Arbeitsablauf
		// verhalten je nach Spritzmethode, festgelegt als Paramter in GUI Oberflueche
		// FN // Zum Verständis: 0=UK, 1=F1=situationsbezogene Behandlung, 2=F2=praxisbezogene Behandlung
		if (verhalten == 0) {
			// es wird nicht gespritzt
		} else if (verhalten == 3) { //Fauler Landwirt
			if (Data.getZeit() >= Data.getEc30() & Data.getZeit() <= Data.getEc61()) {
				faulerLandwirt();
			}
		} else if (verhalten == 2) {  //Fleissiger Landwirt
			if (Data.getZeit() >= Data.getEc30() & Data.getZeit() <= Data.getEc61()) {
				//schaue nach Wetter (jeden Tag)
				if (Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75)  {
					if (schadenSTBonitur > 50){
						anzahlSpritzungen += 1;
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
			}
			}
			
			
		} else {
			throw new ArithmeticException("es sind nur Vehalten 0 bis 2 definiert");
		}

		// am erntetag wird Gewinn/Profit berechnet
		if (Data.getZeit() == Data.getHarvest()) {
			profitfunktion();
			toxizitaetberechnen();

		}
	}

	// --------------------------- Spritzverhalten fauler Landwirt
	// -------------------------------------------------------------------------------\\
	
	public void faulerLandwirt() {
		
		if (Data.getZeit() <= Data.getEc33() & Data.getZeit() >= Data.getEc31()){
			
			if(Data.getZeit() < s1 & Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75) {

				grund = 2;
				anzahlSpritzungen += 1;
				spritzvorgang();
				proS1 = 1;
			}
		} else if (Data.getZeit() < Data.getEc61() & Data.getZeit() > Data.getEc55()){
			if (Data.getZeit() < s2 & Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75){
				grund = 2;
				anzahlSpritzungen += 1;
				spritzvorgang();
				proS2 = 1;
			}
		}
		
		if((Data.getZeit() == s1 & proS1 == 0) | (Data.getZeit() == s2 & proS2 == 0)){
			grund = 1;
			if (Data.getZeit() > Data.getEc37()){
				Random reason = new Random();
				grund = reason.nextInt(2) + 1; //zufällig 1 oder 2
			}
			anzahlSpritzungen += 1;
			spritzvorgang();
		}
	}

	// ----------------------------Spritzverhalten fleissiger
	// Landwirt-----------------------------------------------------------------------------\\
	public void fleissigerLandwirt() {
		//Bekämpfungsschwelle1

		if(anzahlSpritzungen < 1){
			//Bonitur ST (alle 10 Tage)
			schadenSTBonitur = schaedenprozSTfSechs; //% Befall an ST auf F-6 zum Zeitpunkt der Bonitur
		
			// festlegen Bekaempfungsschwelle ST
			if(schadenSTBonitur > 50 & Data.getRegen() >=3 & Data.getLuft() >= 75 & Data.getFeucht() >= 75) { //3mm Regen und anschließend 2 Tage mit LF über 75 %
				
				grund = 2;
				i++; //gibt an, dass vor diesem Hintergrund gespritzt werden sollte
			}
			
			
			// festlegen der bekuempfungsschwelle GR
			double schwellenwertGR;
			schwellenwertGR = pflanzenAnzahl * 0.3;

			if (schaedenGRsicht > schwellenwertGR) { // 1. Schadschwelle ///ÜBERPRÜFEN VON KLAMMERSETZUNG!!!!!
			grund = 1; //Gelbrost
			i++;
			}
		}else if (anzahlSpritzungen >= 1) {
			//ST
			//neue Sporen nach Ablauf der Protektiv Wirkung 
			if (Data.getSchwelle2() > 0) {
				grund = 2;
				i++;
			}
			
			//GR
			//gr: befall auf f-2, f-1, f
			if(schadenGRF2bF > 0){    //Schadschwelle für Folgebehandlung (erste Sporenlager in oberen 3 Blattetagen)
				grund = 1;
				i++;
			}
			
		}


		
		if (i >= 1 & (Data.getZeit() > inDays| Data.getZeit() > inDaysST)){
			anzahlSpritzungen += 1; 
			spritzvorgang();
		}
	}
	

	



	// ----------------------------------------- Spritzvorgang
	// -------------------------------------------------------------------------------------\\

	public void spritzvorgang() {
		System.out.println("Es wird gespritzt" + grund);
		schadenSTBonitur = 0; //damit nicht immer weiter gespritzt wird
		//Auswahl Fungizid
		
		//Wirkdauer
		
		Random kur = new Random();
		int gk; //Kurativleistung Gelbrost
		int gp; //Protektivleistung Gelbrost
		int sk; //Kurativleistung Septoria
		int sp; //Protektivleistung Septoria
		
		
		if(grund == 1 & Data.getZeit() > Data.getEc37()){
			//Carboxamid
			gk = (int) Math.round(kur.nextGaussian() * 1.17 + 8.29); //Kurativleistung 8-9 Tage
			gp = (int) Math.round(kur.nextGaussian() * 3.16 + 27.57);//Protektivleistung 23-32 Tage
			sk = (int) Math.round(kur.nextGaussian() * 0.55 + 5.86); //Kurativleistung 6-7 Tage
			sp = (int) Math.round(kur.nextGaussian() * 1.47 + 24); //Protektivleistung 24 - 28 Tage
	
			System.out.println(sk + "Carboxamid" + sp);
		}else {
			//Azol
			gk = (int) Math.round(kur.nextGaussian() * 1.55 + 7); //Kurativleistung 5-9 Tage
			gp = (int) Math.round(kur.nextGaussian() * 3.6 + 16.83); //Protektivleistung 12 - 22 Tage
			sk = (int) Math.round(kur.nextGaussian() * 0.75 + 2.83); //Kurativleistung 2-4 Tage
			sp = (int) Math.round(kur.nextGaussian() * 4.04 + 11.5); //Protektivleistung 6 -16 Tage
			System.out.println(sk + "Azol + kur + prot" + sp);
	
		}
		

		//kurative Wirkung (bis zu 4 Tage)

		//Kurativleistung
		for(Pest pest : blattlauszahl) {
			if(pest.getBirth() >= (Data.getZeit() - gk)) {
				pest.die();

			}
		}
		for(Septoria septoria : septoriaanzahl) {
			if(septoria.getBirth() >= (Data.getZeit() - sk)) {
				septoria.die();
			}
		}
		
		
		
		//ABSTERBEN aller SPOREN!!!!!!! (da lebenserwartung kürzer als fungizid + syst. wirkung fungizid)
		for (PestSpore pestspore : pestSpore) {
			pestspore.die();
		}
		for (SeptoriaSpore septoriaspore : septoriaSpore) {
			septoriaspore.die();
		}
		
		
		
		//Wirkdauer (protektiv) 
		//GR
		inDays = Data.getZeit() + gp;
		//ST
		inDaysST = Data.getZeit() + sp;
		i = 0; //Damit nächster Spritzvorgang erst aufgerufen werden kann, wenn die Schwelle wieder überschritten ist

		kostenberechnung();
		
	}
	
	//schliesst sich direkt an jeden Spritzvorgang an
	public void kostenberechnung() {
		//Externe Kosten
		//Spritzmittelaufwand
		//Ausbringkosten
	}

// FN // Messung der Umweltbelastung //risktoxi soll Risiko darstellen
	public void toxizitaetberechnen() {
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
			
		toxi = toxi * anzahlSpritzungen*risikofaktor; //besser: 		toxi = toxi * anzahlSpritzungen + risikofaktor*anzahlSpritzungen;
	}

	
	
	// --------------------------- berechnen des Profits
	// -----------------------------------------------------------------------------------------\\

	public void profitfunktion() {
		// Gewinn pro 1 qm berechnet
		// MONITORING noch einberechen???

		
		pflanzenschutzKosten = ((ausbringKosten + fpreis) / 10000); // von €/ha zu €/qm
		saatgutkosten = saatgutk / 10000;
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