package cpen391.team6.bored.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import processing.core.PApplet;

/**
 * Created by neema on 2017-03-12.
 */
public class DrawSpaceFragment extends PApplet{

    int lastLocx;
    int lastLocy;
    boolean init;

    @Override
    public void setup(){

        background(255);
        init = false;
    }

    @Override
    public void draw(){


    }

    @Override
    public void mouseDragged(){

        if(!init){
            init = true;
        }else{
            line(lastLocx, lastLocy, mouseX, mouseY);
        }

        lastLocx = mouseX;
        lastLocy = mouseY;

    }

    @Override
    public void mouseReleased(){

        init = false;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle saveState){


    }

    @Override
    public void settings() {

        Bundle arguments = getArguments();

        size((int) arguments.getDouble("width"), (int) arguments.getDouble("height"));
        //num_balls = (int) arguments.getInt("num_balls");

    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "Animation" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }



}
