/*
 * QWBPChart.java
 *
 * Created on 09 February 2009, 10:37
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Font;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
// XYDataset
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.renderer.category.BarRenderer;


import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

import org.jfree.chart.axis.DateTickUnit;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import javax.swing.JFileChooser;

/**
 *
 * @author  AAuklend
 */
public class QWBPChart extends javax.swing.JDialog {
    String start = null;
    String last = null;
    String fnamePart =null;
    int xx;
    int yy;
    JFreeChart chart;

    /** Creates new form QWBPChart */
    public QWBPChart(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public QWBPChart(java.awt.Frame parent,String title, boolean modal) {
        super(parent,title, modal);
        initComponents();
    }
    /**
     * 
     * @param graphFile
     */
    private void saveChart(String graphFile) {
       try {
           ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);        
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
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
    /**
     * 
     * @return
     */
    public String getDateAndTime() {
     return(getDateAndTime("yyyyMMdd_HHmmss"));
    } 
    public String getDateAndTime(String frm) {
     Date now = new Date(System.currentTimeMillis());
     String formatStr = frm;
     SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
     return (formatter.format(now));   
    } 
    /**
     * 
     * @param dataset
     * @param title
     * @param yaxis
     * @param start
     * @param last
     * @return
     */
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
     * @param ArrayList (al)
     */
    public CategoryDataset createDataset(ArrayList al) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      for (int i = 0; i < 25; i++) {
           //QWAnaGUI.BPcompare o = (QWAnaGUI.BPcompare)al.get(i);
           QWBPNameInfo o = (QWBPNameInfo)al.get(i);
           dataset.addValue(o.count,"Encountered",o.name);
    }
    return dataset;
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1133, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 671, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        jMenuItem1.setText("Save In..");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       JFileChooser fc = new JFileChooser("c:");
        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        int ret = fc.showOpenDialog(jPanel1);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String inFile = f.getAbsolutePath() +  "\\" + fnamePart + "-" + getDateAndTime() + ".jpg";
//            saveChart(inFile + "\\" + fnamePart + "-" + getDateAndTime() + ".jpg");
            saveChart(inFile);
        }
}//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QWBPChart dialog = new QWBPChart(new javax.swing.JFrame(), true);
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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
