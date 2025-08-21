import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class initiates the world, and dependent on the size of the world it creates the appropiate amount of cells.
 * @author 589273wb Wessel Boosman
 */

public class World {
    private int w;
    private int h;
    private List<Creature> worldCreatures = new ArrayList<>();
    private Cell[][] cells;



    /**
     * Constructor which initiates the world.
     * @param w represents the width (corresponds with x coordinate)
     * @param h represents the height (corresponds to the y coordinate)
     *
     */
    public World(int w, int h) {
        this.w = w;
        this.h = h;
        cells = new Cell[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Cell c = new Cell(this, i, j);
                cells[i][j]= c;
            }
        }
    }

    /**
     * Method which return the width of a certain World
     * @return World width
     */
    public int getWidth()
    {
        return w;
    }

    /**
     * Method which return the height of a certain World
     * @return World height.
     */
    public int getHeight()
    {
        return h;
    }

    /**
     * This method is written to give back one particular Cell of the world
     * @param x is the x-coordinate of the world, corresponds to the width in my case
     * @param y is the x-coordinate of the world, corresponds to the width in my case
     * @return the Cell you are interested in
     */
    public Cell getCell(int x, int y)
    {
        if(x<0||x>=w||y<0||y>=h){
            throw new IllegalArgumentException("Input should not extend boundaries");
        }
        return cells[x][y];
    }


    /**
     * This method is written to return a list with all the cells
     * @return List of the created Cells
     */
    public List<Cell> getCellList()
    {
        List<Cell> list = new ArrayList<>();
        for (Cell[] array : cells) {
            list.addAll(Arrays.asList(array));
        }
        return list;
    }


    /**
     * The method is written to get a list with all the creatures.
     * @return A List with all the creatures the Worlds holds
     */
    public List<Creature> getCreatures()
    {
        for (int i = 0; i < (w); i++) {
            for (int j = 0; j < (h); j++) {
                worldCreatures.addAll(getCell(i,j).getCreatures());
            }
        }
        return worldCreatures;
    }

}