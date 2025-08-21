import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Assignment 2 SMO - EUR 2021 (ORQL)
 * Find an optimal path through the maze
 *
 * @authors Erik XXXXXXXX & Wessel Boosman
 */
public class QLearning {

    // Rules of the game
    public static final int maze_size = 10;
    public static final int num_states = 100;
    public static final int num_actions = 4;
    public static final int N_max = 3000;

    // Decision variables
    public static double alpha = 0.98;
    public static double learning_rate = 0.9;
    public static String explorationType = "baseline"; // "fixed" = 0.7-0.1-0.1-0.1; "greedy" = epsilon-greedy (see below); "baseline" = 0.5-0.5
    public static double epsilon = 0.99;
    public static double epsilon_decrease = 0.98/N_max;

    public static void main(String[] args) {

        // Define the maze
        char[][] maze = new char[maze_size][maze_size];
        maze[0][0] = 'g';
        maze[0][2] = 'B';
        maze[0][8] = 'B';
        maze[1][0] = 'B';
        maze[2][5] = 'B';
        maze[2][7] = 'G';
        maze[3][4] = 'B';
        maze[5][0] = 'B';
        maze[5][8] = 'B';
        maze[6][0] = 'B';
        maze[6][9] = 'B';
        maze[9][9] = 'g';

        // Alternative maze representation: symbol per state
        char[] maze_array = new char[num_states];
        maze_array[0] =  'g';
        maze_array[2] =  'B';
        maze_array[8] =  'B';
        maze_array[10] = 'B';
        maze_array[25] = 'B';
        maze_array[27] = 'G';
        maze_array[34] = 'B';
        maze_array[50] = 'B';
        maze_array[58] = 'B';
        maze_array[60] = 'B';
        maze_array[69] = 'B';
        maze_array[99] = 'g';

        // DEFINITION ACTIONS: 0 = ^/up/North,  1 = >/right/East,  2 = v/down/South,  3 = </left/West

        // Initializations
        double[][] Qmatrix_init = new double[num_states][num_actions]; // initializes the Qmatrix
        int[] initLocation = {9,0}; // row, column

        // Perform algorithm given specialized variables
        double[][] finalQmatrix = algorithm(Qmatrix_init, initLocation, maze);
        int[] finalActionMatrix = QmatrixToActionMatrix(finalQmatrix);
        String[] finalStringMatrix = actionMatrixToStringMatrix(finalActionMatrix, maze_array);

        // PRINT ACTION MATRIX:
        System.out.println();
        System.out.println("Action matrix: ");
        for (int i=0; i<10; i++) {
            System.out.println(Arrays.toString(Arrays.copyOfRange(finalStringMatrix, 10*i, (10*i)+10)));
        }

    }

    /**
     * Algorithm that performs iteration through maze and updating Q-matrix.
     * Probabilities are chosen in one of 3 of the following ways:
     * 1) Fix probabilities: p=0.7 optimal direction (so far), p=0.1 other of 3 directions
     * 2) Epsilon greedy: p=(1-epsilon) optimal action so far, p=epsilon a random action, epsilon decreasing
     * 3) Baseline: p=0.5 optimal direction (so far)  / Epsilon-greedy
     * @param initQ : Initial Q-matrix
     * @param initLocation : Starting location
     * @param maze : Special symbols in maze
     * @return Qmatrix : Iterated Q-matrix
     */
    public static double[][] algorithm(double[][] initQ, int[] initLocation, char[][] maze) {
        Random rand = new Random();
        int[] currLocation = initLocation;
        double[][] Qmatrix = initQ;
        int objective = 0;

        // Set all the impossible actions around the borders to minus infinity in the Qmatrix
        for (int row=0; row<maze_size; row++) {
            for (int col=0; col<maze_size; col++) {
                // Get state
                int[] loc = new int[2];
                loc[0] = row;
                loc[1] = col;
                int state = locationToState(loc);

                // Restrict from side to side
                if (col == 0) { // left side
                    Qmatrix[state][3] = -1*Double.POSITIVE_INFINITY;
                }
                if (col == 9) { // right side
                    Qmatrix[state][1] = -1*Double.POSITIVE_INFINITY;
                }
                if (row == 0) { // upper side
                    Qmatrix[state][0] = -1*Double.POSITIVE_INFINITY;
                }
                if (row == 9) { // bottom side
                    Qmatrix[state][2] = -1*Double.POSITIVE_INFINITY;
                }
            }
        }

        // Iterate for N_max steps
        int numIters = N_max;
        for (int iter=0; iter < numIters; iter++) {

            int currState = locationToState(currLocation);
            List<Integer> bestActions = new ArrayList<Integer>();
            double bestRewardValue = -1*Double.POSITIVE_INFINITY;

            // Choose the action that maximizes the corresponding row in the Qmatrix
            for (int rew=0; rew<num_actions; rew++) {
                if (Qmatrix[currState][rew] == bestRewardValue) {
                    bestActions.add(rew);
                } else if (Qmatrix[currState][rew] > bestRewardValue) {
                    bestActions.clear();
                    bestActions.add(rew);
                    bestRewardValue = Qmatrix[currState][rew];
                }
            }
            int chosenDirection = -1;
            if (bestActions.size() == 1) { // no ties in Q-matrix row, then choose the best action
                chosenDirection = bestActions.get(0);
            } else if (bestActions.size() > 1) { // ties in Q-matrix row, then choose a random action among the ties
                int min=0;
                int max=bestActions.size()-1; // number between 0 and size-1
                chosenDirection = rand.nextInt((max - min) + 1) + min;
            }

            // Choose the chosen direction with p=0.7 and one of the other locations with p=0.1
            List<Integer> posActions = new ArrayList<Integer>();
            posActions.add(0);
            posActions.add(1);
            posActions.add(2);
            posActions.add(3);
            posActions.remove(chosenDirection); // you won't randomly go into the chosen direction
            double prob = Math.random();
            int direction = -1;

            // Fixed: p=0.7 best, p=0.1 other
            if (explorationType == "fixed") {
                if (prob <= 0.7) {
                    direction = chosenDirection;
                } else if (prob > 0.7 && prob <= 0.8) {
                    direction = posActions.get(0);
                } else if (prob > 0.8 && prob <= 0.9) {
                    direction = posActions.get(1);
                } else {
                    direction = posActions.get(2);
                }
            }

            // Epsilon greedy: p=1-eps best, p=e/4 a random one
            if (explorationType == "greedy") {
                double eps = epsilon - iter * epsilon_decrease;
                if (prob <= 1-eps) {
                    direction = chosenDirection;
                } else if (prob > (1-eps) && prob <= ((1-eps)+(eps/4))) {
                    direction = 0;
                } else if (prob > ((1-eps)+(eps/4)) && prob <= ((1-eps)+(eps/2))) {
                    direction = 1;
                } else if (prob > ((1-eps)+(eps/2)) && prob <= ((1-eps)+(3*eps/4))) {
                    direction = 2;
                } else if (prob > ((1-eps)+(3*eps/4))) {
                    direction = 3;
                }
            }

            // Baseline: p=0.5 best, p=0.5/4=0.125 a random one
            if (explorationType == "baseline") {
                if (prob <= 0.5) {
                    direction = chosenDirection;
                } else if (prob > 0.5 && prob <= 0.625) {
                    direction = 0;
                } else if (prob > 0.625 && prob <= 0.75) {
                    direction = 1;
                } else if (prob > 0.75 && prob <= 0.875) {
                    direction = 2;
                } else if (prob > 0.875) {
                    direction = 3;
                }
            }

            // Determine the new direction on the board
            int[] newDirection = new int[2];
            if (direction == 0) { // ^ NORTH
                newDirection[0] = currLocation[0]-1;
                newDirection[1] = currLocation[1]; // column goes up
            } else if (direction == 1) { // > EAST
                newDirection[0] = currLocation[0]; // row goes right
                newDirection[1] = currLocation[1]+1;
            } else if (direction == 2) { // v SOUTH
                newDirection[0] = currLocation[0]+1;
                newDirection[1] = currLocation[1]; // column goes down
            } else if (direction == 3) { // < WEST
                newDirection[0] = currLocation[0]; // row goes left
                newDirection[1] = currLocation[1]-1;
            }

            int[] loc_before = currLocation;
            int[] loc_after = {-1,-1}; // means not existing if step outside maze or towards B, g or G
            int reward = 0;

            // Check your movement in the maze and update rewards
            if (newDirection[0] < 0 || newDirection[0] > 9 || newDirection[1] < 0 || newDirection[1] > 9) {
                // If you step outside the maze, stay in your old location
                newDirection[0] = currLocation[0];
                newDirection[1] = currLocation[1];
                reward = -1;
                iter = iter - 1;
                continue; // try again where you do not jump out of the maze
            } else if (maze[newDirection[0]][newDirection[1]] == 'B') {
                // If step towards B, you stay in your old location
                newDirection[0] = currLocation[0];
                newDirection[1] = currLocation[1];
                reward = -5;
            } else if (maze[newDirection[0]][newDirection[1]] == 'g') {
                // If step towards g, go back to init location
                newDirection[0] = initLocation[0];
                newDirection[1] = initLocation[1];
                reward = 50;
            } else if (maze[newDirection[0]][newDirection[1]] == 'G') {
                // If step towards G, go back to init location
                newDirection[0] = initLocation[0];
                newDirection[1] = initLocation[1];
                reward = 200;
            } else {
                // If you make a 'regular' step, don't alter the direction and subtract 1 from the rewards
                reward = -1;
                loc_after = newDirection;
            }
            objective = objective + reward;

            // Update the Qmatrix
            updateQmatrix(Qmatrix, locationToState(loc_before), locationToState(loc_after), direction, reward);

            // Actually go to that new square in the maze (could be the same as you were)
            currLocation = newDirection;

        }
        System.out.println("Objective: " + objective);
        return Qmatrix;
    }

    /**
     * Go from location to state
     * @param location : Location you are in [row, column]
     * @return state : State you are in (between 0 and 99)
     */
    public static int locationToState(int[] location) {
        int row = location[0];
        int column = location[1];
        int state = row*10 + column; // eg {row=4,column=5} => 4*10+5 = state 45
        return state;
    }

    /**
     * Updates the Q-matrix
     * @param Qmatrix
     * @param state_before
     * @param state_after
     * @param action
     * @param reward
     */
    public static void updateQmatrix(double[][] Qmatrix, int state_before, int state_after, int action, int reward) {
        double max_part = -1*Double.POSITIVE_INFINITY;
        if (state_after < 0) { // meaning you step in either a non-existing state or state where no actions are defined
            max_part = 0;
        } else {
            for (int i=0; i<num_actions; i++) {
                double candidate = Qmatrix[state_after][i];
                if (candidate > max_part) {
                    max_part = candidate;
                }
            }
        }
        double right_part = reward + alpha * max_part;
        Qmatrix[state_before][action] = (1-learning_rate)*Qmatrix[state_before][action] + learning_rate * right_part;
    }

    /**
     * Transform the final Qmatrix from a procedure to 10x10 maze with in each entry the optimal action.
     * @param Qmatrix : a Qmatrix that is fully updated
     * @return actionMatrix : a 10x10 matrix with the optimal action per state
     */
    public static int[] QmatrixToActionMatrix(double[][] Qmatrix) {
        int[] actionMatrix = new int[num_states];
        for (int i=0; i<num_states; i++) {
            int bestAction = -1;
            double bestValue = -1*Double.POSITIVE_INFINITY;
            double sumRow = 0.0; // sum of the row of the Q-matrix
            for (int j=0; j<num_actions; j++) {
                sumRow = sumRow + Qmatrix[i][j];
                if (Qmatrix[i][j] > bestValue) { // assume at a possible tie we just take the first action with highest value
                    bestAction = j;
                    bestValue = Qmatrix[i][j];
                }
            }
            if (sumRow == 0) { // meaning you've never been there
                actionMatrix[i] = -1;
            } else {
                actionMatrix[i] = bestAction;
            }
        }

        // Set all the impossible actions back to 0, just to display actions right
        for (int row=0; row<maze_size; row++) {
            for (int col=0; col<maze_size; col++) {
                // Get state
                int[] loc = new int[2];
                loc[0] = row;
                loc[1] = col;
                int state = locationToState(loc);

                // Restrict from side to side
                if (col == 0) { // left side
                    Qmatrix[state][3] = 0;
                }
                if (col == 9) { // right side
                    Qmatrix[state][1] = 0;
                }
                if (row == 0) { // upper side
                    Qmatrix[state][0] = 0;
                }
                if (row == 9) { // bottom side
                    Qmatrix[state][2] = 0;
                }
            }
        }
        for (int i=0; i<num_states; i++) {
            double sumRow = 0.0; // sum of the row of the Q-matrix
            for (int j=0; j<num_actions; j++) {
                sumRow = sumRow + Qmatrix[i][j];
            }
            if (sumRow == 0) { // meaning you've never been there
                actionMatrix[i] = -1;
            }
        }

        return actionMatrix;
    }

    /**
     * Turn the matrix with actions into a matrix with arrows
     * @param actionMatrix : Action matrix (see QmatrixToActionMatrix)
     * @return stringMatrix : The maze with arrows (^>v<), symbols (gBG) and special symbol (x) if that state was never visited
     */
    public static String[] actionMatrixToStringMatrix(int[] actionMatrix, char[] maze_array) {

        String[] stringMatrix = new String[actionMatrix.length];
        for (int i=0; i<actionMatrix.length; i++) {
            if (actionMatrix[i] == -1) {
                stringMatrix[i] = "x"; // not been there during algorithm
            } else if (actionMatrix[i] == 0) {
                stringMatrix[i] = "^";
            } else if (actionMatrix[i] == 1) {
                stringMatrix[i] = ">";
            } else if (actionMatrix[i] == 2) {
                stringMatrix[i] = "v";
            } else if (actionMatrix[i] == 3) {
                stringMatrix[i] = "<";
            }
        }
        for (int i=0; i<actionMatrix.length; i++) {
            if (maze_array[i] == 'B') {
                stringMatrix[i] = "B";
            } else if (maze_array[i] == 'g') {
                stringMatrix[i] = "g";
            } else if (maze_array[i] == 'G') {
                stringMatrix[i] = "G";
            }
        }
        return stringMatrix;
    }

}

