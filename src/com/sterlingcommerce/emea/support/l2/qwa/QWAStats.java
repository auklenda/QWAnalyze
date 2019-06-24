/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Alf
 */
public class QWAStats {

    public QWAStats() {
    }
    public void calcHigherThreadStat(int q,int t,int c,int d) {
        if (t < c && d > 0)
            QWAGlobal.calcHigherThread[q] += 1;
            //QWAGlobal.calcHigherThread[q-1] += 1;
    }
    public void countAllThreadsInUse(int q,int t,int c) {
        if (t == c)
            QWAGlobal.allThreadsInUse[q] += 1;
    }
    public void countFairShareLimitiation(int q,int t,int c, int mx) {
        if ((t == c) && (c < mx))
            QWAGlobal.fairShareImposed[q] += 1;
    }
    
    public void countAnyThreadsInUse(int q,int t) {
        if (t > 0)
            QWAGlobal.anyThreadsInUse[q] += 1;
    }
    public void countAllWaiters(int q,int w) {
        if (w > 0)
            QWAGlobal.waitersForThreads[q] += 1;
    }    
   /**
     * 
     * @param q
     * @param val
     * @param timeAndDate
     */
    protected void queueDepthHistogram(int q,int val, String timeAndDate) {
        if (val == 0) return;
        //int inx =selectIndex(val);
        int inx = 0;
        if (val > 90){ inx = 8;
        } else if (val > 70 && val <= 90){ inx=7;
        } else if (val > 50 && val <= 70){ inx=6;
        } else if (val > 40 && val <= 50){ inx=5;
        } else if (val > 30 && val <= 40){ inx=4;
        } else if (val > 20 && val <= 30){ inx=3;
        } else if (val > 10 && val <= 20){ inx=2;
        } else if (val > 5 && val <= 10) { inx=1;
        } else if (val > 0 && val <= 5) {
            inx=0;
        }
        if (inx < 10 ) {
            QWAGlobal.histoGram[q][inx]++;
        }
        if (QWAGlobal.maxDepth[q] < val ) {
            QWAGlobal.maxDepth[q] = val;
            QWAGlobal.maxDepthTime[q] = timeAndDate;
        }    
    } 
    public void saveHDR(String str) {
           ArrayList al = QWUtil.tokenize(str);
           int i = 4;
           int len = al.size();
           while (i < len){
                String tmp = (String)al.get(i++);
                if ("host".equals((String)tmp)) { 
                   QWAGlobal.hdrHost = (String)al.get(i++);
                } else if ("port".equals((String)tmp)){
                   QWAGlobal.hdrPort = (String)al.get(i++);
                } else if ("rate".equals((String)tmp)) {
                   QWAGlobal.hdrRate = (String)al.get(i++);
                } else if ("threshold".equals((String)tmp)) {
                   QWAGlobal.hdrTH = (String)al.get(i++);
                } else if ("node".equals((String)tmp)) {
                   QWAGlobal.hdrNode = (String)al.get(i++);
                } else if ("memory".equals((String)tmp)) {
                   QWAGlobal.hdrMemory = (String)al.get(i++);
                } else if ("wfid".equals((String)tmp)) {
                   QWAGlobal.hdrWFID = (String)al.get(i++);
                } else 
                   i++; 
           }
        
    }
    /**
     * Run through the total file at startup and set some counters
     * @param str
     */
    /*
    public void collectStats(String str) {
      try { 
        if (QWAGlobal.start == null) {
            String start = str.substring(6,26);
            String nodeName = str.substring(0,5);
            QWAGlobal.start = start;
            int ix =  start.indexOf(" ");
            QWAGlobal.startDate = start.substring(0, ix);
            QWAGlobal.nodeName = nodeName;
        } else {
            QWAGlobal.last = str.substring(6,26);
            int tm2 = QWUtil.getTimeInSeconds(QWAGlobal.last.substring(11));
            
        }
        if (QWAGlobal.bpList == null) {
            QWAGlobal.bpList = new HashMap();
        }
        String tim = str.substring(17,26);
        QWAStats qwaStats = new QWAStats();
        int recType = QWUtil.getRecordType(str);
        if (recType ==  QWAGlobal.que) {
           QWAGlobal.queList.add(str);
           queLines++;
           createQMatrix(str);
           int inUse = 0;
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

           }
           reportQueueExist(-1);
           if (inUse > maxActiveThreads) {
               maxActiveThreads = inUse;
               maxActiveThreadsTime = tim;
           }    
       } else if (recType ==  QWAGlobal.wfc) {
           wfcLines++;
           ArrayList al = QWUtil.tokenize(str);
           int ix = al.indexOf("BP");
           String bpName = (String)al.get(ix+1);
           ix = al.indexOf("QUEUE");
           String q = (String)al.get(ix+1);
           if (QWAGlobal.bpList.containsKey(bpName)) {
               String oldq = (String)QWAGlobal.bpList.get(bpName);
               int i = oldq.indexOf(q);
               if (i == -1 ) {
                   q = oldq + " " + q;
                   QWAGlobal.bpList.remove(bpName);
                   QWAGlobal.bpList.put(bpName,q);
                   //System.out.println(q);
               }
           } else {
               QWAGlobal.bpList.put(bpName,q);
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
           ArrayList al = QWUtil.tokenize(str);
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
           new QWAThreadPool().createQTable(str);        
      }
      } catch(Exception exp) {
          //exp.printStackTrace();
      }  
    }
    */
    /**
     * Called at startup to set some counters to be displayed
     */
    /*
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
      start = last = null;
      for (int i = 0; i < 9; i++) {
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
          wh = new QWAnalyze.WaitHelper();
      QWAGlobal.queList = new ArrayList();
      while ((inpr = QWUtil.readLine()) != null) {
              countLines++;
               collectStats(inpr);
      }
      new QWHistogram(GUI,"Display paralell queues to check FairShare",false).FairShare(QWAGlobal.queList);
                  
    }*/
    
}
