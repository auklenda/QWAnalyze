 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.io.*;
import java.util.*;
import javax.swing.JTable;
/**
 *
 * @author aauklend
 */
public class QWAReporting {
   static String s_td = "<td>";
   static String e_td = "</td>";
   static String s_tr = "<tr>";
   static String e_tr = "</tr>";
   static String s_th = "<th>";
   static String e_th = "</th>";
   static String s_h1 = "<h1>";
   static String e_h1 = "</h1>";
   static String s_h2 = "<h2>";
   static String e_h2 = "</h2>";
   static String s_h3 = "<h3>";
   static String e_h3 = "</h3>";
   
   static StringBuilder report = null;
  
   public static StringBuilder startReport() {
      report = new StringBuilder();
      report.append("<html><head>");
      return report;
    }
   public void commit() {
       if (report == null)
           return;
       report.append("</head><body></html>");
   }
public static void doTitle(String title, StringBuilder report,int count) {
    report.append(s_h2).append(title).append(e_h2);
    report.append(s_h3);
    report.append("List All BPs if more than ").append(count);
    report.append(" BPs are waiting for a Thread").append(e_h3);            
}
public static void doHeader(ArrayList al, StringBuilder report) {
    int lng = al.size();
    report.append(s_tr);
    for (int i = 0; i < lng; i++) {
         report.append(s_th).append(al.get(i)).append(e_th);
    }
    report.append(e_tr).append("\n");
    }
public static void doTable(StringBuilder report,boolean atStart) {
    if (atStart)
        report.append("<table border='1' cellspacing='0' cellpadding='4' style='font-size: small;'>"); 
    else  report.append("</table>");
}
public static void doItems(Object [][] list, int rec , int col, StringBuilder report) {
    int lng = list.length;
    for (int i1 = 0 ; i1 < lng; i1++) {
        report.append(s_tr);
        for (int i = 0; i < col; i++) {
            report.append(s_td);
            if (list[i1][i] != null)
                report.append(list[i1][i]);
            else
                report.append("     ");
            report.append(e_td);
        }
        report.append(e_tr).append("\n");
    }
}
public static void doItems(JTable jtable,StringBuffer sb) {
    int col = jtable.getColumnCount();
    int row = jtable.getRowCount();
    sb.append("<table>");
    for (int i1 = 0 ; i1 < row-1; i1++) {
        sb.append("<tr>");
        for (int i = 0; i < col-1; i++) {
            String val = (String)jtable.getValueAt(row, col);
            sb.append("<td>").append("</td>");          
        }
        sb.append("</tr>").append("\n");
    }
    sb.append("/table>");
}
public static void closeReport(StringBuilder report) {
  if (report != null)
     report.append("</body></html>");
}
public static void doCSV(Object [][] list, int rec , int col, StringBuilder report) {
    int lng = list.length;
    for (int i1 = 0 ; i1 < lng; i1++) {
        for (int i = 0; i < col; i++) {
            if (list[i1][i] != null)
                report.append(list[i1][i]).append(";");
            else
                report.append(" ;");
        }
        report.append("\n");
    }
}
}
