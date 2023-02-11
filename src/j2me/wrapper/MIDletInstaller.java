/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j2me.wrapper;

import j2me.wrapper.util.FileUtils;
import static j2me.wrapper.util.FileUtils.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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
    Path pathToJar = null;
    String midletName = "noname";
    String midletVendor = "nobody";
    String midletVersion = "";
    String iconPath = null;
    ArrayList<MIDlet> midlets = new ArrayList<>();
    String midletDescription = "";
    boolean isAlreadyInstalled = false;
    File tmpFile = createTempFile("midlet-installer-buffer");
    public static boolean doNotAskInstall = false;
    Path targetPath = null;

    public MIDletInstaller(String path) throws IOException {
        pathToJar = Paths.get(path);
        System.out.println(J2meWrapper.OS_NAME);
        Attributes manifestValues = FileUtils.getJarManifestValues(pathToJar);
        midletName = manifestValues.getValue("MIDlet-Name");
        midletVendor = manifestValues.getValue("MIDlet-Vendor");
        midletVersion = manifestValues.getValue("MIDlet-Version");
        iconPath = manifestValues.getValue("MIDlet-Icon");
        midletDescription = manifestValues.getValue("MIDlet-Description");

        for (int i = 1; true; i++) {
            String midletProps = manifestValues.getValue("MIDlet-" + i);
            if (midletProps == null) {
                break;
            }
            midlets.add(new MIDlet(midletProps));
        }
        
        if (midlets.isEmpty()) {
            ActivityCanvas.showError("Could not find \"MIDlet-1\" entry in the manifest. Are you sure it is a J2ME app?");
            System.exit(0);
        }

        if (iconPath == null) {
            iconPath = midlets.get(0).iconPath;
        }
        try {
            extractFile(pathToJar, iconPath, tmpFile.toPath());
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (doNotAskInstall) {
            System.out.println("Set \"-y\", installing without dialog");
            install();
            createMIDletShortcut(midletName);
            System.out.println("Done!");
            System.exit(0);
        }
        
        targetPath = Paths.get(J2meWrapper.APPS_DIR).resolve(midletName + ".jar");
        
        // check if the midlet is already installed
        if (targetPath.toFile().exists()) {
            isAlreadyInstalled = true;
            System.out.println("exists");
        } else {
            System.out.println(targetPath);
        }
        
        String title = isAlreadyInstalled ? "Update " + midletName : "Install " + midletName;
        
        ActivityCanvas.setActivity(new InstallerScreen(), title, false);
    }

    boolean install() {
        assert (!midletName.equals(""));

        try {
            String iconPath = midlets.get(0).iconPath;
            if (iconPath == null) {
                iconPath = this.iconPath;
            }
            try {
                if (iconPath != null) {
                    extractFile(pathToJar, iconPath, Paths.get(J2meWrapper.APPS_DIR).resolve(midletName + ".png"));
                }
            } catch (IOException ex) {
                ActivityCanvas.showError("Failed to extract icon " + iconPath);
            }
            Files.copy(pathToJar, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
            ActivityCanvas.showError("Failed to copy jar");
            return false;
        }
    }
    
    public static boolean createMIDletShortcut(String midletName) {
        try {
            Properties config = new Properties();
            config.load(new FileInputStream(Paths.get("./config/config.txt").toFile()));
            String resPath = "/shortcut-templates/midlet-shortcut-template-" + J2meWrapper.OS_NAME.toLowerCase() + ".desktop";
            System.out.print("Your OS is " + J2meWrapper.OS_NAME + " => ");
            System.out.println("trying to read in resources " + resPath);
            File tmpFile = createTempFile("midlet-shortcut");
            
            j2me.wrapper.util.FileUtils.exportResource(resPath, tmpFile.getPath().toString());
            InputStream is = new FileInputStream(new File(tmpFile.toURI()));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String firstLine = br.readLine();
            String systemShortcutsPath = config.getProperty("SYSTEM_SHORTCUTS_DIR",
                    firstLine.split(":::")[1].trim().replace("replace_with_user_home", System.getProperty("user.home")))
                    .replace("$HOME", System.getProperty("user.home"));
            Path midletShortcutPath = Paths.get(systemShortcutsPath).resolve(midletName + "-j2mew.desktop").normalize();
            assert (!midletName.equals(""));
            assert (Paths.get(systemShortcutsPath).normalize() != midletShortcutPath);

            String[][] mask = {
                {"replace_with_app_name", midletName},
                {"replace_with_install_dir", Paths.get(J2meWrapper.EMU_ROOT).toAbsolutePath().normalize().toString()},
                {firstLine + "\n", ""}
            };
            replaceInFile(tmpFile.toPath(), mask);
            System.out.println("Copying " + tmpFile.toPath() + " to " + midletShortcutPath);
            Files.copy(tmpFile.toPath(), midletShortcutPath, StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (URISyntaxException ex) {
            Logger.getLogger(MIDletManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MIDletManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
                ImageIcon appIcon = FileUtils.getScaledIcon(tmpFile, 96);
                label.setIcon(appIcon);
            } catch (IOException | NullPointerException ex) {
                System.err.println("Can't load app icon");
            }
            topPanel.add(ActivityCanvas.leftJustify(label));//, getGBC(btnCount, 10, 1));
            //btnCount++;
            
            JLabel question = new JLabel();
            if (!isAlreadyInstalled) {
                question.setText("Install \"" + midletName + "\" " + midletVersion + "?");
            } else {
                question.setText("Update \"" + midletName + "\" to " + midletVersion + "?");
            }
            question.setFont(new Font(question.getFont().getFontName(), Font.PLAIN, question.getFont().getSize()));
            question.setAlignmentX(Component.LEFT_ALIGNMENT);
            topPanel.add(ActivityCanvas.leftJustify(question));//, getGBC(btnCount, 10, 1));
            //btnCount++;
            
            if (isAlreadyInstalled) {
                String installedVersionNumber = "?.?.?";
                try {
                    installedVersionNumber = FileUtils.getJarManifestValues(targetPath).getValue("MIDlet-Version");
                } catch (IOException ex) {
                    Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    Logger.getLogger(MIDletInstaller.class.getName()).log(Level.SEVERE, null, ex);
                }
                JLabel installedVersion = new JLabel("Version " + installedVersionNumber + " is already installed");
                installedVersion.setFont(new Font(installedVersion.getFont().getFontName(), Font.PLAIN, installedVersion.getFont().getSize()));
                installedVersion.setAlignmentX(Component.LEFT_ALIGNMENT);
                topPanel.add(ActivityCanvas.leftJustify(installedVersion));//, getGBC(btnCount, 10, 1));
                //btnCount++;
            }
            
            JLabel vendorName = new JLabel("Vendor: " + midletVendor);
            vendorName.setFont(new Font(vendorName.getFont().getFontName(), Font.PLAIN, vendorName.getFont().getSize()));
            vendorName.setAlignmentX(Component.LEFT_ALIGNMENT);
            topPanel.add(ActivityCanvas.leftJustify(vendorName));//, getGBC(btnCount, 10, 1));
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
            
            add(topPanel, ActivityCanvas.getGBC(componentsCount, 10, 0));
            componentsCount++;
            
            add(new JPanel(), ActivityCanvas.getGBC(componentsCount, 10, 1));
            componentsCount++;
            
            JPanel btnPanel = new JPanel(new GridLayout(1, 2));
            
            // Button to install the app
            JButton installBtn = new JButton("Install");
            installBtn.setPreferredSize(new Dimension(200, 60));
            installBtn.setFont(new Font(installBtn.getFont().getFontName(), Font.PLAIN, installBtn.getPreferredSize().height / 2));
            installBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            installBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (install()) {
                        createMIDletShortcut(midletName);
                        String successTitle = isAlreadyInstalled ? "Updated successfully" : "Installed successfully";
                        ActivityCanvas.setActivity(new SuccessScreen(midletName), successTitle, false);
                    }
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
            
            add(btnPanel, ActivityCanvas.getGBC(componentsCount, 0, 0));
            componentsCount++;
        }
    }
    
    public class SuccessScreen extends JPanel {
        public SuccessScreen(String appName) {
            setLayout(new GridBagLayout());
            
            int componentsCount = 0;
            
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            
            // icon and name
            JLabel label = new JLabel(appName, JLabel.CENTER);
            label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, label.getFont().getSize() * 2));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            try {
                ImageIcon appIcon = new ImageIcon(ImageIO.read(
                        MIDletManager.getIcon(appName)).getScaledInstance(96, 96, 0));
                label.setIcon(appIcon);
            } catch (IOException | NullPointerException ex) {
                System.err.println("Can't load app icon");
            }
            topPanel.add(ActivityCanvas.leftJustify(label));
            
            JLabel successMessage = new JLabel();
            if (!isAlreadyInstalled) {
                successMessage.setText("Installed \"" + appName + "\" " + midletVersion + " successfully");
            } else {
                successMessage.setText("Updated \"" + appName + "\" to v" + midletVersion);
            }
            successMessage.setFont(new Font(successMessage.getFont().getFontName(), Font.PLAIN, successMessage.getFont().getSize()));
            successMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
            topPanel.add(ActivityCanvas.leftJustify(successMessage));

            add(topPanel, ActivityCanvas.getGBC(componentsCount, 10, 0));
            componentsCount++;
            
            add(new JPanel(), ActivityCanvas.getGBC(componentsCount, 10, 1));
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
                        new ProcessBuilder(J2meWrapper.EMU_ROOT + "wrapper-files/emu.sh", appName).start();
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
            
            add(btnPanel, ActivityCanvas.getGBC(componentsCount, 0, 0));
        }
    }
}
