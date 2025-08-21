import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IloException {
        try {
            // Read the bin packing instance
            ProductionInstance pi = ProductionInstance.read(new File("lib/instance_3.txt"));

//// ------------------------------------ ILP -----------------------------------------
            long start =  System.currentTimeMillis();
            ModelILP model =new  ModelILP(pi);
            model.solve();

            System.out.println("Objective MILP:" + model.getObjective());
            System.out.println("Solution MILP: " + model.getSolution());
            HashMap<String, List<Integer>> myMap= new HashMap<>();
            List<Integer> myList = new ArrayList<>();

            for (double d: model.getSolution().get("Production") ){
                int i = (int) Math.round(d);
                myList.add(i);
            }
        myMap.put("Production" , myList);
            myList = new ArrayList<>();
            for (double d: model.getSolution().get("Inventory") ){
                int i = (int) Math.round(d);
                myList.add(i);
            }
            myMap.put("Inventory" , myList);
            toTxtFile(myMap);
            System.out.println(model.getSpentPerPeriod(model.getSolution()));


// --------------------------------------- Heuristic ----------------------------------
            long start =  System.currentTimeMillis();
            Heuristic heuristic = new Heuristic(pi);
            toTxtFile(heuristic.heuristicSolution());
            System.out.println(heuristic.heuristicSolution());


            System.out.println("Objective :" + heuristic.getHeuristicObjective());
            long end =  System.currentTimeMillis();
            System.out.println("Run Time: " + (end -start));
            System.out.println("Objective :" + heuristic.getHeuristicObjective());


            int x=0;
            for (int x = 0 ; x< pi.getNumberPeriods(); x++){
                System.out.println(x);

            }
            System.out.println(pi.getNumberPeriods());


// ----------------------------------------------- DP -------------------------------------
           long start = System.currentTimeMillis();
           DynamicProgram dp = new DynamicProgram(pi, 1850);


           dp.maxInventoryDP(dp.initialization());

           System.out.println(dp.getObj());
           long end = System.currentTimeMillis();
           System.out.println(end-start);
           System.out.println(dp.DPSolution());
           System.out.println(d.getDpVars());
// ----------------------------------------------- DP WITH FACTOR -------------------------------------
           int F= 16;
           long start = System.currentTimeMillis();
           DPMEFACTORS dp = new DPMEFACTORS(pi, 52876,F );
           dp.maxInventoryDP(dp.initialization());
           long end = System.currentTimeMillis();
           System.out.println("Objective, first b for which final inventory  >= 0 : " + dp.getObj());
           System.out.println("Solution: " + dp.DPSolution2() );

           System.out.println("Running time: " + (end-start));
           System.out.println("Lower Bound: " + (dp.getObj()-(pi.getNumberPeriods()*F)));
           toTxtFile(dp.DPSolution2());
           List<Integer> myList = Arrays.asList(0,5,10,15,20,25,30,35);
           System.out.println(dp.getA(24000));

           System.out.println(dp.initialization());


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void toTxtFile(HashMap<String, List<Integer>> input) throws IOException {
        File output = new File("solution_heur.txt");
        FileWriter fw = new FileWriter(output);
        PrintWriter pw = new PrintWriter(fw);
        for (int i = 0; i < input.get("Production").size(); i++) {
            for (String s : input.keySet()) {
                if (s.equals("Production")) {
                    pw.print(input.get("Production").get(i));
                    pw.print("  ");
                } else {
                    pw.print(input.get("Inventory").get(i));
                    pw.print("  ");
                }
            }
            pw.println();
        }
        pw.close();
    }

}

