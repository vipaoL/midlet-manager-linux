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
        
        int i = 0;
        String iconPath = MIDletManager.dir + "apps/" + midletName + ".png";
        Icon icon = null;
        try {
            icon = new ImageIcon(ImageIO.read(new File(iconPath)).getScaledInstance(192, 192, 0));
        } catch (IOException ex) { }
        JLabel label = new JLabel(midletName, icon, JLabel.CENTER);
        label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, label.getFont().getSize() * 2));
        GridBagConstraints gbc = new GridBagConstraints(
                0, i, //cell for top left corner
                1, 1, //cells to span
                1, 1, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(10, 10, 10, 10), //insets for the cell
                0, 0);                          //additional padding
        add(label, gbc);
        i++;
        
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(200, 0));
        gbc = new GridBagConstraints(
                0, i, //cell for top left corner
                1, 1, //cells to span
                1, 1, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(0, 0, 0, 0), //insets for the cell
                0, 0);                          //additional padding
        //add(separator, gbc);
        //i++;
        
        JButton openBtn = new JButton("Open");
        openBtn.setFont(new Font(openBtn.getFont().getFontName(), Font.PLAIN, openBtn.getFont().getSize() * 2));
        openBtn.setPreferredSize(new Dimension(200, 60));
        openBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new ProcessBuilder(MIDletManager.dir + "wrapper-files/emu.sh", midletName).start();
                } catch (IOException ex) {
                    Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        gbc = new GridBagConstraints(
                0, i, //cell for top left corner
                1, 1, //cells to span
                1, 0, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(0, 0, 0, 0), //insets for the cell
                0, 0);                          //additional padding
        add(openBtn, gbc);
        i++;
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font(deleteBtn.getFont().getFontName(), Font.PLAIN, deleteBtn.getFont().getSize() * 2));
        deleteBtn.setPreferredSize(new Dimension(200, 60));
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!MIDletManager.showDialog("", "Are you sure to delete \"" + midletName + "\"?")) {
                    return;
                }
                try {
                    new ProcessBuilder(MIDletManager.dir + "wrapper-files/uninstall-j2me-app.sh", midletName).start();
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
        gbc = new GridBagConstraints(
                0, i, //cell for top left corner
                1, 1, //cells to span
                1, 0, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(0, 0, 0, 0), //insets for the cell
                0, 0);                          //additional padding
        add(deleteBtn, gbc);
        i++;
        
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font(backBtn.getFont().getFontName(), Font.PLAIN, backBtn.getFont().getSize() * 2));
        backBtn.setPreferredSize(new Dimension(200, 60));
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MIDletManager.setActivity(new MIDletList());
            }
        });
        gbc = new GridBagConstraints(
                0, i, //cell for top left corner
                1, 1, //cells to span
                1, 0, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(0, 0, 0, 0), //insets for the cell
                0, 0);                          //additional padding
        add(backBtn, gbc);
        i++;
    }

}
