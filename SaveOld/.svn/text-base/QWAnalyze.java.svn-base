/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2;

import java.io.*;
import java.util.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
/**
 *
 * @author AAuklend
 */
public class QWAnalyze {
  long tstgap = 0l;
  long bigGap = 0l;
  int linr =0;
  int depth = 0;
  String folder = null;
  String searchP = null; 
  String csv = ",";
  String FromDate = null;
  String ToDate = null;
  String inFileName = null;
  long fromTime = 0l;
  long toTime = 0l;
  boolean prtAll = false;
  boolean subf = true;
  boolean verbose = false;
  boolean memPrt=false;
  boolean quePrt = true;
  boolean wfdPrt = false;
  boolean debug = false;
  boolean html = true;
  static BufferedReader inp = null;  
  static PrintWriter outp = null;
  PrintWriter queFile;
  PrintWriter wfdFile;
  PrintWriter memFile;
  BufferedReader QWFile = null;
  long samples = 0l;
 public int [][] qmon = new int[9][6] ; 
 int  [][] histoGram = new int [9][10];
 int [] maxDepth =  new int [9];
 int [] calcHigherThread = new int[9];
 String [] maxDepthTime = new String[9];
 String [] series = {"Q1","Q2","Q3","Q4","Q5","Q6","Q7","Q8","Q9"};

    /** Creates a new instance of LogSearch */
    public QWAnalyze() {
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
            exp.printStackTrace();
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
    protected void closeQWFile() {
     try {
        if (QWFile !=null)
            QWFile.close();
     } catch(IOException exp) {
         exp.printStackTrace();
     }   
    }
    protected void closeQueFile() {
        if (queFile !=null)
            queFile.close();
    }
    
    protected void setQWFile(BufferedReader br) {
        QWFile = br;
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

    private void setFolder(String dir) {
        folder = dir;
    }
    private String getFolder(){
        return(folder);
    }
    private void setSubf(boolean b) {
        subf = b;
    }
    private boolean getSubf(){
        return(subf);
    }
    private void setSearchPhrase(String sp) {
        searchP = sp;
    }
    private String getSearchPhrase(){
        return(searchP);
    }    
   
    public String getTime(String line,int offs, String delimit) {
        return(line.substring(offs,line.indexOf(delimit)));
    }
    private String rl() {
     String instring = null;   
     try {
        while(true) {
            if ((instring = inp.readLine())== null)
                 return instring;
            linr++;
            if (instring.startsWith("[")) 
                 return(instring);
            else if(prtAll)
                 System.out.println(instring);
         }
     } catch (IOException exp) {
           exp.printStackTrace();
       }
    return instring; 
    }
    private String getDate(String args){
        return(args.substring(0,args.indexOf(" ")));
    }
    private String getTime(String args){
        return(args.substring(args.indexOf(" "),args.length()));
    }
    
    public long getTimeInMS(String instring) {
       String rcv = getTime(instring,11,"]");
       long ms = Integer.parseInt(rcv.substring(1,3))*60*60*1000;
       ms += Integer.parseInt(rcv.substring(4,6))*60*1000;
       ms += Integer.parseInt(rcv.substring(7,9))*1000;
       long tms = Integer.parseInt(rcv.substring(10));
       if (rcv.substring(10).length() == 1)
          ms += tms*100;
       else if (rcv.substring(10).length() == 2 )
          ms += tms*10;
       else ms += tms;
     return ms;   
    }
    private long getMS(String tm) {
       long ms = Integer.parseInt(tm.substring(1,3))*60*60*1000;
       ms += Integer.parseInt(tm.substring(4,6))*60*1000;
       ms += Integer.parseInt(tm.substring(7,9))*1000;
       return ms; 
    }
    /**
     * If calculated threads are higher
     * @param q
     * @param t
     * @param c
     */
    protected void calcHigherThreadStat(int q,int t,int c,int d) {
        if (t < c && d > 0)
            calcHigherThread[q] += 1;
    }
    protected void interValStat(int q,int val, String timeAndDate) {
       
        if (val == 0) return;
        int inx =10;
        if (val > 100){
           inx = 9; 
        } else if (val > 90 && val < 100){
            inx=8;
        } else if (val > 70 && val <= 90){
            inx=7;
        } else if (val > 50 && val <= 70){
            inx=6;
        } else if (val > 40 && val <= 50){
            inx=5;
        } else if (val > 30 && val <= 40){
            inx=4;
        } else if (val > 20 && val <= 30){
            inx=3;
        } else if (val > 10 && val <= 20) {
            inx=2;
        } else if (val > 5 && val <= 10) {
            inx=1;
        } else if (val > 0 && val <= 5)
            inx=0;
        if (inx < 10 ) histoGram[q][inx]++;
        if (maxDepth[q] < val ) {
            maxDepth[q] = val;
            maxDepthTime[q] = timeAndDate;
        }    
    }

    /*
     *   Create a matrix with all parms for this sample line
     *   1 - 9 queues : Minpool, Used, Calculated , Pool, MaxPoool, Depth
     */ 
    protected void createQMatrix(String str) {
	StringTokenizer st = null;
        int inx,inx1 =0;
        inx = str.indexOf('Q',26);
        inx1 = str.indexOf("Q",inx+8);
     	st = new StringTokenizer(str.substring(inx+8,inx1-1)," ");
//        sb.insert(1,str.substring(inx+8,inx1-1));  //q1
 	int i0 = 0;
        while(st.hasMoreTokens()){
		qmon[0][i0++] = Integer.parseInt(st.nextToken());
        }       
        for (int i = 0; i < 8; i++) {
            inx = inx1; 
            inx1 = str.indexOf("Q",inx+8);
            if (inx1==-1)
                st = new StringTokenizer(str.substring(inx+8)," ");
            else 
                st = new StringTokenizer(str.substring(inx+8,inx1-1)," ");
 //           sb.insert(1+i,str.substring(inx+8,inx1-1));  //
            i0=0;
            while(st.hasMoreTokens()) {
		qmon[i+1][i0++] = Integer.parseInt(st.nextToken());
            }       
        }
    }
    /**
     * 
     * @param str
     * @return StringBuffer
     */
    private ArrayList tokenizeWFC(String str) {
        ArrayList al = new ArrayList();
        StringTokenizer st = null;
     	st = new StringTokenizer(str," ");
 	int i0 = 0;
        while(st.hasMoreTokens()){
           al.add(i0++,(String)st.nextToken()); 
        }       
        return al;
    }
    /**
     * 
     * @param line
     * @param offs
     * @param delimit
     * @return
     */
    public String getTimeString(String line,int offs, String delimit) {
        return(line.substring(offs,line.indexOf(delimit)));
    }
    /**
     * 
     * @param fn
     */
    public void getLogTimeAndDate(String fn) {
       String str = null;
       String rcv = null;
       boolean fileNamePrinted = false;
       long ms = 0;
       long tmp=0l;
        try {
           while (true) {
                 if ((str= rl()) == null)
                    break;
                 if (str.startsWith("[" + FromDate)) {
                      rcv = getTimeString(str,11,"]");
                      String hh = rcv.substring(10);
                      ms = Integer.parseInt(rcv.substring(1,3))*60*60*1000;
                      ms += Integer.parseInt(rcv.substring(4,6))*60*1000;
                      ms += Integer.parseInt(rcv.substring(7,9))*1000;
                      tmp = Integer.parseInt(rcv.substring(10));
                      if ( ms >= toTime ) {
                          break;
                      }
                      if (ms >= fromTime) {
                         if (!fileNamePrinted) {
                             System.out.println("\n" + fn + "\n");
                             fileNamePrinted = true;
                         } 
                         System.out.println(str);
                     }   
                 }   
            }
      } catch (Exception exp) {
          exp.printStackTrace();
      }    
       
    }
    /**
     * 
     * @param fn
     */
    private void listLines(String fn) {
 //       System.out.println(fn);
        getLogTimeAndDate(fn);
    }
    protected void htmlBegEnd(boolean b) {
      if (!html) return;
      if (b) 
        queFile.println("<html><head></head><body>");
      else 
        queFile.println("</body></html>");  
    }
    public String splitQueues(String str, boolean html) {
        String tim = null;
        createQMatrix(str);
        StringBuffer sb = new StringBuffer();
        tim = str.substring(17,26);
        if (html) {
            sb.append("<tr><th>");
        }
        sb.append(tim);
        sb.append(str.substring(0,17));
        if (html) 
            sb.append("</th>");
        else 
            sb.append("  ");
        boolean gotSome = false;
        for (int i = 0; i < 9; i++) {
            interValStat(i,qmon[i][5],tim);
            calcHigherThreadStat(i,qmon[i][1],qmon[i][2],qmon[i][5]);
            if (qmon[i][5] >= depth) {
                if (html) {
                    sb.append("<td>Q: " + (i+1) + " d: " + qmon[i][5] + " t: " + qmon[i][1] + " c: " + + qmon[i][2] +"</td>"); 
                } else {
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
      String inpr;
      int countLines=0;
      String hdr ="------Report of queues with queue depth > " + depth + "  d: depth    t: threads   c: fairness calc---------"; 
      if (html) {
         queFile.println("<h2>Queues with depth higher than " + depth + "</h2>");
         queFile.println("<h4> Q: Queue  d: BPs on queue   t: active threads   c: calculated fairness</h4>");
         queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
         queFile.println("<font size='1'>");
      } else {
         queFile.println("");
         queFile.println(hdr);
         queFile.println("");
      }   
      if (debug) System.out.println(hdr);
      try {
           while (true) {
               if ((inpr = QWFile.readLine()) == null)
                    break;
               countLines++; 
               String tmp = inpr.substring(34,37);    //  Record type
               if (tmp.equalsIgnoreCase("WFC")) {
                  if (wfdPrt)
                     wfdFile.println(inpr);
               } else if (tmp.equalsIgnoreCase("MEM")) {

 //               } else if (tmp.equalsIgnoreCase("MIN")) {
               } else { 
                  if (quePrt) {
//                   queFile.println(inpr);
                     boolean mod = html ? true : false; 
                     String str = splitQueues(inpr,mod);
                     if (str != null) {
                           queFile.println(str); 
                           if (debug) System.out.println(str);
                     }
                  }   
               } 
           }
           if (html) {
              queFile.println("</font></table>");  
           }
      } catch (Exception exp) {
          exp.printStackTrace();
      }   
      return;
    }
    /**
     * 
     */
    public void reportQAndWFC() {
      String inpr;
      boolean expectWFC = false;
      String hdr = "-------Report queues with queue depth and active BPs---";
      if (html) {

          queFile.println("<h2>Queues with depth and active BPs</h2>");
/*          
         queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
         queFile.println("<font size='1'>");
 */ 
      } else {
         queFile.println("");
         queFile.println(hdr);
         queFile.println("");
         if (debug) System.out.println("\n" + hdr + "\n");
      }
      int countLines=0;
      try {
           while (true) {
               if ((inpr = QWFile.readLine()) == null)
                    break;
               countLines++; 
               int cnt = 0;
               String tmp = inpr.substring(34,37);    //  Time and date
               if (tmp.equalsIgnoreCase("INS")) {
                  if (expectWFC){
                      if (html) { 
                          queFile.println("<br>" + inpr);
                      } else {
                           queFile.println(inpr);
                           if (debug) System.out.println(inpr);
                      }    
                      //wfdFile.println(inpr);
                  }   
               } else if (tmp.equalsIgnoreCase("MEM")) {
                 if (memPrt) {
                          memFile.println(inpr);
                  }
               } else {
                   expectWFC=false;
                   String str = splitQueues(inpr,false);
                   if (str != null) {
                      if (html) {
                         queFile.println("<br><b>" + str + "</b>");  
                      } else {
                         queFile.println(str); 
                         if (debug) System.out.println(str);
                      }
                       expectWFC = true;
                   }   
               }
           }
           queFile.println("</font></table>");  
      } catch (Exception exp) {
          exp.printStackTrace();
      }   
      return;
    }
    /**
     * 
     * @return JFreeChart dataset
     */
    public DefaultCategoryDataset buildBarChart() {
      String inpr = null;
      String cat = null;
      String previous = null;
      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      try {
           while (true) {
               if ((inpr = QWFile.readLine()) == null)
                    break;
               String tmp = inpr.substring(34,37);    //  Time and date
               if (tmp.equalsIgnoreCase("INS")) {
               } else if (tmp.equalsIgnoreCase("MEM")) {
 //               } else if (tmp.equalsIgnoreCase("MIN")) {
               } else { 
                 
                 String tim = inpr.substring(17,22);
                 if (tim.equalsIgnoreCase(previous))
                     cat = "";
                 else cat = tim;
                 createQMatrix(inpr);
                 for (int i = 0; i < 9; i++) {
                     if (qmon[i][5] > depth) {
                         dataset.addValue(qmon[i][5],series[i],cat);
                         cat ="";
                     }
                 }
                 previous = tim;
               } 
           }
      } catch (Exception exp) {
          exp.printStackTrace();
      }   
      return dataset;
        
    }
    /**
     *  Create HTML report
     */
    public void printHTMLReport() {    
         queFile.println("<h2>Work found on each QUEUE during this periode</h2>");
         queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
         queFile.println("<font size='1'>");
         StringBuffer sb = new StringBuffer();
         sb.append("<tr><th>Queue depths:</th>");
         sb.append("<th>1-5</th><th>6-10</th><th>11-20</th><th>21-30</th><th>31-40</th>");
         sb.append("<th>41-50</th><th>51-70</th><th>71-90</th><th>91-99</th><th>gt 100</th></tr>");
         queFile.println(sb.toString());
         for (int i = 0; i < 9; i++) {
            queFile.println("<tr><th>Queue" + (i+1) + "</th6>");
            queFile.println("<td>" + histoGram[i][0] + "</td><td>" + histoGram[i][1] + "</td><td>" + histoGram[i][2] + "</td>");
            queFile.println("<td>" + histoGram[i][3] + "</td><td>" + histoGram[i][4] + "</td><td>" + histoGram[i][5] + "</td>");
            queFile.println("<td>" + histoGram[i][6] + "</td><td>" + histoGram[i][7] + "</td><td>" + histoGram[i][8] + "</td>");
            queFile.println("<td>" + histoGram[i][9] + "</td></tr>"); 
         }
         queFile.println("</font></table>");
         queFile.println("<h2>Highest depth found on queues during this periode:</h2>");
         queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
         queFile.println("<font size='1'>");
         for (int i = 0; i < 9; i++) {
            if (maxDepth[i] == 0) {
                queFile.println("<tr><th>Queue" + (i+1) + "</th><td> had no queue during this periode</td></tr>");    
            } else { 
                queFile.println("<tr><th>Queue" + (i+1) + "</th><td> peaked at " + maxDepthTime[i] + " with a queue depth of " + maxDepth[i] + "</td></tr>");    
            }    
         }
         queFile.println("</font></table>"); 
         queFile.println("<h2>Calculated threads higher then Active threads while queue (under-sceduleing)</h2>");
         for (int i = 0; i < 9; i++) {
            if (calcHigherThread[i] == 0) {
                queFile.println("<li>Queue" + (i+1) + "  had no under-schedule in this periode</li>");    
            } else { 
                queFile.println("<li>Queue" + (i+1) + " was under-scheduled " + calcHigherThread[i] + " times in this periode</li>");
            }    
        }
    }
    /**
     * printReport - do various reports in debug mode and text editor
     */
    public void printReport() {
        if (html) {
            printHTMLReport();
            return;
        }
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
        queFile.println("----- Calculated threads higher then active threads while queue (under sceduleing) ------");
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
    }
    /**
     * Get name and count the the number of times a BP is executing 
     * 
     */
    public void reportWFCs(){
      String inpr = null;
      String start = null;
      String last = null;
      samples = 0l;
      Properties prop = new Properties();
      ArrayList al = new ArrayList();
      try {
           while (true) {
               if ((inpr = QWFile.readLine()) == null)
                    break;
                 String tmp = inpr.substring(34,37);    //  Time and date
                 if (start == null)
                     start = inpr.substring(6,26);
                 else
                     last = inpr.substring(6,26);
                 if (tmp.equalsIgnoreCase("INS")) {
                     al = tokenizeWFC(inpr);
                     String name = (String)al.get(20);
                     String p = prop.getProperty(name);
                     String count = null;
                     if (p == null)
                         count = "1";
                     else {
                         int cnt = Integer.parseInt(p)+1;
                         count = String.valueOf(cnt);
                     }
                     prop.setProperty(name, count);
                } else if (tmp.equalsIgnoreCase("MEM")) {
                    samples++;
                } else {
                     samples++;
                }
            }
           if (html) {
              queFile.println("<h1>Start of queue analysis</h1>");
              queFile.println("<h3>Sampling started at: " + start + " and ended: " + last + " . During this periode " + samples + " samples was taken</h3>");
              queFile.println("<h2>Occurrances of BPs found this periode</h2>");
              queFile.println("<table border='0' cellspacing='0' cellpadding='4'>");
              queFile.println("<font size='1'>");
           } else {
              queFile.println("");
              queFile.println("--------- Occurrances of BPs found this periode -----");
              queFile.println("");
              if (debug) System.out.println("\n");
           }
           Enumeration en = prop.keys();
           while (en.hasMoreElements()) {
               String name = (String)en.nextElement();
               String p = prop.getProperty(name);
               if (html) {
                   queFile.printf("<tr><td align='right'>" + name + "</td><td>Found</td><td>" + p + "</td><td>Times</td></tr>");
               } else {
                   queFile.printf("%50s   Found  %4s   times",name,p);
                   queFile.println("");
                   if (debug) System.out.println("\n" + name + "   found   " + p);
               }
           } 
           if (html)
               queFile.println("</font></table>");
      } catch (Exception exp) {
          exp.printStackTrace();
      }    
 //     if (verbose) System.out.println(fn + " Number of lines searched :" + countLines + "   Number of hits: " + count);
      if (outp != null)
        outp.flush();
    }
    
    public void doRawQueues(int que){
          String inpr = null;
      boolean print_next = false;
      int count = 0;
      int countLines = 0;
      try {
           while (true) {
               if ((inpr = inp.readLine()) == null)
                    break;
                 countLines++; 
                 int cnt = 0;
                 String tmp = inpr.substring(34,37);    //  Time and date
                 if (tmp.equalsIgnoreCase("MIN")) {
                     StringBuffer sb = new StringBuffer();
                     sb.append(inpr.substring(0,26)); 
                     sb.append(inpr.substring(26,34));
                     sb.append(inpr.substring(38,40));
                     sb.append(inpr.substring(45,47));
                     sb.append(inpr.substring(52,54));
                     sb.append(inpr.substring(59,61));
                     sb.append(inpr.substring(65,67));
                     sb.append(inpr.substring(73));
                     sb.append(" ");
                     cnt = Integer.parseInt(inpr.substring(73));
                     for (int i= 0; i < 8 ; i++) {
                          if ((inpr = inp.readLine()) == null) {
                              System.out.println("************'''' ERROR - zero"); 
                              break;
                          }     
                          sb.append(inpr.substring(26,34));
                          sb.append(inpr.substring(38,40));
                          sb.append(inpr.substring(45,47));
                          sb.append(inpr.substring(52,54));
                          sb.append(inpr.substring(59,61));
                          sb.append(inpr.substring(65,67));
                          sb.append(inpr.substring(73));
                          sb.append(" ");
                          cnt += Integer.parseInt(inpr.substring(73));
                     }
                     String pl = sb.toString();
                     sb=null;
                     if (cnt > 0)
                         System.out.println(pl);
               }   
            }
      } catch (Exception exp) {
          exp.printStackTrace();
      }    
 //     if (verbose) System.out.println(fn + " Number of lines searched :" + countLines + "   Number of hits: " + count);
      if (outp != null)
        outp.flush();
    }

    public void dumpWFCs(int que){
      String inpr = null;
      try {
           while (true) {
               if ((inpr = inp.readLine()) == null)
                    break;
                 int cnt = 0;
                 String tmp = inpr.substring(34,37);    //  Time and date
                 if (tmp.equalsIgnoreCase("INS")) {
                     System.out.println(inpr);
               }   
            }
      } catch (Exception exp) {
          exp.printStackTrace();
      }    
 //     if (verbose) System.out.println(fn + " Number of lines searched :" + countLines + "   Number of hits: " + count);
      if (outp != null)
        outp.flush();
    }

    public void findInFiles(int action) {
//        SearchJars.setSubFolder(subf);
//        SearchJars.setFilter(filter);
        boolean listOnly = false;
        String st = getSearchPhrase();
        SearchJars sj = new SearchJars();
        Vector files = sj.scanFolderV(getFolder());
        Enumeration e = files.elements();
        while (e.hasMoreElements()) {
            String fn = e.nextElement().toString();
            if (listOnly) {
                System.out.println(fn);
            } else {
                    openInFile(fn);
                    if (action == 1) {
//                    if (FromDate == null) {
                      doRawQueues(5);
                } else {
                    dumpWFCs(0);
                }
            }        
       }
        System.out.println("done Logs");
    }
    public void analyze(int action) {
        openInFile(inFileName);
                if (action == 1) 
            findInFiles(5);
        else if (action == 2) {
            doRawQueues(5);
        } else {
            
        } 
    }
    /**
     * @param args the command line arguments
     */
    protected void showUsage() {
        System.out.println("-usage  display command-line options");
        System.out.println("-dir  Root Directory of search (madatory)");
        System.out.println("-subf  Search all subDirectories (default = true");
        System.out.println("-filter Give filename to search (default = '*') ");
        System.out.println("-v  verbose   (default = false");
        System.out.println("-s  Search phrase (( -s 'look for this')");
        System.out.println("-csv delimiter for  i.e Excel handling");
        System.out.println("-tf  pick time from i.e 2008-06-16 14:52:47");
        System.out.println("-tt  pick time to   i.e 2008-06-16 14:53:40");
    }
    protected void checkArgs(String args[]) {
      String validArgs[] ={"-usage", "-dir", "-subf", "-filter", "-v","-csv","-s","-tf","-tt"};
      int argPos;
      String tmp = null;
      if (args.length == 0) {
         showUsage();
         System.exit(1);
      }
      SearchJars.setFilter("*");
      int i = 0;
      while(i<args.length) {
         argPos = -1;
         for ( int argNum=0; argNum < validArgs.length; argNum++ ) {
            if ( validArgs[argNum].compareTo(args[i]) == 0 ) {
               argPos = argNum;
               break;
            }
         }
         switch ( argPos ) {
            case 0:   // usage
               showUsage();
               break;
            case 1:   // Directory
               i++;
               tmp = args[i];
               if (tmp != null ) {
                  setFolder(args[i]);
               } else {
                   showUsage();    
               }
               break;
            case 2:   // subfolders
               i++;
               tmp = args[i];
               if (tmp != null && tmp.equalsIgnoreCase("false")) {
                   setSubf(false);
                   SearchJars.setSubFolder(false);
                   
               } else {
                   setSubf(true);    
                   SearchJars.setSubFolder(true);
               }
               break;
            case 3:   // apserver config file - apservsetup is expected
               i++;
               SearchJars.setFilter(args[i]);
               inFileName = args[i];
               break;
            case 4:   // verbose
            //   i++;
               verbose = true;
               break;
            case 5:   // search phrase
                i++;
                csv = args[i];
               break;
             case 6:   // search phrase
                i++;
                setSearchPhrase(args[i]);
               break;
             case 7:   // From data & time 
                i++;
                FromDate = getDate(args[i]);
                tmp = getTime(args[i]);
                fromTime = getMS(tmp);
               break;
             case 8:   // -tt
                i++;
                ToDate = getDate(args[i]);
                tmp = getTime(args[i]);
                toTime = getMS(tmp);
                break;
             default: // unknown option
               System.err.println("unknown option: " + args[i]);
               showUsage();
               System.exit(1);
               break;
         }
         if (verbose) System.out.println(args[i]);
         i++;
      }
   }    
    public static void main(String[] args) {
      QWAnalyze lg = new QWAnalyze();
      lg.checkArgs(args);
 //     lg.analyze(1);
      lg.findInFiles(2);
//       lg.openFile(path + "httpclient.log.D20071121.T114744",path + "sys1.out.txt");
//      lg.openle(path + "\\httpclient.log.D20071121.T114744",path + "\\out.tmp.end.txt");
 //     lg.openFile(path + "gaptest.txt",path + "sys1.out.txt");
 //     lg.spinThrough();
//        lg.listEntries("HTTPClientEndService.primitiveProcessData() - completed, ended session");

}
}    
