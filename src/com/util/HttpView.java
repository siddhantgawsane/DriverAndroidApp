package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.net.ParseException;
import android.util.Log;

public class HttpView {
	public static String result[][] = new String[500][];
	public static int count = -1;
	static String TAG = "HttpView";
	public static int timeout = -1;

	public static String connect2Server(String url) {

		HttpView.count = -1;
		StringBuffer res = new StringBuffer();

		Log.v(TAG, url);
		URL u;
		try {

			for (int i = 0; i < result.length; i++) {
				result[i] = null;
			}
			u = new URL(url);
			URLConnection uc = u.openConnection();
			if (timeout != -1)
				uc.setConnectTimeout(timeout);

			Scanner scanner = new Scanner(uc.getInputStream());

			while (scanner.hasNext()) {
				String row = StringHelper.n2s(scanner.nextLine());
				if (row.length() > 0) {
					res.append(row + "\n");
					String cols[] = row.split(",");
					for (int i = 0; i < cols.length && cols[i] != null; i++) {
						cols[i] = cols[i].trim();
					}
					result[++HttpView.count] = cols;
					Log.v(TAG, HttpView.count + " " + row);
				}
			}
			scanner.close();
			u = null;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.v(TAG, "HttpView.count " + HttpView.count);
		return res.toString();
	}

	public static boolean checkConnectivityServer(String ip, int port) {
		boolean success = false;
		try {
			Socket soc = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(ip, port);
			soc.connect(socketAddress, 3000);
			success = true;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(" Connecting to server " + success);
		return success;

	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("HttpView", ex.toString());
		}
		return null;
	}

	public static Object getDataBluetooth() {
		String url = "http://" + AndroidConstants.MAIN_SERVER_IP + ":"
				+ AndroidConstants.MAIN_SERVER_PORT + "/?method=receive";
		return connect2ServerObject(url);
	}

	

	public static Object resetOBD() {
		String url = "http://" + AndroidConstants.MAIN_SERVER_IP + ":"
				+ AndroidConstants.MAIN_SERVER_PORT + "/?method=reset";
		return connect2ServerObject(url);
	}

	public static Object connect2ServerObject(String url) {
		Log.v(TAG, "Reading Object");
		Log.v(TAG, url);
		Object o = null;
		URL u;
		try {
			u = new URL(url);
			URLConnection uc = u.openConnection();
			if (timeout != -1)
				uc.setConnectTimeout(timeout);
			ObjectInputStream ois = new ObjectInputStream(uc.getInputStream());
			o = ois.readObject();
			System.out.println(o);
			u = null;

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return o;
	}

	public static String connectToServer(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		HttpResponse response;
		String s = "";
		HttpView.count = -1;
		try {
			int i = 0;
			for (i = 0; i < result.length; i++) {
				result[i] = null;
			}

			Log.d(TAG, "UPLOAD: about to execute");
			response = httpclient.execute(httppost);
			Log.d(TAG, "UPLOAD: executed");

			s = HttpView.getResponseBody(response);
			s = s.trim();
			String rows[] = s.split("\n");

			System.out.println("rows.length " + rows.length);
			for (String string : rows) {
				String cols[] = string.split(",");
				result[++count] = cols;

			}

			System.out.println("Data " + s);
			// System.out.println("Data in result "+result[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getResponseBody(HttpResponse response) {
		String response_text = null;
		HttpEntity entity = null;
		try {
			entity = response.getEntity();
			response_text = _getResponseBody(entity);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e1) {
				}
			}
		}
		return response_text;
	}

	public static String _getResponseBody(final HttpEntity entity)
			throws IOException, ParseException {
		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}
		InputStream instream = entity.getContent();
		if (instream == null) {
			return "";
		}

		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(

			"HTTP entity too large to be buffered in memory");
		}
		String charset = getContentCharSet(entity);
		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		Reader reader = new InputStreamReader(instream, charset);
		StringBuilder buffer = new StringBuilder();
		try {

			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}

		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	public static ArrayList _getResponseBodyObject(final HttpEntity entity)
			throws IOException, ParseException {
		ArrayList arr = new ArrayList();
		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}
		InputStream instream = entity.getContent();
		if (instream == null) {
			return null;
		}

		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(

			"HTTP entity too large to be buffered in memory");
		}
		String charset = getContentCharSet(entity);
		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		ObjectInputStream reader = new ObjectInputStream(instream);
		StringBuilder buffer = new StringBuilder();
		try {
			char[] tmp = new char[1024];
			Object o;
			while ((o = reader.readObject()) != null) {
				if (o instanceof ArrayList) {
					arr = (ArrayList) o;
				}
				System.out.println("no of elements: " + arr.size());
			}
			// for (int a = 0; a < elements.length; a++) {
			// System.out.println(elements[a]);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			reader.close();
		}
		return arr;
	}

	public static String getContentCharSet(final HttpEntity entity)
			throws ParseException {

		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		String charset = null;

		if (entity.getContentType() != null) {

			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0) {

				NameValuePair param = values[0].getParameterByName("charset");

				if (param != null) {

					charset = param.getValue();

				}

			}

		}

		return charset;

	}
	public static String createURL(HashMap param) {

		String parameterURLString = "";

		Set set = param.keySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display elements
		while (i.hasNext()) {
			String key = StringHelper.nullObjectToStringEmpty(i.next());
			String value = StringHelper.nullObjectToStringEmpty(param.get(key));
			value = URLEncoder.encode(value);
			parameterURLString += "&" + key + "=" + value;

		}

		if (parameterURLString.length() > 0) {
			parameterURLString = parameterURLString.substring(1);
			parameterURLString = "?" + parameterURLString;
		}
		String url = AndroidConstants.url() + parameterURLString;
		System.out.println("url  " + url);
	
		connect2Server(url);
		return url;
	}
}
