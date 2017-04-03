package cpen391.team6.bored.Items;

/**
 * Created by neema on 2017-03-13.
 */
public class Point {

    public int locX, locY;

    public Point(int locX, int locY){

        this.locX = locX;
        this.locY = locY;
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof Point) {
            if (this.locX == ((Point) o).locX
                    && this.locY == ((Point) o).locY) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
