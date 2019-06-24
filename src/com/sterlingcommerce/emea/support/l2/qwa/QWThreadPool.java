/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;
import java.util.ArrayList;
/**
 *
 * @author Alf
 */
public class QWThreadPool {
 javax.swing.JPanel jPan = null;
    public QWThreadPool(javax.swing.JPanel jPan) {
      this.jPan = jPan;
    }
    /**
     * Handle the CFG token on logline
     * Called from QWAnalyzer at start up it populates the q pool cfg aray
     * @param str 
     */
    public void createQTable(String str) {
     ArrayList al = QWUtil.tokenize(str," ");
     String tmp = (String)al.get(4);
     QWAThreadPool.columnNames[0] = tmp;
     int inx = tmp.indexOf("=");
     try{
        QWAThreadPool.MaxThreads = Integer.parseInt(tmp.substring(inx+1));
        QWAGlobal.MaxThreads = QWAThreadPool.MaxThreads;
        for (int i = 0; i < 16; i++) {
            String qItem = (String)al.get(i+5);
            ArrayList qiAl = QWUtil.tokenize(qItem,";");
            for (int j = 0; j < 10; j++) {
                QWAThreadPool.ThreadPools[i][j] = (String)qiAl.get(j);
            }
        }
      } catch(Exception  exp) {
        exp.printStackTrace();    
      }
      createFairShareTable();
    }
    /**
     * Set up MinSize,MaxSze,Resource
     */
    public void createFairShareTable() {
      createIntArray(QWAThreadPool.ThreadPools,3,1,QWAFairShareCalc.FairShareFeed,0,0);
      createIntArray(QWAThreadPool.ThreadPools,5,1,QWAFairShareCalc.FairShareFeed,1,0);  
      createIntArray(QWAThreadPool.ThreadPools,11,1,QWAFairShareCalc.FairShareFeed,2,0);
        
    }
     private void createIntArray(String [][] iPool, int iRow, int iCol, int [][] oPool, int oRow, int oCol) {
       try {
           for (int i = 0; i < 9; i++) {
              oPool[oRow][oCol + i] = Integer.parseInt(iPool[iRow][iCol + i]);
           }
       } catch (Exception exp) {
          exp.printStackTrace(); 
       }
         String tmp = iPool[iRow][iCol];
    }
   
    /**
     * createPoolThread table
     * Display the active configuration of the Queue PooolThreads.
     */
    public void createJtable(){
       if (QWAThreadPool.MaxThreads == -1)
           return;
       javax.swing.JTable jt = new javax.swing.JTable(QWAThreadPool.ThreadPools,QWAThreadPool.columnNames); 
       javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(jt);
       //setSize(900,350);
       scrollPane.setSize(900, 350);
       int wth = scrollPane.getWidth() - 10;
       int col0 = 140;
       int col1to9 = (wth-100)/9;
       jt.getColumnModel().getColumn(0).setPreferredWidth(col0);
       for (int i = 0; i < 9; i++) {
            jt.getColumnModel().getColumn(i+1).setPreferredWidth(col1to9);
       }
       //add(scrollPane);
       jPan.add(scrollPane);
    }

    
    
}
