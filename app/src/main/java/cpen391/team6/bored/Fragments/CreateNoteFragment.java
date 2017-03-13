package cpen391.team6.bored.Fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.app.Fragment;

import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.R;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-12.
 */
public class CreateNoteFragment extends Fragment {

    DrawSpaceFragment mDrawSpace;
    FrameLayout mDrawFrame;

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);

        /* We may want to contribute to the action bar menu */
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.create_note_fragment_layout, container, false);

        mDrawFrame = (FrameLayout) view.findViewById(R.id.drawing_space);
        mDrawSpace = (DrawSpaceFragment) getActivity().getFragmentManager().findFragmentById(R.id.drawing_space);

        mDrawFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDrawFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(mDrawSpace == null) {

                    /* Get Width and add an additional offset, for some reason getWidth()
                     * doesn't provide the full width of the layout
                     */

                    int frameWidth = mDrawFrame.getWidth() + 100;
                    int frameHeight = mDrawFrame.getHeight();

                    System.out.println(frameHeight);

                    //pass width and height of screen as arguments to launch animation
                    Bundle arguments = new Bundle();

                    arguments.putDouble("width", frameWidth);
                    arguments.putDouble("height", frameHeight);

                    mDrawSpace = new DrawSpaceFragment();
                    mDrawSpace.setArguments(arguments);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    /* Replace the current fragment that is being displayed, provide it with a tag so we can
                    * locate it in the future
                    */
                    transaction.add(R.id.drawing_space, mDrawSpace, "draw_space");

                    /* This call is necessary so we don't create a new fragment by default, not sure why */
                    transaction.addToBackStack(null);

                    /* allows for smoother transitions between screens */
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                    /* Actually make the transition */
                    transaction.commit();

                }
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

       // if(mDrawSpace != null){
       //     mDrawSpace.destroy();
       // }
    }

}
