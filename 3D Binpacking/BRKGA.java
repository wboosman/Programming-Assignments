package GA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class represents the BRKGA algorithm. It will be able to return the best
 * found chromosome, which represents the best solution to pack the current
 * order.
 */
public class BRKGA {

    private final List<Item> order;
    private final int[] crateData;
    private final int popSize;
    private final int eliteSize;
    private final int mutantSize;
    private final double crossProp;
    private final int maxGen;
    private final Random r = new Random(44);

    /**
     * Constructor for the BRKGA object, all the parameters of the algorithm are
     * given as input here as well as the order.
     *
     * @param order      The current order.
     * @param popMult    The multiplier to determine the population size.
     * @param eliteProp  The proportion of elite chromosomes per population.
     * @param mutantProp The proportion of mutants for the new population.
     * @param crossProp  The probability for the crossovers.
     * @param maxGen     The maximum amount of generations after which the best
     *                   chromosome is returned.
     */
    public BRKGA(List<Item> order, int[] crateData, int popMult, double eliteProp, double mutantProp, double crossProp,
                 int maxGen) {
        this.order = order;
        this.crateData = crateData;
        this.popSize = popMult * order.size();
        this.eliteSize = (int) Math.round(eliteProp * popSize);
        this.mutantSize = (int) Math.round(mutantProp * popSize);

        this.crossProp = crossProp;
        this.maxGen = maxGen;
    }

    /**
     * Method to create the initial population completely random.
     *
     * @return The initial population.
     */
    private Population initialPop() {
        Population pop = new Population(0);

        for (int i = 0; i < popSize; i++) {
            double[] sequence = r.doubles(order.size()).toArray();
            double[] orientation = r.doubles(order.size()).toArray();

            pop.addChromosome(new Chromosome(0, i, sequence, orientation));
        }

        return pop;
    }

    /**
     * Method to create the population of the next generation from the current
     * population.
     *
     * @param currPop The current population.
     * @return The new population.
     */
    private Population nextGeneration(Population currPop) {
        Population newPop = new Population(currPop.getGenenration() + 1);
        List<Chromosome> elite = new ArrayList<>(currPop.getElite(eliteSize));
        List<Chromosome> rest = new ArrayList<>(currPop.getPopulation());
        rest.removeAll(elite);

        // First adds the elites of the current population to the new population.
        for (Chromosome chr : elite) {
            Chromosome newChr = new Chromosome(newPop.getGenenration(), newPop.getSize(), chr.getSequence(),
                    chr.getOrientation());
            newChr.setFitness(chr.getFitness());
            newPop.addChromosome(newChr);
        }

        // Then adds the mutants to the new population.
        for (int i = 0; i < mutantSize; i++) {
            double[] sequence = r.doubles(order.size()).toArray();
            double[] orientation = r.doubles(order.size()).toArray();
            newPop.addChromosome(new Chromosome(newPop.getGenenration(), i + newPop.getSize(), sequence, orientation));
        }

        // Then performs the crossovers to fill the population to its desired size.
        for (int i = newPop.getSize(); i < popSize; i++) {
            Chromosome chr1 = elite.get((int) Math.floor(r.nextDouble() * elite.size()));
            Chromosome chr2 = rest.get((int) Math.floor(r.nextDouble() * elite.size()));

            newPop.addChromosome(crossover(newPop.getGenenration(), i, chr1, chr2));
        }

        return newPop;
    }

    /**
     * This method defines the crossovers which are preformed between an elite
     * chromosome and non-elite chromosome, both chosen at random. The crossover
     * probability represents the probability p for which a component i of the
     * vector v is from the elite chromosome or from the non-elite chromosome (1 -
     * p).
     *
     * @param gen         The number of the next generation.
     * @param number      The id for the new chromosome.
     * @param eliteChr    The elite parent chromosome.
     * @param nonEliteChr The non-elite parent chromosome.
     * @return The child chromosome.
     */
    private Chromosome crossover(int gen, int number, Chromosome eliteChr, Chromosome nonEliteChr) {
        double[] newSeq = new double[eliteChr.getSequence().length];
        double[] newOr = new double[eliteChr.getOrientation().length];

        // Creates the sequence and orientation vectors for the new chromosome.
        for (int i = 0; i < newSeq.length; i++) {
            if (r.nextDouble() < crossProp) {
                newSeq[i] = eliteChr.getSequence()[i];
            } else {
                newSeq[i] = nonEliteChr.getSequence()[i];
            }
            if (r.nextDouble() < crossProp) {
                newOr[i] = eliteChr.getOrientation()[i];
            } else {
                newOr[i] = nonEliteChr.getOrientation()[i];
            }
        }

        return new Chromosome(gen, number, newSeq, newOr);
    }

    /**
     * Runs the GA, updates the population each generation and returns the final
     * best chromosome.
     *
     * @return The best chromosome from the last generation.
     */
    public Chromosome solve() {
        Population pop = initialPop();
        fitnessFunction(pop);
        System.out.println(pop.getBestChromosome());

        while (pop.getGenenration() < maxGen) {
            pop = nextGeneration(pop);
            fitnessFunction(pop);
            System.out.println(pop.getBestChromosome());
        }

        return pop.getBestChromosome();
    }

    /**
     * Method to appoint each chromosome in a population its fitness when none was
     * assigned yet. Only the elite chromosomes from the previous generation already
     * have their fitness.
     *
     * @param pop The current population.
     */
    private void fitnessFunction(Population pop) {
        for (Chromosome chr : pop.getPopulation()) {
            if (chr.getFitness() == -1) {
                List<Bin> sol = decoder(chr);
                double fitness = sol.size()
                        + ( getLeastLoaded(sol) * 1.0 ) / ( 1.0 * crateData[0] * crateData[1] * crateData[2] );
                chr.setFitness(fitness);
            }
        }
    }

    /**
     * Method to find the least load of the current solution (list of bins).
     *
     * @param bins The current solution.
     * @return The least load.
     */
    private int getLeastLoaded(List<Bin> bins) {
        int minLoad = Integer.MAX_VALUE;

        for (Bin bin : bins) {
            if (bin.getCurrentVolume() < minLoad) {
                minLoad = bin.getCurrentVolume();
            }
        }

        return minLoad;
    }

    /**
     * Method to decode a chromosome into a solution.
     *
     * @param chr The chromosome.
     * @return The solution.
     */
    public List<Bin> decoder(Chromosome chr) {
        // Order the items according to the sequence.
        List<Element> orderOfItems = new ArrayList<>();
        List<Bin> bins = new ArrayList<>();
        bins.add(new Bin(0, crateData[0], crateData[1], crateData[2], crateData[3]));

        for (int i = 0; i < chr.getSequence().length; i++) {
            orderOfItems.add(new Element(i, chr.getSequence()[i]));
        }

        Collections.sort(orderOfItems);

        // Keep track of the remaining items to be placed and their smallest dimension
        // and volume.
        List<Item> remainder = new ArrayList<>(order);
        int smallestDim = getSmallestDim(remainder);
        int smallestVol = getSmallestVolume(remainder);

        // Place the items in order of the sequence.
        for (Element e : orderOfItems) {
            Item current = order.get(e.index);
            int currBin = 0;
            EMS selected = null;

            // Search for a bin to place the item in.
            while (selected == null && currBin < bins.size()) {
                selected = DFTRC1(bins.get(currBin), current, chr.getOrientation()[e.index]);
                currBin++;
            }

            // If no suitable bin was found open a new bin and place the item in.
            if (selected == null) {
                Bin newBin = new Bin(bins.get(bins.size() - 1).getBinNum() + 1, crateData[0], crateData[1],
                        crateData[2], crateData[3]);
                bins.add(newBin);
                newBin.addItem(current, 0, 0, 0,
                        getOrientation(newBin.getS().get(0), current, chr.getOrientation()[e.index]));
            }
            // Otherwise, place the item in the current bin at the found EMS starting point.
            else {
                Bin currentBin = bins.get(selected.getBinNum());
                currentBin.addItem(current, selected.getStartX(), selected.getStartY(), selected.getStartZ(),
                        current.getOrientation());
            }

            // Update the remainder, only update the smallest dimension and volume if the
            // current item leaving can cause them to change.
            remainder.remove(current);
            if (current.getSmallestDim() == smallestDim || current.getVolume() == smallestVol) {
                smallestDim = getSmallestDim(remainder);
                smallestVol = getSmallestVolume(remainder);
            }

            // Update the EMS according to the smallest dimension and volume.
            updateEMS(bins, smallestDim, smallestVol);
        }

        return bins;
    }

    /**
     * Method to update the EMS based on volume and smallest dimension. If in the
     * remaining items no item can fit in an EMS the EMS should be removed from
     * consideration in order to speed up computation times.
     *
     * @param bins      The current bins in the solution.
     * @param remainder The remaining items.
     */
    private void updateEMS(List<Bin> bins, int smallestDim, int smallestVol) {
        for (Bin bin : bins) {
            List<EMS> removed = new ArrayList<>();
            for (EMS e : bin.getS()) {
                if (smallestDim > e.getSmallestDim() || smallestVol > e.getVolume()) {
                    removed.add(e);
                }
            }

            bin.removeAllEMS(removed);
        }
    }

    /**
     * Method to find the smallest volume within a given set of remaining items.
     *
     * @param remainder The list of remaining items to be placed.
     * @return The smallest volume within the list.
     */
    private int getSmallestVolume(List<Item> remainder) {
        int minVol = Integer.MAX_VALUE;

        for (Item item : remainder) {
            if (item.getVolume() < minVol) {
                minVol = item.getVolume();
            }
        }

        return minVol;
    }

    /**
     * Method to find the smallest dimension within a given set of remaining items.
     *
     * @param remainder The list of remaining items to be placed.
     * @return The smallest dimension within the list.
     */
    private int getSmallestDim(List<Item> remainder) {
        int minDim = Integer.MAX_VALUE;

        for (Item item : remainder) {
            if (item.getSmallestDim() < minDim) {
                minDim = item.getSmallestDim();
            }
        }

        return minDim;
    }

    /**
     * This methods represents the first placement heuristic. It searches for the
     * best EMS to place the given item in based on its given key of orientation.
     *
     * @param bin  The current bin.
     * @param item The item to be placed.
     * @param vbo  The key for orientation.
     * @return The EMS in which the item is placed, within the item its orientation
     * saved.
     */
    private EMS DFTRC1(Bin bin, Item item, double vbo) {
        double maxDist = -1;
        EMS selected = null;

        if (bin.getCurrentWeight() + item.getWeight() > bin.getWeightLimit() || bin.getS().isEmpty()) {
            return null;
        }

        for (EMS e : bin.getS()) {
            int BO = getOrientation(e, item, vbo);

            if (BO > -1) {
                double dist = Math.pow(bin.getLength() - e.getStartX(), 2) + Math.pow(bin.getWidth() - e.getStartY(), 2)
                        + Math.pow(bin.getHeight() - e.getStartZ(), 2);

                if (dist > maxDist) {
                    maxDist = dist;
                    selected = e;
                    item.setOrientation(BO);
                }
            }
        }

        return selected;
    }

    // possible orientations:
    // option 1: l-x w-y h-z
    // option 2: l-z w-y h-x
    // option 3: l-x w-z h-y
    // option 4: l-z w-x h-y
    // option 5: l-y w-x h-z
    // option 6: l-y w-z h-x

    /**
     * Method to get the desired orientations for a given item within the given EMS.
     * <p>
     * possible orientations: option 1: l-x w-y h-z option 2: l-z w-y h-x option 3:
     * l-x w-z h-y option 4: l-z w-x h-y option 5: l-y w-x h-z option 6: l-y w-z h-x
     *
     * @param e    The given EMS.
     * @param item The given item to be placed.
     * @param vbo  The key from the chromosome corresponding with the item.
     * @return The desired orientation.
     */
    private int getOrientation(EMS e, Item item, double vbo) {
        List<Integer> BOs = new ArrayList<>();

        int x1 = e.getStartX();
        int y1 = e.getStartY();
        int z1 = e.getStartZ();
        int x2 = e.getEndX();
        int y2 = e.getEndY();
        int z2 = e.getEndZ();

        int length = item.getLength();
        int width = item.getWidth();
        int height = item.getHeight();

        // 1
        if (x1 + length <= x2 && y1 + width <= y2 && z1 + height <= z2) {
            BOs.add(1);
        }
        // 2
        if (x1 + height <= x2 && y1 + width <= y2 && z1 + length <= z2) {
            BOs.add(2);
        }
        // 3
        if (x1 + length <= x2 && y1 + height <= y2 && z1 + width <= z2) {
            BOs.add(3);
        }
        // 4
        if (x1 + width <= x2 && y1 + height <= y2 && z1 + length <= z2) {
            BOs.add(4);
        }
        // 5
        if (x1 + width <= x2 && y1 + length <= y2 && z1 + height <= z2) {
            BOs.add(5);
        }
        // 6
        if (x1 + height <= x2 && y1 + length <= y2 && z1 + width <= z2) {
            BOs.add(6);
        }

        return ( BOs.isEmpty() ? -1 : BOs.get((int) ( Math.ceil(vbo * BOs.size()) - 1 )) );
    }

    /**
     * Private element class in order to sort the sequences within the chromosomes.
     * Therefore it only consists of a compareTo method.
     *
     */
    private class Element implements Comparable<Element> {
        final int index;
        final double value;

        Element(int index, double value) {
            this.index = index;
            this.value = value;
        }

        public int compareTo(Element e) {
            return ( this.value - e.value < 0 ? -1 : 1 );
        }
    }
}
