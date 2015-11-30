package com.team.redacted.stegogram;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import java.util.List;

/**
 * Created by Shawn on 11/30/2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add a button to the header list.
        if (hasHeaders() && isValidFragment("CompressionFragment")) {
            Button button = new Button(this);
            button.setText("Compression");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new CompressionFragment();
                    getFragmentManager().beginTransaction().add(fragment, "Compression")
                            .show(fragment)
                            .commit();
                }
            });
            setListFooter(button);
        }
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_settings, target);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class CompressionFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.preferences_compression, false);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences_compression);
        }
    }
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return CompressionFragment.class.getName().equals(fragmentName);
    }
}
