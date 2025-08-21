/**
 * This class Extends to the Creature class, specific properties and decisions of Herbivores are described here.
 * @author 589273wb Wessel Boosman
 */
public class Herbivore extends Creature {

    private int size;

    /**
     * This Constructor initiates a Creature of the type Herbivore.
     * @param size is the size of the Herbivore
     * @throws IllegalArgumentException when the size of the creature is below zero
     */
    public Herbivore(int size) throws IllegalArgumentException
    {
       super(1);
       this.size=size;
        if (size <= 0){
            throw new IllegalArgumentException("Herbivore should have a positive size");
        }
    }

    /**
     * A method to get the size of the creature
     * @return the size of the creature
     */
    public int getSize()
    {
        return size;
    }

    /**
     * A method which describes the move method. When called on, the creature moves to a newCell via the moveTo(newCell) method.
     *
     * Moet hier een if statement voor als het dier leeft....? Als de die() methode wordt aangeroepen en dan de moveTo(), dan zou het
     * dier gelijk weer tot leven komen wat me niet de bedoeling lijkt..?
     */
    @Override
    public void move() throws IllegalStateException {
        if (!this.isAlive()) {
           throw new IllegalStateException("The creature is die and can therefore not move");
        }
        super.moveTo(getVisibleCells().get(0));
    }

    /**
     * A method which describes the act method. When called on, the creature is gaining. It eats as much as it can dependent on size and circumstances
     *  After it has eaten, it loses energy due to metabolism.
     */
    @Override
    public void act() {
        int amountHerbivoreEats=Math.min(getCurrentCell().getPlants(), (2*size*size)/(1+size));
        this.changeEnergy(amountHerbivoreEats);
        this.getCurrentCell().changePlants(-1*amountHerbivoreEats);
        this.changeEnergy(-1*size);
    }
}
