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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author diegoacosta127
 */
public class JFrame_ExportSeason extends JFrame {
    private DatabaseQuery app;
    private JComboBox<Season> comboSeason;
    private JButton btnExport;
    private JButton btnCancel;
    
    public JFrame_ExportSeason(DatabaseQuery dbApp) {
        this.app = dbApp;
        initComponents();
        loadSeasons(comboSeason, app);
    }
    
    private void initComponents(){
        setTitle("Export season");
        setSize(400, 250);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Seasons
        JLabel lblSeason = new JLabel("Choose season");
        lblSeason.setBounds(150, 20, 100, 30);
        add(lblSeason);
        comboSeason = new JComboBox();
        comboSeason.setBounds(70, 70, 280, 30);
        comboSeason.setEditable(true);
        AutoCompletion.enable(comboSeason);
        comboSeason.addItem(null);
        add(comboSeason);
        
        // Export
        btnExport = new JButton("Export");
        btnExport.setBounds(70, 150, 100, 30);
        btnExport.addActionListener((ActionEvent e) -> {
            try {
                exportSeason();
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
    
    private void loadSeasons(JComboBox<Season> comboSeason, DatabaseQuery app) {
        try (ResultSet rs = app.selectQuery("SELECT * FROM season WHERE is_over = true;")) {
            while (rs.next()) {
                Season season = new Season();
                season.setSeasonId(rs.getInt("season_id"));
                season.setCountryId(rs.getInt("country_id"));
                season.setDescription(rs.getString("description"));
                season.setYear(rs.getInt("year"));
                comboSeason.addItem(season);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading seasons: " + ex);
        }
    }
    
    private void exportSeason() throws DocumentException {
        try {
            File pdfDir = new File("pdf/");
            if (!pdfDir.exists()) pdfDir.mkdirs();
            Document doc = new Document(PageSize.A4, 20, 20, 20, 20);
            Season season = new Season();
            Object selectedSeason = comboSeason.getSelectedItem();
            if (selectedSeason instanceof Season) {
                season = (Season) selectedSeason;
            }
            PdfWriter.getInstance(doc, new FileOutputStream("pdf/" + season.toString() + ".pdf"));
            doc.open();
            Font font = new Font(Font.FontFamily.HELVETICA, 30f, Font.BOLD);
            Paragraph paragraph = new Paragraph(season.toString(), font);
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(paragraph);
            int seasonId = season.getSeasonId();
            try (ResultSet rs = app.selectQuery("SELECT m.* "
                                              + "FROM match m JOIN team_has_season ths ON m.team_home = ths.team_season "
                                              + "JOIN season USING (season_id) "
                                              + "WHERE season_id = " + seasonId
                                              + " ORDER BY match_id;")) {
                int matchCount = 1;
                float[] colWidth = new float[] {100f, 15f, 100f, 15f};
                float widthVal = 25;
                while (rs.next()) {
                    PdfPTable table = new PdfPTable(4);
                    PdfPCell cell;
                    // Match cell
                    cell = new PdfPCell(new Phrase("Match #" + matchCount));
                    matchCount++;
                    cell.setColspan(4);
                    cell.setFixedHeight(20);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    Match match = new Match();
                    match.setMatchId(rs.getInt("match_id"));
                    match.setTeamHomeId(rs.getInt("team_home"));
                    match.setScoreHome(rs.getInt("score_home"));
                    match.setTeamAwayId(rs.getInt("team_away"));
                    match.setScoreAway(rs.getInt("score_away"));
                    ResultSet rsHome = app.selectQuery("SELECT name FROM team "
                                                     + "JOIN team_has_season USING (team_id) "
                                                     + "WHERE team_season = " + match.getTeamHomeId() + ";");
                    if (rsHome.next()) {
                        // Team home cell
                        cell = new PdfPCell(new Phrase(rsHome.getString("name")));
                        cell.setFixedHeight(widthVal);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                    // Home score cell
                    cell = new PdfPCell(new Phrase(String.valueOf(match.getScoreHome())));
                    cell.setFixedHeight(widthVal);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    ResultSet rsAway = app.selectQuery("SELECT name FROM team "
                                                     + "JOIN team_has_season USING (team_id) "
                                                     + "WHERE team_season = " + match.getTeamAwayId() + ";");
                    if (rsAway.next()) {
                        // Team away cell
                        cell = new PdfPCell(new Phrase(rsAway.getString("name")));
                        cell.setFixedHeight(widthVal);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                    // Away score cell
                    cell = new PdfPCell(new Phrase(String.valueOf(match.getScoreAway())));
                    cell.setFixedHeight(widthVal);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    List<Player> playersHome = new ArrayList<>();
                    List<Player> playersAway = new ArrayList<>();
                    ResultSet players = app.selectQuery("SELECT p.first_name, p.last_name, p.mid_name, mhp.score, mhp.team_season "
                                                      + "FROM match_has_player mhp "
                                                      + "JOIN player p USING (player_id) "
                                                      + "JOIN team_has_player thp ON "
                                                      + "thp.player_id = p.player_id AND thp.team_id = mhp.team_season "
                                                      + "WHERE match_id = " + match.getMatchId() + ";");
                    while (players.next()) {
                        Player player = new Player();
                        player.setFirstName(players.getString("first_name"));
                        player.setLastName(players.getString("last_name"));
                        player.setMidName(players.getString("mid_name"));
                        player.setScore(players.getInt("score"));
                        int teamSeason = players.getInt("team_season");
                        if (teamSeason == match.getTeamHomeId()) {
                            playersHome.add(player);
                        } else {
                            playersAway.add(player);
                        }
                    }
                    for (int i = 0; i < 4; i++) {
                        Player homePlayer = i < playersHome.size() ? playersHome.get(i) : null;
                        Player awayPlayer = i < playersAway.size() ? playersAway.get(i) : null;
                        // Home player cell
                        cell = new PdfPCell(new Phrase(homePlayer.toString()));
                        cell.setFixedHeight(widthVal);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                        // Home player score cell
                        cell = new PdfPCell(new Phrase(String.valueOf(homePlayer.getScore())));
                        cell.setFixedHeight(widthVal);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                        // Away player cell
                        cell = new PdfPCell(new Phrase(awayPlayer.toString()));
                        cell.setFixedHeight(widthVal);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                        // Away player score cell
                        cell = new PdfPCell(new Phrase(String.valueOf(awayPlayer.getScore())));
                        cell.setFixedHeight(widthVal);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                    table.setSpacingAfter(10);
                    table.setSpacingBefore(10);
                    table.setWidthPercentage(90);
                    table.setWidths(colWidth);
                    doc.add(table);
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
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting season: " + ex,
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (FileNotFoundException fileEx) {
            System.out.println(fileEx);
        }
        
    }
}