import java.util.*;

/**
 * This class Extends to the Creature class, specific properties and decisions of Carnivores are described here.
 * @author 589273wb Wessel Boosman
 */
public class Carnivore extends Creature {

    /**
     * This Constructor initiates a Creature of the type Carnivore, with sight 2 and sets its energy to 30.
     */
    public Carnivore(){
        super(2);
        super.setEnergy(30);
    }

    /**
     * This method is written to check for each cell in the visible cells the amount of herbivores and the biggest herbivore in that cell. This data is stored in an 2D array
     * of size (length visible cells X 2) with column one containing an integer of the amount of herbivores and column 2 the biggest herbivore for the corresponding cell.
     * @return sorted 2D array with most preferable cell (most herbivores and if equal, largest herbivore) at the last index.
     *
     */
    public int[][] listWithHerbivoreCharacteristics() {
        int[][] bambi = new int[getVisibleCells().size()][2];
        for (int i = 0; i < getVisibleCells().size(); i++) {
            int amountHerbivores=0;
            int sizeBigBambi=0;
                for (int j = 0; j < getVisibleCells().get(i).getCreatures().size(); j++) {
                    if (getVisibleCells().get(i).getCreatures().get(j) instanceof Herbivore) {
                        amountHerbivores += 1;
                        if (( (Herbivore) getVisibleCells().get(i).getCreatures().get(j) ).getSize() > sizeBigBambi) {
                            sizeBigBambi = ( (Herbivore) getVisibleCells().get(i).getCreatures().get(j) ).getSize();
                        }
                    }
                }
                bambi[i][0] = amountHerbivores;
                bambi[i][1] = sizeBigBambi;
                    }
        Arrays.sort(bambi, Comparator.<int[]>comparingInt(a -> a[0]).thenComparingInt(a -> a[1]));
        return bambi;
    }

    /**
     * Method to return the best visible cell. In the method listWithHerbivoreCharacteristics() the characteristics of the best visible cell are discovered. This method is meant to get the corresponding cell.
     * @return best visible cell
     */
    public Cell getBestCell() {
        List<Cell> bestCell = new ArrayList<>();
        for (int i = 0; i < getVisibleCells().size(); i++) {
            int amountHerbivores = 0;
            int sizeBigBambi = 0;
            for (int j = 0; j < getVisibleCells().get(i).getCreatures().size(); j++) {
                if (getVisibleCells().get(i).getCreatures().get(j) instanceof Herbivore) {
                    amountHerbivores += 1;
                    if (( (Herbivore) getVisibleCells().get(i).getCreatures().get(j) ).getSize() > sizeBigBambi) {
                        sizeBigBambi = ( (Herbivore) getVisibleCells().get(i).getCreatures().get(j) ).getSize();
                    }
                }
            }
            if (amountHerbivores == listWithHerbivoreCharacteristics()[listWithHerbivoreCharacteristics().length -1][0] && sizeBigBambi == listWithHerbivoreCharacteristics()[listWithHerbivoreCharacteristics().length - 1][1]) {
                bestCell.add(getVisibleCells().get(i));
            }
        }
        return bestCell.get(0);
    }



    /**
     * A method which describes the move method. When called on, the creature moves to the best visible cell via the getBestCell() method and via the moveTo(newCell) method. It can only move if alive
     */
    @Override
    public void move() throws IllegalStateException
    {
        if (!this.isAlive()){
        throw new IllegalStateException("The creature is die and can therefore not move");
    }
        super.moveTo(getBestCell());
    }

    /**
     * A method which describes the act method. When called on, the creature is gaining in the current cell if there is a herbivore to eat.
     * It eats the largest herbivore. After which it loses energy due to metabolism. If there is no herbivore present, it only loses energy due to metabolism.
     */
    @Override
    public void act() {
        List<Herbivore> listUnluckyBambi= new ArrayList<>();
        int dummySize = 0;
        if(getCurrentCell().getCreatures().size()==0){
            changeEnergy(-6);
        }
        for (int j = 0; j < getCurrentCell().getCreatures().size(); j++) {
            if (getCurrentCell().getCreatures().get(j) instanceof Herbivore && ( (Herbivore) getCurrentCell().getCreatures().get(j) ).getSize() > dummySize){
                dummySize = ( (Herbivore) getCurrentCell().getCreatures().get(j) ).getSize();
                listUnluckyBambi.add((Herbivore) getCurrentCell().getCreatures().get(j));
            }
        }
        if( dummySize ==0){
            changeEnergy(-6);
        }
        if(dummySize>0)
        {
            changeEnergy(listUnluckyBambi.get(listUnluckyBambi.size()-1).getEnergy());
            listUnluckyBambi.get(listUnluckyBambi.size()-1).die();
            changeEnergy(-6);
        }
    }
}

