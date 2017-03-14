package cpen391.team6.bored.Fragments;

import android.os.Bundle;

import android.app.Fragment;

import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.joanzapata.iconify.widget.IconTextView;

import cpen391.team6.bored.Items.ColourMenu;
import cpen391.team6.bored.R;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-12.
 */
public class CreateNoteFragment extends Fragment implements View.OnClickListener {

    private DrawerFragment mDrawer;
    private FrameLayout mDrawFrame;

    private int mDrawFrameWidth;
    private int mDrawFrameHeight;

    private IconTextView mColourPallette;
    private IconTextView mPenWidth;
    private IconTextView mRedo;
    private IconTextView mUndo;
    private IconTextView mFill;
    private IconTextView mTextBox;
    private IconTextView mClear;

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);

        /* We may want to contribute to the action bar menu */
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.create_note_fragment_layout, container, false);

        /* Find our view in the view hierarchy */
        mDrawFrame = (FrameLayout) view.findViewById(R.id.drawing_space);
        mColourPallette = (IconTextView) view.findViewById(R.id.colour_pallette);
        mPenWidth = (IconTextView) view.findViewById(R.id.pen_width);
        mRedo = (IconTextView) view.findViewById(R.id.redo);
        mUndo = (IconTextView) view.findViewById(R.id.undo);
        mFill = (IconTextView) view.findViewById(R.id.fill);
        mTextBox = (IconTextView) view.findViewById(R.id.text_box);
        mClear = (IconTextView) view.findViewById(R.id.clear_screen);

        /* Set Listeners */
        mColourPallette.setOnClickListener(this);
        mPenWidth.setOnClickListener(this);
        mRedo.setOnClickListener(this);
        mFill.setOnClickListener(this);
        mUndo.setOnClickListener(this);
        mFill.setOnClickListener(this);
        mTextBox.setOnClickListener(this);
        mClear.setOnClickListener(this);

        mDrawer = (DrawerFragment) getActivity().getFragmentManager().findFragmentById(R.id.drawing_space);

        /* We have to wait until the frame layout's dimensions have been determined before we can attach the
         * Processing fragment
         */
        mDrawFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDrawFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(mDrawer == null) {

                    /* Get Width and add an additional offset, for some reason getWidth()
                     * doesn't provide the full width of the layout
                     */

                    mDrawFrameWidth = mDrawFrame.getWidth() + 100;
                    mDrawFrameHeight = mDrawFrame.getHeight();

                    System.out.println("Draw Frame Width:" + mDrawFrame.getWidth());
                    System.out.println("Draw Frame Height:" + mDrawFrame.getHeight());

                    //pass width and height of screen as arguments to launch animation
                    Bundle arguments = new Bundle();

                    arguments.putDouble("width", mDrawFrameWidth);
                    arguments.putDouble("height", mDrawFrameHeight);

                    mDrawer = new DrawerFragment();
                    mDrawer.setArguments(arguments);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    /* Replace the current fragment that is being displayed, provide it with a tag so we can
                    * locate it in the future
                    */
                    transaction.add(R.id.drawing_space, mDrawer, "draw_space");

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

    }

    @Override
    public void onClick(View v){


        switch(v.getId()){

            case R.id.colour_pallette:
                mDrawer.toggleColourMenu();
                break;

            case R.id.pen_width:
                mDrawer.togglePenWidthMenu();
                break;

            case R.id.clear_screen:
                mDrawer.clearScreen();
        }

    }

}
