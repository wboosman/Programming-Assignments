import java.util.ArrayList;
import java.util.List;

public class greedyHeuristic {
	private static int N;
	private static int Q;
	private static double[][] distances;
	private static int[] demand;

	private static List< List<Integer>> routes;
	private static List<Integer> used;
	private static List<Integer> unUsed;

	@SuppressWarnings("static-access")
	public greedyHeuristic(int N, int Q, double[][] distances, int[] demand) {
		this.N = N;
		this.Q = Q;
		this.distances = distances;
		this.demand = demand;

		construction();
	}

	private static void construction() {
		routes = new ArrayList<>();
		used = new ArrayList<>();
		unUsed = new ArrayList<>();

		for(int i=1; i<N; i++) {
			unUsed.add(i);
		}

		// create routes until all locations are visited (depot is not in used)
		while(used.size()<N-1) {
			// create a new route and add the depot to it
			List<Integer> route = new ArrayList<Integer>();
			route.add(0);
			int fill = 0;
			int currLoc = 0;

			// keep adding items until the vehicle is full
			while(fill<=Q) {
				double closest = Double.MAX_VALUE;
				int newLoc = 0;

				for(int j: unUsed) {
					if(distances[currLoc][j]<closest) {
						newLoc = j;
						closest = distances[currLoc][j];
					}
				}
				if(newLoc==0 || Q-fill<demand[newLoc]) {
					break;
				}
				route.add(newLoc);
				updateUsage(newLoc);

				currLoc = newLoc;
				fill += demand[newLoc];
			}
			
			// a route starts at depot and ends at depot, the last index is the filled volume
			route.add(0);
			route.add(fill);
			routes.add(route);
		}
	}

	private static double getRouteLength(List<Integer> route) {
		double obj = 0;
		
		// sum over all distances between the locations on the route
		for(int i=0; i<route.size()-2; i++) {
			obj += distances[route.get(i)][route.get(i+1)];
		}
		return obj;
	}

	private static void updateUsage(int newLoc) {
		// add the location to the used locations
		used.add(newLoc);
		
		// remove the location from the unused locations
		for(int i=0; i<unUsed.size(); i++) {
			if(unUsed.get(i)==newLoc) {
				unUsed.remove(i);
				break;
			}
		}
	}

	public List<List<Integer>> getRoutes(){
		return routes;
	}
	
 	public void printRoutes() {
		for(int r=0; r<routes.size(); r++) {
			System.out.print("Route "+(r+1)+": (" +routes.get(r).get(routes.get(r).size()-1) + ") ");
			
			for(int i=1; i<routes.get(r).size()-2; i++) {
				System.out.print((routes.get(r).get(i)+1) + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public double getObjective() {
		double obj = 0;

		// calculate the length of the routes altogether
		for(List<Integer> route : routes) {
			obj += getRouteLength(route);
		}
		return obj;
	}
}
