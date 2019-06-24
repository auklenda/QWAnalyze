/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;
/**
 *
 * @author aauklend
 */
public class QWAGlobal {
 public static int  [][] histoGram = new int [9][10];
 public static int [][] threadConfig = new int [9][2];
 public static int [] maxDepth =  new int [9];
 public static int [] calcHigherThread = new int[9];
 public static String [] maxDepthTime = new String[9];
 public static int [] qs = new int[9];
 public static int [] allThreadsInUse = new int[9];
 public static int [] anyThreadsInUse = new int[9];
 public static int [] waitersForThreads = new int[9];
 public static int [] fairShareImposed = new int[9];
 static int que = 1;
 static int wfc = 2;
 static int mem = 3;
 static int hdr = 4;
 static int jdbc = 5;
 static int cfg = 6;
 static int env = 7;
 static int noop = 8;
 static BufferedReader QWFile = null;
 static PrintWriter htmlFile = null;
 static PrintWriter csvFile = null;
 public static javax.swing.JTextField jTextField1;
 static String start = null;
 static String startDate = null;
 static String last = null;
 static String nodeName = null;
 static String procs = null;
 static String hdrHost = null;
 static String hdrPort = null;
 static String hdrRate = null;
 static String hdrNode = null;
 static String hdrTH = null;
 static String hdrMemory = null;
 static String hdrWFID = null;
 static String QWFileName = null;
 static String outFolder = "./";
 static HashMap bpList = null;
 static ArrayList queList = null;
 static String project="TEST";
 static ArrayList executePoolList = null;
 static int nOfNodes = 1;
 static String [] nodeNames = new String[2]; // NB!!! to be in sync with extractor reporter
 static int MaxThreads = -1;
 static HashMap bpListnew;
 static boolean isCSV = false;
 static long qwFileLength = 0l;
 static long qwFuzzyRecs = 0l;
 static long qwFuzzyRecLng = 180;
 static long [][] queStat = new long[9][4];
}
