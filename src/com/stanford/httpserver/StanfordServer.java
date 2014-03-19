package com.stanford.httpserver;

import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.stanford.httpserver.request.RequestModel;
import com.stanford.httpserver.request.RequestWorker;

public class StanfordServer {

	public static boolean running = false;
	public static int timeout = 8000; // Time limit for data transfers.
	public static ServerSocket serverSocket; // Our server socket
	public static int maxThreads = 8;
	public static int currentThreads = 0;

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
			HttpServer.contentPath = contentpath;
		}
		catch (Exception e){
			return false; 
		}

		//Listen to sockets
		Thread serverThread = new Thread(){
			public void run(){
				while(running){
					try {
						Socket clientSocket = serverSocket.accept();
						clientSocket.setSoTimeout(timeout);

						final RequestModel request = new RequestModel(clientSocket);
						HttpServer.getInstance().addRequest(request);

						//Check the threads
						if(currentThreads > maxThreads)
							continue;
						
						//Process request
						RequestWorker.process();
						
					} catch (Exception e) {
						System.out.println("Goodbye!");
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


	public static void main(String[] args) throws Exception {

		//Start Server command
		startServer((Inet4Address) Inet4Address.getByName("192.168.1.2"),8080,5,"/tmp/", 0);

		while(running){
			System.out.println("Type Stop to stop server");
			Scanner in = new Scanner(System.in);
			String input = in.next();
			if(input.equals("stop")){
				//Stop command

				stop();

			}
		}
	}
}
