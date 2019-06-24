/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;

import java.awt.Component;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Alf
 */
public abstract class QWAJtabModel  {
 javax.swing.JTable jt = null;
 Object dataObject [][] = null;
 private JCheckBox cellCheck;
 
 public void outputSelection() {
        jt.getSelectionModel().getLeadSelectionIndex();
        jt.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        int[] r = jt.getSelectedRows();
        int[] c = jt.getSelectedColumns();
        Object name = jt.getValueAt(r[0], 0);
        Object queue = jt.getValueAt(r[0], 1);
        Object count = jt.getValueAt(r[0], 2);
        int cnt = Integer.parseInt(count.toString());
    }    

    public class RowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            System.out.println("ROW SELECTION");
            //outputSelection();
        }
    }

    public class ColumnListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            System.out.println("COLUMN SELECTION");
            //outputSelection();
        }
    }

    public class TableListener1 implements TableModelListener {

        public TableListener1(javax.swing.JTable jt) {
            jt.getModel().addTableModelListener(this);
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

    class Tablemodel1 extends AbstractTableModel {

        int rows,cols;
        int [][] dataObject = null;
        String [] columnNames = null;
        String [] rowNames = null;

        public Tablemodel1(int row) {
            rows = row;
        }
        public Tablemodel1(int row, int col){
            rows =row;
            cols = col;
        }
        public Tablemodel1(int row, int col,int [][] dobj,String [] cn, String [] rn){
            rows =row;
            cols=col;
            dataObject = dobj;
            columnNames =cn;
            rowNames = rn;
        }

        @Override
        public int getRowCount() {
            return rows;
        }

        @Override
        public int getColumnCount() {
            return cols;
        }

        public Object getValueAt(int rad, int kolonne) {
          try { 
            if (kolonne == 0) {
                return rowNames[rad];
            }         
            if (kolonne == 5) {
                return false;
                //fix dette
            }         
            return dataObject[rad][kolonne-1];
          } catch(Exception  ex) {
              System.out.println(ex); 
          }  
          return null;
        }
        //eventuelt også

        @Override
        public String getColumnName(int kolonne) {
          return columnNames[kolonne];
        }

        @Override
        public Class getColumnClass(int kolonne) {
           // System.out.println("Col" + kolonne);
            if (kolonne == 0) {
                return String.class;
            } else if (kolonne == 5) {
                return Boolean.class;
                
                
                
            } else {
                return Integer.class;
            }
        }
/*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            if (col < 2) {
                return false;
            } else {
                return true;
            }
        }        
    }
    class Tablemodel2 extends AbstractTableModel {

        int rows,cols;
        String [][] dataObject = null;

        public Tablemodel2(int row) {
            rows = row;
        }
        public Tablemodel2(int row, int col){
            rows =row;
            cols = col;
        }
        public Tablemodel2(int row, int col,String [][] dobj){
            rows =row;
            cols = col;
            dataObject = dobj;
        }

        @Override
        public int getRowCount() {
            return rows;
        }

        @Override
        public int getColumnCount() {
            return cols;
        }

        public Object getValueAt(int rad, int kolonne) {
            String chr = dataObject[rad][kolonne];
            if (chr.equals("O"))
                return " ";
            return chr;
        }
        //eventuelt også

        @Override
        public String getColumnName(int kolonne) {
          if (kolonne < 9){
              return "Queue" + String.valueOf(kolonne+1);
          }
          if (kolonne == 9)  return "Calc";
          if (kolonne == 10) return "Spotted";
          if (kolonne == 11) return "Spotted %";
          return " ";
        }

        @Override
        public Class getColumnClass(int kolonne) {
           
            // System.out.println("Col" + kolonne);
          return String.class;
        }
    }
class ColumnSorter implements Comparator {
  int colIndex;

  ColumnSorter(int colIndex) {
    this.colIndex = colIndex;
  }

  public int compare(Object a, Object b) {
    Vector v1 = (Vector) a;
    Vector v2 = (Vector) b;
    Object o1 = v1.get(colIndex);
    Object o2 = v2.get(colIndex);

    if (o1 instanceof String && ((String) o1).length() == 0) {
      o1 = null;
    }
    if (o2 instanceof String && ((String) o2).length() == 0) {
      o2 = null;
    }

    if (o1 == null && o2 == null) {
      return 0;
    } else if (o1 == null) {
      return 1;
    } else if (o2 == null) {
      return -1;
    } else if (o1 instanceof Comparable) {

      return ((Comparable) o1).compareTo(o2);
    } else {

      return o1.toString().compareTo(o2.toString());
    }
  }
}
    
    public class EvenOddRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            /*
            if (row % 2 == 0)
            renderer.setBackground(Color.LIGHT_GRAY);
            else
            renderer.setBackground(Color.WHITE);
            //renderer.setBackground(getBackground());

            if (col == 3){
            Integer thread = (Integer)table.getValueAt(row, 2);
            Integer all = (Integer)table.getValueAt(row, 3);
            if (thread < all)
            renderer.setBackground(Color.pink);
            else
            renderer.setBackground(Color.WHITE);
            } else
            renderer.setBackground(Color.WHITE);
             */
            return renderer;
        }
    }
    
}
