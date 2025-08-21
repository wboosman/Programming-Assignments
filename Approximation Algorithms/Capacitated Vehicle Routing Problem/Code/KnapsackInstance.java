/**
 * This class creates a Knapsack instance.
 *
 * @author xxxxxxmk Matthijs xxxxxxxx
 * @author 589273wb	Wessel Boosman
 */

//Packages used.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class KnapsackInstance {

    //The number of items
    private int numItems;

    //The capacity
    private int capacity;

    //A list of all the weights of items, ItemWeight[i] is the weight of item i in the list (remember, the list starts at i=0)
    private int[] itemWeight;

    //A list of all the profits of items, ItemProfit[i] is the profit of item i in the list (remember, the list starts at i=0)
    private int[] itemProfit;

    //Constructor of BinPackingInstance.
    public KnapsackInstance(int newNumItems, int newCapacity, int[] newItemWeights, int[] newItemProfits) {
        this.numItems = newNumItems;
        this.capacity = newCapacity;
        this.itemWeight = newItemWeights;
        this.itemProfit = newItemProfits;
    }

    //Getter functions
    public int getNumItems() {
        return numItems;
    }

    public int getCapacity() {
        return capacity;
    }

    public int[] getItemProfit() {
        return itemProfit;
    }

    public int[] getItemWeight() {
        return itemWeight;
    }

    /**
     *
     * @param instanceFileName read the file
     * @return A Knapsack instance with all relevant information
     * @throws FileNotFoundException
     */
    public static KnapsackInstance read(File instanceFileName) throws FileNotFoundException {
        try (Scanner s = new Scanner(instanceFileName)) {

            int numItems = s.nextInt();

            int capacity = s.nextInt();

            int[] itemProfit = new int[numItems];
            for (int i = 0; i < numItems; i++) {
                itemProfit[i] = s.nextInt();
            }

            int[] itemWeight = new int[numItems];
            for (int i = 0; i < numItems; i++) {
                itemWeight[i] = s.nextInt();
            }

        KnapsackInstance result = new KnapsackInstance(numItems, capacity, itemWeight, itemProfit);
        return result;
        }
    }
}
