package GA;

import java.util.ArrayList;
import java.util.List;

public class Bin {

	private final int binNum;
	private final int length;
	private final int width;
	private final int height;
	private final int volume;
	private final int weightLimit;

	private List<Item> items;
	private List<int[]> itemLocationMap;
	private List<EMS> S;
	private int currentWeight;
	private int currentVolume;

	public Bin(int binNum, int length, int width, int height, int weightLimit) {
		this.binNum = binNum;
		this.length = length;
		this.width = width;
		this.height = height;
		this.volume = length * width * height;
		this.weightLimit = weightLimit;

		items = new ArrayList<>();
		S = new ArrayList<>();
		S.add(new EMS(binNum, 0, 0, 0, length, width, height));
		itemLocationMap = new ArrayList<>();
	}

	public List<Item> getItems() {
		return items;
	}

	public List<int[]> getItemLocation() {
		return itemLocationMap;
	}

	public List<EMS> getS() {
		return S;
	}

	public int getBinNum() {
		return binNum;
	}

	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getWeightLimit() {
		return weightLimit;
	}

	public int getCurrentWeight() {
		return currentWeight;
	}

	public int getCurrentVolume() {
		return currentVolume;
	}

	public double getFillRate() {
		return (volume * 1.0) / (currentVolume * 1.0);
	}
	
	public void removeAllEMS(List<EMS> removeS) {
		S.removeAll(removeS);
	}

	public void removeEMS(EMS ems) {
		S.remove(ems);
	}

	public void addItem(Item item, int startX, int startY, int startZ, int orientation) {
		items.add(item);
		currentWeight += item.getWeight();
		currentVolume += item.getVolume();
		int[] location = new int[7];
		location[0] = item.getNumber();

		location[1] = startX;
		location[2] = startY;
		location[3] = startZ;
		
		int lengthItem = item.getLength();
		int widthItem = item.getWidth();
		int heightItem = item.getHeight();

		// possible orientations:
		// option 1: l-x w-y h-z
		// option 2: l-z w-y h-x
		// option 3: l-x w-z h-y
		// option 4: l-z w-x h-y
		// option 5: l-y w-x h-z
		// option 6: l-y w-z h-x
		switch (orientation) {
			case 1 -> {
				location[4] = startX + lengthItem;
				location[5] = startY + widthItem;
				location[6] = startZ + heightItem;
			}
			case 2 -> {
				location[4] = startX + heightItem;
				location[5] = startY + widthItem;
				location[6] = startZ + lengthItem;
			}
			case 3 -> {
				location[4] = startX + lengthItem;
				location[5] = startY + heightItem;
				location[6] = startZ + widthItem;
			}
			case 4 -> {
				location[4] = startX + widthItem;
				location[5] = startY + heightItem;
				location[6] = startZ + lengthItem;
			}
			case 5 -> {
				location[4] = startX + widthItem;
				location[5] = startY + lengthItem;
				location[6] = startZ + heightItem;
			}
			case 6 -> {
				location[4] = startX + heightItem;
				location[5] = startY + lengthItem;
				location[6] = startZ + widthItem;
			}
		}

		if (location[4] > length || location[5] > width || location[6] > height) {
			System.out.println("Bin: width: " + length + " height: " + width + " depth: " + height);
			System.out.println("Item: endX: " + location[4] + " endY: " + location[5] + " endZ: " + location[6]);
			throw new IllegalArgumentException("Item placement exceeds dimension limit of the bin");
		}

		if (currentWeight > weightLimit || currentVolume > volume) {
			System.out.println(binNum);
			System.out.println("Current weight: " + currentWeight + " Current volume: " + currentVolume);
			System.out.println("Weight limit: " + weightLimit + " Volume: " + volume);
			
			System.out.println(this);
			
			throw new IllegalArgumentException("Item placed exceeds weight or volume capacity");
		}
		
		itemLocationMap.add(location);
		updateEMS(item);
	}

	/**
	 * Update method for the list S of EMS. It creates the new EMS based on the
	 * current EMS and the new Item and composes the correct new EMS and updates the
	 * list S. It removes the infinitely thin or encapsulated EMS. Other checks such
	 * as volume, smallest dimension, etc. should be done outside of the bin object.
	 * 
	 * Infinite thinness should account for an EMS going through the newly added
	 * item and older items.
	 * 
	 * The update method corrects for EMS that would be incorrectly defined by their
	 * second point being to the left or below the first point (x/y/z3 smaller than
	 * x/y/z1 or x/y/z4 greater than x/y/z2). It also corrects for the x/y/z4 being
	 * smaller or equal than x/y/z1, which is problematic as the starting point of
	 * the new EMS cannot be closer to the bottom left of the bin than the previous
	 * EMS.
	 * 
	 * @param item The newly added item.
	 */
	private void updateEMS(Item item) {
		int[] location = itemLocationMap.get(items.lastIndexOf(item));
		List<EMS> newS = new ArrayList<>();

		// For all the current EMS need to be updated, even if the new item does not
		// fall into that space. The update can correct for that.
		for (EMS e : S) {
			int x1 = e.getStartX();
			int y1 = e.getStartY();
			int z1 = e.getStartZ();
			int x2 = e.getEndX();
			int y2 = e.getEndY();
			int z2 = e.getEndZ();

			// Create the new EMS for all current EMS according to the Difference Process,
			// keep in mind it currents here for the above mentioned problems.
			newS.add(new EMS(binNum, x1, y1, z1, (location[1] < x1 ? x1 : Math.min(location[1], x2)), y2, z2));
			newS.add(new EMS(binNum, (location[4] > x2 ? x2 : Math.max(location[4], x1)), y1, z1, x2, y2, z2));
			newS.add(new EMS(binNum, x1, y1, z1, x2, (location[2] < y1 ? y1 : Math.min(location[2], y2)), z2));
			newS.add(new EMS(binNum, x1, (location[5] > y2 ? y2 : Math.max(location[5], y1)), z1, x2, y2, z2));
			newS.add(new EMS(binNum, x1, y1, z1, x2, y2, (location[3]) < z1 ? z1 : Math.min(location[3], z2)));
			newS.add(new EMS(binNum, x1, y1, (location[6] > z2 ? z2 : Math.max(location[6], z1)), x2, y2, z2));
		}

		List<EMS> removed = new ArrayList<>();

		// Checks for infinite thinness and encapsulation.
		for (EMS e : newS) {
			// If infinite thinness is found then add and continue.
			if (e.getStartX() == e.getEndX() || e.getStartY() == e.getEndY() || e.getStartZ() == e.getEndZ()) {
				removed.add(e);
				continue;
			}

			// Check for encapsulation with all other EMS, if found add and break out of the
			// for loop below to continue to the next new EMS.
			for (EMS otherE : newS) {
				if (!removed.contains(otherE) && !e.equals(otherE)) {
					if (e.getStartX() >= otherE.getStartX() && e.getStartY() >= otherE.getStartY()
							&& e.getStartZ() >= otherE.getStartZ() && e.getEndX() <= otherE.getEndX()
							&& e.getEndY() <= otherE.getEndY() && e.getEndZ() <= otherE.getEndZ()) {
						removed.add(e);
						break;
					}
				}
			}
		}

		// Remove all eliminated EMS and update S.
		newS.removeAll(removed);
		S = newS;
	}

	@Override
	public String toString() {
		String temp = "Bin: ";

		for (int[] location : itemLocationMap) {
			temp += "Item: " + location[0] + ", x3: " + location[1] + ", y3: " + location[2] + ", z3: " + location[3]
					+ ", x4: " + location[4] + ", y4: " + location[5] + ", z4: " + location[6] + "\n";
		}

		return temp + S;
	}
}
