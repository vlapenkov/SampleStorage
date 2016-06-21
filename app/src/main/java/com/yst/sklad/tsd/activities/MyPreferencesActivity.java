package com.yst.sklad.tsd.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.yst.sklad.tsd.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 05.06.2016.
 */
public class MyPreferencesActivity extends PreferenceActivity /*implements SharedPreferences.OnSharedPreferenceChangeListener*/  {
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String oldCatName;
    private EditTextPreference catName;
    private SharedPreferences preferences;
    private MultiSelectListPreference catArrayOfStorages;
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String value = preferences.getString("username", "");
        Set<String> arrayOfStorages = preferences.getStringSet("storagesArray", new HashSet<String>());
        catName = (EditTextPreference) findPreference("username");
        catArrayOfStorages= (MultiSelectListPreference) findPreference("storagesArray");

        catArrayOfStorages.setTitle(arrayOfStorages.toString());

        catArrayOfStorages.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {

               preference.setTitle(newValue.toString());
                return true;
            }


        });



        catName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {

                preference.setTitle(newValue.toString());
                return true;
            }
        });
        catName.setTitle(value);
   //     getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }


/*
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        catName = (EditTextPreference) findPreference("username");
        catName.setTitle("triggered");
    }


    */

}