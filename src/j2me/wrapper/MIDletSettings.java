/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper.linux;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author vipaol
 */
public class MIDletSettings extends JPanel {

    MIDletSettings(String midletName) {
        super(new GridBagLayout());
        
        int btnCount = 0;
        
        // icon and name
        JLabel label = new JLabel(midletName, JLabel.CENTER);
        label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, label.getFont().getSize() * 2));
        
        String iconPath = MIDletManager.WORKDIR + "apps/" + midletName + ".png";
        try {
            ImageIcon appIcon = new ImageIcon(ImageIO.read(
                    new File(iconPath)).getScaledInstance(192, 192, 0));
            label.setIcon(appIcon);
        } catch (IOException ex) {
            System.err.println("Can't load app icon: " + iconPath);
        }
        add(label, getGBC(btnCount, 10, 1));
        btnCount++;
        
        // Button to open the app
        JButton openBtn = new JButton("Open");
        openBtn.setFont(new Font(openBtn.getFont().getFontName(), Font.PLAIN, openBtn.getFont().getSize() * 2));
        openBtn.setPreferredSize(new Dimension(200, 60));
        openBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new ProcessBuilder(MIDletManager.WORKDIR + "wrapper-files/emu.sh", midletName).start();
                } catch (IOException ex) {
                    Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(openBtn, getGBC(btnCount, 0, 0));
        btnCount++;
        
        // Button to delete the app
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font(deleteBtn.getFont().getFontName(), Font.PLAIN, deleteBtn.getFont().getSize() * 2));
        deleteBtn.setPreferredSize(new Dimension(200, 60));
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!MIDletManager.showConfirmDialog("", "Are you sure to delete \"" + midletName + "\"?")) {
                    return;
                }
                try {
                    new ProcessBuilder(MIDletManager.WORKDIR + "wrapper-files/uninstall-j2me-app.sh", midletName).start();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    MIDletManager.setActivity(new MIDletList());
                } catch (IOException ex) {
                    Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(deleteBtn, getGBC(btnCount, 0, 0));
        btnCount++;
        
        // Button to main menu
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font(backBtn.getFont().getFontName(), Font.PLAIN, backBtn.getFont().getSize() * 2));
        backBtn.setPreferredSize(new Dimension(200, 60));
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MIDletManager.setActivity(new MIDletList());
            }
        });
        add(backBtn, getGBC(btnCount, 0, 0));
        btnCount++;
    }

    private GridBagConstraints getGBC(int i, int insets, int spacingWeight) {
        return new GridBagConstraints(
                0, i, //cell for top left corner
                1, 1, //cells to span
                1, spacingWeight, //spacing weight
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(insets, insets, insets, insets), //insets for the cell
                0, 0);                          //additional padding
    }

}
