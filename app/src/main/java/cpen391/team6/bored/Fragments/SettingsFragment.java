package cpen391.team6.bored.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import cpen391.team6.bored.R;

/**
 * Created by Corwin on 2017-03-28.
 */

public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_fragment);

    }

    public String getClassCode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return sharedPref.getString("classCodePref", "");
    }

}

