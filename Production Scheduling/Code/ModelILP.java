import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ModelILP formulates and solves a mixed-integer production planning model in CPLEX.
 * Decision variables per period t:

 *   y_t ∈ {0,1}: 1 if production is started in period t (fixed production cost applies)</li>
 *   x_t ≥ 0: production quantity in period t</li>
 *   z_t ∈ {0,1}: 1 if inventory is held in period t (fixed holding cost applies)</li>
 *   i_t ≥ 0: inventory level at end of period t</li>

 * Constraints enforce inventory flow balance, capacity coupling ,
 * and a linking constraint for inventory .
 * Objective minimizes fixed/variable production and holding costs over the horizon.
 *
 * Author: Wessel Boosman
 */
public class ModelILP {
    private ProductionInstance instance;
    private IloCplex cplex;
    private Map<Integer, IloNumVar> varMapY;
    private Map<Integer, IloNumVar> varMapZ;
    private Map<Integer, IloNumVar> varMapX;
    private Map<Integer, IloNumVar> varMapI;



    public ModelILP(ProductionInstance instance) throws IloException {
        this.instance = instance;
        this.cplex = new IloCplex();
        this.varMapY = new HashMap<>();
        this.varMapZ = new HashMap<>();
        this.varMapX = new HashMap<>();
        this.varMapI = new HashMap<>();

        addVariables();
        addContinuityConstraints();
        addCapacityConstraints();
        addInventoryConstraints();
        addObjective();
        cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, 0.0000);
        cplex.setParam(IloCplex.Param.Threads, 1);
        cplex.setParam(IloCplex.Param.TimeLimit, 300);
        cplex.exportModel("model1.lp");
        cplex.setOut(null);
        System.out.println(cplex.getVersion());

    }

    private void addInventoryConstraints() throws IloException {
        for (int i =0; i<instance.getNumberPeriods(); i++){
            IloNumVar varI = varMapI.get(i);
            IloNumVar varZ = varMapZ.get(i);
            int demand = 0;
            for(int j= i+1 ; j < instance.getNumberPeriods(); j++ ){
                demand = demand + instance.getProdSpecifics().get("Demand").get(j);
            }
            IloNumExpr rhs = cplex.prod(demand, varZ);
            String a = "I" + i;
            cplex.addLe(varI, rhs, a);
        }
    }


    private void addCapacityConstraints() throws IloException {
        for (int i =0; i<instance.getNumberPeriods(); i++){
            IloNumVar varX = varMapX.get(i);
            IloNumVar varY = varMapY.get(i);
            int capacity   = instance.getProdSpecifics().get("Capacity").get(i);
            String a = "C" + i;
            IloNumExpr rhs = cplex.prod(varY, capacity);

            cplex.addLe(varX, rhs, a);
        }
    }

    private void addObjective() throws IloException {

            // Initialize the objective sum to 0
            IloNumExpr obj = cplex.constant(0);
//        for (int i = 0; i < instance.getNumPackings(); i++)
            for (int i =0; i<instance.getNumberPeriods(); i++)
            {
                IloNumVar varY = varMapY.get(i);
                IloNumVar varZ = varMapZ.get(i);
                IloNumVar varX = varMapX.get(i);
                IloNumVar varI = varMapI.get(i);

                IloNumExpr term1 = cplex.prod(varY, instance.getFixProduction());
                IloNumExpr term2 = cplex.prod(varX, instance.getVarProduction());
                IloNumExpr term3 = cplex.prod(varZ, instance.getFixHolding());
                IloNumExpr term4 = cplex.prod(varI, instance.getVarHolding());


                // Take the sum of bin as the objective value
                obj = cplex.sum(obj, term1, term2, term3, term4);
            }
            // Add the obj expression as a minimization objective
            cplex.addMinimize(obj);
        }

    private void addContinuityConstraints() throws IloException {
        for (int i = 0; i< instance.getNumberPeriods(); i++){
            if (i==0){
                IloNumVar varI =varMapI.get(i);
                IloNumVar varX =varMapX.get(i);
                IloNumExpr lhs = cplex.sum(varI, instance.getProdSpecifics().get("Demand").get(0));
                String a = "B" + i;
                cplex.addLe(lhs, varX, a);
                cplex.addGe(lhs,varX, a);
            }
            else {
                IloNumVar varX =varMapX.get(i);
                IloNumVar varPrevI= varMapI.get(i-1);
                IloNumExpr lhs = cplex.sum(varPrevI, varX);
                IloNumVar varY = varMapI.get(i);
                IloNumExpr rhs = cplex.sum(varY, instance.getProdSpecifics().get("Demand").get(i));

                 String a = "B" + i;
                 cplex.addLe(lhs, rhs, a);
                 cplex.addGe(lhs, rhs, a);

            }
        }
    }

    private void addVariables() throws IloException {
        for (int i =0; i<instance.getNumberPeriods(); i++)
        {
            IloNumVar var1 = cplex.intVar(0,1);
            IloNumVar var2 = cplex.intVar(0,1);

            varMapY.put(i, var1);
            varMapZ.put(i, var2);

            IloNumVar varA = cplex.intVar(0,Integer.MAX_VALUE);
            IloNumVar varB = cplex.intVar(0,Integer.MAX_VALUE);

            varMapI.put(i,varA);
            varMapX.put(i,varB);
        }
    }


    public void solve() throws IloException {
        cplex.solve();
    }

    public void cleanup() throws IloException
    {
        cplex.clearModel();
        cplex.end();
    }
    public double getObjective() throws IloException
    {
        return cplex.getObjValue();
    }
    public double getLowerBound() throws IloException
    {
        return cplex.getBestObjValue();
    }

    public HashMap<String, List<Double>> getSolution() throws IloException
    {
        List<Double> production  = new ArrayList<>();
        List<Double> inventory  = new ArrayList<>();
        HashMap<String, List<Double>> result = new HashMap<>();
        for (int i : varMapX.keySet())
        {
            IloNumVar var = varMapX.get(i);
            double value = cplex.getValue(var);
            production.add(value);
        }

        for (int i : varMapI.keySet())
        {
            IloNumVar var = varMapI.get(i);
            double value = cplex.getValue(var);
            inventory.add(value);
        }

        result.put("Production", production);
        result.put("Inventory", inventory);
        return result;
    }

    public HashMap<Integer, Integer> getSpentPerPeriod(HashMap<String, List<Double>> sol){
        HashMap<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < instance.getNumberPeriods(); i++) {
            int cost =0;
            for (String s : sol.keySet()) {
                if(s.equals("Production")) {
                    cost += prodCost((int) Math.round(sol.get(s).get(i)));
                }
                else
                    {
                        cost+= holdCost((int) Math.round(sol.get(s).get(i)));
                    }
                }
            result.put(i, cost);
            }
        return result;
        }


    public Integer prodCost(int x) {
        int cost = 0;

        if (x > 0) {
            cost = instance.getFixProduction() + ( x * instance.getVarProduction() );
        }
        return cost;
    }

    public Integer holdCost(int i) {
        int cost = 0;

        if (i > 0) {
            cost = instance.getFixHolding() + ( i * instance.getVarHolding() );
        }
        return cost;
    }

}
