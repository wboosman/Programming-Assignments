/**
 * This class contains the Dynamic Programming algorithm for a knapsack problem.
 *
 * @author xxxxxxmk Matthijs xxxxxxxx
 * @author 589273wb	Wessel Boosman
 */



public class DynamicProgram {

    private KnapsackInstance instance;
    private int n;
    private int C;
    private int[] weights;
    private int[] profits;

    /**
     * This is the constructor of the algorithm.
     */
    public DynamicProgram(KnapsackInstance newInstance) {
        this.instance = newInstance;
        this.n = newInstance.getNumItems();
        this.C = newInstance.getCapacity();
        this.weights = newInstance.getItemWeight();
        this.profits = newInstance.getItemProfit();
    }


    /**
     * This is the method that calculates the optimal objective value via a recursion formula.
     * We keep track of a two dimensional array with two rows. Initialization is stored in the 0th row, the DP variable corresponding
     *  to the first item is stored in the first row, the DP variable corresponding to the second item in te 0th row again etc....
     */
    public int knapsackDPMemoryEfficient() {
        long startTime = System.nanoTime();

        int[][] DPvar = new int[2][C + 1];
        for (int j = 0; j <= C; j++) {
            DPvar[0][j] = 0;
        }

        for (int i = 1; i <= n; i++) {
            if (i % 2 == 0) {
                for (int j = 0; j <= C; j++) {
                    if (weights[i - 1] > j) {
                        DPvar[0][j] = DPvar[1][j];
                    } else {
                        DPvar[0][j] = Math.max(
                                DPvar[1][j],
                                DPvar[1][j - weights[i - 1]] + profits[i - 1]);
                    }
                }
            } else {
                for (int j = 0; j <= C; j++) {
                    if (weights[i - 1] > j) {
                        DPvar[1][j] = DPvar[0][j];
                    } else {
                        DPvar[1][j] = Math.max(
                                DPvar[0][j],
                                DPvar[0][j - weights[i - 1]] + profits[i - 1]);
                    }
                }
            }
        }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time of the Memory efficient Dynamic Programming algorithm in milliseconds: " + timeElapsed / 1000000);
            if (n % 2 == 0) {
                return DPvar[0][C];
            }
            else
                {
                    return DPvar[1][C];
                }
        }
    }

