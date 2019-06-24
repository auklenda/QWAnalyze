/*
 * QWAnaGUI.java
 *
 * Created on 02 October 2008, 19:52
 */
package com.sterlingcommerce.emea.support.l2.qwa;
//
import com.sterlingcommerce.emea.support.l2.qwa.QWAnalyze.listHelper;
import java.awt.Color;
import java.io.*;
import java.util.*;
import java.awt.Component;
import java.awt.Font;
//
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import org.jfree.data.category.CategoryDataset;
// XYDataset
import org.jfree.data.time.TimeSeriesCollection;
//import org.jfree.data.time.RegularTimePeriod;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.text.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.awt.*;
import javax.swing.SwingUtilities;
/**
 *
 * @author  AAuklend
 */
public class QWAnaGUI extends javax.swing.JFrame  {
    String inFile = null;
    //String outFolder = "./";
    String currentFolder = "C:";
    String qwOutFileName = null;
    QWAnalyze qwAnalyze = null;
    BufferedReader QWFile = null;
    boolean quePrt = true;
    boolean wfdPrt = false;
    boolean memPrt = false;
    boolean graphPrt = true;
    boolean listWfc_html = false;
    static boolean isCSV = false;
    public static boolean [] prtHtml = {true,false,false,false,false,false,false,false,false,false,false};
    String editor = "notepad";
    String jpgChart = null;
    int browserIndex = 0;
    int threshold =0;
    String[] browser = {"C:\\Program Files\\Internet Explorer\\iexplore.exe",
        "C:\\Program Files\\Mozilla Firefox\\firefox.exe"};
    Properties props = null;
    Properties graphList = null;
    String propsFN = "QWAnalyzer.proprties";
    javax.swing.JTable jTableBP;
    Object[][] cells;
    JTextArea jtWaiters = null;
    JLabel jlWaiters = null;
    private boolean initHtml;
    private static final Icon CLOSE_TAB_ICON = new ImageIcon(QWAnaGUI.class.getResource("delX.ico"));
   // private static final Icon PAGE_ICON = new ImageIcon(TabDemoFrame.class.getResource("1353549278_page-edit.png"));
     private final int tabCount = 0;
     
    /** Creates new form QWAnaGUI */
    public QWAnaGUI() {
        super("GIS QWAnalyzer");
        initComponents();
        this.setBackground(Color.red);
        initLocal();
    }
    /**
     *  Init all local stuff
    */
    private void initLocal() {
        qwAnalyze = new QWAnalyze(this);
        qwAnalyze.setQueSelect(true);
//        jCheckBox1.setSelected(true);
        qwAnalyze.depth = 1;
        //Commented  by Priya
        //qwa.jHdr = jHeaderField;
        qwAnalyze.html = false;
        qwAnalyze.memThreshold = 90.0;
        qwAnalyze.depth = 0;
        qwAnalyze.lowerThreadLimit = 0;
        qwAnalyze.jtWaiters = jTextArea5;
        qwAnalyze.jlWaiters = jLabel13;
        QWAGlobal.jTextField1 = jTextField1;
        getVersion();
        loadProps();
   }
    private void getVersion(){
      Properties verProps = new Properties();
      InputStream in = getClass().getResourceAsStream("/appinfo.properties");
      try {
            verProps.load(in);
            QWversion.setVERSION((String)verProps.get("program.VERSION"));
            QWversion.setCOMPANY((String)verProps.get("program.COMPANY"));
            QWversion.setCOPYRIGHT((String)verProps.get("program.COPYRIGHT"));
            QWversion.setDESCRIPTION((String)verProps.get("program.DESCRIPTION"));
            QWversion.setBUILDNUM((String)verProps.get("program.BUILDNUM"));
            QWversion.setBUILDDATE((String)verProps.get("program.BUILDDATE"));
            QWversion.setAUTHOR((String)verProps.get("program.AUTHOR"));
            StringBuilder sb = new StringBuilder();
            sb.append("VERSION: ").append(QWversion.getVERSION()).append("\n");
            sb.append("BUILD: ").append(QWversion.getBUILDNUM()).append("\n");
            sb.append("BUILDDATE: ").append(QWversion.getBUILDDATE()).append("\n");
            //sb.append("COMPANY: ").append(QWversion.getCOMPANY()).append("\n");
            sb.append("AUTHOR: ").append(QWversion.getAUTHOR()).append("\n");
            jTextArea2.append(sb.toString());
      } catch (IOException ex) {
            Logger.getLogger(QWAnaGUI.class.getName()).log(Level.SEVERE, null, ex);
      }
        System.out.println("");
        String version = QWversion.getVERSION();
      
    }        
   public boolean loadProps() {
        props = new Properties();
        //Commented by Priya
        //QWGlobal.props = props;
        try {
            File f = new File(propsFN);
            if (!f.exists()) {
                return false;
            }
            FileInputStream istream = new FileInputStream(propsFN);
            String tmp;
            props.load(istream);
            currentFolder = (String) props.get("lastFolder"); 
            tmp = (String) props.get("workdir");
            if (tmp != null && !tmp.isEmpty()) {
                //Commented by Priya
                //QWGlobal.OutPath = tmp;
                jTextField1.setText(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void saveProps() {
        Properties p = new Properties();
        try {
            File f = new File(propsFN);
            OutputStream ostream = new FileOutputStream(propsFN);
            //    props.load(istream);
            p.put("workdir", jTextField1.getText());
            p.put("lastFolder", currentFolder);
            p.store(ostream, null);
        } catch (Exception e) {
        }
    }
    public static boolean isHTML(int func) {
        if (prtHtml.length > func) {
            if (prtHtml[func])
                return true;
        }    
        return false;
    }
    /**
     *
     * @param file
     */
    public void invokeEditor(String file) {
        String cmd = editor + " " + file;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public String copyCMD(ArrayList al){
        int sz = al.size();
        
        return null;
    }
    // Distplay Tab based on tab Title
    public static void displayTab(javax.swing.JTabbedPane jpan,String tabName) {
        int tabInx = -1;
        // Check if this TAB exist
        for (int i = 0; i < jpan.getTabCount(); i++) {
             String tName = jpan.getTitleAt(i);
             if (tName.equalsIgnoreCase(tabName)) {
                 jpan.setSelectedIndex(i);
                 break;
             }
        }
    }
    public void setTab() {
        javax.swing.JButton btnClose = new javax.swing.JButton(CLOSE_TAB_ICON);
        javax.swing.JPanel tab = new javax.swing.JPanel();
        tab.setOpaque(false);

        int pos = jTabbedPane1.indexOfComponent(jScrollPane1);
        btnClose.setOpaque(false);
     // Configure icon and rollover icon for button
        btnClose.setRolloverIcon(CLOSE_TAB_ICON);
        btnClose.setRolloverEnabled(true);
        //btnClose.setIcon(RGBGrayFilter.getDisabledIcon(btnClose, CLOSE_TAB_ICON));
        btnClose.setIcon(CLOSE_TAB_ICON);
    // Set border null so the button doesn't make the tab too big
        btnClose.setBorder(null);
    // Make sure the button can't get focus, otherwise it looks funny
        btnClose.setFocusable(false);
    // Put the panel together
        
       tab.add(new JLabel("lblTitle"));
       tab.add(btnClose);
    // Add a thin border to keep the image below the top edge of the tab
    // when the tab is selected
       //jTabbedPane1.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
 
    // Now assign the component for the tab
       jTabbedPane1.setTabComponentAt(3, tab);
                 
    }
    private void createBPjList(Properties bpNames) {
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        jList1.addListSelectionListener(new ListSelectionListener() {
              @Override
              public void valueChanged(ListSelectionEvent evt) {
                  if (!evt.getValueIsAdjusting())
                      return;
                  String bp = (String) jList1.getSelectedValue();  
                  initFunction();
                  (new QWWWorkFlows(this,"List BP ")).listWFDs(0,7,bp);
                  closeFunction();                  
                    // System.out.println("Selected from " + evt.getFirstIndex() + " to " + evt.getLastIndex());
              }
        });     
        javax.swing.DefaultListModel listModel = new javax.swing.DefaultListModel();
        Vector v = new Vector();
        for (Enumeration en = bpNames.keys(); en.hasMoreElements();) {
            String name = (String) en.nextElement();
            v.add(name);
       }   
       jList1.setListData(v);
    }
     /* 
     * @param rows
     */
    public void createBPTable(int rows) {
        jTableBP = new javax.swing.JTable(new Tablemodel1(rows));
 
        int wth = jScrollPane1.getWidth();
        jScrollPane1.add(jTableBP);
        //
        int name = (wth *60)/100;
        jTableBP.getSelectionModel().addListSelectionListener(new RowListener());
        jTableBP.getColumnModel().getSelectionModel().
                addListSelectionListener(new ColumnListener());
        jTableBP.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTableBP.setRowSelectionAllowed(true); 
        jTableBP.setColumnSelectionAllowed(false);
        jTableBP.addMouseListener(new MouseAdapter(){
           @Override
           public void mouseClicked(MouseEvent e){
            boolean e1;
            e1 = SwingUtilities.isLeftMouseButton(e);
            e1 = SwingUtilities.isRightMouseButton(e);
            e1 = SwingUtilities.isMiddleMouseButton(e);
               
             if (e.getClickCount() == 2){
               System.out.println(" double click" );
              }
           }
        } );
        int left = (wth - name);
        
        jTableBP.getColumnModel().getColumn(0).setPreferredWidth(wth - name);
        jTableBP.getColumnModel().getColumn(1).setPreferredWidth(left/4);
        jTableBP.getColumnModel().getColumn(2).setPreferredWidth(left/4);
        jTableBP.getColumnModel().getColumn(3).setPreferredWidth(left/4);
        jTableBP.getColumnModel().getColumn(4).setPreferredWidth(left/4);
        jTableBP.getColumnModel().getColumn(5).setPreferredWidth(left/4);
        
        jTableBP.setAutoCreateRowSorter(true);
        
        jScrollPane1.setViewportView(jTableBP);
    }

    public void updateBPTable(String wfd, int cnt, int row) {
        cells[row][0] = wfd;
        cells[row][1] = cnt;
//        cells[row][2] = wfcnt;
    }
    public void setBPs(Properties p) {
        //int rows = p.size();
        //cells = new Object[rows][2];
        int rec = 0;
        int rows = QWAGlobal.bpListnew.size();
        Set keys = QWAGlobal.bpListnew.keySet();
        Iterator it = keys.iterator();
        cells = new Object[rows][6];
        while (it.hasNext()){
        //for (Enumeration en = p.keys(); en.hasMoreElements();) {
            String name = (String)it.next();
            //String name = (String) en.nextElement();
            //String pn = p.getProperty(name);
            //String q = (String)QWAGlobal.bpList.get(name);
            listHelper lh =  (listHelper)QWAGlobal.bpListnew.get(name);
            if (lh == null)
                System.out.println("");
            //if (name.indexOf("=") == -1) {
                cells[rec][0] = lh.name;
                cells[rec][1] = lh.ques;
                cells[rec][2] = lh.minActive;
                cells[rec][3] = lh.maxActive;
                cells[rec][4] = lh.totActive/lh.totObserved;
                cells[rec][5] = lh.totObserved;
                //Integer.parseInt(pn);
                rec++;
            //}
//            updateBPTable(name, Integer.parseInt(pn), rec++);
        }
        createBPTable(rows);
        createBPjList(p);
    }
    public void setChartNames(String prefix, String nameOfFile){
        if (graphList == null) {
            graphList = new Properties();
        }
        graphList.put(prefix,nameOfFile);
    }
    public String getChartNames(String prefix) {
        if (graphList == null) {
            return(null);
        }
        return((String)graphList.get(prefix));
    }
    public void freeChart() {
    }

    public String setChartFile(String prefix) {
        String pfix = QWUtil.getDateAndTime("yyyyMMdd_HHmmss");
        String fn = QWAGlobal.outFolder + "\\" + prefix + "-" + pfix + ".jpeg";
        
        return QWAGlobal.outFolder + "\\" + prefix + "-" + pfix + ".jpeg";
    }
    public void closeHTMLfile(){
        if (QWAGlobal.htmlFile != null){
            QWAGlobal.htmlFile.flush();
            QWAGlobal.htmlFile.close();
            QWAGlobal.htmlFile = null;
            jHtmlFile.setText("");
        }       
    }    
    private void openHTMLFile(){
        String ext = ".html";
        //String pfix = QWUtil.getDateAndTime("yyyyMMdd_HHmm");
        String tmp = QWAGlobal.start;
        tmp = tmp.replace(" ", "_");
        String pfix = tmp.replace(":","");
        qwOutFileName = QWAGlobal.outFolder + "\\QUE-Report-" + pfix + ext;
        qwAnalyze.setHTMLFile(qwAnalyze.openFile(qwOutFileName));
        jHtmlFile.setText(qwOutFileName);
        QWCreateHTML.createHDR();
    }
    private void openCSVFile(){
        String ext = ".csv";
        //String pfix = QWUtil.getDateAndTime("yyyyMMdd_HHmm");
        String tmp = QWAGlobal.start;
        tmp = tmp.replace(" ", "_");
        String pfix = tmp.replace(":","");
        qwOutFileName = QWAGlobal.outFolder + "\\WFC-Report-" + pfix + ext;
        
        qwAnalyze.setCSVFile(qwAnalyze.openFile(qwOutFileName));
        //jHtmlFile.setText(qwOutFileName);
        //QWCreateHTML.createHDR();
    }
    public void closeCSVfile(){
        if (QWAGlobal.csvFile != null){
            QWAGlobal.csvFile.flush();
            QWAGlobal.csvFile.close();
            QWAGlobal.csvFile = null;
            //jHtmlFile.setText("");
        }       
    }        
    
  public void setUpOutput() {
        String ext = ".html";
        String pfix = QWUtil.getDateAndTime("yyyyMMdd_HHmm");
        qwOutFileName = QWAGlobal.outFolder + "\\QUE-Report-" + pfix + ext;
        qwAnalyze.setQueFile(qwAnalyze.openFile(qwOutFileName));
        jpgChart = QWAGlobal.outFolder + "\\QUE-Report-" + pfix + ".jpeg";
        if (qwAnalyze.quePrt) {
            qwAnalyze.setQueFile(qwAnalyze.openFile(qwOutFileName));
        }
    }

    private void setUpTArea() {
        qwAnalyze.jTA1 = null;
        JTextArea jTextArea01 = new javax.swing.JTextArea();
        qwAnalyze.jTA1 = jTextArea01;
    }

    public void initFunction() {
        qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
        if (qwAnalyze.html) {
            setUpOutput();
            qwAnalyze.htmlBegEnd(true);
        } else {
            setUpTArea();
        }
    }

    private void closeFunction() {
 //       if (qwAnalyze.html) {
            qwAnalyze.htmlBegEnd(false);
//        }
        qwAnalyze.closeQWFile();
        qwAnalyze.closeQueFile();
    }

    private void displayOutput() {
        if (qwAnalyze.html) {
            invokeEditor(qwOutFileName);
        } else {
           // jScrollPane1.setViewportView(qwAnalyze.jTA1);
        }
    }
/*
    private void listWFDs(int th, int inx) {
        initFunction();
        (new QWWfc(this,"WFD Listing",true)).listWFDs(th, inx);
        //qwa.listWFDs(th, inx);
        closeFunction();
    }
*/
    private void listAQueue(int qx) {
        initFunction();
        qwAnalyze.listEachQueue(qx);
        closeFunction();
    }

    private void ListAll() {
        initFunction();
        qwAnalyze.reportWFCs();
        qwAnalyze.closeQWFile();
        qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
        (new QWMemory(this, true)).reportMemory();
        //qwa.reportMemory();
        qwAnalyze.closeQWFile();
        qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
        qwAnalyze.splitFile();
        qwAnalyze.printReport();
        qwAnalyze.closeQWFile();
        qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
//        qwAnalyze.reportQAndWFC();
        qwAnalyze.closeQWFile();
        qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
        qwAnalyze.htmlBegEnd(false);
        qwAnalyze.closeQueFile();
        invokeEditor(qwOutFileName);
    }
    void bumpNumberOfLines(String s){
        jTextField14.setText(s);
    }
private void init_jXTable1(){
  jXTable1.setFont(new Font("Verdana", Font.PLAIN, 12));
  jXTable1.setValueAt("Sample Started", 0, 0);
  jXTable1.setValueAt(QWAGlobal.start, 0, 1);
  jXTable1.setValueAt("Sample Ended", 1, 0);
  jXTable1.setValueAt(QWAGlobal.last, 1, 1);
  jXTable1.setValueAt("Total Records", 2, 0);
  jXTable1.setValueAt(qwAnalyze.countLines, 2, 1);
  jXTable1.setValueAt("Queue Records", 3, 0);
  jXTable1.setValueAt(qwAnalyze.queLines, 3, 1);
  jXTable1.setValueAt("WFC Records", 4, 0);
  jXTable1.setValueAt(qwAnalyze.wfcLines, 4, 1);
  jXTable1.setValueAt("Memory Records", 5, 0);
  jXTable1.setValueAt(qwAnalyze.memLines, 5, 1);
  jXTable1.setValueAt("Node", 6, 0);
  jXTable1.setValueAt(qwAnalyze.nodeName, 6, 1);
  jXTable1.setValueAt("Processors", 7, 0);
  jXTable1.setValueAt(QWAGlobal.procs, 7, 1);
  jXTable1.setValueAt("Host", 8, 0);
  jXTable1.setValueAt(QWAGlobal.hdrHost, 8, 1);
  jXTable1.setValueAt("Port", 9, 0);
  jXTable1.setValueAt(QWAGlobal.hdrPort, 9, 1);
  jXTable1.setValueAt("Rate", 10, 0);
  jXTable1.setValueAt(QWAGlobal.hdrRate, 10, 1);
  jXTable1.setValueAt("Threshold", 11, 0);
  jXTable1.setValueAt(QWAGlobal.hdrTH, 11, 1);
  jXTable1.setValueAt("Node", 12, 0);
  jXTable1.setValueAt(QWAGlobal.hdrNode, 12, 1);
  jXTable1.setValueAt("Memory", 13, 0);
  jXTable1.setValueAt(QWAGlobal.hdrMemory, 13, 1);
}    
private int getIndex(String q) {
    if(q.equalsIgnoreCase("All"))
         return -1;
    return Integer.parseInt(q.substring(1));
} 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel16 = new javax.swing.JPanel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu6 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jCheckBox7 = new javax.swing.JCheckBox();
        jXTaskPaneContainer1 = new org.jdesktop.swingx.JXTaskPaneContainer();
        jXTaskPane1 = new org.jdesktop.swingx.JXTaskPane();
        jXHyperlink16 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink10 = new org.jdesktop.swingx.JXHyperlink();
        jXTaskPane2 = new org.jdesktop.swingx.JXTaskPane();
        jXHyperlink4 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink8 = new org.jdesktop.swingx.JXHyperlink();
        jXTaskPane3 = new org.jdesktop.swingx.JXTaskPane();
        jXHyperlink6 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink7 = new org.jdesktop.swingx.JXHyperlink();
        jXTaskPane5 = new org.jdesktop.swingx.JXTaskPane();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink11 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink12 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink13 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink14 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink15 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink17 = new org.jdesktop.swingx.JXHyperlink();
        jXTaskPane4 = new org.jdesktop.swingx.JXTaskPane();
        jTextArea2 = new javax.swing.JTextArea();
        jXTaskPane6 = new org.jdesktop.swingx.JXTaskPane();
        jXHyperlink18 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink3 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink9 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink19 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink20 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink21 = new org.jdesktop.swingx.JXHyperlink();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jPanel22 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jHtmlFile = new org.jdesktop.swingx.JXTextField();
        jXButton1 = new org.jdesktop.swingx.JXButton();
        jXButton3 = new org.jdesktop.swingx.JXButton();
        jXButton4 = new org.jdesktop.swingx.JXButton();
        jTextField14 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox();
        jPanel15 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jComboBox13 = new javax.swing.JComboBox();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel17 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jPanelFairShare1 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        jLabel56 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox();
        jComboBox10 = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jComboBox14 = new javax.swing.JComboBox();
        jLabel50 = new javax.swing.JLabel();
        jComboBox15 = new javax.swing.JComboBox();
        jXButton2 = new org.jdesktop.swingx.JXButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox();
        jLabel24 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox();
        jLabel53 = new javax.swing.JLabel();
        jComboBox18 = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        jComboBox19 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jComboBox20 = new javax.swing.JComboBox();
        jComboBox21 = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox();
        jScrollPane13 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel23 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jComboBox16 = new javax.swing.JComboBox();
        jComboBox17 = new javax.swing.JComboBox();
        jLabel65 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jLabel66 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenuItem32 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenu2 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenuItem36 = new javax.swing.JMenuItem();
        jMenuItem37 = new javax.swing.JMenuItem();
        jMenuItem38 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jMenu6.setText("File");
        jMenuBar2.add(jMenu6);

        jMenu7.setText("Edit");
        jMenuBar2.add(jMenu7);

        jCheckBox7.setText("jCheckBox7");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(117, 150, 227));
        setResizable(false);

        jXTaskPaneContainer1.setLayout(new org.jdesktop.swingx.VerticalLayout());

        jXTaskPane1.setTitle("Queues");
        jXTaskPane1.getContentPane().setLayout(new org.jdesktop.swingx.VerticalLayout());

        jXHyperlink16.setText("Summary");
        jXHyperlink16.setToolTipText("Thread usage pr. queue");
        jXHyperlink16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink16ActionPerformed(evt);
            }
        });
        jXTaskPane1.getContentPane().add(jXHyperlink16);

        jXHyperlink10.setText("Selections");
        jXHyperlink10.setToolTipText("Make finer granule selection");
        jXHyperlink10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink10ActionPerformed(evt);
            }
        });
        jXTaskPane1.getContentPane().add(jXHyperlink10);

        jXTaskPaneContainer1.add(jXTaskPane1);

        jXTaskPane2.setTitle("Business Process");
        jXTaskPane2.getContentPane().setLayout(new org.jdesktop.swingx.VerticalLayout());

        jXHyperlink4.setText("List All");
        jXHyperlink4.setToolTipText("List ALL executing BP's for ALL Queues");
        jXHyperlink4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink4ActionPerformed(evt);
            }
        });
        jXTaskPane2.getContentPane().add(jXHyperlink4);

        jXHyperlink5.setText("BP Summary");
        jXHyperlink5.setToolTipText("Show individual BP count (as seen by QWW)");
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink5ActionPerformed(evt);
            }
        });
        jXTaskPane2.getContentPane().add(jXHyperlink5);

        jXHyperlink8.setText("Histogram");
        jXHyperlink8.setToolTipText("Show occurance of waiting BP's pr queue");
        jXHyperlink8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink8ActionPerformed(evt);
            }
        });
        jXTaskPane2.getContentPane().add(jXHyperlink8);

        jXTaskPaneContainer1.add(jXTaskPane2);

        jXTaskPane3.setTitle("Memory");
        jXTaskPane3.getContentPane().setLayout(new org.jdesktop.swingx.VerticalLayout());

        jXHyperlink6.setText("Usage");
        jXHyperlink6.setToolTipText("List free memory %");
        jXHyperlink6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink6ActionPerformed(evt);
            }
        });
        jXTaskPane3.getContentPane().add(jXHyperlink6);

        jXHyperlink7.setText("Changes");
        jXHyperlink7.setToolTipText("Heap Expansion / Contraction ");
        jXHyperlink7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink7ActionPerformed(evt);
            }
        });
        jXTaskPane3.getContentPane().add(jXHyperlink7);

        jXTaskPaneContainer1.add(jXTaskPane3);

        jXTaskPane5.setTitle("Graphs");
        jXTaskPane5.getContentPane().setLayout(new org.jdesktop.swingx.VerticalLayout());

        jXHyperlink1.setText("Total Waiting For Thread");
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });
        jXTaskPane5.getContentPane().add(jXHyperlink1);

        jXHyperlink11.setText("Waiting For Thread");
        jXHyperlink11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink11ActionPerformed(evt);
            }
        });
        jXTaskPane5.getContentPane().add(jXHyperlink11);

        jXHyperlink12.setText("Active Threads pr Queue");
        jXHyperlink12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink12ActionPerformed(evt);
            }
        });
        jXTaskPane5.getContentPane().add(jXHyperlink12);

        jXHyperlink13.setText("Total Active Threads");
        jXHyperlink13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink13ActionPerformed(evt);
            }
        });
        jXTaskPane5.getContentPane().add(jXHyperlink13);

        jXHyperlink14.setText("Heap Usage");
        jXHyperlink14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink14ActionPerformed(evt);
            }
        });
        jXTaskPane5.getContentPane().add(jXHyperlink14);

        jXHyperlink15.setText("BP Usage");
        jXHyperlink15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink15ActionPerformed(evt);
            }
        });
        jXTaskPane5.getContentPane().add(jXHyperlink15);

        jXHyperlink17.setText("JDBC Usage");
        jXHyperlink17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink17ActionPerformed(evt);
            }
        });
        jXTaskPane5.getContentPane().add(jXHyperlink17);

        jXTaskPaneContainer1.add(jXTaskPane5);

        jXTaskPane4.setTitle("QWA");
        jXTaskPane4.getContentPane().setLayout(new org.jdesktop.swingx.VerticalLayout());

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jTextArea2.setRows(5);
        jXTaskPane4.getContentPane().add(jTextArea2);

        jXTaskPaneContainer1.add(jXTaskPane4);

        jXTaskPane6.setTitle("Various");
        jXTaskPane6.getContentPane().setLayout(new org.jdesktop.swingx.VerticalLayout());

        jXHyperlink18.setText("QueuePool");
        jXHyperlink18.setToolTipText("Heap Expansion / Contraction ");
        jXHyperlink18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink18ActionPerformed(evt);
            }
        });
        jXTaskPane6.getContentPane().add(jXHyperlink18);

        jXHyperlink2.setText("HTML Report");
        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink2ActionPerformed(evt);
            }
        });
        jXTaskPane6.getContentPane().add(jXHyperlink2);

        jXHyperlink3.setText("HTML Q-Frequency");
        jXHyperlink3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink3ActionPerformed(evt);
            }
        });
        jXTaskPane6.getContentPane().add(jXHyperlink3);

        jXHyperlink9.setText("HTML Q-Stat");
        jXHyperlink9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink9ActionPerformed(evt);
            }
        });
        jXTaskPane6.getContentPane().add(jXHyperlink9);

        jXHyperlink19.setText("HTML BP Summary");
        jXHyperlink19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink19ActionPerformed(evt);
            }
        });
        jXTaskPane6.getContentPane().add(jXHyperlink19);

        jXHyperlink20.setText("CSV WFC List");
        jXHyperlink20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink20ActionPerformed(evt);
            }
        });
        jXTaskPane6.getContentPane().add(jXHyperlink20);

        jXHyperlink21.setText("CSV Close");
        jXHyperlink21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink21ActionPerformed(evt);
            }
        });
        jXTaskPane6.getContentPane().add(jXHyperlink21);

        jXTaskPaneContainer1.add(jXTaskPane6);

        jTabbedPane1.setBackground(new java.awt.Color(117, 150, 227));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(214, 223, 247));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton4.setText("OutFolder");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton1.setText("QW File");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField2)
                    .addComponent(jTextField1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13))
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/sterlingcommerce/emea/support/QWA_long.gif"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable1.setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(jXTable1);

        jPanel22.setPreferredSize(new java.awt.Dimension(247, 271));

        jLabel48.setText("Architecture");

        jLabel51.setText("Procs");

        jLabel57.setText("OS Version");

        jLabel58.setText("VM Start");

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel59.setText("VM Uptime");

        jLabel60.setText("VM Version");

        jLabel61.setText("VM Vendor");

        jLabel64.setText("OSName");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField6)
                    .addComponent(jTextField7)
                    .addComponent(jTextField8)
                    .addComponent(jTextField9)
                    .addComponent(jTextField11)
                    .addComponent(jTextField10)
                    .addComponent(jTextField12)
                    .addComponent(jTextField13, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51))
                .addGap(7, 7, 7)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64))
                .addGap(4, 4, 4)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jHtmlFile.setText("HTMLfile");

        jXButton1.setText("C");
        jXButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXButton1ActionPerformed(evt);
            }
        });

        jXButton3.setText("F");
        jXButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXButton3ActionPerformed(evt);
            }
        });

        jXButton4.setText("N");
        jXButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXButton4ActionPerformed(evt);
            }
        });

        jTextField14.setText("jTextField14");
        jTextField14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jHtmlFile, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jXButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jXButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jXButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 120, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jHtmlFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jXButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Home", jPanel3);

        jPanel4.setBackground(new java.awt.Color(117, 150, 227));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1040, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("BP Summary", jPanel4);

        jPanel9.setBackground(new java.awt.Color(117, 150, 227));

        jTextArea4.setBackground(new java.awt.Color(214, 223, 247));
        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jTextArea4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Queue Usage", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 11))); // NOI18N
        jScrollPane4.setViewportView(jTextArea4);

        jTextArea6.setBackground(new java.awt.Color(214, 223, 247));
        jTextArea6.setColumns(20);
        jTextArea6.setRows(5);
        jTextArea6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Heap Usage", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 11))); // NOI18N
        jScrollPane7.setViewportView(jTextArea6);

        jTabbedPane3.setBackground(new java.awt.Color(214, 223, 247));

        jTabbedPane4.setName("Heap"); // NOI18N
        jTabbedPane4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane4MouseClicked(evt);
            }
        });

        jScrollPane8.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        );

        jTabbedPane4.addTab("Heap Usage", jPanel18);

        jScrollPane9.setViewportView(jTextPane2);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 562, Short.MAX_VALUE)
            .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
            .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("All Queues", jPanel19);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane3)
                            .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane4)
        );

        jTabbedPane1.addTab("Summary", jPanel9);

        jPanel11.setBackground(new java.awt.Color(214, 223, 247));

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jScrollPane5.setViewportView(jTextArea5);

        jLabel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Queue Service Time"));

        jLabel16.setText("Queue");

        jLabel17.setText("Started");

        jLabel18.setText("Cleared");

        jLabel19.setText("Initial  Depth");

        jLabel31.setText("Min Depth");

        jLabel32.setText("Max Depth");

        jLabel52.setText("Wait");

        jLabel62.setText("Date");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(29, 29, 29)
                        .addComponent(jLabel62)
                        .addGap(45, 45, 45)
                        .addComponent(jLabel17)
                        .addGap(41, 41, 41)
                        .addComponent(jLabel18)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE))
                        .addGap(30, 30, 30))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel62)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32)
                            .addComponent(jLabel31)
                            .addComponent(jLabel19)
                            .addComponent(jLabel52)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Waiting", jPanel11);

        jPanel13.setBackground(new java.awt.Color(214, 223, 247));

        jLabel29.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Connection", "Maximum", "GetItems", "Buffered Req", "Activities", "PoolUsage" }));
        jComboBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox12ActionPerformed(evt);
            }
        });

        jTabbedPane2.setBackground(new java.awt.Color(214, 223, 247));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1030, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE))
        );

        jLabel33.setText("GRAPH:");

        jLabel34.setText("TABLE:");

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Connection", "Maximum", "GetItems", "Wait", "Buffered", "DelReq", "BadItem", "Activities", " " }));
        jComboBox13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(95, 95, 95)
                .addComponent(jLabel34)
                .addGap(18, 18, 18)
                .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(485, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 1016, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("JDBC Pool", jPanel13);

        jPanel14.setBackground(new java.awt.Color(214, 223, 247));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 899, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );

        jScrollPane6.setViewportView(jPanel17);

        jTextField4.setBackground(new java.awt.Color(214, 223, 247));
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel30.setText(" ");
        jLabel30.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel35.setText(" ");
        jLabel35.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel36.setText(" ");
        jLabel36.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel37.setText(" ");
        jLabel37.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel42.setText("Max step:");

        jLabel43.setText("Count:");

        jLabel44.setText(" ");
        jLabel44.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel45.setText("Avg Step:");

        jLabel46.setText(" ");
        jLabel46.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel38.setText("Min Step:");

        jLabel39.setText("Max Time:");

        jLabel40.setText("Min Time:");

        jLabel41.setText("Avg Time:");

        jLabel47.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 901, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel43)
                        .addComponent(jLabel44)
                        .addComponent(jLabel42)
                        .addComponent(jLabel37)
                        .addComponent(jLabel38)
                        .addComponent(jLabel35)
                        .addComponent(jLabel45)
                        .addComponent(jLabel46)
                        .addComponent(jLabel39)
                        .addComponent(jLabel30)
                        .addComponent(jLabel40)
                        .addComponent(jLabel36)
                        .addComponent(jLabel41))
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Threads", jPanel14);

        jPanel21.setBackground(new java.awt.Color(102, 204, 255));
        jPanel21.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jPanelFairShare1.setBackground(new java.awt.Color(153, 255, 255));

        javax.swing.GroupLayout jPanelFairShare1Layout = new javax.swing.GroupLayout(jPanelFairShare1);
        jPanelFairShare1.setLayout(jPanelFairShare1Layout);
        jPanelFairShare1Layout.setHorizontalGroup(
            jPanelFairShare1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1001, Short.MAX_VALUE)
        );
        jPanelFairShare1Layout.setVerticalGroup(
            jPanelFairShare1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 615, Short.MAX_VALUE)
        );

        jScrollPane10.setViewportView(jPanelFairShare1);

        jLabel54.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel54.setText("FareShare Configuration");

        jLabel55.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel55.setText("MaxGlobalThreads:");

        jTextField5.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextField5.setText("jTextField5");

        jScrollPane11.setBackground(new java.awt.Color(102, 153, 255));

        jLabel56.setBackground(new java.awt.Color(51, 153, 255));
        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel56.setText("Queue concurrency view. Shows which queues are most often runing in parallel. Will influence FairShare and the # threads in use.");

        jPanel20.setBackground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel21Layout.createSequentialGroup()
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel55)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel21Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 726, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 276, Short.MAX_VALUE))
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane11))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(jLabel55)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("AE Pools", jPanel21);

        jPanel10.setBackground(new java.awt.Color(214, 223, 247));

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane2.setViewportView(jTextArea3);

        jLabel12.setText(" ");
        jLabel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "General List Box\n", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 12))); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1016, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 734, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Listings", jPanel10);

        jPanel1.setBackground(new java.awt.Color(117, 150, 227));
        jPanel1.setPreferredSize(new java.awt.Dimension(891, 912));

        jPanel2.setBackground(new java.awt.Color(214, 223, 247));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Waiting Queues"));

        jLabel2.setText("List Queues when Wf waited for thread");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Any", ">1", ">5", ">10", ">50", ">100", ">500", ">1000", ">2000 " }));
        jComboBox1.setToolTipText("WFCs waiting for thread");
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel14.setText("List how long Queues Existed");

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "All", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox10ActionPerformed(evt);
            }
        });

        jLabel26.setText("in chunks of ");

        jLabel27.setText("List progress on queue  ");

        jTextField3.setText("100");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel49.setText("List gaps in sample records");

        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "6", "10", "20", "30", "60" }));
        jComboBox14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox14ActionPerformed(evt);
            }
        });

        jLabel50.setText("List longrunners");

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "60", "120", "360", "3600" }));
        jComboBox15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox15ActionPerformed(evt);
            }
        });

        jXButton2.setText("jXButton2");
        jXButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXButton2ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("HTML");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jComboBox14, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jComboBox15, javax.swing.GroupLayout.Alignment.TRAILING, 0, 71, Short.MAX_VALUE))
                                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBox1)))
                        .addGap(91, 91, 91))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(jXButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setBackground(new java.awt.Color(214, 223, 247));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Active Queues"));

        jLabel4.setText("List Queues when more than selected BP threads are active");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", ">0", ">5", ">10", ">20", ">30", ">40", ">50", ">60", ">70", ">80" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel10.setText("List Queue and Mem when more than selected BP threads active");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", ">1", ">5", ">10", ">15", ">20", ">30" }));
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jLabel20.setText("List All  BPs Running longer then selected time in seconds ");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", ">1", ">5", ">10", ">20", ">40", ">50", ">60", ">120", ">300" }));
        jComboBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox7ActionPerformed(evt);
            }
        });

        jLabel22.setText("List Start of  BPs  Running longer  then selected seconds");

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", ">1", ">5", ">10", ">20", ">40", ">50", ">60", ">120", ">300" }));
        jComboBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox8ActionPerformed(evt);
            }
        });

        jLabel24.setText("List WF-ID's observed multiple times on selected queue");

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "1", "2", "3", "4", "5", "6", "7", "8", "9", "ANY" }));
        jComboBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox9ActionPerformed(evt);
            }
        });

        jLabel53.setText("List compressed with threshold");

        jComboBox18.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", ">0", ">5", ">10", ">20", ">30", ">40", ">50", ">60", ">70", ">80" }));
        jComboBox18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(10, 10, 10)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox2, 0, 62, Short.MAX_VALUE)
                    .addComponent(jComboBox5, 0, 62, Short.MAX_VALUE)
                    .addComponent(jComboBox7, 0, 62, Short.MAX_VALUE)
                    .addComponent(jComboBox8, 0, 62, Short.MAX_VALUE)
                    .addComponent(jComboBox9, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox18, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(128, 128, 128))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel53)
                    .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(214, 223, 247));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("List Queues"));

        jLabel6.setText("List individual queues");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jXLabel1.setText("With Depth");

        jComboBox19.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox19ActionPerformed(evt);
            }
        });

        jButton2.setText("A");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("W");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jComboBox20.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox20ActionPerformed(evt);
            }
        });

        jComboBox21.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jXLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton3))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 13, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(214, 223, 247));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("List Memoery"));

        jLabel7.setText("When wit less than % Heap available");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(274, 274, 274))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox4)))
        );

        jPanel12.setBackground(new java.awt.Color(214, 223, 247));
        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("JDBC Pool Activities"));

        jLabel28.setText("JDBC connections");

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "none", "Connections", "GetItem", "Maximum", "Buffered Req", "Activities" }));
        jComboBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102)
                .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jList1.setBackground(new java.awt.Color(214, 223, 247));
        jList1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("List BP")));
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane13.setViewportView(jList1);

        jPanel23.setBackground(new java.awt.Color(213, 223, 247));
        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Graphs"));

        jLabel63.setText("WAITING On Queue");

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox16ActionPerformed(evt);
            }
        });

        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9" }));
        jComboBox17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox17ActionPerformed(evt);
            }
        });

        jLabel65.setText("ACTIVE On Queue");

        jButton12.setText("Log");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel66.setText("Display Raw Log ");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12)
                .addGap(32, 32, 32))
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65))
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12)
                    .addComponent(jLabel66))
                .addContainerGap(134, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Select", jPanel1);

        jMenuBar1.setBackground(new java.awt.Color(117, 150, 227));

        jMenu1.setText("File");

        jMenuItem31.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem31.setText("Open...");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem31);

        jMenuItem32.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem32.setText("Exit");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem32);
        jMenu1.add(jSeparator1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("ActiveList");

        jMenu4.setText("More than ..");

        jMenuItem2.setText("0  - BPs running");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);

        jMenuItem3.setText("10");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem3);

        jMenuItem1.setText("20");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem1);

        jMenuItem4.setText("30");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuItem5.setText("40");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuItem6.setText("50");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem6);

        jMenuItem7.setText("60");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);

        jMenuItem8.setText("70");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuItem9.setText("80");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem9);

        jMenuItem10.setText("90");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem10);

        jMenuItem11.setText("100");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem11);

        jMenu2.add(jMenu4);

        jMenu5.setText("Mem And Wait..");

        jMenuItem13.setText("0 - BPs running");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        jMenuItem14.setText("10");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenuItem15.setText("20");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jMenuItem16.setText("30");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem16);

        jMenuItem17.setText("40");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem17);

        jMenuItem18.setText("50");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem18);

        jMenuItem19.setText("60");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem19);

        jMenuItem20.setText("70");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem20);

        jMenuItem21.setText("80");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem21);

        jMenuItem22.setText("90");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem22);

        jMenuItem23.setText("100");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem23);

        jMenu2.add(jMenu5);

        jMenu8.setText("Run for more than..");

        jMenuItem12.setText("1   secconds");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem12);

        jMenuItem24.setText("5");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem24);

        jMenuItem25.setText("10");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem25);

        jMenuItem26.setText("20");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem26);

        jMenuItem27.setText("60");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem27);

        jMenuItem28.setText("120");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem28);

        jMenuItem29.setText("300");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem29);

        jMenu2.add(jMenu8);

        jMenu9.setText("When it started..");
        jMenu9.setToolTipText("When those active more than nn seconds started");

        jMenuItem30.setText("1  - secconds");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem30);

        jMenuItem33.setText("5");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem33);

        jMenuItem34.setText("10");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem34);

        jMenuItem35.setText("20");
        jMenuItem35.setToolTipText("When did those running longer than nn seconds start ?");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem35);

        jMenuItem36.setText("60");
        jMenuItem36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem36ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem36);

        jMenuItem37.setText("120");
        jMenuItem37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem37ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem37);

        jMenuItem38.setText("300");
        jMenuItem38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem38ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem38);

        jMenu2.add(jMenu9);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("WaitingList");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jXTaskPaneContainer1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXTaskPaneContainer1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
    initFunction();
    qwAnalyze.reportActiveQAndWFC(jTextArea3);
    closeFunction();
    displayOutput();
}//GEN-LAST:event_jMenuItem16ActionPerformed

private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
    JFileChooser fc = new JFileChooser("c:");
    fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
    int ret = fc.showOpenDialog(this);
    if (ret == JFileChooser.APPROVE_OPTION) {
        File f = fc.getSelectedFile();
        inFile = f.getAbsolutePath();
        QWAGlobal.outFolder = f.getParent() + "\\result";
        new File(QWAGlobal.outFolder).mkdir();
        jTextField1.setText(inFile);
        qwAnalyze.setQWFileName(inFile);
        qwAnalyze.setQWFile(qwAnalyze.openInFile(inFile, 0));
        qwAnalyze.collectStats();
        init_jXTable1();
        closeFunction();
        initFunction();
        setBPs(qwAnalyze.countBPs());
        closeFunction();
    }
}//GEN-LAST:event_jMenuItem31ActionPerformed

private void jXHyperlink4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink4ActionPerformed
    initFunction();
    //listWFDs(0,1);
    //(new QWWfc(this,"WFD Listing",true,"List All Active BPs on any Queue")).listWFDs(0, 2);
    (new QWWWorkFlows(this,"List All Active BPs on any Queue")).listWFDs(0,2);
    //qwa.reportBP(null, qwAnalyze.wfcLines);
    closeFunction();
}//GEN-LAST:event_jXHyperlink4ActionPerformed

private void jXHyperlink5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink5ActionPerformed
    //jTabbedPane1.setSelectedIndex(2);
    displayTab(jTabbedPane1,"BP Summary");
}//GEN-LAST:event_jXHyperlink5ActionPerformed
// 
private void jXHyperlink8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink8ActionPerformed
    initFunction();
    qwAnalyze.listHistogram();
    closeFunction();
}//GEN-LAST:event_jXHyperlink8ActionPerformed

private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
    closeFunction();
    closeHTMLfile();
    System.exit(0);
}//GEN-LAST:event_jMenuItem32ActionPerformed

private void jXHyperlink6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink6ActionPerformed
    // TODO add your handling code here:
    initFunction();
    (new QWMemory(this, true)).reportMemory();
    //qwa.reportMemory();
    closeFunction();
}//GEN-LAST:event_jXHyperlink6ActionPerformed

private void jXHyperlink7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink7ActionPerformed
    // TODO add your handling code here:
    initFunction();
    qwAnalyze.reportMemoryFlux();
    closeFunction();
}//GEN-LAST:event_jXHyperlink7ActionPerformed

/**
 * Report how lon queues last
 * @param evt
 */
private void jXHyperlink10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink10ActionPerformed
  jTabbedPane1.setSelectedIndex(1);
  jComboBox6.setFocusable(true);
}//GEN-LAST:event_jXHyperlink10ActionPerformed

private void jXHyperlink11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink11ActionPerformed
    qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("QueueDepths");
    QWTCharts QWtchart = new QWTCharts(this, "BPs on the Wait Queue", false);
    QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.createTestThreadDataset(5), "Queue Depths - " + qwAnalyze.nodeName, "Queue size", QWAGlobal.start, QWAGlobal.last);
    QWtchart.setVisible(true);
    qwAnalyze.closeQWFile();
}//GEN-LAST:event_jXHyperlink11ActionPerformed
// Active pr Q graph
private void jXHyperlink12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink12ActionPerformed
    qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("ActiveThreads");
    QWTCharts QWtchart = new QWTCharts(this, "Queues with Active BPs", false);
    QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.createTestThreadDataset(1), "Executing Bps - " + qwAnalyze.nodeName, "Threads In Use", QWAGlobal.start, QWAGlobal.last);
    Rectangle r = QWtchart.getBounds();
    Random rnd = new Random();
    r.setLocation(rnd.nextInt(150), rnd.nextInt(70));
    QWtchart.setBounds(r);
    QWtchart.setVisible(true);
    qwAnalyze.closeQWFile();

}//GEN-LAST:event_jXHyperlink12ActionPerformed
// Total Threads (pool)
private void jXHyperlink13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink13ActionPerformed
    qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("ActiveThreads");
    QWTCharts QWtchart = new QWTCharts(this, "Total Active BPs", false);
    QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.createActiveThreadDataset(1, " Total Active BP Threads"), "Total Active Bp's - " + qwAnalyze.nodeName, "Threads In Use", QWAGlobal.start, QWAGlobal.last);
    Rectangle r = QWtchart.getBounds();
    Random rnd = new Random();
    r.setLocation(rnd.nextInt(150), rnd.nextInt(70));
    QWtchart.setBounds(r);
    QWtchart.setVisible(true);
    //  createQChart((TimeSeriesCollection)qwAnalyze.createActiveThreadDataset(1," Total Active Queue Threads"),"Total Active Bp's","Threads In Use");
    qwAnalyze.closeQWFile();
}//GEN-LAST:event_jXHyperlink13ActionPerformed

private void jXHyperlink14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink14ActionPerformed
    qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("HeapMemory");
    QWCharts QWchart = new QWCharts(this, "Heap memory", false);
    QWchart.createMemChart((TimeSeriesCollection) qwAnalyze.createMemoryDataset("Memory"), "Heap usage - " + qwAnalyze.nodeName, "Heap Comitted(GB)", QWAGlobal.start, QWAGlobal.last);
    QWchart.setVisible(true);
    qwAnalyze.closeQWFile();
}//GEN-LAST:event_jXHyperlink14ActionPerformed

private void jXHyperlink15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink15ActionPerformed
    qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("BPList");
    ArrayList al = new ArrayList();
    long st = System.currentTimeMillis();
    //Properties p = qwAnalyze.observedBPgraph();
    HashMap hm = qwAnalyze.observedBPgraph();
    Iterator it = hm.entrySet().iterator();
    /*
    for (Enumeration en = p.keys();en.hasMoreElements();){
        String name = (String)en.nextElement();
        String cnt = p.getProperty(name);
        int count = Integer.parseInt(cnt);
        al.add(new BPcompare(name,count));
    }
    */
    while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        QWBPNameInfo bpObj = (QWBPNameInfo)pair.getValue();
        al.add(bpObj);
        //System.out.println(pair.getKey() + " = " + pair.getValue());
    }
    //Collections.sort(al, (BPcompare u1, BPcompare u2) -> u1.getCount().compareTo(u2.getCount()));    
    Collections.sort(al, new Comparator<QWBPNameInfo>() {
    @Override
    public int compare(QWBPNameInfo u1, QWBPNameInfo u2) {
      //return u2.compareTo(u1);
      return u2.count - u1.count;
    }
    });  
/*    
    Collections.sort(al, new Comparator<BPcompare>() {
    @Override
    public int compare(BPcompare u1, BPcompare u2) {
      //return u2.compareTo(u1);
      return u2.count - u1.count;
    }
    });  
   /* p.clear();
    for (int i = 0; i < 25; i++) {
        BPcompare o = (BPcompare)al.get(i);
        p.put(o.name,String.valueOf(o.count));
    }*/
    System.out.println("time --> " + (System.currentTimeMillis() - st));
    QWBPChart QWbp = new QWBPChart(this, "BPName Chart", false);
    //QWbp.createBPChart((CategoryDataset) QWbp.createDataset(qwAnalyze.countBPs(true)), "BP Usage - " + qwAnalyze.nodeName, "Times encountered", QWAGlobal.start, QWAGlobal.last);
    QWbp.createBPChart((CategoryDataset) QWbp.createDataset(al), "BP Usage - " + qwAnalyze.nodeName, "Times encountered", QWAGlobal.start, QWAGlobal.last);
    QWbp.setVisible(true);
    qwAnalyze.closeQWFile();
}//GEN-LAST:event_jXHyperlink15ActionPerformed

private void jXHyperlink16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink16ActionPerformed
    //jTabbedPane1.setSelectedIndex(4);
    displayTab(jTabbedPane1,"Summary");
}//GEN-LAST:event_jXHyperlink16ActionPerformed

private void jXHyperlink17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink17ActionPerformed
    displayJDBC("PoolU",true);
    //jTabbedPane1.setSelectedIndex(6);
     displayTab(jTabbedPane1,"JDBC Pool");
    /*
        initFunction();
        (new QWWJdbc(this,"JDBC Pool",true,"List JDBC Pool")).displayPool();
        closeFunction();
   *
   */
}//GEN-LAST:event_jXHyperlink17ActionPerformed

private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
    jTabbedPane1.getSelectedIndex();
}//GEN-LAST:event_jTabbedPane1StateChanged

private void jComboBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox11ActionPerformed
    String str = jComboBox11.getSelectedItem().toString().substring(0);
    displayJDBC(str,true);
}//GEN-LAST:event_jComboBox11ActionPerformed

private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
    QWUtil.resetInput();
    (new QWAList(this,true)).listQueueInfo();
    //qwa.listQueueInfo(jTextArea3);
}//GEN-LAST:event_jButton12ActionPerformed

private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
    String comboStr = jComboBox4.getSelectedItem().toString();
    if (comboStr.equalsIgnoreCase("none"))
        return;
    double thrsHold = Double.parseDouble(comboStr);
    QWUtil.resetInput();
    (new QWMemory(this, true)).reportMemory(thrsHold);
}//GEN-LAST:event_jComboBox4ActionPerformed

private void jComboBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox9ActionPerformed
    String comboStr = jComboBox9.getSelectedItem().toString().substring(0);
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int queue= -1;
    if (!"ANY".equalsIgnoreCase(comboStr))
        queue = Integer.parseInt(comboStr);
    initFunction();
    //(new QWWfc(this,"WFC Listing",true,"List possible requeued BP's")).listReQueued(queue);
    (new QWWWorkFlows(this,"List possible requeued (long running) BP's")).listReQueued(queue);
    closeFunction();
}//GEN-LAST:event_jComboBox9ActionPerformed

private void jComboBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox8ActionPerformed
    String comboStr = jComboBox8.getSelectedItem().toString().substring(1);
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int active = Integer.parseInt(comboStr)*1000;
    QWUtil.resetInput();
    //(new QWWfc(this,"WFC Listing",true,"List First occurence of BPs running longer than " + active + " ms")).listWFDs(active, 9, null);
    (new QWWWorkFlows(this,"List First occurence of BPs running longer than " + active + " ms")).listWFDs(active, 9, null);
}//GEN-LAST:event_jComboBox8ActionPerformed

private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
    String comboStr = jComboBox7.getSelectedItem().toString().substring(1);
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int active = Integer.parseInt(comboStr)*1000;
    QWUtil.resetInput();
    //(new QWWfc(this,"WFC Listing",true,"List All occurence of BPs running longer than " + active + " ms")).listWFDs(active, 8, null);
    (new QWWWorkFlows(this,"List All occurence of BPs running longer than " + active + " ms")).listWFDs(active, 8, null);
    displayTab(jTabbedPane1,"Threads");  
}//GEN-LAST:event_jComboBox7ActionPerformed

private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
    String comboStr = jComboBox5.getSelectedItem().toString();
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int act = Integer.parseInt(comboStr.substring(1));
    QWUtil.resetInput();
    jTextArea3.setText("");
    qwAnalyze.reportActiveQueANDMemory(act,jTextArea3,jLabel12);
    //jTabbedPane1.setSelectedIndex(3); // setEnabledAt(3,true);
    displayTab(jTabbedPane1,"Listings");  
}//GEN-LAST:event_jComboBox5ActionPerformed

private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
    String comboStr = jComboBox2.getSelectedItem().toString();
    String countStr = comboStr.substring(1);
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int count;
    if(comboStr.equalsIgnoreCase(">0")){
        count = 1;
    } else {
        count = Integer.parseInt(countStr);
   }
    StringBuilder sb = new StringBuilder();
    sb.append("List All BPs if more than ");
    sb.append(count);
    sb.append(" Active Threads (BPs) for a Queue");
    QWUtil.resetInput();
    //(new QWWfc(this,"WFD Listing",true,sb.toStr   ing())).listWFDs(count, 6);
    (new QWWWorkFlows(this,sb.toString())).listWFDs(count, 2);
       
}//GEN-LAST:event_jComboBox2ActionPerformed

private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextField3ActionPerformed

private void jComboBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox10ActionPerformed
    int q= 1;
    String comboStr = jComboBox10.getSelectedItem().toString();
    if (comboStr.equalsIgnoreCase("none"))
        return;
    q = Integer.parseInt(comboStr.substring(1));
    int chunk = Integer.parseInt(jTextField3.getText());
    QWUtil.resetInput();
    qwAnalyze.ListQueueProgress(q-1,chunk,jTextArea3);
    //jTabbedPane1.setSelectedIndex(3);
    displayTab(jTabbedPane1,"Listings");
}//GEN-LAST:event_jComboBox10ActionPerformed

private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
    String comboStr = jComboBox6.getSelectedItem().toString();
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int inx = getIndex(comboStr);
    QWUtil.resetInput();
    qwAnalyze.reportQueueExist(jTextArea5,jLabel13,inx);
    //jTabbedPane1.setSelectedIndex(5);
    //displayTab(jTabbedPane1,"Listings");
    displayTab(jTabbedPane1,"Waiting");
}//GEN-LAST:event_jComboBox6ActionPerformed

private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    String comboStr = jComboBox1.getSelectedItem().toString();
    int count=0;
    if (comboStr.equalsIgnoreCase("none"))
        return;
    if (comboStr.equalsIgnoreCase("ANY")) {
        count = 1;
        // qwAnalyze.reportQueues();
    }
    try {
        count = Integer.parseInt(comboStr.substring(1));
    } catch (Exception exp) {
        System.out.println("");
    }
    QWUtil.resetInput();
    StringBuilder sb = new StringBuilder();
    sb.append("List All BPs if more than ");
    sb.append(count);
    sb.append(" BPs are waiting for a Thread");
    //(new QWWfc(this,"WFD Listing",false,sb.toString())).listWFDs(count, 6);
    //(new QWWfc(this,"WFD Listing", false ,sb.toString(),jScrollPane6)).listWFDs(count, 6);
    (new QWWWorkFlows(this,"WFC Listing")).listWFDs(count, 6,listWfc_html);
}//GEN-LAST:event_jComboBox1ActionPerformed
private void displayJDBC(String str, boolean graph) {
    String str1 = null;
    int action = -1;
    if (str.startsWith("Connection")) {
        action = 0;
        str1 = "Available Pool Connections ";
    } else if (str.startsWith("Maximum")){
        action = 1;
        str1 = "Maximum Connections for each pool  - Note that the Buffered count is not added (default 500)";
    } else if (str.startsWith("GetItems")) {
        action = 2;
        str1 = "Accumulated connedction Items count for pool";
    } else if (str.startsWith("Wait")) {
        action = 3;
        str1 = "Accumulated Wait (for a connection) Requests for pool";
    } else if (str.startsWith("Buffered")) {
        action = 4;
        str1 = "Buffered Requests (temp pooled connection is created)";
    }  else if (str.startsWith("DelReq")) {
        action = 5;
        str1 = "Total Deleted requests for pool since start of SI";
    }  else if (str.startsWith("BadItem")) {
        action = 6;
        str1 = "Total bad item requests for pool since start of SI";
    }  else if (str.startsWith("Activities")) {
        action = 7;
        str1 = "Number of requested connections in this time interval  ";
    }  else if (str.startsWith("PoolUsage")) {
        action = 8;
        str1 = "Total connection requests since start of SI";
    }  else if (action == -1)
        return;

    jLabel29.setText("\t\t" + str1 );
    QWUtil.resetInput();
    //QWTableJdbc qwjdbc = new QWTableJdbc(jScrollPane7,jPanel15);
    //QWTableJdbc qwjdbc = new QWTableJdbc(jScrollPane7,jTabbedPane2);
    QWTableJdbc qwjdbc = new QWTableJdbc(new JScrollPane(),jTabbedPane2);
    //TimeSeriesCollection tsc = qwjdbc.ListJDBCInfo(str,action);
    if (graph)
        qwjdbc.ListJDBCInfo(str,action);
    else qwjdbc.ListJDBCTable(str,action);
    displayTab(jTabbedPane1,"JDBC Pool"); 
    //jTabbedPane1.setSelectedIndex(6);
}
private void jComboBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox12ActionPerformed
    String str = jComboBox12.getSelectedItem().toString().substring(0);
    displayJDBC(str,true);
}//GEN-LAST:event_jComboBox12ActionPerformed

private void jComboBox13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox13ActionPerformed
    String str = jComboBox13.getSelectedItem().toString().substring(0);
    displayJDBC(str,false);

}//GEN-LAST:event_jComboBox13ActionPerformed

private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextField4ActionPerformed

private void jTabbedPane4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane4MouseClicked
        String st = evt.paramString();
        jTabbedPane3.setSelectedIndex(0); // TODO add your handling code here:
}//GEN-LAST:event_jTabbedPane4MouseClicked
// check gaps in sampling interval
    private void jComboBox14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox14ActionPerformed
    String comboStr = jComboBox14.getSelectedItem().toString();
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int act = Integer.parseInt(comboStr.substring(0));
    QWUtil.resetInput();
    jTextArea3.setText("");
    qwAnalyze.reportSamplingGaps(act,jTextArea3,jLabel12);
    //jTabbedPane1.setSelectedIndex(3); // setEnabledAt(3,true);
    displayTab(jTabbedPane1,"Listings");
    }//GEN-LAST:event_jComboBox14ActionPerformed
    // Longrunners
    private void jComboBox15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox15ActionPerformed
    String comboStr = jComboBox15.getSelectedItem().toString();
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int act = Integer.parseInt(comboStr.substring(0));
    QWUtil.resetInput();
    jTextArea3.setText("");
    qwAnalyze.listLongRunners(act,jTextArea3,jLabel12);
    //jTabbedPane1.setSelectedIndex(3); // setEnabledAt(3,true);
    displayTab(jTabbedPane1,"Listings");
    }//GEN-LAST:event_jComboBox15ActionPerformed

    private void jComboBox18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox18ActionPerformed
    String comboStr = jComboBox18.getSelectedItem().toString();
    String countStr = comboStr.substring(1);
    if (comboStr.equalsIgnoreCase("none"))
        return;
    int count;
    if(comboStr.equalsIgnoreCase(">0")){
        count = 1;
    } else {
        count = Integer.parseInt(countStr);
    }
    StringBuilder sb = new StringBuilder();
    sb.append("List All BPs if more than ");
    sb.append(count);
    sb.append(" Active Threads (BPs) for a Queue");
    QWUtil.resetInput();
    //(new QWWfc(this,"WFD Listing",true,sb.toString())).listWFDs(count, 6);
    (new QWAWorkFlowCompressed(this,sb.toString())).listWFDs(count, 2);
    displayTab(jTabbedPane1,"Threads");
    }//GEN-LAST:event_jComboBox18ActionPerformed

    private void jXHyperlink18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink18ActionPerformed
      QWAThreadPool threadPool = new QWAThreadPool(this,true);
      threadPool.createJtable();
//        QWThreadPool threadPool = new QWThreadPool(jPanel20);
//        threadPool.createJtable();
        
    }//GEN-LAST:event_jXHyperlink18ActionPerformed
    private void jMenuItemActive(java.awt.event.ActionEvent evt) {
        String parm = evt.paramString();
        int activeBPs = 0;
        int inx =parm.indexOf("d=");
        int inx1 = parm.indexOf(",", inx);
        String tmp = parm.substring(inx+2,inx1);
        if (!tmp.startsWith("0 ")) {    
            activeBPs = Integer.parseInt(tmp);
        }    
        StringBuilder sb = new StringBuilder();
        sb.append("List All BPs if more than ");
        sb.append(activeBPs);
        sb.append(" Active Threads (BPs) for a Queue");
        QWUtil.resetInput();
    //(new QWWfc(this,"WFD Listing",true,sb.toStr   ing())).listWFDs(count, 6);
        (new QWWWorkFlows(this,sb.toString())).listWFDs(activeBPs, 2);
        displayTab(jTabbedPane1,"Threads");

       }
     private void jMenuItemMem(java.awt.event.ActionEvent evt) {
        String parm = evt.paramString();
        int activeBPs = 0;
        int inx =parm.indexOf("d=");
        int inx1 = parm.indexOf(",", inx);
        String tmp = parm.substring(inx+2,inx1);
        if (!tmp.startsWith("0 ")) {    
            activeBPs = Integer.parseInt(tmp);
        }    
        QWUtil.resetInput();
        jTextArea3.setText("");
        qwAnalyze.reportActiveQueANDMemory(activeBPs,jTextArea3,jLabel12);
    //jTabbedPane1.setSelectedIndex(3); // setEnabledAt(3,true);
        displayTab(jTabbedPane1,"Listings");  
     }
     private void jMenuItemTime(java.awt.event.ActionEvent evt) {
        String parm = evt.paramString();
        int activeBPs = 1;
        int inx =parm.indexOf("d=");
        int inx1 = parm.indexOf(",", inx);
        String tmp = parm.substring(inx+2,inx1);
        if (!tmp.startsWith("1 ")) {    
            activeBPs = Integer.parseInt(tmp);
        }    
        activeBPs = activeBPs * 1000;
        QWUtil.resetInput();
        (new QWWWorkFlows(this,"List All occurence of BPs running longer than " + activeBPs + " ms")).listWFDs(activeBPs, 8, null);
         displayTab(jTabbedPane1,"Threads");  

    }
     private void jMenuItemStartTime(java.awt.event.ActionEvent evt) {
        String parm = evt.paramString();
        int activeBPs = 1;
        int inx =parm.indexOf("d=");
        int inx1 = parm.indexOf(",", inx);
        String tmp = parm.substring(inx+2,inx1);
        if (!tmp.startsWith("1 ")) {    
            activeBPs = Integer.parseInt(tmp);
        }    
        activeBPs = activeBPs * 1000;
        QWUtil.resetInput();
        (new QWWWorkFlows(this,"List First occurence of BPs running longer than " + activeBPs/1000 + " seconds")).listWFDs(activeBPs, 9, null);
        displayTab(jTabbedPane1,"Threads");  
    }      
     
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem3ActionPerformed
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem5ActionPerformed
    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
       jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem6ActionPerformed
    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
       jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
       jMenuItemActive(evt);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        jMenuItemMem(evt);
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
       jMenuItemTime(evt);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        jMenuItemTime(evt);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        jMenuItemTime(evt);
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        jMenuItemTime(evt);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        jMenuItemTime(evt);
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        jMenuItemTime(evt);
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        jMenuItemTime(evt);
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        jMenuItemStartTime(evt);
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        jMenuItemStartTime(evt);
    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        jMenuItemStartTime(evt);
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed
        jMenuItemStartTime(evt);
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    private void jMenuItem36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem36ActionPerformed
        jMenuItemStartTime(evt);
    }//GEN-LAST:event_jMenuItem36ActionPerformed

    private void jMenuItem37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem37ActionPerformed
        jMenuItemStartTime(evt);
    }//GEN-LAST:event_jMenuItem37ActionPerformed

    private void jMenuItem38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem38ActionPerformed
        jMenuItemStartTime(evt);
    }//GEN-LAST:event_jMenuItem38ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed

    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
       try { 
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JFileChooser fc = new JFileChooser(currentFolder);
        fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        closeHTMLfile();
        fc.setMultiSelectionEnabled(true);
        int ret = fc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File [] fs = fc.getSelectedFiles();
            File f = fc.getSelectedFile();
            inFile = f.getAbsolutePath();
            currentFolder = f.getParent();
            QWAGlobal.qwFileLength = f.length();
            QWAGlobal.qwFuzzyRecs = QWAGlobal.qwFileLength / QWAGlobal.qwFuzzyRecLng;
            QWAGlobal.outFolder = currentFolder + "\\result";
            jTextField2.setText(QWAGlobal.outFolder);
            new File(QWAGlobal.outFolder).mkdir();
            jTextField1.setText(inFile);
            qwAnalyze.setQWFileName(inFile);
            qwAnalyze.setQWFile(qwAnalyze.openInFile(inFile, 0));
            qwAnalyze.collectStats();
            qwAnalyze.displayCounters(jTextArea4,jTextArea6);
            init_jXTable1();
            closeFunction();
            initFunction();
            openHTMLFile();
            QWAGlobal.htmlFile.println(new QWUqueue().createHDR());
            QWUtil.createMemChart((TimeSeriesCollection) qwAnalyze.createMemoryDataset("Memory"),
                "Heap usage - " + qwAnalyze.nodeName, "Heap Comitted(GB)",
                QWAGlobal.start,
                qwAnalyze.last,
                jTabbedPane3,
                "Heap Usage");
            jTabbedPane3.setVisible(true);
            closeFunction();
            initFunction();
            QWUtil.createThreadChart((TimeSeriesCollection) qwAnalyze.createActiveThreadDataset(1, " Total Active Queue Threads"),
                "Total Active Bp's - " + qwAnalyze.nodeName,
                "Threads In Use",
                QWAGlobal.start,
                qwAnalyze.last,
                jTabbedPane3,
                "All Queues");

            jTabbedPane3.setVisible(true);
            closeFunction();
            initFunction();
            QWUtil.createThreadChart((TimeSeriesCollection) qwAnalyze.createTestThreadDataset(1), "Executing Bps - " + qwAnalyze.nodeName,
                "Threads In Use",
                QWAGlobal.start,
                qwAnalyze.last,
                jTabbedPane3,
                "Each Queue");

            closeFunction();
            initFunction();
            QWUtil.createThreadChart((TimeSeriesCollection) qwAnalyze.createTestThreadDataset(5), "Queue Depths - " + qwAnalyze.nodeName,
                "Queue size",
                QWAGlobal.start,
                qwAnalyze.last,
                jTabbedPane3,
                "Wait For Thread");
            StyledDocument doc = jTextPane1.getStyledDocument();
            try {
                QWAText.addStylesToDocument(doc);
                for (int i=0; i < QWAText.heapStyles.length; i++) {
                    doc.insertString(doc.getLength(), QWAText.heapText[i], doc.getStyle(QWAText.heapStyles[i]));
                }
                doc = jTextPane2.getStyledDocument();
                QWAText.addStylesToDocument(doc);
                for (int i = 0; i < QWAText.allQStyles.length; i++) {
                    doc.insertString(doc.getLength(), QWAText.allQText[i], doc.getStyle(QWAText.allQStyles[i]));

                }

            } catch (BadLocationException ex) {
                Logger.getLogger(QWAnaGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            closeFunction();
            initFunction();
            setBPs(qwAnalyze.countBPs(true));
            closeFunction();
            initFunction();
            QWThreadPool threadPool = new QWThreadPool(jPanel20);
            threadPool.createJtable();
            closeFunction();
            saveProps();
        }
       } finally {
            this.setCursor(Cursor.getDefaultCursor());
       }        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
        QWAGlobal.outFolder = jTextField2.getText();
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser("c:");
        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        int ret = fc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            QWAGlobal.outFolder = f.getAbsolutePath();
            jTextField2.setText(QWAGlobal.outFolder);
            //          QWFile = qwAnalyze.openInFile(inFile,0);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
    /*    int index = evt.getLastIndex();
        if (evt.getValueIsAdjusting())
            return;
        System.out.println("Index " + index);
        */
    }//GEN-LAST:event_jList1ValueChanged

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
    }//GEN-LAST:event_jList1MouseClicked

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
    qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("WaitingThreads");
    QWTCharts QWtchart = new QWTCharts(this, "Total Waiting BPs", false);
    QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.createActiveThreadDataset(5, " Total Waiting BPs"), "Total Waiting Bp's - " + qwAnalyze.nodeName, "Waiting BPs", QWAGlobal.start, QWAGlobal.last);
    //QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.createActiveThreadDataset(5, " TestBP processing time"), "TestBP processing time - " + qwAnalyze.nodeName, "Time in MS", QWAGlobal.start, QWAGlobal.last);
    Rectangle r = QWtchart.getBounds();
    Random rnd = new Random();
    r.setLocation(rnd.nextInt(150), rnd.nextInt(70));
    QWtchart.setBounds(r);
    QWtchart.setVisible(true);
    //  createQChart((TimeSeriesCollection)qwAnalyze.createActiveThreadDataset(1," Total Active Queue Threads"),"Total Active Bp's","Threads In Use");
    qwAnalyze.closeQWFile();        // TODO add your handling code here:
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

    private void jComboBox16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox16ActionPerformed
        // TODO add your handling code here:
    int inx= 1;
    String comboStr = jComboBox16.getSelectedItem().toString();
    inx = getIndex(comboStr);
    QWUtil.resetInput();
    //qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("WaitingThreads For Q" + inx);
    QWTCharts QWtchart = new QWTCharts(this, "Q" + inx + " Waiting BPs", false);
    QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.createActiveThreadDataset(5, " Waiting BPs For Q" + inx,inx), "WaitingBPsOnQ" + inx + "-" + qwAnalyze.nodeName, "Waiting BPs", QWAGlobal.start, QWAGlobal.last);
    Rectangle r = QWtchart.getBounds();
    Random rnd = new Random();
    r.setLocation(rnd.nextInt(150), rnd.nextInt(70));
    QWtchart.setBounds(r);
    QWtchart.setVisible(true);
    //  createQChart((TimeSeriesCollection)qwAnalyze.createActiveThreadDataset(1," Total Active Queue Threads"),"Total Active Bp's","Threads In Use");
    //qwAnalyze.closeQWFile();        // TODO add your handling code here:
    //QWUtil.resetInput();
    }//GEN-LAST:event_jComboBox16ActionPerformed

    private void jComboBox17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox17ActionPerformed
    int inx= 1;
    String comboStr = jComboBox17.getSelectedItem().toString();
    inx = getIndex(comboStr);
    QWUtil.resetInput();
    //qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
    setChartFile("Active Threads On Q" + inx);
    QWTCharts QWtchart = new QWTCharts(this, "Q" + inx + " Active BPs", false);
    QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.createActiveThreadDataset(1, " Active Threads On Q" + inx,inx), "ActiveOnQ" + inx + "-" + qwAnalyze.nodeName, "Active BPs", QWAGlobal.start, QWAGlobal.last);
    Rectangle r = QWtchart.getBounds();
    Random rnd = new Random();
    r.setLocation(rnd.nextInt(150), rnd.nextInt(70));
    QWtchart.setBounds(r);
    QWtchart.setVisible(true);
    //  createQChart((TimeSeriesCollection)qwAnalyze.createActiveThreadDataset(1," Total Active Queue Threads"),"Total Active Bp's","Threads In Use");
    //qwAnalyze.closeQWFile();        // TODO add your handling code here:
    //QWUtil.resetInput();
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox17ActionPerformed

    private void jXHyperlink2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink2ActionPerformed
        new QWCreateHTML().printHTMLReport(QWAGlobal.htmlFile);
        qwAnalyze.reportHTMLQueueExist(-1);
    }//GEN-LAST:event_jXHyperlink2ActionPerformed

   // WFCs waiting for threads on any of the 9 execution queues
    private void jXButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXButton2ActionPerformed
       int th = 400;
       QWUqueue qus = new QWUqueue();
       StringBuilder sb = new StringBuilder();
       sb.append("<h3>QUEUEDEPTH: Report QueueDepth when more than <font color='red'><b>" + th + "</b></font> BPs on depth queue</h3>");
       sb.append(QWUhtml.createQueueHdr("Queue Depth","'special3'","bt3"));
       QWAGlobal.htmlFile.println(sb.toString());
       ArrayList al = qus.buildAEReprt(5,th,QWAGlobal.queList);  
/*
       if (al != null) {
          Iterator it = al.iterator();
              while (it.hasNext()){
                    QWAUtil.printIt((String)it.next()) ;
                     //     found=true;
              }
           }    
       }
       */
       QWAGlobal.htmlFile.println("</table>"); 
       //qus.destroyNodeList();

    }//GEN-LAST:event_jXButton2ActionPerformed

    private void jXHyperlink3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink3ActionPerformed
       QWUqueue qus = new QWUqueue();
       StringBuilder sb = new StringBuilder();
       sb.append("<h3>QUEUES: Frequence of QueueDepth Queue / Node</h3>");
       sb.append("<h4>The counters indicates how many times the sampling found BPs waiting for threads</h4>");
       //sb.append(QWUhtml.comment + QWUhtml.recommendation);
       sb.append(QWUhtml.createBigQHdr());
       QWAGlobal.htmlFile.println(sb.toString());
       qus.buildQueFrequency(QWAGlobal.queList);
       QWAGlobal.htmlFile.println("</table>");
    }//GEN-LAST:event_jXHyperlink3ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
         String act = evt.getActionCommand();
         if (jCheckBox1.isSelected())
             listWfc_html=true;
         else listWfc_html=false;
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jXHyperlink9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jXHyperlink9ActionPerformed
   //close html file from Close button
    private void jXButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXButton1ActionPerformed
        closeHTMLfile();
    }//GEN-LAST:event_jXButton1ActionPerformed

    private void jXButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXButton3ActionPerformed
       if (QWAGlobal.htmlFile != null){
            QWAGlobal.htmlFile.flush();
       }     
    }//GEN-LAST:event_jXButton3ActionPerformed

    private void jXButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jXButton4ActionPerformed
    // HTML BP Summary 
    private void jXHyperlink19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jXHyperlink19ActionPerformed
// List CSV EFC entries 
    private void jXHyperlink20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink20ActionPerformed
        isCSV=true;
        closeCSVfile();
        openCSVFile();
    }//GEN-LAST:event_jXHyperlink20ActionPerformed
// Close csv
    private void jXHyperlink21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink21ActionPerformed
        closeCSVfile();
    }//GEN-LAST:event_jXHyperlink21ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed
    // 
    // Check for right mouse click from BP_Summary
    //
    private void jTextArea1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseClicked
        boolean e;
        e = SwingUtilities.isLeftMouseButton(evt);
        e = SwingUtilities.isRightMouseButton(evt);
        e = SwingUtilities.isMiddleMouseButton(evt);


    }//GEN-LAST:event_jTextArea1MouseClicked

     // Selected active Q graph
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int inx= 1;
        String comboStr = jComboBox21.getSelectedItem().toString();
        if (comboStr.equalsIgnoreCase("none"))
        return;
        inx = getIndex(comboStr);
        qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
        setChartFile("ActiveThreads");
        QWTCharts QWtchart = new QWTCharts(this, "Queues with Active BPs", false);
        QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.checkAQueueDataset(inx-1), "Active Bps - " + qwAnalyze.nodeName, "Threads In Use at " + inx, QWAGlobal.start, QWAGlobal.last);
        Rectangle r = QWtchart.getBounds();
        Random rnd = new Random();
        r.setLocation(rnd.nextInt(150), rnd.nextInt(70));
        QWtchart.setBounds(r);
        QWtchart.setVisible(true);
        qwAnalyze.closeQWFile();

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox19ActionPerformed
        int inx= 1;
        String comboStr = jComboBox19.getSelectedItem().toString();
        if (comboStr.equalsIgnoreCase("none"))
        return;
        inx = getIndex(comboStr);
        //     listAQueue(inx);
        StringBuilder sb = new StringBuilder();
        sb.append("List All BPs on Queue ");
        sb.append(inx).append(" With Waiting WFCs in DEPTH ");
        QWUtil.resetInput();
        //(new QWWfc(this,"WFD Listing",true,sb.toString())).listWFDs(inx, 0);
        (new QWWWorkFlows(this,sb.toString())).listWFDs(inx, 15);
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox19ActionPerformed

// List Q 
    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        int inx= 1;
        String comboStr = jComboBox3.getSelectedItem().toString();
        if (comboStr.equalsIgnoreCase("none"))
        return;
        inx = getIndex(comboStr);
        //     listAQueue(inx);
        StringBuilder sb = new StringBuilder();
        sb.append("List All BPs on Queue ");
        sb.append(inx);
        QWUtil.resetInput();
        //(new QWWfc(this,"WFD Listing",true,sb.toString())).listWFDs(inx, 0);
        (new QWWWorkFlows(this,sb.toString())).listWFDs(inx, 0);
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jComboBox20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox20ActionPerformed
        // TODO add your handling code here:
        int inx= 1;
        String comboStr = jComboBox20.getSelectedItem().toString();
        if (comboStr.equalsIgnoreCase("none"))
        return;
        inx = getIndex(comboStr); 
        qwAnalyze.setQWFile(qwAnalyze.openInFile(jTextField1.getText(), 0));
        setChartFile("ActiveThreads");
        QWTCharts QWtchart = new QWTCharts(this, "Active Threads on Q" + inx, false);
        QWtchart.createThreadChart((TimeSeriesCollection) qwAnalyze.checkAQueueDataset(inx), "Active Bps - " + qwAnalyze.nodeName, "Threads In Use at Q4", QWAGlobal.start, QWAGlobal.last);
        Rectangle r = QWtchart.getBounds();
        Random rnd = new Random();
        r.setLocation(rnd.nextInt(150), rnd.nextInt(70));
        QWtchart.setBounds(r);
        QWtchart.setVisible(true);
        qwAnalyze.closeQWFile();
        
    }//GEN-LAST:event_jComboBox20ActionPerformed

    private void jComboBox21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox21ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void outputSelection() {
        jTableBP.getSelectionModel().getLeadSelectionIndex();
        jTableBP.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        int[] r = jTableBP.getSelectedRows();
        int[] c = jTableBP.getSelectedColumns();
        Object name = jTableBP.getValueAt(r[0], 0);
        try{
            Object queue = jTableBP.getValueAt(r[0], 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }    
        Object count = jTableBP.getValueAt(r[0], 2);
        int cnt = Integer.parseInt(count.toString());
        initFunction();
        (new QWWfc(this,"WFD Listing",true,"List occurence of BP " + name)).listWFDs(0, 7,(String) name);
        closeFunction();
    }
 

    private class RowListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            int r =event.getFirstIndex();
            outputSelection();
            jTableBP.clearSelection();
        }
    }

    private class ColumnListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
           //otputSelection();
        }
    }

    public class TableListener1 implements TableModelListener {

        public TableListener1() {
            jTableBP.getModel().addTableModelListener(this);
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
            //return 2;
            return 6;
        }

        public Object getValueAt(int rad, int kolonne) {

            return cells[rad][kolonne];
        }
        //eventuelt også

        @Override
        public String getColumnName(int kolonne) {
            switch (kolonne) {
                case 0:
                    return "WFD";
                case 1:
                    return "Queue";
                case 2:
                    return "MinAct";
                case 3:
                    return "MaxAct";
                case 4:
                    return "AvgAct(ms)";
                case 5:   
                    return "Count";
            }
            return null;
        }

        @Override
        public Class getColumnClass(int kolonne) {
            if (kolonne == 0 || kolonne == 1) {
                return String.class;
            } else {
                return Integer.class;
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
 class BPcompare implements Comparable<BPcompare> {
   public String name;
   public int count;
   BPcompare() {
   }
   BPcompare(String n, int a) {
      name = n;
      count = a;
   }
   public String getName() {
      return name;
   }
   public int getCount() {
      return count;
   }
   // Overriding the compare method to sort the age 
   public int compareTo(BPcompare d) {
      //return this.count.compareTo(d.count);
      return 0;
   }

 }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //new java.awt.Font("Trebuchet MS", 0, 12)
            java.util.Enumeration keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, new FontUIResource("Trebuchet MS", 0, 12));
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        //GEMSLogger.log(ex.getMessage());
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QWAnaGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox13;
    private javax.swing.JComboBox jComboBox14;
    private javax.swing.JComboBox jComboBox15;
    private javax.swing.JComboBox jComboBox16;
    private javax.swing.JComboBox jComboBox17;
    private javax.swing.JComboBox jComboBox18;
    private javax.swing.JComboBox jComboBox19;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox20;
    private javax.swing.JComboBox jComboBox21;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private org.jdesktop.swingx.JXTextField jHtmlFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    public static javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    public static javax.swing.JLabel jLabel35;
    public static javax.swing.JLabel jLabel36;
    public static javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    public static javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    public static javax.swing.JLabel jLabel46;
    public static javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    public static javax.swing.JPanel jPanelFairShare1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JScrollPane jScrollPane10;
    public static javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    public static javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    public static javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextField jTextField1;
    public javax.swing.JTextField jTextField10;
    public javax.swing.JTextField jTextField11;
    public javax.swing.JTextField jTextField12;
    public javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    public static javax.swing.JTextField jTextField4;
    public static javax.swing.JTextField jTextField5;
    public javax.swing.JTextField jTextField6;
    public javax.swing.JTextField jTextField7;
    public javax.swing.JTextField jTextField8;
    public javax.swing.JTextField jTextField9;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private org.jdesktop.swingx.JXButton jXButton1;
    private org.jdesktop.swingx.JXButton jXButton2;
    private org.jdesktop.swingx.JXButton jXButton3;
    private org.jdesktop.swingx.JXButton jXButton4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink10;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink11;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink12;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink13;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink14;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink15;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink16;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink17;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink18;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink19;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink20;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink21;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink6;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink7;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink8;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink9;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane1;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane2;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane3;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane4;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane5;
    private org.jdesktop.swingx.JXTaskPane jXTaskPane6;
    private org.jdesktop.swingx.JXTaskPaneContainer jXTaskPaneContainer1;
    // End of variables declaration//GEN-END:variables
}
