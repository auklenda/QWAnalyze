/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;

/**
 *
 * @author Alf
 */
public class QWversion {
 static String AUTHOR = null;
 static String COMPANY=null; 
 static String COPYRIGHT= null;
 static String DESCRIPTION= null;
 static String VERSION= null;
 static String BUILDNUM= null;
 static String BUILDDATE= null;
 
    public static String getVERSION() {
        return VERSION;
    }
    public static void setVERSION(String VERSION) {
        QWversion.VERSION = VERSION;
    }
    public static String getAUTHOR() {
        return AUTHOR;
    }
    public static void setAUTHOR(String AUTHOR) {
        QWversion.AUTHOR = AUTHOR;
    }
    public static String getCOMPANY() {
        return COMPANY;
    }

    public static void setCOMPANY(String COMPANY) {
        QWversion.COMPANY = COMPANY;
    }

    public static String getCOPYRIGHT() {
        return COPYRIGHT;
    }

    public static void setCOPYRIGHT(String COPYRIGHT) {
        QWversion.COPYRIGHT = COPYRIGHT;
    }

    public static String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public static void setDESCRIPTION(String DESCRIPTION) {
        QWversion.DESCRIPTION = DESCRIPTION;
    }

    public static String getBUILDNUM() {
        return BUILDNUM;
    }

    public static void setBUILDNUM(String BUILDNUM) {
        QWversion.BUILDNUM = BUILDNUM;
    }

    public static String getBUILDDATE() {
        return BUILDDATE;
    }

    public static void setBUILDDATE(String BUILDDATE) {
        QWversion.BUILDDATE = BUILDDATE;
    }
 
     
}
