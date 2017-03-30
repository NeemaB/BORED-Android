package cpen391.team6.bored.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.view.View;

import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.R;

/**
 * Created by Corwin on 2017-03-28.
 */

public class SettingsFragment extends PreferenceFragment {
    private String classCode = "XXXX";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void onClassCodeClicked (View view)
    {
        // create a new AlertDialog Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // set the message and the Title
        builder.setTitle("Enter Class Code:");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int id) {
                // put any "No/cancel" response code here
            }
        });
        builder.setPositiveButton ("OK", new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int id) {
                // put any "Yes" response code here
            }
        });


        // make and show the dialog box
        AlertDialog dialog = builder.create() ;
        dialog.show();

        // set the width and height of the dialog box
        dialog.getWindow().setLayout(400, 200);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public String GetClassCode(){
        return classCode;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
