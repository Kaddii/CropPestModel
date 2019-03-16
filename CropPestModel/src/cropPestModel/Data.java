package cropPestModel;

/*
 * @Author Katrin;
*/


import java.io.*;
import java.util.*;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Data {
	//Daten Ec Stadien
	//Daten Dahnsdorf 2017 (ISIP)
	private static int ec30 = 57;
	private static int ec31 = 60;
	private static int ec32 = 78;
	private static int ec33 = 83;
	private static int ec37 = 84;
	private static int ec39 = 93;
	private static int ec43 = 94;
	private static int ec47 = 95;
	private static int ec51 = 96;
	private static int ec55 = 100;
	private static int ec59 = 101;
	private static int ec61 = 102;
	private static int ec65 = 105;
	private static int ec69 = 109;
	private static int ec71 = 114;
	private static int ec73 = 117;
	private static int harvest = 166;
	
	//allgemein
	private ContinuousSpace<Object> space; // Standort des Farmers (hypothetisch, da nicht auf Feld angezeigt)
	private Grid<Object> grid; // hypothetische Koordinaten des Farmers
	private int i = 11;
	private static int zeit = 0;
	private int ze;
	private int gr;
	private static int septSchwelle2;
	
	//für Schwelle ST fleissiger LW
	private static double regen;
	private static double luft;
	private static double feucht;
	
	private double[] niederschlag; // = new double[200];
	private double[] luftfeuchte; // = new double[200];

	
	
	//Witterungsparameter
	int jahr = 1;
	private static double temp;
	private static double rain;
	private static double humidity;
	
	//VERSUCH121118
	List<Crop> pflanzen = new ArrayList<Crop>();
	List<Pest> gelbrost = new ArrayList<Pest>();
	List<PestSpore> pestSpore = new ArrayList<PestSpore>();
	List<Septoria> septoria = new ArrayList<Septoria>();
	List<SeptoriaSpore> septoriaSpore = new ArrayList<SeptoriaSpore>();
	List<Farmer> farmer = new ArrayList<Farmer>();
	
	List<Septoria> septneu = new ArrayList<Septoria>();

	
	
		// TODO Auto-generated constructor stub

		public static int getEc30(){
			return ec30;
		}
		public static int getEc31(){
			return ec31;
		}
		public static int getEc32(){
			return ec32;
		}
		public static int getEc33(){
			return ec33;
		}
		public static int getEc37(){
			return ec37;
		}
		public static int getEc39(){
			return ec39;
		}
		public static int getEc43(){
			return ec43;
		}
		public static int getEc47(){
			return ec47;
		}
		public static int getEc51(){
			return ec51;
		}
		public static int getEc55(){
			return ec55;
		}
		public static int getEc59(){
			return ec59;
		}
		public static int getEc61(){
			return ec61;
		}
		public static int getEc71(){
			return ec71;
		}
		public static int getHarvest(){
			return harvest;
		}
		public static double getTemp(){
			return temp;
		}
		public static double getRain(){
			return rain;
		}
		public static double getHumidity(){
			return humidity;
		}
		public static int getZeit(){
			return zeit;
		}
		public static double getRegen(){
			return regen;
		}
		public static double getLuft(){
			return luft;
		}
		public static double getFeucht(){
			return feucht;
		}
		public static int getSchwelle2(){
			return septSchwelle2;
		}
		
		public int getJahr(){
			return jahr;
		}


	public Data(ContinuousSpace<Object> space, Grid<Object> grid, int zeit){
		this.grid = grid;
		this.space = space;
		this.zeit = zeit;
		}


//------------------------------------ Beginn des "täglichen" Ablaufs
	// -------------------------------------------------------------------------\\

	@ScheduledMethod(start = 1, interval = 1)
	public void weather(){
		//System.out.println("Hallooooooooooooooooooooooooooooooooooooooooo");
		if(zeit <= 1){
			
			//Arrays erstellen, damit das Wetter über den Verlauf der Anbauperiode aufgezeichnet werden kann
			niederschlag = new double[200];
			luftfeuchte = new double[200];
			
			//Pests den Pflanzen zuordnen
			for (Object obj : grid.getObjects()) { 
				if (obj instanceof Pest) {
					((Pest) obj).zuordnung();
				}
			}
			
		
		}
		zeit++;
		i++;
		septSchwelle2 = 0;
		System.out.println("zeit" + zeit);
		

		
	
		
		
		/*if(zeit == ec31){
			System.out.println("EC31!!!!!");
		} else if (zeit == ec32){
			System.out.println("EC32!!!!");
		} else if (zeit == ec37){
			System.out.println("EC37!!!");
		}*/
		
		//alle 10 Tage (i) wird ausgewählt aus welchem Jahr die Witterungsdaten stammen
		/*if(i >= 10)
		{
			i = 0;
			Random year = new Random();
			jahr = year.nextInt(14) + 1; //eins von 15 Jahren wird ausgewählt
		}*/
		jahr = 17;

		rain = CropPestModelBuilder.weatherArray[jahr][zeit][2];
		temp = CropPestModelBuilder.weatherArray[jahr][zeit][3];
		humidity = CropPestModelBuilder.weatherArray[jahr][zeit][4];
		
		/*niederschlag[zeit] = rain;
		luftfeuchte[zeit] = humidity;*/ //ANSCHALTEN WENN SPRITZUNGEN TESTEN!!!!!!!!!!!!!!!!!!!!
		if (zeit >= ec30-5){
		regen = CropPestModelBuilder.weatherArray[jahr][zeit-3][2];
		luft = CropPestModelBuilder.weatherArray[jahr][zeit-2][4];
		feucht = CropPestModelBuilder.weatherArray[jahr][zeit-1][4];
		}
		
		//LÖSCHEN???!!!!
		/*if (zeit >= ec30-5){
		regen = niederschlag[zeit-3];
		luft = luftfeuchte[zeit-2];
		feucht = luftfeuchte[zeit-1];
		}*/
		
		//System.out.println(rain + "Regen + Luft " + humidity + " feucht" + temp);
	
		
		/*if (zeit >= ec30){
		regen = CropPestModelBuilder.weatherArray[jahr][zeit-3][2];
		luft = CropPestModelBuilder.weatherArray[jahr][zeit-2][4];
		feucht = CropPestModelBuilder.weatherArray[jahr][zeit-1][4];
		
		//System.out.println("RegenSchwelle  " + regen + "   LuftTag1 " + luft + "  tag2  " + feucht);
		//STIMMT! NUR BIS WECHSEL DER JAHRE!!!!!!!!!!!!!!!!!!
		}*/
		

		
		/*System.out.println("Jahr" + jahr);
		System.out.println("zeit" + zeit);
		System.out.println("temp" + temp);*/
		
		//System.out.println("jahr " + jahr);
		//System.out.println("zeit " + zeit);
		//System.out.println("rain " + rain);
		//System.out.println("temp " + temp);
		//System.out.println("humidity " + humidity);
	
		//----------Aufruf der Agenten-----------
		//---------------------------
		//--------------------
		

		
		
	
	//sortiert Pests in Arrays je nach Blatt auf der sie sich befinden
	
		
		//gr=0;
		List<Pest> ge = new ArrayList<Pest>();
		/*List<Pest> allgBlatt = new ArrayList<Pest>();
		List<Object> fFuenf = new ArrayList<Object>();
		List<Object> fVier = new ArrayList<Object>();
		List<Object> fDrei = new ArrayList<Object>();
		List<Object> fZwei = new ArrayList<Object>();
		List<Object> fEins = new ArrayList<Object>();*/
		List<Object> s = new ArrayList<Object>();
		List<Object> sterbe = new ArrayList<Object>();
		
		if(zeit == (ec37 + 3) | zeit == (ec47 + 3) | zeit == (ec59 + 3) | zeit == (ec71 + 3)){
			int z = 0;
			if(zeit == (ec37 + 3)){
				z = 5;
			}
			if(zeit == (ec47 + 3)){
				z = 4;
			}
			if(zeit == (ec59 + 3)){
				z = 3;
			}
			if(zeit == (ec73 + 3)){
				z = 2;
			}
			for (Object obj : grid.getObjects()) { 
				if (obj instanceof Pest) {
					/*if(((Pest) obj).getLeaf() >= z){
						s.add(obj);
						/*Context<Object> context = ContextUtils.getContext(obj);
						context.remove(obj);
						
					}*///funkt NIcht!!
				ge.add((Pest) obj);
				}
			}
			//System.out.println("PestCount  " + ge.size());
		
			for(Pest pest : ge){
				if(pest.getLeaf() > z){            //Fässt alle Blätter zusammen die in Modell zsm. bei EC37 absterben
					//System.out.println("ich werde aufgerufen");
					pest.sterbe();
					//sterbe.add(pest); 
				} 
				/*if (pest.getLeaf() == 5){
					fFuenf.add(pest);
				} 
				if (pest.getLeaf() == 4){
					fVier.add(pest);
				} 
				if (pest.getLeaf() == 3){
					fDrei.add(pest);
				} 
				if (pest.getLeaf() == 2){
					fZwei.add(pest);
				} 
				if (pest.getLeaf() == 1){
					fEins.add(pest);
				} if (pest.getLeaf() == 0){
					fahnenblatt.add(pest);
				}*/
		}
		

		
			
		//System.out.println("Ich werde aufgerufen!!!!!!!!!!!!  " + sterbe.size() + "variante2" + s.size());
		//System.out.println("Allg Blatt   " + allgBlatt.size());
		//System.out.println("Blatt5   " + fFuenf.size());
		//System.out.println("Ballt4   " + fVier.size());
		//System.out.println("Blatt3   " + fDrei.size());
		//System.out.println("Blatt2   " + fZwei.size());
		//System.out.println("Ballt1   " + fEins.size());
		//System.out.println("Blatt0   " + fahnenblatt.size());
		
		/*if(zeit == (ec37 + 3) | zeit == (ec47 + 3) | zeit == (ec59 + 3) | zeit == (ec73 + 3)){
			for(Object obj : sterbe){
				
				//System.out.println("Ich sterbe!!!!!");
				((Pest) obj).sterbe();
				
				Context<Object> context = ContextUtils.getContext(obj);
				context.remove(obj);
				
				
			}
			sterbe.clear();
			//System.out.println("ich auch!!!!!!!!!!!!!!!" + allgBlatt.size());
		} */
		/*if (zeit == ec47){
				for(Object obj : fFuenf){
					Context<Object> context = ContextUtils.getContext(obj);
					context.remove(obj);
				}
				fFuenf.clear();
		} 
		if (zeit == ec59){
			for(Object obj : fVier){
				Context<Object> context = ContextUtils.getContext(obj);
				context.remove(obj);
			}
			fVier.clear();
		} 
		if (zeit == ec71){
			for(Object obj : fDrei){
				Context<Object> context = ContextUtils.getContext(obj);
				context.remove(obj);
			}	
			fDrei.clear();
		}*/
		}
		
		pflanzen.clear();
		gelbrost.clear();
		pestSpore.clear();
		septoria.clear();
		septoriaSpore.clear();
		farmer.clear();
		septneu.clear();
		
		List<Integer> gebl = new ArrayList<Integer>();

		
		for (Object obj : grid.getObjects()) { // befüllen der Listen
			if (obj instanceof Crop) {
				pflanzen.add((Crop) obj);

			} 
			if (obj instanceof Pest) {
				gelbrost.add((Pest) obj);
				((Pest) obj).isDone = false;
				
			}
			if (obj instanceof PestSpore) {
				pestSpore.add((PestSpore) obj);
			}
			if (obj instanceof Septoria) {
				septoria.add((Septoria) obj);
			}
			if (obj instanceof SeptoriaSpore) {
				septoriaSpore.add((SeptoriaSpore) obj);
			}
			if (obj instanceof Farmer) {
				farmer.add((Farmer) obj);
			}
		}
		
		
		//KANN MAN LÖSCHEN!!!!!!!!!!!!!!!!!!!!!!!!
		/*for(Pest pest : gelbrost){
			if(pest.getLeaf() ==0){
			gebl.add(pest.getLeaf());
		}
		}
		
		System.out.println("Blattpest       " + gebl.size());*/
		for (Septoria septoria : septoria){
			if(septoria.getBirth() > Farmer.getInDaysST()){
				septSchwelle2 += 1;
			}
		}
		
		
		//AUFRUF DER AGENTEN
		
		
		if (zeit < (ec71 +3)){
			for(Crop crop : pflanzen){
				//crop.gelb.clear();
				crop.gelbS.clear();
				crop.septfSechs.clear();
				//crop.gelbfbf2.clear();
			}
		                    //ab ec71 können sich die pilze nicht mehr vermehren/wachsen
			if(gelbrost.size() > 0){
				for(Pest pest : gelbrost){
					pest.start();
					System.out.println("ich bin dran");
					if(pest.isDone == true){
						System.out.println("ich bin fertig");
						continue;
						
					}
				}


				
				/*for(int i = 0; i < gelbrost.size(); i++){
					gelbrost.get(i).start();
					
				}*/
			}
			

			

			if(septoria.size() > 0){
				for(int i = 0; i < septoria.size(); i++){
					septoria.get(i).start();
				}
			}
			if(septoriaSpore.isEmpty() == false){
				for(int i = 0; i < septoriaSpore.size(); i++){
					septoriaSpore.get(i).start();
				}
			}
			if(pestSpore.isEmpty() == false){
				for(int i = 0; i < pestSpore.size(); i++){
					pestSpore.get(i).start();
				}
			}

			
		}
		if(pflanzen.size() > 0){
			for(int i = 0; i < pflanzen.size(); i++){
				pflanzen.get(i).befall();
			}
		}
		
		if(farmer.size() > 0) {
			for(int i = 0; i < farmer.size(); i++){
			farmer.get(i).starte();
			}
		}

	
		
	}
	
}



		

		
		
//------------------------------ nach Fertigstellung löschen ---------------------------------------//
		
		/*weatherArray = new double[7][4];
		
		Scanner scanIn = null;
		int Rowc = 0;
		int Row = 0;
		int Colc = 0;
		int Col = 0;
		String InputLine = "";
		double xnum = 0;
		String xfileLocation;
		
		xfileLocation = "C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\Weather.csv";
		
		System.out.println("--------Setup Array Weather ------- ");
		
		try
		{
			//Scanner setup
			scanIn = new Scanner(new BufferedReader(new FileReader(xfileLocation)));
			
			while (scanIn.hasNextLine())
			{
				//Zeile auf Datei lesen
				InputLine = scanIn.nextLine();
				//eingelesene Zellen in Array aufteilen anhand von Kommata
				String [] InArray = InputLine.split(";"); //InArray ist vorrübergehendes Array, um einkommende Zahlen zu speichern
				
				//Inhalt von inArray in weatherArray kopieren
				i = Double.parseDouble(InArray[0]);
				for (int x = 0; x < InArray.length; x++)
				{
					weatherArray[i][Rowc][x] = Double.parseDouble(InArray[x]); //Inhalt als double abspeichern
				}
				//Reihe dem Array hinzufügen
				Rowc++; //nächste Zeile einfügen
			}
		} catch (Exception e)
		{
			System.out.println(e);
		}*/
		
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
		
		
		
	
