/*
 * QWTCharts.java
 *
 * Created on 23 January 2009, 13:23
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
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;
import org.jfree.chart.axis.PeriodAxis;

import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import javax.swing.JFileChooser;

/**
 *
 * @author  AAuklend
 */
public class QWTCharts extends javax.swing.JDialog {
    String start = null;
    String last = null;
    String fnamePart = null;
    String graphFile = null;
    String title = null;
    JFreeChart chart;
    int xx = 0;
    int yy = 0;
    /** Creates new form QWTCharts */
    public QWTCharts(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public QWTCharts(java.awt.Frame parent,String title, boolean modal) {
        super(parent, title, modal);
        initComponents();
    }
    public void adjustPlot(int x , int y ) {
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, x, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(437, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, y, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(288, Short.MAX_VALUE))
        );
   }
    private void saveChart(String graphFile) {
       try {
           ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);        
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
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
 public JFreeChart createThreadChart(TimeSeriesCollection dataset,
            String title,
            String yaxis,
            String start,
            String last,
            javax.swing.JPanel jp) {
        this.start = start;
        this.last = last;
        ChartPanel ch = null;
        return(createThreadChart(dataset,title,yaxis,jp));
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
    public JFreeChart createThreadChart(TimeSeriesCollection dataset,
            String title, 
            String yaxis,
            String start,
            String last) {
        this.start = start;
        this.last = last;
        return(createThreadChart(dataset,title,yaxis,null));
    }
    /**
     * 
     * @param dataset
     * @param title
     * @param yaxis
     * @return
     */
   public JFreeChart createThreadChart(TimeSeriesCollection dataset,String title,String yaxis,javax.swing.JPanel jpan) {
        this.title = title; 
        chart = ChartFactory.createTimeSeriesChart(
            title,               // chart title
            "Time",               // domain axis label
            yaxis,                  // range axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        ); 
        setFname(title);  
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(Color.white);
        Rectangle rt = jPanel1.getBounds();
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
        if (jpan != null)
            jpan.add(cpanel);
        else
           setContentPane(cpanel);
        return chart;
    }
    private String getStart(){
        String st = this.start;
        int inx = st.indexOf(" ");
        String st1 = st.substring(0, inx);
        ArrayList al = QWUtil.tokenize(st1,"-");
        StringBuilder sb = new StringBuilder(); 
        sb.append((String)al.get(0));
        sb.append((String)al.get(1));
        sb.append((String)al.get(2));
        sb.append("_");
        al = QWUtil.tokenize(st.substring(inx+1),":");
        sb.append((String)al.get(0));
        sb.append((String)al.get(1));
        sb.append((String)al.get(2));
        return sb.toString();
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
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
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
                .addContainerGap(1035, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(565, Short.MAX_VALUE))
        );

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("Save");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Save in..");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jButton1))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        File f = new File("." + "\\");
        String inFile = f.getAbsolutePath();
        saveChart(inFile + "\\" + title + "-" + getDateAndTime() + ".jpg");
}//GEN-LAST:event_jButton1ActionPerformed
// Save In....
private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JFileChooser fc = new JFileChooser(QWAGlobal.outFolder);
        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        int ret = fc.showOpenDialog(jPanel1);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String inFile = f.getAbsolutePath();
            String st = getStart();
            saveChart(inFile + "\\" + title + "-" + st + ".jpg");
            //saveChart(inFile + "\\" + title + "-" + getDateAndTime() + ".jpg");
            String imageName = fnamePart + "-" + st + ".jpg";
            //String imageName = fnamePart + "-" + getDateAndTime() + ".jpg";
            QWCreateHTML.createIMG("", imageName);
        }

}//GEN-LAST:event_jMenuItem2ActionPerformed
// Save as...
private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JFileChooser fc = new JFileChooser("c:");
        fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        int ret = fc.showOpenDialog(jPanel1);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            graphFile = fc.getName();
            saveChart(graphFile);
            String imageName = title + "-"+ getDateAndTime() + ".jpg";
            QWCreateHTML.createIMG("", imageName);
              
        }    

}//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QWTCharts dialog = new QWTCharts(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
