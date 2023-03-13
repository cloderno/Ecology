package com.example.ecologyproject.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Indice implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("value")
    @Expose
    private Double value;
    @SerializedName("element")
    @Expose
    private String element;
    @SerializedName("color_description")
    @Expose
    private String colorDescription;
    @SerializedName("green")
    @Expose
    private Double green;
    @SerializedName("yellow")
    @Expose
    private Integer yellow;
    @SerializedName("orange")
    @Expose
    private Integer orange;
    @SerializedName("red")
    @Expose
    private Integer red;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("effect")
    @Expose
    private String effect;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getColorDescription() {
        return colorDescription;
    }

    public void setColorDescription(String colorDescription) {
        this.colorDescription = colorDescription;
    }

    public Double getGreen() {
        return green;
    }

    public void setGreen(Double green) {
        this.green = green;
    }

    public Integer getYellow() {
        return yellow;
    }

    public void setYellow(Integer yellow) {
        this.yellow = yellow;
    }

    public Integer getOrange() {
        return orange;
    }

    public void setOrange(Integer orange) {
        this.orange = orange;
    }

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        this.red = red;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

}
