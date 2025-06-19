package com.juancavr6.regibot.ui.fragment;

import static com.juancavr6.regibot.controller.SettingsValuesProvider.CATEGORY_GENERAL;
import static com.juancavr6.regibot.controller.SettingsValuesProvider.CATEGORY_THRESHOLD;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

import com.juancavr6.regibot.R;
import com.juancavr6.regibot.controller.SettingsValuesProvider;


public class ParamsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.parameters_preferences, rootKey);
        applyEditTextRules(getPreferenceScreen());
        Preference resetPref = findPreference("reset_general");
        Preference resetTPref = findPreference("reset_threshold");
        resetPref.setOnPreferenceClickListener(preference -> {
            SettingsValuesProvider.getInstance(getContext()).loadOnPreferences(getContext(), CATEGORY_GENERAL);

            // Reload the preferences screen to reflect the changes
            onCreatePreferences(savedInstanceState, rootKey);

            Toast.makeText(getContext(), getString(R.string.displayText_reset), Toast.LENGTH_SHORT).show();
            return true;
        });
        resetTPref.setOnPreferenceClickListener(preference -> {
            SettingsValuesProvider.getInstance(getContext()).loadOnPreferences(getContext(),CATEGORY_THRESHOLD);

            // Reload the preferences screen to reflect the changes
            onCreatePreferences(savedInstanceState, rootKey);

            Toast.makeText(getContext(), getString(R.string.displayText_reset), Toast.LENGTH_SHORT).show();
            return true;
        });

    }

    // Method to apply rules to EditTextPreferences
    private void applyEditTextRules(PreferenceGroup group) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference pref = group.getPreference(i);

            if (pref instanceof PreferenceGroup) {
                // Recursividad para anidadas
                applyEditTextRules((PreferenceGroup) pref);
            } else if (pref instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) pref;

                String summary = editTextPref.getSummary()!=null ? editTextPref.getSummary().toString() : "";
                String stringSymbol;
                int maxValue,minValue;
                if (getString(R.string.preferences_key_max_results).equals(pref.getKey()))
                {
                    stringSymbol = " max";
                    maxValue = 20;
                    minValue = 1;
                }
                else if (getString(R.string.preferences_key_cycle_interval).equals(pref.getKey())){
                    stringSymbol = " ms";
                    maxValue = 2000;
                    minValue =100 ;
                }
                else {
                    stringSymbol = " ms";
                    maxValue = 10000;
                    minValue = 500;
                }


                editTextPref.setSummaryProvider(preference -> {
                    String value = editTextPref.getText();

                    return (value == null || value.isEmpty()) ? summary : summary + "\n" + value + stringSymbol;
                });

                editTextPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    try {
                        int value = Integer.parseInt((String) newValue);

                        // Ejemplo genérico: mínimo 3 caracteres
                        if (value < minValue || value > maxValue) {
                            Toast.makeText(getContext(), getString(R.string.displayText_invalid_input), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }
                    catch (NumberFormatException e) {
                        Toast.makeText(getContext(), getString(R.string.displayText_invalid_input), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                 });

                editTextPref.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                });
            }
        }
    }

}