/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2;
import java.io.File;
import java.io.FileFilter;
import java.util.Vector;
import java.util.StringTokenizer;
/**
 *
 * @author AAuklend
 */
public class WildCardFilter implements FileFilter {
    private boolean debug = false;
    private boolean allowDirs = false;
    private static final String FIND     = "find";
    private static final String ENDSWITH = "endswith";
    private static final String EXPECT   = "expect";
    private static final String ANYTHING = "anything";
    private static final String NOTHING  = "nothing";
    private int size = 0;
    private int curPos = 0;
    private String cmd = null;
    private String parm = null;
    private Vector pattern = new Vector();

    /**
     * Constructs a WildCardFilter with the supplied parameters and defaults for the rest.
     * 
     * @param filter The filter used in the accept method.
     */
    public WildCardFilter(String filter) {
        this(filter, false, false, null);
    }

    /**
     * Constructs a WildCardFilter with the supplied parameters and defaults for the rest.
     * 
     * @param filter The filter used in the accept method.
     * @param allowDirs Determines if directory entries should be accepted or not.
     */
    public WildCardFilter(String filter, boolean allowDirs) {
        this(filter, allowDirs, false, null);
    }

    /**
     * Constructs a WildCardFilter with the supplied parameters.
     * 
     * @param filter The filter used in the accept method.
     * @param allowDirs Determines if directory entries should be accepted or not.
     * @param debug Determines if debugging messages should be shown or not.
     * @param svcName Used to set the service name of the XLogger class when debugging.
     */
    public WildCardFilter(String filter, boolean allowDirs, boolean debug, String svcName) {
        this.debug = debug;
        this.allowDirs = allowDirs;
        int i = 0;
        while ( (i = filter.indexOf("**")) >= 0 ) {
            filter = filter.substring(0, i+1) + filter.substring(i+2);
        }
//        if ( debug ) { log.log("##[DEBUG]## allowDirs="+allowDirs+" filter="+filter); }
        boolean wroteExpectNothing = false;
        StringTokenizer filters = new StringTokenizer(filter, ";, ");
        StringTokenizer tokens = null;
        String token = null;
        while ( filters.hasMoreTokens() ) {
            tokens = new StringTokenizer(filters.nextToken(), "*", true);
            while ( tokens.hasMoreTokens() ) {
                wroteExpectNothing = false;
                token = tokens.nextToken();
  //              if ( debug ) { log.log("##[DEBUG]## token1="+token); }
                if ( token.equals("*") ) {
                    if ( tokens.hasMoreTokens() ) {
                        token = tokens.nextToken();
//                        if ( debug ) { log.log("##[DEBUG]## token2="+token); }
                        if ( tokens.hasMoreTokens() ) { // *<token>*<token>
                            pattern.addElement(FIND);
                            pattern.addElement(token);
                        }
                        else { // *<token>
                            pattern.addElement(ENDSWITH);
                            pattern.addElement(token);
                        }
                    }
                    else { // just '*'
                        pattern.addElement(FIND);
                        pattern.addElement(ANYTHING);
                    }
                }
                else {
                    pattern.addElement(EXPECT);
                    pattern.addElement(token);
                }
            }
            if ( !wroteExpectNothing ) {
                pattern.addElement(EXPECT);
                pattern.addElement(NOTHING);
            }
        }
        size = pattern.size();
        if ( debug ) {
            for ( i = 0; i < size; i++ ) {
//                log.log("##[DEBUG]## pattern["+i+"]="+pattern.elementAt(i).toString());
            }
        }
    }

    /** Called to determine if a particular <code>File</code> should be accepted or not.
     * @param file The <code>File</code> object to check.
     * @return Returns true if accepted, otherwise false
     */
    public boolean accept(File file) {
  //      if ( debug ) { log.log("##[DEBUG]## checking="+file.getAbsolutePath()+" dir="+file.isDirectory()); }
        boolean acceptFile = false;
        if ( !file.isDirectory() || allowDirs ) { // always process files or directories if allowed
            String name = file.getName();
            int cmdPos = 0;
            int fndPos = 0;
            int nameLen = name.length();
            curPos = 0;
            while ( cmdPos < size ) {
                cmd = pattern.elementAt(cmdPos).toString(); // find or expect
                parm = pattern.elementAt(cmdPos+1).toString(); // anything, nothing, or a token
 //               if ( debug ) { log.log("##[DEBUG]## cmd="+cmd+" parm="+parm); }
                if ( cmd.equals(ENDSWITH) ) {
                    if ( name.endsWith(parm) ) {
                        acceptFile = true;
                        break;
                    }
                    cmdPos = nextPattern(cmdPos+2);
                }
                else if ( cmd.equals(FIND) ) {
                    if ( parm.equals(ANYTHING) ) { // done if 'find anything'
                        acceptFile = true;
                        break;
                    }
                    if ( curPos == nameLen ) { // can't search if already at the end of string
                        cmdPos = nextPattern(cmdPos+2);
                    }
                    else if ( (fndPos = name.indexOf(parm, curPos)) >= 0 ) { // search for parm from curPos
                        curPos = fndPos + parm.length();
//                        if ( debug ) { log.log("##[DEBUG]## find fndPos="+fndPos+" curPos="+curPos); }
                    }
                    else { // didn't find pattern
                        cmdPos = nextPattern(cmdPos+2);
                    }
                }
                else if ( cmd.equals(EXPECT) ) {
                    if ( parm.equals(NOTHING) ) { // done if 'expect nothing'
                        if ( curPos == nameLen ) {
                            acceptFile = true;
                            break;
                        }
                        curPos = 0; // reset - no need to call nextPattern()
                    }
                    else { // check if the expected string is at our current position
                        fndPos = name.indexOf(parm, curPos);
//                        if ( debug ) { log.log("##[DEBUG]## expect fndPos="+fndPos+" curPos="+curPos); }
                        if ( fndPos != curPos ) {
                            cmdPos = nextPattern(cmdPos+2);
                        }
                        else {
                            curPos += parm.length(); // reset current position
//                            if ( debug ) { log.log("##[DEBUG]## expect curPos="+curPos); }
                        }
                    }
                }
                cmdPos += 2;
            } // end of while
        }
//        if ( debug ) { log.log("##[DEBUG]## accepted="+acceptFile); }
        return acceptFile;
    }

    /** Called to find the next pattern.
     * @param cmdPos The current command position to use.
     * @param size The size of the pattern Vector.
     * @return The command position of the next pattern.
     */
    private int nextPattern(int cmdPos) {
        curPos = 0;
//        if ( debug ) { log.log("##[DEBUG]## nextPattern"); }
        while ( cmdPos < size ) {
            cmd = pattern.elementAt(cmdPos).toString(); // find or expect
            parm = pattern.elementAt(cmdPos+1).toString(); // anything, nothing, or a token
            if ( cmd.equals(EXPECT) && parm.equals(NOTHING) ) {
                break;
            }
            cmdPos += 2;
        }
        return cmdPos;
    }
}