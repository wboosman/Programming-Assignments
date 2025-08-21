import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static int N;
	private static int Q;
	private static double[][] distances;
	private static int[] demand;

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws FileNotFoundException {
		for(int i=1; i<=5; i++) {
			System.out.println("Instance " + i);
			File fileToRead = new File("lib/instance"+i+".txt");
			read(fileToRead);

			greedyHeuristic greedyHeuristic = new greedyHeuristic(N, Q, distances, demand);


			VND vnd = new VND(N, Q, distances, demand, greedyHeuristic.getRoutes());
			System.out.println("VND on greedy Heuristic: "+vnd.getObjective());
			System.out.println();

			long start = System.currentTimeMillis();
			long end = start + 1000*600;

			double optimum = Double.MAX_VALUE;

			List<List<Integer>> optRoutes = new ArrayList<>();

			while(System.currentTimeMillis()<end) {
				randConstruction GRASP = new randConstruction(N, Q, distances, demand);
				VND vndG = new VND(N, Q, distances, demand, GRASP.getRoutes());
				
				if(vndG.getObjective()<optimum) {
					optimum = vndG.getObjective();
					optRoutes = vndG.getRoutes();
				}
			}
			System.out.println("Using the GRASP initialization:");
			System.out.println(optimum);
			print(optRoutes);
			System.out.println();
		}
	}

	private static void read(File fileToRead) throws FileNotFoundException{
		//Try to open the file and initialize the use of a so called scanner.
		try (Scanner s = new Scanner(fileToRead)) {
			// Ignore first two tokens
			s.next();
			s.next();
			N = s.nextInt();

			// Ignore next two tokens
			s.next();
			s.next();
			Q = s.nextInt();

			// Ignore next token
			s.next();
			int [][] coordinates = new int[N][2];

			for(int i=0; i<N; i++) {
				s.nextInt();
				coordinates[i][0] = s.nextInt();
				coordinates[i][1] = s.nextInt();
			}

			// Calculate the Euclidean distances using the coordinates
			distances = new double[N][N];
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					if (i == j) {
						distances[i][j] = 0;
					} else {
						distances[i][j] = (double) Math.sqrt(Math.pow(coordinates[i][0] - coordinates[j][0], 2)
								+ Math.pow(coordinates[i][1] - coordinates[j][1], 2));
						distances[j][i] = distances[i][j];
					}
				}
			}

			// Ignore next token
			s.next();
			demand = new int[N];

			for(int i=0; i<N; i++) {
				s.nextInt();
				demand[i] = s.nextInt();
			}
		}
	}
	
	private static void print(List<List<Integer>> routes) {
		for(int r=0; r<routes.size(); r++) {
			for(int i=1; i<routes.get(r).size()-2; i++) {
				System.out.print((routes.get(r).get(i)+1) + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}
