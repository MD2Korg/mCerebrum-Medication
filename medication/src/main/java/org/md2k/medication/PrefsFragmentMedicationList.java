package org.md2k.medication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import org.md2k.medication.ema.EMA;
import org.md2k.medication.model.Category;
import org.md2k.medication.model.CategoryList;
import org.md2k.medication.model.Medication;
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

        setupPreferenceMedication();

        setSaveButton();
    }

    private void clearPreferenceScreenMedicationList() {
        ((PreferenceCategory) findPreference("medication_list")).removeAll();
    }

    void deleteMedication(String d_m) {
        for (int cc=0;cc< categoryList.categories.length;cc++) {
            Category c=categoryList.categories[cc];
            for (int i = 0; i < c.medications.length; i++) {
                if (d_m.equals(c.medications[i].generic_name)) {
                    c.medications=deleteMedication(c,i);
                    if(c.medications==null){
                        categoryList.categories=deleteCategory(categoryList.categories,cc);
                    }
                    Configuration.write(categoryList);
                    return;
                }
            }
        }
    }
    Category[] deleteCategory(Category[] categories, int index){
        if(categories.length==1){
            return new Category[0];
        }else{
            Category[] newCategories=new Category[categories.length-1];
            for(int i=0;i<index;i++)
                newCategories[i]=categories[i];
            for(int i=index+1;i<categories.length;i++)
                newCategories[i-1]=categories[i];
            return newCategories;
        }
    }
    Medication[] deleteMedication(Category c, int index){
        if(c.medications.length==1)
            return null;
        else{
            Medication newMedication[]=new Medication[c.medications.length-1];
            for(int i=0;i<index;i++)
                newMedication[i]=c.medications[i];
            for(int i=index+1;i<c.medications.length;i++)
                newMedication[i-1]=c.medications[i];
            return newMedication;
        }
    }
    @Override
    public void onResume(){
        setupPreferenceMedication();
        super.onResume();
    }

    private void setupPreferenceMedication() {
        categoryList = Configuration.readSelectedMedicationList();
        PreferenceCategory medPreCat = (PreferenceCategory) findPreference("medication_list");
        medPreCat.removeAll();

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
                final String medName = preference.getKey();
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
                                clearPreferenceScreenMedicationList();
                                setupPreferenceMedication();
                            }
                        });
                alertDialog.show();
                return false;
            }
        };
    }

    private void setSaveButton() {
        final Button button_1 = (Button) getActivity().findViewById(R.id.button_1);
        button_1.setText(R.string.button_save);
        button_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Configuration.write(categoryList);
                new EMA().createEMA(getActivity());
                getActivity().finish();
            }
        });
        final Button button_2 = (Button) getActivity().findViewById(R.id.button_2);
        button_2.setText(R.string.button_add_more);
        button_2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewMedicationActivity.class);
                startActivity(intent);
            }
        });
        final Button button_3 = (Button) getActivity().findViewById(R.id.button_3);
        button_3.setText(R.string.button_cancel);
        button_3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


}