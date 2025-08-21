import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class randConstruction {
    private static int N;
    private static int Q;
    private static double[][] distances;
    private static int[] demand;

    private static List< List<Integer>> routes;
    private static List<Integer> used;
    private static List<Integer> unUsed;

    private static List<Integer> rndList;
    private static double alpha = 0.10;

    public randConstruction(int N, int Q, double[][] distances, int[] demand) {
        this.N = N;
        this.Q = Q;
        this.distances = distances;
        this.demand = demand;

        construction();
    }

    private static void construction() {
        int sizeRndList= (int) Math.floor((alpha*N));

        rndList = new ArrayList<>();
        routes = new ArrayList<>();
        used = new ArrayList<>();
        unUsed = new ArrayList<>();

        for(int i=1; i<N; i++) {
            unUsed.add(i);
        }

        // create routes until all locations are visited (depot is not in used)
        while(used.size() < N-1) {
            // create a new route and add the depot to it
            List<Integer> route = new ArrayList<>();
            route.add(0);
            int fill = 0;


            //Pick random element from the unused
            int randomPos = ThreadLocalRandom.current().nextInt(0, unUsed.size());
            int newLoc = unUsed.get(randomPos);

            //Update the current location, delete location from unused cities and add to the route
            route.add(newLoc);
            updateUsage(newLoc);
            fill += demand[newLoc];
            int currLoc = newLoc;



            // keep adding items until the vehicle is full
            while(fill<=Q) {
                newLoc = 0;
                // Change this such you start at random location and then choose not the closest but one of the sizeRndList closest cities
                List<Double> distListOfAllOtherClients = new ArrayList<>();
                for(int j : unUsed) {
                    distListOfAllOtherClients.add(distances[currLoc][j]);
                }
                Collections.sort(distListOfAllOtherClients);

                if (distListOfAllOtherClients.size()>0) {
                    int rndClient = ThreadLocalRandom.current().nextInt(0, Math.min(distListOfAllOtherClients.size(), sizeRndList));
                    double distanceNewClient = distListOfAllOtherClients.get(rndClient);

                    for (int j : unUsed) {
                        if (distances[currLoc][j] == distanceNewClient) {
                            newLoc = j;
                        }
                    }

                    if (newLoc == currLoc) {
                        System.out.println("IETS GAAT FOUT");
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
