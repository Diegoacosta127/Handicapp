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

import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

class JFrame_EditTeam extends JFrame{
    private JComboBox<Team> comboTeam;
    private JComboBox<Season> comboSeason;
    private JTextField txtName;
    private JComboBox<Player>[] comboPlayers = new JComboBox[4];
    private JComboBox<Integer>[] hcp = new JComboBox[4];
    private JLabel[] lblPlayerNames = new JLabel[4];
    private JButton btnEdit, btnCancel;
    private static DatabaseQuery app;
    
    public JFrame_EditTeam(DatabaseQuery dbApp) {
        this.app = dbApp;
        initComponents();
        loadPlayers();
        loadTeams();
        JFrame_AddTeam.loadHcp();
    }
    
    private void initComponents(){
        setTitle("Edit team");
        setSize(680, 450);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Season
        JLabel lblSeason = new JLabel("Season:");
        lblSeason.setBounds(70, 20, 100, 30);
        add(lblSeason);
        comboSeason = new JComboBox();
        comboSeason.setBounds(170, 20, 360, 30);
        comboSeason.setEditable(true);
        comboSeason.addItem(null);
        JFrame_AddTeam.loadSeasons(comboSeason, app);
        AutoCompletion.enable(comboSeason);
        comboSeason.addActionListener((ActionEvent e) -> {
            if (comboSeason.getSelectedItem() != null) {
                loadTeams();
            } else {
                comboTeam.removeAllItems();
            }
        });
        add(comboSeason);
        
        // Name
        JLabel lblTeam = new JLabel("Team:");
        lblTeam.setBounds(70, 70, 100, 30);
        add(lblTeam);
        comboTeam = new JComboBox();
        comboTeam.setBounds(170, 70, 360, 30);
        comboTeam.setEditable(true);
        comboTeam.addItem(null);
        AutoCompletion.enable(comboTeam);
        comboTeam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboTeam.getSelectedItem() != null) {
                    for (int i = 0; i < 4; i++) {
                        lblPlayerNames[i].setText(loadLblPlayer(i));
                    }
                }
            }
        });
        add(comboTeam);
        JLabel lblNewName = new JLabel("New name (left empty to keep):");
        lblNewName.setBounds(20, 120, 200, 30);
        add(lblNewName);
        txtName = new JTextField();
        txtName.setBounds(220, 120, 360, 30);
        add(txtName);
        
        // Players
        
        int y = 170;
        this.hcp = new JComboBox[4];
        hcp = JFrame_AddTeam.getHcpCombos();
        for (int i = 0; i < 4; i++) {
            JLabel lblCurrentPlayer = new JLabel("Current Player #" + (i + 1) + ":");
            lblCurrentPlayer.setBounds(20, y - 25, 120, 30);
            JLabel lblPlayerName = new JLabel();
            lblPlayerName.setBounds(150, y - 25, 250, 30);
            lblPlayerName.setText(loadLblPlayer(i));
            lblPlayerNames[i] = lblPlayerName;
            add(lblPlayerName);
            add(lblCurrentPlayer);
            comboPlayers[i] = JFrame_AddTeam.players(this, i + 1, y);
            add(hcp[i]);
            y += 50;
        }
        
        // Edit
        btnEdit = new JButton("Edit");
        btnEdit.setBounds(160, 370, 100, 30);
        btnEdit.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e) {
                editTeam();
            }
        });
        add(btnEdit);
        
        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(340, 370, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private void loadTeams(){
        if (comboSeason.getSelectedItem() == null) {
            return;
        }
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId = season.getSeasonId();
        try (ResultSet rs = app.selectQuery("SELECT t.name, ths.team_id "
                                            + "FROM team t "
                                            + "JOIN team_has_season ths "
                                            + "ON t.team_id = ths.team_id "
                                            + "JOIN season s "
                                            + "ON ths.season_id = s.season_id "
                                            + "WHERE s.season_id = " + seasonId
                                            + " AND s.is_over = false "
                                            + "ORDER BY t.name;")) {
            comboTeam.removeAllItems();
            comboTeam.addItem(null);
            while (rs.next()) {
                Team team = new Team();
                team.setTeamId(rs.getInt("team_id"));
                team.setName(rs.getString("name"));
                comboTeam.addItem(team);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading teams: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static List<Player> getPlayers(DatabaseQuery app) {
        List<Player> players = new ArrayList<>();
        try (ResultSet rs = app.selectQuery("SELECT player_id, first_name, last_name, mid_name, country_id "
                                            + "FROM player "
                                            + "ORDER BY last_name;")) {
            while (rs.next()) {
                Player player = new Player();
                player.setPlayerId(rs.getInt("player_id"));
                player.setFirstName(rs.getString("first_name"));
                player.setLastName(rs.getString("last_name"));
                player.setMidName(rs.getString("mid_name"));
                player.setCountryId(rs.getInt("country_id"));
                players.add(player);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error getting players: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
        return players;
    }
    
    private void loadPlayers() {
        List<Player> players = getPlayers(app);
        for (JComboBox<Player> comboPlayer : comboPlayers) {
            players.forEach((player) -> {
                comboPlayer.addItem(player);
            });
        }
        this.revalidate();
        this.repaint();
    }
    
    private String loadLblPlayer(int position) {
        String lblPlayer = new String();
        Team team = (Team) comboTeam.getSelectedItem();
        int teamId = team == null ? 0 : team.getTeamId();
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId = season == null ? 0 : season.getSeasonId();
        try (ResultSet rs = app.selectQuery("SELECT p.player_id, first_name, last_name, mid_name "
                                            + "FROM player p "
                                            + "JOIN team_has_player thp "
                                            + "ON p.player_id = thp.player_id "
                                            + "JOIN team_has_season ths "
                                            + "ON thp.team_id = ths.team_season "
                                            + "WHERE ths.team_id = " + teamId
                                            + " AND ths.season_id = " + seasonId 
                                            + " AND thp.position = " + position + ";")) {
            while (rs.next()) {
                Player player = (Player) new Player();
                player.setFirstName(rs.getString("first_name"));
                player.setLastName(rs.getString("last_name"));
                player.setMidName(rs.getString("mid_name"));
                lblPlayer = player.toString();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading player: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
        return lblPlayer;
    }
    
    private void editTeam() {
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId;
        if (season != null) {
            seasonId = season.getSeasonId();
        } else {
            seasonId = 0;
        }
        
        Team team = (Team) comboTeam.getSelectedItem();
        int teamId;
        String teamName = "";
        if (team != null) {
            teamId = team.getTeamId();
            teamName = team.getName();
        } else {
            teamId = 0;
        }
        
        int teamSeasonId = 0;
        String newName = "".equals(txtName.getText()) ? teamName : txtName.getText();
        
        Player player = new Player();
        
        ArrayList <Player> players = new ArrayList<>();
        int notSelectedPlayers = 0, notFilledHcp = 0;
        for (int i = 0; i < comboPlayers.length; i++) {
            player = comboPlayers[i].getSelectedItem() == "" ? null : (Player) comboPlayers[i].getSelectedItem(); 
            players.add(player);
            if (player == null){
                notSelectedPlayers++;
            }
            if (hcp[i].getSelectedItem() == null) {
                notFilledHcp++;
            }
        }
        try {
            if (seasonId == 0 || teamId == 0 ||
                notSelectedPlayers > 0 || notFilledHcp > 0) {
                JOptionPane.showMessageDialog(this, "All fields are required!",
                                              "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                Set<Player> set = new HashSet<>(players);
                if (set.size() < players.size()) {
                    JOptionPane.showMessageDialog(this, "You're trying to add repeated players.",
                                                  "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    String displayNamesAndHcp = "Team: " + newName + "\nSeason: " + season.toString() + "\n";
                    for(int i = 0; i < comboPlayers.length; i++) {
                        displayNamesAndHcp += comboPlayers[i].getSelectedItem() + "\t - \tHcp: " + hcp[i].getSelectedItem() + "\n";
                    }
                    // Ask for comformity with players selected
                    int confirmEdit = JOptionPane.showConfirmDialog(this, displayNamesAndHcp,
                                                                    "Warning", JOptionPane.YES_NO_OPTION);
                    if (confirmEdit == 0) {
                        try {
                            ResultSet rs = app.selectQuery("SELECT team_season "
                                                         + "FROM team_has_season "
                                                         + "WHERE team_id = " + teamId
                                                         + " AND season_id = " + seasonId + ";");
                            if (rs.next()) {
                                teamSeasonId = rs.getInt("team_season");
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error retrieving team season ID: " + ex);
                        }
                        int confirmPlayer = 0;
                        int[] currentTeamId = new int[4];
                        // Check if players are in another teams at selected season
                        for(int i = 0; i < comboPlayers.length; i++) {
                            player = (Player) comboPlayers[i].getSelectedItem();
                            ResultSet rs = app.selectQuery("SELECT p.player_id, last_name, first_name, t.name, ths.team_season "
                                                         + "FROM player p "
                                                         + "JOIN team_has_player thp "
                                                         + "ON p.player_id = thp.player_id "
                                                         + "JOIN team_has_season ths "
                                                         + "ON thp.team_id = ths.team_season "
                                                         + "JOIN team t "
                                                         + "ON ths.team_id = t.team_id "
                                                         + "WHERE ths.season_id = " + seasonId
                                                         + " AND ths.team_id != " + teamId
                                                         + " AND p.player_id = " + player.getPlayerId() + ";");
                            // Warn for player currently in another team and ask if proceed
                            if(rs.next()){
                                String warningPlayer = comboPlayers[i].getSelectedItem()
                                                       + "is currently playing this season with "
                                                       + rs.getString("name")
                                                       + ". Do you want to proceed?";
                                confirmPlayer = JOptionPane.showConfirmDialog(this, warningPlayer, "Warning",
                                                                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                if (confirmPlayer == 1) {
                                    break;
                                } else {
                                    currentTeamId[i] = rs.getInt("team_season");
                                }
                            }
                        }
                        if (confirmPlayer == 0) {
                            for (int i = 0; i < 4; i++) {
                                player = (Player) comboPlayers[i].getSelectedItem();
                                // Delete player from previous team
                                if (currentTeamId[i] != 0) {
                                    try {
                                        app.updateQuery("DELETE FROM team_has_player "
                                                    + "WHERE team_id = " + currentTeamId[i]
                                                    + " AND player_id = " + player.getPlayerId() + ";");
                                    } catch (SQLException ex) {
                                        JOptionPane.showMessageDialog(this, "Error deleting player: " + ex);
                                    }
                                }
                                // Insert player into new team
                                int rowsUpdated = app.updateQuery("UPDATE team_has_player "
                                                                  + "SET player_id = " + player.getPlayerId()
                                                                  + ", handicap = " + hcp[i].getSelectedItem()
                                                                  + " WHERE team_id = " + teamSeasonId                                                                  + " AND position = " + i + ";");
                                if (rowsUpdated == 0) {
                                    try{
                                        app.updateQuery("INSERT INTO team_has_player(team_id, player_id, position, handicap)"
                                                        + "VALUES("+ teamSeasonId + ", " + player.getPlayerId() + ", " + i + ", " +hcp[i].getSelectedItem()+ ");");
                                    } catch (SQLException ex) {
                                        JOptionPane.showMessageDialog(this, "Error inserting player: " + ex);
                                    }
                                }
                            }
                            app.updateQuery("UPDATE team SET name = '" + newName + "' WHERE team_id = " + teamId + ";");
                            JOptionPane.showMessageDialog(this, newName + " edited successfully!");
                            // Set of previous teams, avoiding repeated IDs
                            Set<Integer> uniqueCurrentTeamId = new HashSet<>();
                            for(int x : currentTeamId) {
                                uniqueCurrentTeamId.add(x);
                            }
                            // Warn for a lack of players on previous team
                            for (int y : uniqueCurrentTeamId) {
                                ResultSet rs = app.selectQuery("SELECT COUNT(player_id), t.name "
                                                           + "FROM team_has_player thp "
                                                           + "JOIN team_has_season ths "
                                                           + "ON thp.team_id = ths.team_season "
                                                           + "JOIN team t "
                                                           + "ON ths.team_id = t.team_id "
                                                           + "WHERE thp.team_id = " + y
                                                           + " GROUP BY t.name;");
                                if (rs.next()){
                                    JOptionPane.showMessageDialog(this, rs.getString("name") + " now has " + rs.getString("count") + " players."
                                                                  , "Warning", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                            dispose();
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error editing team: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
