/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j2me.wrapper;

import static j2me.wrapper.util.FileUtils.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author vipaol
 */
public class MIDletInstaller {

    Path jarPath = null;
    String midletName = "noname";
    String midletVendor = "nobody";
    String midletVersion = "";
    String iconPath = null;
    ArrayList<MIDlet> midlets = new ArrayList<>();
    String midletDescription = "";
    boolean isAlreadyInstalled = false;
    File tmpFile = createTempFile("midlet-installer-buffer");
    boolean doNotAskInstall = false;

    public MIDletInstaller(String[] args) throws IOException {
        String path;
        if (args.length == 2) {
            path = args[1];
        } else {
            path = args[2];
            if (args[1].equals("-y")) {
                doNotAskInstall = true;
            }
        }
        System.out.println(J2meWrapper.OS_NAME);
        jarPath = Paths.get(path);
        Attributes manifestValues;

        try {
            JarFile jarfile = new JarFile(jarPath.toFile());
            manifestValues = jarfile.getManifest().getMainAttributes();
        } catch (IOException ex) {
            Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("Could not read jar");
        } catch (NullPointerException ex) {
            throw new NullPointerException("Could not read manifest");
        }
        midletName = manifestValues.getValue("MIDlet-Name");
        midletVendor = manifestValues.getValue("MIDlet-Vendor");
        midletVersion = manifestValues.getValue("MIDlet-Version");
        iconPath = manifestValues.getValue("MIDlet-Icon");
        midletDescription = manifestValues.getValue("MIDlet-Description");
        
        // check if the midlet is already installed
        if (new File(Paths.get(MIDletManager.APPS_DIR).resolve(midletName + ".jar").toUri()).exists()) {
            isAlreadyInstalled = true;
        }

        for (int i = 1; true; i++) {
            String midletProps = manifestValues.getValue("MIDlet-" + i);
            if (midletProps == null) {
                break;
            }
            midlets.add(new MIDlet(midletProps));
        }
        
        if (midlets.isEmpty()) {
            MIDletManager.showError("Could not find \"MIDlet-1\" value in the manifest. Are you sure it is a J2ME app?");
            System.exit(0);
        }

        if (iconPath == null) {
            iconPath = midlets.get(0).iconPath;
        }
        try {
            extractFile(jarPath, iconPath, tmpFile.toPath());
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (doNotAskInstall) {
            System.out.println("Set \"-y\", installing without dialog");
            install();
            MIDletManager.createMIDletShortcut(midletName);
            System.out.println("Done!");
            System.exit(0);
        }
        
        if (!isAlreadyInstalled) {
            MIDletManager.inst.setTitle("Install " + midletName);
        } else {
            MIDletManager.inst.setTitle("Update " + midletName);
        }
        
        MIDletManager.inst.setContentPane((JPanel) new InstallerScreen());
        MIDletManager.inst.pack();
        MIDletManager.inst.setVisible(true);
    }

    void install() {
        assert (!midletName.equals(""));

        try {
            String iconPath = midlets.get(0).iconPath;
            if (iconPath == null) {
                iconPath = this.iconPath;
            }
            try {
                if (iconPath != null) {
                    extractFile(jarPath, iconPath, Paths.get(MIDletManager.APPS_DIR).resolve(midletName + ".png"));
                }
            } catch (IOException ex) {
                MIDletManager.showError("Failed to extract icon " + iconPath);
            }
            Files.copy(jarPath, Paths.get(MIDletManager.APPS_DIR).resolve(midletName + ".jar"), StandardCopyOption.REPLACE_EXISTING);
            String successTitle = "Installed successfully";
            if (isAlreadyInstalled) {
                successTitle = "Updated successfully";
            }
            MIDletManager.inst.setTitle(successTitle);
            MIDletManager.inst.setContentPane(new SuccessScreen(midletName, Paths.get(MIDletManager.APPS_DIR).resolve(midletName + ".png").toFile()));
            MIDletManager.inst.pack();
        } catch (IOException ex) {
            Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
            MIDletManager.showError("Failed to copy jar");
        }
    }

    public class InstallerScreen extends JPanel {

        public InstallerScreen() {
            setLayout(new GridBagLayout());
            int componentsCount = 0;
            
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

            // icon and name
            JLabel label = new JLabel(midletName, JLabel.CENTER);
            label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, label.getFont().getSize() * 2));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);

            try {
                ImageIcon appIcon = new ImageIcon(ImageIO.read(
                        tmpFile).getScaledInstance(96, 96, 0));
                label.setIcon(appIcon);
            } catch (IOException | NullPointerException ex) {
                System.err.println("Can't load app icon");
            }
            topPanel.add(leftJustify(label));//, getGBC(btnCount, 10, 1));
            //btnCount++;
            
            JLabel question = new JLabel();
            if (!isAlreadyInstalled) {
                question.setText("Install \"" + midletName + "\" " + midletVersion + "?");
            } else {
                question.setText("Update \"" + midletName + "\" to " + midletVersion + "?");
            }
            question.setFont(new Font(question.getFont().getFontName(), Font.PLAIN, question.getFont().getSize()));
            question.setAlignmentX(Component.LEFT_ALIGNMENT);
            topPanel.add(leftJustify(question));//, getGBC(btnCount, 10, 1));
            //btnCount++;
            
            JLabel installedVersion = new JLabel("Version ?.?.? is already installed");
            installedVersion.setFont(new Font(installedVersion.getFont().getFontName(), Font.PLAIN, installedVersion.getFont().getSize()));
            installedVersion.setAlignmentX(Component.LEFT_ALIGNMENT);
            //topPanel.add(leftJustify(installedVersion));//, getGBC(btnCount, 10, 1));
            //btnCount++;
            
            JLabel vendorName = new JLabel("Vendor: " + midletVendor);
            vendorName.setFont(new Font(vendorName.getFont().getFontName(), Font.PLAIN, vendorName.getFont().getSize()));
            vendorName.setAlignmentX(Component.LEFT_ALIGNMENT);
            topPanel.add(leftJustify(vendorName));//, getGBC(btnCount, 10, 1));
            //btnCount++;
            
            // midlet description
            JTextArea descr = new JTextArea();
            if (midletDescription != null) {
                descr.setText("Description: " + midletDescription);
            } else {
                descr.setText("Description: -");
            }
            descr.setLineWrap(true);
            descr.setWrapStyleWord(true);
            //descr.se
            descr.setFont(new Font(descr.getFont().getFontName(), Font.PLAIN, descr.getFont().getSize() * 1));
            topPanel.add(descr);//, getGBC(btnCount, 10, 1));
            //btnCount++;
            
            add(topPanel, getGBC(componentsCount, 10, 0));
            componentsCount++;
            
            add(new JPanel(), getGBC(componentsCount, 10, 1));
            componentsCount++;
            
            JPanel btnPanel = new JPanel(new GridLayout(1, 2));
            
            // Button to install the app
            JButton installBtn = new JButton("Install");
            installBtn.setPreferredSize(new Dimension(200, 60));
            installBtn.setFont(new Font(installBtn.getFont().getFontName(), Font.PLAIN, installBtn.getPreferredSize().height / 2));
            installBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            installBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    install();
                    MIDletManager.createMIDletShortcut(midletName);
                }
            });
            btnPanel.add(installBtn);
            
            // Cancel button
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setPreferredSize(new Dimension(200, 60));
            cancelBtn.setFont(new Font(cancelBtn.getFont().getFontName(), Font.PLAIN, cancelBtn.getPreferredSize().height / 2));
            cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            btnPanel.add(cancelBtn);
            
            add(btnPanel, getGBC(componentsCount, 0, 0));
            componentsCount++;
        }
    }
    
    private Component leftJustify(Component component) {
        Box b = Box.createHorizontalBox();
        b.add(component);
        b.add(Box.createHorizontalGlue());
        return b;
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

    public class MIDlet {

        public int index;
        public String name = null;
        public String iconPath = null;
        public String mainClass = null;

        public MIDlet(String manifestEntryValue) {
            if (manifestEntryValue.startsWith("MIDlet-")) {
                manifestEntryValue = manifestEntryValue.split(":")[1];
            }
            String[] params = manifestEntryValue.trim().split(",");
            if (params.length > 2) {
                midletName = params[0].trim();
                iconPath = params[1].trim();
                if (iconPath.equals("")) {
                    iconPath = null;
                }
                mainClass = params[2].trim();
            } else {
                midletName = params[0].trim();
                mainClass = params[1].trim();
            }
        }
    }
    
    public class SuccessScreen extends JPanel {
        public SuccessScreen(String midletName, File icon) {
            setLayout(new GridBagLayout());
            
            int componentsCount = 0;
            
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            
            // icon and name
            JLabel label = new JLabel(midletName, JLabel.CENTER);
            label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, label.getFont().getSize() * 2));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            try {
                ImageIcon appIcon = new ImageIcon(ImageIO.read(
                        icon).getScaledInstance(96, 96, 0));
                label.setIcon(appIcon);
            } catch (IOException | NullPointerException ex) {
                System.err.println("Can't load app icon");
            }
            topPanel.add(leftJustify(label));
            
            JLabel successMessage = new JLabel();
            if (!isAlreadyInstalled) {
                successMessage.setText("Installed \"" + midletName + "\" " + midletVersion + " successfully");
            } else {
                successMessage.setText("Updated \"" + midletName + "\" to v" + midletVersion);
            }
            successMessage.setFont(new Font(successMessage.getFont().getFontName(), Font.PLAIN, successMessage.getFont().getSize()));
            successMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
            topPanel.add(leftJustify(successMessage));

            add(topPanel, getGBC(componentsCount, 10, 0));
            componentsCount++;
            
            add(new JPanel(), getGBC(componentsCount, 10, 1));
            componentsCount++;
            
            JPanel btnPanel = new JPanel(new GridLayout(1, 2));
            
            // Button to open the app
            JButton openBtn = new JButton("Open");
            openBtn.setPreferredSize(new Dimension(200, 60));
            openBtn.setFont(new Font(openBtn.getFont().getFontName(), Font.PLAIN, openBtn.getPreferredSize().height / 2));
            openBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            openBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        new ProcessBuilder(MIDletManager.EMU_ROOT + "wrapper-files/emu.sh", midletName).start();
                    } catch (IOException ex) {
                        Logger.getLogger(MIDletSettings.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                    }
                }
            });
            btnPanel.add(openBtn);
            
            // Cancel button
            JButton cancelBtn = new JButton("Done");
            cancelBtn.setPreferredSize(new Dimension(200, 60));
            cancelBtn.setFont(new Font(cancelBtn.getFont().getFontName(), Font.PLAIN, cancelBtn.getPreferredSize().height / 2));
            cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            btnPanel.add(cancelBtn);
            
            add(btnPanel, getGBC(componentsCount, 0, 0));
        }
    }
}
