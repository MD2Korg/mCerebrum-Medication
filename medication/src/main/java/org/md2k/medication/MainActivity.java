package org.md2k.medication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.md2k.datakitapi.messagehandler.ResultCallback;
import org.md2k.medication.model.Category;
import org.md2k.medication.model.CategoryList;
import org.md2k.medication.model.Medication;
import org.md2k.utilities.permission.PermissionInfo;

import java.io.BufferedReader;
import java.io.File;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (!result) {
                    Toast.makeText(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    load();
                }
            }
        });
    }

    void load() {
        CategoryList selectedCategoryList = Configuration.readSelectedMedicationList();
        prepareTable(selectedCategoryList.categories);
    }
    @Override
    public void onResume(){
        load();
        super.onResume();
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

    void prepareTable(Category[] categories) {
        TableLayout ll = (TableLayout) findViewById(R.id.tableLayout);
        assert ll != null;
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
