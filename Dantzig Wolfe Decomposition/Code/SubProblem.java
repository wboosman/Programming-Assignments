import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.*;

/**
 * This class uses the Instance with information about the commodity network, the dual variables from the RMP,
 * the Map with information about the already generated variables by the algorithm, the iteration count and the activity.
 * This information is translated into a subproblem, nescessary to generate new extreme points for the commodities.
 */

public class SubProblem {
    private  IloCplex cplex;
    private List<Double> dualVars;
    private Instance instance;
    private Map<Integer, List<List<Double>>> addedVarInfo;
    private Map<Integer, IloNumVar> varMap;
    private Map<Integer, IloRange> constraints;
    private RestrictedMasterProblem RMP;
    private Integer numberSP;
    private Integer iteration;



    public SubProblem(Instance instance, List<Double> newDualVars, Map<Integer, List<List<Double>>> newVarInfo, Integer newNumberSP, Integer newIteration) throws IloException {
        this.cplex = new IloCplex();
        this.dualVars = newDualVars;
        this.instance= instance;
        this.varMap = new LinkedHashMap<>();
        this.constraints = new LinkedHashMap<>();
        this.addedVarInfo= newVarInfo;
        this.numberSP = newNumberSP;
        this.iteration=newIteration;


        addVariables();
        addConstraint();
        addObjective();
//        cplex.exportModel("modelSP.lp");
        cplex.setOut(null);



    }

    private void addObjective() throws IloException {
        List<Double> objCoeffSP = new ArrayList<>();
        for (int i = 0; i< instance.getNumberOfArcs(); i++){
            objCoeffSP.add(dualVars.get(i)-instance.getCostOnArcs().get(i));
        }

        IloNumExpr obj = cplex.constant(0);
        for (int i = 0; i < instance.getNumberOfArcs(); i++) {
            //For the objective function you only need the last three variables.
            //The first ten are reserved for the capacity constraints
            IloNumVar var = varMap.get(i);
            IloNumExpr term = cplex.prod(var, objCoeffSP.get(i));
            obj = cplex.sum(obj, term);
        }
        cplex.addMaximize(obj);
    }

    private void addConstraint() throws IloException {
        for (int i = 0; i < instance.getNumberOfNodes(); i++) {
            IloRange constraint;
            IloNumExpr lhs = cplex.constant(0);
            for (int j = 0; j< instance.getNumberOfArcs(); j++ ){
                IloNumVar variable = varMap.get(j);
                IloNumExpr term = cplex.prod(variable, instance.getD().get(i).get(j));
                lhs=cplex.sum(lhs, term);
            }
            constraint = cplex.addEq(lhs, instance.getB().get(numberSP).get(i));
            constraints.put(i, constraint);
        }
    }

    private void addVariables() throws IloException {
        for (int i = 0; i < (instance.getNumberOfArcs()); i++) {
            IloNumVar var = cplex.numVar(0.0, Double.MAX_VALUE);
            varMap.put(i, var);
        }
    }


    public void solve() throws IloException
    {
        cplex.solve();
    }

    public List<Double> getSolution() throws IloException {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < varMap.size(); i++)
        {
            IloNumVar var = varMap.get(i);
            double value = cplex.getValue(var);
            result.add(value);
        }
        return result;
    }

    public double getObjective() throws IloException
    {
        return cplex.getObjValue();
    }

    public Map<Integer, List<List<Double>>> getModifiedVarInfo() throws IloException {
        ArrayList<Double> dummy = new ArrayList<>(Collections.nCopies(instance.getNumberOfCommodities(), 0.0));
        List<Double> objectiveValue = new ArrayList<>();
        List<Double> constraintColumnArcs;
        List<Double> constraintColumnCommodities;
        List<Double> extremePoint;

        objectiveValue.add(getObjective());
        extremePoint=getSolution();
        constraintColumnArcs=getSolution();
        dummy.set((numberSP-1),1.0);
        constraintColumnCommodities=dummy;

        List<List<Double>> newVariableInfo= new ArrayList<>();
        newVariableInfo.add(constraintColumnArcs);
        newVariableInfo.add(constraintColumnCommodities);
        newVariableInfo.add(objectiveValue);
        newVariableInfo.add(extremePoint);
        addedVarInfo.put(iteration, newVariableInfo);
        return addedVarInfo;
    }

    public double getReducedCost() throws IloException {
        return ((-(dualVars.get(instance.getNumberOfArcs()+(numberSP-1)))) - getObjective());
    }

    public void cleanUp() throws IloException {
        cplex.clearModel();
        cplex.end();
    }
}
