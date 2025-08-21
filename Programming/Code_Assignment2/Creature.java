import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;

/**
 * This abstract class keeps track of the condition of the Creature within a particular Cell.
 * @author 589273wb Wessel Boosman
 */
public abstract class Creature {
    private int sight;
    private Cell currentCell;
    private int energy=20;
    private boolean aliveDummy;

    /**
     *The constructor will create a Creature whith a specific sight which is specified in the Herbivore/Carnivore class. Initially the Creature is dead.
     * @param sight is the sight of the creature, means how 'far' it can look and move.
     */
    public Creature(int sight){
        this.sight=sight;
        aliveDummy = false;

    }

    /**
     * This Method is written to initiate the Creature if this is not done already and to move if the Creature decides to move. At first
     * it is not alive, therefore the if statement. When it is first moved to a cell it becomes alive via the aliveDummy.
     * When it is already alive, it is used to move to a new Cell.
     * @param newCell indicates the Cell the creature should move.
     */

    public final void moveTo(Cell newCell){
        //adds current creature to the newCell
        if(!isAlive()) {
            newCell.addCreature(this);
            currentCell = newCell;
            aliveDummy = true;

        }
        if(isAlive()){
            newCell.addCreature(this);
            currentCell.removeCreature(this);
            currentCell = newCell;
        }
    }

    /**
     * This method is used to return the currentCell the creature lives in. However, when the creature is not alive, it returns a null.
     * @return the current Cell the creature lives in.
     */
    public final Cell getCurrentCell(){
        if (!isAlive())
        {
            return null;
        }
        return currentCell;
    }

    /**
     * A method to describe the height of the world,
     * @return the height of the world
     */
    public int getHeight(){
        return getCurrentCell().getWorld().getHeight();
    }

    /**
     * A method to get the width of the world,
     * @return the width of the world
     */
    public int getWidth(){
        return getCurrentCell().getWorld().getWidth();
    }

    /**
     * Method which has yet to be described in the Herbivore/Carnivore class....
     */
    public abstract void move();

    /**
     * Method which has yet to be described in the Herbivore/Carnivore class....
     */
    public abstract void act();

    /**
     * A method to get the sight of the creature
     * @return the sight of the creature
     */
    public final int getSight(){
        return sight;
    }

    /**
     * A method written to describe the death of a Creature, when this method is called, the Creature is removed from the list of creatures via
     * the removeCreature method. Also aliveDummy is set to false.
     */
    public final void die(){
        getCurrentCell().removeCreature(this);
        //this.setEnergy(0);
        aliveDummy=false;
    }

    /**
     * A method to get to know if a creature is dead or alive
     * @return the dead or alive state of the Creature
     */
    public final Boolean isAlive(){
        return aliveDummy;

    }

    /**
     * A method to force a creature to have a certain energy, especially usefull if the creature's energy differentiates from the standard energy.
     * @param energy the energy set to the creature
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * A method to get the energy of the creature.
     * @return The currene energy of the Creature
     */
    public final int getEnergy(){
        return energy;
    }

    /**
     * A Method which changes the energy of the Creature for whatever reason
     * @param amount the amount of change of energy of the current creature.
     */
    public final void changeEnergy(int amount) {
        if (isAlive()) {
            this.energy += amount;
            if (this.getEnergy() <= 0) {
                this.die();
            }
        }
    }

    /**
     * A method written to give back a List of cell which can be seen by the Creature.
     * @return A list of visible cells for the creature, dependent on the sight.
     */
    public final List<Cell> getVisibleCells(){
        List<Cell> visibleCells = new ArrayList<>();
        //visibleCells.add(getCurrentCell());
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (!isAlive())
                {
                    return null;
                }
                if(abs(getCurrentCell().getX() - getCurrentCell().getWorld().getCell( i, j).getX()) <= sight && abs(getCurrentCell().getY() - getCurrentCell().getWorld().getCell( i, j).getY())<= sight){
                    visibleCells.add(getCurrentCell().getWorld().getCell( i, j));

                }
              }
          }

        Collections.sort(visibleCells);
        return visibleCells;
    }
}
