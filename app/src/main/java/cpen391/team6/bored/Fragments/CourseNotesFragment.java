package cpen391.team6.bored.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.R;

/**
 * Created by neema on 2017-03-14.
 */
public class CourseNotesFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.course_notes_fragment_layout, container, false);

        //((MainActivity) getActivity()).updateDrawerList();

        //TODO: Write code for this fragment in a different branch

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

    }

}
