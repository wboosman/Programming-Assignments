/**
 * This class contains the constructor of the FPTAS (Fully Polynomial-Time Approximation Scheme) algorithm.
 * 
 * @author XXXXXmk Matthijs XXXXXX
 * @author 589273wb	Wessel Boosman
 */
import java.util.HashMap;
import java.util.Map;

public class FPTAS {
	private int n;
	private int Q;
	private int[] values;
	private int[] correctedValues;
	private int[] weights;
	private double epsilon;
	private double K;
	private Map<Integer, int[]> F;
	private boolean[][] backTrack;

	private int cmax;
	private int trackLength;
	private long cStart;
	private long cEnd;
	
	/**
	 * This is the constructor of the algorithm.
	 */
	public FPTAS(int n, int Q, int[] values, int[] weights, double epsilon) {
		this.n = n;
		this.Q = Q;
		this.values = values;
		this.weights = weights;
		this.epsilon = epsilon;
		this.F = new HashMap<>();
		
		// Get starting time
		cStart = System.currentTimeMillis();
		
		// Find maximum value
		cmax = 0;
		for(int i=0; i<n; i++) {
			if(values[i]>cmax) {
				cmax = values[i];
			}
		}
		
		// Calculate K and the corrected values
		K = epsilon*cmax/n;
		if(K<1) {
			K = 1;
		}
		
		trackLength = 1;
		correctedValues = new int[n];
		for(int i=0; i<n; i++) {
			correctedValues[i] = (int) Math.floor(values[i]/K);
			trackLength += correctedValues[i];
		}
		
		// Update value of c_max
		cmax = 0;
		for(int i=0; i<n; i++) {
			if(correctedValues[i]>cmax) {
				cmax = correctedValues[i];
			}
		}
		
		applyAlgorithm();
		findOptimum();
	}

	/**
	 * This method initializes and applies the FPTAS algorithm. 
	 */
	private void applyAlgorithm() {
		// Create the backtracking matrix
		backTrack = new boolean[n][trackLength];
		
		// Step 1: initialization
		int[] F1 = new int[trackLength];
		F1[0] = 0;
		backTrack[0][0] = false;
		
		for(int p=1; p<trackLength; p++) {
			if(p==correctedValues[0]) {
				F1[p] = weights[0];
				backTrack[0][p] = true;
			} else {
				F1[p] = Integer.MAX_VALUE;
				backTrack[0][p] = false;
			}
		}
		F.put(1, F1);

		// Step 2: compute F_j(p) for all values of j and p
		int curr = 1;
		for(int j=1; j<n; j++) {
			int[] Fj = new int[trackLength];
			for(int p=0; p<trackLength; p++) {
				int p1 = F.get(curr)[p];
				int p2 = weights[j];
				if(p-correctedValues[j]<0) {
					p2 += Integer.MAX_VALUE;
				} else {
					p2 += F.get(curr)[p-correctedValues[j]];
				}
				if(p2<0) {
					p2 = Integer.MAX_VALUE;
				}
				if(p1<p2) {
					Fj[p] = p1;
					backTrack[j][p] = false;
				} else {
					Fj[p] = p2;
					backTrack[j][p] = true;
				}
			}
			
			if(j%2==0) {
				F.put(1, Fj);
				curr = 1;
			}
			else {
				F.put(2, Fj);
				curr = 2;
			}
		}
	}

	/**
	 * This method finds the optimal solution with corrected values.
	 */
	private void findOptimum() {
		int z = 0;
		
		int find = 2;
		if(n-1%2==0) {
			find = 1;
		}
		
		// Find the optimal value
		for(int p=0; p<trackLength; p++) {
			int weight = F.get(find)[p];
			if(p>z && weight<=Q) {
				z = p;
			}
		}

		// Find out which items belong to the optimal solution
		applyBacktracking(z);
	}
	
	/**
	 * This method applies backtracking to find the uncorrected optimal objective value
	 */
	private void applyBacktracking(int z) {
		int[] sol = new int[n];

		for(int curr = n-1; curr>=0; curr--) {
			if(backTrack[curr][z]==true) {
				sol[curr] = 1;
				z = z - correctedValues[curr];
			}
		}
		
		// Calculate the objective value with the original profits
		int opt = 0;
		for(int i=0; i<n; i++) {
			opt += values[i]*sol[i];
		}
		
		System.out.print("Objective found with epsilon " + epsilon + ": " +opt);
		cEnd = System.currentTimeMillis();
		System.out.println(", with running time: "+ (cEnd-cStart) + " ms");
	}
}
