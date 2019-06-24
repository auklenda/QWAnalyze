/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.emea.support.l2.qwa;

/**
 *
 * @author AlfAuklend
 */
public class QWBPNameInfo {
   public String name;
   public int count;
   public long act;
   public int port;
   QWBPNameInfo() {
   }
   QWBPNameInfo(String n, int a) {
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
   public int compareTo(QWBPNameInfo d) {
      //return this.count.compareTo(d.count);
      return 0;
   }    
}
