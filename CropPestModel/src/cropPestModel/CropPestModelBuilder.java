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
					new repast.simphony.space.continuous.WrapAroundBorders(), 100, 100);

			GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
			Grid<Object> grid = gridFactory.createGrid("grid", context,
					new GridBuilderParameters<Object>(new WrapAroundBorders(),
							// der Adder legt fest wo im Grid oder space neue Objekte zu Beginn sind
							new SimpleGridAdder<Object>(), true, 100, 100));
			// GridBuilderParameters mit dem Wert true = es ist möglich mehrere Objekte
			// einen Grid-Punkt besetzen können

			// ------------------ Parameter für Agenten
			// -------------------------------------------

			Parameters params = RunEnvironment.getInstance().getParameters();

			int zeit = 0;

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
		
			int pestCount = (Integer) params.getValue("PestCount");

			int birth = 1;

			
			for (int i = 0; i < pestCount; i++) {
				// variable die bestimmt wann sich die Laus vermehrt
				int vermehrungGR = inkubation.nextInt(4) + 12; // Inkubationszeit 12 bis 15 Tage für GR
				Pest pest = new Pest(space, grid, vermehrungGR, resistance, birth);
				context.add(pest);

			}
			
			// -------------------------- PestSpore -------------------
			int vermehrungGR = inkubation.nextInt(4) + 12;
			PestSpore pestSpore = new PestSpore(space, grid, vermehrungGR, resistance);
			context.add(pestSpore);
			
			// ---------------------------------- Septoria
			// --------------------------------------------------------

			int STCount = (Integer) params.getValue("septoria_count");
			int birthST = 1;
			int leafST = 7;
			boolean sichtbar = true;
			
			int septoriaCount;
			//Wenn Resistenz =3 (hoch) ist Ausgangsbefall nur 60% des der anderen Varianten
			
			
			if(resistance == 3) {
				septoriaCount = (int) Math.ceil(0.6*STCount);
			}else{
				septoriaCount = STCount;
			}
			
			for (int i = 0; i < septoriaCount; i++) {
				// variable die bestimmt wann sich die Laus vermehrt
				
				Septoria septoria = new Septoria(space, grid, vermehrungST, resistance);
				context.add(septoria);

			}
			
			// ---------------------------------- SeptoriaSpore
			// -----------------------------------------------------
			
			SeptoriaSpore septoriaSpore = new SeptoriaSpore(space, grid, vermehrungST, resistance);
			context.add(septoriaSpore);

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
			
			//int farmerCount = 1;
			///for (int i = 0; i < farmerCount; i++) {
				// double preis = weizenPreis / 10000; //von dt/ha auf dt/m2 umrechnen

				context.add(new Farmer(space, grid, preis, price, fpreis, tox, behaviour, resistance, zeit, risktox, resistance));
			//}
			for (Object obj : context) {
				NdPoint pt = space.getLocation(obj);
				grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
			}
			
			/*fungicideArray = new double[30][3000][50];
			
			Scanner scanIn = null;
			int Rowc = 0;
			int Row = 0;
			int Colc = 0;
			int Col = 0;
			String InputLine = "";
			double xnum = 0;
			String xfileLocation;
			
			xfileLocation = "C:\\Users\\Katrin\\workworkwork\\CropPestModel\\WeatherFin_NEU.csv";
					//"C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\WeatherFin_NEU.csv";
			
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
			}*/
			
			
			
			//--------------------------- Weather ----------------------\\
			
			context.add(new Data(space, grid, zeit));
			
			weatherArray = new double[30][3000][50];
			
			Scanner scanIn = null;
			int Rowc = 0;
			int Row = 0;
			int Colc = 0;
			int Col = 0;
			String InputLine = "";
			double xnum = 0;
			String xfileLocation;
			
			xfileLocation = "C:\\Users\\Katrin\\git\\CropPestModel\\CropPestModel\\WeatherFin_NEU.csv";
					// "C:\\Users\\Katrin\\workworkwork\\CropPestModel\\WeatherFin_NEU.csv";
					//"C:\\Users\\Kokusnuss\\Documents\\M.Sc.Agrarwissenschaften\\Forschungsprojekt_Gerullis\\Witterung\\WeatherFin_NEU.csv";
			
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



