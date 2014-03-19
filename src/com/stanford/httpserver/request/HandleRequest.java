package com.stanford.httpserver.request;

import java.io.IOException;

import com.stanford.httpserver.HttpServer;
import com.stanford.httpserver.response.ResponseNotFound;
import com.stanford.httpserver.response.ResponseNotImplemented;
import com.stanford.httpserver.response.ResponseOK;
import com.stanford.httpserver.util.Util;

/**
 * 
 * Contains methods and structures related to handling requests
 * 
 * @author Mark Stanford
 *
 */
public class HandleRequest {
	/**
	 * Handles the request
	 * @param RequestModel
	 * @throws IOException
	 */
	public static void handleTheRequest(RequestModel request) throws IOException{

		switch(request.getMethod()){
		case GET:
			handleGet(request);
			break;
		default:
			ResponseNotImplemented.notImplemented(request.getClientSocket());
			break;
		}
	}

	/**
	 * Handles GET requests
	 * @param request
	 */
	private static void handleGet(RequestModel request){
		//Make sure it's a file and not a dir/app
		if (!request.isDir()){
			//Check if we support the extension, otherwise 500 it
			if (HttpServer.contentType.containsKey(request.getExtension())){
				//Send file name to our util to see if file exists
				if (Util.getFile(HttpServer.settings.get(HttpServer.CONTENT_PATH) + request.getUrl()) != null){ 
					try {
						System.out.println("Sending OK with requested file");
						//Parse file into bytes with file util and then send it on to the response.
						ResponseOK.sendOkResponse(request.getClientSocket(),
								Util.getByteFromFile(Util.getFile(HttpServer.settings.get(HttpServer.CONTENT_PATH) + request.getUrl())),
								HttpServer.contentType.get(request.getExtension()));
					} catch (Exception e) {
						ResponseNotFound.notFound(request.getClientSocket());
						e.printStackTrace();
						return;
					}
				}else{
					//Can't find the damn thing
					System.out.println("Requested File doesn't exist: " + request.getUrl());
					ResponseNotFound.notFound(request.getClientSocket());
				}
			}else{
				//Don't support the POS
				System.out.println("Requested Extension not supported: " + request.getUrl());
				ResponseNotFound.notFound(request.getClientSocket());
			}
		//Requested URL doesn't have an extension, so either get root index or launch app
		}else{
			//Checking to see if looking at root
			if(request.getUrl().equals("/")){
				System.out.println("Requesting root: " + request.getUrl());
				if (Util.getFile(HttpServer.settings.get(HttpServer.CONTENT_PATH) + request.getUrl() + HttpServer.settings.get(HttpServer.DEFAULT_PAGE)) != null){
					try {
						System.out.println("Sending OK with default");
						ResponseOK.sendOkResponse(request.getClientSocket(),
								Util.getByteFromFile(Util.getFile(HttpServer.settings.get(HttpServer.CONTENT_PATH) + request.getUrl() + "/"  + HttpServer.settings.get(HttpServer.DEFAULT_PAGE))),
								HttpServer.contentType.get("HTML"));
					} catch (Exception e) {
						ResponseNotFound.notFound(request.getClientSocket());
						e.printStackTrace();
						return;
					}
				}else{
					System.out.println("Default not found: " + HttpServer.settings.get(HttpServer.CONTENT_PATH) + request.getUrl() + "/index.html");
					ResponseNotFound.notFound(request.getClientSocket());
				}
			}else{ //Not looking at root, its pointed at dir or app
				System.out.println("Requesting dir or app: " + request.getUrl());
				ResponseNotFound.notFound(request.getClientSocket());
			}
		}
	}
}

