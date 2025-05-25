package com.diegoacosta127.handicapp;

import java.sql.*;
import javax.swing.JOptionPane;

public class Season {

    public int seasonId;
    public int countryId;
    public int year;
    public String description;
    public boolean seasonOver;

    Season() {
    }

    public int getSeasonId() {
        return seasonId;
    }
    
    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSeasonOver() {
        return seasonOver;
    }

    public void setSeasonOver(boolean isOver) {
        this.seasonOver = isOver;
    }
    
    @Override
    public String toString() {
        DatabaseQuery app = new DatabaseQuery();
        String countryName = new String();
        try{
            ResultSet rs = app.selectQuery("SELECT name FROM country WHERE country_id = " + countryId + ";");
            while(rs.next()){
                countryName = rs.getString("name");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error retrieving country name: "+ ex,
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return countryName + ", " + year + ": " + description;
    }
}
