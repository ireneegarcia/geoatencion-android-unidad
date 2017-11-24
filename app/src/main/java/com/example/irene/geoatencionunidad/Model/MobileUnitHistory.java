package com.example.irene.geoatencionunidad.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Irene on 14/11/2017.
 */

public class MobileUnitHistory {

    @SerializedName("mobileUnit")
    @Expose
    private String mobileUnit;
    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;

    public String getMobileUnit() {
        return mobileUnit;
    }

    public void setMobileUnit(String mobileUnit) {
        this.mobileUnit = mobileUnit;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
