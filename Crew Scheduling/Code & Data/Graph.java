
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Graph {
    /**
     * Graph class represents a directed graph structure used for optimization.
     * Each node can have outgoing arcs with associated costs (prices).
     * Prices are updated using dual values from optimization constraints.
     *
     * Author: Wessel Boosman
     */
    private LinkedHashMap<Integer, List<Arc>> graph;
    private final int maxMeanLabour = 432;
    private final int fixCosts = 900;

    public Graph(LinkedHashMap<Integer, List<Arc>> graph) {
        this.graph  = graph;
    }



    public void updatePrices(HashMap<String, List<Double>> dualMap, HashMap<Integer, Task> allTask, int origin){
        List<Double> lambda = dualMap.get("presenceConstraints");
        Double mu = dualMap.get("maxMeanLabourConstraint").get(0);
        for (int i : graph.keySet()){
            for (Arc j : graph.get(i))
            {
                if (j.getFrom() == -1) {
                    int lengthOrigin = allTask.get(origin).getEndTime() - allTask.get(origin).getStartTime();
                    double price = fixCosts - (mu * maxMeanLabour) + (( 1 + mu ) * ( lengthOrigin )) - lambda.get(origin);
                    j.setPrice(price);
                }
                else if(j.getTo() == 1000){
                    double price = 0.0;
                    j.setPrice(price);
                }
                else{
                    double price = ( 1 + mu ) * ( allTask.get(j.getTo()).getEndTime() - allTask.get(j.getFrom()).getEndTime()) - lambda.get(j.getTo());
                    j.setPrice(price);
                }
            }
        }
    }

    public LinkedHashMap<Integer, List<Arc>> getGraph() {
        return graph;
    }
}
