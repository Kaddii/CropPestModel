package cropPestModel;




	import java.io.BufferedReader;
import java.io.FileReader;
/* ---------------- neue Version Fabian -----------------*/
	import java.util.Random;
import java.util.Scanner;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class CropPestModelBuilder implements ContextBuilder<Object> {
		
		//Import Excel
		static String xStrPath;
		static double [][][] weatherArray;
		static double [][] azoleArray;
		static double [][] carboxamideArray;
		static double [][] seedArray;
	

		@Override
		public Context build(Context<Object> context) {
			// -------------------- Aufbau der Welt bzw. des Feldes
			// -------------------------------------

			context.setId("CropPestModel");
			// der Folgende Abschnitt erstellt die ContinuousSpace und Grid-Projections.
			ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
			ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
					// der Adder legt fest wo im Grid oder space neue Objekte zu Beginn sind
					// WrapAroundBorders legt die Grenzen des space oder Grid fest (z.B. 50 x 50)
					// der CartesianAdder sorgt dafür dass jedes neue Objekt eine zufällige Position
					// bekommt
					new RandomCartesianAdder<Object>(),
					// repast.simphony.space.... legt die grenzen fest. Hier also 50 mal 50
					// erstellt auch einen grid der "grid" heißt und verbindet ihn mit dem Context
					new repast.simphony.space.continuous.WrapAroundBorders(), 200, 200);//200, 500);

			GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
			Grid<Object> grid = gridFactory.createGrid("grid", context,
					new GridBuilderParameters<Object>(new WrapAroundBorders(),
							// der Adder legt fest wo im Grid oder space neue Objekte zu Beginn sind
							new SimpleGridAdder<Object>(), true, 200, 200)); //200, 500));
			// GridBuilderParameters mit dem Wert true = es ist möglich mehrere Objekte
			// einen Grid-Punkt besetzen können

			
			
			
			
			
			//--------------Import Seed Array ---------------------//
			//-------------------------------------------
			
			seedArray = new double[300][300];
			
			Scanner scanIn00 = null;
			int Rowc00 = 0;
			int Row00 = 0;
			int Colc00 = 0;
			int Col00 = 0;
			String InputLine00 = "";
			double xnum00 = 0;
			String xfileLocation00;
			
			xfileLocation00 = "C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\Seeds.csv";
					//"C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\activeIngredient.csv";
					//"C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\WeatherFin_NEU.csv";
			
			System.out.println("-------- Seed Array ------- ");
			
			try
			{
				//Scanner setup
				scanIn00 = new Scanner(new BufferedReader(new FileReader(xfileLocation00)));
				
				while (scanIn00.hasNextLine())
				{
					//Zeile auf Datei lesen
					InputLine00 = scanIn00.nextLine();
					//eingelesene Zellen in Array aufteilen anhand von Kommata
					String [] InArray00 = InputLine00.split(";"); //InArray ist vorrübergehendes Array, um einkommende Zahlen zu speichern
					
					//Inhalt von inArray in weatherArray kopieren
					int i = Integer.parseInt(InArray00[0]);
					//int t = Integer.parseInt(InArray1[1]);
					//System.out.println(i);
					for (int x = 0; x < InArray00.length; x++)
					{
						seedArray[i][x] = Double.parseDouble(InArray00[x]); //Inhalt als double abspeichern
					}
					//Reihe dem Array hinzufügen
					Rowc00++; //nächste Zeile einfügen
				}
			} catch (Exception e)
			{
				System.out.println(e);
			}
			
			// ------------------ Parameter für Agenten
			// -------------------------------------------

			Parameters params = RunEnvironment.getInstance().getParameters();

			int location = (Integer) params.getValue("location");
			
			int ec25;
			int ec30;
			int ec31;
			int ec32;
			int ec33;
			int ec37;
			int ec39;
			int ec43;
			int ec47;
			int ec51;
			int ec55;
			int ec59;
			int ec61;
			int ec65;
			int ec69;
			int ec71;
			int ec73;
			int harvest;
			
			if (location == 1){
				//EC Werte für Data (Standort 1 / DAH)
				ec25 = 34;
				ec30 = 57;
				ec31 = 60;
				ec32 = 78;
				ec33 = 83;
				ec37 = 84;
				ec39 = 93;
				ec43 = 94;
				ec47 = 95;
				ec51 = 96;
				ec55 = 100;
				ec59 = 101;
				ec61 = 102;
				ec65 = 105;
				ec69 = 109;
				ec71 = 114;
				ec73 = 117;
				harvest = 166;
			} else {
				//EC Werte für Data (Standort 2 / SOL)
				ec25 = 44;
				ec30 = 55;
				ec31 = 56;
				ec32 = 66;
				ec33 = 72;
				ec37 = 73;
				ec39 = 83;
				ec43 = 84;
				ec47 = 85;
				ec51 = 87;
				ec55 = 90;
				ec59 = 91;
				ec61 = 92;
				ec65 = 95;
				ec69 = 99;
				ec71 = 103;
				ec73 = 117;
				harvest = 166;
			} 
			
			int zeit = 0;
			
			int year = (Integer) params.getValue("Jahr");
			
			context.add(new Data(space, grid, zeit, ec25, ec30, ec31, ec32, ec33, ec37, ec39, ec43, ec47, ec51, ec55, ec59, ec61, ec65, ec69, ec71, ec73, harvest, location, year));
			
			

			Random inkubation = new Random();
			
			//int vermehrungGR = 12;
			int vermehrungST = inkubation.nextInt(201) + 225;  //Inkubationszeit 225-425 Gradtage für ST

			
			int resistance = (Integer) params.getValue("resistance");
			// ertragspot der unterschiedlichen weizensorten
			double ertragspot; // Ertragspotenzial der Weizenpflanzen variiert mit Sorte
			double saatgutk;
			if (resistance == 1) {
				ertragspot =  10000;//164; // entspricht JBAsano
				saatgutk = 86.48;
			} else if (resistance == 2) {
				ertragspot = 10000;//148;
				saatgutk = 91.18;
			} else if (resistance == 3) {
				ertragspot = 10000; //140;
				saatgutk = 92.12;
			} else {
				throw new ArithmeticException("es sind nur Resistenzwerte zwischen 1 und 3 definiert");
			}
			
			

			// ---------------------------------- Pest
			// --------------------------------------------------------
		
			//int pestCount = (Integer) params.getValue("PestCount");
			//int STCount = (Integer) params.getValue("septoria_count");
			int birth = 1;
			int septoriaCount;
			int rustCount;
			
			int STCount;
			int pestCount;
			
			if(year == 17){
				STCount = 2700;
				pestCount = 80;
			} else if(year == 16){
				STCount = 400;
				pestCount = 3000;
			} else{
				STCount = 2500;
				pestCount = 700;
			}
			
			
			if (resistance == 2){
				septoriaCount = (int) Math.ceil(0.8*STCount); //alt 0.9 0.8 0,75
				rustCount = (int) Math.ceil(0.13*pestCount);
			}
			else if(resistance == 3) {
				septoriaCount = (int) Math.ceil(0.7*STCount); //alt 0,6
				rustCount = (int) Math.ceil(0.09*pestCount);
			}else{
				septoriaCount = STCount;
				rustCount = pestCount;
			}
			
			
			for (int i = 0; i < rustCount; i++) {
				// variable die bestimmt wann sich die Laus vermehrt
				int vermehrungGR = inkubation.nextInt(4) + 12; // Inkubationszeit 12 bis 15 Tage für GR
				Pest pest = new Pest(space, grid, vermehrungGR, resistance);
				context.add(pest);

			}
			
			// -------------------------- PestSpore -------------------
			/*int vermehrungGR = inkubation.nextInt(4) + 12;
			PestSpore pestSpore = new PestSpore(space, grid, vermehrungGR, resistance);
			context.add(pestSpore);*/
			
			// ---------------------------------- Septoria
			// --------------------------------------------------------

			

			
		
		
				
				int leaf = 6;
			
				// variable die bestimmt wann sich die Laus vermehrt
	
				//DREECKSVERTEILUNG mit a = min; b = max; c =modalwert
				
				int reproductionST;
				double a = 175;
				double b = 440;
				double c = 280;
				 
				double F = (c - a) / (b - a);
				double rand = Math.random();
				
				for (int i = 0; i < septoriaCount; i++) {
					//Bestimmt benötigte Temperatursumme
					if (rand < F) {
					 reproductionST = (int) (a + Math.sqrt(rand * (b - a) * (c - a)));
					 } else {
					 reproductionST = (int) (b - Math.sqrt((1-rand) * (b - a) * (b - c)));
					 }
				Septoria septoria = new Septoria(space, grid, resistance, leaf);
				context.add(septoria);

			}
			
			// ---------------------------------- SeptoriaSpore
			// -----------------------------------------------------
			
			/*SeptoriaSpore septoriaSpore = new SeptoriaSpore(space, grid, vermehrungST, resistance);
			context.add(septoriaSpore);*/

			// --------------------------------- Crop
			// ---------------------------------------------
			
			int cropCount = (Integer) params.getValue("cropCount");


			for (int i = 0; i < cropCount; i++) {
				context.add(new Crop(space, grid, ertragspot));
			}
			

			// ---------------------------------- Farmer
			// ---------------------------------------------
			
			int behaviour = (Integer) params.getValue("behaviour");
			double preis = (Double) params.getValue("applicationcost");
			double price = (Double) params.getValue("erzeugerpreis");
			double fpreis = (Double) params.getValue("fungizid");	
			double tox = (Double) params.getValue("tox");
			int risktox = (Integer) params.getValue("risktox");
			int noAzole = (Integer) params.getValue("noAzole");
			int noCarboxamide = (Integer) params.getValue("noCarboxamide");
			
			//int farmerCount = 1;
			///for (int i = 0; i < farmerCount; i++) {
				// double preis = weizenPreis / 10000; //von dt/ha auf dt/m2 umrechnen

				context.add(new Farmer(space, grid, preis, price, fpreis, tox, behaviour, resistance, risktox, resistance, noAzole, noCarboxamide));
			//}
			for (Object obj : context) {
				NdPoint pt = space.getLocation(obj);
				grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
			}
			
			
			
			
			//--------------Import Azol Array ---------------------//
			//-------------------------------------------
			
			azoleArray = new double[300][300];
			
			Scanner scanIn0 = null;
			int Rowc0 = 0;
			int Row0 = 0;
			int Colc0 = 0;
			int Col0 = 0;
			String InputLine0 = "";
			double xnum0 = 0;
			String xfileLocation0;
			
			xfileLocation0 = "C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\Azole.csv";
					//"C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\activeIngredient.csv";
					//"C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\WeatherFin_NEU.csv";
			
			System.out.println("-------- Azole Array ------- ");
			
			try
			{
				//Scanner setup
				scanIn0 = new Scanner(new BufferedReader(new FileReader(xfileLocation0)));
				
				while (scanIn0.hasNextLine())
				{
					//Zeile auf Datei lesen
					InputLine0 = scanIn0.nextLine();
					//eingelesene Zellen in Array aufteilen anhand von Kommata
					String [] InArray0 = InputLine0.split(";"); //InArray ist vorrübergehendes Array, um einkommende Zahlen zu speichern
					
					//Inhalt von inArray in weatherArray kopieren
					int i = Integer.parseInt(InArray0[0]);
					//int t = Integer.parseInt(InArray1[1]);
					//System.out.println(i);
					for (int x = 0; x < InArray0.length; x++)
					{
						azoleArray[i][x] = Double.parseDouble(InArray0[x]); //Inhalt als double abspeichern
					}
					//Reihe dem Array hinzufügen
					Rowc0++; //nächste Zeile einfügen
				}
			} catch (Exception e)
			{
				System.out.println(e);
			}
			
			//--------------Import Fungizid Array ---------------------//
			//-------------------------------------------
			
			carboxamideArray = new double[300][300];
			
			Scanner scanIn1 = null;
			int Rowc1 = 0;
			int Row1 = 0;
			int Colc1 = 0;
			int Col1 = 0;
			String InputLine1 = "";
			double xnum1 = 0;
			String xfileLocation1;
			
			xfileLocation1 = "C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\Carboxamide.csv";
					//"C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\activeIngredient.csv";
					//"C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\WeatherFin_NEU.csv";
			
			System.out.println("-------- Carboxamide Array ------- ");
			
			try
			{
				//Scanner setup
				scanIn1 = new Scanner(new BufferedReader(new FileReader(xfileLocation1)));
				
				while (scanIn1.hasNextLine())
				{
					//Zeile auf Datei lesen
					InputLine1 = scanIn1.nextLine();
					//eingelesene Zellen in Array aufteilen anhand von Kommata
					String [] InArray1 = InputLine1.split(";"); //InArray ist vorrübergehendes Array, um einkommende Zahlen zu speichern
					
					//Inhalt von inArray in weatherArray kopieren
					int i = Integer.parseInt(InArray1[0]);
					//int t = Integer.parseInt(InArray1[1]);
					//System.out.println(i);
					for (int x = 0; x < InArray1.length; x++)
					{
						carboxamideArray[i][x] = Double.parseDouble(InArray1[x]); //Inhalt als double abspeichern
					}
					//Reihe dem Array hinzufügen
					Rowc1++; //nächste Zeile einfügen
				}
			} catch (Exception e)
			{
				System.out.println(e);
			}
			/*for (int i = 0; i < fungicideArray.length; i++){
				for(int x = 1; x < seedArray[i].length; x++){
				System.out.print(fungicideArray[i][x]);
				System.out.print("  ");
			}
				System.out.println();
			}*/
		
		
			
			
			
			
			
			/*/--------------Import Seed Array ---------------------//
			//-------------------------------------------
			
			seedArray = new double[30][3000][50];
			
			Scanner scanIn2 = null;
			int Rowc2 = 0;
			int Row2 = 0;
			int Colc2 = 0;
			int Col2 = 0;
			String InputLine2 = "";
			double xnum2 = 0;
			String xfileLocation2;
			
			xfileLocation2 = "C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\Seeds.csv";
					//"C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\activeIngredient.csv";
					//"C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\WeatherFin_NEU.csv";
			
			System.out.println("-------- Seed Array ------- ");
			
			try
			{
				//Scanner setup
				scanIn2 = new Scanner(new BufferedReader(new FileReader(xfileLocation2)));
				
				while (scanIn2.hasNextLine())
				{
					//Zeile auf Datei lesen
					InputLine2 = scanIn2.nextLine();
					//eingelesene Zellen in Array aufteilen anhand von Kommata
					String [] InArray2 = InputLine2.split(";"); //InArray ist vorrübergehendes Array, um einkommende Zahlen zu speichern
					
					//Inhalt von inArray in weatherArray kopieren
					int l = Integer.parseInt(InArray2[0]);
					int p = Integer.parseInt(InArray2[1]);
					for (int x = 0; x < InArray2.length; x++)
					{
						seedArray[l][p][x] = Double.parseDouble(InArray2[x]); //Inhalt als double abspeichern
					}
					
					//Reihe dem Array hinzufügen
					Rowc2++; //nächste Zeile einfügen
				}
			} catch (Exception e)
			{
				System.out.println(e);
			}
			for (int i = 0; i < seedArray.length; i++){
				for(int t = 0; t < seedArray[i].length; t++){
				System.out.print(seedArray[i][t].toString());
				System.out.print("  ");
			}
				System.out.println();
			}*/
			
			
			
			//--------------------------- Weather ----------------------\\
			
			
			
			weatherArray = new double[30][3000][50];
			
			Scanner scanIn = null;
			int Rowc = 0;
			int Row = 0;
			int Colc = 0;
			int Col = 0;
			String InputLine = "";
			double xnum = 0;
			String xfileLocation;
			
			if (location == 1){
				//File path Location 1 (DAH)
				xfileLocation = "C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\WeatherFin_NEU.csv";
			} else {
				//File path Location 2 (SOL)
				//NOCH ÄNDERN!!!!!!!!!!!!!!!!!!!!!
				
				xfileLocation = "C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\WeatherFin_NEU.csv";
					// "C:\\Users\\Katrin\\workworkwork\\CropPestModel\\WeatherFin_NEU.csv";
					//"C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\WeatherFin_NEU.csv";
			} 
			
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
					int i = Integer.parseInt(InArray[0]);
					int t = Integer.parseInt(InArray[1]);
					//System.out.println(i);
					for (int x = 0; x < InArray.length; x++)
					{
						weatherArray[i][t][x] = Double.parseDouble(InArray[x]); //Inhalt als double abspeichern
					}
					//Reihe dem Array hinzufügen
					Rowc++; //nächste Zeile einfügen
				}
			} catch (Exception e)
			{
				System.out.println(e);
			}
			
			//System.out.println(weatherArray[17][122][3]);
			//System.out.println(weatherArray[17][123][3]);
			//System.out.println(weatherArray[18][50][3]);
			//System.out.println(weatherArray[18][51][3]);

			// ------------------------------------ Ende
			// -----------------------------------------------
			NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("vermehrungs network", context, true);
			netBuilder.buildNetwork();

			// ein Run geht bis zur Ernte
			RunEnvironment.getInstance().endAt(Data.getHarvest());

			return context;
		}
	}



