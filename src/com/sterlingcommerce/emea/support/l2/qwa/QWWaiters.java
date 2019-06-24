/*
 * QWWaiters.java
 *
 * Created on 19 January 2009, 09:28
 */

package com.sterlingcommerce.emea.support.l2.qwa;

import java.util.*;
import java.awt.Font;
import java.awt.Color;
import javax.swing.table.*;
import java.awt.Component;

/**
 *
 * @author  AAuklend
 */
public class QWWaiters extends javax.swing.JDialog {
    Object [][] cells;
    TableCellRenderer renderer = null;
    /** Creates new form QWWaiters */
    public QWWaiters(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public QWWaiters(java.awt.Frame parent, String title,boolean modal) {
        super(parent,title, modal);
        
    }
    public QWWaiters(java.awt.Frame parent, String title,boolean modal,int rows) {
        super(parent,title, modal);
        initComponents();
        cells = new Object[rows][5];
        
    }
    
    public void createTable(int rows) {
//       jTable1 =  new  javax.swing.JTable(rows,5);
       jTable1 =  new  javax.swing.JTable(new Tablemodel1(rows));
        renderer = new EvenOddRenderer();
        jTable1.setDefaultRenderer(Integer.class, renderer);
        jTable1.setDefaultRenderer(String.class, renderer);
/*        
   
       javax.swing.table.TableColumn column = new javax.swing.table.TableColumn();
       jTable1.getColumnModel().getColumn(0).setHeaderValue("TIME");
       jTable1.getColumnModel().getColumn(1).setHeaderValue("QUEUE");
       jTable1.getColumnModel().getColumn(2).setHeaderValue("THREADS");
       jTable1.getColumnModel().getColumn(3).setHeaderValue("ALLOWED");
       jTable1.getColumnModel().getColumn(4).setHeaderValue("DEPTH");
*/
 //       jTextField1.setText("  Sampled Free Heap Memory lower than " + memThreshold + " %");
       jTable1.setFont(new Font("Verdana",Font.PLAIN,14));
       jScrollPane1.setViewportView(jTable1);
    }
/*    
    public void updateTable(int row,String tim,int que,int threads,int allowed,int depth) {
      jTable1.setValueAt("   " + tim,row,0);           // Time 
      jTable1.setValueAt("     " + que,row,1);           // Queue
      jTable1.setValueAt("     " + threads,row,2);       // Threads
      jTable1.setValueAt("     " + allowed,row,3);       // Calculated
      jTable1.setValueAt("     " + depth,row,4);         // Deth
       renderer.getTableCellRendererComponent(jTable1, String.class,false, false, row,2);
      if (threads < allowed) {
          renderer.getTableCellRendererComponent(jTable1, String.class,false, false, row,2);
      }
    }
 */ 

    public void updateTable(int row,String tim,int que,int threads,int allowed,int depth) { 
        cells[row][0] = tim;
        cells[row][1] = que;
        cells[row][2] = threads;
        cells[row][3] = allowed;
        cells[row][4] = depth;
    }
    
class Tablemodel1 extends AbstractTableModel  {
    int rows;
    public Tablemodel1(int row) {
        rows = row;
    }
    public int getRowCount() {
        return rows;
    }
    public int getColumnCount() {
        return 5;
    }
    public Object getValueAt( int rad, int kolonne ) {
        
      return cells[rad][kolonne];  
    }
        @Override
    public boolean isCellEditable(int row, int column){
       return false;
    }   
    //eventuelt også
        @Override
    public String getColumnName( int kolonne ) {
        switch(kolonne) {
            case 0:
                    return "TIME";
            case 1:
                    return "QUEUE";
            case 2:
                    return "THREADS";
            case 3:
                    return "ALLOWED";
            case 4:
                    return "QDEPTH";
        }
        return null;
    }
    public Class getColumnClass( int kolonne ) {
        if (kolonne == 0)
            return String.class;
        else return Integer.class;
    }
  }   
  public class EvenOddRenderer extends DefaultTableCellRenderer {
     @Override
   
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int col) {
        Component renderer = super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, col);
/*        
        if (row % 2 == 0)
            renderer.setBackground(Color.LIGHT_GRAY);
        else
            renderer.setBackground(Color.WHITE);
            //renderer.setBackground(getBackground());
 */       
      if (col == 3){
          Integer thread = (Integer)table.getValueAt(row, 2);
          Integer all = (Integer)table.getValueAt(row, 3);
          if (thread < all) 
              renderer.setBackground(Color.pink);
          else 
              renderer.setBackground(Color.WHITE);
      } else
          renderer.setBackground(Color.WHITE);
      return renderer;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(new java.awt.Rectangle(50, 30, 0, 0));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QWWaiters dialog = new QWWaiters(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
