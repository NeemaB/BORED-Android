package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cpen391.team6.bored.R;



/**
 * Created by neema on 2017-03-14.
 */
public class ViewNotesFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.view_notes_fragment_layout, container, false);



        return view;
    }

}
