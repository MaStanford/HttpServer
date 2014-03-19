package com.stanford.httpserver.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import com.stanford.httpserver.request.RequestModel.HTTPMethod;

public class RequestURL {

	private HTTPMethod method;
	private String uri;
	private String url;
	private String extension;
	private String params;
	private boolean isDir;

	public RequestURL(Socket clientSocket) throws Exception{
		processURL(clientSocket);
	}

	/**
	 * Processes the request URL and populates the object
	 * @param clientSocket
	 * @throws Exception
	 */
	private void processURL(Socket clientSocket) throws Exception {
		//Create reader
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		// Parse method of the request
		String firstLine = in.readLine();
		String httpMethod = firstLine.substring(0, firstLine.indexOf((" ")));
		System.out.println(firstLine);
		System.out.println("Method: " + httpMethod);
		
		if(httpMethod.equals("GET")){
			setMethod(HTTPMethod.GET);
		}else if(httpMethod.equals("PUT")){
			setMethod(HTTPMethod.PUT);
		}else if(httpMethod.equals("DELETE")){
			setMethod(HTTPMethod.DELETE);
		}else if(httpMethod.equals("POST")){
			setMethod(HTTPMethod.POST);
		}else{
			setMethod(HTTPMethod.UPDATE);
		}

		//Parse the url
		int start = firstLine.indexOf(httpMethod) + httpMethod.length() + 1;
		int length = firstLine.lastIndexOf("HTTP") - 1;
		String requestedUrl = firstLine.substring(start, length);
		System.out.println("Requested URL: " + requestedUrl);
		
		setUri(requestedUrl);

		//Get params
		if(requestedUrl.contains("?")){
			setParams(requestedUrl.substring(requestedUrl.indexOf('?'),requestedUrl.length())); 
			requestedUrl = requestedUrl.substring(0,requestedUrl.indexOf('?'));
		}else{
			setParams("");
		}

		String requestedFile; 
		//Sanitize the URL and parse out the extension
		requestedFile = requestedUrl.replace("..", "");
		requestedFile = requestedUrl.replace("/..", "");
		requestedFile = requestedUrl.replace("../", "");
		System.out.println("Requested File: " + requestedFile);
		
		setUrl(requestedFile);

		length = requestedFile.length();
		//Make sure its not a directory
		if (requestedFile.contains(".")){
			String extension = requestedFile.substring(requestedFile.lastIndexOf('.') + 1, length);
			System.out.println("Extension: " + extension);
			setExtension(extension);
			setDir(false);
		}else{
			setExtension("");
			setDir(true);
		}
	}

	public HTTPMethod getMethod() {
		return method;
	}
	public void setMethod(HTTPMethod method) {
		this.method = method;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public boolean isDir() {
		return isDir;
	}
	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}
}
