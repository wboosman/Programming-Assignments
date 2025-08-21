import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class uses the instance with all the information about the commodities, in particular it uses the generated matrices.
 * Furthermore it uses a Map with all the newly added variables generated in the subproblem of the Dantzig-Wolfe algorithm
 */

public class RMP {
    private Instance instance;
    private IloCplex cplex;
    private Map<Integer, IloNumVar> varMap;
    private Map<Integer, IloRange> constraints;
    private Map<Integer, List<List<Double>>> addedVarInfo;

    public RMP(Instance newInstance, Map<Integer, List<List<Double>>> newAddedVarInfo) throws IloException {
        this.instance = newInstance;
        this.addedVarInfo= newAddedVarInfo;
        this.cplex = new IloCplex();
        this.constraints = new LinkedHashMap<>();
        this.varMap = new LinkedHashMap<>();
        addVariables();
        addConstraints();
        addObjective();
        cplex.setOut(null);

    }

    // Initialize, s equals the capacity, t equals minimal 1
    private void addConstraints() throws IloException {
        for (int i = 0; i < (instance.getNumberOfArcs()+ instance.getNumberOfCommodities()); i++) {
                IloRange constraint;
                IloNumExpr lhs = cplex.constant(0);

                if (i < instance.getNumberOfArcs())
                {
                    IloNumVar variable = varMap.get(i);
                    IloNumExpr term = cplex.prod(1, variable);
                    lhs = cplex.sum(lhs, term);

                    for (int j = 0; j < addedVarInfo.size(); j++) {
                        IloNumVar var = varMap.get(( instance.getNumberOfArcs() + instance.getNumberOfCommodities() + j ));
                        IloNumExpr term2 = cplex.prod(addedVarInfo.get(j).get(0).get(i), var);
                        lhs = cplex.sum(lhs, term2);
                    }
                    constraint = cplex.addEq(lhs, instance.getCapacityOnArcs().get(i));
                }
                else
                    {
                    IloNumVar variable = varMap.get(i);
                    IloNumExpr term = cplex.prod(1, variable);
                    lhs = cplex.sum(lhs, term);
                    for (int j = 0; j < addedVarInfo.size(); j++)
                    {
                        IloNumVar var = varMap.get(( instance.getNumberOfArcs() + instance.getNumberOfCommodities() + j ));
                        IloNumExpr term2 = cplex.prod(addedVarInfo.get(j).get(1).get(i-(instance.getNumberOfArcs())), var);
                        lhs = cplex.sum(lhs, term2);
                    }
                        constraint = cplex.addEq(lhs,1);
                    }
            constraints.put(i, constraint);
                }

        }

    private void addObjective() throws IloException {
        IloNumExpr obj = cplex.constant(0);
        double M = 1000000;
        int dummy=0;

        for (int i = 0; i < varMap.size(); i++) {
            IloNumVar var = varMap.get(i);
            IloNumExpr term;
            if (i< instance.getNumberOfArcs())
            {
                term = cplex.prod(var, dummy);
            }
            else if (i< (instance.getNumberOfArcs()+ instance.getNumberOfCommodities()))
                {
                    term = cplex.prod(var, M);
                }
            else
                {
                    term=cplex.prod(var, -addedVarInfo.get(i - instance.getNumberOfCommodities()- instance.getNumberOfArcs()).get(2).get(0));
                }
            obj = cplex.sum(obj, term);

        }
        cplex.addMinimize(obj);
    }

    private void addVariables() throws IloException {
        for (int i = 0; i < ( instance.getNumberOfArcs() + instance.getNumberOfCommodities()+ addedVarInfo.size()); i++) {
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

    //Returns dual variables
    public List<Double> getDuals() throws IloException
    {
        List<Double> duals = new ArrayList<>();
        for (int i=0; i<constraints.size(); i++  ){
            Double constraintDual = cplex.getDual(constraints.get(i));
            duals.add(constraintDual);
        }
        return duals;
    }

    public void cleanUp() throws IloException {
        cplex.clearModel();
        cplex.end();
    }
}

