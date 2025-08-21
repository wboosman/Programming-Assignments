import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Duties class represents a collection of duty assignments.
 * Each duty is stored as a list of integers representing tasks and associated costs.
 * Duties can be constructed from an input file of tasks.
 *
 * Author: Wessel Boosman
 */
public class Duties {

    private List<List<Integer>> duties;

    public Duties() {
        duties = new ArrayList<>();
    }

    public List<List<Integer>> getDuties() {
        return duties;
    }

    public int getNumberDuties(){
        return duties.size();
    }


    public void addDuty(List<Integer> duty) {
        duties.add(duty);
    }


    public static Duties construction(String string) throws FileNotFoundException {
        Duties newDuties = new Duties();
        Scanner s = new Scanner(new File(string));
        HashMap<Integer, Task> Tasks = new HashMap<>();
        int numberOfDuties = 0;
        while (s.hasNext()) {
            int one = s.nextInt();
            String two = s.next();
            String three = s.next();
            int four = s.nextInt();
            int five = s.nextInt();
            Task task = new Task(one, four, five, two, three);
            numberOfDuties++;
            Tasks.put(one, task);
        }
        s.close();
        int bigM = 2090;
        int FixedPrice = 900;
        for (int i = 0; i < numberOfDuties; i++) {
            ArrayList<Integer> tasksInDuties = new ArrayList<>();
            for (int j = 0; j < numberOfDuties; j++) {
                if (i == j) {
                    tasksInDuties.add(1);
                } else {
                    tasksInDuties.add(0);
                }
            }
            tasksInDuties.add(0, Tasks.get(i).getEndTime()-Tasks.get(i).getStartTime());
            int price = bigM;
            Task feasible = Tasks.get(i);
            if (feasible.getStartPlatform().equals(feasible.getEndPlatform()) && ((feasible.getStartPlatform().equals("A") || feasible.getStartPlatform().equals("B") || feasible.getStartPlatform().equals("C")))){
                price = FixedPrice + ( feasible.getEndTime() - feasible.getStartTime() );
            }
            tasksInDuties.add(0,price);
            newDuties.addDuty(tasksInDuties);
        }

        return newDuties;
    }
}

