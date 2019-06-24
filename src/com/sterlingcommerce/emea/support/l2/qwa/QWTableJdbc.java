/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.awt.Font;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.CategoryLabelPositions;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.axis.CategoryAxis;

// XYDataset
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GradientPaint;
/**
 *
 * @author aauklend
 */
public class QWTableJdbc {
    static TableColumnModel colModel = null;
    static String [] colNames;
    String start = null;
    String last = null;
    String fnamePart = null;
    String graphFile = null;
    javax.swing.JScrollPane js;
    javax.swing.JTabbedPane jp;


    public QWTableJdbc() {
    }
    public QWTableJdbc( javax.swing.JScrollPane jscroll, javax.swing.JTabbedPane jpanel ) {
        this.js = jscroll;
        this.jp = jpanel;
    }
    /**
     * Called from QWAnaGUI to display JDBC Connection Pool usage
     * ListJDBCInfo will display action in a JTable and graphically
     * @param str
     * @param action
     * @return
     */
    public void ListJDBCInfo(String actionStr,int action) {
     String inpr = null;
     int poolField = action;
     if (action == 7) {
         poolField = 2;
     }
     ArrayList inRecord = new ArrayList();
     // Get all JDBC records
     while ((inpr = QWUtil.readLine(QWAGlobal.jdbc)) != null) {
           inRecord.add((String)inpr);
     }
     int rows = inRecord.size();
     ArrayList al = QWUtil.tokenize((String)inRecord.get(0));      
     int nCols = Integer.parseInt((String)al.get(4));
     int offset = 5;
     colNames = new String[nCols+1];
     colNames[0] = "Time";
     // Build PoolName header
     for (int i=0 ; i < nCols ; ++i){
         String tmp = (String)al.get(offset);
         colNames[i+1] = tmp.substring(1);
         offset += 8;
     }
     if (action == 8) {
         buildUsageChart(inRecord,nCols,actionStr);
         return;
     }
     // Time Graph for each pool
     TimeSeries []ts =  new TimeSeries[nCols];
     for (int i = 0; i < nCols; i++) {
          ts [i] = new TimeSeries(colNames[i+1] + "", Second.class);
     }
     // now set up JTable
 //    Object [][] rowData = new Object[rows][nCols+1];
     int rowIndex = 0;
     int [] previous = new int[nCols];
     Iterator it = inRecord.iterator();
     while (it.hasNext()) {
         String record = (String)it.next();
         al = QWUtil.tokenize(record);
       //  rowData[rowIndex][0] = al.get(2);
         offset = 6 + poolField;
         if (action == 7 ) {      // ACTIVE items
            for (int i=0 ; i < nCols ; ++i){
                 if (rowIndex == 0) {
                    //rowData[rowIndex][i+1] = (String)al.get(offset);
               //     rowData[rowIndex][i+1] = "0";
                    previous[i]  = Integer.parseInt((String)al.get(offset));
                 } else  {
                    int tmp =  Integer.parseInt((String)al.get(offset));
                    int tmp1 = previous[i];
                //    rowData[rowIndex][i+1]  = String.valueOf(tmp - tmp1);
                    int data =  tmp - tmp1;
                    if (data > 10000)
                         System.out.println("noooo");
                    createTestJDBCDataset(ts[i],record,data);
                    previous[i] = tmp;
                 }
                 offset += 8;
           }
         } else if (action == 1) {
                    for (int i=0 ; i < nCols ; ++i){
                        int buffered = Integer.parseInt((String)al.get(offset));
                        if (buffered >= 500)
                            buffered = buffered -500;
                        createTestJDBCDataset(ts[i],record,buffered);
                        //rowData[rowIndex][i+1]  = String.valueOf(buffered);
                        offset += 8;
                    }
         } else {
            for (int i=0 ; i < nCols ; ++i){
                // rowData[rowIndex][i+1]  = (String)al.get(offset);
                 createTestJDBCDataset(ts[i],record,Integer.parseInt((String)al.get(offset)));
                 offset += 8;
            }
         }
         rowIndex += 1;
     }
     TimeSeriesCollection dataset = new TimeSeriesCollection();
     for (int i = 0; i < nCols; i++) {
         if (ts[i] != null)
             dataset.addSeries(ts[i]);
     }
     String yaxis_header =get_yaxis_header(action);
     String gtitle = "JDBC " + actionStr + " - " + QWAGlobal.nodeName;
     createChart(dataset,gtitle,yaxis_header, QWAGlobal.start, QWAGlobal.last,jp,actionStr );
     return;
     //return(dataset);
   }
    /**
     * Display JDBC activities in table format
     * @param actionStr
     * @param action
     * FMT : 0 -Conn 1 - Max  2 -GetItem 3 -Wait  4 -Buffered  5 -Delete  6 -BadCount
     */
    public void ListJDBCTable(String actionStr,int action) {
     String inpr = null;
     int poolField = action;
     if (action == 7) {
         poolField = 2;
     }
     ArrayList inRecord = new ArrayList();
     // Get all JDBC records
     while ((inpr = QWUtil.readLine(QWAGlobal.jdbc)) != null) {
           inRecord.add((String)inpr);
     }
     int rows = inRecord.size();
     ArrayList al = QWUtil.tokenize((String)inRecord.get(0));
     int nCols = Integer.parseInt((String)al.get(4));
     int offset = 5;
     colNames = new String[nCols+1];
     colNames[0] = "Time";
     // Build PoolName header
     for (int i=0 ; i < nCols ; ++i){
         String tmp = (String)al.get(offset);
         colNames[i+1] = tmp.substring(1);
         offset += 8;
     }
     if (action == 5) {
     }
     Object [][] rowData = new Object[rows][nCols+1];
     int rowIndex = 0;
     //int [] previous = new int[nCols];
     long [] previous = new long[nCols];
     Iterator it = inRecord.iterator();
     while (it.hasNext()) {
         String record = (String)it.next();
         al = QWUtil.tokenize(record);
         rowData[rowIndex][0] = al.get(2);
         offset = 6 + poolField;
         if (action == 7 ) {      // ACTIVE items
            for (int i=0 ; i < nCols ; ++i){
                 if (rowIndex == 0) {
                    //rowData[rowIndex][i+1] = (String)al.get(offset);
                    rowData[rowIndex][i+1] = "0";
                    previous[i]  = Long.parseLong((String)al.get(offset));
                 } else  {
                    long tmp =  Long.parseLong((String)al.get(offset));
                    long tmp1 = previous[i];
                    rowData[rowIndex][i+1]  = String.valueOf(tmp - tmp1);
                    long data =  tmp - tmp1;
//                    createTestJDBCDataset(ts[i],record,data);
                    previous[i] = tmp;
                 }
                 offset += 8;
           }
         } else if (action == 1) {   // deduct Buffered part because it creates a connection
                    for (int i=0 ; i < nCols ; ++i){
                        int buffered = Integer.parseInt((String)al.get(offset));
                        if (buffered >= 500)
                            buffered = buffered -500;
                        rowData[rowIndex][i+1]  = String.valueOf(buffered);
                        offset += 8;
                    }
         } else {
            for (int i=0 ; i < nCols ; ++i){
                 rowData[rowIndex][i+1]  = (String)al.get(offset);
                 offset += 8;
            }
         }
         rowIndex += 1;
     }
     javax.swing.JTable jt = new javax.swing.JTable(rowData,colNames);
     colModel = jt.getColumnModel();
     String tabName =  actionStr + "-List";
     remove_tab_if_exist(jp,tabName);;
     js.add(jt);
     jp.add(js,actionStr + "-List");
     jp.setSelectedIndex(jp.getTabCount()-1);
     jp.setVisible(true);
     js.setViewportView(jt);
     return;
     //return(dataset);
   }

   protected JTableHeader createDefaultTableHeader() {
       return new JTableHeader(){
           public String getToolTipText(java.awt.event.MouseEvent e) {
               String tip = null;
               java.awt.Point p = e.getPoint();
               int index = colModel.getColumnIndexAtX(p.x);
               int realIndex = colModel.getColumn(index).getModelIndex();
               return colNames[realIndex];
           }
       };
   }
   /**
    * Create Timeseries for active queues
    * @return
    */
    public static void createTestJDBCDataset(TimeSeries ts,String inpr,int inUse) {
      String tim = inpr.substring(17,22);
      int year = Integer.parseInt(inpr.substring(6,10));
      int month = Integer.parseInt(inpr.substring(11,13));
      int wday = Integer.parseInt(inpr.substring(14,16));
      int hh = Integer.parseInt(inpr.substring(17,19));
      int mm = Integer.parseInt(inpr.substring(20,22));
      int ss = Integer.parseInt(inpr.substring(23,25));
      try {
          ts.add(new Second(ss,mm,hh,wday,month,year),inUse);
      } catch (org.jfree.data.general.SeriesException exp) {
          ts.update(new Second(ss,mm,hh,wday,month,year),inUse);
          System.out.println(inpr);
      }
    }
    /**
     *
     * @param dataset
     * @param title
     * @param yaxis
     * @return
     */
    //private JFreeChart createChart(TimeSeriesCollection dataset,String title,String yaxis,javax.swing.JPanel jpan) {
      private void createChart(TimeSeriesCollection dataset,
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

        int xx = 0;
        int yy = 0;
        setFname(title);
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(Color.white);
        Rectangle rt = jpan.getBounds();
        xx = rt.width;
        yy = rt.height;
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYItemRenderer r = plot.getRenderer();
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
        remove_tab_if_exist(jpan,tabName);
        jpan.add(cpanel,tabName);
        jpan.setSelectedIndex(jpan.getTabCount()-1);
        jpan.setVisible(true);
    }
    /**
     *
     * @param inRecord
     * @param nCols
     * @param tabName
     */
    private void buildUsageChart(ArrayList inRecord, int nCols,String tabName) {
     long [] poolCountStart = new long[nCols];  // only interested in this periode
     long [] poolCountEnd = new long[nCols];    // max 24 hours
     boolean getFirst = false;
     Iterator it = inRecord.iterator();
     while (it.hasNext()) {
       int offset = 5;
       String record = (String)it.next();
       ArrayList al = QWUtil.tokenize(record);
       for (int i=0 ; i < nCols ; ++i){
           if (!getFirst) {
               poolCountStart[i]  = Long.parseLong((String)al.get(offset+3));
           } else
             poolCountEnd[i]  = Long.parseLong((String)al.get(offset+3));
           offset += 8;
       }
       getFirst = true;
     }
     DefaultCategoryDataset dataset = new DefaultCategoryDataset();
     for (int i=0 ; i < nCols ; ++i){
         dataset.addValue(poolCountEnd[i]-poolCountStart[i],"Pools",colNames[i+1]);
     }
     createUsageChart(dataset,"Pool Usage","Accumulated GetItems",tabName);
    }
    /**
     *
     * @param dataset
     * @param title
     * @param yaxis
     * @return
     */
    private void createUsageChart(CategoryDataset dataset,String title, String yaxis,String tabName) {
          JFreeChart chart = ChartFactory.createBarChart(
            title,
            "POOL Usage",
            yaxis,
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tool tips
            false  // URLs
        );
//        setFname(title);
        String shdr = "   From: " + QWAGlobal.start + "    To: " + QWAGlobal.last + " (accumulated values this periode)";
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);

        Rectangle rt = jp.getBounds();
        int xx = rt.width -0;
        int yy = rt.height - 0;

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(true);
        plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8f);
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setVerticalTickLabels(false);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        StandardCategoryToolTipGenerator tt
                = new StandardCategoryToolTipGenerator("{1}: {2} projects",
                new DecimalFormat("0"));
        renderer.setBaseToolTipGenerator(tt);
        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, Color.lightGray);

   //     GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
     //           0.0f, 0.0f, new Color(0, 0, 64));
        renderer.setSeriesPaint(0, gp0);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        ChartPanel cpanel = new ChartPanel(chart);
        cpanel.setPreferredSize(new java.awt.Dimension(xx,yy));
        remove_tab_if_exist(jp,tabName);
        //setContentPane(cpanel);
        jp.add(cpanel,"PoolUsage");
        jp.setSelectedIndex(jp.getTabCount()-1);
        jp.setVisible(true);
        return;// chart;
    }
    private void setFname(String title) {
        int i = title.trim().indexOf(" ");
        if (i != -1)
           fnamePart =  title.trim().substring(0,i);
        else fnamePart = "chart";
    }
    private void remove_tab_if_exist(javax.swing.JTabbedPane jpan,String tabName) {
        int tabInx = -1;
        // Check if this TAB exist
        for (int i = 0; i < jpan.getTabCount(); i++) {
             String tName = jpan.getTitleAt(i);
             //if (tabName.equals(tName)) {
             //if (tabName.startsWith(tName)) {
             if (tName.equalsIgnoreCase(tabName)) {
                 tabInx = i;
             }
        }
        if ( tabInx != -1)
           jpan.remove(tabInx);
    }
    private String get_yaxis_header(int action) {
     if (action ==0)
         return(" Connections in each pool") ;
     else if (action == 1)
         return("Maximum Connection Count");
     else if (action ==2)
         return("GetItem (connection) Count ");
     else if (action ==3)
         return("Buffered Count (new Connection)");
     else if (action ==4)
         return("GetItem Count between samples");
     return null;
    }
/*
    private long getMillis(ArrayList al) {
    ArrayList date = QWUtil.tokenize((String)al.get(1),"-");
    ArrayList time = QWUtil.tokenize((String)al.get(2),":");
    int yr = Integer.parseInt((String)date.get(0));
    int mn = Integer.parseInt((String)date.get(1));
    int dd = Integer.parseInt((String)date.get(2));
    int hh = Integer.parseInt((String)time.get(0));
    int mi = Integer.parseInt((String)time.get(1));
    int se = Integer.parseInt((String)time.get(2));
    Calendar cal = Calendar.getInstance();
    cal.set(yr,mn,dd,hh,mi,se);
    return(cal.getTimeInMillis());
  }
 *
 */

}
