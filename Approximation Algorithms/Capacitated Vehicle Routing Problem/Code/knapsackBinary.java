/**
 * This class contains the constructor of the CPLEX model.
 * 
 * @author XXXXXXmk Matthijs XXXXXXX
 * @author 589273wb	Wessel Boosman
 */
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class knapsackBinary {
	public IloCplex cplex;
	private int n;
	private int Q;
	private int[] values;
	private int[] weights;
	
	private IloNumVar[] x;
	
	/**
	 * This is the constructor of the CPLEX model.
	 */
	public knapsackBinary(int n, int Q, int[] values, int[] weights) throws IloException {
		 this.n = n;
		 this.Q = Q;
		 this.values = values;
		 this.weights = weights;
		 
		 cplex = new IloCplex();
		 
		 addVariables();
		 addConstraint();
		 addObjective();
		 
		 cplex.setOut(null);
		 cplex.exportModel("1a.lp");
		 
		 solve();
		 cleanup();
	}

	/**
	 * This method adds the x-variables to the problem.
	 * @throws IloException
	 */
	private void addVariables() throws IloException {
		x = new IloNumVar[n];
		for(int i=0; i<n; i++) {
			x[i] = cplex.boolVar();
		}
		
	}

	/**
	 * This method adds the capacity constraint to the problem.
	 * @throws IloException
	 */
	private void addConstraint() throws IloException {
		IloNumExpr lhs = cplex.constant(0);
		IloNumExpr rhs = cplex.constant(Q);
		for(int i=0; i<n; i++) {
			lhs = cplex.sum(lhs, cplex.prod(weights[i], x[i]));
		}
		cplex.addLe(lhs, rhs);
	}

	/**
	 * This method adds the objective to the problem (to maximize profits).
	 * @throws IloException
	 */
	private void addObjective() throws IloException {
		IloNumExpr obj = cplex.constant(0);
		for(int i=0; i<n; i++) {
			obj = cplex.sum(obj, cplex.prod(values[i], x[i]));
		}
		cplex.addMaximize(obj);
	}
	
	/**
	 * This method cleans the model.
	 * @throws IloException
	 */
	public void cleanup() throws IloException {
		cplex.clearModel();
		cplex.end();
	}

	/**
	 * This method solves the model.
	 * @throws IloException
	 */
	public void solve() throws IloException {
		cplex.setParam(IloCplex.Param.TimeLimit, 900);
		cplex.solve();
		System.out.println(cplex.getObjValue());
	}
}
