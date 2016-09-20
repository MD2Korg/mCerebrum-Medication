package org.md2k.medication;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

import org.md2k.medication.model.CategoryList;
import org.md2k.utilities.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public static final String SELECTED_MEDICATION = "config.json";
    public static final String EMA_MEDICATION_FILENAME = "medication.json";
    public static final String EMA_MEDICATION_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.ema/";


    public static CategoryList readSelectedMedicationList() {
        try {
            return read(new InputStreamReader(new FileInputStream(CONFIG_DIRECTORY + SELECTED_MEDICATION)));
        } catch (FileNotFoundException e) {
            return new CategoryList();
        }
    }

    public static CategoryList readMedicationDB(Context context) {
        try {
            return read(new InputStreamReader(context.getAssets().open("medicationDB.json"), "UTF-8"));
        } catch (IOException e) {
            return new CategoryList();
        }
    }

    private static CategoryList read(InputStreamReader in) {
        CategoryList categoryList = new CategoryList();
        try {
            BufferedReader br = new BufferedReader(in);
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
        FileManager.deleteFile(CONFIG_DIRECTORY+SELECTED_MEDICATION);
        if(json.length()<5)
            return;

        try {
            FileManager.createDir(new File(CONFIG_DIRECTORY));
            FileWriter writer = new FileWriter(CONFIG_DIRECTORY+SELECTED_MEDICATION);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
