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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import static j2me.wrapper.MIDletManager.EMU_ROOT;
import javax.swing.JLabel;

/**
 *
 * @author vipaol
 */
public class MIDletList extends JPanel {

    public MIDletList() {
        super(new GridBagLayout());

        int cellsOnTop = 1;

        /*JLabel title = new JLabel("Installed MIDlets");
        title.setFont(new Font(title.getFont().getFontName(), Font.PLAIN, title.getFont().getSize() * 2));
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, //cell for top left corner
                1, 1, //cells to span
                1, 0, //spacing wieght
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(0, 0, 0, 0), //insets for the cell
                0, 0);                          //additional padding
        add(title, gbc);*/

        // fill extra space at the top to push menu items down 
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

        // get all installed JARs
        File[] fileList = (new File(EMU_ROOT + "apps")).listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });

        // add a button for each app
        for (int i = 0; i < fileList.length; i++) {
            String name = fileList[i].getName();
            if (name.endsWith(".jar")) {
                name = name.substring(0, name.length() - 4);
            }
            final String midletName = name;

            JButton appBtn = new JButton(midletName);
            appBtn.setPreferredSize(new Dimension(400, MIDletManager.BTN_H));
            // bigger font
            appBtn.setFont(new Font(appBtn.getFont().getFontName(), Font.PLAIN,
                    appBtn.getPreferredSize().height / 4));

            // try to load and set app icon
            String iconPath = EMU_ROOT + "apps/" + midletName + ".png";
            try {
                int iconScale = MIDletManager.BTN_H / 96;
                ImageIcon icon = new ImageIcon(ImageIO.read(new File(iconPath))
                        .getScaledInstance(96 * iconScale, 96 * iconScale, 0));
                appBtn.setIcon(icon);
            } catch (IOException ex) {
                System.err.println("icon not found:" + iconPath);
            }
            // add action to open app settings on click
            appBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MIDletManager.setActivity(new MIDletSettings(midletName), midletName + " - settings");
                }
            });
            // set where to place the button
            gbc = new GridBagConstraints(
                    0, i + cellsOnTop, //cell for top left corner
                    1, 1, //cells to span
                    1, 0, //spacing wieght
                    GridBagConstraints.WEST, //where to anchor the component in the cell
                    GridBagConstraints.HORIZONTAL, //how to fill extra space
                    new Insets(0, 0, 0, 0), //insets for the cell
                    0, 0);                          //additional padding
            add(appBtn, gbc);
        }

        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(200, 60));
        exitBtn.setFont(new Font(exitBtn.getFont().getFontName(), Font.PLAIN, exitBtn.getPreferredSize().height / 2));
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
    }
}
