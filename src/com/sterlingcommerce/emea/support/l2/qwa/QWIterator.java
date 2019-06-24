/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.io.*;
import java.util.*;
/**
 *
 * @author AAuklend
 */
public class QWIterator implements Iterator{
    String forwardRead = null;
    String InFile = null;
    BufferedReader in = null;
    public QWIterator() {}
    
    public QWIterator(String infile) {
      InFile = infile;
    }
    public BufferedReader openInFile(String fn) {
        try {  
             return(new BufferedReader(new InputStreamReader(new FileInputStream(fn))));
      } catch (Exception exp) {
            exp.printStackTrace();
            return null;
      }  
    }
    public Iterator iterator() {
      try {  
        if (in != null) {
            in.close();
        }
        in = openInFile(InFile);
        forwardRead = in.readLine();
      } catch (IOException iexp) {
          iexp.printStackTrace();
      }  
      return (Iterator)this;
    }
    public boolean hasNext() {
        if ((in != null) && (forwardRead != null))
            return true;
        return false;
    }
    public String next() {
        String tmp = forwardRead; 
        try {  
        in = openInFile(InFile);
        forwardRead = in.readLine();
      } catch (IOException iexp) {
          iexp.printStackTrace();
      }  
      return tmp;
    }
    public void remove(){
        
    }
}
