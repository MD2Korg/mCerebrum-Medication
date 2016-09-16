package org.md2k.medication.model;

/**
 * Created by nsleheen on 7/22/2016.
 */
public class Medication {
    public String generic_name;
    public String us_band_name;

    public String getName() {
        return generic_name+" "+us_band_name;
    }
}
