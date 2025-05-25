package com.diegoacosta127.handicapp;

public class Country {
    
    public int countryId;
    public String name;

    Country(){
    };
    
    public Country(int countryId, String name){
        this.countryId = countryId;
        this.name = name;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
