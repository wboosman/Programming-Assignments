package GA;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

/**
 * Main class for the 3D-BPP. Please note to use the provided items.csv file as
 * the csv has been altered to be ; separated. When using a different csv the
 * read method will not be able to read in the data.
 *
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Map<Integer, List<Item>> orders = readData(new File("data/orders.csv"), new File("data/items.csv"));

        int[] crateData = readCrateData(new File("data/crate_properties.csv"));

        Bin crate = new Bin(0, crateData[0], crateData[1], crateData[2], crateData[3]);

        //System.out.println(crate);
        //System.out.println(orders.get(6));

        // According to my calculations (Xander) the addition of the below 3 items to
        // the crate should result in 9 EMS at the end, which the program also outputs.
        // Please check either by hand or use
        // https://xserver2-dashboard.cloud.ptvgroup.com/dashboard/Content/Showcases/LoadingOptimization/InteractiveVisualization/index.htm
        // as a visualisation tool to visualise all the EMS.
        //crate.addItem(orders.get(6).get(11), 0, 0, 0, 1); // 3 EMS
        //System.out.println(crate);
        //crate.addItem(orders.get(6).get(17), 0, 0, 61, 6); // 6 EMS
        //System.out.println(crate);
        //crate.addItem(orders.get(6).get(3), 0, 0, 151, 2);
        //System.out.println(crate);

        // TODO:
        // Placement Heuristic 2 / 2nd Decoder for the chromosomes
        // Fitness Function for the chromosomes based on the decoder 2

        BRKGA GA = new BRKGA(orders.get(381), crateData, 10, 0.1, 0.25, 0.6, 3);
//        System.out.println(GA.solve());
        List<Bin> bins = GA.decoder(GA.solve());
        System.out.println(bins.get(0).getItemLocation());
        PrintStream out = new PrintStream(new FileOutputStream("test.txt"));
        FileWriter out2 = new FileWriter("test2.csv");
        out2.append("Bin,item,x3,y3,z3,x4,y4,z4");
        out2.append("\n");
        for (Bin bin : bins) {
            out.println("Bin :");
            for (int[] i : bin.getItemLocation()) {
                for (int j : i) {
                    out.print(j + " ");
                }
                out.println();
                out2.append(String.valueOf(bin.getBinNum())).append(",").append(Arrays.toString(i).substring(1, Arrays.toString(i).length() - 1));
                out2.append("\n");
            }
            out.println();
        }

        out.close();
        out2.flush();
        out2.close();
    }




    /**
     * Reader for the crate data, outputs an array containing the data.
     *
     * length = depth = z, width = width = x, height = height = y.
     *
     * @param crateFile The file containing he crate data.
     * @return An integer array as follows: { width, height, depth, weight }
     * @throws FileNotFoundException
     */
    public static int[] readCrateData(File crateFile) throws FileNotFoundException {
        Scanner s = new Scanner(crateFile);
        s.nextLine();

        String[] str = s.nextLine().split(",");
        int length = Integer.parseInt(str[0]);
        int width = Integer.parseInt(str[1]);
        int height = Integer.parseInt(str[2]);
        int weight = Integer.parseInt(str[3]);

        int[] crate = { length, width, height, weight };

        s.close();

        return crate;
    }

    /**
     * Reader for the data, outputs it in a map with the order id as the key and a
     * list of items representing that order. Note, if an item has a quantity of x
     * the item is added x times to the list of items.
     *
     * width = width = x, length = depth = z, height = height = y.
     *
     * @param orderFile The file containing the orders.
     * @param itemFile  The file containing the items.
     * @return The orders in a map.
     * @throws FileNotFoundException
     */
    public static Map<Integer, List<Item>> readData(File orderFile, File itemFile) throws FileNotFoundException {
        Scanner s = new Scanner(orderFile);
        s.nextLine();

        Map<Integer, List<Item>> itemsPerOrder = new HashMap<>();
        List<String[]> orderData = new ArrayList<>();

        // Reads the orders, saves the orders as strings in a list.
        // Also creates an empty list for each order id in the order Map.
        while (s.hasNext()) {
            String[] str = s.nextLine().split(",");
            orderData.add(str);

            if (!itemsPerOrder.containsKey(Integer.parseInt(str[0]))) {
                itemsPerOrder.put(Integer.parseInt(str[0]), new ArrayList<>());
            }
        }

        s.close();
        s = new Scanner(itemFile);
        s.nextLine();

        Map<Integer, Item> items = new HashMap<>();

        // Reads the items, creates the Item object for each item and adds it to the
        // list.
        while (s.hasNext()) {
            String[] str = s.nextLine().split(";");
            items.put(Integer.parseInt(str[0]), new Item(Integer.parseInt(str[0]), Integer.parseInt(str[3]),
                    Integer.parseInt(str[2]), Integer.parseInt(str[4]), Integer.parseInt(str[5])));
        }

        s.close();

        // Creates the order, it adds the specified item for the specified quantity
        // amount of times to the list off the order.
        for (String[] str : orderData) {
            for (int i = 0; i < Integer.parseInt(str[2]); i++) {
                itemsPerOrder.get(Integer.parseInt(str[0])).add(items.get(Integer.parseInt(str[1])));
            }
        }

        return itemsPerOrder;
    }

}

