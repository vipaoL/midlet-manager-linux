/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author vipaol
 */
public class WindowDimensionsGetter extends JFrame implements Runnable {
    MCanvas canvas = new MCanvas();
    
    public WindowDimensionsGetter() {
        System.out.println("Getting window size...");
        //setSize(480, 640);
        setMinimumSize(new Dimension(100, 200));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(canvas);
        setVisible(true);
        (new Thread(this, "Main")).start();
    }

    @Override
    public void run() {
        while (true) {
            canvas.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(WindowDimensionsGetter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    boolean saveDimensions(int w, int h) {
        System.out.println(w + " " + h);
        try {
            File f = new File(J2meWrapper.INSTALLATION_DIR + "config/window-dimensions.txt");
            if (!f.exists()) {
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                f.createNewFile();
            }
            PrintWriter writer = new PrintWriter(f, "UTF-8");
            writer.println("W=" + w + " H=" + h + " # do not change. you may want to change SCALE in config.txt" + "\n");
            writer.close();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    class MCanvas extends Canvas implements MouseListener {

        public MCanvas() {
            addMouseListener(this);
        }
        
        @Override
        public void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            FontMetrics metrics = g.getFontMetrics();
            String text = w + "x" + h;
            g.drawString(text, w/2 - metrics.stringWidth(text)/2, h/2);
            text = "Click to continue";
            g.drawString(text, w/2 - metrics.stringWidth(text)/2, h/2+metrics.getHeight());
        }
        
        public void mousePressed(MouseEvent e) {
            repaint();
            saveDimensions(getWidth(), getHeight());
            System.exit(0);
        }

        @Override
        public void mouseClicked(MouseEvent me) {}
        @Override
        public void mouseReleased(MouseEvent me) {}
        @Override
        public void mouseEntered(MouseEvent me) {}
        @Override
        public void mouseExited(MouseEvent me) {}
    }
}
