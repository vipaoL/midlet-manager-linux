/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper.linux;

import java.awt.Dimension;
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

    static MIDletManager inst = null;
    static JScrollPane listScroller;
    //static String WORKDIR = "/home/vipaol/freej2me/"; // for testing from other directories
    static String WORKDIR = "../";
    static int BTN_H = 180;
    static int SCALE = 3;

    public MIDletManager(String app) {
        inst = this;
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        if (app == null) {
            setActivity(new MIDletList());
        } else {
            setActivity(new MIDletSettings(app));
        }
        setVisible(true);
    }

    public static void setActivity(JPanel panel) {
        if (listScroller != null) {
            inst.remove(listScroller);
        }
        listScroller = new JScrollPane(panel);
        JScrollBar scrollBar = listScroller.getVerticalScrollBar();
        int w = scrollBar.getPreferredSize().width * SCALE;
        int h = scrollBar.getPreferredSize().height;
        scrollBar.setPreferredSize(new Dimension(w, h));
        inst.add(listScroller);
        inst.pack();
    }
    
    public static boolean showConfirmDialog(String title, String question) {
        return JOptionPane.showConfirmDialog(inst, question, title, JOptionPane.OK_CANCEL_OPTION) == 0;
    }
}
