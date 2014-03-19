package com.stanford.httpserver.response;

import java.net.Socket;

public class ResponseOK extends BaseResponse {

	public ResponseOK() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Sends OK response
	 * @param clientSocket
	 * @param bContent
	 * @param contentType
	 */
	public static void sendOkResponse(Socket clientSocket, byte[] bContent, String contentType)
	{
		sendResponse(clientSocket, bContent, "200 OK", contentType);
	}

}
