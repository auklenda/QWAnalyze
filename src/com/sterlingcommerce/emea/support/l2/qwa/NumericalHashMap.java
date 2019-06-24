/* 
  * (C) Copyright 2001 - 2008 Sterling Commerce, Inc. ALL RIGHTS RESERVED 
  * 
  * ** Trade Secret Notice ** 
  * 
  * This software, and the information and know-how it contains, is  
  * proprietary and confidential and constitutes valuable trade secrets 
  * of Sterling Commerce, Inc., its affiliated companies or its or 
  * their licensors, and may not be used for any unauthorized purpose 
  * or disclosed to others without the prior written permission of the 
  * applicable Sterling Commerce entity. This software and the 
  * information and know-how it contains have been provided 
  * pursuant to a license agreement which contains prohibitions 
  * against and/or restrictions on its copying, modification and use. 
  * Duplication, in whole or in part, if and when permitted, shall 
  * bear this notice and the Sterling Commerce, Inc. copyright 
  * legend. As and when provided to any governmental entity,  
  * government contractor or subcontractor subject to the FARs,  
  * this software is provided with RESTRICTED RIGHTS under  
  * Title 48 CFR 52.227-19. 
  * Further, as and when provided to any governmental entity,  
  * government contractor or subcontractor subject to DFARs, 
  * this software is provided pursuant to the customary  
  * Sterling Commerce license, as described in Title 48 
  * CFR 227-7202 with respect to commercial software and commercial 
  * software documentation. 
  */ 
 package com.sterlingcommerce.emea.support.l2.qwa;
/*
 * NumericalHashMap.java
 *
 * Created on August 24, 2004, 3:48 PM
 */


/**
 *
 * @author  mrusoff-tw
 */
public class NumericalHashMap extends java.util.HashMap {
    
    /** Creates a new instance of NumericalHashMap */
    public NumericalHashMap() {
    }
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//    }
    
    public Object put(Object key, Object value) {
        Object retValue;
        if (value instanceof String) {
            value=new Long((String)value);
        }
        retValue = super.put(key, value);
        return retValue;
    }
    
    public Object get(Object key) {
        Object retValue;
        
        retValue = super.get(key);
        return retValue;
    }
    
    int getInt(Object Key)  {
        Object retValue;
        
        retValue = super.get(Key);
        int retInt = ((Long) retValue).intValue();
        return retInt;
    }
    
    long getLong(Object Key)  {
        Object retValue;
        
        retValue = super.get(Key);
        int retInt = ((Long) retValue).intValue();
        return retInt;
    }
    
    Object put(Object Key, int Number) {
       Object retValue;
        Object value= new Long((long) Number);
        retValue = super.put(Key, value);
        return retValue;
    }
    
    Object put(Object Key, long Number)  {
        Object retValue;
        Object value= new Long((long) Number);
        retValue = super.put(Key, value);
        return retValue;
    }
    
}