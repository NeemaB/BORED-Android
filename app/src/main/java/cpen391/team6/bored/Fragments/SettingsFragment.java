package cpen391.team6.bored.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.Preference;

import cpen391.team6.bored.Activities.MainActivity;

/**
 * Created by Corwin on 2017-03-28.
 */

public class SettingsFragment extends PreferenceFragment {
    private String classCode = "XXXX";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onPreferenceClick(){

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
