package com.example.irene.geoatencionunidad.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Irene on 4/10/2017.
 */

public class NotificationFirebase {

    @SerializedName("clientLatitude")
    @Expose
    private String clientLatitude;

    @SerializedName("clientLongitude")
    @Expose
    private String clientLongitude;

    @SerializedName("clientAddress")
    @Expose
    private String clientAddress;

    @SerializedName("clientName")
    @Expose
    private String clientName;

    @SerializedName("status")
    @Expose
    private String status;

    public String getClientLatitude() {
        return clientLatitude;
    }

    public void setClientLatitude(String clientLatitude) {
        this.clientLatitude = clientLatitude;
    }

    public String getClientLongitude() {
        return clientLongitude;
    }

    public void setClientLongitude(String clientLongitude) {
        this.clientLongitude = clientLongitude;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NotificationFirebase(String clientLatitude, String clientLongitude,
                                String clientAddress, String clientName,
                                String status) {
        this.clientLatitude = clientLatitude;
        this.clientLongitude = clientLongitude;
        this.clientAddress = clientAddress;
        this.clientName = clientName;
        this.status = status;
    }
}
