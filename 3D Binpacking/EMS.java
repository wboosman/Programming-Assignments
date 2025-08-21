package GA;

public class EMS {
	
	private final int binNum;
	private final int startX;
	private final int startY;
	private final int startZ;
	private final int endX;
	private final int endY;
	private final int endZ;
	
	public EMS(int binNum, int startX, int startY, int startZ, int endX, int endY, int endZ) {
		this.binNum = binNum;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
	}
	
	public int getBinNum() {
		return binNum;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getStartZ() {
		return startZ;
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public int getEndZ() {
		return endZ;
	}
	
	public int getVolume() {
		return (endX - startX) * (endY - startY) * (endZ - startZ);
	}
	
	public int getSmallestDim() {
		int x = endX - startX;
		int y = endY - startY;
		int z = endZ - startZ;
		
		return Math.min(x, Math.min(y, z));
	}
	
	@Override
	public String toString() {
		return "EMS: x1: " + startX + ", y1: " + startY + ", z1: " + startZ + ", x2: " + endX + ", y2: " + endY + ", z2: " + endZ;
	}
}
