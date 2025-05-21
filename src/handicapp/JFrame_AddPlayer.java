/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handicapp;

/**
 *
 * @author diegoacosta127
 */
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class JFrame_AddPlayer extends JFrame {
    private JTextField txtName, txtSurname;
    public JComboBox txtMidName;
    public JComboBox<Country> comboCountry;
    private JButton btnAdd, btnCancel;
    public JButton btnAddCountry;
    private DatabaseQuery app;
    private Player newPlayer;

    public JFrame_AddPlayer(DatabaseQuery dbApp) {
        this.app = dbApp;
        initComponents();
        loadMidNames();
        loadCountries();
    }
    
    public static String[] getMidNames() {
        return new String[] {"Sr", "Jr", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    }

    private void initComponents() {
        setTitle("Add Player");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // First name
        JLabel lblName = new JLabel("First name:");
        lblName.setBounds(20, 20, 100, 30);
        add(lblName);
        txtName = new JTextField();
        txtName.setBounds(120, 20, 200, 30);
        add(txtName);

        // Last name
        JLabel lblSurname = new JLabel("Last name:");
        lblSurname.setBounds(20, 70, 100, 30);
        add(lblSurname);
        txtSurname = new JTextField();
        txtSurname.setBounds(120, 70, 200, 30);
        add(txtSurname);
        
        // Optional mid name
        JLabel lblMidName = new JLabel("Midname (opt.):");
        lblMidName.setBounds(20, 120, 100, 30);
        add(lblMidName);
        txtMidName = new JComboBox();
        txtMidName.setBounds(120, 120, 200, 30);
        txtMidName.setEditable(true);
        txtMidName.addItem("");
        AutoCompletion.enable(txtMidName);
        add(txtMidName);
        
        Player player = new Player();

        // Birth country
        JLabel lblCountry = new JLabel("Country:");
        lblCountry.setBounds(20, 170, 100, 30);
        add(lblCountry);

        comboCountry = new JComboBox<>();
        comboCountry.setBounds(120, 170, 200, 30);
        comboCountry.setEditable(true);
        comboCountry.addItem(null);
        AutoCompletion.enable(comboCountry);
        add(comboCountry);

        btnAddCountry = new JButton("Add");
        btnAddCountry.setBounds(330, 170, 70, 30);
        btnAddCountry.addActionListener(new ActionListenerImpl(player));
        add(btnAddCountry);

        // Add
        btnAdd = new JButton("Add Player");
        btnAdd.setBounds(120, 210, 100, 30);
        btnAdd.addActionListener((ActionEvent e) -> {
            Object selectedCountry = comboCountry.getSelectedItem();
            Country country = new Country();
            if (selectedCountry instanceof Country) {
                country = (Country) selectedCountry;
            }
            player.setCountryId(country.getCountryId());
            player.setFirstName(txtName.getText());
            player.setLastName(txtSurname.getText());
            player.setMidName(txtMidName.getSelectedItem().toString());
            addPlayer(player);
        });
        add(btnAdd);

        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(230, 210, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private void loadMidNames() {
        for (String midName : JFrame_AddPlayer.getMidNames()) {
            txtMidName.addItem(midName);
        }
    }

    private void loadCountries() {
        try {
            ResultSet rs = app.selectQuery("SELECT country_id, name FROM country ORDER BY name;");
            comboCountry.removeAllItems();
            comboCountry.addItem(null);
            while (rs.next()) {
                int id = rs.getInt("country_id");
                String name = rs.getString("name");
                comboCountry.addItem(new Country(id, name));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading countries: " + ex.getMessage());
        }
    }
    
    public Player getNewPlayer() {
        return this.newPlayer;
    }
    
    private void addPlayer(Player player) {
        if (player.getFirstName().isEmpty() || player.getLastName().isEmpty() ||
            player.getCountryId() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill the required fields");
            return;
        }
        try {
            ResultSet rs = app.selectQuery("INSERT INTO player(first_name, last_name, mid_name, country_id) " 
                          + "VALUES ('" + player.getFirstName() + "', '"
                                        + player.getLastName() + "', '"
                                        + player.getMidName() + "', "
                                        + player.getCountryId() + ") "
                                        + "RETURNING player_id;");
            newPlayer = new Player();
            newPlayer = player;
            if (rs.next()) {
                newPlayer.setPlayerId(rs.getInt("player_id"));
            }
            JOptionPane.showMessageDialog(this, "Player added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding player: " + ex.getMessage());
        }
    }

    private class ActionListenerImpl implements ActionListener {

        private final Player player;

        public ActionListenerImpl(Player player) {
            this.player = player;
        }

        public void actionPerformed(ActionEvent e) {
            String country = JFrame_AddCountry.countryName();
            if (country == null) {
                return;
            }
            int countryId = JFrame_AddCountry.addCountry(country, comboCountry, app);
            if (countryId != -1) {
                player.setCountryId(countryId);
            }
        }
    }
}
