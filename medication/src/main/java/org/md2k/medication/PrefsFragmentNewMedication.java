package org.md2k.medication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.md2k.medication.model.Category;
import org.md2k.medication.model.CategoryList;
import org.md2k.medication.model.Medication;
import org.md2k.utilities.UI.AlertDialogs;
import org.md2k.utilities.UI.OnClickListener;


public class PrefsFragmentNewMedication extends PreferenceFragment {
    private static final String TAG = PrefsFragmentNewMedication.class.getSimpleName();
    CategoryList categoryList;
    CategoryList selectedCategoryList;

    //    MySharedPreference mySharedPreference;
    ContextThemeWrapper contextThemeWrapper;
    EditText input;
    private String med_category="";
    private String med_name="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_new_medication);

        categoryList = Configuration.readMedicationDB(getActivity());
        selectedCategoryList = Configuration.readSelectedMedicationList();

        setPreferences();
        setSaveButton();
        contextThemeWrapper = new ContextThemeWrapper(getActivity(), org.md2k.utilities.R.style.app_theme_teal_light_dialog);
        input = new EditText(getActivity());
//        createMySharedPreference();
    }

    private void setPreferences() {
        ListPreference catPref = (ListPreference) findPreference("med_category");
        catPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                update(preference.getKey(), (String) newValue);
                return false;
            }
        });

        catPref.setTitle("--Please Select Medication Category--");
        String[] entries = new String[categoryList.categories.length];
        String[] entryValues = new String[categoryList.categories.length];
        int i = 0;
        for (Category c : categoryList.categories) {
            entries[i] = c.name;
            entryValues[i++] = c.name;
        }
        catPref.setEntries(entries);
        catPref.setEntryValues(entryValues);

        ListPreference medNamePref = (ListPreference) findPreference("med_name");
        medNamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                update(preference.getKey(), (String) newValue);
                return false;
            }
        });
        medNamePref.setTitle("--Please Select Medication Name--");
        medNamePref.setEnabled(false);
    }

    /*
        private void createMySharedPreference() {
            mySharedPreference = MySharedPreference.getInstance(getActivity());
            mySharedPreference.setListener(onSharedPreferenceChangeListener);
        }
    */
    private Medication[] getMedications(String selectedCategory) {
        for (int i = 0; i < categoryList.categories.length; i++)
            if (categoryList.categories[i].name.equals(selectedCategory))
                return categoryList.categories[i].medications;
        return null;
    }

    void update(String key, String value) {
        switch (key) {
            case "med_category":
                med_category=value;
                Medication[] m = getMedications(med_category);
                updateMedicationNamePreference(m);
                ListPreference lpLocation = (ListPreference) findPreference("med_category");
                lpLocation.setTitle(med_category);
                lpLocation.setValue(value);
                break;
            case "med_name":
                med_name=value;
                ListPreference lpMedName = (ListPreference) findPreference("med_name");
                lpMedName.setTitle(med_name);
                lpMedName.setValue(value);
                if ("Other".equals(med_name)) {
                    addOtherMedication();
                } else {
                    Category c = getCategoryMedication(med_category, med_name);
                    lpMedName.setSummary(c.medications[0].us_band_name);
                }
                break;
        }
    }


    private void addOtherMedication() {
        alertDialogEditText("Other Medication", "Please type medication name", R.drawable.ic_download_teal_48dp, "Add", "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, String result) {
                if (which != DialogInterface.BUTTON_POSITIVE) return;
                if (result.trim().length() == 0) return;
                update("med_name",result);
//                mySharedPreference.setSharedPreferencesString("med_name", result);
            }
        });

//        PreferenceCategory medManePreCat = (PreferenceCategory) findPreference("select_med_name");
//        EditTextPreference etpOther = new EditTextPreference(getActivity());
//        etpOther.setKey("med_other");
//        medManePreCat.addPreference(etpOther);
    }

    public void alertDialogEditText(String title, String message, int iconId, String positive, String negative, final OnClickListener onClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextThemeWrapper)
                .setTitle(title)
                .setIcon(iconId)
                .setMessage(message);

        input.setSingleLine();
        alertDialogBuilder.setView(input);

        if (positive != null)
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = input.getText().toString().trim();
                    onClickListener.onClick(dialog, which, str);
                }
            });
        if (negative != null)
            alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickListener.onClick(dialog, which, null);
                }
            });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        AlertDialogs.AlertDialogStyle(getActivity(), alertDialog);
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
                        return c;
                    }
            }
        c.medications = new Medication[1];
        c.medications[0] = new Medication();
        c.medications[0].generic_name = medName;
        c.medications[0].us_band_name = medName;
        return c;

    }


    private void updateMedicationNamePreference(Medication[] m) {
        ListPreference medNamePref = (ListPreference) findPreference("med_name");
        medNamePref.setTitle("--Please Select Medication Name--");
        medNamePref.setEnabled(true);

        String[] entries = new String[m.length + 1];
        String[] entryValues = new String[m.length + 1];
        int i = 0;
        for (Medication md : m) {
            entries[i] = md.generic_name;
            entryValues[i++] = md.generic_name;
        }
        entries[i] = "Other";
        entryValues[i] = "Other";
        medNamePref.setEntries(entries);
        medNamePref.setEntryValues(entryValues);
    }

    private void setSaveButton() {
        final Button button_1 = (Button) getActivity().findViewById(R.id.button_1);
        button_1.setText(R.string.button_save);
        button_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isAdd = addMedication();
                if (!isAdd) {
                    Toast.makeText(getActivity(), "Please select Medication", Toast.LENGTH_SHORT).show();
                    return;
                }
                getActivity().finish();
            }
        });
        final Button button_2 = (Button) getActivity().findViewById(R.id.button_2);
        button_2.setText(R.string.button_cancel);
        button_2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    public boolean addMedication() {
        String sCategory = med_category;
        String sMedName = med_name;

        if (sCategory == null || sCategory.length() == 0 || sMedName == null || sMedName.length() == 0)
            return false;

        Category c = getCategoryMedication(sCategory, sMedName);

        boolean isFound = false;
        for (Category ct : selectedCategoryList.categories) {
            if (ct.name.equals(sCategory)) {
                Medication[] mdList = ct.medications;
                for (Medication aMdList : mdList)
                    if (sMedName.equals(aMdList.generic_name)) return true;
                isFound = true;
                ct.medications = new Medication[mdList.length + 1];
                System.arraycopy(mdList, 0, ct.medications, 0, mdList.length);
                ct.medications[mdList.length] = c.medications[0];
            }
        }
        if (!isFound) {
            Category[] catList = selectedCategoryList.categories;
            selectedCategoryList.categories = new Category[catList.length + 1];
            System.arraycopy(catList, 0, selectedCategoryList.categories, 0, catList.length);
            selectedCategoryList.categories[catList.length] = c;
        }

        Configuration.write(selectedCategoryList);
        return true;
    }


}