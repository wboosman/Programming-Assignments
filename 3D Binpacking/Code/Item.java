package GA;

public class Item {

	private final int number;
	private final int length;
	private final int width;
	private final int height;
	private final int weight;
	private int orientation = -1;
	
	public Item(int number, int length, int width, int height, int weight) {
		this.number = number;
		this.length = length;
		this.width = width;
		this.height = height;
		this.weight = weight;
	}

	public int getNumber() {
		return number;
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
	
	public int getVolume() {
		return length * width * height;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int getSmallestDim() {		
		return Math.min(length, Math.min(width, height));
	}
	
	public void setOrientation(int BO) {
		orientation = BO;
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	@Override
	public String toString() {
		return ("Item: " + number + ", length: " + length + ", width: " + width + ", height: " + height + ", weight: " + weight);
	}
}
