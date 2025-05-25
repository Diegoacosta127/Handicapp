/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diegoacosta127.handicapp;

/**
 *
 * @author diegoacosta127
 */
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class JFrame_AddSeason extends JFrame {
    private JTextField txtDescription, txtYear;
    public JComboBox<Country> comboCountry;
    private JButton btnAdd, btnCancel;
    public JButton btnAddCountry;
    private DatabaseQuery app;
    
    public JFrame_AddSeason(DatabaseQuery dbApp){
        this.app = dbApp;
        initComponents();
        loadCountries();
    }
    
    private void initComponents(){
        setTitle("Add Season");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        Season season = new Season();
        
        // Country
        JLabel lblCountry = new JLabel("Country:");
        lblCountry.setBounds(20, 20, 100, 30);
        add(lblCountry);
        comboCountry = new JComboBox<>();
        comboCountry.setBounds(120, 20, 200, 30);
        comboCountry.setEditable(true);
        comboCountry.addItem(null);
        AutoCompletion.enable(comboCountry);
        add(comboCountry);
        
        JFrame_AddPlayer addPlayer = new JFrame_AddPlayer(app);
        
        btnAddCountry = new JButton("Add");
        btnAddCountry.setBounds(330, 20, 70, 30);
        btnAddCountry.addActionListener((ActionEvent e) -> {
            String country = JFrame_AddCountry.countryName();
            if (country == null) {
                return;
            }
            int countryId = JFrame_AddCountry.addCountry(country, comboCountry, app);
            if (countryId != -1) {
                season.setCountryId(countryId);
            }
        });
        add(btnAddCountry);
        
        // Description
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setBounds(20, 70, 100, 30);
        add(lblDesc);
        txtDescription = new JTextField();
        txtDescription.setBounds(120, 70, 200, 30);
        add(txtDescription);
        
        // Year
        JLabel lblYear = new JLabel("Year:");
        lblYear.setBounds(20, 120, 100, 30);
        add(lblYear);
        txtYear = new JTextField();
        txtYear.setBounds(120, 120, 200, 30);
        add(txtYear);
        
        // Add
        btnAdd = new JButton("Add Season");
        btnAdd.setBounds(100, 200, 120, 30);
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Country country = new Country();
                Object selectedCountry = (Country) comboCountry.getSelectedItem();
                if (selectedCountry instanceof Country) {
                    country = (Country) selectedCountry;
                }
                if (country.getCountryId() != 0) {
                    season.setCountryId(country.getCountryId());
                }
                season.setDescription(txtDescription.getText());
                try {
                    season.setYear(Integer.parseInt(txtYear.getText()));
                } catch(NumberFormatException ex) {
                    season.setYear(0);
                }
                season.setSeasonOver(false);
                addSeason(season);
            }
        });
        add(btnAdd);
                
        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, 200, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private void loadCountries() {
        try (ResultSet rs = app.selectQuery("SELECT country_id, name FROM country ORDER BY name")) {
            while (rs.next()) {
                int id = rs.getInt("country_id");
                String name = rs.getString("name");
                comboCountry.addItem(new Country(id, name));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading countries: " + ex.getMessage());
        }
    }
    
    private void addSeason(Season season) {
        if ("".equals(season.getDescription()) || season.getCountryId() == 0 ||
            (season.getYear() == 0 && "".equals(txtYear.getText()))) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }
        if (season.getYear() == 0 && !"".equals(txtYear.getText())) {
            JOptionPane.showMessageDialog(null, "Year must be a number");
            return;
        }
        
        try {
            app.updateQuery("INSERT INTO season(country_id, year, description) "
                            + "VALUES (" + season.getCountryId() + ", " + season.getYear()
                            + ", '" + season.getDescription() + "');");
            JOptionPane.showMessageDialog(this, "Season added successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding season: " + ex.getMessage());
        }
    }
}