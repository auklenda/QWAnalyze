/*
 * QWMemory.java
 *
 * Created on 16 January 2009, 10:34
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.awt.Font;
import java.util.*;
/**
 *
 * @author  AAuklend
 */
public class QWMemory extends javax.swing.JDialog {

  double memThreshold = 50.0;
  int memLines = 0;
    /** Creates new form QWMemory */
    public QWMemory(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public QWMemory(java.awt.Frame parent, String title,boolean modal) {
        super(parent,title, modal);
        initComponents();
    }
   public void reportMemory() {
       reportMemory(100.0);
   }
   /**
     *  MEMORY - display free memory below Threshold
     */
    public void reportMemory(double memLimit) {
      String str = null;
      double freeMem= 0;
      ArrayList al = new ArrayList();
      ArrayList holdRecord = new ArrayList();
      int count = 0;
      // First get the number of lines
      while ((str = QWUtil.readLine(QWAGlobal.mem)) != null) {
             // String tim = str.substring(17,26);
             // int recType = QWUtil.getRecordType(str);
             // if (recType ==  QWAGlobal.mem) {
             al = QWUtil.tokenize(str);
             int size = al.size();
             String name0 = (String)al.get(size-3);
             String name = name0.substring(0,name0.length()-1);
             try {
                 freeMem = Double.parseDouble(name);
             } catch (Exception exp) {
                 freeMem = 50.0;
             }
             if (memLimit > freeMem) {
                    // if (100.0 > freeMem) {
                    //updateTable(count++,(String)al.get(2),(String)al.get(5),name);
                holdRecord.add(str);
             }
           //   }
        }
        // Now create table and insert the records
        if (holdRecord.size() > 0 ) {
            createTable(holdRecord.size()+1,memLimit);
            for (int i = 0; i < holdRecord.size(); i++) {
                al = QWUtil.tokenize((String)holdRecord.get(i));
                int size = al.size();
                String name = (String)al.get(size-3);
                try {
                     freeMem = Double.parseDouble(name);
                } catch (Exception exp) {
                    freeMem = 50.0;
                }
                //updateTable(count++,(String)al.get(2),(String)al.get(5),name);
                updateTable(count++,(String)al.get(1),(String)al.get(2),(String)al.get(5),name);
            }
        }
        this.setVisible(true);
    }
    
    public void createTable(int size,Double memThreshold ){
       jTextField1.setFont(new Font("Verdana",Font.PLAIN,14));
       if (memThreshold == 0) {
          jTextField1.setText("  No memory information found ");
          return;
       }
       jTable2 =  new  javax.swing.JTable(size,4);
       javax.swing.table.TableColumn column = new javax.swing.table.TableColumn();
       jTable2.getColumnModel().getColumn(0).setHeaderValue("Date");
       jTable2.getColumnModel().getColumn(1).setHeaderValue("Time");
       jTable2.getColumnModel().getColumn(2).setHeaderValue("Mem Size(GB)");
       jTable2.getColumnModel().getColumn(3).setHeaderValue("Free Mem(%)");
       jTextField1.setText("  Sampled Free Heap Memory lower than " + memThreshold + " %");
       jScrollPane3.setViewportView(jTable2);
    }
    public void updateTable(int sz,String date,String tim,String totMem,String freeMem){
        jTable2.setValueAt(date,sz,0);
        jTable2.setValueAt(tim,sz,1);
        jTable2.setValueAt(totMem,sz,2);
        jTable2.setValueAt(freeMem,sz,3);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(new java.awt.Rectangle(150, 70, 0, 0));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Mem Size (GB)", "Free Mem (%)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable2);

        jTextField1.setText("jTextField1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QWMemory dialog = new QWMemory(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
