import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {


    public static void main(String[] args)  {
//        File fileToRead = new File("lib/BinPackingInstance.txt");
        File fileToRead = new File("lib/InitialPackings.txt");
        File fileToReadBin = new File("lib/BinPackingInstance.txt");
        try {
            //Read initial packings
            BinPackingInstance instanceBin = BinPackingInstance.read(fileToReadBin);
            InitialPacking instance = InitialPacking.read(fileToRead);
            System.out.println("The following instance was read:");
            System.out.println(instance);

            System.out.println("ITERATION 1 SOLUTION");
            Model model = new Model(instance);
            model.solve();
            System.out.println("RMP obj value: "+ model.getObjective());
            System.out.println(model.getSolution());
//            System.out.println(model.getVarMap());
//            System.out.println(model.getNumVar());
            System.out.println(model.getPackings());
            System.out.println(model.getDuals());
            System.out.println(model.getDuals().size());
            System.out.println("PRICING MODEL SOLUTION");
            PricingModel pricingModelIT1 = new PricingModel(instanceBin, instance, model.getDuals());
            pricingModelIT1.solve();
            System.out.println(pricingModelIT1.getObjective());
            double a =1;
            System.out.println("RC:" + (a-pricingModelIT1.getObjective()));
            System.out.println("Opt. Packig:" +pricingModelIT1.getPacking());
            instance.updatePackings(pricingModelIT1.getPacking());
//            System.out.println(instance.getInitialPackings());
//            System.out.println(instance.getInitialPackings().get(0).size());

            System.out.println("ITERATION 2 SOLUTION");
            Model modelIt2 = new Model(instance);
            modelIt2.solve();
            System.out.println("RMP obj value: "+ modelIt2.getObjective());
            System.out.println(modelIt2.getSolution());
            System.out.println(modelIt2.getDuals());
            System.out.println("PRICING MODEL SOLUTION");
            PricingModel pricingModelIT2 = new PricingModel(instanceBin, instance, modelIt2.getDuals());
            pricingModelIT2.solve();
            System.out.println("RC:" + (a-pricingModelIT2.getObjective()));
            System.out.println(pricingModelIT2.getPacking());
            instance.updatePackings(pricingModelIT2.getPacking());

            System.out.println("ITERATION 3 SOLUTION");
            Model modelIt3 = new Model(instance);
            modelIt3.solve();
            System.out.println("RMP obj value: "+ modelIt3.getObjective());
            System.out.println(modelIt3.getSolution());
            System.out.println(modelIt3.getDuals());
            System.out.println("PRICING MODEL SOLUTION");
            PricingModel pricingModelIT3 = new PricingModel(instanceBin, instance, modelIt3.getDuals());
            pricingModelIT3.solve();
            System.out.println("RC:" + (a-pricingModelIT3.getObjective()));
            System.out.println(pricingModelIT3.getPacking());
            instance.updatePackings(pricingModelIT3.getPacking());

            System.out.println("ITERATION 4 SOLUTION");
            Model modelIt4 = new Model(instance);
            modelIt4.solve();
            System.out.println("RMP obj value: "+ modelIt4.getObjective());
            System.out.println(modelIt4.getSolution());
            System.out.println(modelIt4.getDuals());
            System.out.println("PRICING MODEL SOLUTION");
            PricingModel pricingModelIT4 = new PricingModel(instanceBin, instance, modelIt4.getDuals());
            pricingModelIT4.solve();
            System.out.println("RC:" + (a-pricingModelIT4.getObjective()));
            System.out.println(pricingModelIT4.getPacking());
            instance.updatePackings(pricingModelIT4.getPacking());


            System.out.println("ITERATION 5 SOLUTION");
            Model modelIt5 = new Model(instance);
            modelIt5.solve();
            System.out.println("RMP obj value: "+ modelIt5.getObjective());
            System.out.println(modelIt5.getSolution());
            System.out.println(modelIt5.getDuals());
            System.out.println("PRICING MODEL SOLUTION");
            PricingModel pricingModelIT5 = new PricingModel(instanceBin, instance, modelIt5.getDuals());
            pricingModelIT5.solve();
            System.out.println("RC:" + (a-pricingModelIT5.getObjective()));
            System.out.println(pricingModelIT5.getPacking());
            instance.updatePackings(pricingModelIT5.getPacking());


            System.out.println("ITERATION 6 SOLUTION");
            Model modelIt6 = new Model(instance);
            modelIt6.solve();
            System.out.println("RMP obj value: "+ modelIt6.getObjective());
            System.out.println(modelIt6.getSolution());
            System.out.println(modelIt6.getDuals());
            System.out.println("PRICING MODEL SOLUTION");
            PricingModel pricingModelIT6 = new PricingModel(instanceBin, instance, modelIt6.getDuals());
            pricingModelIT6.solve();
            System.out.println("RC:" + (a-pricingModelIT6.getObjective()));
            System.out.println(pricingModelIT6.getPacking());
            instance.updatePackings(pricingModelIT6.getPacking());


            System.out.println("ITERATION 7 SOLUTION");
            Model modelIt7 = new Model(instance);
            modelIt7.solve();
            System.out.println("RMP obj value: "+ modelIt7.getObjective());
            System.out.println(modelIt7.getSolution());
            System.out.println(modelIt7.getDuals());
            System.out.println("PRICING MODEL SOLUTION");
            PricingModel pricingModelIT7 = new PricingModel(instanceBin, instance, modelIt7.getDuals());
            pricingModelIT7.solve();
            System.out.println("RC:" + (a-pricingModelIT7.getObjective()));
            System.out.println(pricingModelIT7.getPacking());
            instance.updatePackings(pricingModelIT7.getPacking());

        }

        catch (FileNotFoundException | IloException ex) {
            System.out.println("There was an error reading file "+fileToRead);
            ex.printStackTrace();
        }
    }
}
