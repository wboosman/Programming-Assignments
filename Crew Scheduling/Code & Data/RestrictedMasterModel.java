import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.*;

public class RestrictedMasterModel {
    /**
     * RestrictedMasterModel builds and solves a restricted master problem (RMP) for a column
     * generation / set-cover-like formulation using CPLEX. Variables represent duty selections,
     * constraints ensure each task is covered, and an aggregate labour constraint bounds average labour.
     *
     * Author: Wessel Boosman
     */
    private IloCplex cplex;
    private Map<Integer, IloNumVar> varMap;
    private Map<Integer, IloRange> taskCompletionConstraints;
    private IloConstraint maxMeanLabourConstraint;
    private Duties duties;
    private final int numberTasks;
    private List<Integer> fixedVars;

    public RestrictedMasterModel(Duties duties , List<Integer> fixedVars) throws IloException {
        /**
         * Constructs the RMP:
         * - creates decision variables (some optionally fixed),
         * - adds task completion constraints,
         * - adds max mean labour constraint,
         * - sets the minimization objective on duty costs.
         *
         * @param duties    duties matrix (row = duty, columns: [price, length, task-incidence...])
         * @param fixedVars indices of variables/duties to fix at 1.0
         * @throws IloException if CPLEX fails to create model objects
         */
        this.duties = duties;
        this.cplex = new IloCplex();
        this.varMap = new LinkedHashMap<>();
        this.taskCompletionConstraints = new LinkedHashMap<>();
        this.numberTasks = duties.getDuties().get(0).size()-2;
        this.fixedVars = fixedVars;
        addVariables();
        addTasksCompletionConstraints();
        addMaxMeanLabourConstraint();
        addObjective();
        cplex.setOut(null);
    }

    public int getNumberTasks() {
        return numberTasks;
    }

    private void addMaxMeanLabourConstraint() throws IloException {
        IloNumExpr lhs = cplex.constant(0);
        IloNumExpr rhs = cplex.constant(0);
        IloNumExpr maxMeanLabour = cplex.constant(432);
        for (int i =0; i<duties.getNumberDuties(); i++){
            IloNumExpr dutyLength = cplex.constant(duties.getDuties().get(i).get(1));
            IloNumVar var = varMap.get(i);
            IloNumExpr prodLHS = cplex.prod(dutyLength,var);
            IloNumExpr prodRHS = cplex.prod(maxMeanLabour, var);
            lhs = cplex.sum(lhs,prodLHS);
            rhs = cplex.sum(rhs, prodRHS);
        }
        IloConstraint constraint = cplex.addLe(lhs,rhs);
//        cplex.addLe(lhs,rhs);
        maxMeanLabourConstraint = constraint;
    }

    private void addTasksCompletionConstraints() throws IloException {
        for (int j = 2; j < numberTasks + 2; j++) {
            IloNumExpr lhs = cplex.constant(0);
            for (int i : varMap.keySet())
            {
                IloNumVar var = varMap.get(i);
                // Take the product of the decision variable and the item weight
                IloNumExpr term = cplex.prod(duties.getDuties().get(i).get(j), var);
                // Add the term to the left hand side summation
                lhs = cplex.sum(lhs, term);
            }
            // Add the constraint lhs <= capacity to the model
            IloRange constraint = cplex.addGe(lhs, 1);
            taskCompletionConstraints.put(j - 2,constraint);
        }
    }


    private void addVariables() throws IloException {
        for (int i =0; i<duties.getNumberDuties(); i++)
        {
            IloNumVar var;
            if (fixedVars.contains(i)){
                var = cplex.numVar(1.0, 1.0);
            }
            else {
                var = cplex.numVar(0, Double.MAX_VALUE);
            }
            varMap.put(i, var);
        }
    }

    private void addObjective() throws IloException
    {
        // Initialize the objective sum to 0
        IloNumExpr obj = cplex.constant(0);
        for (int i =0; i< duties.getNumberDuties(); i++)
        {
            IloNumExpr cost = cplex.constant(duties.getDuties().get(i).get(0));
            IloNumVar var = varMap.get(i);
            IloNumExpr prod= cplex.prod(var, cost);
            // Take the sum of bin as the objective value
            obj = cplex.sum(obj, prod);
        }
        // Add the obj expression as a minimization objective
        cplex.addMinimize(obj);
    }

    public void cleanup() throws IloException{
        cplex.clearModel();
        cplex.end();
    }

    public double getObjective() throws IloException{
        return cplex.getObjValue();
    }

    public HashMap<String,  List<Double>> getDuals() throws IloException
    {
        HashMap<String,  List<Double>> dualMap = new HashMap<>();
        List<Double> duals = new ArrayList<>();
        for (int i=0; i<taskCompletionConstraints.size();i++  ){
            Double constraintDual = cplex.getDual(taskCompletionConstraints.get(i));
            duals.add(constraintDual);
        }
        dualMap.put("presenceConstraints",duals);

        dualMap.put("maxMeanLabourConstraint",  Collections.singletonList(-cplex.getDual((IloRange) maxMeanLabourConstraint)));
        return dualMap;
    }

    public void solve() throws IloException
    {
        cplex.solve();
    }

    public List<Double> getSolution() throws IloException
    {
        List<Double> result = new ArrayList<>();
        for (int i : varMap.keySet())
        {
            IloNumVar var = varMap.get(i);
            double value = cplex.getValue(var);
            result.add(value);
        }
        return result;
    }
}
