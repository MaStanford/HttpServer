package com.stanford.httpserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stanford.httpserver.request.RequestModel;

/**
 * This will be a singleton that holds the requests. 
 * I may need to put requests into a queue in case we have reached the maximun number of threads open. 
 * The main loop will keep adding to the queue unless we reach a max size, then we will respond with 500 or some error until we catch up.
 * The main loop will add a queue and then pop off the queue and respond to the request.
 * 
 * @author Mark Stanford
 *
 */
public class HttpServer {
	
	private static HttpServer instance;
	private List<RequestModel> requestQueue;
	
	//Content Types
	public static Map<String,String> contentType = new HashMap<String,String>();
	static{
		contentType.put("htm", "text/html");
		contentType.put("html", "text/html");
		contentType.put("xml", "text/xml");
		contentType.put("txt", "text/plain");
		contentType.put("css", "text/css");
		contentType.put("png", "image/png");
		contentType.put("gif", "image/gif");
		contentType.put("jpg", "image/jpg");
		contentType.put("jpeg", "image/jpeg");
		contentType.put("zip", "application/zip");
		contentType.put("mov", "application/octet-stream");
	}
	
	//Keys for the settings
	public static final String PORT = "PORT";
	public static final String CONTENT_PATH = "CONTENT_PATH";
	public static final String SERVER_NAME = "SERVER_NAME";
	public static final String SERVER_IP = "SERVER_IP";
	public static final String BACK_LOG = "BACK_LOG";
	public static final String TIMEOUT = "TIMEOUT";
	public static final String MAX_THREADS = "MAX_THREADS";
	public static final String DEFAULT_PAGE = "DEFAULT_PAGE";
	
	//Holds Settings
	public static Map<String,String> settings = new HashMap<String,String>();
	static{
		settings.put(PORT, "8080");
		settings.put(CONTENT_PATH, "/tmp/");
		settings.put(SERVER_NAME, "Stanford Simple Web Server");
		settings.put(SERVER_IP, "192.168.1.2");
		settings.put(BACK_LOG, "0");
		settings.put(TIMEOUT, "150");
		settings.put(MAX_THREADS, "4");
		settings.put(DEFAULT_PAGE, "index.html");
	}
	

	private HttpServer() {
		requestQueue =  new ArrayList<RequestModel>();
	}
	
	public static HttpServer getInstance(){
		if(instance != null)
			return instance;
		instance = new HttpServer();
			return instance;
	}
	
	synchronized public void addRequest(RequestModel request){
		requestQueue.add(request);
	}

	synchronized public RequestModel popRequest(){
		if(requestQueue.size() < 1)
			return null;
		return requestQueue.remove(requestQueue.size()-1);
	}
	
	synchronized public boolean hasNextRequest(){
		return !requestQueue.isEmpty();
	}
}
