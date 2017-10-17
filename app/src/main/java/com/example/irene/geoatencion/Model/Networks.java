package com.example.irene.geoatencion.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Irene on 17/10/2017.
 */

public class Networks {

    @SerializedName("carCode")
    @Expose
    private String carCode;

    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("carBrand")
    @Expose
    private String carBrand;

    @SerializedName("carModel")
    @Expose
    private String carModel;

    @SerializedName("carPlate")
    @Expose
    private String carPlate;

    @SerializedName("carColor")
    @Expose
    private String carColor;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("serviceUser")
    @Expose
    private String serviceUser;

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceUser() {
        return serviceUser;
    }

    public void setServiceUser(String serviceUser) {
        this.serviceUser = serviceUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Networks{" +
                "carCode='" + carCode + '\'' +
                ", carBrand='" + carBrand + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carPlate='" + carPlate + '\'' +
                ", carColor='" + carColor + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", user=" + user +
                ", serviceUser='" + serviceUser + '\'' +
                '}';
    }
}
