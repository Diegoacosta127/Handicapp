/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handicapp;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author diegoacosta127
 */
public class ComeBackSoon extends JFrame {
    public static void comeBackSoon () {
        JLabel label = new JLabel("<html>Sorry, I'm still working on this feature.<br>"
                                + "Be sure to check <a href=''>the project repository</a> for updates.</html>",
                                JLabel.CENTER);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Diegoacosta127/Handicapp"));
                } catch (IOException | URISyntaxException ex) {
                    System.out.println(ex);
                }
            }
        });
        JOptionPane.showMessageDialog(null, label);
    }
}
