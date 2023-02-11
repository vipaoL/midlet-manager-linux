/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper;

import static j2me.wrapper.util.FileUtils.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 *
 * @author vipaol
 */
public class MIDletManager extends JFrame {

    public static MIDletManager inst = null;
    static JScrollPane listScroller;
    //static String EMU_ROOT = "/home/vipaol/freej2me/"; // for testing from other directories
    static String EMU_ROOT = "../";
    static String APPS_DIR = EMU_ROOT + "apps/";
    static int BTN_H = 120;
    //static int SCALE = 2;

    public MIDletManager(String app) {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        inst = this;
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        if (app.equals("")) {
            setActivity(new MIDletList(), "Installed MIDlets");
        } else {
            setActivity(new MIDletSettings(app), app + " - settings");
        }
    }

    public MIDletManager() {
        setLayout(new GridLayout(1, 1));
        inst = this;
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void setActivity(JPanel panel, String title) {
        inst.setTitle(title);
        if (listScroller != null) {
            inst.remove(listScroller);
        }
        listScroller = new JScrollPane(panel);
        JScrollBar scrollBar = listScroller.getVerticalScrollBar();
        int w = scrollBar.getPreferredSize().width * 3;
        int h = scrollBar.getPreferredSize().height;
        scrollBar.setPreferredSize(new Dimension(w, h));
        listScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScroller.setHorizontalScrollBar(null);
        inst.add(listScroller);
        inst.pack();
        inst.setVisible(true);
    }

    public static boolean showConfirmDialog(String title, String question) {
        return JOptionPane.showConfirmDialog(inst, question, title, JOptionPane.OK_CANCEL_OPTION) == 0;
    }

    public static void showInfo(String message) {
        System.out.println(message);
        if (!J2meWrapper.headlessMode) {
            JOptionPane.showMessageDialog(inst, message);
        }
    }

    public static void showError(String message) {
        System.err.println(message);
        if (!J2meWrapper.headlessMode) {
            JOptionPane.showMessageDialog(inst, message, "error", JOptionPane.ERROR_MESSAGE);
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
                {"replace_with_install_dir", Paths.get(EMU_ROOT).toAbsolutePath().normalize().toString()},
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
}
