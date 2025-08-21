import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class keeps track of everything that happens within a particular Cell.
 * @author 589273wb Wessel Boosman
 */
public class Cell implements Comparable<Cell> {

    //Maximum amount of plants in a cell
    public static final int MAX_PLANTS = 100;
    private int plants=0;
    private int x;
    private int y;
    private World w;
    private List<Creature> listOfCreatures = new ArrayList<>();

    /**
     * This Constructor is build to decide on which cell it has to perform its actions. Therefore, the input needs to be a World, x- and y coordinate.
     * @param w Nessecary to indicate in which World the Cell is.
     * @param x Indicates the X-coordinate of the Cell
     * @param y Indicates the y-coordinate of the cell
     */
    public Cell(World w, int x, int y) {
        this.x = x;
        this.y = y;
        this.w = w;
    }

    /**
     * A getter for the x-coordinate
     * @return Cell's x-coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * A getter for the y-coordinate
     * @return Cell's y-coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * A getter for the World
     * @return Cell's World
     */
    public World getWorld()
    {
        return w;
    }

    /**
     * A getter for the amount of plants in the Cell
     * @return Cell's amount of plants
     */
    public int getPlants()
    {
        return plants;
    }

    /**
     * This method is written to change the amount of plants in a cell.
     * @param amounts indicates the amount of plants which need to be added up to its current value
     * @return new amount of plants, with a maximum of 100 plants
     * @throws IllegalArgumentException when the amount of plants hypothetical becomes negative
     */
    public int changePlants(int amounts) throws IllegalArgumentException
    {
        if(plants +amounts <0 ){
            throw new IllegalArgumentException("It is not possible to have an negative amount of plants");
        }
        if(plants+ amounts > 100){
            plants = MAX_PLANTS;
            return plants;
        }
        plants += amounts;
        return plants;
    }

    /**
     * A method which keeps track of all the Creatures which are coming into the in the cell.
     * @param c The specific Creature.
     */
    public void addCreature(Creature c){
        listOfCreatures.add(c);
    }

    /**
     * A method which keeps track of all the Creatures which are leaving the in the cell.
     * @param c The specific Creature.
     */
    public void removeCreature(Creature c)
    {
        listOfCreatures.remove(c);
    }

    /**
     * This Method is written to make a comparison to cells. The natural order should be that the most preferable cell is the one with whe most plants.
     * If the amount of plants is equal, the most preferable cell is one with the least amount of creatures.
     * @param other is the comparison cell
     * @return integer
     */
    @Override
    public int compareTo(Cell other){

    if (plants != other.getPlants())
        {
            return other.plants - plants;
        }
        return listOfCreatures.size() - other.listOfCreatures.size();
    }

    /**
     * A method written to produce a copy list of all the creatures in the cell
     * @return the copies List of creatures in the Cell
     */
    public List<Creature> getCreatures(){
        List<Creature> readOnlyCreatureList= Collections.unmodifiableList(listOfCreatures);
        return readOnlyCreatureList;
    }

    /**
     * A method which returns the amount of creatures in a cell.
     * @return an integer which represents the amount of creatures in a cell.
     */
    public int getCreaturesListSize(){
        int readOnlyCreatureListSize= listOfCreatures.size();
        return readOnlyCreatureListSize;
    }
}


