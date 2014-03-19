package com.stanford.httpserver.response;

import java.net.Socket;

import com.stanford.httpserver.HttpServer;

public class ResponseNotImplemented extends BaseResponse {

	private final static String code = "500 Not Implemented";
	private final static String responseType = "html";
	
	public ResponseNotImplemented() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Sends not implemented response
	 * @param clientSocket
	 */
	public static void notImplemented(Socket clientSocket)
	{
		//Eventually grab this file from somewhere TODO;
		String content = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html\"; charset=\"utf-8\"></head><body><h2>Stanford Simple Web Server</h2><div>500 - Not Implemented</div></body></html>";
		sendResponse(clientSocket, content, code , HttpServer.contentType.get(responseType));
	}

}
