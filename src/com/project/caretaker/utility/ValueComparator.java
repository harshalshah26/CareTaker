package com.project.caretaker.utility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<Integer> {
	 Map<Integer,Integer> map;
	 ArrayList<String> list;
	 public ValueComparator(Map<Integer, Integer> base) {
	        this.map = base;
	    }
	
	 @Override
     public int compare(Integer lhs, Integer rhs) {
		 Integer val1 = map.get(lhs);
		 Integer val2 = map.get(rhs);            
		 if(val1>=val2)
		 {
			 return -1;
		 }
		 else
		 {
			 return 1;
		 }
	 }

}
