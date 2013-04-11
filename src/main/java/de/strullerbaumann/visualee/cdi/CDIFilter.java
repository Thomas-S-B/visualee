/*
 * Created on 11.04.2013 - 11:20:35 
 * 
 * Copyright(c) 2013 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
package de.strullerbaumann.visualee.cdi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class CDIFilter {

   List<CDIType> filterCDITypes = new ArrayList<>();

   public CDIFilter addCDIType(CDIType cdiType) {
      filterCDITypes.add(cdiType);
      return this;
   }

   public List<CDIType> getFilterCDITypes() {
      return filterCDITypes;
   }

   public boolean contains(CDIType cdiType) {
      return filterCDITypes.contains(cdiType);
   }

   public CDIFilter clearFilter() {
      filterCDITypes.clear();
      return this;
   }
}
