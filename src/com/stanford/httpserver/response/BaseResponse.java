package com.stanford.httpserver.response;

import java.io.OutputStream;
import java.net.Socket;

import com.stanford.httpserver.HttpServer;

/**
 * Base class for a response.
 * @author admin
 *
 */
public class BaseResponse {
	
	public static final String EoL = "\r\n";
	
	/**
	 * Sends the specified response
	 * @param clientSocket
	 * @param strContent
	 * @param responseCode
	 * @param contentType
	 */
	protected static void sendResponse(Socket clientSocket, String strContent, String responseCode,String contentType)
	{
		byte[] bContent = new String(strContent).getBytes();
		sendResponse(clientSocket, bContent, responseCode, contentType);
	}

	/**
	 * Sends the specified response
	 * @param clientSocket
	 * @param bContent
	 * @param responseCode
	 * @param contentType
	 */
	protected static void sendResponse(Socket clientSocket, byte[] bContent, String responseCode, String contentType){
		try{
			byte[] bHeader = new String(
					"HTTP/1.1 " + responseCode + EoL
					+ "Server: " + HttpServer.SERVER_NAME + EoL
					+ "Content-Length: " + bContent.length + EoL
					+ "Connection: close\r\n"
					+ "Content-Type: " + contentType + EoL + EoL).getBytes();
			OutputStream out = clientSocket.getOutputStream();
			out.write(bHeader);
			out.write(bContent);
			out.close();
			clientSocket.close();
		}catch(Exception e){ 
			System.out.println("Couldn't write to clientSocket");
		}
	}
}
