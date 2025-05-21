/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handicapp;

import java.sql.*;
import javax.swing.*;

/**
 *
 * @author diegoacosta127
 */
public class JFrame_AddCountry {
    
    public static int addCountry(String country, JComboBox<Country> comboCountry, DatabaseQuery app) {
        int countryId = getCountryId(country, app);
        
        if (countryId != -1) {
            JOptionPane.showMessageDialog(null, country + " already exists!",
                                         "Warning", JOptionPane.WARNING_MESSAGE);
            return countryId;
        }
        try {
            app.updateQuery("INSERT INTO country(name) VALUES ('" + country + "');");
            countryId = getCountryId(country, app);
            if (countryId == -1) {
                throw new SQLException("Error getting countryID.");
            }
            ResultSet rs = app.selectQuery("SELECT country_id, name FROM country ORDER BY name;");
            comboCountry.removeAllItems();
            comboCountry.addItem(null);
            while (rs.next()) {
                int id = rs.getInt("country_id");
                String name = rs.getString("name");
                comboCountry.addItem(new Country(id, name));
            }
            JOptionPane.showMessageDialog(null, country + " added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding country: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
        return countryId;
    }
    
    public static String countryName() {
        while (true) {
            String country = JOptionPane.showInputDialog(null, "Enter new country");
            if (country == null) {
                return null;
            }
            if (!country.trim().isEmpty()) {
                return country.trim();
            }
            JOptionPane.showMessageDialog(null, "Country name cannot be empty!",
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public static int getCountryId(String country, DatabaseQuery app) {
        try {
            ResultSet rs = app.selectQuery("SELECT country_id "
                                         + "FROM country "
                                         + "WHERE name = '" + country + "';");
            if (rs.next()) {
                return rs.getInt("country_id");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error retrieving country ID: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
}
