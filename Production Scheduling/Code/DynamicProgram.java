import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * DynamicProgram solves a budgeted production/inventory planning subproblem via DP.
 * State: (t, b) where t = period index, b = cumulative budget used.
 * DP stores the maximum feasible end-of-period inventory achievable with budget ≤ b.
 * Backtracking maps how much budget was allocated to the previous stage.
 *
 * Author: Wessel Boosman
 */
public class DynamicProgram {

    private ProductionInstance instance;
    private int upperBound;
    private int obj;
    private HashMap<Integer, List<Integer>> dpVars;
    private HashMap<Integer, List<Integer>> backTrack;


    /**
     * Creates a DP with a given instance and global budget upper bound.
     *
     * @param instance   production instance (costs, demand, capacity)
     * @param upperBound maximum total budget considered in the DP
     */
    public DynamicProgram(ProductionInstance instance, int upperBound) {
        this.instance = instance;
        this.upperBound = upperBound;

    }

    public Integer prodCost(int x){
        int cost = 0;

        if (x > 0) {
            cost = instance.getFixProduction() + (x * instance.getVarProduction());
        }
        return cost;
    }


    public Integer holdCost(int i){
        int cost = 0;

        if (i > 0) {
            cost =  instance.getFixHolding() + (i * instance.getVarHolding());
        }
        return cost;
    }


    public List<Integer> initialization(){
        List<Integer> initList = new ArrayList<>();
        for (int b = 0; b <= upperBound; b++){
            if(b < prodCost(instance.getProdSpecifics().get("Demand").get(0)) ){
                initList.add(Integer.MIN_VALUE);
            }
            else if( b>= (prodCost(instance.getProdSpecifics().get("Capacity").get(0)) + holdCost(instance.getProdSpecifics().get("Capacity").get(0) - instance.getProdSpecifics().get("Demand").get(0))))
            {
                initList.add(instance.getProdSpecifics().get("Capacity").get(0) - instance.getProdSpecifics().get("Demand").get(0));
            }
            else
            {
                int x = 0;
                while(prodCost(x) + holdCost(x -  instance.getProdSpecifics().get("Demand").get(0)) <= b)
                {
                    x++;
                }
                x--;
                initList.add(x - instance.getProdSpecifics().get("Demand").get(0));
            }
        }

        return initList;
    }



    public Integer maxInventoryDP(List<Integer> initialization) {
        HashMap<Integer, List<Integer>> DPVar = new HashMap<>();
        HashMap<Integer, List<Integer>> backtrackMap = new HashMap<>();

        List<Integer> init = initialization();
        DPVar.put(0, init);

        for (int i = 1 ; i < instance.getNumberPeriods(); i++) {

            List<Integer> listOfB = new ArrayList<>();
            List<Integer> listofA = new ArrayList<>();

            for (int b = 0; b <= upperBound; b++) {

                List<Integer> F1 = new ArrayList<>();
                List<Integer> F2 = new ArrayList<>();
//              ------------------------- For loop in A -------------------------
                for (int a = 0; a <= b; a++) {

                    if (DPVar.get(i - 1).get(a) < 0) {
                        F1.add(Integer.MIN_VALUE);
                        F2.add(Integer.MIN_VALUE);
                    } else
                        {
                            int x = Math.max(0,instance.getProdSpecifics().get("Demand").get(i) - DPVar.get(i - 1).get(a)) ;
                            if(prodCost(x) + holdCost(DPVar.get(i - 1).get(a) + x - instance.getProdSpecifics().get("Demand").get(i)) > ( b - a )){
                                F1.add(Integer.MIN_VALUE);
                            }
                            else {
                                while (prodCost(x) + holdCost(DPVar.get(i - 1).get(a) + x - instance.getProdSpecifics().get("Demand").get(i)) <= ( b - a ) && x <= instance.getProdSpecifics().get("Capacity").get(i)) {
                                    x++;
                                }
                                if (x > 0) {
                                    x--;
                                }
                                F1.add(DPVar.get(i - 1).get(a) + x - instance.getProdSpecifics().get("Demand").get(i));
                            }
                            int It = 0;
                            if(DPVar.get(i - 1).get(a) - instance.getProdSpecifics().get("Demand").get(i) < 1)
                            {
                                F2.add(Integer.MIN_VALUE);
                            }
                            else {
                                while (holdCost(It) <= ( b - a ) && It < DPVar.get(i - 1).get(a) - instance.getProdSpecifics().get("Demand").get(i)) {
                                    It++;
                                }
                                if (It > 0) {
                                    It--;
                                }
                                F2.add(It);
                            }

                        }
                }
                // ---------------------- End For loop in A --------------------------
                List<Integer> maxValComp = new ArrayList<>();
                for(int z =0; z< F1.size(); z++){
                    int c1 = F1.get(z);
                    int c2 = F2.get(z);
                    maxValComp.add(Math.max(c1,c2));
                }
                int maxVal = Collections.max(maxValComp); // should return 7
                int maxIdx = maxValComp.indexOf(maxVal);
                listOfB.add(maxVal);
                listofA.add(maxIdx);
            }
            // --------------------------End for loop in B =---------------------------
            DPVar.put(i, listOfB);
            backtrackMap.put(i, listofA);
        }
        // --------------------------End for loop in I --------------------------

        obj=0;
        while(DPVar.get(instance.getNumberPeriods()-1).get(obj) < 0 ){
                obj++;
        }
        backTrack= backtrackMap;
        dpVars= DPVar;
        return obj;
    }

    public int getObj() {
        return obj;
    }

    public HashMap<Integer, List<Integer>> getDpVars() {
        return dpVars;
    }

    public HashMap<Integer, List<Integer>> getBackTrack() {
        return backTrack;
    }

    public HashMap<String, List<Integer>> DPSolution(){
        HashMap<Integer,Integer> spent = new HashMap<>();
        HashMap<String, List<Integer>> result = new HashMap<>();
        int seek = obj;
        int periodBudget  = 0;
        int budgetToPrevPeriodsAlloc = 0;
        for (int i= (instance.getNumberPeriods()-1); i> 0; i--){
            int index = seek;
            budgetToPrevPeriodsAlloc = backTrack.get(i).get(index);
            periodBudget = seek - backTrack.get(i).get(index);
            seek = budgetToPrevPeriodsAlloc;
            spent.put(i, periodBudget);
        }
        spent.put(0, backTrack.get(1).get(budgetToPrevPeriodsAlloc));
        System.out.println(spent);


        List<Integer> production = new ArrayList<>();
        List<Integer> inventory = new ArrayList<>();
        int residualInv = 0;


        for (int i : spent.keySet()){
            for ( int x = 0 ; x <= instance.getProdSpecifics().get("Capacity").get(i); x++){
                if ((prodCost(x) + holdCost(x + residualInv - instance.getProdSpecifics().get("Demand").get(i))) == spent.get(i))
                {
                    production.add(x);
                    residualInv = x + residualInv - instance.getProdSpecifics().get("Demand").get(i);
                    inventory.add(residualInv);
                    break;
                }
            }
        }
        result.put("Production" , production);
        result.put("Inventory" , inventory);


        return result;
    }

}

