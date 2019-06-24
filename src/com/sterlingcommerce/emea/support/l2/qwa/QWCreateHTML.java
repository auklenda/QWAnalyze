/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.io.PrintWriter;
import java.util.*;
/**
 *
 * @author no055212
 */
public class QWCreateHTML {
    StringBuilder sb = new StringBuilder();
    Properties prob = null;
    String tableSmallFont = "<table border='1' cellspacing='0' cellpadding='4' style='font-size: small;'>";
    
    public QWCreateHTML() {
     
    }
    public static StringBuilder getSB(){
         return(new StringBuilder());
    }
     public static void createHDR() {
        if (QWAGlobal.htmlFile != null) {
            StringBuilder sb = getSB();
            sb.append("<!DOCTYPE html PUBLIC ").append('"').append("-//W3C//DTD XHTML 1.0 Transitional//EN").append('"').append("\n");
            sb.append("xmlns=").append('"').append("http://www.w3.org/1999/xhtml").append('"').append("\n");
            sb.append("<HEAD>").append("</HEAD>").append("\n");
            //sb.append("<h2>QWA Report - ").append(QWUtil.getDateAndTime()).append("</h1>").append("\n");
            sb.append("<h2>Sampling from: ").append(QWAGlobal.start).append("</h2>").append("\n");
            QWAGlobal.htmlFile.println(sb.toString());
        }
    }
    
    public static void createIMG(String hdr, String filename){
       if (QWAGlobal.htmlFile != null) {
            StringBuilder sb = getSB();  
       sb.append("<img").append(" src=").append(filename).append(">");
       QWAGlobal.htmlFile.println(sb.toString());
       }    
    }
  /**
     *  Create HTML report
     */
    
    public void printHTMLReport(PrintWriter queFile) {    
         queFile.println("<h2>QUEUE Activity during this periode</h2>");
         queFile.println("<h3>Histogram of BPs Waiting on Queues:</h3>");
         queFile.println("<p>Each col represent BP wait count range ");
         queFile.println(" (e.g 1-5) and each cell the number of times we encounter BPs in a range</p>");
         queFile.println("<table border='1' cellspacing='0' cellpadding='4' style='font-size: small;'>");
         //queFile.println("<font size='1'>");
         StringBuilder sb = new StringBuilder();
         sb.append("<tr><th>Que</th>");
         sb.append("<th>1-5</th><th>6-10</th><th>11-20</th><th>21-30</th><th>31-40</th>");
         sb.append("<th>41-50</th><th>51-70</th><th>71-90</th><th>91-99</th><th>gt 100</th></tr>");
         queFile.println(sb.toString());
         for (int i = 0; i < 9; i++) {
            queFile.println("<tr><th>Queue" + (i+1) + "</th6>");
            queFile.println("<td>" + QWAGlobal.histoGram[i][0] + "</td><td>" + QWAGlobal.histoGram[i][1] + "</td><td>" + QWAGlobal.histoGram[i][2] + "</td>");
            queFile.println("<td>" + QWAGlobal.histoGram[i][3] + "</td><td>" + QWAGlobal.histoGram[i][4] + "</td><td>" + QWAGlobal.histoGram[i][5] + "</td>");
            queFile.println("<td>" + QWAGlobal.histoGram[i][6] + "</td><td>" + QWAGlobal.histoGram[i][7] + "</td><td>" + QWAGlobal.histoGram[i][8] + "</td>");
            queFile.println("<td>" + QWAGlobal.histoGram[i][9] + "</td></tr>");
         }
         queFile.println("</table>");
         queFile.println("<h2>Highest Waiting Count found during this periode:</h2>");
         queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
         queFile.println("<font size='1'>");
         for (int i = 0; i < 9; i++) {
            if (QWAGlobal.maxDepth[i] == 0) {
                queFile.println("<tr><th>Queue" + (i+1) + "</th><td> had no queue during this periode</td></tr>");    
            } else { 
                queFile.println("<tr><th>Queue" + (i+1) + 
                        "</th><td> peaked at " + QWAGlobal.maxDepthTime[i] +
                        " with a queue depth of " + QWAGlobal.maxDepth[i] + "</td></tr>");
            }    
         }
         queFile.println("</font></table>"); 
         queFile.println("<h2>Calculated Threads higher then Active Threads while queue (under-sceduleing)</h2>");
         for (int i = 0; i < 9; i++) {
            if (QWAGlobal.calcHigherThread[i] == 0) {
                queFile.println("<li>Queue" + (i+1) + "  had no under-schedule in this periode</li>");    
            } else { 
                queFile.println("<li>Queue" + (i+1) + " was under-scheduled " + QWAGlobal.calcHigherThread[i] + " times in this periode</li>");
            }    
        }
        queFile.println("</font></table>"); 
        queFile.println("<h2>Total number of sampled WFC's found on Execution Queues</h2>");
        for (int i = 0; i < 9; i++) {
               queFile.println("<li>Queue" + (i+1) + "  found " + QWAnalyze.totalWFCs[i] + " BP's active during sampling</li>");    
        }
        queFile.println("<h2>Highest number of active BP's found </h2>");
        queFile.println("<li>Highest BP count: " + QWAnalyze.maxActiveThreads + " was found at :" + QWAnalyze.maxActiveThreadsTime + "</li>");    
        queFile.println("<h2>Time It Takes To Clear a Queue (sample interval)</h2>");
        queFile.println(tableSmallFont);
        //new QWAnalyze().reportHTMLQueueExist(-1);
        //queFile.println("</table>"); 
    }
    
}
