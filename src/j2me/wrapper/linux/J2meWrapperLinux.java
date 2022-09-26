/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package j2me.wrapper.linux;
/**
 *
 * @author vipaol
 */
public class J2meWrapperLinux {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            new MIDletManager(null);
        } else if (args[0].equals("get-dimensions")) {
            new WindowDimensionsGetter();
        } else {
            new MIDletManager(args[0]);
        }
    }

}
