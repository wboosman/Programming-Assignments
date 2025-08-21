import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


/**
 * ProductionInstance represents a single instance of a production planning problem.
 * <p>
 * It contains fixed and variable production/holding costs, the number of planning
 * periods, and production-specific data such as demand and capacity per period.
 *
 * Author: Wessel Boosman
 */
public class ProductionInstance {

    private int fixProduction;
    private int fixHolding;
    private int varProduction;
    private int varHolding;
    private int numberPeriods;
    private HashMap<String, List<Integer>> prodSpecifics;

    public ProductionInstance(int fixProduction, int fixHolding, int varProduction, int varHolding, int numberPeriods, HashMap<String, List<Integer>> prodSpecifics) {
        /**
         * Constructor for ProductionInstance.
         *
         * @param fixProduction  fixed production cost
         * @param fixHolding     fixed holding cost
         * @param varProduction  variable production cost
         * @param varHolding     variable holding cost
         * @param numberPeriods  number of planning periods
         * @param prodSpecifics  map containing demand and capacity per period
         */
        
        this.fixProduction = fixProduction;
        this.fixHolding = fixHolding;
        this.varProduction = varProduction;
        this.varHolding = varHolding;
        this.numberPeriods = numberPeriods;
        this.prodSpecifics = prodSpecifics;
    }

    public int getFixProduction() {
        return fixProduction;
    }

    public int getFixHolding() {
        return fixHolding;
    }

    public int getVarProduction() {
        return varProduction;
    }

    public int getVarHolding() {
        return varHolding;
    }

    public int getNumberPeriods() {
        return numberPeriods;
    }

    public HashMap<String, List<Integer>> getProdSpecifics() {
        return prodSpecifics;
    }

    public static ProductionInstance read(File instanceFileName) throws FileNotFoundException {

//Try to open the file and initialize the use of a so called scanner.
        try (Scanner s = new Scanner(instanceFileName)) {

            //Obtain the size of the bin.
            int numberPeriods = s.nextInt();

            //Ignore the third token ("NumberOfItems:").
            s.next();

            //Obtain the production costs
            int fixProduction = s.nextInt();

            int varProduction = s.nextInt();

            //Ignore the fifth token ("ItemSizes:")
            s.next();

            //Obtain the holding costs
            int fixHolding = s.nextInt();

            int varHolding = s.nextInt();

            //Ignore the fifth token ("ItemSizes:")
            s.next();

            //Go over all items and obtain their sizes.
            List<Integer> demand = new ArrayList<>();
            List<Integer> capacity = new ArrayList<>();
            for (int i = 0; i < numberPeriods; i++) {
                demand.add(s.nextInt());
                capacity.add(s.nextInt());
                s.next();
            }

            HashMap<String, List<Integer>> productionSpecifics = new HashMap<>();
            productionSpecifics.put("Demand", demand);
            productionSpecifics.put("Capacity", capacity);

            ProductionInstance result = new ProductionInstance(fixProduction,
                    fixHolding,
                    varProduction,
                    varHolding,
                    numberPeriods,
                    productionSpecifics);

            return result;
        }
    }
}
