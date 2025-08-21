import ilog.concert.IloException;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {
    public static HashMap<Integer, Task> Tasks;
    public static HashMap<Integer , Graph> megaGraph = new HashMap<>();

    public static HashMap<Integer, Task> TasksStartingFromA;
    public static HashMap<Integer, Task> TasksStartingFromB;
    public static HashMap<Integer, Task> TasksEndingAtA;
    public static HashMap<Integer, Task> TasksEndingAtB;
    public static HashMap<Task, List<Task>> TasksNeighbourhoodA;
    public static HashMap<Task, List<Task>> TasksNeighbourhoodB;
    public static List<Integer> originList =  new ArrayList<>();
    public static int maxLabour = 570;
    public static HashMap<Integer, List<Task>> childrenList;

    public static int maxMeanLabour = 700;

    public static void main(String[] args) throws IOException, IloException {
//       -----------------------   GOOD --------------------------------
        long startTotal = System.currentTimeMillis();
        readFileTasks("lib/tasksLarge.tsv");
        specificMaps();
        maxLabourMaps();
        childrenMap(maxLabour);
        constructMegaGraph(originList);
        Duties duties = Duties.construction("lib/tasksLarge.tsv");
        long start = System.currentTimeMillis();
        Solver solve = new Solver();
        solve.solve(megaGraph, duties, Tasks, childrenList, originList);
        long end = System.currentTimeMillis();

        long start2 = System.currentTimeMillis();
        SolveHeuristic heuristic = new SolveHeuristic();
        heuristic.solve(solve.getFinalDuties());
        long end2 = System.currentTimeMillis();

        System.out.println("Running time of pricing Problem: " + solve.getRunPricing());
        System.out.println("Running time of RMP: " +solve.getRunRMP());

        System.out.println("Heuristic objective: " + heuristic.getObjective());
        System.out.println("LP objective: " + solve.getFinalObjective());
        toTxtFile(solve.getFinalSolution(),solve.getFinalDuties());
        toTxtFileH(heuristic.getSolution(),solve.getFinalDuties());

        System.out.println("Total time in solve: " + (end -start));
        System.out.println("Total time in heuristic:" + (end2-start2));
        System.out.println("Total time :" + (end2-startTotal));

        // construct initial duty list --> solve RMP --> get duals --> solve Pricing for every start at A or B and add to map--> Add route with most negative reduced cost


    }


    private static void toTxtFileH(List<Double> finalSolution, Duties finalDuties) throws IOException {
        File output = new File("out2.txt");
        FileWriter fw =new FileWriter(output);
        PrintWriter pw = new PrintWriter(fw);
        for (int i =0; i  <finalSolution.size(); i++) {
            if (finalSolution.get(i) > 0) {
                pw.print(finalSolution.get(i));
                pw.print("  ");
                for (int j=0; j < finalDuties.getDuties().get(i).size(); j++) {
                    if (j==0) {
                        pw.print(finalDuties.getDuties().get(i).get(0));
                        pw.print("  ");
                    } else if(j ==1){
                        pw.print(finalDuties.getDuties().get(i).get(1));
                        pw.print("  ");
                    }
                    else{
                        if (finalDuties.getDuties().get(i).get(j)> 0) {
                            pw.print(j - 2);
                            pw.print("  ");
                        }
                    }
                }
                pw.println();
            }
        }
        pw.close();
    }


    private static void toTxtFile(List<Double> finalSolution, Duties finalDuties) throws IOException {
        File output = new File("out.txt");
        FileWriter fw =new FileWriter(output);
        PrintWriter pw = new PrintWriter(fw);
        for (int i =0; i  <finalSolution.size(); i++) {
            if (finalSolution.get(i) > 0) {
                pw.print(finalSolution.get(i));
                pw.print("  ");
                for (int j=0; j < finalDuties.getDuties().get(i).size(); j++) {
                    if (j==0) {
                        pw.print(finalDuties.getDuties().get(i).get(0));
                        pw.print("  ");
                    } else if(j ==1){
                        pw.print(finalDuties.getDuties().get(i).get(1));
                        pw.print("  ");
                    }
                    else{
                        if (finalDuties.getDuties().get(i).get(j)> 0) {
                            pw.print(j - 2);
                            pw.print("  ");
                        }
                    }
                }
                pw.println();
            }
        }
        pw.close();
    }
    /**
     *
     *
     * @param string file name.
     * @throws FileNotFoundException
     */
    private static void readFileTasks(String string) throws FileNotFoundException {
        Scanner s = new Scanner(new File(string));
        Tasks = new HashMap<>();
        while (s.hasNext()) {
            int one = s.nextInt();
            String two = s.next();
            String three = s.next();
            int four = s.nextInt();
            int five = s.nextInt();
            Task task= new Task(one, four, five, two, three);
            Tasks.put(one, task);
        }
        s.close();
        Task taskStart = new Task(-1, -1, -1,  "S", "S");
        Tasks.put(-1,taskStart );
        Task taskEnd = new Task(1000, -2,-2, "T", "T" );
        Tasks.put(1000, taskEnd);
    }



    private static void specificMaps(){
        HashMap<Integer,Task> MapA = new HashMap<>();
        HashMap<Integer,Task> MapB = new HashMap<>();
        HashMap<Integer,Task> EndMapA = new HashMap<>();
        HashMap<Integer,Task> EndMapB = new HashMap<>();
        for (int i : Tasks.keySet()){
            if (Tasks.get(i).getStartPlatform().equals("A")) {
                MapA.put(i, Tasks.get(i));
                originList.add(i);
            } else if (Tasks.get(i).getStartPlatform().equals("B")) {
                MapB.put(i, Tasks.get(i));
                originList.add(i);
            }
            else if (Tasks.get(i).getStartPlatform().equals("C")){
                originList.add(i);
            }
        }
        for (int i : Tasks.keySet()) {
            if (Tasks.get(i).getEndPlatform().equals("A")) {
                EndMapA.put(i, Tasks.get(i));
            } else if (Tasks.get(i).getEndPlatform().equals("B")) {
                EndMapB.put(i, Tasks.get(i));
            }
        }
        TasksStartingFromA = MapA;
        TasksStartingFromB = MapB;
        TasksEndingAtA = EndMapA;
        TasksEndingAtB = EndMapB;
    }

    private static void maxLabourMaps(){
        HashMap<Task, List<Task>> MapA = new HashMap<>();
        HashMap<Task, List<Task>> MapB = new HashMap<>();

        for (Task i : TasksStartingFromA.values()){
            List<Task> A = new ArrayList<>();
            for (Task j : Tasks.values()) {
                if (j.getStartTime() > i.getEndTime() && j.getEndTime()<= i.getStartTime()+maxLabour) {
                    A.add(j);
                }
            }
            MapA.put(i,A);
        }

        for (Task i : TasksStartingFromB.values()){
            List<Task> B = new ArrayList<>();
            for (Task j : Tasks.values()) {
                if (j.getStartTime() > i.getEndTime() && j.getEndTime()<= i.getStartTime()+maxLabour){
                    B.add(j);
                }
            }
            MapB.put(i,B);
        }
        TasksNeighbourhoodA = MapA;
        TasksNeighbourhoodB = MapB;
    }

    private static List<Task> neighborhood(int time, Task origin){

        List<Task> ListA = new ArrayList<>();
        for (Task i : Tasks.values()){

            if (origin.getEndTime() < i.getStartTime() && i.getEndTime()<= origin.getStartTime()+time) {
                ListA.add(i);
            }
        }
        return ListA;
    }



    public static void childrenMap(int time)
    {
        childrenList = new HashMap<>();
        for (Task i : Tasks.values())
        {
            List<Task> children = new ArrayList<>();
            for (Task j : neighborhood(time,i)) {
                if(j.getStartPlatform().equals(i.getEndPlatform())){
                    children.add(j);
                }
            }
            childrenList.put(i.getTaskNumber(), children);
        }
    }

    public static void constructMegaGraph(List<Integer> originList)
    {
        //Initiate Graph
        int maxLabour = 570;

        for (int i : originList){
            String startPlatform = Tasks.get(i).getStartPlatform();
            LinkedHashMap<Integer, List<Arc>>  graph = new LinkedHashMap<>();
            graph.put(-1, new ArrayList<>());
            graph.put(i, new ArrayList<>());

            List<Integer> neighborhoodOrigin = neighborhood2(maxLabour, Tasks.get(i));
            for (int z : neighborhood2(maxLabour, Tasks.get(i))){
                graph.put(z, new ArrayList<>());
            }
            graph.put(1000, new ArrayList<>());


            //Add Arc from source to start!
            graph.get(-1).add(new Arc(-1, i));

            //If Start begins an ends at same spot, Make sure it gets its own duty!
            if (Tasks.get(i).getStartPlatform().equals(Tasks.get(i).getEndPlatform())){
                graph.get(i).add(new Arc(i, 1000));
            }

            //Create a list of the children of the origin and add Arcs!
            List<Integer> end = new ArrayList<>();

            for (Task t : childrenList.get(i)) {
                graph.get(i).add(new Arc(i, t.getTaskNumber()));
                end.add(t.getTaskNumber());
                //If task i has end station same as start, then there
                //must be the possibility to terminate the duty and return home.
            }

            boolean stop = false;
            while(!stop){
                List<Integer> end2 = new ArrayList<>();
                for(Integer j : end) {
                    if (Tasks.get(j).getEndPlatform().equals(Tasks.get(i).getStartPlatform())) {
                        graph.get(j).add(new Arc(j, 1000));
                    }
                    for (Task t : childrenList.get(j)) {
                        if (neighborhoodOrigin.contains(t.getTaskNumber())) {
                            //If task t in neighborhood of A and a child from one of the tasks in the end list --> add arc
                            graph.get(j).add(new Arc(j, t.getTaskNumber()));
                            end2.add(t.getTaskNumber());

                        }
                        //If the to part of the arc has as ending platform the same as the starting platform --> add arc
                    }
                }
                end = end2.stream()
                        .distinct()
                        .collect(Collectors.toList());

                if (end.isEmpty()){
                    stop = true;
                }
            }
            Graph toAdd = new Graph(graph);
            megaGraph.put(i, toAdd);
        }
    }

    public static List<Integer> neighborhood2(int time, Task task){

        List<Integer> ListA = new ArrayList<>();
        for (Task i : Tasks.values()){

            if (task.getEndTime() < i.getStartTime() && i.getEndTime()<= task.getStartTime()+time) {
                ListA.add(i.getTaskNumber());
            }
        }
        return ListA;
    }


}
