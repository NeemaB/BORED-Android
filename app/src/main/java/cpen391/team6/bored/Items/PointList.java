package cpen391.team6.bored.Items;

/**
 * Created by neema on 2017-03-21.
 */
public class PointList {


    private Point point;
    private PointList next;

    public PointList(Point point) {
        this.next = null;
        this.point = point;
    }

    public Point getPoint() {
        return this.point;
    }

    public PointList getNext() {
        return this.next;
    }

    public void setNext(PointList pointList) {
        this.next = pointList;
    }

}

