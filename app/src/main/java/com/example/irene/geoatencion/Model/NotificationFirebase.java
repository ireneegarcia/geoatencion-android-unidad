package com.example.irene.geoatencion.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Irene on 4/10/2017.
 */

public class NotificationFirebase {

    @SerializedName("networkLatitude")
    @Expose
    private String networkLatitude;

    @SerializedName("networkLongitude")
    @Expose
    private String networkLongitude;

    @SerializedName("networkAddress")
    @Expose
    private String networkAddress;

    @SerializedName("network")
    @Expose
    private String network;

    @SerializedName("status")
    @Expose
    private String status;

    public String getNetworkLatitude() {
        return networkLatitude;
    }

    public void setNetworkLatitude(String networkLatitude) {
        this.networkLatitude = networkLatitude;
    }

    public String getNetworkLongitude() {
        return networkLongitude;
    }

    public void setNetworkLongitude(String networkLongitude) {
        this.networkLongitude = networkLongitude;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }

    public NotificationFirebase(String networkLatitude, String networkLongitude, String networkAddress, String network, String status) {
        this.networkLatitude = networkLatitude;
        this.networkLongitude = networkLongitude;
        this.networkAddress = networkAddress;
        this.network = network;
        this.status = status;
    }

    @Override
    public String toString() {
        return "NotificationFirebase{" +
                "networkLatitude='" + networkLatitude + '\'' +
                ", networkLongitude='" + networkLongitude + '\'' +
                ", networkAddress='" + networkAddress + '\'' +
                ", network='" + network + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
