package cpen391.team6.bored.Fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.R;

/**
 * Created by neema on 2017-03-12.
 */
public class CreateNoteFragment extends Fragment {

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);

        /* We may want to contribute to the action bar menu */
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.create_note_fragment_layout, container, false);

        /* Ensure that the fragment is displayed in landscape mode */
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /* We don't want to interfere with the drawing space so disable gesture activation of the
         * drawer layout
         */
        ((MainActivity) getActivity()).lockDrawer();


        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }

}
