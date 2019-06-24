/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Alf
 */
public class QWAFairShareCalc extends QWAJtabModel {
  //String [] columnNames = {"Pools ","Queue1","Queue2","Queue3","Queue4","Queue5","Queue6","Queue7","Queue8","Queue9"};
   //String [] rowNames = {"MaxPoolSize","MinPoolSize","Resource","Calc Threads"};
  String [] rowNames = {"Queue1","Queue2","Queue3","Queue4","Queue5","Queue6","Queue7","Queue8","Queue9"};
  String [] columnNames = {"Pools ","MaxPoolSize","MinPoolSize","Resource","Calc Threads","InUse"};

   Pool [] Pools = {null,null,null,null,null,null,null,null,null};
   static int [][] FairShareFeed = new int[4][9];
   static int [][] fsVertical = new int[9][4]; 
   public int [][] tmpFS = null;
   static String [][] queuesCC = null;
   int length = 0;
   javax.swing.JTable jt;
   Font font;
   boolean Debug = true;
   public static NumericalHashMap QueueResourceAllocations = null; 
   public static NumericalHashMap QueueActiveThreads = new NumericalHashMap();
   int ThreadsLimit = QWAThreadPool.MaxThreads;
   
   public QWAFairShareCalc() {
        QWAnaGUI.jTextField5.setText(String.valueOf(QWAThreadPool.MaxThreads));
        tmpFS = FairShareFeed.clone();
        QueueResourceAllocations = new NumericalHashMap();
        moveTo();
        initPools();
    }
   private void moveTo(){
       for (int i = 0; i < 4; i++) {
           for (int j = 0; j < 9 ; j++) {
               fsVertical[j][i] =  FairShareFeed[i][j];   
           }
       }
   }
   
   /**
     * createPoolThread table
     * Display the active configuration of the Queue PooolThreads.
     */
    public void createJtable(){
       //jt = new javax.swing.JTable(new Tablemodel1(5,10,FairShareFeed,columnNames,rowNames));
       jt = new javax.swing.JTable(new Tablemodel1(9,6,fsVertical,columnNames,rowNames));
       jt.setFont(new Font("Verdana",Font.PLAIN,12));
       jt.getSelectionModel().addListSelectionListener(new RowListener());
       jt.getColumnModel().getSelectionModel().
            addListSelectionListener(new ColumnListener());       
       jt.setCellSelectionEnabled(true);
       new TableListener1(jt);
       QWAnaGUI.jScrollPane10.add(jt);
       int wth = QWAnaGUI.jScrollPane10.getWidth() - 10;
       int col0 = 140;
       int col1to9 = (wth-100)/9;
       jt.getColumnModel().getColumn(0).setPreferredWidth(col0);
       for (int i = 0; i < 5; i++) {
            jt.getColumnModel().getColumn(i+1).setPreferredWidth(col1to9);
       }
       jt.createDefaultColumnsFromModel();
       QWAnaGUI.jScrollPane10.setViewportView(jt);
    }   
    public void createConcurrency(Properties props) {
       Properties p = props;
       length = p.size();
       if (length == 0)
           return;
       int ii = 0;
       queuesCC = new String[length][12];
       Enumeration em = p.keys();
     
       while(em.hasMoreElements()){
           try{  
             String str = (String)em.nextElement();
             String count = p.getProperty(str);
             ArrayList al = QWUtil.tokenize(str,";");
             for (int i = 0; i < 9; i++) {
                 String str1 = (String)al.get(i);
                 if (!str1.isEmpty())
                     queuesCC[ii][i] = str1;
             }
             double teller = Double.parseDouble(count);
             double nevner =  QWAnalyze.queLines;
             double cntPcnt = (teller / nevner) * 100;
             queuesCC[ii][9] = " ";
             queuesCC[ii][10] = count;
             queuesCC[ii][11] = afterComma(String.valueOf(cntPcnt));
             ii++; 
           } catch (Exception exp){
               exp.printStackTrace();
           }  
       }            
       createJtableCC();
    }      

    public void createJtableCC(){
       javax.swing.JTable jt = new javax.swing.JTable(new Tablemodel2(length,12,queuesCC));
       /*
       TableModel tm =
              Vector data = tm.DataVector();
       Collections.sort(data, new ColumnSorter(0));
       */         
       jt.setFont(new Font("Verdana",Font.PLAIN,12));
       jt.getSelectionModel().addListSelectionListener(new RowListener());
       jt.getColumnModel().getSelectionModel().
                addListSelectionListener(new ColumnListener());
       jt.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
       jt.setRowSelectionAllowed(true); 
       jt.setColumnSelectionAllowed(false);
       jt.addMouseListener(new MouseAdapter(){
           @Override
           public void mouseClicked(MouseEvent e){
             if (e.getClickCount() == 2){
               System.out.println(" double click" );
              }
           }
        } );       
       QWAnaGUI.jScrollPane11.add(jt);
       int wth = QWAnaGUI.jScrollPane11.getWidth() - 10;
       int col0 = 140;
       int col1to9 = (wth-100)/9;
       jt.getColumnModel().getColumn(0).setPreferredWidth(col0);
       for (int i = 0; i < 9; i++) {
            jt.getColumnModel().getColumn(i+1).setPreferredWidth(col1to9);
       }
       jt.setAutoCreateRowSorter(true);
       jt.createDefaultColumnsFromModel();
       QWAnaGUI.jScrollPane11.setViewportView(jt);
    } 
    public String afterComma(String str) {
        String str1 = null;
        int l = 3;
        if (str.length() < 4)
            l=2;
        int i1 = str.indexOf(".");
        if (i1 != -1)
           try { 
               str1 = str.substring(0,i1+2);
           } catch (Exception e) {
               System.out.println("bad string--> " +str1);
               e.printStackTrace();
           }  
        return str1;
    }
    /**
     * 
     * @return 
     */
     public int getActiveAllocations() {
        Iterator myIterator = QueueActiveThreads.keySet().iterator();
        int returnValue=0;
        //StringBuffer SB = new StringBuffer();
        while (myIterator.hasNext()) {
            Object Key = myIterator.next();
            // if (Debug) {SB.append("Queue:"+Key + " RA:"+QueueResourceAllocations.getInt(Key)+" Threads:"+ QueueActiveThreads.getInt(Key) );}
            if(QueueActiveThreads.getInt(Key)>0) {
                // if allow stealing, us max(min,threadsinuse)
                returnValue = returnValue + QueueResourceAllocations.getInt(Key);
            }
        }
        if (returnValue<1) {returnValue=1;}
        //if(Debug) {System.out.println("The active allocations are " + SB.toString()); }
        return returnValue;
    }
    public int getActiveAllocations(String QueueName) {
        Iterator myIterator = QueueActiveThreads.keySet().iterator();
        int returnValue=0;
        //StringBuffer SB = new StringBuffer();
        if ((getThreadPool(QueueName)).StealThreads==false) {
            returnValue=getActiveAllocations();
        } else {
            while (myIterator.hasNext()) {
                Object Key = myIterator.next();
                // if (Debug) {SB.append("Queue:"+Key + " RA:"+QueueResourceAllocations.getInt(Key)+" Threads:"+ QueueActiveThreads.getInt(Key) );}
                if(QueueActiveThreads.getInt(Key)>0) {
                    // if allow stealing, us max(min,threadsinuse)
                    if ((getThreadPool((String)Key)).AllowStealing==false) {
                        returnValue = returnValue + QueueResourceAllocations.getInt(Key);
                    } else {
                        returnValue += Math.max(this.getMinThreads((String) Key),this.getThreadsInUse((String)Key));
                    }
                }
            }
        }
        if (returnValue<1) {returnValue=1;}
        //if(Debug) {System.out.println("The active allocations are " + SB.toString()); }
        return returnValue;
    }

    public int getAvailableThreads(String QueueName) {
    	int returnValue = 1; // WAIT queue only has one thread.
    	if (!QueueName.equals("0")) {
    		// Not the WAIT queue so determine availability
    	   returnValue = (int) Math.ceil(Math.max(Math.min((((double)QueueResourceAllocations.getInt(QueueName)/(double)getActiveAllocations(QueueName))*(double)ThreadsLimit),(double)getMaxThreads(QueueName)),(double)getMinThreads(QueueName)));
    	}
        if (Debug) {System.out.println("getAvailableThreads:Queue:" + QueueName + " Calculated Value is:" + returnValue);}

        return returnValue;
    }
    private int getMinThreads(String q){
        int qInt = Integer.parseInt(q);
        return tmpFS[0][qInt-1];
    }
    private int getMaxThreads(String q){
        int qInt = Integer.parseInt(q);
        return tmpFS[1][qInt-1];
   }
   private int getThreadsInUse(String q){
        return 0;
   }
   private Pool getThreadPool(String key){
       return Pools[Integer.parseInt(key)-1];
   }
   private void initPools(){
       for (int i = 0; i < 9; i++) {
           Pools[i] = new Pool();
           Pools[i].AllowStealing = false;
           Pools[i].StealThreads = false;
       }
 
   }
   private class Pool {
     boolean StealThreads = false;  
     boolean AllowStealing = false;
   }
}
