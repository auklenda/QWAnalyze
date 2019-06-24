/*
 * QWCharts.java
 *
 * Created on 23 January 2009, 10:32
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Font;
import javax.swing.JFileChooser;
import java.awt.*;
import java.awt.datatransfer.*;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
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

/**
 *
 * @author  AAuklend
 */
public class QWCharts extends javax.swing.JDialog {
    String start = null;
    String last = null;
    String fnamePart =null;
    int xx;
    int yy;
    JFreeChart chart;

    /** Creates new form QWCharts */
    public QWCharts(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public QWCharts(java.awt.Frame parent,String title, boolean modal) {
        super(parent,title, modal);
        initComponents();
    }
    private void saveChart(String graphFile) {
       try {
           ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);
        } catch (IOException e) {
            e.getStackTrace();
            System.err.println("Problem occurred creating chart in " + graphFile);
        } 
        
    }
 
    private void setFname(String title) {
        int i = title.trim().indexOf(" ");
        if (i != -1)
           fnamePart =  title.trim().substring(0,i);
        else fnamePart = "chart";
    }
    public String getDateAndTime() {
     return(getDateAndTime("yyyyMMdd_HHmmss"));
    } 
    public String getDateAndTime(String frm) {
     Date now = new Date(System.currentTimeMillis());
     String formatStr = frm;
     SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
     return (formatter.format(now));   
    } 
    
    public JFreeChart createMemChart(TimeSeriesCollection dataset,
            String title, 
            String yaxis,
            String start,
            String last) {
        this.start = start;
        this.last = last;
        return(createMemChart(dataset,title,yaxis));
    }
    
    private JFreeChart createMemChart(TimeSeriesCollection dataset,String title, String yaxis) {    
        chart = ChartFactory.createXYAreaChart(
            title,
            "Time",
            yaxis,
            dataset,
            PlotOrientation.VERTICAL,
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
        setContentPane(cpanel);
//        jPanel1.add(cpanel);
//        jPanel1.setVisible(true);
        return chart;
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("uncheced")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1010, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(600, Short.MAX_VALUE))
        );

        jMenu1.setText("File");

        jMenuItem4.setText("Copy");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem2.setText("Save");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setText("Save in..");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem3.setText("Save as..");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        //File f = new File("." + "\\results");
        File f = new File(QWAGlobal.outFolder);
        String imageName = fnamePart + "-" + getDateAndTime() + ".jpg";
        String inFile = f.getAbsolutePath() + "\\" + fnamePart + "-" + getDateAndTime() + ".jpg";
        saveChart(inFile); 
        //saveChart(inFile + "\\" + fnamePart + "-" + getDateAndTime() + ".jpg");
        QWCreateHTML.createIMG("", imageName);
}//GEN-LAST:event_jMenuItem2ActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        //JFileChooser fc = new JFileChooser("c:");
        JFileChooser fc = new JFileChooser(QWAGlobal.outFolder);
        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        int ret = fc.showOpenDialog(jPanel1);
        String imageName = fnamePart + "-" + getDateAndTime() + ".jpg";
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String inFile = f.getAbsolutePath() +  "\\" + fnamePart + "-" + getDateAndTime() + ".jpg";
//            saveChart(inFile + "\\" + fnamePart + "-" + getDateAndTime() + ".jpg");
            saveChart(inFile);
            QWCreateHTML.createIMG("", imageName);
        }

}//GEN-LAST:event_jMenuItem1ActionPerformed

private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JFileChooser fc = new JFileChooser(QWAGlobal.outFolder);
        fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        int ret = fc.showOpenDialog(jPanel1);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String graphFile = fc.getName();
            saveChart(graphFile);
            String imageName = fnamePart + "-" + getDateAndTime() + ".jpg";
            QWCreateHTML.createIMG("", imageName);
        }  
}//GEN-LAST:event_jMenuItem3ActionPerformed
// Copy
private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
 Clipboard clipboard = getToolkit ().getSystemClipboard ();
// DataFlavor imagedContent = new imageSelection(chart);
// clipboard.setContents (fieldContent, Example1.this);
}//GEN-LAST:event_jMenuItem4ActionPerformed
 
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QWCharts dialog = new QWCharts(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
