package com.stanford.httpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HttpServer {

	static final int port = 8080;	
	final static String EoL = "\r\n";
	//Content Types
	static Map<String,String> contentType = new HashMap<String,String>();
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
	}
	static boolean running = false;
	private static int timeout = 8000; // Time limit for data transfers.
	private static ServerSocket serverSocket; // Our server socket
	private static String contentPath = "/tmp"; // Root path of our contents
	private static BufferedReader in;
	private static BufferedWriter out;

	/**
	 * Starts the HTTP Server
	 * @param ipAddress
	 * @param port
	 * @param maxConnections
	 * @param contentPath
	 * @return if successful
	 */
	public static boolean startServer(Inet4Address ipAddress, int port, int maxConnections, String contentpath,int Backlog){

		if (running) return false;

		try{
			// A tcp/ip socket (ipv4)
			serverSocket = new ServerSocket(port, Backlog, ipAddress);
			running = true;
			contentPath = contentpath;
		}
		catch (Exception e){
			return false; 
		}

		Thread serverThread = new Thread(){
			public void run(){
				while(running){
					try {
						final Socket clientSocket = serverSocket.accept();
						clientSocket.setSoTimeout(timeout);

						Thread requestHandler = new Thread(){
							public void run(){
								try {
									handleTheRequest(clientSocket);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						};
						requestHandler.start();


					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		serverThread.start();
		return true;
	}

	/**
	 * Stops the server
	 */
	public static void stop(){
		if (running)
		{
			running = false;
			try { 
				serverSocket.close();
			}
			catch (Exception e){
			}
			serverSocket = null;
		}
	}


	private static void handleTheRequest(Socket clientSocket) throws IOException{

		//Create readers and writers.  In is the request, out is the response
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));


		/**
		 * Parsing can all be seperate functions
		 */
		// Parse method of the request
		String firstLine = in.readLine();
		String httpMethod = firstLine.substring(0, firstLine.indexOf((" ")));

		System.out.println(firstLine);
		System.out.println("Method: " + httpMethod);

		//Parse the url
		int start = firstLine.indexOf(httpMethod) + httpMethod.length() + 1;
		int length = firstLine.lastIndexOf("HTTP") - 1;
		String requestedUrl = firstLine.substring(start, length);

		System.out.println("Requested URL: " + requestedUrl);

		String urlParams;

		//Check method, only supporting 2 for now
		if (!httpMethod.equals("GET") && !httpMethod.equals("POST")){
			notImplemented(clientSocket);
			return;
		}

		String requestedFile; 

		//Sanitize the URL and parse out the extension
		requestedFile = requestedUrl.replace("..", "");
		requestedFile = requestedUrl.replace("/..", "");
		requestedFile = requestedUrl.replace("../", "");

		System.out.println("Requested File: " + requestedFile);

		length = requestedFile.length();
		//Make sure its not a directory
		if (requestedFile.contains(".")){
			String extension = requestedFile.substring(requestedFile.lastIndexOf('.') + 1, length);
			System.out.println("Extension: " + extension);
			if (contentType.containsKey(extension)){ // Do we support this extension?
				if (getFile(contentPath + requestedFile) != null){ //If yes check existence of the file
					// Everything is OK, send requested file with correct content type:
					try {
						System.out.println("Sending OK with requested file");
						sendOkResponse(clientSocket,getByteFromFile(getFile(contentPath + requestedFile)),contentType.get(extension));
					} catch (Exception e) {
						notFound(clientSocket);
						e.printStackTrace();
						return;
					}
				}else{
					System.out.println("Requested File doesn't exist: " + requestedFile);
					notFound(clientSocket); // We don't support this extension.
				}   // We are assuming that it doesn't exist.

			}
			//Requested URL doesn't have an extension, so do something
		}else{
			//Checking to see if looking at root
			if(requestedFile.equals("/")){
				System.out.println("Requesting root: " + requestedFile);
				if (getFile(contentPath + requestedFile + "/index.html") != null){
					try {
						System.out.println("Sending OK with default");
						sendOkResponse(clientSocket,getByteFromFile(getFile(contentPath + requestedFile + "/index.html")),contentType.get("HTML"));
					} catch (Exception e) {
						notFound(clientSocket);
						e.printStackTrace();
						return;
					}
				}else{
					System.out.println("Default not found: " + contentPath + requestedFile + "/index.html");
					notFound(clientSocket);
				}
			}else{ //Not looking at root, its pointed at dir or app
				System.out.println("Requesting dir or app: " + requestedFile);
				notFound(clientSocket);
			}
		}
		out.close();
		in.close();
		clientSocket.close();
	}

	/**
	 * Sends the specified response
	 * @param clientSocket
	 * @param strContent
	 * @param responseCode
	 * @param contentType
	 */
	private static void sendResponse(Socket clientSocket, String strContent, String responseCode,String contentType)
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
	private static void sendResponse(Socket clientSocket, byte[] bContent, String responseCode, String contentType){
		try{
			byte[] bHeader = new String(
					"HTTP/1.1 " + responseCode + EoL
					+ "Server: Stanford Simple Web Server" + EoL
					+ "Content-Length: " + bContent.length + EoL
					+ "Connection: close\r\n"
					+ "Content-Type: " + contentType + EoL + EoL).getBytes();
			OutputStream out = clientSocket.getOutputStream();
			out.write(bHeader);
			out.write(bContent);
			out.close();
			clientSocket.close();
		}catch(Exception e){ 

		}
	}

	/**
	 * Sends not implemented response
	 * @param clientSocket
	 */
	private static void notImplemented(Socket clientSocket)
	{
		String content = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html\"; charset=\"utf-8\"></head><body><h2>Stanford Simple Web Server</h2><div>500 - Not Implemented</div></body></html>";
		sendResponse(clientSocket, content, "500 Not Implemented", "text/html");
	}

	/**
	 * Sends not found response
	 * @param clientSocket
	 */
	private static void notFound(Socket clientSocket)
	{

		String content = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html\"; charset=\"utf-8\"></head><body><h2>Stanford Simple Web Server</h2><div>404 - Not Found</div></body></html>";
		sendResponse(clientSocket, content, "404 Not Found", "text/html");
	}

	/**
	 * Sends OK response
	 * @param clientSocket
	 * @param bContent
	 * @param contentType
	 */
	private static void sendOkResponse(Socket clientSocket, byte[] bContent, String contentType)
	{
		sendResponse(clientSocket, bContent, "200 OK", contentType);
	}


	public static void main(String[] args) throws Exception {


		startServer((Inet4Address) Inet4Address.getByName("192.168.1.2"),8080,5,contentPath, 0);
		while(running){
			System.out.println("Type Stop to stop server");
			Scanner in = new Scanner(System.in);
			String input = in.next();
			if(input.equals("stop")){
				stop();
			}
		}
	}

	/**
	 * Returns a file or null
	 * @param path
	 * @return
	 */
	private static File getFile(String path){
		File file = new File(path);
		if(file.exists())
			return file;
		return null;
	}

	/**
	 * Converts file to byte array
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static byte[] getByteFromFile(File file) throws Exception{
		byte []buffer = new byte[(int) file.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if ( ios.read(buffer) == -1 ) {
				throw new IOException("EOF reached while trying to read the whole file");
			}        
		} finally { 
			try {
				if ( ios != null ) 
					ios.close();
			} catch ( IOException e) {
			}
		}
		return buffer;
	}
}
