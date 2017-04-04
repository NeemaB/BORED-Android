package cpen391.team6.bored.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import cpen391.team6.bored.R;

/**
 * Created by Corwin on 2017-03-28.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_fragment);

        //show class code under menu item
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        Preference pref = findPreference("classCodePref");
        pref.setSummary(sp.getString("classCodePref", ""));

        sp.registerOnSharedPreferenceChangeListener(this);
    }

    public String getClassCode() {
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        return sp.getString("classCodePref", "");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("classCodePref")) {
            Preference pref = findPreference(key);
            pref.setSummary(getClassCode());
        }
    }
}
