/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.*;
import java.io.*;

/**
 *
 * @author AAuklend
 */
public class SearchJars {
    static boolean useSubFolders = false;
    static boolean debug = false;
    static boolean filenameOnly = false;
    static String filters = null;
    static String svcName=null;
    static int vectorSize=100;
    static String directory = null;
    static String cN = null;
    /** Creates a new instance of SearchJars */
    public SearchJars() {
        
    }
 
public static void setSubFolder(boolean s) {
    useSubFolders = s;
}
public static boolean getSubFolder() {
    return useSubFolders;
}
public static void setFileNameOnly(boolean b) {
    filenameOnly = b;
}
public static void setFilter(String filt) {
    if (filt != null && (!filt.equals(""))) 
        filters = filt;
}
public static String getFilter() {
    return filters;
}
public static void setDirectory(String s){
    if (s != null && s != "") 
        directory = s;
}
public static String getDirectory(){
    return directory;
}

public static void setClassName(String s){
    if (s != null && s != "")  
        cN = s;
}
public static String getClassName() {
    return cN;
}    
    

public String findClassInJarFile( String jarfilename , String className,boolean longSearch)  throws Exception{
    try {
        java.util.jar.JarFile jarfile = new java.util.jar.JarFile( jarfilename );
        ZipEntry entry;
        Enumeration entries = jarfile.entries();
        while ( entries.hasMoreElements() ) {
            entry = (ZipEntry)entries.nextElement();
            if ( !entry.isDirectory() ){
                String name = entry.getName();
                long fileSize = entry.getSize();
                if ( name.length() > 6 ) {
                    String ext = name.substring ( name.length() - 6 );
                    if ( ".class".equalsIgnoreCase ( ext ) ) {
                        if (longSearch) {
                            if (name.indexOf(className) > 0) 
                                return name;    
                        }
                        if (name.startsWith(className)) 
                            return name;
                    }
                }
            }
        }  
    } catch (Exception exp) {
        exp.printStackTrace();
    }
    return null;
}  
    public void singleJarFile( String jarfilename )  throws Exception {
       try {
        java.util.jar.JarFile jarfile = new java.util.jar.JarFile( jarfilename );
        ZipEntry entry;
         Enumeration entries = jarfile.entries();
         while ( entries.hasMoreElements() ) {
            entry = (ZipEntry)entries.nextElement();
            if ( !entry.isDirectory() ) {
                String name = entry.getName();
                long fileSize = entry.getSize();
                 if ( name.length() > 6 ) {
                    String ext = name.substring ( name.length() - 6 );
                     if ( ".class".equalsIgnoreCase ( ext ) ) {
                        // Found a class !
                        System.out.println ( "      - CLASS : " + name );
                        // Optional ...  decompress to a byte array
                        if ( ( fileSize > 0 ) && ( fileSize <= Integer.MAX_VALUE ) ) {
                            byte[] classBuffer = new byte[(int)fileSize];
                            InputStream zipDataStream = jarfile.getInputStream( entry );
                            long bytesRead = 0;
                             while ( bytesRead < fileSize ){
                                bytesRead += zipDataStream.read ( classBuffer, (int)bytesRead, (int)(fileSize-bytesRead) );
                            }
                            if ( bytesRead == fileSize ){                                
                                ByteArrayInputStream classDataStream = new ByteArrayInputStream ( classBuffer );
                                // do stuff with the byte array of the class file contents
                                classDataStream.close();
                            }
                            else {
                                System.out.println ( "Error : " + name + "[ only read " + bytesRead + " of " + fileSize + " bytes ]" );
                            }
                        }
                        else {
                            System.out.println ( "Error : " + name + "[ cannot determine uncompressed size ]" );
                        }
                   }    // = ".class"
                }   // name > 6
            }   // !directory
        } // while
    }
    catch ( Throwable ex )
    {
        System.out.println ("Exception : " + ex.getMessage() );
        ex.printStackTrace();
    }
        
    }
    public File[] scanFolder(String folder) {
        File wkdir = new File(folder);
        if ( !wkdir.exists() ) {
            return null;
        }
       File[] files = null;
        if ( !useSubFolders && filters.indexOf("*") == -1 ) {
            files = new File[1];
            files[0] = new File(folder, filters);
        }
        else {
            WildCardFilter filter = new WildCardFilter(filters, false, debug, svcName);
            if ( useSubFolders ) {
                Vector fileVect = new Vector(vectorSize); // used to combine all File objects
                traverseDir(fileVect, filter, wkdir);
                int vectSize = fileVect.size();
                if ( vectSize > 0 ) {
                    files = new File[vectSize];
                    for ( int i = 0; i < vectSize; i++ ) {
                        files[i] = (File)fileVect.elementAt(i);
                    }
                }
            }
            else { // short and sweet when useSubFolders=false
                files = wkdir.listFiles(filter);
            }
        }
        return files;
    }
    public Vector scanFolderV(String folder) {
        File wkdir = new File(folder);
        if ( !wkdir.exists() ) {
            return null;
        }
        WildCardFilter filter = new WildCardFilter(filters, false, debug, svcName);
        Vector fileVect = new Vector(vectorSize); // used to combine all File objects
        File[] files = null;
        if ( useSubFolders ) {
            traverseDir(fileVect, filter, wkdir);
            int vectSize = fileVect.size();
        } else { // short and sweet when useSubFolders=false
             files = wkdir.listFiles(filter);
             for ( int i = 0; i < files.length; i++ ) {
                 if (filter.accept(files[i]) ) {
                    if (filenameOnly)
                        fileVect.add(files[i].getName());
                    else fileVect.add(files[i]);
                }
             }
        }
        return fileVect;
    }
    
    public static Vector findDirectories(String startFolder) {
        Object [] o = null;
        
        File wkdir = new File(startFolder);
        if ( !wkdir.exists() ) {
            return null;
        }
        Vector fileVect = new Vector(vectorSize); // used to combine all File objects
        File[] files = null;
        files = wkdir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
             if (files[i].isDirectory()) {
                    fileVect.add(files[i]);
             }
        }
        return fileVect;        
    }

   public void traverseDir(Vector fileVect, WildCardFilter filter, File wkdir) {
        File[] fileList = wkdir.listFiles(); // returns everything
        if ( fileList != null ) {
            for ( int i = 0; i < fileList.length; i++ ) {
                if ( !fileList[i].isDirectory() && filter.accept(fileList[i]) ) {
                    fileVect.add(fileList[i]);
                }
                else {
                    traverseDir(fileVect, filter, fileList[i]);
                }
            }
        }
    }
    public void getArgs(String [] args) {
        int sz = args.length;
        String tmp = null;
        for (int i = 0; i < args.length; i++) {
            tmp = args[i+1];
            if (tmp.startsWith("-d") )
                setDirectory(tmp.substring(2) );
            else if (tmp.startsWith("-c"))
                    setClassName(tmp.substring(2) );
            else if (tmp.startsWith("-s"))
                    setSubFolder(true);
            else if (tmp.startsWith("-z"))
                    setFilter(tmp.substring(2) );
        }
    }
    public static void main(String[] args) {
      String name = null;
      SearchJars sjf = new SearchJars();
      try {
          sjf.setFilter("*.jar");
          sjf.setSubFolder(true);
          sjf.setClassName("WorkFlowContext");
          sjf.setDirectory("c:\\Sc41-1\\si\\jar\\");
//          String cN = "com/sterlingcommerce/woodstock/workflow/WorkFlowContext";
          Vector files = sjf.scanFolderV(sjf.getDirectory());
          for (int i=0;i < files.size();i++) {
              String o = files.get(i).toString();
 //             System.out.println(o);
 //             sjf.singleJarFile(o);
              name = sjf.findClassInJarFile(o,sjf.getClassName(),true);
              if (name != null) 
                 System.out.println("CLASS-" + name + "  Found in  " + o);
          }
//          sjf.singleJarFile("c:\\Sc41-1\\si\\jar\\woodstock.jar");
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }    

}
