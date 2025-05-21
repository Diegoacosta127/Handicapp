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
class JFrame_EditCountry extends JFrame {
    private JComboBox comboCountry;
    private JTextField txtCountry;
    private JButton btnEdit, btnCancel;
    private DatabaseQuery app;
    
    public JFrame_EditCountry(DatabaseQuery dbApp) {
        this.app = dbApp;
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Edit country");
        setSize(400, 210);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel lblCountry = new JLabel("Select");
        lblCountry.setBounds(20, 20, 100, 30);
        add(lblCountry);
        JFrame_AddPlayer addPlayer = new JFrame_AddPlayer(app);
        comboCountry = addPlayer.comboCountry;
        comboCountry.setBounds(140, 20, 200, 30);
        add(comboCountry);
        
        JLabel lblNewName = new JLabel("Edit country name:");
        lblNewName.setBounds(20, 70, 120, 30);
        add(lblNewName);
        txtCountry = new JTextField();
        txtCountry.setBounds(140, 70, 200, 30);
        add(txtCountry);
        
        // Edit
        btnEdit = new JButton("Edit");
        btnEdit.setBounds(90, 120, 100, 30);
        btnEdit.addActionListener((ActionEvent e) -> {
            editCountry();
        });
        add(btnEdit);
        
        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, 120, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private void editCountry() {
        Country country = new Country();
        Object selectedCountry = comboCountry.getSelectedItem();
        if (selectedCountry instanceof Country) {
            country = (Country) selectedCountry;
        }
        String newCountry = txtCountry.getText();
        int countryId = country.getCountryId();
        try {
            if (countryId == 0 || "".equals(newCountry)) {
                JOptionPane.showMessageDialog(this, "All fields are required!",
                                              "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                app.updateQuery("UPDATE country "
                                + "SET name = '" + newCountry
                                + "' WHERE country_id = " + countryId + ";");
                JOptionPane.showMessageDialog(this, "Country edited successfully!");
                dispose();
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error editing country: " + ex,
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
