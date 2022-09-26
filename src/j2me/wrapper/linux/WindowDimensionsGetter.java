/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper.linux;

import java.awt.Canvas;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFrame;

/**
 *
 * @author vipaol
 */
public class WindowDimensionsGetter extends JFrame implements Runnable {
    int w = 1;
    int h = 1;
    Canvas canvas = new Canvas();
    
    public WindowDimensionsGetter() {
        System.out.println("Getting window size...");
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        canvas.setBackground(Color.black);
        add(canvas);
        setVisible(true);
        (new Thread(this, "Main")).start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) { }
        
        w = canvas.getWidth();
        h = canvas.getHeight();
        System.out.println(w + " " + h);
        
        try {
            File f = new File(MIDletManager.dir + "wrapper-files/config/window-dimensions.txt");
            if (!f.exists()) {
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                f.createNewFile();
            }
            PrintWriter writer = new PrintWriter(f, "UTF-8");
            writer.println("W=" + w + " H=" + h + " # do not change. you may want to change SCALE in config.txt" + "\n");
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
