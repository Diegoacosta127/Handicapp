/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handicapp;

import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
/**
 *
 * @author diegoacosta127
 */
public class JFrame_AddMatch extends JFrame {
    private JComboBox<Season> comboSeason;
    private JComboBox<Team>[] comboTeams;
    private JLabel[][] lblPlayerNames = new JLabel[2][4];
    private JComboBox<Integer>[][] score = new JComboBox[2][4];
    private List<Player>[] players;
    private JCheckBox finalMatch;
    private JButton btnAdd, btnCancel;
    private DatabaseQuery app;

    public JFrame_AddMatch(DatabaseQuery dbApp) {
        players = new ArrayList[2];
        for (int i = 0; i < 2; i++) {
            players[i] = new ArrayList<>();
        }
        this.comboTeams = new JComboBox[2];
        this.app = dbApp;
        initComponents();
        loadTeams();
    }
    
    private void initComponents(){
        setTitle("Add match");
        setSize(500, 750);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel lblSeason = new JLabel("Season");
        lblSeason.setBounds(20, 20, 100, 30);
        add(lblSeason);
        comboSeason = new JComboBox();
        comboSeason.setBounds(120, 20, 360, 30);
        comboSeason.setEditable(true);
        comboSeason.addItem(null);
        JFrame_AddTeam.loadSeasons(comboSeason, app);
        AutoCompletion.enable(comboSeason);
        comboSeason.addActionListener(new ActionListenerImpl());
        add(comboSeason);
        int y = 70;
        for (int i = 0; i < comboTeams.length; i++) {
            final int index = i;
            JLabel lblHomeAway = new JLabel(i == 0 ? "Home team" : "Away team");
            lblHomeAway.setBounds(20, y, 100, 30);
            add(lblHomeAway);
            comboTeams[i] = new JComboBox();
            comboTeams[i].setBounds(120, y, 360, 30);
            comboTeams[i].setEditable(true);
            comboTeams[i].addItem(null);
            AutoCompletion.enable(comboTeams[i]);
            comboTeams[i].addActionListener((ActionEvent e) -> {
                if (comboTeams[index].getSelectedItem() != null) {
                    loadPlayers(index);
                    loadLblPlayers(index);
                }
            });
            add(comboTeams[i]);
            for (int j = 0; j < 4; j++) {
                y += 50;
                lblPlayerNames[index][j] = new JLabel("#" + (j + 1) + ": ");
                lblPlayerNames[index][j].setBounds(20, y, 250, 30);
                add(lblPlayerNames[index][j]);
                score[index][j] = new JComboBox<>();
                score[index][j].setEditable(true);
                score[index][j].addItem(null);
                for (int k = 0; k < 40; k++) {
                    score[index][j].addItem(k);
                }
                AutoCompletion.enable(score[index][j]);
                score[index][j].setBounds(280, y, 60, 30);
                add(score[index][j]);
            }
            y += 50;
        }
        
        finalMatch = new JCheckBox("Final season match");
        finalMatch.setBounds(200, y, 150, 30);
        add(finalMatch);
        
        // Add match button
        btnAdd = new JButton("Add");
        btnAdd.setBounds(120, 620, 100, 30);
        btnAdd.addActionListener((ActionEvent e) -> {
            addMatch();
            loadTeams();
            for (int i = 0; i < comboTeams.length; i++) {
                loadPlayers(i);
                loadLblPlayers(i);
                for (int j = 0; j < 4; j++) {
                    score[i][j].setSelectedIndex(0);
                }
            }
        });
        add(btnAdd);
        // Cancel button
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(300, 620, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private void loadTeams(){
        if (comboSeason.getSelectedItem() == null) {
            return;
        }
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId = season == null ? 0 : season.getSeasonId();
        try (ResultSet rs = app.selectQuery("SELECT t.name, ths.team_season "
                                          + "FROM team t "
                                          + "JOIN team_has_season ths "
                                          + "ON t.team_id = ths.team_id "
                                          + "WHERE ths.season_id = " + seasonId + " "
                                          + "ORDER BY t.name;")) {
            List<Team> teams = new ArrayList<>();
            while (rs.next()) {
                Team team = new Team();
                team.setTeamId(rs.getInt("team_season"));
                team.setName(rs.getString("name"));
                teams.add(team);
            }
            for (JComboBox<Team> comboTeam : comboTeams) {
                comboTeam.removeAllItems();
                comboTeam.addItem(null);
                teams.forEach((team) -> {
                    comboTeam.addItem(team);
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading teams: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPlayers(int i) {
        players[i].clear();
        
        if (comboTeams[i].getSelectedItem() == null) {
            return;
        }
        Team team = new Team();
        Object selectedTeam = comboTeams[i].getSelectedItem();
        if (selectedTeam instanceof Team) {
            team = (Team) selectedTeam;
        }
        int teamId = team.getTeamId();
        try (ResultSet rs = app.selectQuery("SELECT p.player_id, first_name, last_name, mid_name, thp.position "
                                          + "FROM player p "
                                          + "JOIN team_has_player thp "
                                          + "ON p.player_id = thp.player_id "
                                          + "WHERE team_id = " + teamId
                                          + " ORDER BY thp.position;")) {
            while (rs.next()) {
                Player player = new Player();
                player.setPlayerId(rs.getInt("player_id"));
                player.setFirstName(rs.getString("first_name"));
                player.setLastName(rs.getString("last_name"));
                player.setMidName(rs.getString("mid_name"));
                players[i].add(player);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading players: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadLblPlayers(int i) {
        for (int j = 0; j < 4; j++) {
            if (j < players[i].size()) {
                lblPlayerNames[i][j].setText("#" + (j + 1) + ": " + players[i].get(j).toString());
            } else {
                lblPlayerNames[i][j].setText("#" + (j + 1) + ":");
            }
        }
    }
    
    private void addMatch() {
        Match match = new Match();
        
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId = season == null ? 0 : season.getSeasonId();
        Team teamHome = new Team();
        Object selectedHome = comboTeams[0].getSelectedItem();
        if (selectedHome instanceof Team) {
            teamHome = (Team) selectedHome;
        }
        int teamHomeId = teamHome.getTeamId();
        Team teamAway = new Team();
        Object selectedAway = comboTeams[1].getSelectedItem();
        if (selectedAway instanceof Team) {
            teamAway = (Team) selectedAway;
        }
        int teamAwayId = teamAway.getTeamId();
        
        // Check if any JComboBox for score is not selected
        boolean nullScore = false;
        for (int i = 0; i < 2; i++) {
            for (JComboBox<Integer> currentScore : score[i]) {
                if (currentScore.getSelectedItem() == null) {
                    nullScore = true;
                    break;
                }
            }
        }
        if (seasonId == 0 || teamHomeId == 0 || teamAwayId == 0 || nullScore) {
            // Warning for any field not selected
            JOptionPane.showMessageDialog(this, "All fields are required!",
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (teamHomeId == teamAwayId) {
            // Warning for repeated teams
            JOptionPane.showMessageDialog(this, "Match must have different teams.",
                                          "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            match.setTeamHomeId(teamHomeId);
            match.setTeamAwayId(teamAwayId);
            try {
                int confirm;
                ResultSet rs = app.selectQuery("SELECT match_id "
                                             + "FROM match "
                                             + "WHERE (team_home = " + teamHomeId
                                             + " AND team_away = " + teamAwayId + ") "
                                             + "OR (team_home = " + teamAwayId
                                             + " AND team_away = " + teamHomeId + ");");
                if (rs.next() && !finalMatch.isSelected()) {
                    confirm = JOptionPane.showConfirmDialog(this, "These 2 teams has already played this season. Do you want to proceed?",
                                                            "Warning", JOptionPane.YES_NO_OPTION);
                    if (confirm == 1) {
                        return;
                    }
                }
                boolean isOver = false;
                if (finalMatch.isSelected()) {
                    confirm = JOptionPane.showConfirmDialog(this, "You are about to finish the season. Are you sure?",
                                                            "Warning", JOptionPane.YES_NO_OPTION);
                    if (confirm == 1) {
                        return;
                    } else {
                        isOver = true;
                    }
                }
                for (int i = 0; i < 2; i++) {
                    int teamScore = 0;
                    for (int j = 0; j < 4; j ++) {
                        teamScore += (int) score[i][j].getSelectedItem();
                    }
                    if (i == 0) {
                        match.setScoreHome(teamScore);
                    } else {
                        match.setScoreAway(teamScore);
                    }
                }
                try {
                    rs = app.selectQuery("INSERT INTO match(team_home, team_away, score_home, score_away) "
                                       + "VALUES (" + teamHomeId + ", " + teamAwayId + ", " + match.getScoreHome() + ", " + match.getScoreAway() + ") "
                                       + "RETURNING match_id;");
                    if (rs.next()) {
                        match.setMatchId(rs.getInt("match_id"));
                    }
                    for (int i = 0; i <2; i++) {
                        int j = 0;
                        for (Player player : players[i]){
                            app.updateQuery("INSERT INTO match_has_player(match_id, player_id, score) "
                                          + "VALUES(" + match.getMatchId() + ", " + player.getPlayerId() + ", " + score[i][j].getSelectedItem() + ");");
                            j++;
                        }
                    }
                    if (isOver) {
                        finishSeason();
                        JOptionPane.showMessageDialog(this, "Season has over successfully! Handicaps had been modified for next season.",
                                                     "Season is over!", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Match added successfully!",
                                                     "Match added", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding match: " +  ex,
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error retrieving match: " + ex,
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void finishSeason() {
        Season season = (Season) comboSeason.getSelectedItem();
        int seasonId = season.getSeasonId();
        String description = season.getDescription();
        int countryId = season.getCountryId();
        int year = season.getYear();
        List <Match> matches = new ArrayList<>();
        List <Integer> teamIds = new ArrayList<>();
        List <Integer> teamSeasonIds = new ArrayList<>();
        List <Integer> currentHcp = new ArrayList<>();
        try {
            // Query to get matches for a given season
            ResultSet rs = app.selectQuery("SELECT match_id, team_home, score_home, team_away, score_away "
                                         + "FROM match m JOIN team_has_season ths "
                                         + "ON (m.team_home = ths.team_season OR m.team_away = ths.team_season) "
                                         + "WHERE ths.season_id = " + seasonId
                                         + " GROUP BY m.match_id;");
            // Fill list with matches
            while (rs.next()) {
                Match match = new Match();
                match.setMatchId(rs.getInt("match_id"));
                match.setTeamHomeId(rs.getInt("team_home"));
                match.setScoreHome(rs.getInt("score_home"));
                match.setTeamAwayId(rs.getInt("team_away"));
                match.setScoreAway(rs.getInt("score_away"));
                matches.add(match);
            }
            
            rs = app.selectQuery("SELECT team_season, ths.team_id, SUM(thp.handicap) AS current_hcp "
                               + "FROM team_has_season ths "
                               + "JOIN team_has_player thp "
                               + "ON ths.team_season = thp.team_id "
                               + "WHERE season_id = " + seasonId
                               + " GROUP BY team_season;");
            while (rs.next()) {
                teamIds.add(rs.getInt("team_id")); // Global team id
                teamSeasonIds.add(rs.getInt("team_season")); // Season team id
                currentHcp.add(rs.getInt("current_hcp"));
            }
            float[] weightScore = {1.0f, 1.3f, 1.5f, 1.7f}; // Weight for each goal done depending player's position
            float[] weightPunishment = {0.3f, 0.5f, 0.7f, 1.0f}; // Weight for each goal recieved depending player's position
            int newSeasonId = -1;
            try {
                // Create next year's season
                rs = app.selectQuery("INSERT INTO season (country_id, year, description) "
                              + "VALUES (" + countryId + ", " + (year + 1) + ", '" + description + "') "
                              + "RETURNING season_id;");
                if (rs.next()) {
                    newSeasonId = rs.getInt("season_id");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error inserting new season: " + ex,
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
            for (int team : teamSeasonIds) {
                int teamIndex = teamSeasonIds.indexOf(team);
                int teamHcp = currentHcp.get(teamIndex);
                int teamId = teamIds.get(teamIndex);
                try {
                    // Create team for next year's season
                    rs = app.selectQuery("INSERT INTO team_has_season(team_id, season_id) "
                                       + "VALUES(" + teamId +", " + newSeasonId + ") "
                                       + "RETURNING team_season;");
                    if (rs.next()) {
                        teamId = rs.getInt("team_season");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error inserting new team: " + ex,
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                }
                int rivalId;
                int teamScore;
                int rivalScore;
                int diffHcp; // Handicap difference between teams
                int seasonBalance = 0;
                int balance = 0; // Score difference minus handicap difference between teams
                int[][] individualScore = new int[4][3]; // Stores id, handicap and total score for each player
                rs = app.selectQuery("SELECT handicap, player_id "
                                   + "FROM team_has_player "
                                   + "WHERE team_id = " + team
                                   + "ORDER BY position;");
                int i = 0;
                while (rs.next()) {
                    // i will be equal to position
                    individualScore[i][0] = rs.getInt("player_id");
                    individualScore[i][1] = rs.getInt("handicap");
                    // Individual score ([i][2]) will be 0 by default
                    i++;
                }
                float teamContribution = 0f;
                float individualContribution[] = new float[4];
                for (Match match : matches) {
                    if (match.getTeamHomeId() == team) {
                        rivalId = match.getTeamAwayId();
                        teamScore = match.getScoreHome();
                        rivalScore = match.getScoreAway();
                    } else if (match.getTeamAwayId() == team) {
                        rivalId = match.getTeamHomeId();
                        teamScore = match.getScoreAway();
                        rivalScore = match.getScoreHome();
                    } else {
                        // Current team doesn't play current match
                        continue;
                    }
                    int rivalIndex = teamSeasonIds.indexOf(rivalId);
                    int rivalHcp = currentHcp.get(rivalIndex);
                    int diffMatch = teamScore - rivalScore;
                    diffHcp = teamHcp - rivalHcp;
                    balance += diffMatch - diffHcp;
                    seasonBalance += balance;
                    int matchIndividualScore = 0;
                    float matchRatio = rivalHcp / teamHcp;
                    for (int j = 0; j < 4; j++) {
                        rs = app.selectQuery("SELECT score "
                                           + "FROM match_has_player "
                                           + "WHERE match_id = " + match.getMatchId()
                                           + " AND player_id = " + individualScore[j][0] + ";");
                        if (rs.next()) {
                            matchIndividualScore = rs.getInt("score");
                        }
                        float scoreWithWeight = matchIndividualScore * weightScore[j];
                        float punishmentWithWeight = rivalScore * weightPunishment[j] / 4;
                        float contribution = (scoreWithWeight - punishmentWithWeight) * matchRatio;
                        individualContribution[j] += contribution;
                        teamContribution += contribution;
                    }
                }
                for (int j = 0; j < 4; j++) {
                    float proportion = teamContribution == 0 ? 0.25f : individualContribution[j] / teamContribution;
                    int variationHcp = (int) Math.round(proportion) * seasonBalance;
                    individualScore[j][1] += variationHcp;
                    if (individualScore[j][1] > 10) individualScore[j][1] = 10;
                    if (individualScore[j][1] < -2) individualScore[j][1] = -2;
                    try {
                        app.updateQuery("INSERT INTO team_has_player(team_id, player_id, position, handicap) "
                                      + "VALUES(" + teamId + ", " + individualScore[j][0] + ", " + j + ", " + individualScore[j][1] + ");");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error creating player for new season: " + ex,
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error getting matches: " + ex);
        }
        try {
            app.updateQuery("UPDATE season "
                          + "SET is_over = true "
                          + "WHERE season_id = " + seasonId + ";");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error finishing season: " + ex,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ActionListenerImpl implements ActionListener {

        public ActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (comboSeason.getSelectedItem() != null) {
                loadTeams();
            } else {
                for(JComboBox<Team> combo : comboTeams) {
                    combo.removeAllItems();
                }
            }
        }
    }
}
