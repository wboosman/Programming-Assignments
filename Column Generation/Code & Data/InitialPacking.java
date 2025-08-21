
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Class that represent an instance of the bin packing problem - this instance is a map with item, and an array of length N which indicates if packing i includes this item.
 */

public class InitialPacking {
    // number of packings
    private int numPackings;
    //number of items per packing
    private int numItems;
    //A HashMap with keys and Packings
    private HashMap<Integer, ArrayList<Integer>> test;

    /**
     * Constructor of the Initial Packing class
     * @param newnumPackings number of packings
     * @param newNumItems number of items
     * @param newtest  A map with item, and an array of length N which indicates if packing i includes this item.
     */
    public InitialPacking(int newnumPackings, int newNumItems, HashMap<Integer, ArrayList<Integer>> newtest) {
        this.numPackings = newnumPackings;
        this.test = newtest;
        this.numItems= newNumItems;
    }

    //Get the Map of initial Packings.
    public HashMap<Integer, ArrayList<Integer>> getInitialPackings() {
        return test;
    }

    // method for updating the instance with a new packing
    public HashMap<Integer, ArrayList<Integer>> updatePackings(List<Integer> newSolution)
    {
        int in =1;
        int out =0;
        for (int i = 0; i < numItems; i++)
        {
            if (newSolution.contains(i)){
                test.get(i).add(in);
        }
            else {
                test.get(i).add(out);
            }

        }
        return test;
    }

    //Get the number of packings.
    public int getNumPackings() {
            return test.get(0).size();

    }

    //Get the number of items.
    public int getNumItems() {
        return numItems;
    }


    //Construct a initial packing instance by reading it from a txt file of a specific format.
    //InstanceFileName should be of the type: File. Below you can find a code snippet on
    //how to correctly create InstanceFileName.
    public static InitialPacking read(File instanceFileName) throws FileNotFoundException {
        //Try to open the file and initialize the use of a so called scanner.
        try (Scanner s = new Scanner(instanceFileName)) {

            //Ignore the first token ("BinSize:").
            s.next();

            //Obtain the size of the bin.
            int itemCount = s.nextInt();

            //Ignore the third token ("NumberOfItems:").
            s.next();

            //Obtain the number of items.
            int packingCount = s.nextInt();

            //Ignore the fifth token ("ItemSizes:")
            s.next();

            //Go over all items and obtain their sizes.
            HashMap<Integer, ArrayList<Integer>> mapPossiblePackings = new HashMap<>();
            for (int j = 0; j < itemCount; j++)
            {
                ArrayList<Integer> dummyArray = new ArrayList<>();
                for (int i = 0; i < packingCount; i++)
                {
                    dummyArray.add(s.nextInt());
                }
                mapPossiblePackings.put(j, dummyArray);
            }
            InitialPacking result = new InitialPacking(packingCount, itemCount, mapPossiblePackings);
            return result;
        }
    }
}

