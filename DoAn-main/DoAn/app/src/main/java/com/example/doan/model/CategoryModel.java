package com.example.doan.model;

import android.annotation.SuppressLint;

public class CategoryModel {
    private String Id;
    private String catName;
    private String noOfSets;
    private String setCounter;

     public CategoryModel( String Id, String catName, String noOfSets, String setCounter){
         this.Id = Id;
         this.catName = catName;
         this.noOfSets = noOfSets;
         this.setCounter = setCounter;

     }
    public String getSetCounter() {
        return setCounter;
    }
    public void setSetCounter(String setCounter) {
        this.setCounter = setCounter;
    }
    public String getId() {
        return Id;
    }
    public void setId(String Id){
         this.Id = Id;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
    public String getNoOfSets() {
        return noOfSets;
    }

    public void setNoOfSets(String noOfSets) {
        this.noOfSets = noOfSets;
    }
}
