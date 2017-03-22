package cpen391.team6.bored.Items;

/**
 * Created by neema on 2017-03-21.
 */
public class UndoList {


    private PointList pointListHead;
    private PointList pointList;

    private UndoList next;
    private UndoList prev;

    private ColourMenu.Colour colour;
    private PenWidthMenu.PenWidth penWidth;

    public UndoList(Point point, ColourMenu.Colour colour, PenWidthMenu.PenWidth penWidth){

        this.colour = colour;
        this.penWidth = penWidth;
        this.next = null;
        this.prev = null;
        this.pointListHead = new PointList(point);
        this.pointList = this.pointListHead;

    }

    public PointList getPointListHead(){
        return this.pointListHead;
    }

    public PointList getPointList(){
        return this.pointList;
    }

    public UndoList getNext(){
        return this.next;
    }

    public UndoList getPrev(){
        return this.prev;
    }

    public ColourMenu.Colour getColour(){
        return this.colour;
    }

    public PenWidthMenu.PenWidth getPenWidth(){
        return this.penWidth;
    }

    public void setNext(UndoList undoList){
        this.next = undoList;
    }

    public void setPrev(UndoList undoList){
        this.prev = undoList;
    }

    public void setPointList(PointList pointList){
        this.pointList = pointList;
    }

}


