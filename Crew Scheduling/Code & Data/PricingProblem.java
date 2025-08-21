import java.util.*;
import java.util.stream.Collectors;
/**
 * PricingProblem performs the pricing step in a column generation framework.
 * It updates arc prices using duals, computes shortest reduced-cost paths
 * for each origin, and returns the best (most negative reduced-cost) duty/column.
 *
 * Author: Wessel Boosman
 */
public class PricingProblem {

    private final HashMap<Integer, Task> allTask;
    private final HashMap<Integer, List<Task>> childrenList;
    public static HashMap<Integer , Graph> megaGraph;

    //First Task in the graph, also the one connected to S.
    private final int fixCosts= 900;
    private  LinkedHashMap<Integer, List<Arc>> graph;
    private HashMap<Integer, Double> reducedCost;
    private List<Integer> newDuty;
    private final List<Integer> originList;
    private double minimumRC;
    private HashMap<String, List<Double>> dualMap;

    /**
     * Constructor: initializes pricing data and immediately computes the best column.
     *
     * @param megaGraph   map from origin task to its Graph
     * @param allTask     all tasks by task id
     * @param childrenList optional adjacency info by task (not used directly here)
     * @param originList  list of origin task ids to consider
     * @param dualMap     dual values from the restricted master problem
     */
    public PricingProblem(HashMap<Integer , Graph> megaGraph, HashMap<Integer, Task> allTask, HashMap<Integer, List<Task>> childrenList, List<Integer> originList, HashMap<String, List<Double>> dualMap) {
        this.allTask = allTask;
        this.megaGraph = megaGraph;
        this.childrenList = childrenList;
        this.dualMap = dualMap;
        this.newDuty = new ArrayList<>();
        this.reducedCost= new HashMap<>();
        this.originList = originList;
        getObjValue();

    }

    public LinkedHashMap<Integer, List<Arc>> getGraph() {
        return graph;
    }
    /**
     * Returns the list of neighbor task ids reachable from a task within a time window.
     *
     * @param time maximum allowed connection time
     * @param task current task
     * @return list of feasible next task ids
     */
    public List<Integer> neighborhood(int time, Task task){
        List<Integer> ListA = new ArrayList<>();
        for (Task i : allTask.values()){

            if (task.getEndTime() < i.getStartTime() && i.getEndTime()<= task.getStartTime()+time) {
                ListA.add(i.getTaskNumber());
            }
        }
        return ListA;
    }
    /**
     * Computes shortest path (in reduced cost) from source (-1) to sink (1000)
     * using a DAG-like forward relaxation (assumes topological order in keys).
     * Fills {@code reducedCost} and reconstructs {@code newDuty} path.
     *
     * @param graph adjacency list with priced arcs
     */
    public void shortestPath(LinkedHashMap<Integer, List<Arc>> graph) {
        int numberOfNodes = graph.size();
        HashMap<Integer, Integer> predecessor = new HashMap<>();
        reducedCost = new HashMap<>();
        reducedCost.put(-1, 0.0);
        predecessor.put(-1,-1);

        for (int i : graph.keySet()) {
            if (reducedCost.get(i) != null) {
                List<Arc> adjacentArcs = graph.get(i);
                if (adjacentArcs != null) {
                    for (Arc arc : adjacentArcs) {
                        double newReducedCost = reducedCost.get(i) + arc.getPrice();
                        if (reducedCost.get(arc.getTo()) == null) {
                            reducedCost.put(arc.getTo(), newReducedCost);
                            predecessor.put(arc.getTo(), arc.getFrom());
                        } else {
                            if (newReducedCost < reducedCost.get(arc.getTo())){
                                reducedCost.put(arc.getTo(), newReducedCost);
                                predecessor.put(arc.getTo(), arc.getFrom());
                            }
                        }
                    }
                }
            }
        }
        List<Integer> getPath = new ArrayList<>();
        getPath.add(0,1000);
        int previous1 = 1000;
        boolean stop = false;
        while (true){
            if (predecessor.get(previous1) == null){
                getPath = new ArrayList<>();
                reducedCost.put(1000,1.0);
                break;
            }
            int previous2 = predecessor.get(previous1);
            if (previous1 == -1){
                break;
            }
            getPath.add(0, previous2);
            previous1 = previous2;
        }
        newDuty = getPath;
    }

    public List<Integer> getNewDuty() {
        return newDuty;
    }

    public double getMinimumRC() {
        return minimumRC;
    }


    /**
     * Runs pricing: updates arc prices using duals, finds the best reduced-cost path
     * over all origins, and constructs the duty vector (cost, length, incidence...).
     * Updates {@code newDuty} and {@code minimumRC}.
     */
    public void getObjValue(){
        double lowestReducedCost = 0.0;
        List<Integer> lowestReducedCostPath= new ArrayList<>();
        //The minus two in the next line still is to debug. Making the final version should look at the exact structure
        List<Integer> duty = new ArrayList<>(Collections.nCopies(allTask.size() - 2, 0));

        for (int i : originList){
           Graph graph = megaGraph.get(i);
           graph.updatePrices(dualMap, allTask, i);
           shortestPath(graph.getGraph());

            if (reducedCost.get(1000) < lowestReducedCost){
                lowestReducedCost = reducedCost.get(1000);
                lowestReducedCostPath = newDuty;

            }
        }
        for(int d = 0; d < duty.size(); d++){
            if(lowestReducedCostPath.contains(d)){
                duty.set(d,1);
            }
        }
        if(lowestReducedCost < -0.00001){
            int lengthDuty = -allTask.get(lowestReducedCostPath.get(1)).getStartTime() + allTask.get(lowestReducedCostPath.get(lowestReducedCostPath.size()-2)).getEndTime();
            duty.add(0, lengthDuty);
            duty.add(0, fixCosts + lengthDuty);
        }

        newDuty = duty;
        minimumRC = lowestReducedCost;
    }

}
