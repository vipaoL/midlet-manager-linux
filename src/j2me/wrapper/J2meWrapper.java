/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package j2me.wrapper;

import static j2me.wrapper.MIDletManager.showError;
import java.io.IOException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author vipaol
 */
public class J2meWrapper {

    static String OS_NAME = System.getProperty("os.name");
    static boolean headlessMode = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try { // set system look and feel, it looks better
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (UnsupportedLookAndFeelException ex) {
        }

        switch (args.length) {
            case 0:
                new MIDletManager("");
                break;
            case 1:
                if (args[0].equals("get-dimensions")) {
                    new WindowDimensionsGetter();
                } else {
                    new MIDletManager(args[0]);
                }
                break;
            default:
                String command = args[0];
                if (command.equals("install")) {
                    try {
                        new MIDletInstaller(args);
                    } catch (IOException | NullPointerException ex) {
                        String errorMessage = ex.getMessage();
                        errorMessage += "\n\nDebug info:\n";
                        StackTraceElement[] stackTrace = ex.getStackTrace();
                        for (int i = 0; i < stackTrace.length; i++) {
                            errorMessage += stackTrace[i].toString() + "\n";
                        }
                        showError(errorMessage);
                        System.exit(1);
                    }
                }
                break;
        }
    }

}
