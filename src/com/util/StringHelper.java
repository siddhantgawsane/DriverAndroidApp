/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.util;

/**
 *
 * @author user
 */
public class StringHelper {

public static String n2s(Object d){
	String dual="";
	if(d==null){
		dual =  "";
	}
	else
		dual=d.toString().trim();
	
	return dual;
}
public static String nullObjectToStringEmpty(Object d){
	String dual="";
	if(d==null){
		dual =  "";
	}
	else
		dual=d.toString().trim();
	
	return dual;
}
public static float nullObjectToFloatEmpty(Object d){
	float i=0;
	if(d!=null){
		String dual=d.toString().trim();
		try{
			i=new Float(dual).floatValue();
		}catch (Exception e) {
			System.out.println("Unable to find integer value");	
		}
	}
	return i;
}	
public static int nullObjectToIntegerEmpty(Object d){
	int i=0;
	if(d!=null){
		String dual=d.toString().trim();
		try{
			i=new Integer(dual).intValue();
		}catch (Exception e) {
			System.out.println("Unable to find integer value");	
		}
	}
	return i;
}


public static void main(String args[]) {
	// new StringHelper().split("sadas:asdasd:asdas");

}



}
