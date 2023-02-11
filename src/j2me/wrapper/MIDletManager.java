/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper;

import java.io.File;
import java.nio.file.Paths;
import javax.swing.JScrollPane;

/**
 *
 * @author vipaol
 */
public class MIDletManager {
    static JScrollPane listScroller;
    static int BTN_H = 120;
    //static int SCALE = 2;

    public MIDletManager() {
        ActivityCanvas.setActivity(new MIDletList(), "Installed MIDlets", true);
    }
    
    public static File getIcon(String appName) {
        return Paths.get(J2meWrapper.APPS_DIR).resolve(appName + ".png").toFile();
    }
}
