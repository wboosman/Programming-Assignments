import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Class reads the file with commodities and converst them into matrices as in the lecture slides.
 */

public class Instance {
    private int numberOfNodes;
    private int numberOfArcs;
    private int numberOfCommodities;
    private List<Integer> costOnArcs;
    private List<Integer> capacityOnArcs;
    private List<ArrayList<Integer>> A;
    private List<ArrayList<Integer>> D;


    private HashMap<Integer, ArrayList<Integer>> b;


    public Instance(int newNumberOfNodes, int newNumberOfArcs, int newNumberofCommodities,
                    List<Integer> newCostOnArcs, List<Integer> newCapacityOnArcs,
                    List<ArrayList<Integer>> newD,
                    HashMap<Integer, ArrayList<Integer>> newb) {
        this.numberOfNodes = newNumberOfNodes;
        this.numberOfArcs = newNumberOfArcs;
        this.numberOfCommodities = newNumberofCommodities;
        this.costOnArcs = newCostOnArcs;
        this.capacityOnArcs = newCapacityOnArcs;
        this.b = newb;
        this.D = newD;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getNumberOfArcs() {
        return numberOfArcs;
    }

    public int getNumberOfCommodities() {
        return numberOfCommodities;
    }

    public List<Integer> getCostOnArcs() {
        return costOnArcs;
    }

    public List<Integer> getCapacityOnArcs() {
        return capacityOnArcs;
    }


    public  List<ArrayList<Integer>> getD() {
        return D;
    }

    public HashMap<Integer, ArrayList<Integer>> getB() {
        return b;
    }

    public static Instance read(File instanceFileName) throws FileNotFoundException {
        //Try to open the file and initialize the use of a so called scanner.
        try (Scanner s = new Scanner(instanceFileName)) {

            //Ignore the first token ("NumberOfNodes:").
            s.next();

            //Obtain the size of the bin.
            int numberOfNodes = s.nextInt();

            //Ignore the third token ("NumberOfArcs:").
            s.next();

            //Obtain the number of items.
            int numberOfArcs = s.nextInt();

            //Ignore the fifth token ("NumberOfCommodities:")
            s.next();

            int numberOfCommodities = s.nextInt();

            //Ignore the fifth token ("StartNode_EndNode_Cost_Capacity:")
            s.next();

            //Go over all items and obtain their sizes.
            ArrayList<Integer> startNodes = new ArrayList<>();
            ArrayList<Integer> endNodes = new ArrayList<>();
            ArrayList<Integer> costOnArcs = new ArrayList<>();
            ArrayList<Integer> capacityOnArcs = new ArrayList<>();

            for (int i = 0; i < numberOfArcs; i++)
            {
                startNodes.add(s.nextInt());
                endNodes.add(s.nextInt());
                costOnArcs.add(s.nextInt());
                capacityOnArcs.add(s.nextInt());
            }
            //Ignore the  token ("Origin_Destination_FlowDemand::")
            s.next();

            List<Integer> sourceCommodity= new ArrayList<>();
            List<Integer> sinkCommodity = new ArrayList<>();
            List<Integer> demandCommodity= new ArrayList<>();
            for (int i = 0; i < numberOfCommodities; i++)
            {
                sourceCommodity.add(s.nextInt());
                sinkCommodity.add(s.nextInt());
                demandCommodity.add(s.nextInt());
            }

            HashMap<Integer, ArrayList<Integer>> b = new HashMap<>();
            b.put(0, capacityOnArcs);
            int a = 1;
            for (int i = 0; i < (numberOfCommodities); i++)
            {
                ArrayList<Integer> dummy = new ArrayList<Integer>(Collections.nCopies(numberOfNodes, 0));
                dummy.set((sourceCommodity.get(i) - a), demandCommodity.get(i));
                dummy.set((sinkCommodity.get(i) - a), -demandCommodity.get(i));
                b.put(i+1, dummy);
            }

            //Make d
            List<ArrayList<Integer>> D = new ArrayList<ArrayList<Integer>>();
//            HashMap<Integer, ArrayList<Integer>> d = new HashMap<>();

            for(int i = 0; i<numberOfNodes; i++)
            {
                ArrayList<Integer> dummy = new ArrayList<Integer>(Collections.nCopies(numberOfArcs, 0));
                D.add(dummy);
            }
//
            for (int i=0; i<startNodes.size() ;i++)
            {
                D.get(startNodes.get(i) - 1).set(i, 1);
            }
            for (int i=0; i<endNodes.size() ;i++)
            {
                D.get(endNodes.get(i) - 1).set(i, -1);
            }

            Instance result = new Instance(numberOfNodes, numberOfArcs, numberOfCommodities, costOnArcs, capacityOnArcs,  D, b);
            return result;
        }
    }
}
