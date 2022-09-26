/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper.linux;

import static j2me.wrapper.linux.MIDletManager.dir;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 *
 * @author vipaol
 */
public class MIDletList extends JPanel {

    public MIDletList() {
        super(new GridBagLayout());
        
        int cellsOnTop = 1;
        
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(200, 0));
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, //cell for top left corner
                1, 1, //cells to span
                1, 1, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(0, 0, 0, 0), //insets for the cell
                0, 0);                          //additional padding
        add(separator, gbc);

        File f = new File(dir + "apps");
        File[] fileList = f.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        String[] fileNames = new String[fileList.length];
        for (int i = 0; i < fileList.length; i++) {
            fileNames[i] = fileList[i].getName();
        }
        for (int i = 0; i < fileList.length; i++) {
            String name = fileNames[i];
            if (name.endsWith(".jar")) {
                name = name.substring(0, name.length() - 4);
            }
            final String midletName = name;

            String iconPath = dir + "apps/" + midletName + ".png";
            JButton btn = null;
            try {
                btn = new JButton(new ImageIcon(ImageIO.read(new File(iconPath)).getScaledInstance(96 * (MIDletManager.BTN_H / 96), 96 * (MIDletManager.BTN_H / 96), 0)));
                btn.setPreferredSize(new Dimension(200, MIDletManager.BTN_H));
                btn.setFont(new Font(btn.getFont().getFontName(), Font.PLAIN, btn.getFont().getSize() * 3));
            } catch (IOException ex) {
                System.err.println("icon not found:" + iconPath);
                btn = new JButton();
                btn.setPreferredSize(new Dimension(200, MIDletManager.BTN_H));
            }
            btn.setText(midletName);
            btn.setHorizontalAlignment(JButton.LEADING);
            btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MIDletManager.setActivity(new MIDletSettings(midletName));
                }
            });
            gbc = new GridBagConstraints(
                    0, i + cellsOnTop, //cell for top left corner
                    1, 1, //cells to span
                    1, 0, //spacing wieght
                    GridBagConstraints.WEST, //where to anchor the component in the cell
                    GridBagConstraints.HORIZONTAL, //how to fill extra space
                    new Insets(0, 0, 0, 0), //insets for the cell
                    0, 0);                          //additional padding
            add(btn, gbc);
        }
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(200, 60));
        exitBtn.setFont(new Font(exitBtn.getFont().getFontName(), Font.PLAIN, exitBtn.getFont().getSize() * 2));
        gbc = new GridBagConstraints(
                0, fileList.length + cellsOnTop, //cell for top left corner
                1, 1, //cells to span
                1, 0, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(0, 0, 0, 0), //insets for the cell
                0, 0);                          //additional padding
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(exitBtn, gbc);
        JList list = new JList(fileNames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.setFixedCellHeight(50);
    }
}
