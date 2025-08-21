import ilog.concert.IloNumVar;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Heuristic constructs a simple production plan by batching production
 * against cumulative demand and capacity, then derives the implied inventory.
 * It also provides an objective calculator for the heuristic plan.
 *
 * Author: Wessel Boosman
 */
public class Heuristic {

    private ProductionInstance instance;
    private HashMap<String,  List<Integer>> productionHeuristic;

    /**
     * Builds a heuristic solution:
     * - Computes remaining and cumulative demand
     * - Chooses production periods by comparing demand vs. capacity
     * - Computes inventory as cumulative production minus cumulative demand
     *
     * @return map with keys "Production" and "Inventory" (per-period integers)
     */
    public Heuristic(ProductionInstance instance) {
        this.instance = instance;
        this.productionHeuristic = new HashMap<>();
    }
    public HashMap<String, List<Integer>> heuristicSolution(){

//    public void heuristicSolution(){
        List<Integer> production  = new ArrayList<>();
        List<Integer> inventory  = new ArrayList<>();
        HashMap<String, List<Integer>> result = new HashMap<>();
        List<Integer> demandLeft  = new ArrayList<>();
        List<Integer> demandCum  = new ArrayList<>();
        List<Integer> productionCum  = new ArrayList<>();
        int j = 0;

        for(int d=0; d< instance.getNumberPeriods(); d++){
            int totalDemand = 0;
            for (int p=d ; p< instance.getNumberPeriods(); p++) {
                totalDemand = totalDemand + instance.getProdSpecifics().get("Demand").get(p);
            }
            demandLeft.add(totalDemand);
        }
//        System.out.println("Demand remaining: " +  demandLeft);

        for(int d=0; d< instance.getNumberPeriods(); d++){
            int totalDemand = 0;
            for (int p=0 ; p <= d; p++) {
                totalDemand = totalDemand + instance.getProdSpecifics().get("Demand").get(p);
            }
            demandCum.add(totalDemand);
        }


        int excessInventory = 0;
        for (int i = 0; i < instance.getNumberPeriods(); i++){
            if(i==0){
                if (demandLeft.get(i) > instance.getProdSpecifics().get("Capacity").get(i)){
                    //Productie, maar zeker niet de laatste

                    production.add(instance.getProdSpecifics().get("Capacity").get(i));

                    for (int w = i+1 ; w< instance.getNumberPeriods(); w++){
                        if((instance.getProdSpecifics().get("Capacity").get(i)) >= (demandCum.get(w))){
                        }
                        else{
                            j=w;
                            excessInventory = (instance.getProdSpecifics().get("Capacity").get(i)) - (demandCum.get(w-1));
                            break;
                        }
                    }
                }
                else{
                    //Laatste productie periode
                    production.add(demandLeft.get(i) - excessInventory);
                    j = 0;
                }
            }

            else if(i==j){
                if (demandLeft.get(i) - excessInventory > instance.getProdSpecifics().get("Capacity").get(i)){
                    //Productie, maar zeker niet de laatste

                    production.add(instance.getProdSpecifics().get("Capacity").get(i));

                    for (int w = i ; w< instance.getNumberPeriods(); w++){
                        if((instance.getProdSpecifics().get("Capacity").get(i) + excessInventory) >= (demandCum.get(w) - demandCum.get(i-1) )){
                        }
                        else{
                            j=w;
                            excessInventory = (instance.getProdSpecifics().get("Capacity").get(i) + excessInventory) - (demandCum.get(w-1) - demandCum.get(i-1));
                            break;
                        }
                    }
                }
                else{
                    //Laatste productie periode
                    production.add(demandLeft.get(i) - excessInventory);
                    j = 0;
                }

            }
            else{
                //Geen productie
                production.add(0);
            }
        }

        result.put("Production", production);


        for(int d=0; d< instance.getNumberPeriods(); d++){
            int totalProduction = 0;
            for (int p=0 ;  p <= d; p++) {
                totalProduction = totalProduction + production.get(p);
            }
            productionCum.add(totalProduction);
        }

        for(int d=0; d< instance.getNumberPeriods(); d++){
            int dummy = 0;
            for (int p=0 ; p<= d; p++) {
                dummy = dummy + production.get(p) - instance.getProdSpecifics().get("Demand").get(p);
            }
            inventory.add(dummy);
        }
//        System.out.println("Inventory: " +  inventory);

        result.put("Inventory",  inventory);
        productionHeuristic = result;
        return result;
    }

    public Integer getHeuristicObjective() {
        int obj = 0;
        for (int i = 0; i < instance.getNumberPeriods(); i++){
            if(productionHeuristic.get("Inventory").get(i) > 0 && productionHeuristic.get("Production").get(i) > 0 ){
                obj= obj + instance.getFixHolding() + instance.getFixProduction()+ instance.getVarHolding()*productionHeuristic.get("Inventory").get(i) + instance.getVarProduction()*productionHeuristic.get("Production Plan").get(i);
            }
            else if(productionHeuristic.get("Inventory").get(i) == 0 && productionHeuristic.get("Production").get(i) > 0 ){
                obj=obj + instance.getFixProduction() + instance.getVarProduction()*productionHeuristic.get("Production Plan").get(i);

            }
            else if(productionHeuristic.get("Inventory").get(i) > 0 && productionHeuristic.get("Production").get(i) == 0 ){
                obj=obj + instance.getFixHolding() + instance.getVarHolding()*productionHeuristic.get("Inventory").get(i);

            }
        }
        return obj;
    }

}
