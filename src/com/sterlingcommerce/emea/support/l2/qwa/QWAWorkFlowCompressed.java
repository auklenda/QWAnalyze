/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import javax.swing.table.TableColumn;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.Color;
import java.awt.Toolkit; 
import java.awt.datatransfer.Clipboard; 
import java.awt.datatransfer.DataFlavor; 
import java.awt.datatransfer.StringSelection; 
import java.awt.event.KeyAdapter; 
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane; 
/**
 *
 * @author Alf
 */
public class QWAWorkFlowCompressed {
 private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard(); 
 javax.swing.JTable jTable2;
 TableCellRenderer renderer = null;
 Object [][] jtable;
 int theSize = 0;
 String hdr;
 int [][] qmon = new int[9][7] ;  // 0=q,1=min,2=used,3=calc,4=pool,5=max,6=dept
 long [] bpAttributes = null;   // count, min,max,min,max,accstep,acctime,lastsep,lastwait
 Object jframe = null;
    
    public QWAWorkFlowCompressed(String header) {
        hdr = header;
    }
    public QWAWorkFlowCompressed(Object jframe, String header) {
        this.jframe = jframe;
        hdr = header;
    }    
    /**
     *  0=q,1=min,2=used,3=calc,4=pool,5=max,6=dept
     * @param threshhold - list if higher than this value
     * @param index      - 0 = queue, 2 = active threads , 6 = waiting for threads , 7 = BPname
     * @param name       - BPName
     * called from : QWAnaGUI
     */
public void listWFDs(int threshold,int action) {
      String inpr;
      String freeMem = " ";
      ArrayList alString = new ArrayList();
      Qhelper qh = null;
      //Properties wfidProps = new Properties();
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {     // Dispatch Queue Info
                int qEltsOffset = inpr.indexOf("QUEU");
                String qElts = inpr.substring(qEltsOffset);
                ArrayList qElements = QWUtil.tokenize(qElts);
                populateQueues(qElements);
                // "QUEUE Qnr Min Used Calc Pool Max Depth
                int tot =0;
                for (int i = 0; i < 9; i++) {
                     tot +=  qmon[i][action];
                }  
                if (tot >= threshold) {
                   String tim = inpr.substring(17,26);
                   String total = String.valueOf(tot);
                   for (int ii = 0; ii < 9; ii++) {
                       if (qmon[ii][action] > 0) {
                           qh = new Qhelper();
                           qh.setTime(tim); // Time only
                           tim = " ";
                           qh.setQ(String.valueOf(qmon[ii][0]));
                           qh.setMin(String.valueOf(qmon[ii][1]));
                           qh.setThread(String.valueOf(qmon[ii][2]));
                           qh.setCalc(String.valueOf(qmon[ii][3]));
                           qh.setPool(String.valueOf(qmon[ii][4]));
                           qh.setMax(String.valueOf(qmon[ii][5]));
                           qh.setWaiters(String.valueOf(qmon[ii][6]));
                           qh.setMem(freeMem);
                           qh.setTot(total);
                           total = " ";
                           freeMem = " ";
                           alString.add(qh);
                       }
                 }
              }   
           } else if (recType ==  QWAGlobal.mem) {
                     int ix = inpr.indexOf("(%)",75);
                     int ix1 = inpr.indexOf(" ",ix+6);   // Get end of value
                     //freeMem = inpr.substring(ix+4,ix1);
           }
        } // end while
        createTable(alString.size());
        Iterator it = alString.iterator();
        int row = 0;
        while (it.hasNext()) {
            qh = (Qhelper)it.next();
            updateTable(row++,
                    qh.getTime(),
                    qh.getQ(),
                    qh.getMin(),
                    qh.getThread(),
                    qh.getCalc(),
                    qh.getPool(),
                    qh.getMax(),
                    qh.getWaitersw(),
                    qh.getMem(),
                    qh.getTot());
        }
    }
   public void updateTable(int row,
                            String tim,
                            String q,
                            String min,
                            String t,
                            String c,
                            String p,
                            String mx,
                            String d,
                            String mem,
                            String tot){
        //jTable2.setValueAt(tim,row,0);
        if (t != null) {
            try {
                if (Integer.parseInt(d) > 0 && Integer.parseInt(c) > Integer.parseInt(t))
                    renderer.getTableCellRendererComponent(jTable2 , null,false, false, row,2);
            } catch (Exception exp) {
            }
        }
        setValueAt(tim,row,0);
        //jTable2.setValueAt(q,row,1);
        setValueAt(q,row,1);
        setValueAt(min,row,2);
        setValueAt(t,row,3);
        setValueAt(c,row,4);
        setValueAt(p,row,5);
        setValueAt(mx,row,6);
        setValueAt(d,row,7);
        setValueAt(mem,row,8);
        setValueAt(tot,row,9);
/*
        if (t != null) {
            try {
                if (Integer.parseInt(d) > 0 && Integer.parseInt(c) > Integer.parseInt(t))
                    renderer.getTableCellRendererComponent(jTable2 , null,false, false, row,2);
            } catch (Exception exp) {
            }
        }
*/
    }   
    private void setValueAt(String arg,int record,int col) {
       if (jtable != null) {
           jtable[record][col] = arg;
        } else
           jTable2.setValueAt(arg,record,col);
    }
    private void setValueAt(int arg,int record,int col) {
       if (jtable != null) {
           jtable[record][col] = arg;
        } else
           jTable2.setValueAt(arg,record,col);
    }   
    private class RowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
          //  outputSelection();
        }
    }


    private void populateQueues(ArrayList inpAL) {
     int offset = 1;
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 7; j++) {
             try {
                 sb.append((String) inpAL.get(offset + j)).append(" ");
                 qmon[i][j] = Integer.parseInt((String)inpAL.get(offset + j));
             } catch (Exception exp) {
                 System.out.println("Faild in creating qmon:" );
             }
         }
         sb.append(" ");
         offset +=8;
    }};
/**
  * 
  * @param size 
  */
    public void createTable(int size ){
       //jTextField1.setFont(new Font("Verdana",Font.PLAIN,14));
       TableColumn column = null;
       int wth = QWAnaGUI.jScrollPane6.getWidth() - 10;
       int small = 70;
       //jtable = null;
       jtable = new Object[size + 1][10];
       jTable2 = new javax.swing.JTable(new Tablemodel1(size));
       jTable2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       jTable2.setColumnSelectionAllowed(true);
       jTable2.setRowSelectionAllowed(true);       
       renderer = new EvenOddRenderer();
       jTable2.setDefaultRenderer(String.class, renderer);
       jTable2.getColumnModel().getSelectionModel().
                 addListSelectionListener(new ColumnListener());
       jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
       jTable2.setAutoCreateRowSorter(true);
       QWAnaGUI.jScrollPane6.setViewportView(jTable2);
       jTable2.getColumnModel().getColumn(0).setPreferredWidth(small);
       column = jTable2.getColumnModel().getColumn(0);
       jTable2.getColumnModel().getColumn(1).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(2).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(3).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(4).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(5).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(6).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(7).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(8).setPreferredWidth(small);
       jTable2.getColumnModel().getColumn(9).setPreferredWidth(wth - 9*small);
       jTable2.addKeyListener(new ClipboardKeyAdapter(jTable2) );
       QWAnaGUI.jTextField4.setText(hdr);
       QWAnaGUI.jScrollPane6.setViewportView(jTable2);
       QWAnaGUI.jTabbedPane1.setSelectedIndex(QWAnaGUI.jTabbedPane1.getTabCount()-1);
       
    }  
    
    public class TableListener1 implements TableModelListener {
        public TableListener1() {
          jTable2.getModel().addTableModelListener(this);
        }
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel) e.getSource();
            String columnName = model.getColumnName(column);
            Object data = model.getValueAt(row, column);
            String dt = (String) data;
// Do something with the data...
        }
    } 
    private class ColumnListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            //outputSelection();
        }
    }    
    public class Tablemodel1 extends AbstractTableModel {
        int rows;
        public Tablemodel1(int row) {
            rows = row;
        }
        @Override
        public int getRowCount() {
            return rows;
        }
        @Override
        public int getColumnCount() {
            return 10;
        }

        @Override
        public Object getValueAt(int rad, int kolonne) {
            if (rad > jtable.length)
                return jtable[jtable.length][kolonne];
            try {
                return jtable[rad][kolonne];
            } catch (Exception e) {
                System.out.println("exp");
            }
            return jtable[rad][kolonne];
        }

        //eventuelt også
        public String getColumnName(int kolonne) {
            switch (kolonne) {
                case 0:
                    return "Time";
                case 1:
                    return "Queue";
                case 2:
                    return "Min";
                case 3:
                    return "Threads";
                case 4:
                    return "Calc";
                case 5:
                    return "Pool";
                case 6:
                    return "Max";
                case 7:
                    return "Depth";
                case 8:
                    return "Free Mem%";
                case 9:
                    return "Total Threads";
            }
            return null;
        }
    }
    public Class getColumnClass(int kolonne) {
      return String.class;
     /*
     if (kolonne == 0) {
         return String.class;
     } else {
         return Integer.class;
     }
 */
   }
   public class EvenOddRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            int thread = Integer.parseInt((String)table.getValueAt(row, 2));
            int calc = Integer.parseInt((String)table.getValueAt(row, 3));
                //Integer thread = (Integer)table.getValueAt(row, 2);
                //Integer calc = (Integer)table.getValueAt(row, 3);
            if (thread < calc)
                renderer.setBackground(Color.pink);
            else
                renderer.setBackground(Color.WHITE);
            return renderer;
       }
    }
/** 
 * KeyAdapter to detect Windows standard cut, copy and paste keystrokes on a JTable and put them to the clipboard 
 * in Excel friendly plain text format. Assumes that null represents an empty column for cut operations. 
 * Replaces line breaks and tabs in copied cells to spaces in the clipboard. 
 * 
 * @see java.awt.event.KeyAdapter 
 * @see javax.swing.JTable 
 */ 
public class ClipboardKeyAdapter extends KeyAdapter {

        private static final String LINE_BREAK = "\n"; 
        private static final String CELL_BREAK = "\t"; 
     
        
        private final javax.swing.JTable table; 
        
        public ClipboardKeyAdapter(javax.swing.JTable table) { 
                this.table = table; 
        } 
        
        @Override 
        public void keyReleased(KeyEvent event) { 
                if (event.isControlDown()) { 
                        if (event.getKeyCode()==KeyEvent.VK_C) { // Copy                        
                                cancelEditing(); 
                                copyToClipboard(false); 
                        } else if (event.getKeyCode()==KeyEvent.VK_X) { // Cut 
                                cancelEditing(); 
                                copyToClipboard(true); 
                        } else if (event.getKeyCode()==KeyEvent.VK_V) { // Paste 
                                cancelEditing(); 
                                pasteFromClipboard();           
                        } 
                } 
        } 
        
        private void copyToClipboard(boolean isCut) { 
                int numCols=table.getSelectedColumnCount(); 
                int numRows=table.getSelectedRowCount(); 
                int[] rowsSelected=table.getSelectedRows(); 
                int[] colsSelected=table.getSelectedColumns(); 
                if (numRows!=rowsSelected[rowsSelected.length-1]-rowsSelected[0]+1 || numRows!=rowsSelected.length || 
                                numCols!=colsSelected[colsSelected.length-1]-colsSelected[0]+1 || numCols!=colsSelected.length) {

                        JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
                        return; 
                } 
                
                StringBuffer excelStr=new StringBuffer(); 
                for (int i=0; i<numRows; i++) { 
                        for (int j=0; j<numCols; j++) { 
                                excelStr.append(escape(table.getValueAt(rowsSelected[i], colsSelected[j]))); 
                                if (isCut) { 
                                        table.setValueAt(null, rowsSelected[i], colsSelected[j]); 
                                } 
                                if (j<numCols-1) { 
                                        excelStr.append(CELL_BREAK); 
                                } 
                        } 
                        excelStr.append(LINE_BREAK); 
                } 
                
                StringSelection sel  = new StringSelection(excelStr.toString()); 
                CLIPBOARD.setContents(sel, sel); 
        } 
        
        private void pasteFromClipboard() { 
                int startRow=table.getSelectedRows()[0]; 
                int startCol=table.getSelectedColumns()[0];

                String pasteString = ""; 
                try { 
                        pasteString = (String)(CLIPBOARD.getContents(this).getTransferData(DataFlavor.stringFlavor)); 
                } catch (Exception e) { 
                        JOptionPane.showMessageDialog(null, "Invalid Paste Type", "Invalid Paste Type", JOptionPane.ERROR_MESSAGE);
                        return; 
                } 
                
                String[] lines = pasteString.split(LINE_BREAK); 
                for (int i=0 ; i<lines.length; i++) { 
                        String[] cells = lines[i].split(CELL_BREAK); 
                        for (int j=0 ; j<cells.length; j++) { 
                                if (table.getRowCount()>startRow+i && table.getColumnCount()>startCol+j) { 
                                        table.setValueAt(cells[j], startRow+i, startCol+j); 
                                } 
                        } 
                } 
        } 
        
        private void cancelEditing() { 
                if (table.getCellEditor() != null) { 
                        table.getCellEditor().cancelCellEditing(); 
            } 
        } 
        
        private String escape(Object cell) { 
                return cell.toString().replace(LINE_BREAK, " ").replace(CELL_BREAK, " "); 
        } 
}    
 private class Qhelper {
    String tm;
    String q = null;
    String min = null;
    String thread = null;
    String calc = null;
    String pool = null;
    String max = null;
    String waiters = null;
    String mem = null;
    String tot = null;
    public Qhelper() {
    }
    public void setTime(String tm){ 
        this.tm=tm;}
    public String getTime(){
        return tm;}
    public void setQ(String qu) {
        this.q= qu;}
    public String getQ() {
        return q;}
    public void setMin(String t) { 
        this.min = t; }
    public String getMin() { 
        return min; }
    public void setThread(String t) { 
        this.thread = t; }
    public String getThread() { 
        return thread; }
    public void setCalc(String v) { 
        this.calc = v; }
    public String getCalc() { 
        return calc; }
    public void setPool(String t) { 
        this.pool = t; }
    public String getPool() { 
        return pool; }
    public void setMax(String t) { 
        this.max = t; }
    public String getMax() { 
        return max; }
    public void setWaiters(String v) { 
        this.waiters = v; }
    public String getWaitersw() { 
        return waiters; }
    public void setMem(String v) {
        this.mem=v;}
    public String getMem(){
        return mem;}
    public void setTot(String v) {
        this.tot=v;}
    public String getTot(){
        return tot;}    
   }  // QHelper
}
