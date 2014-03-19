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
	
	public static final int port = 8080;	
	private static HttpServer instance;
	public static String contentPath; 
	private List<RequestModel> requestQueue;
	public final static String SERVER_NAME = "Stanford Simple Web Server";
	
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
