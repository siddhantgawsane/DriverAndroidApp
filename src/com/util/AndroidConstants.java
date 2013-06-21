package com.util;

public class AndroidConstants {
	public static String MAIN_SERVER_IP="192.168.43.232"; 
	public static String MAIN_SERVER_PORT = "9988";
	
	public static String url() { return "http://" + AndroidConstants.MAIN_SERVER_IP + ":"
			+ AndroidConstants.MAIN_SERVER_PORT+"/";}
}