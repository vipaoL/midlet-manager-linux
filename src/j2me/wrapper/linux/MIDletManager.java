/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper.linux;

import java.awt.BorderLayout;
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

    static JScrollPane prevActivity = null;
    static MIDletManager inst = null;
    static JScrollPane listScroller;
    //static String dir = "/home/vipaol/freej2me/"; // for testing from other directories
    static String dir = "../";
    static int BTN_H = 180;

    public MIDletManager(String app) {
        inst = this;
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setLayout(new BorderLayout());
        setActivity(new MIDletList());
        pack();
        setVisible(true);
    }

    public static void setActivity(JPanel panel) {
        prevActivity = listScroller;
        if (listScroller != null) {
            inst.remove(listScroller);
        }
        listScroller = new JScrollPane(panel);
        JScrollBar scrollBar = listScroller.getVerticalScrollBar();
        int w = scrollBar.getPreferredSize().width * 3;
        int h = scrollBar.getPreferredSize().height;
        scrollBar.setPreferredSize(new Dimension(w, h));
        inst.add(listScroller);
        inst.pack();
    }

    public static void backToPrevActivity() {
        JScrollPane tmp = listScroller;
        if (listScroller != null) {
            inst.remove(listScroller);
        }
        inst.add(prevActivity);
        prevActivity = tmp;
        inst.pack();
    }
    
    public static boolean showDialog(String title, String question) {
        JFrame jFrame = new JFrame();
        return JOptionPane.showConfirmDialog(jFrame, question, title, JOptionPane.OK_CANCEL_OPTION) == 0;
    }
}
