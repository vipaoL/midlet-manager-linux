/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author vipaol
 */
public class MIDletSettings extends JPanel {

    MIDletSettings(String appName) {
        super(new GridBagLayout());
        
        int elementCount = 0;
        
        // icon and name
        JLabel label = new JLabel(appName, JLabel.CENTER);
        label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, label.getFont().getSize() * 2));
        
        try {
            ImageIcon appIcon = new ImageIcon(ImageIO.read(MIDletManager.getIcon(appName)).getScaledInstance(192, 192, 0));
            label.setIcon(appIcon);
        } catch (IOException ex) {
            System.err.println("Can't load icon for " + appName);
        }
        add(label, ActivityCanvas.getGBC(elementCount, 10, 1));
        elementCount++;
        
        // Button to open the app
        JButton openBtn = new JButton("Open");
        openBtn.setPreferredSize(new Dimension(200, 60));
        openBtn.setFont(new Font(openBtn.getFont().getFontName(), Font.PLAIN, openBtn.getPreferredSize().height / 2));
        openBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new ProcessBuilder(J2meWrapper.EMU_ROOT + "wrapper-files/emu.sh", appName).start();
                } catch (IOException ex) {
                    Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(openBtn, ActivityCanvas.getGBC(elementCount, 0, 0));
        elementCount++;
        
        // Button to delete the app
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setPreferredSize(new Dimension(200, 60));
        deleteBtn.setFont(new Font(deleteBtn.getFont().getFontName(), Font.PLAIN, deleteBtn.getPreferredSize().height / 2));
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!ActivityCanvas.showConfirmDialog("", "Are you sure to delete \"" + appName + "\"?")) {
                    return;
                }
                try {
                    new ProcessBuilder(J2meWrapper.EMU_ROOT + "wrapper-files/uninstall-j2me-app.sh", appName).start();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ActivityCanvas.setActivity(new MIDletList(), "Installed MIDlets", true);
                } catch (IOException ex) {
                    Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(deleteBtn, ActivityCanvas.getGBC(elementCount, 0, 0));
        elementCount++;
        
        // Button to main menu
        JButton backBtn = new JButton("Back");
        backBtn.setPreferredSize(new Dimension(200, 60));
        backBtn.setFont(new Font(backBtn.getFont().getFontName(), Font.PLAIN, backBtn.getPreferredSize().height / 2));
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ActivityCanvas.setActivity(new MIDletList(), "Installed MIDlets", true);
            }
        });
        add(backBtn, ActivityCanvas.getGBC(elementCount, 0, 0));
        elementCount++;
    }
}
