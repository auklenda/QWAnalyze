/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * QWWJdbc.java
 *
 * Created on 26.aug.2010, 13:59:39
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.awt.Color;
import java.io.IOException;
import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.BasicStroke;
import javax.swing.BorderFactory;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.renderer.category.BarRenderer;

// JFree 
import org.jfree.chart.ChartFactory;
import org.jfree.data.time.Second;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.chart.title.TextTitle;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import javax.swing.JFileChooser;
/**
 *
 * @author aauklend
 */
public class QWWJdbc extends javax.swing.JDialog {
    String hdr = null;
    String start = null;
    String last = null;
    String fnamePart =null;
    int xx;
    int yy;
    JFreeChart chart;

    /** Creates new form QWWJdbc */

    public QWWJdbc(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public QWWJdbc(java.awt.Frame parent, String title,boolean modal,String header) {
        super(parent,title, modal);
        initComponents();
        hdr = header;
    }

    public void displayPool() {
      String inpr;
        while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.jdbc) {
                int offset = 0;
                ArrayList al = QWUtil.tokenize(inpr);
                System.out.println("");
                int pools = Integer.parseInt((String)al.get(4));
                for (int i = 0; i < pools; i++) {
                     System.out.println((String)al.get(5 + offset));
                     offset += 8;
                }
                
            }
        }
        this.setVisible(true);
    }
    /**
     *
     * @param graphFile
     */
    private void saveChart(String graphFile) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);
        } catch (IOException ex) {
            Logger.getLogger(QWWJdbc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *
     * @param title
     */
    private void setFname(String title) {
        int i = title.trim().indexOf(" ");
        if (i != -1)
           fnamePart =  title.trim().substring(0,i);
        else fnamePart = "chart";
    }

    public JFreeChart createBPChart(CategoryDataset dataset,
            String title,
            String yaxis,
            String start,
            String last) {
        this.start = start;
        this.last = last;
        return(createBPChart(dataset,title,yaxis));
    }

    private JFreeChart createBPChart(CategoryDataset dataset,String title, String yaxis) {
          chart = ChartFactory.createBarChart(
            title,
            "BP",
            yaxis,
            dataset,
            PlotOrientation.HORIZONTAL,
    //                PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tool tips
            false  // URLs
        );
        setFname(title);
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
        Rectangle rt = jPanel1.getBounds();
        xx = rt.width -0;
        yy = rt.height - 0;
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
/*
        ValueAxis domainAxis = new DateAxis("COUNT");
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
*/
        plot.setDomainGridlinesVisible(true);
        plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8f);
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        StandardCategoryToolTipGenerator tt
                = new StandardCategoryToolTipGenerator("{1}: {2} projects",
                new DecimalFormat("0"));
        renderer.setBaseToolTipGenerator(tt);
        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        renderer.setSeriesPaint(0, gp0);
        ChartPanel cpanel = new ChartPanel(chart);
        cpanel.setPreferredSize(new java.awt.Dimension(xx,yy));
        setContentPane(cpanel);
//        jPanel1.add(cpanel);
//        jPanel1.setVisible(true);
        return chart;
    }
    /**
     *
     * @param prop
     */
    public CategoryDataset createDataset(Properties prop) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      for (Enumeration en = prop.keys();en.hasMoreElements();){
           String name = (String)en.nextElement();
           String p = prop.getProperty(name);
           int count = Integer.parseInt(p);
           dataset.addValue(count,"Encountered",name);
      }
        return dataset;
    }
    /**
     * 
     * @param maxAge
     */
    public void jdbcUsage(int maxAge) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        JFreeChart chart = ChartFactory.createTimeSeriesChart("SI Heap Memory Usage",
                "Hour of Operation","Heap Size (GB)", dataset, true, true,
                false);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer
                = (XYLineAndShapeRenderer) plot.getRenderer();

        //Configure the presentation of the items.
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setSeriesShape(0, new Rectangle(2, 2));
        renderer.setSeriesShape(1, new Rectangle(2, 2));
        renderer.setBaseLinesVisible(true);
        renderer.setBaseShapesFilled(true);
        renderer.setBaseShapesVisible(true);
        renderer.setSeriesStroke(0, new BasicStroke(3f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL));
        renderer.setSeriesStroke(1, new BasicStroke(3f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL));

        // create two series that automatically discard data more than 30
        // seconds old...
        //total = new TimeSeries("Total Memory", Millisecond.class);
        TimeSeries total = new TimeSeries("Comitted Heap", Second.class);
        total.setMaximumItemAge(maxAge);
        TimeSeries free = new TimeSeries("Used Heap", Second.class);
        free.setMaximumItemAge(maxAge);
       // TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(total);
        dataset.addSeries(free);
        Rectangle rt = jPanel1.getBounds();
        int xx = rt.width - 30;
        int yy = rt.height - 30;
        DateAxis domain = new DateAxis("Time");
        NumberAxis range = new NumberAxis("Heap Memory");
        domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        plot.setForegroundAlpha(0.5f);
        domain.setAutoRange(true);
        domain.setLowerMargin(0.0);
        domain.setUpperMargin(0.0);
        domain.setTickLabelsVisible(true);
        //range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        range.setStandardTickUnits(NumberAxis.createStandardTickUnits());

        //JFreeChart chart1 = new JFreeChart("SI Heap Memory Usage",
        //        new Font("SansSerif", Font.BOLD, 24), plot, true);
        ChartUtilities.applyCurrentTheme(chart);
        ChartPanel chartPanel = new ChartPanel(chart, true);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createLineBorder(Color.black)));
        chartPanel.setPreferredSize(new java.awt.Dimension(xx, yy));

        //jp.add(chartPanel, "Memory");
    }
/*
    private void updateHeapChart(int tmSeries) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeTaken);
        Date now = new Date(timeTaken);
        int ss = cal.get(Calendar.SECOND);
        int mm = cal.get(Calendar.MINUTE);
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int wday = cal.get(Calendar.DAY_OF_WEEK);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        try {
            if (tmSeries == 1) {
                total.add(new Second(ss, mm, hh, wday, month, year), totalMEM);
                free.add(new Second(ss, mm, hh, wday, month, year), (totalMEM - totalFREE));
            } else if (tmSeries == 2) {
                activeWFC.add(new Second(ss, mm, hh, wday, month, year), actives);
                waitingWFC.add(new Second(ss, mm, hh, wday, month, year), waiters);

            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

  */
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
       JFileChooser fc = new JFileChooser("c:");
        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        int ret = fc.showOpenDialog(jPanel1);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String inFile = f.getAbsolutePath() +  "\\" + fnamePart + "-" + QWUtil.getDateAndTime() + ".jpg";
//            saveChart(inFile + "\\" + fnamePart + "-" + getDateAndTime() + ".jpg");
            saveChart(inFile);
        }
}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QWWJdbc dialog = new QWWJdbc(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
