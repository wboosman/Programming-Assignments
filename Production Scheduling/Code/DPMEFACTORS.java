import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DPMEFACTORS {

    private ProductionInstance instance;
    private int upperBound;
    private int obj;
    //    private HashMap<Integer, List<Integer>> dpVars;
    private HashMap<Integer, List<Integer>> backTrack;
    private List<Integer> listB;
    private final int fixHold;
    private final int fixProd;
    private final int varHold;
    private final int varProd;


    public DPMEFACTORS(ProductionInstance instance, int upperBound, int F) {
        this.instance = instance;
        this.upperBound = upperBound;
        this.listB = new ArrayList<>();
        this.fixHold = instance.getFixHolding();
        this.fixProd = instance.getFixProduction();
        this.varHold = instance.getVarHolding();
        this.varProd = instance.getVarProduction();

        createB(F);

    }

    private void createB(int f) {
        int B2 = ( ( Math.floorDiv(upperBound, f) + instance.getNumberPeriods() ) * f );
        int check = 0;
        int it = 0;
        while (check < B2) {
            listB.add(check);
            it++;
            check = it * f;
        }
        listB.add(B2);
//        System.out.println(B2);
//        System.out.println(listB);
//        System.out.println(listB);
    }

    public List<Integer> getA(int b) {
        List<Integer> result = new ArrayList<>();

        listB.stream().filter(i -> i <= b).forEach(result::add);

        return result;
    }

//    public int getASize(int b) {
////        List<Integer> result = new ArrayList<>();
//        AtomicInteger counter = new AtomicInteger(0);
//        listB.stream().filter(i -> i <= b).forEach(counter.getAndIncrement());
//
//        return result.size();
//    }

    public Integer prodCost(int x) {
        int cost = 0;

        if (x > 0) {
            cost = fixProd + ( x * varProd );
        }
        return cost;
    }


    public Integer holdCost(int i) {
        int cost = 0;

        if (i > 0) {
            cost = fixHold + ( i * varHold );
        }
        return cost;
    }


    public List<Integer> initialization() {
        List<Integer> initList = new ArrayList<>();
        for (int b : listB) {
            if (b < prodCost(instance.getProdSpecifics().get("Demand").get(0))) {
                initList.add(Integer.MIN_VALUE);
            } else if (b >= ( prodCost(instance.getProdSpecifics().get("Capacity").get(0)) + holdCost(instance.getProdSpecifics().get("Capacity").get(0) - instance.getProdSpecifics().get("Demand").get(0)) )) {
                initList.add(instance.getProdSpecifics().get("Capacity").get(0) - instance.getProdSpecifics().get("Demand").get(0));
            } else {
                int x = 0;
                while (prodCost(x) + holdCost(x - instance.getProdSpecifics().get("Demand").get(0)) <= b) {
                    x++;
                }
                x--;
                initList.add(x - instance.getProdSpecifics().get("Demand").get(0));
            }
        }

        return initList;
    }


    public void maxInventoryDP(List<Integer> initialization) {
        int[][] DPvar = new int[2][listB.size()];
        List<Integer> init = initialization();
        int It;
        List<Integer> F1;
        List<Integer> F2;
        int c1 ;
        int c2;
        int maxVal;
        int maxIdx ;
        List<Integer> maxValComp;
        int ind;
        int x;
        int getB;
        List<Integer> backtrackList;
        HashMap<Integer, List<Integer>> backTrackMap = new HashMap<>();
        int listAsize;
        int iter;


        for (int u = 0; u < listB.size(); u++) {
            DPvar[0][u] = init.get(u);

        }

        for (int i = 1; i < instance.getNumberPeriods(); i++) {
            backtrackList = new ArrayList<>();
            int demand = instance.getProdSpecifics().get("Demand").get(i);
            int capacity = instance.getProdSpecifics().get("Capacity").get(i);
            iter =1;
            if (i % 2 == 0) {
                for (int b : listB) {

                   F1 = new ArrayList<>();
                   F2= new ArrayList<>();
                   listAsize = iter;
//                    listAsize = getA(b).size();
                    for (int a = 0; a < listAsize; a++) {
                        getB =listB.get(a);
                        // First make sure that if the the previous dp variable with the given a is infeasible, this one is as well....

                        if (DPvar[1][a] < 0) {
                            F1.add(Integer.MIN_VALUE);
                            F2.add(Integer.MIN_VALUE);
                        }
                        //Is this not the case -->
                        else {
                            // The following could also be achieved with an for loop and an if statement, in that case an extra list is needed. In the end, pick the highest ite fro the list. (Maybe better)
                            //Add a value to the F1 list.
                            x = Math.max(0, demand - DPvar[1][a]);
                            if (prodCost(x) + holdCost(DPvar[1][a] + x - demand) > ( b - getB )) {
                                F1.add(Integer.MIN_VALUE);
                            } else {
                                while (prodCost(x) + holdCost(DPvar[1][a] + x - demand) <= ( b - getB ) && x <= capacity) {
                                    x++;
                                }
                                if (x > 0) {
                                    x--;
                                }
                                F1.add(DPvar[1][a] + x - demand);
                            }
                            It = 0;
                            if (DPvar[1][a] - demand < 1) {
                                F2.add(Integer.MIN_VALUE);
                            } else {
                                while (holdCost(It) <= ( b - getB ) && It < DPvar[1][a] - demand) {
                                    It++;
                                }
                                if (It > 0) {
                                    It--;
                                }
                                F2.add(It);
                            }

                        }
                    }
                    // COMPARE/MERGE F1 LIST WITH F2 LIST AND GET MAXIMUM. KEEP TRACK FOR WHICH A THIS IS. DONT OVERWRTIE THAT VALUE. IN THE END YOU NEED AN B BY TIMEPERIODS MATRIX WITH A VALUES.
                    maxValComp = new ArrayList<>();
                    for (int z = 0; z < F1.size(); z++) {
                        c1 = F1.get(z);
                        c2 = F2.get(z);
                        maxValComp.add(Math.max(c1, c2));
                    }
                    maxVal = Collections.max(maxValComp); // should return 7
                    maxIdx = maxValComp.indexOf(maxVal);
                    DPvar[0][listB.indexOf(b)] = maxVal;
                    backtrackList.add(listB.get(maxIdx));
                    backTrackMap.put(i, backtrackList);
                    iter++;
                }
            } else {
                for (int b : listB) {
                    listAsize = iter;
//                    listAsize= getA(b).size();
                    F1 = new ArrayList<>();
                    F2 = new ArrayList<>();

                    for (int a = 0; a < listAsize; a++) {
                        // First make sure that if the the previous dp variable with the given a is infeasible, this one is as well....
                        getB = listB.get(a);

                        if (DPvar[0][a] < 0) {
                            F1.add(Integer.MIN_VALUE);
                            F2.add(Integer.MIN_VALUE);
                        }
                        //Is this not the case -->
                        else {
                            // The following could also be achieved with an for loop and an if statement, in that case an extra list is needed. In the end, pick the highest ite fro the list. (Maybe better)
                            //Add a value to the F1 list.
                            x = Math.max(0, demand - DPvar[0][a]);
                            if (prodCost(x) + holdCost(DPvar[0][a] + x - demand) > ( b - getB )) {
                                F1.add(Integer.MIN_VALUE);
                            } else {
                                while (prodCost(x) + holdCost(DPvar[0][a] + x - demand) <= ( b - getB ) && x <= capacity) {
                                    x++;
                                }
                                if (x > 0) {
                                    x--;
                                }
                                F1.add(DPvar[0][a] + x - demand);
                            }
                            It = 0;
                            if (DPvar[0][a] - demand < 1) {
                                F2.add(Integer.MIN_VALUE);
                            } else {
                                while (holdCost(It) <= ( b - getB ) && It < DPvar[0][a] - demand) {
                                    It++;
                                }
                                if (It > 0) {
                                    It--;
                                }
                                F2.add(It);
                            }

                        }
                    }

                   maxValComp = new ArrayList<>();
                    for (int z = 0; z < F1.size(); z++) {
                         c1 = F1.get(z);
                         c2 = F2.get(z);
                        maxValComp.add(Math.max(c1, c2));
                    }
                     maxVal = Collections.max(maxValComp);
                     maxIdx = maxValComp.indexOf(maxVal);
                    DPvar[1][listB.indexOf(b)] = maxVal;
                    backtrackList.add(listB.get(maxIdx));
                    backTrackMap.put(i, backtrackList);
                    iter++;
                }
            }

        }
        ind = 0;
        if (instance.getNumberPeriods() % 2 == 0) {
            while (DPvar[1][ind] < 0) {
                ind++;
            }
        } else {
            while (DPvar[0][ind] < 0) {
                ind++;
            }
        }
        obj = listB.get(ind);
        backTrack = backTrackMap;

    }


//    public HashMap<String, List<Integer>> DPSolution2() {
//
//        HashMap<Integer, Integer> spent = new HashMap<>();
//        HashMap<String, List<Integer>> result = new HashMap<>();
//        int seek = obj;
//        int periodBudget;
//        int budgetToPrevPeriodsAlloc = 0;
//        for (int i = ( instance.getNumberPeriods() - 1 ); i > 0; i--) {
//
//            int index = listB.indexOf(seek);
//            budgetToPrevPeriodsAlloc = backTrack.get(i).get(index);
//
//            periodBudget = seek - budgetToPrevPeriodsAlloc;
//
//            seek = budgetToPrevPeriodsAlloc;
//
//            spent.put(i, periodBudget);
//        }
//        spent.put(0, backTrack.get(1).get(listB.indexOf(budgetToPrevPeriodsAlloc)));
//        System.out.println(spent);
//
//
////        -----------------------------------------ZIT FOUT ERGENS -----------------------------------------
//        List<Integer> periodNumberReverse = new ArrayList<>();
//        for (int i : spent.keySet()) {
//            periodNumberReverse.add(0, i);
//        }
//        periodNumberReverse.remove(periodNumberReverse.size() - 1);
//        System.out.println(periodNumberReverse);
//
//        List<Integer> production = new ArrayList<>();
//        List<Integer> inventory = new ArrayList<>();
//        int residualInv = 0;
//        int expenditure = 0;
//        inventory.add(0, 0);
//
//        for (int i : periodNumberReverse) {
//            int demand = instance.getProdSpecifics().get("Demand").get(i);
//            int capacity = instance.getProdSpecifics().get("Capacity").get(i);
//            expenditure = spent.get(i) - holdCost(inventory.get(0));
//
//            if (prodCost(demand+ inventory.get(0)) <= expenditure) {
//                if (demand + inventory.get(0) <= capacity) {
//                    production.add(0, demand+ inventory.get(0));
//                    inventory.add(0, 0);
//                } else {
//                    production.add(0, instance.getProdSpecifics().get("Capacity").get(i));
//                    inventory.add(demand + inventory.get(0) - capacity);
//                }
//            } else {
//                for (int x = 1; x <= capacity; x++) {
//                    if (( prodCost(x) ) > expenditure) {
//                        production.add(0, x - 1);
//                        residualInv = demand + inventory.get(0) - ( x - 1 );
//                        inventory.add(0, residualInv);
//                        break;
//                    } else if (x == capacity) {
//                        production.add(0, x);
//                        residualInv = demand + inventory.get(0) - x;
//                        inventory.add(0, residualInv);
//                        break;
//                    }
//                }
//            }
//        }
//
//        production.add(0, instance.getProdSpecifics().get("Demand").get(0) + inventory.get(0));
//        result.put("Production", production);
//        result.put("Inventory", inventory);
//        System.out.println(getActualCost(result));
//        return result;
//    }


    public HashMap<String, List<Integer>> DPSolution2() {

        HashMap<Integer, Integer> spent = new HashMap<>();
        HashMap<String, List<Integer>> result = new HashMap<>();
        int seek = obj;
        int periodBudget;
        int budgetToPrevPeriodsAlloc = 0;
        for (int i = ( instance.getNumberPeriods() - 1 ); i > 0; i--) {

            int index = listB.indexOf(seek);
            budgetToPrevPeriodsAlloc = backTrack.get(i).get(index);

            periodBudget = seek - budgetToPrevPeriodsAlloc;

            seek = budgetToPrevPeriodsAlloc;

            spent.put(i, periodBudget);
        }
        spent.put(0, backTrack.get(1).get(listB.indexOf(budgetToPrevPeriodsAlloc)));
//        System.out.println(spent);


//        -----------------------------------------ZIT FOUT ERGENS -----------------------------------------
        List<Integer> periodNumberReverse = new ArrayList<>();
        for (int i : spent.keySet()) {
            periodNumberReverse.add(0, i);
        }
        periodNumberReverse.remove(periodNumberReverse.size() - 1);
//        System.out.println(periodNumberReverse);

        List<Integer> production = new ArrayList<>();
        List<Integer> inventory = new ArrayList<>();
        int residualInv = 0;
        int expenditure;
        inventory.add(0, 0);

        for (int i : periodNumberReverse) {
            int demand = instance.getProdSpecifics().get("Demand").get(i);
            int capacity = instance.getProdSpecifics().get("Capacity").get(i);
            expenditure = spent.get(i) - holdCost(residualInv);

            if (prodCost(demand+ residualInv) <= expenditure) {
                if (demand + residualInv <= capacity) {
                    production.add(0, demand+ residualInv);
                    inventory.add(0, 0);
                    residualInv = 0;
                } else {
                    production.add(0, capacity);
                    inventory.add(0,demand + residualInv - capacity);
                    residualInv = demand  + residualInv - capacity;
                }

            }
//            else if(expenditure < 0){
//                production.add(0, 0);
//                inventory.add(0,demand + residualInv - capacity);
//            }
            else {
                for (int x = 1; x <= capacity; x++) {
                    if (( prodCost(x) ) > expenditure) {
                        production.add(0, (x - 1));
                        inventory.add(0, (demand + residualInv - (x-1)));
                        residualInv = demand + residualInv - ( x - 1);
                        break;
                    } else if (x == capacity) {
                        production.add(0, x);
                        inventory.add(0, (demand + residualInv - (x)));
                        residualInv = demand + residualInv - x;
                        break;
                    }
                }
            }
        }

        production.add(0, instance.getProdSpecifics().get("Demand").get(0) + residualInv);
        result.put("Production", production);
        result.put("Inventory", inventory);
        System.out.println("Actual Cost (Upper Bound): " + getActualCost(result));
//        System.out.println("Inventory length :" + inventory.size());
//        System.out.println("produc length :" + production.size());

        return result;
    }


    public Integer getActualCost(HashMap<String, List<Integer>> prodPlan){
        int price = 0;
        for (String s : prodPlan.keySet()){
            for (int i : prodPlan.get(s)){
                if (s.equals("Production")){
                    price = price + prodCost(i);
                } else{
                    price = price + holdCost(i);
                }
            }
        }
        return price;
    }



//    BACK UP 2
//
//    public HashMap<String, List<Integer>> DPSolution2() {
//
//        HashMap<Integer, Integer> spent = new HashMap<>();
//        HashMap<String, List<Integer>> result = new HashMap<>();
//        int seek = obj;
//        int periodBudget;
//        int budgetToPrevPeriodsAlloc = 0;
//        for (int i = ( instance.getNumberPeriods() - 1 ); i > 0; i--) {
//
//            int index = listB.indexOf(seek);
//            budgetToPrevPeriodsAlloc = backTrack.get(i).get(index);
//
//            periodBudget = seek - budgetToPrevPeriodsAlloc;
//
//            seek = budgetToPrevPeriodsAlloc;
//
//            spent.put(i, periodBudget);
//        }
//        spent.put(0, backTrack.get(1).get(listB.indexOf(budgetToPrevPeriodsAlloc)));
//        System.out.println(spent);
//
//
////        -----------------------------------------ZIT FOUT ERGENS -----------------------------------------
//        List<Integer> periodNumberReverse = new ArrayList<>();
//        for (int i : spent.keySet()) {
//            periodNumberReverse.add(0, i);
//        }
//        periodNumberReverse.remove(periodNumberReverse.size() - 1);
//        System.out.println(periodNumberReverse);
//
//        List<Integer> production = new ArrayList<>();
//        List<Integer> inventory = new ArrayList<>();
//        int residualInv = 0;
//        int expenditure = 0;
//        inventory.add(0, 0);
//
//        for (int i : periodNumberReverse) {
//            int demand = instance.getProdSpecifics().get("Demand").get(i);
//            int capacity = instance.getProdSpecifics().get("Capacity").get(i);
//            expenditure = spent.get(i) - holdCost(inventory.get(0));
//
//            if (prodCost(demand+ inventory.get(0)) <= expenditure) {
//                if (demand + inventory.get(0) <= capacity) {
//                    production.add(0, demand+ inventory.get(0));
//                    inventory.add(0, 0);
//                } else {
//                    production.add(0, instance.getProdSpecifics().get("Capacity").get(i));
//                    inventory.add(demand + inventory.get(0) - capacity);
//                }
//            } else {
//                for (int x = 1; x <= capacity; x++) {
//                    if (( prodCost(x) ) > expenditure) {
//                        production.add(0, x - 1);
//                        residualInv = demand + inventory.get(0) - ( x - 1 );
//                        inventory.add(0, residualInv);
//                        break;
//                    } else if (x == capacity) {
//                        production.add(0, x);
//                        residualInv = demand + inventory.get(0) - x;
//                        inventory.add(0, residualInv);
//                        break;
//                    }
//                }
//            }
//        }
//
//        production.add(0, instance.getProdSpecifics().get("Demand").get(0) + inventory.get(0));
//        result.put("Production", production);
//        result.put("Inventory", inventory);
//        System.out.println(getActualCost(result));
//        return result;
//    }










        public HashMap<String, List<Integer>> DPSolution(){
        HashMap<Integer,Integer> spent = new HashMap<>();
        HashMap<String, List<Integer>> result = new HashMap<>();
        int seek = obj;
        int periodBudget;
        int budgetToPrevPeriodsAlloc = 0;
        for (int i= (instance.getNumberPeriods()-1); i> 0; i--){
            int index = listB.indexOf(seek);
            budgetToPrevPeriodsAlloc = backTrack.get(i).get(index);

            periodBudget = seek - budgetToPrevPeriodsAlloc;

            seek = budgetToPrevPeriodsAlloc;

            spent.put(i, periodBudget);
        }
        spent.put(0, backTrack.get(1).get(listB.indexOf(budgetToPrevPeriodsAlloc)));
        System.out.println(spent);


        List<Integer> production = new ArrayList<>();
        List<Integer> inventory = new ArrayList<>();
        int residualInv = 0;



        for (int i : spent.keySet()){
            for ( int x = 0 ; x <= instance.getProdSpecifics().get("Capacity").get(i); x++){
                if ((prodCost(x) + holdCost(x + residualInv - instance.getProdSpecifics().get("Demand").get(i))) > spent.get(i))
                {
                    if(x>0) {
                        production.add(x - 1);
                        residualInv = ( x - 1 ) + residualInv - instance.getProdSpecifics().get("Demand").get(i);
                    }
                    else
                        {
                        production.add(x );
                        residualInv = ( x ) + residualInv - instance.getProdSpecifics().get("Demand").get(i);

                        }
                    inventory.add(residualInv);
                    break;
                }
            }
        }
        result.put("Production" , production);
        result.put("Inventory" , inventory);


        return result;
    }


    public int getObj() {
        return obj;
    }
}


