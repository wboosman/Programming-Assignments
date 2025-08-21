import ilog.concert.IloException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * Solver coordinates the column generation loop:
 * it alternates between solving the Restricted Master Problem (RMP)
 * and a PricingProblem to generate new duties (columns) until no
 * negative reduced-cost column is found. Tracks runtime statistics
 * and stores the final solution.
 *
 * Author: Wessel Boosman
 */
public class Solver
{
    private List<Double> finalSolution;
    private Duties finalDuties;
    private double finalObjective;
    private long runRMP;
    private long runPricing;

    public Solver() {
        this.finalSolution = new ArrayList<>();
        this.finalDuties = new Duties();
        this.finalObjective =0 ;
        this.runPricing = 0;
        this.runRMP = 0;
    }
    /**
     * Returns cumulative runtime (ms) spent solving RMPs.
     */
    public long getRunRMP() {
        return runRMP;
    }
    /**
     * Returns cumulative runtime (ms) spent in pricing steps.
     */
    public long getRunPricing() {
        return runPricing;
    }
    /**
     * Returns the final decision variable values from the last RMP.
     */
    public List<Double> getFinalSolution() {
        return finalSolution;
    }
    /**
     * Returns the final set of duties (columns) after convergence.
     */
    public Duties getFinalDuties() {
        return finalDuties;
    }
    /**
     * Returns the final objective value of the last RMP solved.
     */
    public double getFinalObjective() {
        return finalObjective;
    }

    /**
     * Runs the column generation loop:
     * - Solve the RMP to get duals and current objective/solution.
     * - Call the pricing problem to find a most negative reduced-cost duty.
     * - If found, add the duty to the RMP and repeat; otherwise, stop.
     * Also accumulates runtime for RMP and pricing phases.
     *
     * @param megaGraph    per-origin graphs used by pricing
     * @param duties       initial pool of duties (columns)
     * @param allTask      all task data by id
     * @param childrenList optional task adjacency (if used by pricing)
     * @param originList   list of origin task ids to price over
     */
    public void solve(HashMap<Integer , Graph> megaGraph, Duties duties, HashMap<Integer, Task> allTask, HashMap<Integer, List<Task>> childrenList, List<Integer> originList){
        try
        {
            int iteration = 0;
            double Obj = 0.0;
            while(true)
            {
                System.out.println("Iteration: " + iteration);
                long start = System.currentTimeMillis();
                RestrictedMasterModel rmp = new RestrictedMasterModel(duties , new ArrayList<>());
                // We solve the model and print the objective value.
                rmp.solve();
                long end = System.currentTimeMillis();
                runRMP = runRMP + (end - start);

                System.out.println("Restricted master problem: " + rmp.getObjective());
                HashMap<String, List<Double>> duals = rmp.getDuals();
                finalObjective = rmp.getObjective();
                finalSolution = rmp.getSolution();
                rmp.cleanup();


                long start2 = System.currentTimeMillis();

                PricingProblem pp = new PricingProblem(megaGraph, allTask, childrenList, originList, duals);

                long end2 = System.currentTimeMillis();
                runPricing = runPricing +(end2 - start2);

                boolean betterDutyFound = false;
                // We solve the model and print the objective value.
                System.out.println("Lowest reduced cost: " + pp.getMinimumRC());
                if (pp.getMinimumRC() < -0.0001)
                {
                    betterDutyFound = true;
                    duties.addDuty(pp.getNewDuty());
                }

                if(!betterDutyFound)
                {
                    break;
                }
                iteration++;
            }
            finalDuties=duties;

        }
        catch (IloException e)
        {
            e.printStackTrace();
        }
    }


}
