/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import javax.swing.text.*;
/**
 *
 * @author no055212
 */
public class QWAText {
public static String [] heapText =
 {"The Graph shows the heap usage over a periode of time " +
  "where the ", "light area", " is the commited heap space and the ", "darker area ",
  "is used space. In a busy system it is normal to see a high " +
  "usage because the garbage collector -gc- will only start when space " +
  "is needed.\nUnder high load the GC will start more frequently, sometimes " +
  " less than a second apart and the dark area will be more solid at the top. " +
  "\n","IMPORTANT :", "After a system is coming to an operational level the heap will flatten out" +
  " which indicate a normal usage. An increase of the dark area over time could indicate a leak." +
  "\n\nPush 'Heap Usage' for a larger view of the graph."
};
public static String[] heapStyles =
                { "regular",
                  "bold",
                  "regular",
                  "bold","regular",
                  "bold","regular"
                };
public static String [] allQText =
{"The graph will show the thread activities (BPs) across all the nine (9) queues.\nA glance"+
 " at this graph will show when the peaks occours in the system.\n" +
 "The thread usage is controlled by the ","FairShare"," algorithm and should normally not exceed the " +
 " (global)"," MaxThreads"," property in noapp.properties file. This however will depend on the setting of queue's ","MinPoolSize "
 ," as it overide the global"," MaxThreads"," and a queue will ","ALLWAYS get MinPoolSize"," " +
 " threads\nKeeping the accumulated MinPoolSize lower than (global) MaxThreads will ensure a good fairShare usage." };
public static String[] allQStyles =
                { "regular",
                  "bold",
                  "regular",
                  "bold","regular",
                  "bold","regular"
                };

 public static void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "Verdana");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);

        s = doc.addStyle("icon", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
 /*
        ImageIcon pigIcon = createImageIcon("images/Pig.gif",
                                            "a cute pig");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }

        s = doc.addStyle("button", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon soundIcon = createImageIcon("images/sound.gif",
                                              "sound icon");
        JButton button = new JButton();
        if (soundIcon != null) {
            button.setIcon(soundIcon);
        } else {
            button.setText("BEEP");
        }
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.setActionCommand(buttonString);
        button.addActionListener(this);
        StyleConstants.setComponent(s, button);
  *
  */
    }
}
