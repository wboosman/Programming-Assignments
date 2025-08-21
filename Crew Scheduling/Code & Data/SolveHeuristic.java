import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
/**
 * SolveHeuristic applies a simple heuristic on top of the Restricted Master Problem (RMP).
 * It repeatedly solves the LP relaxation of the RMP, then fixes the variable with the
 * largest fractional value (≤ 1.0) to 1. This process continues until no variable can
 * be fixed, at which point it returns a feasible integer solution.
 *
 * Author: Wessel Boosman
 */
public class SolveHeuristic {
    private List<Double> solution ;
    private Double objective;

    public SolveHeuristic() {
        this.solution = new ArrayList<>();
        this.objective = 0.0;
    }

    public Double getObjective() {
        return objective;
    }

    public void solve(Duties duties) {
        try {
            int iteration = 0;
            List<Integer> fix = new ArrayList<>();
            while(true) {
                System.out.println("Iteration: " + iteration);
                int indexLargestVar = 0;
                List<Double> sol;
                double obj;
                RestrictedMasterModel rmp = new RestrictedMasterModel(duties,fix);
                rmp.solve();
                sol = rmp.getSolution();
                obj = rmp.getObjective();
                rmp.cleanup();

                int maxValueIndex = -1;
                double maxValue = 0.0;
                for (int i = 0, n = sol.size(); i < n; ++i) {
                    Double value = sol.get(i);
                    if ((value <= 1.0 &&  value > maxValue && !fix.contains(i))) {
                        maxValue = value;
                        maxValueIndex = i;
                    }
                }
                if (maxValue > 0.0) {
                    fix.add(maxValueIndex);
                }
                else {
                    objective = obj;
                    solution = sol;
                    break;
                }
                iteration++;
                System.out.println("The duty that is fixed: " + maxValueIndex + "With an LP value of: " + maxValue );
            }
        }
        catch (IloException e) {
            e.printStackTrace();
        }
    }

    public List<Double> getSolution() {
        return solution;
    }
}




