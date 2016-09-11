package org.md2k.mcerebrum_medicationadherence;

import android.os.Environment;

import com.google.gson.Gson;

import org.md2k.mcerebrum_medicationadherence.model.CategoryList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * Created by nsaleheen on 8/14/2016.
 */
public class Configuration {
    public static final String CONFIG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.medication/";
    public static final String MEDICATION_DB = "medicationDB.json";
//    public static final String MEDICATION_DB = "medication_database.json";
    public static final String SELECTED_MEDICATION = "selected_medications.json";


    public static CategoryList readSelectedMedicationList() {
        return read(CONFIG_DIRECTORY + SELECTED_MEDICATION);
    }

    public static CategoryList readMedicationDB() {
        return read(CONFIG_DIRECTORY + MEDICATION_DB);
    }

    public static CategoryList read(String fileName) {
        CategoryList categoryList = new CategoryList();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            Gson gson = new Gson();
            categoryList = gson.fromJson(br, CategoryList.class);
            br.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return categoryList;
    }

    static public void write(CategoryList selectedCategoryList) {
        Gson gson = new Gson();
        String json = gson.toJson(selectedCategoryList);

        try {
            FileWriter writer = new FileWriter(CONFIG_DIRECTORY+SELECTED_MEDICATION);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
