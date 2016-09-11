package org.md2k.mcerebrum_medicationadherence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import org.md2k.mcerebrum_medicationadherence.model.Category;
import org.md2k.mcerebrum_medicationadherence.model.CategoryList;
import org.md2k.mcerebrum_medicationadherence.model.Medication;
import org.md2k.utilities.Report.Log;


/**
 * Created by nsaleheen on 8/11/2016.
 */
public class PrefsFragmentMedicationList extends PreferenceFragment {
    private static final String TAG = PrefsFragmentMedicationList.class.getSimpleName();
    CategoryList categoryList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_medication_list);

        categoryList = MainActivity.getMedications(Environment.getExternalStorageDirectory().getAbsolutePath() + "/motion_sense/selected_medications.json");
        setupPreferenceMedication();

        setSaveButton();
    }

    private void clearPreferenceScreenMedicationlist() {
        ((PreferenceCategory) findPreference("medication_list")).removeAll();
    }

    void deleteMedication(String d_m) {
        for (Category c : categoryList.categories) {
            for (int i = 0; i < c.medications.length; i++) {

                if (d_m.equals(c.medications[i].generic_name)) {
                    c.medications[i].generic_name = null;
                    return;
                }
            }
        }
    }

    private void setupPreferenceMedication() {

        PreferenceCategory medPreCat = (PreferenceCategory) findPreference("medication_list");
        for (Category c : categoryList.categories) {
            for (Medication m : c.medications) {
                if (m.generic_name == null) continue;
                Preference pref = createPreference(m);
                medPreCat.addPreference(pref);
            }
        }
    }

    private Preference createPreference(Medication m) {
        Preference preference = new Preference(getActivity());
        preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_medication_128_128));
        preference.setKey(m.generic_name);
        preference.setOnPreferenceClickListener(medicationListener());
        preference.setTitle(m.generic_name);
        preference.setSummary(m.us_band_name);

        return preference;
    }


    private Preference.OnPreferenceClickListener medicationListener() {
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                boolean isNew;
                final String medName = preference.getKey();
                final Intent intent = new Intent(getActivity(), AddMedicationActivity.class);

                Log.d(TAG, "deviceId=" + medName);
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Edit/Delete Selected Medication");
                alertDialog.setMessage("Edit/Delete Medication (" + preference.getTitle() + ")?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMedication(medName);
                                clearPreferenceScreenMedicationlist();
                                setupPreferenceMedication();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Edit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO
                            }
                        });

                alertDialog.show();
                return false;
            }
        };
    }

    private void setSaveButton() {
        final Button button_1 = (Button) getActivity().findViewById(R.id.button_1);
        button_1.setText("Save");
        button_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddMedicationActivity.saveSelectedMedication(categoryList);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        final Button button_2 = (Button) getActivity().findViewById(R.id.button_2);
        button_2.setText("Add More");
        button_2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewMedicationActivity.class);
                startActivity(intent);
            }
        });
        final Button button_3 = (Button) getActivity().findViewById(R.id.button_3);
        button_3.setText("Cancel");
        button_3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


}