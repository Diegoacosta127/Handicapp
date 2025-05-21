/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handicapp;

import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;
/**
 *
 * @author diegoacosta127
 */
public class JFrame_EditSeason extends JFrame {
    private JComboBox<Season> comboSeason;
    private JComboBox<Country> comboCountry;
    private JTextField txtYear, txtDescription;
    private JButton btnEdit, btnCancel, btnAddCountry;
    private DatabaseQuery app;
    
    public JFrame_EditSeason(DatabaseQuery dbApp) {
        this.app = dbApp;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Edit season");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Seasons list
        JLabel lblSeasons = new JLabel("Select season");
        lblSeasons.setBounds(20, 20, 100, 30);
        add(lblSeasons);
        comboSeason = new JComboBox();
        comboSeason.setBounds(120, 20, 200, 30);
        comboSeason.setEditable(true);
        comboSeason.addItem(null);
        JFrame_AddTeam.loadSeasons(comboSeason, app);
        AutoCompletion.enable(comboSeason);
        add(comboSeason);
        
        // Countries list
        JLabel lblCountry = new JLabel("Country:");
        lblCountry.setBounds(20, 70, 100, 30);
        add(lblCountry);
        JFrame_AddSeason addSeason = new JFrame_AddSeason(app);
        comboCountry = addSeason.comboCountry;
        comboCountry.setBounds(120, 70, 200, 30);
        add(comboCountry);
        btnAddCountry = addSeason.btnAddCountry;
        btnAddCountry.setBounds(330, 70, 70, 30);
        add(btnAddCountry);
        
        // Year
        JLabel lblYear = new JLabel("Year:");
        lblYear.setBounds(20, 120, 100, 30);
        add(lblYear);
        txtYear = new JTextField();
        txtYear.setBounds(120, 120, 200, 30);
        add(txtYear);
        
        // Description
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setBounds(20, 170, 100, 30);
        add(lblDesc);
        txtDescription = new JTextField();
        txtDescription.setBounds(120, 170, 200, 30);
        add(txtDescription);
        
        // Edit
        btnEdit = new JButton("Edit");
        btnEdit.setBounds(100, 210, 100, 30);
        btnEdit.addActionListener((ActionEvent e) -> {
            editSeason();
        });
        add(btnEdit);
        
        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, 210, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private void editSeason(){
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId = season == null ? 0 : season.getSeasonId();
        
        Country country = (Country) comboCountry.getSelectedItem();
        int countryId = country == null ? 0 : country.getCountryId();
        int year;
        String description = txtDescription.getText();
        
        if (seasonId == 0 || countryId == 0 || txtYear.getText().isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!",
                                          "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            year = Integer.parseInt(txtYear.getText());
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Year must be a number");
            return;
        }
        try {
            app.updateQuery("UPDATE season "
                            + "SET country_id = ("
                                + "SELECT country_id "
                                + "FROM country "
                                + "WHERE name = '" + country +"')"
                            + ", year = " + year
                            + ", description = '" + description + "' "
                            + "WHERE season_id = " + seasonId + ";");
            JOptionPane.showMessageDialog(this, "Season edited successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error editing season: " + ex,
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
