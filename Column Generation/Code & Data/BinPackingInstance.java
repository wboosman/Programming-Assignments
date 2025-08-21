//Packages used.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

//Class that represent an instance of the bin packing problem
public class BinPackingInstance {

    //The size of a bin
    private int binSize;

    //A list of all items, ItemSize[i] is the size of item i in the list (remember, the list starts at i=0)
    private List<Integer> itemSizes;

    //Constructor of BinPackingInstance.
    public BinPackingInstance(int newBinSize, List<Integer> newItemSizes) {
        this.binSize = newBinSize;
        this.itemSizes = newItemSizes;
    }

    //Get the bin size.
    public int getBinSize() {
        return binSize;
    }

    //Get the list of items.
    public List<Integer> getItems() {
        return Collections.unmodifiableList(itemSizes);
    }

    //Construct a bin packing instance by reading it from a txt file of a specific format.
    //InstanceFileName should be of the type: File. Below you can find a code snippet on
    //how to correctly create InstanceFileName.
    public static BinPackingInstance read(File instanceFileName) throws FileNotFoundException {

        //Try to open the file and initialize the use of a so called scanner.
        try (Scanner s = new Scanner(instanceFileName)) {

            //Ignore the first token ("BinSize:").
            s.next();

            //Obtain the size of the bin.
            int binSize = s.nextInt();

            //Ignore the third token ("NumberOfItems:").
            s.next();

            //Obtain the number of items.
            int itemCount = s.nextInt();

            //Ignore the fifth token ("ItemSizes:")
            s.next();

            //Go over all items and obtain their sizes.
            List<Integer> itemSizes = new ArrayList<>();
            for (int i = 0; i < itemCount; i++) {
                itemSizes.add(s.nextInt());
            }

            //Create instances with the obtained bin size and list of item sizes.
            BinPackingInstance result = new BinPackingInstance(binSize, itemSizes);
            return result;
        }

    }
}
