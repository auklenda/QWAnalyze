/*
 * WQWfc.java
 *
 * Created on 25 February 2009, 13:52
 */
package com.sterlingcommerce.emea.support.l2.qwa;
import java.awt.Font;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumn;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Color;

/**
 *
 * @author  AAuklend
 */
public class QWWfc extends javax.swing.JDialog {
 javax.swing.JTable jTable2;
javax.swing.JScrollPane js1;
 TableCellRenderer renderer = null;
 Object [][] jtable;
 java.awt.Frame aParent;
 String hdr;
 Properties bpNames = null;
 Properties stepProps = null;
 Properties timeProps = null;
 int stepCount = 0;
 int timeCount = 0;
 int [][] qmon = new int[9][7] ;  // 0=q,1=min,2=used,3=calc,4=pool,5=max,6=dept
 long [] bpAttributes = null;   // count, min,max,min,max,accstep,acctime,lastsep,lastwait
 
    /** Creates new form WQWfc */
    public QWWfc(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        aParent = parent;
        initComponents();
    }
    public QWWfc(java.awt.Frame parent, String title,boolean modal) {
        super(parent,title, modal);
        initComponents();
    }
    public QWWfc(java.awt.Frame parent, String title,boolean modal,String header) {
        super(parent,title, modal);
        initComponents();
        hdr = header;
    }

    private QWWfc(QWWfc aThis, String string, boolean b, String header) {
        super(aThis,header, b);
        initComponents();
        hdr = header;
    }
    public QWWfc(java.awt.Frame parent, String title, boolean modal, String header, javax.swing.JScrollPane js ) {
        super(parent,title, modal);
        initComponents();
        hdr = header;
        js1=js;
    }

    public void listReQueued() {
        listReQueued(-1);
    }
    /**
     * We are looking for possible requeued BP's
     */
    public void listReQueued(int q) {
      String inpr;
      ArrayList al1 = new ArrayList();
      Properties p = new Properties();
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {

            } else if (recType ==  QWAGlobal.wfc) {
                ArrayList tokens = QWUtil.tokenize(inpr);
                if (QWUtil.isitQueue((String)tokens.get(5)) == -1)
                    continue;
                if ("WF_ID".equalsIgnoreCase((String)tokens.get(8))){
                    if (q != -1) {
                        if (q != QWUtil.isitDigit((String)tokens.get(5))) 
                           continue; 
                    }
                    String wf = (String)tokens.get(9);
                    if (al1.contains(wf)) {
                        if (!p.containsKey((String)wf))
                           p.put(wf,new ArrayList());
 //                       System.out.println(inpr);
       //              al2.add(wf);
                    } else {
                        al1.add(wf);
                    }
                }
            } else if (recType ==  QWAGlobal.mem) {
            } else if (recType ==  QWAGlobal.jdbc) {
            }
      } // end while
      al1=null;
      QWUtil.resetInput();
      ArrayList total = new ArrayList();
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.wfc) {
                ArrayList tokens = QWUtil.tokenize(inpr);
                if (QWUtil.isitQueue((String)tokens.get(5)) == -1)
                    continue;
                if ("WF_ID".equalsIgnoreCase((String)tokens.get(8))){
                    String wf = (String)tokens.get(9);
                    if (p.containsKey((String)wf)) {
                        ArrayList list =(ArrayList)p.get(wf);
                        list.add(inpr);
                        p.put(wf,list);
                    }
                }
          }
      }
      Enumeration en = p.propertyNames();
      while (en.hasMoreElements()) {
          String wf = (String)en.nextElement();
          ArrayList al = (ArrayList)p.get(wf);
          Iterator it = al.iterator();
          while (it.hasNext()) {
              total.add(it.next());
          }
      }
      createTable(total.size());
      Iterator it2 = total.iterator();
      int row = 0;
      while (it2.hasNext()) {
          ArrayList tokens = QWUtil.tokenize((String)it2.next());
          updateTable(row,
                      (String)tokens.get(2),   // time
                      (String)tokens.get(5),   // queue
                      "",              // threads
                      "",              // calculated
                      "",              // depth
                      (String)tokens.get(9),   // WF-ID
                      (String)tokens.get(11),   //  step-id
                      (String)tokens.get(19),   // active ms
                      "",              // memory level
                      (String)tokens.get(21)); // BP name
          row += 1;

        //  System.out.println((String)it2.next());
      }
       this.setVisible(true);
    }

public void listWFDs(int threshold,int qIndex) {
    listWFDs(threshold , qIndex, null);
}
    /**
     *
     * @param threshhold - list if higher than this value
     * @param index      - 0 = queue, 2 = active threads , 6 = waiting for threads , 7 = BPname
     * @param name       - BPName
     * called from : QWAnaGUI
     */
public void listWFDs(int threshold,int qIndex, String name) {
      String inpr;
      String freeMem = " na ";
      ArrayList alhelper = new ArrayList();
      ArrayList alString = null;      // TRY TO KEEP THIS GLOBAL IN THE CLASS
      boolean candidate = false;
      Properties wfidProps = new Properties();
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {     // Dispatch Queue Info
                if (alString != null){
                    createWFDs(alString,alhelper,freeMem);
                    alString = null;
                }
                candidate=false;
                int qEltsOffset = inpr.indexOf("QUEU");
                String qElts = inpr.substring(qEltsOffset);
                ArrayList qElements = QWUtil.tokenize(qElts);
                populateQueues(qElements);
                // "QUEUE Qnr Min Used Calc Pool Max Depth

                if (qIndex == 0) {
                    if (qmon[threshold-1][2] > 0) 
                        candidate = true;
                }
                if (qIndex == 2 || qIndex == 6) {
                   for (int i = 0; i < 9; i++) {
                       if (qmon[i][qIndex] >= threshold)
                           candidate=true;
                   } // end for
                }
            } else if (recType ==  QWAGlobal.wfc) {  // WFC info of active BP's
                if (candidate) {                     // Candidate found in the Queue Info ?
                    if ( alString == null)
                        alString = new ArrayList();
                    ArrayList wfcList = QWUtil.tokenize(inpr);
                    if (qIndex == 0) {
                        int q = QWUtil.isitDigit((String) wfcList.get(5));
                        if (q != -1 && q == threshold)
                            alString.add(inpr);
                    } else {
                        alString.add(inpr);
                    }
                } else if (qIndex == 7 || qIndex == 8 || qIndex == 9 || qIndex == 10) {
                     if ( alString == null) alString = new ArrayList();
                     ArrayList wfcList = QWUtil.tokenize(inpr);
                     if (qIndex == 7) {
                         String bpn = (String)wfcList.get(21);
                         if (bpn.equalsIgnoreCase(name))
                            alString.add(inpr);
                    } else if (qIndex == 10) {
                         String wfid = (String)wfcList.get(9);
                         if (wfid.equalsIgnoreCase(name))
                            alString.add(inpr);
                     } else {
                         int act = QWUtil.isitDigit((String)wfcList.get(19));
                         if (act != -1 && act >= threshold  ) {
                             alString.add(inpr);
                         }
                    }
                }           // end candidate
            //}
            } else if (recType ==  QWAGlobal.mem) {
                     int ix = inpr.indexOf("(%)",75);
                     int ix1 = inpr.indexOf(" ",ix+6);   // Get end of value
                     freeMem = inpr.substring(ix+4,ix1);
            } else if (recType ==  QWAGlobal.jdbc) {
            }
        } // end while
       // Update display
       createTable(alhelper.size());
       Properties prop = new Properties();
       int rec  = 0;
       String pos = null;
       boolean updatePartial = false;
       Iterator it =  alhelper.iterator();
       while(it.hasNext()) {
            Qhelper qh = (Qhelper)it.next();
            if (qIndex == 9) {
               pos = prop.getProperty(qh.getWfid());
               if (pos == null) {
                   prop.setProperty(qh.getWfid(),String.valueOf(rec));
                   updatePartial = false;
               } else {
                   updatePartial = true;
               }
            }
            if (!updatePartial ) {
               updateTable(rec, qh.getTime(),qh.getQ(),qh.getThread(),qh.getCalc(),qh.getWaitersw(),
                          qh.getWfid(),qh.getStep(),qh.getAct(),qh.getMem(),qh.getBPName());
               rec++;
            } else {
               updateTable(Integer.parseInt(pos),qh.getStep(),qh.getAct(),qh.getWfid());
               updatePartial= false;
            }
       }
       displayCounters();
       this.setVisible(true);
    }

private void createWFDs(ArrayList alStrings,ArrayList alhelper, String mem) {
     String nextTime = " ";
     int prevQueue = 0;
     for (int i = 0; i < alStrings.size(); i++) {
         String inpr = (String)alStrings.get(i);
         Qhelper qh = new Qhelper();
         if (i ==0 ) qh.setMem(mem);
         int inx = 2;
         String tim = inpr.substring(17,26);
         qh.setTime("");
         ArrayList al = QWUtil.tokenize(inpr);
         if (!tim.equalsIgnoreCase(nextTime))
            qh.setTime((String)al.get(inx));
         String q = (String)al.get(inx+3);
         if (q.length() > 1) continue;               // Queue 1 - 9
         qh.setQ((String)al.get(inx+3));
         qh.setWfid((String)al.get(inx+7));
         qh.setStep((String)al.get(inx+9));
         String act = (String)al.get(inx+15);
         if (act.startsWith("-1") || act.startsWith("9") ) {
             qh.setAct((String)al.get(inx+17));
             qh.setBPname((String)al.get(inx+19));
         } else {
             qh.setAct((String)al.get(inx+15));
             qh.setBPname((String)al.get(inx+17));
         }
         String qinxs = qh.getQ();
         int qinx = 0;
         if (!expdORdline(qinxs)) {
            qinx = Integer.parseInt(qinxs);
            if (qinx < 10 && qinx > 0 ) {
               int trds = qmon[qinx-1][1];
               if ( trds != 0 && prevQueue != qinx) {
                  qh.setThread(String.valueOf(qmon[qinx-1][2]));
                  qh.setCalc(String.valueOf(qmon[qinx-1][3]));
                  qh.setWaiters(String.valueOf(qmon[qinx-1][6]));
                  prevQueue = qinx;
               } else {
               }
            }
            nextTime = tim;
            alhelper.add(qh);
         }
      } // end for
    //displayCounters();
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

    private boolean expdORdline(String str) {
        if (str.startsWith("-") || str.startsWith("0") || str.startsWith("f"))
           return true;
        return false;
    }
/**
 *  Display handling
 */
  public void displayCounters() {
     for (Enumeration en = timeProps.keys();en.hasMoreElements();){
           String name = (String)en.nextElement();
           String a = timeProps.getProperty(name);
           String s = stepProps.getProperty(name);
           setAttribute(s, a);
     } 
     if (bpAttributes[1]==99999)
         bpAttributes[1]=0;     
      jLabel14.setText(String.valueOf(bpAttributes[0]));  
      jLabel7.setText(String.valueOf(bpAttributes[2]));
      jLabel8.setText(String.valueOf(bpAttributes[1]));
      jLabel9.setText(String.valueOf(bpAttributes[4]));
      jLabel10.setText(String.valueOf(bpAttributes[3]));
      long avg = 0l;
      if (stepCount > 0)
         avg = bpAttributes[5]/stepCount;
      jLabel11.setText(String.valueOf(avg));
      if (timeCount >0l)
         avg = bpAttributes[6]/timeCount;
      jLabel12.setText(String.valueOf(avg));
    }
    public void createTable(int size ){
       //jTextField1.setFont(new Font("Verdana",Font.PLAIN,14));
       TableColumn column = null; 
       int wth = jScrollPane1.getWidth() - 10;
       int small = 70;
       //jtable = null;
       jtable = new Object[size + 1][10];
       jTable2 = new javax.swing.JTable(new Tablemodel1(size));
       renderer = new EvenOddRenderer();
//       jTable2.setDefaultRenderer(Integer.class, renderer);
//       jTable2.setDefaultRenderer(String.class, renderer);
       //jTable2.getSelectionModel().addListSelectionListener(new RowListener());
       jTable2.getColumnModel().getSelectionModel().
                 addListSelectionListener(new ColumnListener());
       jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
//        jTable2.getColumnModel().getColumn(0).setPreferredWidth(wth - name);
//        jTable2.getColumnModel().getColumn(1).setPreferredWidth(name);
//        jTableBP.getColumnModel().getColumn(2).setPreferredWidth(name/2);
       jTable2.setAutoCreateRowSorter(true);
       jScrollPane1.setViewportView(jTable2);
       //jTable2 =  new  javax.swing.JTable(size+1,10);
       //jTable2.getColumnModel().getColumn(0).setHeaderValue("TIME");
       jTable2.getColumnModel().getColumn(0).setPreferredWidth(small);
       column = jTable2.getColumnModel().getColumn(0);
       //jTable2.getColumnModel().getColumn(1).setHeaderValue("QUEUE");
       jTable2.getColumnModel().getColumn(1).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(2).setHeaderValue("Threads");
       jTable2.getColumnModel().getColumn(2).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(3).setHeaderValue("Calc");
       jTable2.getColumnModel().getColumn(3).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(4).setHeaderValue("Depth");
       jTable2.getColumnModel().getColumn(4).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(5).setHeaderValue("WFC_id");
       jTable2.getColumnModel().getColumn(5).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(6).setHeaderValue("Step");
       jTable2.getColumnModel().getColumn(6).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(7).setHeaderValue("Active(ms)");
       jTable2.getColumnModel().getColumn(7).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(8).setHeaderValue("Free Mem%");
       jTable2.getColumnModel().getColumn(8).setPreferredWidth(small);
       //jTable2.getColumnModel().getColumn(9).setHeaderValue("BP name");
       jTable2.getColumnModel().getColumn(9).setPreferredWidth(wth - 9*small);
       jTextField1.setText(hdr);
       jScrollPane1.setViewportView(jTable2);
//       js1.setViewportView(jTable2);
       bpNames = new Properties();
       //bpAttributes = new long[7];
       bpAttributes = new long[8];
       bpAttributes[1]=99999;
       bpAttributes[3]=99999;
       stepCount=0;
       timeCount=0;
       timeProps = new Properties(); 
       stepProps = new Properties();
    }
    public void updateTable(int row,String step, String act,String wfid){
        setValueAt(step,row,6);
        setValueAt(act,row,7);
//        bpAttributes[5]++;
//        jTable2.setValueAt(bp,row,8);
        timeProps.setProperty(wfid,(String)act);
        stepProps.setProperty(wfid,step);
        
//        adjustAttribute(step,act);
    }
    public void updateTable(int row,
                            String tim,
                            String q,
                            String t,
                            String c,
                            String d,
                            String wfid,
                            String step,
                            String act,
                            String mem,
                            String bp){
        //jTable2.setValueAt(tim,row,0);
        /*
        if (t != null) {
            try {
                if (Integer.parseInt(d) > 0 && Integer.parseInt(c) > Integer.parseInt(t))
                    renderer.getTableCellRendererComponent(jTable2 , null,false, false, row,2);
            } catch (Exception exp) {
            }
        }
        */
        long actInt = Long.parseLong(act);
        setValueAt(tim,row,0);
        //jTable2.setValueAt(q,row,1);
        setValueAt(q,row,1);
        setValueAt(t,row,2);
        setValueAt(c,row,3);
        setValueAt(d,row,4);
        setValueAt(wfid,row,5);
        setValueAt(step,row,6);
        //setValueAt(act,row,7);
        setValueAt(actInt,row,7);
        setValueAt(mem,row,8);
        setValueAt(bp,row,9);
/*
        if (t != null) {
            try {
                if (Integer.parseInt(d) > 0 && Integer.parseInt(c) > Integer.parseInt(t))
                    renderer.getTableCellRendererComponent(jTable2 , null,false, false, row,2);
            } catch (Exception exp) {
            }
        }
*/
        timeProps.setProperty(wfid,(String)act);
        stepProps.setProperty(wfid,step);
    }

    public void updateTable(int row,
                            String tim,
                            String q, int t, int c, int d,
                            String wfid,
                            String step,
                            String act,
                            String mem,
                            String bp){
        long actInt = Long.parseLong(act);
        setValueAt(tim,row,0);
        setValueAt(q,row,1);
        setValueAt(t,row,2);
        setValueAt(c,row,3);
        setValueAt(d,row,4);
        setValueAt(wfid,row,5);
        setValueAt(step,row,6);
        //setValueAt(act,row,7);
        setValueAt(actInt,row,7);
        setValueAt(mem,row,8);
        setValueAt(bp,row,9);
        /*
        if (d > 0 && c > t)
           renderer.getTableCellRendererComponent(jTable2, String.class,false, false, row,2);
        */
        timeProps.setProperty(wfid,(String)act);
        stepProps.setProperty(wfid,step);
    }
    private void setValueAt(long arg,int record,int col) {
       if (jtable != null) {
           jtable[record][col] = arg;
        } else
           jTable2.setValueAt(arg,record,col);
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

    /*
    /*
     * // count, min,max,min,max,accstep,acctime
     */
    public void setAttribute(String step, String act) {
        long steps ;
        long acttim;
        try {
            steps = Long.parseLong(step);
            acttim = Long.parseLong(act);
        } catch (Exception exp) {
            System.out.println("error parseLong: step: " + step + " acttim: " + act);
            return;
        }
        bpAttributes[0]++;
        if (steps > 0)
            stepCount++;
        if (acttim > 0)
            timeCount++;
        if (steps <= bpAttributes[1]) {
           if (steps > 0l )
               bpAttributes[1] = steps;
        } else if (steps > bpAttributes[2])
           bpAttributes[2] = steps;
        if (acttim <= bpAttributes[3]) {
            if (acttim > 0l)
               bpAttributes[3] = acttim;
        } else if (acttim > bpAttributes[4])
        bpAttributes[4] = acttim;
        bpAttributes[5] += steps;
        bpAttributes[6] += acttim;
    }
    // code to perhap make selections
    private void outputSelection() {
        jTable2.getSelectionModel().getLeadSelectionIndex();
        jTable2.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        int[] r = jTable2.getSelectedRows();
        int[] c = jTable2.getSelectedColumns();
        Object name = jTable2.getValueAt(r[0],c[0]);

        if (c[0] == 1 || c[0] == 5 || c[0] == 9 ) {
            QWUtil.resetInput();
            if (c[0] == 1) {
                int q = Integer.parseInt((String)jTable2.getValueAt(r[0],c[0]));
               (new QWWfc(this,"WFD Listing",false,"List All BPs on Queue " + q)).listWFDs(q, 0);
            } else if (c[0] == 5) {
                (new QWWfc(this,"WFD Listing",false,"List occurence of same WFID " + name)).listWFDs(0, 10,(String) name);
            } else if (c[0] == 9) {
                (new QWWfc(this,"WFD Listing",false,"List occurence of BP " + name)).listWFDs(0, 7,(String) name);
            }
        }
//        int cnt = Integer.parseInt(count.toString());
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
 
    private class ColumnListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            outputSelection();
        }
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
                    return "Threads";
                case 3:
                    return "Calc";
                case 4:
                    return "Depth";
                case 5:
                    return "WFC_id";
                case 6:
                    return "Step";
                case 7:
                    return "Active(ms)";
                case 8:
                    return "Free Mem%";
                case 9:
                    return "BP Name";
            }
            return null;
        }
        public Class getColumnClass(int kolonne) {
        //return String.class;
            if (kolonne == 7) {
                return Long.class;
            } else {
                return String.class;
            }
         }
 /*       
    public Class getColumnClass(int kolonne) {
      //return String.class;
     //*
     if (kolonne != 7) {
         return String.class;
     } else {
         return Integer.class;
     }
 //*/
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

// Helper Class
 private class Qhelper {
    String tm;
    String q;
    //int thread;
    String thread;
    //int calc;
    String calc;
    //int waiters;
    String waiters;
    String wfid;
    String step;
    String active;
    String mem;
    String bpn;
    public Qhelper() {
    //    thread = 9999;
    //    calc = 9999;
    //    waiters = 9999;
    }
    public void setTime(String tm) {this.tm=tm;}
    public String getTime(){return tm;}
    public void setQ(String qu) {this.q= qu;}
    public String getQ() {return q;}
    //public void setThread(int t) { this.thread = t; }
    public void setThread(String t) { this.thread = t; }
    //public int getThread() { return thread; }
    public String getThread() { return thread; }
    //public void setCalc(int v) { this.calc = v; }
    public void setCalc(String v) { this.calc = v; }
    //public int getCalc() { return calc; }
    public String getCalc() { return calc; }
    //public void setWaiters(int v) { this.waiters = v; }
    public void setWaiters(String v) { this.waiters = v; }
    //public int getWaitersw() { return waiters; }
    public String getWaitersw() { return waiters; }
    public void setWfid(String v) {this.wfid=v;}
    public String getWfid(){return wfid;}
    public void setStep(String v) {this.step=v;}
    public String getStep(){return step;}
    public void setAct(String v) {this.active=v;}
    public String getAct(){return active;}
    public void setMem(String v) {this.mem=v;}
    public String getMem(){return mem;}
    public void setBPname(String v) {this.bpn=v;}
    public String getBPName(){return bpn;}
   }

/*
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Max step:");

        jLabel2.setText("Min Step:");

        jLabel3.setText("Avg Step:");

        jLabel4.setText("Max Time:");

        jLabel5.setText("Min Time:");

        jLabel6.setText("Avg Time:");

        jLabel7.setText(" ");
        jLabel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText(" ");
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setText(" ");
        jLabel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText(" ");
        jLabel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setText(" ");
        jLabel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setText(" ");
        jLabel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setText("Count:");

        jLabel14.setText(" ");
        jLabel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setText("Chart");
        jButton1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(46, Short.MAX_VALUE))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(19, 19, 19)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addContainerGap(513, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(88, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.out.println("");// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                QWWfc dialog = new QWWfc(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
