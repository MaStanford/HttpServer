package com.stanford.httpserver.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestModel {
	
	private RequestURL requestURL;
	
	private Socket clientSocket;
	
	public static enum HTTPMethod{GET,PUT,POST,DELETE,UPDATE};

	public RequestModel(Socket clientSocket) {
		this.clientSocket = clientSocket;
		//Process the URL
		try {
			requestURL = new RequestURL(clientSocket);
		} catch (Exception e) {
			requestURL = null;
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream(){
		if(getClientSocket() == null)
			return null;
		try {
			return getClientSocket().getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public OutputStream getOutputStream(){
		if(getClientSocket() == null)
			return null;
		try {
			return getClientSocket().getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public HTTPMethod getMethod() {
		return requestURL.getMethod();
	}

	public void setMethod(HTTPMethod method) {
		this.requestURL.setMethod(method);
	}

	public String getUri() {
		return requestURL.getUri();
	}

	public void setUri(String uri) {
		this.requestURL.setUri(uri);
	}

	public String getParams() {
		return requestURL.getParams();
	}

	public void setParams(String params) {
		this.requestURL.setParams(params);
	}

	public boolean isDir() {
		return requestURL.isDir();
	}

	public void setDir(boolean isDir) {
		this.requestURL.setDir(isDir);
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public String getUrl() {
		return requestURL.getUrl();
	}

	public void setUrl(String url) {
		this.requestURL.setUrl(url);
	}

	public String getExtension() {
		return requestURL.getExtension();
	}

	public void setExtension(String extension) {
		this.requestURL.setExtension(extension);
	}

}
