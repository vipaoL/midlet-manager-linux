/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j2me.wrapper;

/**
 *
 * @author vipaol
 */
public class MIDlet {
        public int index;
        public String name = null;
        public String iconPath = null;
        public String mainClass = null;

        public MIDlet(String manifestEntryValue) {
            if (manifestEntryValue.startsWith("MIDlet-")) {
                manifestEntryValue = manifestEntryValue.split(":")[1];
            }
            String[] params = manifestEntryValue.trim().split(",");
            if (params.length > 2) {
                name = params[0].trim();
                iconPath = params[1].trim();
                if (iconPath.equals("")) {
                    iconPath = null;
                }
                mainClass = params[2].trim();
            } else {
                name = params[0].trim();
                mainClass = params[1].trim();
            }
        }
    }