package GA;

import java.util.Arrays;

/**
 * Class to represent the chromosome. It contains the sequence and orientation
 * arrays as well as its computed fitness.
 * 
 *
 */
public class Chromosome {

	private final double[] sequence;
	private final double[] orientation;
	private final int number;
	private final int gen;
	private double fitness = -1;

	/**
	 * Constructor for the chromosome, the new sequence and orientation should be
	 * computed outside of the chromosome and before it is created.
	 * 
	 * @param gen         The generation index.
	 * @param number      The chromosome id.
	 * @param sequence    The array representing the sequence.
	 * @param orientation The array representing the orientation.
	 */
	public Chromosome(int gen, int number, double[] sequence, double[] orientation) {
		this.gen = gen;
		this.number = number;
		this.sequence = sequence;
		this.orientation = orientation;
	}

	/**
	 * Getter for the sequence array.
	 * 
	 * @return The sequence array.
	 */
	public double[] getSequence() {
		return sequence;
	}

	/**
	 * Getter for the orientation array.
	 * 
	 * @return The orientation array.
	 */
	public double[] getOrientation() {
		return orientation;
	}

	/**
	 * Getter for the generation index.
	 * 
	 * @return The generation.
	 */
	public int getGen() {
		return gen;
	}

	/**
	 * Getter for the chromosome id.
	 * 
	 * @return The id.
	 */
	public int getNumber() {
		return number;
	}

	// TODO: Fitness function!
	/**
	 * Setter for the fitness, the fitness will likely be computed outside of the
	 * chromosome.
	 * 
	 * @param fitness The fitness value.
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * Getter for the fitness value.
	 * 
	 * @return The fitness value.
	 */
	public double getFitness() {
		return fitness;
	}

	@Override
	public String toString() {
		String result = "Gene: " + number + "\n";
		result += ("Generation: " + gen + "\n");
		result += ("Fitness: " + fitness + "\n");
		result += ("Sequence: " + Arrays.toString(sequence) + "\n");
		result += ("Orientation: " + Arrays.toString(orientation) + "\n");

		return result;
	}
}
