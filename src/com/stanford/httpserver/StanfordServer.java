package com.stanford.httpserver;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.stanford.httpserver.request.RequestModel;
import com.stanford.httpserver.request.RequestWorker;

public class StanfordServer {

	public static boolean running = false;
	public static ServerSocket serverSocket; // Our server socket
	public static int currentThreads = 0;

	/**
	 * Starts the HTTP Server
	 * @param ipAddress
	 * @param port
	 * @param maxConnections
	 * @param contentPath
	 * @return if successful
	 */
	public static boolean startServer(){

		if (running) return false;

		try{
			System.out.println("Port:" + Integer.decode(HttpServer.settings.get(HttpServer.PORT)));
			System.out.println("Backlog:" + Integer.decode(HttpServer.settings.get(HttpServer.BACK_LOG)));
			System.out.println("Backlog:" + (InetAddress) Inet4Address.getByName(HttpServer.settings.get(HttpServer.SERVER_IP)));

			// A tcp/ip socket (ipv4) Port, BackLog, IPAddress
			serverSocket = new ServerSocket(Integer.decode(HttpServer.settings.get(HttpServer.PORT)), 
					Integer.decode(HttpServer.settings.get(HttpServer.BACK_LOG)), 
					(InetAddress) InetAddress.getByName(HttpServer.settings.get(HttpServer.SERVER_IP)));

			running = true;
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
						clientSocket.setSoTimeout(Integer.decode(HttpServer.settings.get(HttpServer.TIMEOUT)));

						final RequestModel request = new RequestModel(clientSocket);
						HttpServer.getInstance().addRequest(request);

						//Check the threads
						if(currentThreads > Integer.decode(HttpServer.settings.get(HttpServer.MAX_THREADS)))
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
		startServer();

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
