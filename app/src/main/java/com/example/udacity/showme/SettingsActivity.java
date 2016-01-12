package com.example.udacity.showme;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPrefSummaryToValue(findPreference(getString(R.string.sort_by_key)));
    }

    public void bindPrefSummaryToValue(Preference pref){

        pref.setOnPreferenceChangeListener(this);
        onPreferenceChange(pref,
                PreferenceManager.getDefaultSharedPreferences(pref.getContext())
                        .getString(pref.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object value) {
        String stringValue = value.toString();

        if(pref instanceof ListPreference){
            ListPreference listPref = (ListPreference) pref ;
            int prefIndex = listPref.findIndexOfValue(stringValue);
            if(prefIndex >= 0){
                pref.setSummary(listPref.getEntries()[prefIndex]);
            }
        }else{
            pref.setSummary(stringValue);
        }
        return true;
    }
}
