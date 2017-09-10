package com.jepp.server;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/echo")
public class ChatServerEndpoint {

	@OnOpen
	public void onOpen(Session session) {
		System.out.println(session.getId() + " has opened a connection");
		try {
			session.getBasicRemote().sendText("Connection Established");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("Message from " + session.getId() + ": " + message);
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("Session " + session.getId() + " has ended");
	}
	
	@OnError
	public void onError(Throwable error) {
		System.out.println("Error occurs: " + error.getMessage());
	}
}
