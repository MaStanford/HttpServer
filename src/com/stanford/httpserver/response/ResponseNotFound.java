package com.stanford.httpserver.response;

import java.net.Socket;

import com.stanford.httpserver.HttpServer;

public class ResponseNotFound extends BaseResponse {
	
	private final static String code = "404 Not Found";
	private final static String responseType = "html";

	public ResponseNotFound() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sends not found response
	 * @param clientSocket
	 */
	public static void notFound(Socket clientSocket)
	{
		//Eventually grab a file for here TODO: 
		String content = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html\"; charset=\"utf-8\"></head><body><h2>Stanford Simple Web Server</h2><div>404 - Not Found</div></body></html>";
		sendResponse(clientSocket, content, code, HttpServer.contentType.get(responseType));
	}
}
