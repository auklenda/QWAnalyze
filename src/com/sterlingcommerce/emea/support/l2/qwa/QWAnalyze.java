/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import java.awt.Font;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JLabel;
/**
 *
 * @author AAuklend
 */
//public class QWAnalyze {
  public class QWAnalyze {
  QWAnaGUI GUI = null;
  long tstgap = 0l;
  long bigGap = 0l;
  int linr =0;
  int depth = 0;
  int countLines = 0;
  static int queLines = 0;
  int wfcLines = 0;
  int memLines = 0;
  int waiters = 0;           // queues found with waiting wfc's
  int lowerThreadLimit = 0;
  String folder = null;
  String searchP = null; 
  String csv = ",";
  String FromDate = null;
  String ToDate = null;
  String inFileName = null;
  String start = null;
  String last = null;
  String nodeName = null;
  
  long fromTime = 0l;
  long toTime = 0l;
  double memThreshold = 50.0;
  boolean prtAll = false;
  boolean subf = true;
  boolean verbose = false;
  boolean memPrt=false;
  boolean quePrt = true;
  boolean wfdPrt = false;
  boolean debug = false;
  boolean html = true;
  boolean oldFormat = false;
  static BufferedReader inp = null;  
  static PrintWriter outp = null;
  PrintWriter queFile;
  PrintWriter wfdFile;
  PrintWriter memFile;
  //BufferedReader QWFile = null;
  long samples = 0l;
 public int [][] qmon = new int[9][6] ; 
 //public int [] qwaiters = new int[3];
 //int [] maxDepth =  new int [9];
 int [] qs = new int[9];
 int onWait = 0;
 static int maxActiveThreads = 0;
 boolean configRecord = false;
 int moreThan50prct =0;
 int lessThan10prct =0;
 double averageHeap = 0.0;
 double totMemSize = 0.0;
 static String maxActiveThreadsTime = null;
 String currentQTime = null;
 String currentQDate = null;
 javax.swing.JTextArea jtWaiters = null;
 javax.swing.JLabel jlWaiters = null;
  javax.swing.JTextArea jTA1= null;
 javax.swing.JTextArea jTA2= null;
 javax.swing.JTextPane jTPA1 = null;
 javax.swing.JTextField jHdr = null;
 javax.swing.JTable jHistTable = null;
 javax.swing.JTable jPeakTable = null;
 javax.swing.JTable jScheduleTable = null; 
 javax.swing.JTable jQtable = null; 
 int minThread = 0;
 int inUseThreads = 1;
 int fairShare = 2;
 int inPool = 3;
 int maxThread = 4;
 int qDepth = 5;
 static int [] totalWFCs = new int[9];
 static Properties concurrentQs = null; 
 
 String [] series = {"Q1","Q2","Q3","Q4","Q5","Q6","Q7","Q8","Q9"};
 String pad = "                                                                      ";
// NumericalHashMap bps = null;
 public static int bpCount = 0;
 WaitHelper wh = null;
    /** Creates a new instance of LogSearch */
    public QWAnalyze() {
       
    }
    public QWAnalyze(QWAnaGUI inst) {
        GUI = inst;
    }

    private boolean openInFile(String fn) {
      try {  
        inp = null; //gc  
        if (openInFile(fn,0) == null)
            return false;
      } catch (Exception exp) {
            exp.printStackTrace(); 
            return false;
            
      }  
      return true;
    }
    public BufferedReader openInFile(String fn,int i) {
        try {
            return(new BufferedReader(new InputStreamReader(new FileInputStream(fn))));
      } catch (Exception exp) {
            return null;
      }  
    }
    private boolean openOutFile(String fn) {
      try {  
        outp = null; //gc  
        outp = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn)));
      } catch (Exception exp) {
            exp.printStackTrace();
            return false;
      }  
      return true;
    }
    public PrintWriter openFile(File fn) {
     try {  
        return(new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn))));
      } catch (Exception exp) {
            exp.printStackTrace();
            return null;
      }  
    }
    public PrintWriter openFile(String fn) {
     try {  
        return(new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn))));
      } catch (Exception exp) {
            exp.printStackTrace();
            return null;
      }  
    }
    public void openFile(String fn,String fn1){
      try {
        inp = new BufferedReader(new InputStreamReader(new FileInputStream(fn)));
        if (fn1 != null)
            outp = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn1)));
      } catch (Exception exp) {
            exp.printStackTrace();
      }  
    }
    protected void openQWFile() {
     try {
        if (QWAGlobal.QWFileName !=null)
            QWAGlobal.QWFile = openInFile(QWAGlobal.QWFileName,0);
     } catch(Exception exp) {
         exp.printStackTrace();
     }   
    }
    protected void closeQWFile() {
     try {
        if (QWAGlobal.QWFile !=null)
            QWAGlobal.QWFile.close();
     } catch(IOException exp) {
         exp.printStackTrace();
     }   
    }
    protected void closeQueFile() {
        if (queFile !=null)
            queFile.close();
    }
    
    protected void setQWFileName(String path) {
        QWAGlobal.QWFileName = path;
    }
    protected String getQWFileName() {
        return QWAGlobal.QWFileName;
    }

    protected void setQWFile(BufferedReader br) {
        QWAGlobal.QWFile = br;
    }
    public void setHTMLFile(PrintWriter br){
        if (QWAGlobal.htmlFile != null){
            QWAGlobal.htmlFile.flush();
            QWAGlobal.htmlFile.close();
        }
        QWAGlobal.htmlFile = br;
    }
    public void setCSVFile(PrintWriter br){
        if (QWAGlobal.csvFile != null){
            QWAGlobal.csvFile.flush();
            QWAGlobal.csvFile.close();
        }
        QWAGlobal.csvFile = br;
    }
    
    protected void setQueFile(PrintWriter br) {
        queFile = br;
    }
    protected void setWfdFile(PrintWriter br) {
       wfdFile = br;
    }
    protected void setMemFile(PrintWriter br) {
        memFile = br;
    }
    protected void setQueSelect(boolean sel) {
        quePrt = sel;
    }
    protected void setWfdSelect(boolean sel) {
       wfdPrt = sel;
    }
    protected void setMemSelect(boolean sel) {
        memPrt = sel;
    }
    private String removeSpace(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') sb.append(str.charAt(i)); }
        return sb.toString();
    }
    public boolean expdORdline(String str) {
        if (str.startsWith("-") || str.startsWith("0") || str.startsWith("f"))
           return true;
        return false;
    }
    /*
     *   Create a matrix with all parms for this sample line
     *   1 - 9 queues : Minpool, Used, Calculated , Pool, MaxPoool, Depth
     *   Q1 [0] ... Q9 [8]
    */ 
    protected void createQMatrix(String str) {
     ArrayList inpAL = QWUtil.tokenize(str);
     createQMatrix(inpAL);   
    }
    protected void createQMatrix(ArrayList inpAL) {
     int alIndx = 6;
     //ArrayList inpAL = QWUtil.tokenize(str);
     currentQDate = (String)inpAL.get(1);
     currentQTime = (String)inpAL.get(2);
     for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 6; j++) {
             try {
                qmon[i][j] = Integer.parseInt((String)inpAL.get(alIndx++));
             } catch (Exception exp) {
                 System.out.println("Faild in creating QMatrix:" + inpAL.toString());
             }
         }
         alIndx +=2;
     }
    }
     /**
     * Called at startup to set some counters to be displayed
     * Counters and arrays have to be cleared because it could be change of file.
     */
    public void collectStats() {
      String inpr = null;     
      countLines = 0;
      queLines=0;
      memLines=0;
      wfcLines=0;
      waiters = 0;
      moreThan50prct = 0;
      lessThan10prct = 0;
      averageHeap = 0.0;
      QWAGlobal.start = QWAGlobal.last = null;
      for (int i = 0; i < 9; i++) {       // Must clear if new File is opened.
           QWAGlobal.threadConfig[i][0] = 0;
           QWAGlobal.threadConfig[i][1] = 0;
           QWAGlobal.allThreadsInUse[i] = 0;
           QWAGlobal.anyThreadsInUse[i] = 0;
           QWAGlobal.waitersForThreads[i] = 0;
           QWAGlobal.calcHigherThread[i] = 0;
           QWAGlobal.fairShareImposed[i] = 0;
           QWAGlobal.maxDepth[i] = 0;
           QWAGlobal.maxDepthTime[i] = "";
           for (int j = 0; j < 10; j++) {
              QWAGlobal.histoGram[i][j] = 0;
          }
      }
      jtWaiters.setText("");
      jlWaiters.setText("\tTime It Takes To Clear a Queue (between samples)");
      if (wh == null)
          wh = new WaitHelper();
      QWAGlobal.queList = new ArrayList();
      QWAGlobal.bpListnew=null;
      QWAGlobal.bpList = null;
      while ((inpr = QWUtil.readLine()) != null) {
            if (inpr.length() > 1) {
              countLines++;
              GUI.bumpNumberOfLines(String.valueOf(countLines));
              collectStats(inpr);
            }     
      }
      new QWHistogram(GUI,"Display paralell queues to check FairShare",false).FairShare(QWAGlobal.queList);
      QWAFairShareCalc fsc = new QWAFairShareCalc();
      if (configRecord) {  
         fsc.createJtable();
      }  
      fsc.createConcurrency(concurrentQs);
     // }
    }   
    /**
     * Run through the total file at startup and set some counters
     * @param str
     */
    public void collectStats(String str) {
      ArrayList al = QWUtil.tokenize(str);
      try { 
        if (QWAGlobal.start == null) {
            nodeName = (String)al.get(0);
            String start = (String)al.get(1) + " " + (String)al.get(2);
            QWAGlobal.start = start;
            QWAGlobal.startDate = (String)al.get(1);
            QWAGlobal.nodeName = nodeName;
            concurrentQs = new Properties();
        } else {
            last = (String)al.get(1) + " " + (String)al.get(2);
            QWAGlobal.last =last;
            //int tm2 = QWUtil.getTimeInSeconds((String)al.get(2));
        }
        if (QWAGlobal.bpList == null) {
            QWAGlobal.bpList = new HashMap();
            QWAGlobal.bpListnew = new HashMap();
        }
        String tim = (String)al.get(2);
        QWAStats qwaStats = new QWAStats();
        int recType = QWUtil.getRecordType(str);
        if (recType ==  QWAGlobal.que) {
           QWAGlobal.queList.add(str);
           queLines++;
           createQMatrix(str);
           int inUse = 0;
           StringBuilder sb = new StringBuilder();
           for (int i = 0; i < 9; i++) {
               qwaStats.queueDepthHistogram(i,qmon[i][qDepth],tim);
               qwaStats.calcHigherThreadStat(i,qmon[i][inUseThreads],qmon[i][fairShare],qmon[i][qDepth]);
               QWAGlobal.threadConfig[i][0] = qmon[i][minThread];
               QWAGlobal.threadConfig[i][1] = qmon[i][maxThread];
               qwaStats.countAllThreadsInUse(i,qmon[i][inUseThreads],qmon[i][fairShare]);
               qwaStats.countAnyThreadsInUse(i,qmon[i][inUseThreads]);
               qwaStats.countAllWaiters(i,qmon[i][qDepth]);
               qwaStats.countFairShareLimitiation(i,qmon[i][inUseThreads],qmon[i][fairShare],qmon[i][maxThread]);
               inUse += qmon[i][1];
               if (qmon[i][5] > 0)
                   waiters++;
               if (qmon[i][inUseThreads] > 0 ){  // collect queue concurrency
                  //sb.append(i+1).append("; ");             
                  sb.append("A").append(";");              
               } else {
                  sb.append("O;"); 
               }
           }
           String qtoken = sb.toString();
           String vals = "1";
           if (concurrentQs.containsKey(qtoken)) {
              int val = Integer.parseInt((String)concurrentQs.get(qtoken)); 
              vals = String.valueOf(val+1);
           }
           concurrentQs.put(qtoken,vals);
           reportQueueExist(-1);
           if (inUse > maxActiveThreads) {
               maxActiveThreads = inUse;
               maxActiveThreadsTime = tim;
           }    
       } else if (recType ==  QWAGlobal.wfc) {
           wfcLines++;
           int ix = al.indexOf("BP");
           String bpName = (String)al.get(ix+1);
           String q ="";
           if (bpName.startsWith("wfTran")) { 
               bpName = (String)al.get(ix-1);
               //System.out.println("");
           } else {    
               ix = al.indexOf("QUEUE");
               q = (String)al.get(ix+1);
               
           }
           String tst = (String)al.get(18);
           if (!tst.contains("QUEUED(ms)"))
               return;
           int actMs =0;
           try{
               actMs = Integer.parseInt((String)al.get(19));
           } catch(NumberFormatException exp){
               return;
           }     
           //if (QWAGlobal.bpList.containsKey(bpName)) {
           if (QWAGlobal.bpListnew.containsKey(bpName)) {    
               //String oldq = (String)QWAGlobal.bpList.get(bpName);
               listHelper lh = (listHelper)QWAGlobal.bpListnew.get(bpName);
               String oldq = lh.ques;
               int i = oldq.indexOf(q);
               if (i == -1) {
                   q = oldq + " " + q;
                   QWAGlobal.bpList.remove(bpName);
                   QWAGlobal.bpList.put(bpName,q);
                   //System.out.println(q);
               }
               lh.totObserved+=1;
               lh.ques=q;
               if (actMs > lh.maxActive)
                   lh.maxActive = actMs;
               if (actMs < lh.minActive)
                   lh.minActive=actMs;
               lh.totActive += actMs;
               QWAGlobal.bpListnew.put(bpName,lh);
           } else {
               QWAGlobal.bpList.put(bpName,q);
               listHelper lh = new listHelper();
               lh.name=bpName;
               lh.totObserved=1;
               lh.ques=q;
               lh.maxActive = actMs;
               lh.minActive=actMs;
               lh.totActive += actMs;
               QWAGlobal.bpListnew.put(bpName,lh);
           }    
           int qnumber=0;
           try {
               qnumber = Integer.parseInt(str.substring(37,38));
           } catch (NumberFormatException ex) {
               
               //ex.printStackTrace();
           }     
           if (qnumber == 0) 
               onWait++;
           else {
                qs[qnumber-1]++;
                totalWFCs[qnumber-1]++;
           }     
       } else if (recType ==  QWAGlobal.mem) {
           memLines++;
           int ix0 = al.indexOf("FREE(%)");
           String prct = (String)al.get(ix0+1);
           ix0 = prct.indexOf("%");
           if (ix0 != -1)
               prct = prct.substring(0,ix0);
           double freeheap = Double.parseDouble(prct);
           //double freeheap = Double.parseDouble((String)al.get(ix0+1));
           if (freeheap >= 50.0)
               moreThan50prct += 1;
           else if (freeheap < 10.0)
                   lessThan10prct +=1;
           averageHeap += freeheap;
           if (QWAGlobal.procs == null) {
               ix0 = al.indexOf("PROCS");
               QWAGlobal.procs =(String)al.get(ix0+1);
           }
           ix0 =  al.indexOf("TOT(GB)");
           String totSz = (String)al.get(ix0+1);
           if (ix0 != -1)
               totSz = totSz.substring(0,ix0);
           double tmp = Double.valueOf(totSz);
           if (tmp > totMemSize)
               totMemSize = tmp;

      } else if (recType ==  QWAGlobal.hdr) {
           qwaStats.saveHDR(str);
      } else if (recType ==  QWAGlobal.cfg) { 
           configRecord = true;
           new QWAThreadPool().createQTable(str);  
      } else if (recType ==  QWAGlobal.env) {  
           displayEnvironment(str);
      }
    } catch(Exception exp) {
          exp.printStackTrace();
    }  
  }
  private void displayEnvironment(String str) throws Exception {
      int ix = str.indexOf("ENV");
      if (ix == -1)
          return;
      String str1 = str.substring(ix + 4);
      ArrayList al = QWUtil.tokenize(str1,";");
      if (al.size() == 0) {
          return;
      }
      ix = 1;
      GUI.jTextField6.setText((String) al.get(ix));
      GUI.jTextField7.setText((String) al.get(ix + 2));
      GUI.jTextField8.setText((String) al.get(ix + 4));
      GUI.jTextField9.setText((String) al.get(ix + 6));
      GUI.jTextField10.setText((String) al.get(ix + 8));
      GUI.jTextField11.setText((String) al.get(ix + 10));
      GUI.jTextField12.setText((String) al.get(ix + 12));
      GUI.jTextField13.setText((String) al.get(ix + 14));
      
  }
    public void displayReQueued() {

    }
    public void displayCounters(JTextArea jTA) {
      displayCounters(jTA,null);
    }
    public void displayCounters(JTextArea jTA, JTextArea jTA1) {
      jTA.setText("");
      jTA.setFont(new Font("Courier",Font.PLAIN,10));
      jTA.append("ACTIVE BPS:\n");
      jTA1.setText("");      
      jTA1.setFont(new Font("Courier",Font.PLAIN,10));
      //jTA.append("<html><head></head><body>ACTIVE BPS:</body></html>\n");
      double d1=0.0;
      for (int i = 0; i < 9; i++) {
        try {
          d1 = ((QWAGlobal.allThreadsInUse[i] *100 / queLines));
          } catch (java.lang.ArithmeticException exp) {
              
          }
          jTA.append("Number of times ALL threads in Q" + (i+1) + " was in use:  " 
                     + QWAGlobal.allThreadsInUse[i] +  "\t"+ "-" + "\t" + d1 + " %" + "\n");
      }
      jTA.append("\n");
      for (int i = 0; i < 9; i++) {
        d1 = 0.0;  
        try {            
          d1 = ((QWAGlobal.anyThreadsInUse[i] *100 / queLines));
        } catch(Exception exp) {
            
        }  
        jTA.append("Number of times ANY threads in Q" + (i+1) + " was in use:  " 
                   + QWAGlobal.anyThreadsInUse[i] +  "\t" + "-" + "\t" + d1 + " %" + "\n");
      }
      jTA.append("\nBPs WAITING FOR THREAD\n");
      for (int i = 0; i < 9; i++) {
        d1 = 0.0;
        try {
          d1 = (( QWAGlobal.waitersForThreads[i] *100 / queLines));
        } catch(Exception exp) {
            
        }  
        jTA.append("Number of times ANY wfc for Q" + (i+1) + " was waiting for thread:  " 
                   +  QWAGlobal.waitersForThreads[i] +  "\t" + "-" + "\t" + d1 + " %" + "\n");
      }
      jTA.append("\nFAIRSHARE AT WORK ON QUEUES\n");
      for (int i = 0; i < 9; i++) {
        d1 = 0.0;
        try {
          d1 = (( QWAGlobal.fairShareImposed[i] *100 / queLines));
        } catch(Exception exp) {
            
        }  
        jTA.append("Number of times FairShare on Q" + (i+1) + " came into play:  " 
                   +  QWAGlobal.fairShareImposed[i] +  "\t" + "-" + "\t" + d1 + " %" + "\n");
      }
      if (memLines > 0 ) { 
        jTA1.append("\nHEAP INFORMATION\n");
        jTA1.append(" Total Heap Size :" + "\t\t" + totMemSize + "GB\n");
        jTA1.append(" # of times free HEAP was over  50 %:  " + "\t" +  moreThan50prct + " (" + (moreThan50prct * 100 / memLines) + " %" + ")\n");
        jTA1.append(" # of times free HEAP was below 10 %:  " + "\t" +  lessThan10prct + " (" + (lessThan10prct * 100 / memLines) + " %" + ")\n");
        String tmp = String.valueOf(Math.ceil(averageHeap / memLines));
        jTA1.append("The avarage free heap size :  " +  "\t\t" + tmp + "%\n");
        jTA1.append(" # of CPUs :\t\t\t" + QWAGlobal.procs + " (total machine)");
      } else 
        jTA1.append("\nNO HEAP INFORMATION available\n");
    }
    /**
     * starts a html document
     * @param b
     */
    protected void htmlBegEnd(boolean b) {
      if (!html) return;
      if (b) 
        queFile.println("<html><head></head><body>");
      else 
        queFile.println("</body></html>");  
    }
    /**
     * 
     * @param str
     * @param html
     * @param inx
     * @param threshold
     * @return
     */
    public String splitQueues(String str, boolean html,int inx,int threshold ) {
        ArrayList al = QWUtil.tokenize(str);
        createQMatrix(al);
        StringBuilder sb = new StringBuilder();
        //
        String tim = (String)al.get(2); 
        if (html) {
            sb.append("<tr><th>");
        }
        sb.append(tim);
        //sb.append(str.substring(0,17));
        sb.append((String)al.get(0)).append(" ").append((String)al.get(1));
        if (html) 
            sb.append("</th>");
        else 
            sb.append("  ");
        boolean gotSome = false;
        for (int i = 0; i < 9; i++) {
 //           interValStat(i,qmon[i][5],tim);
//            calcHigherThreadStat(i,qmon[i][1],qmon[i][2],qmon[i][5]);
            if (qmon[i][inx] >= threshold) {
                if (html) {
                    sb.append("<td>Q: " + (i+1) + " d: " + qmon[i][5] + " t: " + qmon[i][1] + " c: " + + qmon[i][2] +"</td>"); 
                } else {
                   if (gotSome) sb.append(" -- "); 
                   sb.append("Q: " + (i+1) + " d: " + qmon[i][5] + " t: " + qmon[i][1] + " c: " + + qmon[i][2] +  "  ");
                }
                gotSome = true;
            }
        }
        if (gotSome) {
            if (html) sb.append("</tr>");
            return sb.toString();
        }
        return null;
    } 
    /**
     * 
     */
    public void splitFile() {
      if (html) {
         queFile.println("<h2>Queues with Waiting BPs higher than " + depth + "</h2>");
         queFile.println("<h4> Q: Queue  d: BPs on queue   t: active threads   c: calculated fairness</h4>");
         queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
         queFile.println("<font size='1'>");
      } else {
          jTA1.setFont(new Font("Courier",Font.PLAIN,12)); 
          jHdr.setFont(new Font("Verdana",Font.PLAIN,14));
          jHdr.setText("Queues with with " + depth + " or more waiting BP's --- Q: queue   d: depth    t: threads   c: fairness");
      }   
      String inpr;
      while ((inpr = QWUtil.readLine()) != null) {
                 countLines++; 
                 int recType = QWUtil.getRecordType(inpr);
                 if (recType ==  QWAGlobal.wfc) {
                    if (wfdPrt)
                       wfdFile.println(inpr);
                 } else if (recType ==  QWAGlobal.que) {
                    if (quePrt) {
                       boolean mod = html ? true : false; 
                       String str = splitQueues(inpr,mod,5,depth);
                       if (str != null) {
                          if (html)
                              queFile.println(str); 
                          else 
                              jTA1.append(str + "\n");  
                       }
                    }   
                 } 
             }
             if (html) {
                queFile.println("</font></table>");  
             }
    return;
    }
    /**
     * 
     * @param jta q , chunk
     */
    public void ListQueueProgress(int q , int chunk,javax.swing.JTextArea jta) {
      String inpr;
      boolean add = false;
      int prevDepth = 0;
      jta.setText("");
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {
                ArrayList al = QWUtil.tokenize(inpr);
                createQMatrix(al);
                //String tim = inpr.substring(17,26);
                String tim = (String)al.get(2);
                if (qmon[q][5] > 0 && prevDepth ==0 && qmon[q][5] > chunk) {
                   prevDepth = qmon[q][5];
                   add = true;   
                } else if (qmon[q][5] > 0 && prevDepth !=0 ) {
                     if (prevDepth < qmon[q][5] ){
                         prevDepth = qmon[q][5];
                         add = true;
                     } else {
                       if ((prevDepth - qmon[q][5]) > chunk) {
                           prevDepth = qmon[q][5];
                           add = true;
                       }
                    }
                } else if (qmon[q][5] == 0 && prevDepth !=0 ) {
                    prevDepth = 0;
                    add = true;
                }
                if (add) {
                   StringBuilder sb = new StringBuilder();
                   sb.append(tim).append(" ").append("Queue: ");
                   sb.append(q+1).append("\t").append("QueueDepth: ").append(prevDepth);
                   jta.append(sb.toString()+ "\n");
                   add = false;
                }
            }
      }
    }
    public void listQueueInfo(javax.swing.JTextArea jta) {
      String inpr;
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {
                String s1 = inpr.replaceAll("QUEUE ", "Q");
                jta.append(s1 + "\n");

            }
      }
    }
    public void reportQueues() {
      String inpr;
      int row = 0; 
      QWWaiters waiterDLG = new QWWaiters(GUI,"Queue with waiting WFC's",true,waiters);
 //     waiterDLG.createTable(waiters);
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {
                ArrayList al = QWUtil.tokenize(inpr);
                createQMatrix(al);
                //String tim = inpr.substring(17,26);
                String tim = (String)al.get(2);
                //String tim = inpr.substring(17,26);
                createQMatrix(al);
                for (int i = 0; i < 9; i++) {
                    if (qmon[i][5] > 0) {
                        waiterDLG.updateTable(row,tim,i+1,qmon[i][1],qmon[i][2],qmon[i][5]);
                        row++;
                    }
                }
            }
       }
       waiterDLG.createTable(waiters); 
       waiterDLG.setVisible(true);
    }
    private void createWFDs(ArrayList alStrings,ArrayList alhelper, String mem) {
     String nextTime = " ";
     int prevQueue = 0;
     for (int i = 0; i < alStrings.size(); i++) {
         String inpr = (String)alStrings.get(i);
         Qhelper qh = new Qhelper();
         if (i ==0 ) qh.setMem(mem);
         int inx = 2;
         //String tim = inpr.substring(17,26);
         qh.setTime("");
         ArrayList al = QWUtil.tokenize(inpr);
         String tim = (String)al.get(2);
         if (!tim.equalsIgnoreCase(nextTime))
            qh.setTime((String)al.get(inx));
          String q = (String)al.get(inx+3);
         if (q.length() > 1) continue;
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
            if (qinx < 10 && qinx >= 0 ) {
               int trds = qmon[qinx-1][1];
               if ( trds != 0 && prevQueue != qinx) {
                  qh.setThread(String.valueOf(qmon[qinx-1][1]));
                  qh.setCalc(String.valueOf(qmon[qinx-1][2]));
                  qh.setWaiters(String.valueOf(qmon[qinx-1][5]));
                  prevQueue = qinx;
               } else {
               }
            }
            nextTime = tim;
            alhelper.add(qh);
         }
      } // end for
    }
    /**
     * 
     * @param al
     */
    public void listWFDs(ArrayList al, String header ) {
     String inpr;
     QWWfc qwfc = new QWWfc(GUI,"WFD List",false,header);
     int rows = al.size();
     qwfc.createTable(rows);
     int rec  = 0;
     Iterator it =  al.iterator();
     while(it.hasNext()) {
        Qhelper qh = (Qhelper)it.next();
        qwfc.updateTable(rec,
                qh.getTime(),
                qh.getQ(),
                qh.getThread(),
                qh.getCalc(),
                qh.getWaitersw(),
                qh.getWfid(),
                qh.getStep(),
                qh.getAct(),
                qh.getMem(),
                qh.getBPName());
        rec++;
     }     
     qwfc.setVisible(true);
    }
    /**
     * 
     * @param threshhold - list if higher than this value
     * @param index      - 1 = active threads , 2 = waiting for threads
     */
    public void listWFDs(int threshold,int index) {
      String inpr;
      String freeMem = " na ";
      ArrayList alhelper = new ArrayList();
      ArrayList alString = null;
      boolean candidate = false;
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {
                if (alString != null){
                    createWFDs(alString,alhelper,freeMem);
                    alString = null;
                }
                candidate=false;
                createQMatrix(inpr);
                for (int i = 0; i < 9; i++) {
                    if (qmon[i][index] >= threshold) {    // can be queue or activity
                        candidate=true;
                        alString = new ArrayList();
                        break;
                    }
                }
            } else if (recType ==  QWAGlobal.wfc) {
                if (candidate) {
                    if ( alString == null)
                        alString = new ArrayList();
                    alString.add(inpr);
                }
            //}
            } else if (recType ==  QWAGlobal.mem) {
                     int ix = inpr.indexOf("(%)",75);
                     int ix1 = inpr.indexOf(" ",ix+6);   // Get end of value
                     freeMem = inpr.substring(ix+4,ix1);
            } // end mem
        } // end while
        StringBuilder sb = new StringBuilder();
        sb.append(" List WFCs for all Queues if ");
        sb.append(threshold).append(" or more are ");
        if (index == 5)
           sb.append(" Waiting for thread");
        else sb.append(" Active");
        sb.append(" on a Queue");
        listWFDs(alhelper,sb.toString());
    }
    /**
     * Detect WFC's active accross multiple sampling intervals
     * @param threshhold - list if higher than this value
     * @param index      - 1 = active threads , 2 = waiting for threads
     */
    public void listLongRunningWFDs(int mseconds) {
      String inpr;
      String nextTime = " ";
      String tim= null;
      String freeMem = " na ";
      ArrayList al = null;
      ArrayList alhelper = new ArrayList();
      ArrayList alString = null;
      boolean candidate = false;
      Qhelper qh = null ;
      Qhelper qhsave = null;
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {
                String tmpQue = inpr;
                candidate=false;
                ArrayList alque = QWUtil.tokenize(inpr);
                //tim = inpr.substring(17,26);
                tim = (String)alque.get(2);
                createQMatrix(alque);
/*
                for (int i = 0; i < 9; i++) {
                    if (qmon[i][index] >= threshold) {    // can be queue or activity
                        candidate=true;
                        alString = new ArrayList();
                        break;
                    }
                }
 */
             } else if (recType ==  QWAGlobal.wfc) {
                 if ( alString == null)
                   alString = new ArrayList();
                  alString.add(inpr);
            //}
            } else if (recType ==  QWAGlobal.mem) {
                     int ix = inpr.indexOf("(%)",75);
                     int ix1 = inpr.indexOf(" ",ix+6);   // Get end of value
                     freeMem = inpr.substring(ix+4,ix1);
            } // end mem
        } // end while
        StringBuilder sb = new StringBuilder();
        listWFDs(alhelper,sb.toString());
    }

    public void listEachQueue(int queue) {
      String inpr;
      String nextTime = " ";
      String tim= null;
      int row = 0;
      ArrayList alhelper = new ArrayList();
      Qhelper qh = null ;
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.que) {
               createQMatrix(inpr); 
            } else if (recType ==  QWAGlobal.wfc) {
                ArrayList al = QWUtil.tokenize(inpr);
                int inx = 2;
                int qu = 0;
                try {
                    qu = Integer.parseInt((String)al.get(inx+3));
                } catch (NumberFormatException ex) {
               //ex.printStackTrace();
                }     
                if (qu == queue) {
                    qh = new Qhelper();
                    tim = (String)al.get(inx);
                    qh.setTime("");
                    if (!tim.equalsIgnoreCase(nextTime))
                        qh.setTime((String)al.get(inx));
                    qh.setQ((String)al.get(inx+3));
                    qh.setWfid((String)al.get(inx+7));
                    qh.setStep((String)al.get(inx+9));
                    String act = (String)al.get(inx+15);
                    if (act.startsWith("-1")) {
                       qh.setAct((String)al.get(inx+17));
                       qh.setBPname((String)al.get(inx+19));
                    } else {
                       qh.setAct((String)al.get(inx+15));
                       qh.setBPname((String)al.get(inx+17));
                    }
                   int qinx = Integer.parseInt(qh.getQ());
                   qh.setThread(String.valueOf(qmon[qinx-1][1]));
                   qh.setCalc(String.valueOf(qmon[qinx-1][2]));
                   qh.setWaiters(String.valueOf(qmon[qinx-1][5]));
                   nextTime = tim;
                   alhelper.add(qh);
                }
            }    
        }
        String header = "List all WFC's on Queue " + queue;
        listWFDs(alhelper,header);
    }  
    public String reportQAndWFC() {
      StringBuffer sb = new StringBuffer();
      sb.append("<h2>Active queues with ").append(depth).append("  or more waiting BP's</h2>");
      String inpr;
      boolean expectWFC = false;
      while ((inpr = QWUtil.readLine()) != null) {
               countLines++;
               int recType = QWUtil.getRecordType(inpr);
               if (recType ==  QWAGlobal.wfc) {
                  if (expectWFC){
                      sb.append("<br>").append(inpr);
                      if (debug) System.out.println(inpr);
                  }
               } else if (recType ==  QWAGlobal.que) {
                   expectWFC=false;
                   String str = splitQueues(inpr,false,5,depth);
                   if (str != null) {
                      sb.append("<br><b>").append(str).append("</b>");
                      if (debug) System.out.println(str);
                   }
                   expectWFC = true;
               }
      }
      sb.append("</font></table>");
      return sb.toString();
    }

    /**
     * }
     * 
    */
    public void reportQAndWFC(JTextArea jTA1) {
      if (html) {
          queFile.println("<h2>Active queues with " + depth + "  or more waiting BP's</h2>");
/*          
         queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
         queFile.println("<font size='1'>");
 */ 
      } else {
//          jHdr.setFont(new Font("Verdana",Font.PLAIN,14));
//          jHdr.setText("Active queues with " + depth + "  or more waiting BP's");
      }
 //     int countLines=0;
      String inpr;
      boolean expectWFC = false;
      while ((inpr = QWUtil.readLine()) != null) {
               countLines++; 
               int recType = QWUtil.getRecordType(inpr);
               if (recType ==  QWAGlobal.wfc) {
                  if (expectWFC){
                      if (html) { 
                          queFile.println("<br>" + inpr);
                      } else {
                          int inx = inpr.indexOf("WFC");
                          jTA1.append("   " + inpr.substring(inx) +"\n"); 
                          if (debug) System.out.println(inpr);
                      }    
                  }   
               } else if (recType ==  QWAGlobal.que) {
                   expectWFC=false;
                   String str = splitQueues(inpr,false,5,depth);
                   if (str != null) {
                      if (html) {
                         queFile.println("<br><b>" + str + "</b>");  
                      } else {
                         jTA1.append(str + "\n");  
                         if (debug) System.out.println(str);
                      }
                       expectWFC = true;
                   }   
               }
           }
           if (html)
              queFile.println("</font></table>");  
    return;
    }
    public void reportActiveQAndWFC(JTextArea jTA1) {
      String s_hdr = "-------Report queues with active threads (BPs)---";
      if (html) {
          queFile.println("<h2>Queues with more than  " + lowerThreadLimit + " active threads  (BPs)</h2>");
      } else {
//          jHdr.setFont(new Font("Verdana",Font.PLAIN,14));//
//          jHdr.setText("Queues with " + lowerThreadLimit + " or more active threads (BPs)");
      }
 //     int countLines=0;
      String inpr;
      boolean expectWFC = false;
      while ((inpr = QWUtil.readLine()) != null) {
               countLines++; 
               int cnt = 0;
               int recType = QWUtil.getRecordType(inpr);
               if (recType ==  QWAGlobal.wfc) {
                  if (expectWFC){
                      if (html) { 
                          queFile.println("<br>" + inpr);
                      } else {
                          int inx = inpr.indexOf("WFC");
                          jTA1.append("   " + inpr.substring(inx) +"\n"); 
                      }    
                      //wfdFile.println(inpr);
                  }   
               } else if (recType ==  QWAGlobal.que) {
                   expectWFC=false;
                   createQMatrix(inpr);
                   int inUse = 0;
                   for (int i = 0; i < 9; i++) {
                       inUse += qmon[i][1];
                   }
                   if (inUse >= lowerThreadLimit && inUse > 0) {
                        String tim = inpr.substring(17,26);
                        if (html) {
                           queFile.println("<br><b>" + tim + "  GIS is running " + inUse + " BP's at this time</b>");  
                        } else
                            jTA1.append(tim + "  GIS is running " + inUse + " BP's at this time\n");
                        expectWFC=true;
                   }
               }
           }
//           queFile.println("</font></table>");  
    return;
    }
    public void reportMemoryFlux() {
      if (html) {
          queFile.println("<h2>Memory Expansion / Contraction during the sampling</h2>");
          queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
          queFile.println("<font size='1'>");
      }
      String tim = null;
      String inpr = null;
      String txt =null;
      double totMem= 0;
      double lastMemory = 0;
      boolean firstRecord = true;
      boolean hitChange = false;
      ArrayList al = new ArrayList();
      while ((inpr = QWUtil.readLine()) != null) {
              int recType = QWUtil.getRecordType(inpr);
              if (recType ==  QWAGlobal.mem) {
                  tim = inpr.substring(17,26);
                  int ix = inpr.indexOf("B)",31);
                  int ix1 = inpr.indexOf(" ",ix+5);   // Get end of value
                  String name = inpr.substring(ix+3,ix1);
                  ix = inpr.indexOf("(%)",75);
                  ix1 = inpr.indexOf(" ",ix+6);   // Get end of value
                  String freeMem = inpr.substring(ix+4,ix1);
                  hitChange = true;
                  try {
                      totMem = Double.parseDouble(name);
                  } catch (Exception exp) {
                       totMem = 50.0; 
                  } 
                 if (firstRecord) {
                     txt = "Started: ";
                     firstRecord = false;
                     lastMemory = totMem;
                 } else  if (lastMemory < totMem) {
                     txt = "Expansion: ";
                     lastMemory = totMem;
                 } else  if (lastMemory > totMem) {
                     txt = "Contraction: ";
                     lastMemory = totMem;
                 } else 
                     hitChange = false;    
                 if (hitChange) {  
                    if (html) {
                       queFile.println("<tr><td>" + tim + "</td>");   
                       queFile.println("<td>  " +  txt +  name + " GB</td>");
                       queFile.println("<td> and had Total Free: " + freeMem + " %</td><tr>");
                    //} else
                    }   
                    al.add(tim + ";" + txt + ";" + name + ";" +  freeMem);
//                        jTA1.append(tim + txt + name + " GB   and had Total Free: " + freeMem +"%\n");                        
                 }
              }
        }
        QWHeapChg QWheap = new QWHeapChg(GUI,"Heap fluctuation",false);
        QWheap.createTable(al.size());
        QWheap.updateTable(al);
        QWheap.setVisible(true);
        if (html) {
            queFile.println("</font></table>");  
        }
   }
   public void reportMemory() {
       reportMemory(100.0);
   } 
   /**
     *  MEMORY - display free memory below Threshold   
     */
    public void reportMemory(double memLimit) {
      QWMemory QWmem = null; 
      if (html) {
          queFile.println("<h2>Free Heap Memory lower than " + memThreshold + " %</h2>");
          if (memLines == 0) {
              queFile.println("<h2>No Memory records found in sample</h2>");
              return;
          }
          queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
          queFile.println("<font size='1'>");
      } else {
            QWmem = new QWMemory(GUI,"Memory Usage",false);
//            QWmem.createTable(memLines+1,memThreshold);
             QWmem.createTable(memLines+1,memLimit);
      }
      String str = null;
      double freeMem= 0;
      ArrayList al = new ArrayList();
      int count = 0;
      while ((str = QWUtil.readLine()) != null) {
              String tim = str.substring(17,26);
              int recType = QWUtil.getRecordType(str);
              if (recType ==  QWAGlobal.mem) {
                 al = QWUtil.tokenize(str);
                 int size = al.size();
                 String name = (String)al.get(size-3);
                 try {
                     freeMem = Double.parseDouble(name); 
                 } catch (Exception exp) {
                     freeMem = 50.0;
                 }    
                 if (memLimit > freeMem) {
//                 if (100.0 > freeMem) {    
                    if (html) {
                       queFile.println("<tr><td>" + (String)al.get(2) + "</td>");   
                       queFile.println("<td> Total Heap size " + (String)al.get(5) + " GB</td>");
                       queFile.println("<td> Total Free: " + name + " %</td><tr>");
                    } else
                       QWmem.updateTable(count++,(String)al.get(1),(String)al.get(2),(String)al.get(5),name);
                 } 
              }
        }
        if (html) 
            queFile.println("</font></table>");  
        else  QWmem.setVisible(true);
    }
    private String getTimeInQueue(long millis){
        String s = null;
          try {
               s = String.format("%d hrs %d min, %d sec", 
                                    TimeUnit.MILLISECONDS.toHours(millis),
                                    TimeUnit.MILLISECONDS.toMinutes(millis) - 
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                    TimeUnit.MILLISECONDS.toSeconds(millis) - 
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                                                                                         

          } catch (Exception ex) {
              Logger.getLogger(QWAnalyze.class.getName()).log(Level.SEVERE, null, ex);
          }
          
       return s;
    }
    private long getTimeInMillis(String d, String t){
       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
       format.setTimeZone(TimeZone.getTimeZone("UTC"));
       Date date = null;
       long millis = 0l;
       try {
           date = format.parse(d + " " + t);
           millis = date.getTime();  
       } catch(Exception exp) {
           
       }    
       return millis;
    }
    /**
     * Find intervall for queue - keep highest number for intervall
     * @param wh
     * @param q
     * @param depth
     */
    private void checkIfQueueExist(WaitHelper wh,int q,int depth) {
      if (depth > 0) {
          if (wh.qStart[q] == null) {
              wh.qStart[q] = currentQTime;
              wh.qDateStart[q] = currentQDate;
              wh.sz[q] = depth;
              wh.minsz[q] = depth;
              wh.maxsz[q] = depth;
          } else {
              if (wh.minsz[q] > depth && depth != 0){
                  wh.minsz[q] = depth;
              } else {
                  if (wh.maxsz[q] < depth)
                      wh.maxsz[q] = depth;
              }
          }
      } else if (depth == 0) {
          if (wh.qStart[q] != null) 
              wh.qEnd[q] = currentQTime;
              wh.qDateEnd[q] = currentQDate;
      }   
    }
    public void reportQueueExist(int q) {
       for (int i = 0; i < 9; i++) {
             checkIfQueueExist(wh, i, qmon[i][qDepth]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (wh.qEnd[i] != null) {
                    sb.setLength(0);
                    sb.append("   ").append(i + 1);
                    sb.append("\t");
                    sb.append(wh.qDateStart[i]);
                    sb.append("\t");
                   // sb.append("wait detected: \t");
                    sb.append(wh.qStart[i]);
                    sb.append("\t");
                    //sb.append("gone at: \t");
                    sb.append(wh.qEnd[i]);
                    sb.append("\t");
                    long ms = getTimeInMillis(wh.qDateStart[i],wh.qStart[i]);
                    long ms1 = getTimeInMillis(wh.qDateEnd[i],wh.qEnd[i]);
                    sb.append(getTimeInQueue(ms1-ms));
                    //sb.append("Initial Depth: \t");
                    sb.append("\t");
                    sb.append(wh.sz[i]);
                    sb.append("\t");
                    //sb.append("Minimum depth: \t");
                    sb.append(wh.minsz[i]);
                    sb.append("\t");
                    //sb.append("Maximum Depth: \t");
                    sb.append(wh.maxsz[i]);
                    sb.append("\n");
                    if (q == -1 || q == (i+1))
                       jtWaiters.append(sb.toString());
                    wh.qEnd[i] = null;
                    wh.qStart[i] = null;
                }
            }
    }
    public void reportQueueExist(JTextArea jTA,JLabel jl, int q) {
      String inpr;
      if (wh == null)
          wh = new WaitHelper();
      if (jtWaiters == null)
          jtWaiters = jTA;
      if (jlWaiters == null)
          jlWaiters = jl;
      jtWaiters.setText("");
      jlWaiters.setText("                 Time It Takes To Clear a Queue (betweeen samples)");
      while ((inpr = QWUtil.readLine()) != null) {
        int recType = QWUtil.getRecordType(inpr);
        if (recType ==  QWAGlobal.que) {
            createQMatrix(inpr);
            reportQueueExist(q);
        }
      }
    }
  
    
   public void reportHTMLQueueExist(int q) {
      String inpr;
      closeQWFile();
      openQWFile();        
      StringBuilder sb = new StringBuilder();
      sb.append("<th>Que</th><th>QDate</th><th>QStart</th><th>QEnd</th><th>QLasted</th><th>InitSize</th><th>MinSize</th><th>MaxSize</th>");
      QWAGlobal.htmlFile.println(sb.toString());
      if (wh == null)
          wh = new WaitHelper();
      //jlWaiters.setText("                 Time It Takes To Clear a Queue (betweeen samples)");
      while ((inpr = QWUtil.readLine()) != null) {
        int recType = QWUtil.getRecordType(inpr);
        if (recType ==  QWAGlobal.que) {
            createQMatrix(inpr);
        }
        for (int i = 0; i < 9; i++) {
             checkIfQueueExist(wh, i, qmon[i][qDepth]);
        }
        for (int i = 0; i < 9; i++) {
            if (wh.qEnd[i] != null) {
                    sb.setLength(0);
                    sb.append("<tr><td>").append(i + 1);
                    sb.append("</td><td>");
                    sb.append(wh.qDateStart[i]);
                    sb.append("</td><td>");
                   // sb.append("wait detected: \t");
                    sb.append(wh.qStart[i]);
                    sb.append("</td><td>");
                    //sb.append("gone at: \t");
                    sb.append(wh.qEnd[i]);
                    sb.append("</td><td>");
                    long ms = getTimeInMillis(wh.qDateStart[i],wh.qStart[i]);
                    long ms1 = getTimeInMillis(wh.qDateEnd[i],wh.qEnd[i]);
                    sb.append(getTimeInQueue(ms1-ms));
                    //sb.append("Initial Depth: \t");
                    sb.append("</td><td>");
                    sb.append(wh.sz[i]);
                    sb.append("</td><td>");
                    //sb.append("Minimum depth: \t");
                    sb.append(wh.minsz[i]);
                    sb.append("</td><td>");
                    //sb.append("Maximum Depth: \t");
                    sb.append(wh.maxsz[i]);
                    sb.append("</td></tr>");
                    if (q == -1 || q == (i+1))
                       QWAGlobal.htmlFile.println(sb.toString());
                    wh.qEnd[i] = null;
                    wh.qStart[i] = null;
                }
            }
      }
      QWAGlobal.htmlFile.println("</table>");
    }    
    /**
    * Create Timeseries for active queues
    * @return
    */
//    public XYDataset createMemoryDataset( String header) {
  public TimeSeriesCollection createMemoryDataset( String header) {
      TimeSeries inUse = new TimeSeries("In Use", Second.class);
      TimeSeries inTotal = new TimeSeries("Total Comitted", Second.class);
      String inpr = null;
      String prevTim = "";
      while ((inpr = QWUtil.readLine()) != null) {
             int recType = QWUtil.getRecordType(inpr);
             if (recType ==  QWAGlobal.mem) {
                 boolean dropThis = false;
                 String tim = inpr.substring(17,22);
                 int year = Integer.parseInt(inpr.substring(6,10));
                 int month = Integer.parseInt(inpr.substring(11,13));
                 int wday = Integer.parseInt(inpr.substring(14,16));
                 int hh = Integer.parseInt(inpr.substring(17,19));
                 int mm = Integer.parseInt(inpr.substring(20,22));
                 int ss = Integer.parseInt(inpr.substring(23,25));
                 int ix = inpr.indexOf("TOT(GB)",20);
                 int ix1 = inpr.indexOf("TOT",ix+8);   // Get end of value
                 String m1 = inpr.substring(ix+7,ix1).trim();
                 if (m1.indexOf(".") == -1) dropThis = true;   // ***** some problem with QWW
                 String m2 = removeSpace(m1);
                 double commitedMem = Double.valueOf(m2);
//                 String name = inpr.substring(ix+3,ix1);
                 ix = inpr.indexOf("(%)",75);
                 //ix1 = inpr.indexOf(" ",ix+6);   // Get end of value
                 ix1 = inpr.indexOf("%",ix+6);   // Get end of value
                 if (ix1 == -1)
                     ix1 = inpr.indexOf("P",ix+6);
                 String freeMem = removeSpace(inpr.substring(ix+4,ix1));

                 try {
                     double occMem = (100.00 -(Double.valueOf(freeMem)));
           //        if (!tim.equalsIgnoreCase(prevTim) || !dropThis) {
                      //inUse.add(new Second(ss,mm,hh,wday,month,year),(commitedMem*(occMem/100)));
                      inUse.addOrUpdate(new Second(ss,mm,hh,wday,month,year),(commitedMem*(occMem/100)));
                      //inTotal.add(new Second(ss,mm,hh,wday,month,year),(commitedMem));
                      inTotal.addOrUpdate(new Second(ss,mm,hh,wday,month,year),(commitedMem));
             //      }
                 } catch(Exception exp) {
                    exp.printStackTrace();
                 }
                 prevTim = tim;
               }
             } // end while
             TimeSeriesCollection dataset = new TimeSeriesCollection();
             dataset.addSeries(inTotal);
             dataset.addSeries(inUse);
             return dataset;
      }
    /**
     * 1=min,2=used,3=calc,4=pool,5=max,6=dept
     * sel 0 = min , 1 = used ,2 = calc .....
    * Create Timeseries for active queues
    * @return
    */
    public XYDataset createActiveThreadDataset(int sel, String header) {
     return createActiveThreadDataset(sel,header,-1);   
    } 
    public XYDataset createActiveThreadDataset(int sel, String header,int q) {
      TimeSeries s1 = new TimeSeries(header, Second.class);
      String inpr = null;
      while ((inpr = QWUtil.readLine()) != null) {
                int recType = QWUtil.getRecordType(inpr);
                if (recType ==  QWAGlobal.que) {
                   int year = Integer.parseInt(inpr.substring(6,10));
                   int month = Integer.parseInt(inpr.substring(11,13));
                   int wday = Integer.parseInt(inpr.substring(14,16));
                   int hh = Integer.parseInt(inpr.substring(17,19));
                   int mm = Integer.parseInt(inpr.substring(20,22));
                   int ss = Integer.parseInt(inpr.substring(23,25));
                   createQMatrix(inpr);
                   int inUse = 0;
                   for (int i = 0; i < 9; i++) {
                       if (q == -1) {
                          inUse += qmon[i][sel];
                       } else {
                           if (i == q-1) {
                              inUse += qmon[i][sel];
                              break;
                           }   
                       }
                   }
                   try { 
                       //if (inUse > 0 && inUse >= lowerThreadLimit) {
                       if (inUse >= lowerThreadLimit) {    
                           s1.add(new Second(ss,mm,hh,wday,month,year),inUse);
                        }
                   } catch (org.jfree.data.general.SeriesException exp) {    
                          s1.update(new Second(ss,mm,hh,wday,month,year),inUse); 
                   }
               }
           }    
        TimeSeriesCollection dataset = new TimeSeriesCollection(s1);
       return dataset;        
    }
   /**
    * Create Timeseries for active queues
    * @return
    */
    public XYDataset createTestThreadDataset(int index) {
      String inpr = null;
      TimeSeries []ts = {null,null,null,null,null,null,null,null,null};
      //TimeSeries s1 = new TimeSeries("Active Queue Threads", Second.class);
      while ((inpr = QWUtil.readLine()) != null) {
                 int recType = QWUtil.getRecordType(inpr);
                 if (recType ==  QWAGlobal.que) {
                     String tim = inpr.substring(17,22);
                     int year = Integer.parseInt(inpr.substring(6,10));
                     int month = Integer.parseInt(inpr.substring(11,13));
                     int wday = Integer.parseInt(inpr.substring(14,16));
                     int hh = Integer.parseInt(inpr.substring(17,19));
                     int mm = Integer.parseInt(inpr.substring(20,22));
                     int ss = Integer.parseInt(inpr.substring(23,25));
                     createQMatrix(inpr);
                     int inUse = 0;
                     for (int i = 0; i < 9; i++) {
                         inUse = qmon[i][index];
                         // HACK HACK HACK
                         if (inUse > -1) {
                             //if (inUse > 300) inUse = 500;
                             try {
                               if (ts[i] == null)
                                   ts[i] = new TimeSeries("Q " + (i+1) + " ", Second.class);
                               ts[i].add(new Second(ss,mm,hh,wday,month,year),inUse);
                            } catch (org.jfree.data.general.SeriesException exp) {
                                ts[i].update(new Second(ss,mm,hh,wday,month,year),inUse);
                                System.out.println(inpr);
                            }    
                    }
                 }
             }
         }        
         TimeSeriesCollection dataset = new TimeSeriesCollection();
         for (int i = 0; i < 9; i++) {
             if (ts[i] != null)
                 dataset.addSeries(ts[i]);
         }
    return dataset;        
    }
   /**
    * Create Timeseries for active queues
    * @return
    */
    public XYDataset checkAQueueDataset(int q) {
      String inpr = null;
      TimeSeries s1 = new TimeSeries("Active Threads Q " + q, Second.class);
      while ((inpr = QWUtil.readLine()) != null) {
                 int recType = QWUtil.getRecordType(inpr);
                 if (recType ==  QWAGlobal.que) {
                     String tim = inpr.substring(17,22);
                     int year = Integer.parseInt(inpr.substring(6,10));
                     int month = Integer.parseInt(inpr.substring(11,13));
                     int wday = Integer.parseInt(inpr.substring(14,16));
                     int hh = Integer.parseInt(inpr.substring(17,19));
                     int mm = Integer.parseInt(inpr.substring(20,22));
                     int ss = Integer.parseInt(inpr.substring(23,25));
                     createQMatrix(inpr);
                     if (qmon[q][1] > 0) {
                         s1.add(new Second(ss,mm,hh,wday,month,year),qmon[q][1]);         
                     }
                 }
            }    
            TimeSeriesCollection dataset = new TimeSeriesCollection(s1);
            return dataset;        
    }
    /**
     * 
     * @return JFreeChart dataset
     */
    public DefaultCategoryDataset buildActiveChart(int q) {
      String serie = null;
      if (q == -1) {
         serie = "All Queues";
      } else  
          serie = "Q" + q; // series[q];
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      String inpr = null;
      boolean firstCheck = true;
      String cat = null;
      int now = 0;
      int timeGap = 0;
      int prev = 0;
      while ((inpr = QWUtil.readLine()) != null) {
               int recType = QWUtil.getRecordType(inpr);
               if (recType ==  QWAGlobal.que) {
                   String tim = inpr.substring(17,22);
                   now = Integer.parseInt(tim.substring(4,5));   
                   if (timeGap < 20)
                       cat = " ";
                   else cat = tim;
                   createQMatrix(inpr);
                   if (firstCheck) {
                       cat = tim;
                       dataset.addValue(qmon[0][1],serie,cat);
                       firstCheck=false;
                   } 
                   int inUse = 0;
                   for (int i = 0; i < 9; i++) {
                       inUse += qmon[i][1];
                   }
                   if (inUse > 0 && inUse >= lowerThreadLimit) {
                      dataset.addValue(inUse,serie,cat);
                      cat ="";
                   }
                 }
                 timeGap = (timeGap < 20) ? ++timeGap : 0;
                 if (now > prev)
                    prev = now + timeGap;
             }
      return dataset;
    }    
    /**
     * 
     * @return JFreeChart dataset
     */
    public DefaultCategoryDataset buildBarChart() {
      String inpr = null;
      boolean firstCheck = true;
      String cat = null;
      int prev = 0;
      int timeGap = 0;
      int now =0;
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      while ((inpr = QWUtil.readLine()) != null) {
               int recType = QWUtil.getRecordType(inpr);
               if (recType ==  QWAGlobal.wfc) {
               } else if (recType ==  QWAGlobal.mem) {
               } else if (recType ==  QWAGlobal.que) {
                 String tim = inpr.substring(17,22);
//                 now = Integer.parseInt(tim.substring(0,2))*60 + Integer.parseInt(tim.substring(4,5));  
                now = Integer.parseInt(tim.substring(4,5));   
                //if (tim.equalsIgnoreCase(prev))
                if (now == prev)
                     cat = ".";
                 else cat = tim;
                 createQMatrix(inpr);
                 if (firstCheck) {
                     dataset.addValue(qmon[0][5],series[0],cat);
                     firstCheck=false;
                 } 
                 for (int i = 0; i < 9; i++) {
                     if (qmon[i][5] > depth) {
                         dataset.addValue(qmon[i][5],series[i],cat);
                         cat ="";
                     }
                 }
               }
               if (now > prev)
                   prev = now + timeGap;
           }
      return dataset;
    }
    /**
     *  Create Report header
     */
    public void printHTMLhdr() {
       queFile.println("<h1>Start of queue analysis</h1>");
       queFile.println("<h3>Sampling Started : " + QWAGlobal.start + "</h3>");
       queFile.println("<h3>Sampling Ended   : " + QWAGlobal.last + "</h3>");
       queFile.println("<h3>Samples taken: " + countLines + "</h3>");
    }

    public void listHistogram() {
        QWHistogram QWhist = new QWHistogram(GUI,"Wait count pr. Queue",false);
        QWhist.setThreadConfig();
        QWhist.refreshHistoGram();
        QWhist.refreshPeakWaitWFC();
        QWhist.refreshUnderScedules();
        QWhist.FairShare(QWAGlobal.queList);
        QWhist.setVisible(true);
    }
 
    /**
     * printReport - do various reports in debug mode and text editor
     */
    public void printReport() {
        if (html) {
            new QWCreateHTML().printHTMLReport(queFile);
            return;
        }
        QWHistogram QWhist = new QWHistogram(GUI,"Wait count pr. Queue",false);
        QWhist.refreshHistoGram();
        QWhist.refreshPeakWaitWFC();
        QWhist.refreshUnderScedules();
        QWhist.setVisible(true);
/*        
       String sp = "          ";
        String hdr = "Queue depths     1-5           6-10            11-20          21-30          31-40" +
                           "             41-50        51-70         71-90          91-100          >100";    
        queFile.println("");
        queFile.println(hdr);
        queFile.println("");
        if (debug) System.out.println("\n" + hdr);
        for (int i = 0; i < 9; i++) {
            queFile.printf("Que%d %4s %3d %4s %3d  %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d",
                                + (i+1),sp,histoGram[i][0],sp,histoGram[i][1],sp,histoGram[i][2],
                                sp,histoGram[i][3],sp,histoGram[i][4],sp,histoGram[i][5],sp,histoGram[i][6],
                                sp,histoGram[i][7],sp,histoGram[i][8],sp,histoGram[i][9]);                   
            queFile.println("");
            if (debug)
               System.out.printf("\nQue%d %4s %3d %4s %3d  %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d %4s %3d",
                                + (i+1),sp,histoGram[i][0],sp,histoGram[i][1],sp,histoGram[i][2],
                                sp,histoGram[i][3],sp,histoGram[i][4],sp,histoGram[i][5],sp,histoGram[i][6],
                                sp,histoGram[i][7],sp,histoGram[i][8],sp,histoGram[i][9]);                   
        }
 
        queFile.println("");
        queFile.println("----- The highest queue depth during this periode.------");
        queFile.println("");
        if (debug) System.out.println("\n\n----- The highest queue depth during this periode.------");
        for (int i = 0; i < 9; i++) {
//            System.out.println("");
            if (maxDepth[i] == 0) {
                queFile.println("Queue" + (i+1) + "  had no queue during this periode");    
                if (debug) System.out.println("Queue" + (i+1) + "  had no queue during this periode");    
            } else { 
                queFile.println("Queue" + (i+1) + " peaked at " + maxDepthTime[i] + " with a queue depth of " + maxDepth[i]);
                if (debug) System.out.println("Queue" + (i+1) + " peaked at " + maxDepthTime[i] + " with a queue depth of " + maxDepth[i]);
            }    
        }
        queFile.println("");
        queFile.println("----- Calculated threads higher then active threads while queue (under scheduling) ------");
        queFile.println("");
        if (debug) System.out.println("\n\n");
        for (int i = 0; i < 9; i++) {
//            System.out.println("");
            if (calcHigherThread[i] == 0) {
                queFile.println("Queue" + (i+1) + "  had no under schedule in this periode");    
                if (debug) System.out.println("Queue" + (i+1) + "  had no under schedule in this periode");    
            } else { 
                queFile.println("Queue" + (i+1) + " was under scheduled " + calcHigherThread[i] + " in this periode");
                if (debug) System.out.println("Queue" + (i+1) + " was under scheduled " + calcHigherThread[i] + " in this periode");
            }    
        }
        queFile.println("");     
        if (debug) System.out.println("");
  */      
    }
    /**
     * Counts all unique wfc_id's  
     * @return
     */
    public HashMap observedBPgraph() {
     String inpr;
     HashMap nameList = new HashMap();
     ArrayList wfidList = new ArrayList();
     ArrayList alStr = null;
     while ((inpr = QWUtil.readLine()) != null) {
           int recType = QWUtil.getRecordType(inpr);
           if (recType == QWAGlobal.que) {
               if (alStr != null){
                  wfidList = addBPNameCounters(alStr,nameList,wfidList); 
                  alStr = null; 
               }
           } else if (recType == QWAGlobal.wfc) {
               if (alStr == null){
                   alStr = new ArrayList();
               }
               alStr.add(inpr);
           }
      }
      return nameList;       
    }  
    /**
     * This will pick the number of time QW have observed individual BPs
     * @param alStr
     * @param bpNameList
     * @param wfidList
     * @return 
     */
    public ArrayList addBPNameCounters(ArrayList alStr,HashMap bpNameList,ArrayList wfidList){
        int str_size = alStr.size();
        ArrayList al_wfid = new ArrayList();
        ArrayList al_record= new ArrayList();
        QWBPNameInfo bpObj;
        for (int i = 0; i < str_size-1; i++) {
            al_record = QWUtil.tokenize((String)alStr.get(i));
            int inx = 2;
            String wfid = (String)al_record.get(inx+7);
            String act = (String)al_record.get(inx+15);
            String bpn = (String)al_record.get(inx+17);
            if ((act.startsWith("-1")) || (act.startsWith("9"))) {   // adjust for corrupted input
                bpn = (String)al_record.get(inx+19);
            } 
            bpObj = (QWBPNameInfo)bpNameList.get(bpn);
            if (bpObj == null) {
                bpObj = new QWBPNameInfo(bpn,1);
                bpNameList.put(bpn,bpObj);
                al_wfid.add(wfid);
            } else {
               if (!wfidList.contains(wfid)){
                      bpObj.count +=1;
                      bpNameList.put(bpn,bpObj);
                } //else 
                    //System.out.println("double wfid-->" + bpn + "  " + wfid);
                al_wfid.add(wfid);
              }
            
        }
        return al_wfid;
    }
    /**
     * Counts all unique wfc_id's  
     * @return
     */
    public Properties countBPs(boolean uniqueWF) {
     String inpr;
     Properties prop = new Properties();
     ArrayList al1 = new ArrayList();
     while ((inpr = QWUtil.readLine()) != null) {
           int recType = QWUtil.getRecordType(inpr);
           
           if (recType == QWAGlobal.wfc) {
              int inx = 2;
              ArrayList al = QWUtil.tokenize(inpr);
              String wfid = (String)al.get(inx+7);
              String act = (String)al.get(inx+15);
              String bpn = (String)al.get(inx+17);
              if ((act.startsWith("-1")) || (act.startsWith("9"))) {
                  bpn = (String)al.get(inx+19);
              }
              //if (al1.contains(wfid))
              //    continue;
              String p = prop.getProperty(bpn);
              String count = null;
              if (p == null) {
                  count = "1";
                  prop.setProperty(bpn, count);
                  al1.add(wfid);
              } else {
                   //int p1 = al1.indexOf(wfid);
                   //if (p1 == -1) {
                      int cnt = Integer.parseInt(p)+1;
                      count = String.valueOf(cnt);
                      prop.setProperty(bpn, count);
                      al1.add(wfid);
                   //} 
              }
            }
      }
      return prop;       
    }
 
    /**
     * Counts all wfc's even if the same wfc_id occur at multiple samplings 
     * @return
     */
    public Properties countBPs() {
      String inpr = null;
      String name = null;
      int ix,ix1=0;
      String lookFor = null;
      Properties prop = new Properties();
      while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.wfc) {
               ArrayList al = QWUtil.tokenize(inpr); 
               int inx = al.indexOf("WF_ID");
               String wfid = (String)al.get(inx+1);
               if (lookFor == null) {
                  ix = inpr.indexOf("PRTY", 80);
                  String prty=null;
                  if (ix > -1) {
                     prty = inpr.substring(ix+5,ix+7);
                     if ((prty.startsWith("-1")) || ((prty.startsWith("9"))))
                        lookFor ="BP";
                     else
                        lookFor ="s)";
                     }
               }    
               ix = inpr.indexOf(lookFor, 80);
               String bp = inpr.substring(ix+3).trim();
               ix = 0;
               ix1 = bp.indexOf(" ", ix+1);
               if (ix1 == -1)
                  name = bp.substring(0);
               else 
                  name = bp.substring(ix,ix1);
               String p = prop.getProperty(name);
               String count = null;
               if (p == null)
                  count = "1";
               else {
                  int cnt = Integer.parseInt(p)+1;
                  count = String.valueOf(cnt);
               }
               prop.setProperty(name, count);
             } 
      }
      bpCount = prop.size();   // bpCount is static variable
      return prop;  
    }
    /**
     * 
     * @param bpName
     * @param rows
    */
    public void reportBP(String bpName,int rows ) {
     String inpr;
     QWWfc qwfc = new QWWfc(GUI,"BP List",false);
     qwfc.createTable(rows);
     Properties prop = new Properties();
     int rec  = 0;
     boolean newQ = false;
     boolean add = false;
     while ((inpr = QWUtil.readLine()) != null) {
        int recType = QWUtil.getRecordType(inpr);
        if (recType ==  QWAGlobal.que) {
           createQMatrix(inpr);
           newQ = true;
        } else if (recType ==  QWAGlobal.wfc) {
           int inx = 2;
           String tim = "";
           ArrayList al = QWUtil.tokenize(inpr);
           if (newQ)
               tim = (String)al.get(inx);
           String qe = (String)al.get(inx+3);
           if (!QWUtil.isDigit(qe)) {
              if (expdORdline(qe))
              continue;
           }
           String wfid = (String)al.get(inx+7);
           String step = (String)al.get(inx+9);
           String act = (String)al.get(inx+15);
           String bpn = (String)al.get(inx+17);
           if ((act.startsWith("-1")) || (act.startsWith("9"))) {
               act = (String)al.get(inx+17);
               bpn = (String)al.get(inx+19);
           }
           int qinx = 0;
           try {
               qinx = Integer.parseInt(qe);
           } catch (Exception ex) {
//               ex.printStackTrace();
  //             System.out.println("INPUT: " + inpr);
           }
               if (qinx > 9 || qinx == 0 || qinx == -1) qinx=4; //default
               int used = qmon[qinx-1][1];
               int calc = qmon[qinx-1][2];
               int dpth = qmon[qinx-1][5];
               if (bpName != null) {
                  if (bpName.equalsIgnoreCase(bpn)) {
                      String pos = prop.getProperty(wfid);
                      if (pos == null) {
                         prop.setProperty(wfid,String.valueOf(rec));
                         qwfc.updateTable(rec,tim,qe,used,calc,dpth,wfid,step,act,"na",bpn);
                         rec++;
                      } else {
                         qwfc.updateTable(Integer.parseInt(pos),step,act,wfid);
                      }
                      newQ = false;
                  }
                  add = false;
               } else {
                 qwfc.updateTable(rec,tim,qe,used,calc,dpth,wfid,step,act,"na",bpn);
                 rec++;
                 newQ = false;
              }
       //   int qnumber = Integer.parseInt(str.substring(37,38)); 
       //    qs[qnumber-1]++; 
       //    totalWFCs[qnumber-1]++;
        }
     }
     qwfc.displayCounters();
     qwfc.setVisible(true);
    }
    /**
     * 
     *  1 - 9 queues : Minpool, Used, Calculated , Pool, MaxPoool, Depth 
     */
    public void reportActiveQueANDMemory(int th,JTextArea jt,JLabel jl) {
     String inpr = null;
     int inuse=0;
     int qdepth=0;
     String tim=null;
     boolean track = false;
     jl.setText("Time        Threads         Depth        FreeMem(%)     Commited(GB) ");
     while ((inpr = QWUtil.readLine()) != null) {
           int recType = QWUtil.getRecordType(inpr);
           if (recType ==  QWAGlobal.que) {
              track = false;
              tim = inpr.substring(17,26);
              createQMatrix(inpr);
              inuse = 0;
              qdepth=0;
              for (int i = 0; i < 9; i++) {
                  inuse += qmon[i][inUseThreads];
                  qdepth += qmon[i][5];
              }
              if (inuse >= th) {
                  track = true;
              } 
           } else if (recType ==  QWAGlobal.mem) {
             if (track) {
                 ArrayList al = QWUtil.tokenize(inpr);
                 int size = al.size();
                 String freeMemory = (String)al.get(size-3);
                 String totalMGB =  (String)al.get(5);
                 String tim1 = inpr.substring(17,26);
                 StringBuilder sb = new StringBuilder();
                 sb.append(tim);
                 sb.append("\t");
                 sb.append(inuse);
                 sb.append("\t");
                 sb.append(qdepth);
                 sb.append("\t");
                 sb.append(freeMemory);
                 sb.append("\t");
                 sb.append(totalMGB);
                 sb.append("\n");
                 jt.append(sb.toString());
/*                         
                 System.out.println("Time: " + tim +
                         "       Threads: " + inuse + 
                         "       Waiters: " + qdepth + 
                         "       Time:    " + tim1 + 
                         "       FreeMem: " + freeMemory + " % " +
                         "       Total: " + totalMGB + " (GB)");
*/
                 track = false;
             } // End track  
           } // End Mem
      } // End While  
    }
    /**
     * Display JDBC records
     * @param str
     * @param jt
     */
    public void ListJDBCInfo(String str, javax.swing.JTable jt) {
     String inpr = null;
     while ((inpr = QWUtil.readLine()) != null) {
           int recType = QWUtil.getRecordType(inpr);
           if (recType ==  QWAGlobal.jdbc) {
               ArrayList al = QWUtil.tokenize(inpr);
          }
     }
    }
    /**
     * 
     * @param gapLimit  - look for gaps greater than "gapLimit" in seconds
     * @param jt - TextArea to display gaps if any
     * @param jl - Label
     * Check for sampling gaps (timeout) or SI reboot.
     */
    public void reportSamplingGaps(int gapLimit,JTextArea jt,JLabel jl) {
      jt.setText("");
      int lineCount = 0;
      String line1 = QWUtil.readLine();
      int st = QWUtil.getTimeInSeconds(line1.substring(6+11,26));
      String line2;
      while ((line2 = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(line2);
            line1 = line2;
            int sp = QWUtil.getTimeInSeconds(line2.substring(6+11,26));
            if ((sp-st) > gapLimit) {
               jt.append(line1.substring(6,26) + " " + "have a gap of " + (sp-st) + " seconds)\n");  
               lineCount++;
            }
            st = sp;
     }
     if (lineCount == 0) {
         jt.setText(" No gaps of " + gapLimit + " seconds or greater were found in the samples");
     } else {
          int srate = (Integer.parseInt(QWAGlobal.hdrRate))/1000;
          StringBuilder sb = new StringBuilder();
          sb.append("Sampling Rate: ").append(srate).append(" seconds - ");
          sb.append("Gaps longer than: ").append(gapLimit).append(" secs found ").append(lineCount).append(" times");
          sb.append("  !!! Warning - Gaps longer than 60 seconds could indicate a re-boot of SI");
          jl.setText(sb.toString());
     } 
    }
    /**
     * look for workflow-ids running longer than "parm1"
     * @param longerThanThis - running longer in seconds
     * @param jt - TextArea for display
     * @param jl - TextLabel - heading
     */
    public void listLongRunners(int longerThanThis,JTextArea jt,JLabel jl) {
        Properties starting = new Properties();
        Properties ending = new Properties();
        Properties nameOfBP = new Properties();
        String inpr = null;
        int hitCount = 0;
        jt.setText("");        
        while ((inpr = QWUtil.readLine()) != null) {
            int recType = QWUtil.getRecordType(inpr);
            if (recType ==  QWAGlobal.wfc) {     // Dispatch Queue Info
               ArrayList wfcList = QWUtil.tokenize(inpr);
               String tm = (String)wfcList.get(2);
               String wfcID = (String)wfcList.get(9);
               String BPname = (String)wfcList.get(21);
               if (!QWUtil.insertIfNotIn(wfcID, tm, starting)) {
                   QWUtil.insertRegardless(wfcID, tm, ending);
                   QWUtil.insertIfNotIn(wfcID, BPname, nameOfBP);
               }
            }        
        }
        Iterator myIterator=ending.keySet().iterator();
        while(myIterator.hasNext()) {
             String Key = (String) myIterator.next();
             String et_time = ending.getProperty(Key);
             String st_time = starting.getProperty(Key);
             String name = nameOfBP.getProperty(Key);
             int stTime = QWUtil.getTimeInSeconds(st_time);
             int etTime = QWUtil.getTimeInSeconds(et_time);
             if ((etTime-stTime) >= longerThanThis){
                 StringBuilder sb = new StringBuilder();
                 sb.append("First :\t");
                 sb.append(st_time).append("\t");
                 sb.append("Last :\t");
                 sb.append(et_time).append("\t");
                 sb.append("Elapse time (sec):\t");
                 sb.append((etTime-stTime)).append("\t");
                 sb.append("WorkflowId:\t");
                 sb.append(Key).append("\t");
                 sb.append("BP name:\t");
                 sb.append(name).append("\t").append("\n");
                 jt.append(sb.toString());
                 hitCount++;
             }   
            }
        jl.setText("  " + hitCount + " BP's running  longer than "  + longerThanThis + " seconds");
    }
    public Properties selectOnWFID() {
     Properties p = new Properties();
     String inpr = null;
     while ((inpr = QWUtil.readLine()) != null) {
           int recType = QWUtil.getRecordType(inpr);
           if (recType ==  QWAGlobal.wfc) {
               ArrayList al = QWUtil.tokenize(inpr);
               Map hp = new HashMap();
               WFInfo wfInfo = new WFInfo(); 
            
// todo               
           }
      }         
     return p;
    }
    /**
     * Get BP Names and Count occurances. 
     *  (The code is a bit messy because the input comes in a couple of flavours)
     */
    public void reportWFCs(){
          if (html) {
              queFile.println("<h2>BPs found during this periode</h2>");
              queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
              queFile.println("<font size='1'>");
           } else {
              jTA1.setFont(new Font("Courier",Font.PLAIN,12));
              jHdr.setFont(new Font("Verdana",Font.PLAIN,14));
              jHdr.setText("Occurrances of BPs found this periode (same wfcid can appear multiple times)");       
           }
           if (debug) System.out.println("\n");
           Properties prop = countBPs(true);
           for (Enumeration en = prop.keys();en.hasMoreElements();){
               String name = (String)en.nextElement();
               String p = prop.getProperty(name);
               if (html) {
                   queFile.printf("<tr><td align='right'>" + name + "</td><td>Found</td><td>" + p + "</td><td>Times</td></tr>");
               } else {
                   int ln = name.length();
                   StringBuilder sb = new StringBuilder();
                   sb.append(name);
                   sb.append(pad.substring(0,50-ln));
                   sb.append(p).append("\n");
                   jTA1.append(sb.toString());
               }
           } 
           if (html)
               queFile.println("</font></table>");
 //     if (verbose) System.out.println(fn + " Number of lines searched :" + countLines + "   Number of hits: " + count);
      if (outp != null)
        outp.flush();
    }
   public void collectAll() {
      String str,oName = null;
      int nameindex = 0;
      long totObjects=0l;
      long totSize = 0l;
      HashMap objNames = new HashMap();
      HashSet objectTypes = new HashSet();
      long [] objCount = new long[wfcLines+1];
      long [] objSize = new long[wfcLines+1];
      long lineCount = 0l;
      int arrayInx =0;
      while((str = QWUtil.readLine()) != null ){
          int recType = QWUtil.getRecordType(str);
          if (recType ==  QWAGlobal.wfc) {
             lineCount++;
             int inx = str.indexOf("\t");
             int inx1 = str.indexOf(" ");
             if (inx1 != -1)
                oName = str.substring(inx+1,inx1);
             else
                oName = str.substring(inx+1);
             if (!objNames.containsKey(oName)) {
                objNames.put(oName,String.valueOf(nameindex));
                objectTypes.add(oName);
                arrayInx = nameindex;
                nameindex++;
             } else {
                arrayInx = Integer.parseInt((String)objNames.get(oName));
             }
             inx = str.indexOf("\t");
             int val3 = Integer.parseInt(str.substring(0,inx));
             objCount[arrayInx]++;
             objSize[arrayInx] += val3;
             totObjects++;
             totSize += val3;
          }   
        }
      return;
   }

   public class QWrsp {
      Properties prop = null;
      ArrayList al = null;
   
   } 
   public class BPcount {
     int bp;
     int wfc;
   }
   //  Helper Class
   public class Qhelper {
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

   private class WFInfo {
   // to be filled
   }
   private class WaitHelper {
     String [] qStart = new String[9];
     String [] qEnd = new String[9];
     String [] qDateStart = new String[9];
     String [] qDateEnd = new String[9];
     int [] ques = new int[9];
     int [] sz = new int[9];
     // new 8/3/2011
     int [] maxsz = new int[9];
     int [] minsz = new int[9];
   }
   public class listHelper{
       String name = null;  // Name of the BP
       String ques = null;  // Queues it has been on
       long totActive = 0l; // accumulated time of all invocations of this BP 
       int totObserved = 0; // times it's been observed
       int minActive = 0;   // lowest time it has been active 
       int maxActive = 0;   // highest time it has been active
       
   } 
}
    
