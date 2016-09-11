package org.md2k.mcerebrum_medicationadherence;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.md2k.mcerebrum_medicationadherence.model.Category;
import org.md2k.mcerebrum_medicationadherence.model.CategoryList;
import org.md2k.mcerebrum_medicationadherence.model.Medication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            CategoryList selectedCategoryList = MainActivity.getMedications(Environment.getExternalStorageDirectory().getAbsolutePath() + "/motion_sense/selected_medications.json");
            CategoryList categoryList = getMedications(new BufferedReader(new InputStreamReader(getAssets().open("medicationDB.json"), "UTF-8")));
            prepareTable(selectedCategoryList.categories);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    TableRow createDefaultRow() {
        TableRow row = new TableRow(this);
        TextView tvSensor = new TextView(this);
        tvSensor.setText("Name");
        tvSensor.setTypeface(null, Typeface.BOLD);
        tvSensor.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvCount = new TextView(this);
        tvCount.setText("Band");
        tvCount.setTypeface(null, Typeface.BOLD);
        tvCount.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvFreq = new TextView(this);
        tvFreq.setText("Category");
        tvFreq.setTypeface(null, Typeface.BOLD);
        tvFreq.setTextColor(getResources().getColor(R.color.teal_A700));
        row.addView(tvSensor);
        row.addView(tvCount);
        row.addView(tvFreq);
        return row;
    }

    public static boolean isExist(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    public static CategoryList getMedications(BufferedReader br) {
        CategoryList categoryList = new CategoryList();

        Gson gson = new Gson();
        categoryList = gson.fromJson(br, CategoryList.class);

        return categoryList;
    }

    public static CategoryList getMedications(String fileName) {
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

    void prepareTable(Category[] categories) {
        TableLayout ll = (TableLayout) findViewById(R.id.tableLayout);
        ll.removeAllViews();
        ll.addView(createDefaultRow());

        for (Category c : categories) {
            for (Medication m : c.medications) {
                if (m.generic_name == null) continue;
                TableRow row = new TableRow(this);
                TextView tvSensor = new TextView(this);
                tvSensor.setText(m.generic_name);
                TextView tvCount = new TextView(this);
                tvCount.setText(m.us_band_name);
                TextView tvFreq = new TextView(this);
                tvFreq.setText(c.name);
                row.addView(tvSensor);
                row.addView(tvCount);
                row.addView(tvFreq);
                ll.addView(row);
            }
        }

    }

    public void newMedication(View view) {
//        Intent intent = new Intent(this, AddMedicationActivity.class);
        Intent intent = new Intent(this, MainSettingsActivity.class);
        startActivity(intent);

    }

}
