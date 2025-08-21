import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * VND (Variable Neighborhood Descent) heuristic for a capacitated routing problem.
 * 
 * The class maintains a set of routes and iteratively improves them using:
 * 
 *   2-Opt exchange (intra-route swaps)
 *   3-Opt exchange (selected triple reorders)
 *   Destroy-and-repair (random removal and greedy reinsertion)
 * 
 * Routes are represented as lists of integers where the last element encodes the
 * route's currently used capacity; the other elements are location indices (with
 * depot/location conventions assumed by the caller).
 *
 * Author: Wessel Boosman
 */
public class VND {
	private static int N;
	private static int Q;
	private static double[][] distances;
	private static int[] demand;
	private static List<List<Integer>> routes;
	
	private static double alpha;

	@SuppressWarnings("static-access")
	public VND(int N, int Q, double[][] distances, int[] demand, List< List<Integer>> routes) {
		this.N = N;
		this.Q = Q;
		this.distances = distances;
		this.demand = demand;
		this.routes = routes;
		
		alpha = 0.10;

		runVND();
	}

	private static void runVND() {
		double obj = getObjective();
		double improvedObj = 0;
		int k = 1;

		while(k<=6) {
			if(k==1) {
				while(improvedObj < obj){
					obj = getObjective();
					twoOptExchange();
					improvedObj = getObjective();
				}
				k++;
			}
			
			if(k==2) {
				obj = getObjective();
				threeOptExchange();
				improvedObj = getObjective();
				
				if(improvedObj < obj) {
					k = 1;
				} else {
					k++;
				}
			}
			
			if(k>=3) {
				obj = getObjective();
				destroyRepair();
				improvedObj = getObjective();
				
				if(improvedObj < obj) {
					k = 1;
				} else {
					k++;
				}
			}
		}
	}
	
	private static void twoOptExchange() {
		// for each route, check whether the current order is optimal
		for(List<Integer> route : routes) {
			double curr = getRouteLength(route);

			for(int i=1; i<route.size()-3; i++) {
				for(int j=i+1; j<route.size()-2; j++) {
					int loc1 = route.get(i);
					int loc2 = route.get(j);

					route.set(i, loc2);
					route.set(j, loc1);

					if(getRouteLength(route)>curr) {
						route.set(i, loc1);
						route.set(j, loc2);
					}
				}
			}
		}
	}

	private static void threeOptExchange() {
		for(List<Integer> route : routes) {
			if(route.size()>6) {
				// select three locations
				for(int i=1; i<route.size()-4; i++) {
					for(int j=i+1; j<route.size()-3; j++) {
						for(int k=j+1; k<route.size()-2; k++) {
							int loc1 = route.get(i);
							int loc2 = route.get(j);
							int loc3 = route.get(k);

							double currLength = getRouteLength(route);

							// option 1
							route.set(i, loc2);
							route.set(j, loc3);
							route.set(k, loc1);
							double option1 = getRouteLength(route);

							// option 2
							route.set(i, loc3);
							route.set(j, loc1);
							route.set(k, loc2);
							double option2 = getRouteLength(route);

							// the other possible orders are covered in the 2-Opt-Exchange
							if(option1 < option2 && option1 < currLength) {
								route.set(i, loc2);
								route.set(j, loc3);
								route.set(k, loc1);
							} else if(currLength < option1 && currLength < option2){
								route.set(i, loc1);
								route.set(j, loc2);
								route.set(k, loc3);
							}
						}
					}
				}
			}
		}
	}

	private static void destroyRepair() {		
		double currentObjective = getObjective();
		
		List<Integer> toDestroy = getDestruction(alpha);
		List<List<Integer>> newRoutes = getNewRoutes(toDestroy);
		
		for(int loc : toDestroy) {
			double optimalObjective = Double.MAX_VALUE;
			int route = 0;
			int position = 0;
			
			for(int r=0; r<newRoutes.size(); r++) {
				int size = newRoutes.get(r).size();
				int cap = newRoutes.get(r).get(size-1);
				
				if(Q-cap>demand[loc]) {
					for(int i=1; i<size-2; i++) {
						newRoutes.get(r).add(i, loc);
						
						if(getObjective(newRoutes)<optimalObjective) {
							optimalObjective = getObjective(newRoutes);
							route = r;
							position = i;
						}
						
						newRoutes.get(r).remove(i);
					}
				}
			}
			
			if(position!=0) {
				int size  = newRoutes.get(route).size();
				int cap = newRoutes.get(route).get(size-1);
				
				newRoutes.get(route).add(position, loc);
				newRoutes.get(route).set(size, cap+demand[loc]);
				
			}
		}
		
		if(getObjective(newRoutes)<currentObjective) {
			copy(newRoutes);
		}
	}
	
	private static List<Integer> getDestruction(double alpha){
		int destroy = (int) Math.floor((double) alpha*N);
		Random p = new Random();
		
		List<Integer> toDestroy = new ArrayList<>();
		
		for(int i=0; i<destroy; i++) {
			int next = p.nextInt(N-1)+1;
			if(toDestroy.contains(next)) {
				i--;
			} else {
				toDestroy.add(next);
			}
		}
		
		return toDestroy;
	}
	
	
	private static List<List<Integer>> getNewRoutes(List<Integer> toDestroy){
		List<List<Integer>> newRoutes = new ArrayList<>();
		
		for(int r=0; r<routes.size(); r++) {
			List<Integer> newRoute = new ArrayList<>();
			int size = routes.get(r).size();
			int currCap = routes.get(r).get(size-1);
			
			for(int i=0; i<size-1; i++) {
				if(toDestroy.contains(routes.get(r).get(i))) {
					currCap -= demand[routes.get(r).get(i)];
					continue;
				}
				newRoute.add(routes.get(r).get(i));
			}
			newRoute.add(currCap);
			newRoutes.add(newRoute);
		}
		
		return newRoutes;
	}
	
	
	private static void copy (List<List<Integer>> newRoutes) {
		routes.clear();
		for(int r=0; r<newRoutes.size(); r++) {
			if(newRoutes.get(r).size()>3) {
				List<Integer> route = new ArrayList<>();
				
				for(int i=0; i<newRoutes.get(r).size(); i++) {
					route.add(newRoutes.get(r).get(i));
				}
				routes.add(route);
			}
		}
	}
	
	
 	private static double getRouteLength(List<Integer> route) {
		double obj = 0;

		// sum over all distances between the locations on the route
		for(int i=0; i<route.size()-3; i++) {
			obj += distances[route.get(i)][route.get(i+1)];
		}
		return obj;
	}
 	
 	public static List<List<Integer>> getRoutes(){
 		return routes;
 	}

	public static void printRoutes() {
		for(int r=0; r<routes.size(); r++) {
			System.out.print("Route "+(r+1)+": (" +routes.get(r).get(routes.get(r).size()-1) + ") ");

			for(int i=1; i<routes.get(r).size()-2; i++) {
				System.out.print((routes.get(r).get(i)+1) + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	

	public static double getObjective() {
		double obj = 0;

		// calculate the length of the routes altogether
		for(List<Integer> route : routes) {
			obj += getRouteLength(route);
		}
		return obj;
	}
	
	
	public static double getObjective(List<List<Integer>> newRoutes) {
		double obj = 0;

		// calculate the length of the routes altogether
		for(List<Integer> route : newRoutes) {
			obj += getRouteLength(route);
		}
		return obj;
	}
}
