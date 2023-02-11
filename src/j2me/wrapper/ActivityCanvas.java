/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 *
 * @author vipaol
 */
public class ActivityCanvas extends JFrame {
    public static ActivityCanvas inst = null;
    static JComponent currentActivity = null;
    
    private ActivityCanvas() {
        //setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public static ActivityCanvas getInstance() {
        if (inst == null) {
            inst = new ActivityCanvas();
        }
        return inst;
    }
    
    public static void setActivity(JPanel panel, String title, boolean doAddScrollBar) {
        getInstance();
        inst.setTitle(title);
        if (currentActivity != null) {
            inst.remove(currentActivity);
        }
        
        if (doAddScrollBar) {
            JScrollPane listScroller = new JScrollPane(panel);
            JScrollBar scrollBar = listScroller.getVerticalScrollBar();
            int w = scrollBar.getPreferredSize().width * 3;
            int h = scrollBar.getPreferredSize().height;
            scrollBar.setPreferredSize(new Dimension(w, h));
            listScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            listScroller.setHorizontalScrollBar(null);
            currentActivity = listScroller;
        } else {
            currentActivity = panel;
        }
        
        inst.add(currentActivity);
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
    
    public static Component leftJustify(Component component) {
        Box b = Box.createHorizontalBox();
        b.add(component);
        b.add(Box.createHorizontalGlue());
        return b;
    }
    
    public static GridBagConstraints getGBC(int i, int insets, int spacingWeightY) {
        return new GridBagConstraints(
                0, i, //cell for top left corner
                1, 1, //cells to span
                1, spacingWeightY, //spacing weight
                GridBagConstraints.WEST, //where to anchor the component in the cell
                GridBagConstraints.HORIZONTAL, //how to fill extra space
                new Insets(insets, insets, insets, insets), //insets for the cell
                0, 0);                          //additional padding
    }
}
