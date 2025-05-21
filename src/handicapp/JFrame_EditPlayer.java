/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handicapp;

import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import java.sql.*;

/**
 *
 * @author diegoacosta127
 */
public class JFrame_EditPlayer extends JFrame {
    private JComboBox<Player> comboPlayer;
    private JComboBox<Country> comboCountry;
    private JComboBox txtMidName;
    private JTextField txtName, txtSurname;
    private JButton btnEdit, btnCancel, btnAddCountry;
    private DatabaseQuery app;
    
    public JFrame_EditPlayer(DatabaseQuery dbApp) {
        this.app = dbApp;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Edit Player");
        setSize(400, 360);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Players list
        JLabel lblPlayerName = new JLabel("Select player");
        lblPlayerName.setBounds(20, 20, 100, 30);
        add(lblPlayerName);
        comboPlayer = new JComboBox();
        comboPlayer.setBounds(120, 20, 200, 30);
        comboPlayer.setEditable(true);
        comboPlayer.addItem(null);
        List<Player> players = JFrame_EditTeam.getPlayers(app);
        players.forEach((player) -> {
            comboPlayer.addItem(player);
        });
        AutoCompletion.enable(comboPlayer);
        add(comboPlayer);
        
        // First name
        JLabel lblFirstName = new JLabel("First name:");
        lblFirstName.setBounds(20, 70, 100, 30);
        add(lblFirstName);
        txtName = new JTextField();
        txtName.setBounds(120, 70, 200, 30);
        add(txtName);
        
        // Last name
        JLabel lblLastName = new JLabel("Last name:");
        lblLastName.setBounds(20, 120, 100, 30);
        add(lblLastName);
        txtSurname = new JTextField();
        txtSurname.setBounds(120, 120, 200, 30);
        add(txtSurname);
        
        // Optional mid name
        JLabel lblMidName =  new JLabel("Midname (opt.):");
        lblMidName.setBounds(20, 170, 100, 30);
        add(lblMidName);
        JFrame_AddPlayer addPlayer = new JFrame_AddPlayer(app);
        txtMidName = addPlayer.txtMidName;
        txtMidName.setBounds(120, 170, 200, 30);
        add(txtMidName);
        
        // Birth country
        JLabel lblCurrentCountry = new JLabel("Current country: ");
        lblCurrentCountry.setBounds(20, 195, 300, 30);
        add(lblCurrentCountry);
        comboPlayer.addActionListener(e -> {
            String country = loadLblCountry();
            lblCurrentCountry.setText("Current country: " + country);
            lblCurrentCountry.revalidate();
            lblCurrentCountry.repaint();
        });
        JLabel lblCountry = new JLabel("Country:");
        lblCountry.setBounds(20, 220, 100, 30);
        add(lblCountry);
        comboCountry = addPlayer.comboCountry;
        comboCountry.setBounds(120, 220, 200, 30);
        add(comboCountry);
        btnAddCountry = addPlayer.btnAddCountry;
        btnAddCountry.setBounds(330, 220, 70, 30);
        add(btnAddCountry);
        
        // Edit
        btnEdit = new JButton("Edit");
        btnEdit.setBounds(120, 260, 100, 30);
        btnEdit.addActionListener((ActionEvent e) -> {
            editPlayer();
        });
        add(btnEdit);
        
        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, 260, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private String loadLblCountry () {
        Player player = (Player) comboPlayer.getSelectedItem();
        int playerId = player == null ? 0 : player.getPlayerId();
        try (ResultSet rs = app.selectQuery("SELECT name FROM country c "
                                            + "JOIN player p "
                                            + "ON c.country_id = p.country_id "
                                            + "WHERE p.player_id = " + playerId + ";")){
            while(rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading country: " + ex,
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return "";
    }
    
    private void editPlayer() {
        Player player = (Player) comboPlayer.getSelectedItem();
        int playerId = player == null ? 0 : player.getPlayerId();
        
        Country country = (Country) comboCountry.getSelectedItem();
        int countryId = country == null ? 0 : country.getCountryId();
        
        String firstName = txtName.getText();
        String lastName = txtSurname.getText();
        String midName = txtMidName.getSelectedItem() == null ? ""
                        : txtMidName.getSelectedItem().toString();
        try {
            if ("".equals(firstName) || "".equals(lastName) || countryId == 0) {
                JOptionPane.showMessageDialog(this, "All fields are required!",
                                              "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                app.updateQuery("UPDATE player "
                                + "SET first_name = '" + firstName
                                + "', last_name = '" + lastName
                                + "', mid_name = '" + midName
                                + "', country_id = " + countryId
                                + " WHERE player_id = " + playerId + ";");
                JOptionPane.showMessageDialog(this, "Player edited successfully!");
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error editing player: " + ex,
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        }
        
    }
}
