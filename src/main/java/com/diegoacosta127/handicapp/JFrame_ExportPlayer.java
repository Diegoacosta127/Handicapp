/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diegoacosta127.handicapp;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author diegoacosta127
 */
public class JFrame_ExportPlayer extends JFrame {
    private DatabaseQuery app;
    private JComboBox<Player> comboPlayers;
    private JButton btnExport;
    private JButton btnCancel;
    
    public JFrame_ExportPlayer(DatabaseQuery dbApp) {
        this.app = dbApp;
        initComponents();
        loadPlayers(comboPlayers, app);
    }
    
    private void initComponents(){
        setTitle("Export player history");
        setSize(400, 250);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Players
        JLabel lblPlayer = new JLabel("Choose player");
        lblPlayer.setBounds(150, 20, 100, 30);
        add(lblPlayer);
        comboPlayers = new JComboBox();
        comboPlayers.setBounds(70, 70, 280, 30);
        comboPlayers.setEditable(true);
        AutoCompletion.enable(comboPlayers);
        comboPlayers.addItem(null);
        add(comboPlayers);
        
        // Export
        btnExport = new JButton("Export");
        btnExport.setBounds(70, 150, 100, 30);
        btnExport.addActionListener((ActionEvent e) -> {
            try {
                exportPlayer();
                comboPlayers.setSelectedIndex(0);
            } catch (DocumentException ex) {
                Logger.getLogger(JFrame_ExportSeason.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        add(btnExport);
        
        // Cancel
        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(250, 150, 100, 30);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
    
    private void loadPlayers(JComboBox<Player> comboPlayers, DatabaseQuery app) {
        try(ResultSet rs = app.selectQuery("SELECT DISTINCT player_id, first_name, last_name, mid_name "
                                         + "FROM player "
                                         + "JOIN team_has_player thp USING (player_id) "
                                         + "JOIN team_has_season ths ON thp.team_id = ths.team_season "
                                         + "JOIN season s USING (season_id) "
                                         + "WHERE s.is_over = true "
                                         + "ORDER BY last_name;")) {
            while (rs.next()) {
                Player player = new Player();
                player.setPlayerId(rs.getInt("player_id"));
                player.setFirstName(rs.getString("first_name"));
                player.setLastName(rs.getString("last_name"));
                player.setMidName(rs.getString("mid_name"));
                comboPlayers.addItem(player);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading players: " + ex,
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportPlayer() throws DocumentException {
        try {
            File pdfDir = new File("pdf/");
            if (!pdfDir.exists()) pdfDir.mkdirs();
            Document doc = new Document(PageSize.A4, 20, 20, 20, 20);
            Player player = new Player();
            Object selectedPlayer = comboPlayers.getSelectedItem();
            if (selectedPlayer instanceof Player) {
                player = (Player) selectedPlayer;
            }
            PdfWriter.getInstance(doc, new FileOutputStream("pdf/" + player.toString() + ".pdf"));
            doc.open();
            Font font = new Font(Font.FontFamily.HELVETICA, 30f, Font.BOLD);
            Paragraph paragraph = new Paragraph(player.toString(), font);
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(paragraph);
            // Seasons
            try (ResultSet rs = app.selectQuery("SELECT DISTINCT s.season_id, s.country_id, s.year, s.description, mhp.team_season "
                                              + "FROM season s "
                                              + "JOIN team_has_season USING (season_id)"
                                              + "JOIN match_has_player mhp USING (team_season) "
                                              + "WHERE mhp.player_id = " + player.getPlayerId()
                                              + " AND s.is_over = true;")) {
                float[] colWidth = new float[] {100f, 15f, 100f, 15f};
                while (rs.next()) {
                    Season season = new Season();
                    season.setSeasonId(rs.getInt("season_id"));
                    season.setCountryId(rs.getInt("country_id"));
                    season.setYear(rs.getInt("year"));
                    season.setDescription(rs.getString("description"));
                    int teamSeasonId = rs.getInt("team_season");
                    PdfPTable table = new PdfPTable(4);
                    PdfPCell cell = new PdfPCell(new Phrase(season.toString()));
                    cell.setColspan(4);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    // Matches
                    ResultSet rsMatch = app.selectQuery("SELECT m.* FROM match m "
                                                        + "JOIN team_has_season ths "
                                                        + "ON (m.team_home = ths.team_season "
                                                            + "OR m.team_away = ths.team_season) "
                                                        + "WHERE team_season = " + teamSeasonId + ";");
                    int matchCount = 1;
                    while (rsMatch.next()) {
                        cell = new PdfPCell(new Phrase(" "));
                        cell.setColspan(4);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);
                        cell = new PdfPCell(new Phrase("Match #" + matchCount));
                        cell.setColspan(4);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setFixedHeight(20);
                        table.addCell(cell);
                        matchCount++;
                        Match match = new Match();
                        match.setMatchId(rsMatch.getInt("match_id"));
                        match.setTeamHomeId(rsMatch.getInt("team_home"));
                        match.setScoreHome(rsMatch.getInt("score_home"));
                        match.setTeamAwayId(rsMatch.getInt("team_away"));
                        match.setScoreAway(rsMatch.getInt("score_away"));
                        ResultSet rsHome = app.selectQuery("SELECT name FROM team "
                                                         + "JOIN team_has_season USING (team_id) "
                                                         + "WHERE team_season = " + match.getTeamHomeId() + ";");
                        if(rsHome.next()) {
                            // Team home cell
                            cell = new PdfPCell(new Phrase(rsHome.getString("name")));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            cell.setFixedHeight(25);
                            table.addCell(cell);
                        }
                        // Home score cell
                        cell = new PdfPCell(new Phrase(String.valueOf(match.getScoreHome())));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setFixedHeight(25);
                        table.addCell(cell);
                        ResultSet rsAway = app.selectQuery("SELECT name FROM team "
                                                         + "JOIN team_has_season USING (team_id) "
                                                         + "WHERE team_season = " + match.getTeamAwayId() + ";");
                        if (rsAway.next()) {
                            // Team away cell
                            cell = new PdfPCell(new Phrase(rsAway.getString("name")));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            cell.setFixedHeight(25);
                            table.addCell(cell);
                        }
                        // Away score cell
                        cell = new PdfPCell(new Phrase(String.valueOf(match.getScoreAway())));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setFixedHeight(25);
                        table.addCell(cell);
                        List<Player> playersHome = new ArrayList<>();
                        List<Player> playersAway = new ArrayList<>();
                        // Players
                        ResultSet players = app.selectQuery("SELECT p.first_name, p.last_name, p.mid_name, mhp.score, mhp.team_season "
                                                          + "FROM match_has_player mhp "
                                                          + "JOIN player p USING (player_id) "
                                                          + "JOIN team_has_player thp ON "
                                                          + "thp.player_id = p.player_id AND thp.team_id = mhp.team_season "
                                                          + "WHERE match_id = " + match.getMatchId() + ";");
                        while (players.next()) {
                            Player newPlayer = new Player();
                            newPlayer.setFirstName(players.getString("first_name"));
                            newPlayer.setLastName(players.getString("last_name"));
                            newPlayer.setMidName(players.getString("mid_name"));
                            newPlayer.setScore(players.getInt("score"));
                            int teamSeason = players.getInt("team_season");
                            if (teamSeason == match.getTeamHomeId()) {
                                playersHome.add(newPlayer);
                            } else {
                                playersAway.add(newPlayer);
                            }
                        }
                        for (int i = 0; i < 4; i++) {
                            Player homePlayer = i < playersHome.size() ? playersHome.get(i) : null;
                            Player awayPlayer = i < playersAway.size() ? playersAway.get(i) : null;
                            // Home player cell
                            cell = new PdfPCell(new Phrase(homePlayer.toString()));
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            cell.setFixedHeight(25);
                            table.addCell(cell);
                            // Home player score cell
                            cell = new PdfPCell(new Phrase(String.valueOf(homePlayer.getScore())));
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setFixedHeight(25);
                            table.addCell(cell);
                            // Away player cell
                            cell = new PdfPCell(new Phrase(awayPlayer.toString()));
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            cell.setFixedHeight(25);
                            table.addCell(cell);
                            // Away player score cell
                            cell = new PdfPCell(new Phrase(String.valueOf(awayPlayer.getScore())));
                            cell.setVerticalAlignment(Element.ALIGN_CENTER);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setFixedHeight(25);
                            table.addCell(cell);
                        }
                    }
                    table.setSpacingAfter(10);
                    table.setSpacingBefore(10);
                    table.setWidthPercentage(90);
                    table.setWidths(colWidth);
                    doc.add(table);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching seasons: " + ex,
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
            doc.close();
            JLabel label = new JLabel("<html>Report generated successfully!<br>"
                                    + "Check it clicking <a href = ''>here</a>.</html>",
                                    JLabel.CENTER);
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().open(pdfDir);
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
            });
            JOptionPane.showMessageDialog(this, label);
        } catch (FileNotFoundException fileEx) {
            JOptionPane.showMessageDialog(this, "Error creating file: " + fileEx,
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
