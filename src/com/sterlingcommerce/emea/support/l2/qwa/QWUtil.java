/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.Component;
import java.awt.Font;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.Rectangle;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
// XYDataset
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.TickUnitSource;
/**
 *
 * @author AAuklend
 */
public class QWUtil {
    
    public static void resetInput() {
      try {
        if  (QWAGlobal.QWFile != null)
             QWAGlobal.QWFile.close();
          QWAGlobal.QWFile = new BufferedReader(new InputStreamReader(new FileInputStream(QWAGlobal.jTextField1.getText())));
      } catch (IOException exp) {
          exp.printStackTrace();
      }
    }
    public static void openReportFile() {
        
    }
    // tm HH:MIN:SEC
    public static int getTimeInSeconds(String tm) {
        ArrayList tok = tokenize(tm.trim(),":");
        int hh = (Integer.parseInt((String)tok.get(0)))*60*60;
        int min = hh + (Integer.parseInt((String)tok.get(1)))*60;
        return (min + Integer.parseInt((String)tok.get(2)));
    }
    public static String getTime(String line,int offs, String delimit) {
        return(line.substring(offs,line.indexOf(delimit)));
    }
    
    public static long getTimeInMS(String instring) {
       String rcv = getTime(instring,11,"]");
       long ms = Integer.parseInt(rcv.substring(1,3))*60*60*1000;
       ms += Integer.parseInt(rcv.substring(4,6))*60*1000;
       ms += Integer.parseInt(rcv.substring(7,9))*1000;
       long tms = Integer.parseInt(rcv.substring(10));
       if (rcv.substring(10).length() == 1)
          ms += tms*100;
       else if (rcv.substring(10).length() == 2 )
          ms += tms*10;
       else ms += tms;
     return ms;   
    }
    
    private static long getMS(String tm) {
       long ms = Integer.parseInt(tm.substring(1,3))*60*60*1000;
       ms += Integer.parseInt(tm.substring(4,6))*60*1000;
       ms += Integer.parseInt(tm.substring(7,9))*1000;
       return ms; 
    }
    
    public static String getTimeString(String line,int offs, String delimit) {
        return(line.substring(offs,line.indexOf(delimit)));
    }

    public static String getDateAndTime() {
        return (getDateAndTime("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getDateAndTime(String frm) {
        Date now = new Date(System.currentTimeMillis());
        String formatStr = frm;
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        return (formatter.format(now));
    }
    public static ArrayList tokenize(String str) {
        return(tokenize(str," "));
    }
    public static ArrayList tokenize(String str,String token) {
        ArrayList al = new ArrayList();
	int i0 = 0;
        for ( StringTokenizer st = new StringTokenizer(str,token); st.hasMoreTokens();) {
            al.add(i0++,(String)st.nextToken()); 
        }
        return al;
    }
    public static int isitDigit(String strN) {
      try {
        return(Integer.parseInt(strN));
      } catch (Exception ex) {
          return -1;
      }
    }
    public static int isitQueue(String strN) {
       int tmp = -1;
        try {
           tmp = Integer.parseInt(strN);
           if (tmp < 1 || tmp > 9)
               return -1;
      } catch (Exception ex) {
          return -1;
      }
      return tmp;
    }

    public static boolean isDigit(String strN) {
      try {
        int num = Integer.parseInt(strN);
        return true;
      } catch (Exception ex) {
          return false;
      }
    }
    public static String readLine() {
       String str;
        try {
             str = QWAGlobal.QWFile.readLine();
             return str;
    //       return QWFile.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("QWUtil.readLine got a IOException");
        }
        return null;
    }
    public static String readLine(int recType) {
       String str;
        try {
             while((str = QWAGlobal.QWFile.readLine()) != null){
                if ((getRecordType(str)) == recType)
                     return str;
             }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("QWUtil.readLine got a IOException");
        }
        return null;
    }
    public static boolean insertIfNotIn(String key,String obj,Properties prop) {
        if (!prop.containsKey(key)) {
            prop.put(key, obj);
            return true;
        }
        return false;
    }
    public static boolean insertRegardless(String key,String obj,Properties prop) {
          prop.put(key, obj);
          return true;
    }
    
    private static boolean validateMem(String rec) {
        int i = rec.indexOf("(GB)", 20);
        String r = rec.substring(i+4,rec.indexOf("TOT", i+4));
        if (r.indexOf(".") == -1)
            return false;
        return true;
    }

    public static int getRecordType(String rec) {
       int recType = 0;
       String tmp = rec.substring(26,29);    //  Record Type
       if (tmp.equalsIgnoreCase("QUE"))
           recType = QWAGlobal.que;
       else if (tmp.equalsIgnoreCase("WFC"))
           recType =  QWAGlobal.wfc;
       else if (tmp.equalsIgnoreCase("MEM")) {
           if (!validateMem(rec))
               recType =  QWAGlobal.noop;         // MEM
           else recType =  QWAGlobal.mem;         // MEM
       } else if (tmp.equalsIgnoreCase("HDR"))
           recType =  QWAGlobal.hdr;
       else if (tmp.equalsIgnoreCase("JDB"))
           recType =  QWAGlobal.jdbc;
      else if (tmp.equalsIgnoreCase("CFG"))
           recType =  QWAGlobal.cfg;    
      else if (tmp.equalsIgnoreCase("ENV"))
           recType =  QWAGlobal.env;       
       return recType;
    }
    public static JFreeChart createMemChart(TimeSeriesCollection dataset,
            String title,
            String yaxis,
            String start,
            String last,
            javax.swing.JTabbedPane jPanel,
            String tabName) {
      String fnamePart = null;
      String graphFile = null;
      int xx = 0;
      int yy = 0;
      JFreeChart chart = ChartFactory.createXYAreaChart(
            title,
            "Time",
            yaxis,
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tool tips
            false  // URLs
        );
//        setFname(title);
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);

        Rectangle rt = jPanel.getBounds();
        xx = rt.width -0;
        yy = rt.height -0;

        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis domainAxis = new DateAxis("TIME");
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setTickLabelsVisible(true);
        plot.setDomainAxis(domainAxis);
        plot.setForegroundAlpha(0.5f);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.white);
       // plot.setBackgroundPaint(Color.white);
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.yellow);
        renderer.setSeriesPaint(1, Color.black);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setTickUnit(
            new DateTickUnit(
                //DateTickUnit.MINUTE,30, new SimpleDateFormat("yyMMdd HHmm")
                DateTickUnit.MINUTE,30, new SimpleDateFormat("MM:DD")
            ),false,false
        );
        axis.setVerticalTickLabels(true);
        ChartPanel cpanel = new ChartPanel(chart);
        cpanel.setPreferredSize(new java.awt.Dimension(xx,yy));
        //setContentPane(cpanel);
        jPanel.add(cpanel,tabName);
        return null;
    }
   public static JFreeChart createThreadChart(TimeSeriesCollection dataset,
                                       String title,
                                       String yaxis,
                                       String start,
                                       String last,
                                       javax.swing.JTabbedPane jpan,
                                       String tabName) {
          JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,               // chart title
            "Time",               // domain axis label
            yaxis,                  // range axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(Color.white);
        Rectangle rt = jpan.getBounds();
        int xx = rt.width;
        int yy = rt.height;
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYItemRenderer r = plot.getRenderer();
        /*
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }
         */
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setTickUnit(
            new DateTickUnit(
                DateTickUnit.MINUTE,30, new SimpleDateFormat("HH:MM")
            ),false,false
        );
        // Set Integer as vertical
        axis.setVerticalTickLabels(true);
        NumberAxis axis1 = (NumberAxis) plot.getRangeAxis();
        TickUnitSource units = NumberAxis.createIntegerTickUnits();
        axis1.setStandardTickUnits(units);
        ChartPanel cpanel = new ChartPanel(chart);

        cpanel.setPreferredSize(new java.awt.Dimension(xx,yy));
        jpan.add(cpanel,tabName);
        return null;
    }

}
