package com.stanford.httpserver.request;

import com.stanford.httpserver.StanfordServer;
import com.stanford.httpserver.HttpServer;

public class RequestWorker {
	
	RequestModel request;

	private RequestWorker() {
	}

	public static void process(){
		//Thread for handling requests
		Thread requestHandler = new Thread(){
			public void run(){
				StanfordServer.currentThreads++;
				System.out.println("Adding new Thread to handle request: " + StanfordServer.currentThreads);
				try {
					System.out.println("Handling Request on Thread: " + StanfordServer.currentThreads);
					//While I'm down here, I might as well process all the requests.
					while(HttpServer.getInstance().hasNextRequest())
						HandleRequest.handleTheRequest(HttpServer.getInstance().popRequest());
					StanfordServer.currentThreads--;
					System.out.println("Completed Removing Thread: " + (StanfordServer.currentThreads + 1));
				} catch (Exception e) {
					StanfordServer.currentThreads--;
					System.out.println("Error Removing Thread: " + (StanfordServer.currentThreads + 1));
				}
			}
		};					
		requestHandler.start();
	}
}
