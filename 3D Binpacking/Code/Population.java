package GA;

import java.util.ArrayList;
import java.util.List;

public class Population {

	private final int gen;
	private List<Chromosome> pop = new ArrayList<>();
	private int size = 0;
	private List<Chromosome> sortedPop = new ArrayList<>();

	public Population(int gen) {
		this.gen = gen;
	}

	/**
	 * Method for the GA to add a chromosome to the population.
	 * 
	 * @param chr The to be added chromosome.
	 */
	public void addChromosome(Chromosome chr) {
		pop.add(chr);
		size++;
	}

	/**
	 * The current size of the population.
	 * 
	 * @return Current population size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Getter for the population, does not have to be sorted!
	 * 
	 * @return The population.
	 */
	public List<Chromosome> getPopulation() {
		return pop;
	}

	/**
	 * Getter for the generation of the population.
	 * 
	 * @return The generation.
	 */
	public int getGenenration() {
		return gen;
	}

	/**
	 * Getter for the best chromosome of the population.
	 * 
	 * @return The best chromosome.
	 */
	public Chromosome getBestChromosome() {
		if (sortedPop.isEmpty()) {
			return sortPopulation().get(0);
		} else {
			return sortedPop.get(0);
		}

	}

	/**
	 * Gets the elite chromosomes from this population.
	 * 
	 * @return A list of chromosomes representing the elite.
	 */
	public List<Chromosome> getElite(int eliteSize) {
		if (sortedPop.isEmpty()) {
			System.out.println(true);
			return sortPopulation().subList(0, eliteSize - 1);
		} else {
			return sortedPop.subList(0, eliteSize - 1);
		}
	}

	/**
	 * A compare function to compare two Chromosome objects.
	 * 
	 * @param first  The first chromosome.
	 * @param second The second chromosome.
	 * @return A negative value if the first chromosome is lesser than the second, 0
	 *         if equal and a positive value otherwise.
	 */
	private int compare(Chromosome first, Chromosome second) {
		return Double.compare(first.getFitness(), second.getFitness());
	}

	/**
	 * The method sorts the population, from best fitness to worst fitness. Note,
	 * best fitness is the lowest value as it is a minimisation problem.
	 * 
	 * @return The sorted population.
	 */
	private List<Chromosome> sortPopulation() {
		if (sortedPop.isEmpty()) {
			List<Chromosome> temp = new ArrayList<>();
			temp.addAll(pop);
			mergeSort(temp, 0, temp.size() - 1);
			sortedPop.addAll(temp);
		}

		return sortedPop;
	}

	/**
	 * This function performs the merge procedure and orders the current sublist.
	 * 
	 * @param subList The current sublist.
	 * @param l       Left index.
	 * @param m       Middle index.
	 * @param r       Right index.
	 */
	private void merge(List<Chromosome> subList, int l, int m, int r) {
		int n1 = m - l + 1;
		int n2 = r - m;

		List<Chromosome> tempL = new ArrayList<>();
		List<Chromosome> tempR = new ArrayList<>();

		for (int i = 0; i < n1; i++)
			tempL.add(subList.get(l + i));
		for (int j = 0; j < n2; j++)
			tempR.add(subList.get(m + 1 + j));

		int i = 0;
		int j = 0;
		int k = l;

		while (i < n1 && j < n2) {
			if (compare(tempL.get(i), tempR.get(j)) < 0) {
				subList.set(k, tempL.get(i));
				i++;
			} else {
				subList.set(k, tempR.get(j));
				j++;
			}
			k++;
		}

		while (i < n1) {
			subList.set(k, tempL.get(i));
			i++;
			k++;
		}

		while (j < n2) {
			subList.set(k, tempR.get(j));
			;
			j++;
			k++;
		}
	}

	/**
	 * This function recursively performs a merge sort of the population from worst
	 * to best.
	 * 
	 * @param population The to be sorted population.
	 * @param l          Index from which to sort on the left.
	 * @param r          Index from which to sort on the right.
	 */
	private void mergeSort(List<Chromosome> population, int l, int r) {
		if (l >= r) {
			return;
		}
		int m = l + (r - l) / 2;
		mergeSort(population, l, m);
		mergeSort(population, m + 1, r);
		merge(population, l, m, r);
	}

}
