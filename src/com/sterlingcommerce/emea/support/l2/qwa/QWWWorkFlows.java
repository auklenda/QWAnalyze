/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.util.Enumeration;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane; 



/**
 *
 * @author AAuklend
 */
//public class QWWWorkFlows extends QWAnaGUI{
public class QWWWorkFlows {
 private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard(); 
 javax.swing.JTable jTable2;
 TableCellRenderer renderer = null;
 Object [][] jtable;
 int theSize = 0;
 String hdr;
 Properties bpNames = null;
 Properties stepProps = null;
 Properties timeProps = null;
 int stepCount = 0;
 int timeCount = 0;
 int [][] qmon = new int[9][7] ;  // 0=q,1=min,2=used,3=calc,4=pool,5=max,6=dept
 long [] bpAttributes = null;   // count, min,max,min,max,accstep,acctime,lastsep,lastwait
 Object jframe = null;

    public QWWWorkFlows(String header) {
        hdr = header;
    }
    public QWWWorkFlows(Object jframe, String header) {
        this.jframe = jframe;
        hdr = header;
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
      while ((inpr = QWUtil.readLine(QWAGlobal.wfc)) != null) {
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
      Enumeration en = p.propertyNames();
      while (en.hasMoreElements()) {
          String wf = (String)en.nextElement();
          ArrayList al = (ArrayList)p.get(wf);
          Iterator it = al.iterator();
          while (it.hasNext()) {
              total.add(it.next());
          }
      }
      theSize = total.size();
      createTable(theSize);
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
      if (QWAnaGUI.isHTML(10))
           createHTML(theSize);

    }
    // time=2 wf_if = 9 bpName=21
public void listWFDs(int threshold, int qIndex,boolean html){
    listWFDs(threshold , qIndex, null);
    if (html)
        createHTML(theSize,threshold);
        
}    

public void listWFDs(int threshold,int qIndex) {
    listWFDs(threshold , qIndex, null);
}
    /**
     *  0=q,1=min,2=used,3=calc,4=pool,5=max,6=dept
     * @param threshhold - list if higher than this value
     * @param index      - 0 = queue, 2 = active threads , 6 = waiting for threads , 7 = BPname
     * @param name       - BPName
     * called from : QWAnaGUI
     */
public void listWFDs(int threshold,int action, String name) {
      String inpr;
      String freeMem = " na ";
      ArrayList alhelper = new ArrayList();
      ArrayList alString = null;
      boolean candidate = false;
      //Properties wfidProps = new Properties();
      int dubCount=0;
      int thresCount = 0;
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

                if (action == 0) {
                    if (qmon[threshold-1][2] > 0)
                        candidate = true;
                } else if (action == 15) {
                    if (qmon[threshold-1][5] > 0)
                        candidate = true;
                                
                }
                if (action == 2 || action == 6) {        //list All
                   for (int i = 0; i < 9; i++) {
                       if (qmon[i][action] >= threshold) {
                           candidate=true;
                           thresCount++;
                           break;
                       }    
                   } // end for
                }
            } else if (recType ==  QWAGlobal.wfc) {  // WFC info of active BP's
                if (candidate) {                     // Candidate found in the Queue Info ?
                    if ( alString == null)
                        alString = new ArrayList();
                    //ArrayList wfcList = QWUtil.tokenize(inpr);
                    if (action == 0) {
                        ArrayList wfcList = QWUtil.tokenize(inpr);
                        int q = QWUtil.isitDigit((String) wfcList.get(5));
                        if (q != -1 && q == threshold)
                            alString.add(inpr);
                    } else {
                        alString.add(inpr);
                    }
                } else if (action == 7 || action == 8 || action == 9 || action == 10) {
                     if ( alString == null) alString = new ArrayList();
                     ArrayList wfcList = QWUtil.tokenize(inpr);
                     if (action == 7) {
                         String bpn = (String)wfcList.get(21);
                         if (bpn.equalsIgnoreCase(name))
                            alString.add(inpr);
                    } else if (action == 10) {
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
       Properties prop = new Properties();
       int tableSize = alhelper.size();
       if (action == 9) {  //We need to prescan to get correct table size
          Iterator it =  alhelper.iterator();
          int rec = 0;
          while(it.hasNext()) {
               Qhelper qh = (Qhelper)it.next();
               String pos = prop.getProperty(qh.getWfid());
               if (pos == null) {
                   prop.setProperty(qh.getWfid(),String.valueOf(rec));
                   rec++;
               } else {
                   tableSize--;
               }
          } // end while
       }  //  end prescan
       prop.clear();
       createTable(tableSize);
       int rec  = 0;
       String pos = null;
       boolean updatePartial = false;
       Iterator it =  alhelper.iterator();
       while(it.hasNext()) {
            Qhelper qh = (Qhelper)it.next();
            if (action == 9) {
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
               dubCount++;
            }
       }
       displayCounters();
       if (QWAnaGUI.isCSV){
           createCSV(tableSize,threshold);
       }
       if (QWAnaGUI.isHTML(action))
           createHTML(tableSize,threshold);
//       this.setVisible(true);
    }

/**
 *
 * @param alStrings
 * @param alhelper
 * @param mem
 */
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
             //  if ( trds != 0 && prevQueue != qinx) {
                 if ( prevQueue != qinx) {
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
      QWAnaGUI.jLabel44.setText(String.valueOf(bpAttributes[0]));
      QWAnaGUI.jLabel37.setText(String.valueOf(bpAttributes[2]));
      QWAnaGUI.jLabel35.setText(String.valueOf(bpAttributes[1]));
      QWAnaGUI.jLabel30.setText(String.valueOf(bpAttributes[4]));
      QWAnaGUI.jLabel36.setText(String.valueOf(bpAttributes[3]));
      long avg = 0l;
      if (stepCount > 0)
         avg = bpAttributes[5]/stepCount;
      QWAnaGUI.jLabel46.setText(String.valueOf(avg));
      if (timeCount >0l)
         avg = bpAttributes[6]/timeCount;
      QWAnaGUI.jLabel47.setText(String.valueOf(avg));
    }
 private void createHTML(int tableSize){
     createHTML(tableSize,-1);
 }
 private void createHTML(int tableSize,int count){
    if (QWAGlobal.htmlFile == null)
        return;
    //QWAReporting qwar = new QWAReporting();
    StringBuilder sb = QWAReporting.startReport();
    ArrayList al = new ArrayList(12);
    al.add("Time    ");
    al.add("Queue   ");
    al.add("Threads ");
    al.add("Calc    ");
    al.add("Depth   ");
    al.add("WF-ID   ");
    al.add("Step    ");
    al.add("Active(ms)");
    al.add("Heap%  ");
    al.add("BPName  ");
    QWAReporting.doTitle(hdr, sb,count);
    QWAReporting.doTable(sb,true);
    QWAReporting.doHeader(al, sb);
    QWAReporting.doItems(jtable,tableSize, 10, sb);
    QWAReporting.doTable(sb, false);
    //QWAReporting.closeReport(sb);
    QWAGlobal.htmlFile.println(sb.toString());
    //System.out.println(sb.toString());
    System.out.println("stop");
}
 /**
  * Create comma separated values (for spreadsheet
  */
   public void createCSV(int tableSize,int count){
    if (QWAGlobal.csvFile == null)
       return;
    // CSV header
    String hdr =("Time;Queue;Threads;Calc;Depth;WF_ID;Step;Active(ms);Heap(%);BPName;");
    QWAGlobal.csvFile.println(hdr);
    int lng = jtable.length;
    for (int i1 = 0 ; i1 < lng; i1++) {
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (jtable[i1][i] != null)
                report.append(jtable[i1][i]).append(";");
            else
                report.append(" ;");
        }
        QWAGlobal.csvFile.println(report.toString());
    }    
   } 
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
       //jTable2.setDefaultRenderer(String.class, renderer);
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
       jTable2.addMouseListener(new MouseAdapter(){
           public void mouseClicked(MouseEvent e){
             if (e.getClickCount() == 2){
               System.out.println(" double click" );
              }
           }
        } );       
       QWAnaGUI.jTextField4.setText(hdr);
       QWAnaGUI.jScrollPane6.setViewportView(jTable2);
     //  QWAnaGUI.jTabbedPane1.setSelectedIndex(QWAnaGUI.jTabbedPane1.getTabCount()-1);
       QWAnaGUI.displayTab(QWAnaGUI.jTabbedPane1,"Threads");
       
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
        timeProps.setProperty(wfid,(String)act);
        stepProps.setProperty(wfid,step);
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
        long actLong;
        try {
            actLong = Long.parseLong(act);
        } catch (NumberFormatException exp) {
            System.out.println("NumberFormatException: "+ act);
            return;
        }
    
        /*
        if (t != null) {
            try {
                if (Integer.parseInt(d) > 0 && Integer.parseInt(c) > Integer.parseInt(t))
                    renderer.getTableCellRendererComponent(jTable2 , null,false, false, row,2);
            } catch (Exception exp) {
            }
        }
        */
        setValueAt(tim,row,0);
        //jTable2.setValueAt(q,row,1);
        setValueAt(q,row,1);
        setValueAt(t,row,2);
        setValueAt(c,row,3);
        setValueAt(d,row,4);
        setValueAt(wfid,row,5);
        setValueAt(step,row,6);
        //setValueAt(act,row,7);
        setValueAt(actLong,row,7);
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
        long actLong = Long.parseLong(act);
        setValueAt(tim,row,0);
        setValueAt(q,row,1);
        setValueAt(t,row,2);
        setValueAt(c,row,3);
        setValueAt(d,row,4);
        setValueAt(wfid,row,5);
        setValueAt(step,row,6);
        //setValueAt(act,row,7);
        setValueAt(actLong,row,7);
        setValueAt(mem,row,8);
        setValueAt(bp,row,9);
        //if (d > 0 && c > t)
           //renderer.getTableCellRendererComponent(jTable2, String.class,false, false, row,2);
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
               (new QWWfc((QWAnaGUI)this.jframe,"WFD Listing",false,"List All BPs on Queue " + q)).listWFDs(q, 0);
            } else if (c[0] == 5) {
                (new QWWfc((QWAnaGUI)this.jframe,"WFD Listing",false,"List occurence of same WFID " + name)).listWFDs(0, 10,(String) name);
            } else if (c[0] == 9) {
                (new QWWfc((QWAnaGUI)this.jframe,"WFD Listing",false,"List occurence of BP " + name)).listWFDs(0, 7,(String) name);
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
    }
    public Class getColumnClass(int kolonne) {
      return String.class;
     /*
     if (kolonne != 7) {
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
            int thread =0;
            int calc =0;
            try {
                thread = Integer.parseInt((String)table.getValueAt(row, 2));
                calc = Integer.parseInt((String)table.getValueAt(row, 3));
                //Integer thread = (Integer)table.getValueAt(row, 2);
                //Integer calc = (Integer)table.getValueAt(row, 3);
            } catch(Exception exp) {
                exp.printStackTrace();
            }
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
    public void setTime(String tm){ 
        this.tm=tm;}
    public String getTime(){
        return tm;}
    public void setQ(String qu) {
        this.q= qu;}
    public String getQ() {
        return q;}
    public void setThread(String t) { 
        this.thread = t; }
    public String getThread() { 
        return thread; }
    public void setCalc(String v) { 
        this.calc = v; }
    public String getCalc() { 
        return calc; }
    public void setWaiters(String v) { 
        this.waiters = v; }
    public String getWaitersw() { 
        return waiters; }
    public void setWfid(String v) {
        this.wfid=v;}
    public String getWfid(){
        return wfid;}
    public void setStep(String v) {
        this.step=v;}
    public String getStep(){
        return step;}
    public void setAct(String v) {
        this.active=v;}
    public String getAct(){
        return active;}
    public void setMem(String v) {
        this.mem=v;}
    public String getMem(){
        return mem;}
    public void setBPname(String v) {
        this.bpn=v;}
    public String getBPName(){
        return bpn;}
   }

}
