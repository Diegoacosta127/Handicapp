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

import java.awt.Container;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class JFrame_AddTeam extends JFrame{
    private JComboBox<Team> comboTeam;
    public static JComboBox<Season> comboSeason = new JComboBox<>();
    public static JComboBox<Player> comboPlayer1, comboPlayer2, comboPlayer3, comboPlayer4;
    public static JComboBox<Integer> hcp1 , hcp2, hcp3, hcp4;
    private static JButton btnAdd, btnCancel, btnAddTeam, btnAddSeason, btnEditSeason,
                    btnAddPlayer, btnEditPlayer;
    private static DatabaseQuery app;
    
    public JFrame_AddTeam(DatabaseQuery dbApp){
        this.app = dbApp;
        initComponents();
        loadSeasons(comboSeason, app);
        loadPlayers();
        loadTeams();
        loadHcp();
    }
    
    private void initComponents(){
        setTitle("Add team");
        setSize(700, 420);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JComboBox[] comboPlayers = {comboPlayer1, comboPlayer2, comboPlayer3, comboPlayer4};
        
        // Team Name
        JLabel lblTeam = new JLabel("Team:");
        lblTeam.setBounds(20, 20, 100, 30);
        add(lblTeam);
        comboTeam = new JComboBox();
        comboTeam.setBounds(120, 20, 360, 30);
        comboTeam.setEditable(true);
        comboTeam.addItem(null);
        AutoCompletion.enable(comboTeam);
        add(comboTeam);
        btnAddTeam = new JButton("Add");
        btnAddTeam.setBounds(500, 20, 70, 30);
        btnAddTeam.addActionListener((ActionEvent e) -> {
            addTeamName();
        });
        add(btnAddTeam);
        
        // Season
        JLabel lblSeason = new JLabel("Season:");
        lblSeason.setBounds(20, 70, 100, 30);
        add(lblSeason);
        comboSeason = new JComboBox<>();
        comboSeason.setBounds(120, 70, 360, 30);
        comboSeason.setEditable(true);
        comboSeason.addItem(null);
        AutoCompletion.enable(comboSeason);
        comboSeason.addActionListener((ActionEvent e) -> {
            if (comboSeason.getSelectedItem() != null && !comboSeason.getSelectedItem().toString().isEmpty()) {
                loadPlayers();
            } else {
                for (JComboBox combo : comboPlayers) {
                    combo.removeAllItems();
                    combo.addItem("");
                }
            }
        });
        add(comboSeason);
        btnAddSeason = new JButton("Add");
        btnAddSeason.setBounds(500, 70, 70, 30);
        btnAddSeason.addActionListener((ActionEvent e) -> {
            JFrame_AddSeason addSeason = new JFrame_AddSeason(app);
            addSeason.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadSeasons(comboSeason, app);
                }
            });
            addSeason.setLocationRelativeTo(null);
            addSeason.setVisible(true);
        });
        add(btnAddSeason);
        btnEditSeason = new JButton("Edit");
        btnEditSeason.setBounds(580, 70, 70, 30);
        btnEditSeason.addActionListener((ActionEvent e) -> {
            JFrame_EditSeason editSeason = new JFrame_EditSeason(app);
            editSeason.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadSeasons(comboSeason, app);
                }
            });
            editSeason.setLocationRelativeTo(null);
            editSeason.setVisible(true);
        });
        add(btnEditSeason);
        
        // Players
        int y = 120;
        for (int i = 0; i < 4; i++) {
            comboPlayers[i] = new JComboBox<>();
            players(this, i + 1, y);
            y += 50;
        }
        
        // Add team
        btnAdd = new JButton("Add Team");
        btnAdd.setBounds(220, 320, 100, 30);
        btnAdd.addActionListener((ActionEvent e) -> {
            addTeam();
            loadTeams();
            loadSeasons(comboSeason, app);
        });
        add(btnAdd);
        
        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(400, 320, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    // Load players, handicaps and button to add a new or edit player into the frame
    public static JComboBox<Player> players(Container container, int num, int y) {
        JLabel lblPlayer = new JLabel("Player " + num + ":");
        lblPlayer.setBounds(20, y, 100, 30);
        container.add(lblPlayer);
        JComboBox comboPlayer = new JComboBox<>();
        comboPlayer.setBounds(120, y, 250, 30);
        comboPlayer.setEditable(true);
        comboPlayer.addItem(null);
        AutoCompletion.enable(comboPlayer);
        container.add(comboPlayer);
        
        JLabel lblHcp = new JLabel("Hcp:");
        lblHcp.setBounds(390, y, 30, 30);
        container.add(lblHcp);
        
        JComboBox<Integer> hcpCombo = null;
        switch (num)  {
            case 1 : {
                comboPlayer1 = comboPlayer;
                if (hcp1 == null) hcp1 = new JComboBox<>();
                hcpCombo = hcp1;
                break;
            }
            case 2 : {
                comboPlayer2 = comboPlayer;
                if (hcp2 == null) hcp2 = new JComboBox<>();
                hcpCombo = hcp2;
                break;
            }
            case 3 : {
                comboPlayer3 = comboPlayer;
                if (hcp3 == null) hcp3 = new JComboBox<>();
                hcpCombo = hcp3;
                break;
            }
            case 4 : {
                comboPlayer4 = comboPlayer;
                if (hcp4 == null) hcp4 = new JComboBox<>();
                hcpCombo = hcp4;
                break;
            }
            default : {
                hcpCombo = new JComboBox<>();
                break;
            }
        }
        
        hcpCombo.setBounds(430, y, 50, 30);
        hcpCombo.setEditable(true);
        AutoCompletion.enable(hcpCombo);
        container.add(hcpCombo);
    
        addBtnPlayer(container, y, comboPlayer);
        editBtnPlayer(container, y);
        return comboPlayer;
    }
    
    // Method for the btnAddPlayer in players()
    public static void addBtnPlayer (Container container, int a, JComboBox<Player> targetCombo) {
        btnAddPlayer = new JButton("Add");
        btnAddPlayer.setBounds(500, a, 70, 30);
        btnAddPlayer.addActionListener((ActionEvent e) -> {
            addPlayer(app, targetCombo);
        });
        container.add(btnAddPlayer);
        container.revalidate();
        container.repaint();
    }
    
    // Method for the btnEditPlayer in players()
    public static void editBtnPlayer(Container container, int a) {
        btnEditPlayer = new JButton("Edit");
        btnEditPlayer.setBounds(580, a, 70, 30);
        btnEditPlayer.addActionListener((ActionEvent e) -> {
            editPlayer(app);
        });
        container.add(btnEditPlayer);
        container.revalidate();
        container.repaint();
    }

    // Pops-up the frame to add a new player
    private static void addPlayer(DatabaseQuery app, JComboBox<Player> targetCombo) {
        JFrame_AddPlayer addPlayer = new JFrame_AddPlayer(app);
        addPlayer.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Player newPlayer = addPlayer.getNewPlayer();
                loadPlayers();
                if (newPlayer != null) {
                    targetCombo.setSelectedItem(newPlayer);
                }
            }
        });
        addPlayer.setLocationRelativeTo(null);
        addPlayer.setVisible(true);
    }
    
    // Pops-up the frame to edit a player
    private static void editPlayer(DatabaseQuery app){
        JFrame_EditPlayer editPlayer = new JFrame_EditPlayer(app);
        editPlayer.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                loadPlayers();
            }
        });
        editPlayer.setLocationRelativeTo(null);
        editPlayer.setVisible(true);
    }
    
    // Pops-up the frame to add the name of a new team
    private void addTeamName() {
        String newTeam = JOptionPane.showInputDialog(this, "Enter new team:");
        if (newTeam != null && !newTeam.trim().isEmpty()) {
            try {
                app.updateQuery("INSERT INTO team(name) VALUES ('" + newTeam + "');");
                ResultSet rs = app.selectQuery("SELECT team_id, name FROM team ORDER BY name;");
                comboTeam.removeAllItems();
                comboTeam.addItem(null);
                Team addedTeam = null;
                while (rs.next()) {
                    Team team = new Team();
                    team.setTeamId(rs.getInt("team_id"));
                    String name = rs.getString("name");
                    team.setName(name);
                    comboTeam.addItem(team);
                    if (name.equals(newTeam)) {
                        addedTeam = team;
                    }
                }
                if (addedTeam != null) {
                    comboTeam.setSelectedItem(addedTeam);
                }
                JOptionPane.showMessageDialog(this, newTeam + " added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding team: " + ex.getMessage(),
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (newTeam != null) {
            JOptionPane.showMessageDialog(this, "Team name can not be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            addTeamName();
        }
    }
    
    // Populates JComboBox with team names
    private void loadTeams() {
        comboTeam.removeAllItems();
        comboTeam.addItem(null);
        try (ResultSet rs = app.selectQuery("SELECT team_id, name FROM team ORDER BY name;")) {
            while (rs.next()) {
                Team team = new Team();
                team.setName(rs.getString("name"));
                team.setTeamId(rs.getInt("team_id"));
                comboTeam.addItem(team);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading teams: " + ex.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Populates JComboBox with seasons
    public static void loadSeasons(JComboBox<Season> comboSeason, DatabaseQuery app) {
        comboSeason.removeAllItems();
        comboSeason.addItem(null);
        try (ResultSet rs = app.selectQuery("SELECT season_id, country_id, year, description "
                                            + "FROM season "
                                            + "WHERE season.is_over = false ORDER BY year;")) {
            while (rs.next()) {
                Season season = new Season();
                season.setSeasonId(rs.getInt("season_id"));
                season.setCountryId(rs.getInt("country_id"));
                season.setYear(rs.getInt("year"));
                season.setDescription(rs.getString("description"));
                comboSeason.addItem(season);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading seasons: " + ex.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Populates JComboBox with players
    private static void loadPlayers() {
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId = season == null ? 0 : season.getSeasonId();
        
        try (ResultSet rs = app.selectQuery("SELECT player_id, first_name, last_name, mid_name "
                                            + "FROM player p "
                                            + "WHERE p.player_id NOT IN("
                                                + "SELECT thp.player_id "
                                                + "FROM team_has_player thp "
                                                + "JOIN team_has_season ths "
                                                + "ON thp.team_id = ths.team_season "
                                                + "WHERE ths.season_id = " + seasonId
                                            + ") ORDER BY p.last_name;")) {
            JComboBox[] comboPlayers = {comboPlayer1, comboPlayer2, comboPlayer3, comboPlayer4};
            for (JComboBox combo : comboPlayers) {
                combo.removeAllItems();
                combo.addItem(null);
            }
            while (rs.next()) { 
                Player player = new Player();
                player.setPlayerId(rs.getInt("player_id"));
                player.setFirstName(rs.getString("first_name"));
                player.setLastName(rs.getString("last_name"));
                String midName = rs.getString("mid_name") == null ? "" : rs.getString("mid_name");
                player.setMidName(midName);
                for (JComboBox combo : comboPlayers) {
                    combo.addItem(player);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading players: " + ex.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Populates JComboBox with handicap values
    public static void loadHcp () {
        JComboBox<Integer> [] hcp = getHcpCombos();
        for (JComboBox<Integer> combo: hcp){
            ActionListener[] listeners = combo.getActionListeners();
            for (ActionListener listener : listeners) {
                combo.removeActionListener(listener);
            }
            combo.removeAllItems();
            combo.addItem(null);
            for(int i = -2; i < 11; i++) {
                combo.addItem(i);
            }
            for (ActionListener listener : listeners) {
                combo.addActionListener(listener);
            }
        }
    }
    
    // Add team for a season with its players
    private void addTeam(){
        Team team = new Team();
        Object selectedTeam = comboTeam.getSelectedItem();
        if (selectedTeam instanceof Team) {
            team = (Team) selectedTeam;
        }
        int teamId = team.getTeamId();
        
        Season season = new Season();
        Object selectedSeason = comboSeason.getSelectedItem();
        if (selectedSeason instanceof Season) {
            season = (Season) selectedSeason;
        }
        int seasonId = season.getSeasonId();

        int notSelectedPlayer = 0, notFilledHcp = 0;
        JComboBox[] comboPlayers = {comboPlayer1, comboPlayer2, comboPlayer3, comboPlayer4};
        ArrayList <Integer> playersId = new ArrayList<>();
        JComboBox[] comboHcps = getHcpCombos();
        ArrayList <Integer> hcps = new ArrayList<>();
        int playerId;
        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            Object selectedPlayer = comboPlayers[i].getSelectedItem();
            if (selectedPlayer instanceof Player) {
                player = (Player) selectedPlayer;
            }
            playerId = player.getPlayerId();
            if (playerId != 0) {
                playersId.add(playerId);
            } else {
                notSelectedPlayer++;
            }
            Object selectedHcp = comboHcps[i].getSelectedItem();
            if (selectedHcp == null || selectedHcp.toString().trim().isEmpty()) {
                notFilledHcp++;
            } else {
                try {
                    hcps.add(Integer.parseInt(selectedHcp.toString().trim()));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Handicap value for player " + (i + 1) + " not valid",
                                                  "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        
        try {
            if (teamId == 0 || seasonId == 0 || notSelectedPlayer > 0 || notFilledHcp > 0) {
                JOptionPane.showMessageDialog(this, "All fields are required!",
                                              "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                ResultSet rs = app.selectQuery("SELECT team_season "
                                              + "FROM team_has_season "
                                              + "WHERE team_id = " + teamId
                                              + " AND season_id = " + seasonId + ";" );
                if (!rs.next()) {
                    // Team hasn't been added to season previously
                    Set<Integer> uniquePlayers = new HashSet<>(playersId);
                    if (uniquePlayers.size() < playersId.size()) {
                        JOptionPane.showMessageDialog(this, "You're trying to add repeated players.",
                                                      "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        String displayNamesAndHcp = "Team: " + team.toString() + "\nSeason: " + season.toString() + "\n";
                        Player player;
                        for (int i = 0; i < comboPlayers.length; i++) {
                            player = (Player) comboPlayers[i].getSelectedItem();
                            displayNamesAndHcp += player.toString() + "\tHcp: " + hcps.get(i) + "\n";
                        }
                        int confirm = JOptionPane.showConfirmDialog(this, displayNamesAndHcp, "Confirm your selection", JOptionPane.YES_NO_OPTION);
                        if (confirm == 0) {
                            app.updateQuery("INSERT INTO team_has_season(team_id, season_id) "
                                           + "VALUES (" + teamId + ", " + seasonId +");");
                            int teamSeasonId = getTeamSeasonId(teamId, seasonId);
                            for (int i = 0; i < comboPlayers.length; i++) {
                                player = (Player) comboPlayers[i].getSelectedItem();
                                app.updateQuery("INSERT INTO team_has_player(team_id, player_id, position, handicap) "
                                               + "VALUES (" + teamSeasonId + ", " + player.getPlayerId() + ", " + i + ", " + hcps.get(i) +");");
                            }
                            JOptionPane.showMessageDialog(null, team.toString() + " added at season " + season.toString() + " successfully!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "This team has been already added into this season.",
                                                  "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding team: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static JComboBox<Integer>[] getHcpCombos() {
        if (hcp1 == null) hcp1 = new JComboBox<>();
        if (hcp2 == null) hcp2 = new JComboBox<>();
        if (hcp3 == null) hcp3 = new JComboBox<>();
        if (hcp4 == null) hcp4 = new JComboBox<>();
        return new JComboBox[]{hcp1, hcp2, hcp3, hcp4};
    }
    
    public static int getTeamSeasonId(int teamId, int seasonId){
        int teamSeason = 0;
        try{
            ResultSet rs = app.selectQuery("SELECT team_season "
                                          + "FROM team_has_season "
                                          + "WHERE team_id = " + teamId
                                          + " AND season_id = " + seasonId + ";");
            while(rs.next()) {
                teamSeason = rs.getInt("team_season");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error retrieving team season ID: " + ex);
        }
        return teamSeason;
    }
}
