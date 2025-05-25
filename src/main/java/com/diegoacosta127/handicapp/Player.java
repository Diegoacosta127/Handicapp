package com.diegoacosta127.handicapp;

public class Player {

    private int playerId;
    private String firstName;
    private String lastName;
    private String midName;
    private int countryId;
    
    Player(){
    }

    Player (String firstName, String lastName, String midName, int countryId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.midName = midName;
        this.countryId = countryId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
    
    
    
    @Override
    public String toString() {
        if (midName == null) {
            midName = "";
        }
        return lastName + ", " + firstName + " " + midName;
    }
}
