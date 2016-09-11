package org.md2k.mcerebrum_medicationadherence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.mcerebrum_medicationadherence.model.Category;
import org.md2k.mcerebrum_medicationadherence.model.CategoryList;
import org.md2k.mcerebrum_medicationadherence.model.Medication;
import org.md2k.utilities.Report.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class PrefsFragmentNewMedication extends PreferenceFragment {
    private static final String TAG = PrefsFragmentNewMedication.class.getSimpleName();
    CategoryList categoryList;
    CategoryList selectedCategoryList;

    MySharedPreference mySharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_new_medication);

        categoryList = Configuration.readMedicationDB();
        selectedCategoryList = Configuration.readSelectedMedicationList();

        createMySharedPreference();
        setPreferences();
        setSaveButton();
    }

    private void setPreferences() {
        PreferenceCategory medPreCat = (PreferenceCategory) findPreference("select_med_cat");
        ListPreference catPref = new ListPreference(getActivity());
        catPref.setTitle("--Please Select Medication Category--");
        catPref.setKey("med_category");
        String[] entries = new String[categoryList.categories.length];
        String[] entryValues = new String[categoryList.categories.length];
        int i = 0;
        for (Category c : categoryList.categories) {
            entries[i] = c.name;
            entryValues[i++] = c.name;
        }
        catPref.setEntries(entries);
        catPref.setEntryValues(entryValues);
        medPreCat.addPreference(catPref);

        PreferenceCategory medPreName = (PreferenceCategory) findPreference("select_med_name");
        ListPreference medNamePref = new ListPreference(getActivity());
        medNamePref.setKey("med_name");

        medNamePref.setTitle("--Please Select Medication Name--");
        medNamePref.setEnabled(false);
        medPreName.addPreference(medNamePref);

    }

    private void createMySharedPreference() {
        mySharedPreference = MySharedPreference.getInstance(getActivity());
        mySharedPreference.setListener(onSharedPreferenceChangeListener);
    }

    private Medication[] getMedications(String selectedCategory) {
        for (int i = 0; i < categoryList.categories.length; i++)
            if (categoryList.categories[i].name.equals(selectedCategory))
                return categoryList.categories[i].medications;
        return null;
    }

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "med_category":
                    String selectedCategory = mySharedPreference.getSharedPreferenceString("med_category");
                    Medication[] m = getMedications(selectedCategory);
                    updateMedicationNamePreference(m);
                    ListPreference lpLocation = (ListPreference) findPreference("med_category");
                    lpLocation.setTitle(selectedCategory);
                    lpLocation.setValue(mySharedPreference.getSharedPreferenceString("med_category"));
                    break;
                case "med_name":
                    String sCategory = mySharedPreference.getSharedPreferenceString("med_category");
                    String selectedMedName = mySharedPreference.getSharedPreferenceString("med_name");
                    ListPreference lpMedName = (ListPreference) findPreference("med_name");
                    lpMedName.setTitle(selectedMedName);
                    lpMedName.setValue(mySharedPreference.getSharedPreferenceString("med_name"));
                    if ("Other".equals(selectedMedName)) {
                        addOtherMedication();

                    } else {
                        Category c = getCategoryMedication(sCategory, selectedMedName);
                        lpMedName.setSummary(c.medications[0].us_band_name);
                    }
                    break;
            }
        }
    };

    private void addOtherMedication() {
        PreferenceCategory medManePreCat = (PreferenceCategory) findPreference("select_med_name");
        EditTextPreference etpOther = new EditTextPreference(getActivity());
        etpOther.setKey("med_other");
        medManePreCat.addPreference(etpOther);
    }

    private Category getCategoryMedication(String catName, String medName) {
        Category c = new Category();
        c.name = catName;
        for (Category ct : categoryList.categories)
            if (ct.name.equals(catName)) {
                for (Medication m : ct.medications)
                    if (m.generic_name.equals(medName)) {
                        c.medications = new Medication[1];
                        c.medications[0] = m;
                        break;
                    }
                break;
            }
        return c;

    }


    private void updateMedicationNamePreference(Medication[] m) {
        ListPreference medNamePref = (ListPreference) findPreference("med_name");
        medNamePref.setTitle("--Please Select Medication Name--");
        medNamePref.setEnabled(true);

        String[] entries = new String[m.length+1];
        String[] entryValues = new String[m.length+1];
        int i = 0;
        for (Medication md : m) {
            entries[i] = md.generic_name;
            entryValues[i++] = md.generic_name;
        }
        entries[i]="Other";
        entryValues[i]="Other";
        medNamePref.setEntries(entries);
        medNamePref.setEntryValues(entryValues);
    }

    private void setSaveButton() {
        final Button button_1 = (Button) getActivity().findViewById(R.id.button_1);
        button_1.setText("Save");
        button_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isAdd= addMedication();
                if (!isAdd) {
                    Toast.makeText(getActivity(), "Please select Medication", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), MainSettingsActivity.class);
                startActivity(intent);
            }
        });
        final Button button_2 = (Button) getActivity().findViewById(R.id.button_2);
        button_2.setText("Cancel");
        button_2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean addMedication() {
        String sCategory = mySharedPreference.getSharedPreferenceString("med_category");
        String sMedName = mySharedPreference.getSharedPreferenceString("med_name");

        if (sCategory == null || sCategory.length()==0 || sMedName==null || sMedName.length()==0) return false;

        Category c = getCategoryMedication(sCategory, sMedName);

        boolean isFound = false;
        for (Category ct : selectedCategoryList.categories) {
            if (ct.name.equals(sCategory)) {
                Medication[] mdList = ct.medications;
                isFound = true;
                ct.medications = new Medication[mdList.length + 1];
                for (int i = 0; i < mdList.length; i++) ct.medications[i] = mdList[i];
                ct.medications[mdList.length] = c.medications[0];
            }
        }
        if (!isFound) {
            Category[] catList = selectedCategoryList.categories;
            selectedCategoryList.categories = new Category[catList.length + 1];
            for (int i = 0; i < catList.length; i++)
                selectedCategoryList.categories[i] = catList[i];
            selectedCategoryList.categories[catList.length] = c;
        }

        Configuration.write(selectedCategoryList);
        return true;
    }


}