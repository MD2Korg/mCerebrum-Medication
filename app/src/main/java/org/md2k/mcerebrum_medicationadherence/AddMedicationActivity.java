package org.md2k.mcerebrum_medicationadherence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.md2k.mcerebrum_medicationadherence.model.Category;
import org.md2k.mcerebrum_medicationadherence.model.CategoryList;
import org.md2k.mcerebrum_medicationadherence.model.Medication;
import org.md2k.mcerebrum_medicationadherence.model.MedicationList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsleheen on 8/9/2016.
 */
public class AddMedicationActivity extends Activity {

    static String[] categorySpinner;
    String[] meds;

    CategoryList categoryList;
    CategoryList selectedCategoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Medication");
        setContentView(R.layout.activity_new_medication);
        try {
            categoryList = MainActivity.getMedications(new BufferedReader(new InputStreamReader(getAssets().open("medicationDB.json"), "UTF-8")));
            selectedCategoryList = MainActivity.getMedications(Environment.getExternalStorageDirectory().getAbsolutePath() + "/motion_sense/selected_medications.json");
            setCategorySpinner();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCategorySpinner() {
        categorySpinner = new String[categoryList.categories.length];
        for (int i = 0; i < categoryList.categories.length; i++)
            categorySpinner[i] = categoryList.categories[i].name;
        findViewById(R.id.button_1);

        Spinner sCatName = (Spinner) findViewById(R.id.catName);
        ArrayAdapter<String> adapterCatName = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categorySpinner);

        sCatName.setAdapter(adapterCatName);

        sCatName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner sCatName = (Spinner) findViewById(R.id.catName);
                Spinner sMed = (Spinner) findViewById(R.id.medName);
                Medication[] m = getMedications((String) sCatName.getSelectedItem());
                meds = new String[m.length];
                for (int i = 0; i < m.length; i++)
                    meds[i] = m[i].getName();
                ArrayAdapter<String> adapterMedName = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, meds);

                sMed.setAdapter(adapterMedName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private Medication[] getMedications(String selectedCategory) {
        for (int i = 0; i < categoryList.categories.length; i++)
            if (categoryList.categories[i].name.equals(selectedCategory))
                return categoryList.categories[i].medications;
        return null;
    }

    public void addMedication(View view) {
        Spinner sCatName = (Spinner) findViewById(R.id.catName);
        String catName = (String) sCatName.getSelectedItem();
        Spinner sMed = (Spinner) findViewById(R.id.medName);
        String medName = (String) sMed.getSelectedItem();

        Category c = getCategoryMedication(catName, medName);

        boolean isFound = false;
        for (Category ct : selectedCategoryList.categories) {
            if (ct.name.equals(catName)) {
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

        saveSelectedMedication(selectedCategoryList);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


    }

    static public void saveSelectedMedication(CategoryList selectedCategoryList) {
        Gson gson = new Gson();
        String json = gson.toJson(selectedCategoryList);

        try {
            FileWriter writer = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/motion_sense/selected_medications.json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Category getCategoryMedication(String catName, String medName) {
        Category c = new Category();
        c.name = catName;
        for (Category ct : categoryList.categories)
            if (ct.name.equals(catName)) {
                for (Medication m : ct.medications)
                    if (m.getName().equals(medName)) {
                        c.medications = new Medication[1];
                        c.medications[0] = m;
                        break;
                    }
                break;
            }
        return c;

    }

    public void cancelMedication(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
