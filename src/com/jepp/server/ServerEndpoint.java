package com.jepp.server;

import java.io.IOException;

import javax.websocket.OnOpen;
import javax.websocket.Session;

@javax.websocket.server.ServerEndpoint("/foo")
public class ServerEndpoint {

	@OnOpen
	public void onOpen(Session session) {
		System.out.println(session.getId() + " has opened a connection");
		try {
			session.getBasicRemote().sendText("Connection Established");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
