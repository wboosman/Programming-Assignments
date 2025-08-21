import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IloException {

        File fileToRead = new File("lib/MCFInstanceLarge.txt");

        try {

            /**
             * Check the Instance
             */
            Instance test = Instance.read(fileToRead);
            /**
             * Code for the Large Instance, different than for the small Instance since you need to keep track of different information for the report.
             */
            //Some initializations of thinks i want to keep track of!
            List<Double> solutionListPerIteration = new ArrayList<>();
            Map<Integer, List<List<Double>>> testMap = new LinkedHashMap<>();
            // Some expensive operation on the item.

            for (int iteration = 0; iteration < 150; iteration++) {

                RMP model = new RMP(test, testMap);
                model.solve();

                solutionListPerIteration.add(model.getObjective());
                System.out.println(model.getObjective());

                for (int numberSP = 1; numberSP < test.getNumberOfCommodities() + 1; numberSP++) {
                    SubProblem SP = new SubProblem(test, model.getDuals(), testMap, numberSP, iteration);
                    SP.solve();

                    if (SP.getReducedCost() < 0.0) {
                        testMap = SP.getModifiedVarInfo();
                        break;
                    }
                    SP.cleanUp();
                }
                model.cleanUp();
            }
            FileWriter writer = new FileWriter("output.txt");
            for (Double dou : solutionListPerIteration) {
                writer.write(dou + System.lineSeparator());
            }
            writer.close();
        }

        catch( FileNotFoundException ex)
            {
                System.out.println("There was an error reading file " + fileToRead);
                ex.printStackTrace();
            } catch (IOException e) {
            e.printStackTrace();
        }

    }


    }

