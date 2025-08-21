public class Arc {
    /**
     * Arc class represents a directed edge in a graph,
     * connecting two nodes (from → to) with an associated price (cost).
     *
     * Author: Wessel Boosman
     */
    private final int from;
    private final int to;
    private double price;


    public Arc(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Arc{" +
                "from=" + from +
                ", to=" + to +
                ", price=" + price +
                '}';
    }
}
