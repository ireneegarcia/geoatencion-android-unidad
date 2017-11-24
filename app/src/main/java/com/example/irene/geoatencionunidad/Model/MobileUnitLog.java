package com.example.irene.geoatencionunidad.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Irene on 17/11/2017.
 */

public class MobileUnitLog {

    @SerializedName("mobileUnit")
    @Expose
    private String mobileUnit;
    @SerializedName("mobileUnitCarCode")
    @Expose
    private String mobileUnitCarCode;
    @SerializedName("description")
    @Expose
    private String description;

    public String getMobileUnit() {
        return mobileUnit;
    }

    public void setMobileUnit(String mobileUnit) {
        this.mobileUnit = mobileUnit;
    }

    public String getMobileUnitCarCode() {
        return mobileUnitCarCode;
    }

    public void setMobileUnitCarCode(String mobileUnitCarCode) {
        this.mobileUnitCarCode = mobileUnitCarCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
