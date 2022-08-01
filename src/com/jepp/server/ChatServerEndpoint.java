package com.jepp.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import com.jepp.handlers.ChatSessionHandler;
import com.jepp.objects.Person;

@ApplicationScoped
@ServerEndpoint("/chat")
public class ChatServerEndpoint {
	
	@Inject
	static private ChatSessionHandler sessionHandler = new ChatSessionHandler();

	@OnOpen
	public void onOpen(Session session) {
		sessionHandler.addSession(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		JSONObject jsonMessage = new JSONObject(message);
		if ("signin".equals(jsonMessage.getString("action"))) {
			Person person = new Person();
			person.setId(session.getId());
			person.setNickname(jsonMessage.getString("nickName"));
            sessionHandler.addPerson(session, person);
        }

        if ("signout".equals(jsonMessage.getString("action"))) {
            String id = jsonMessage.getString("id");
            sessionHandler.removePerson(id);
        }
        
        if ("message".equals(jsonMessage.getString("action"))) {
            String content = jsonMessage.getString("message");
            String id = jsonMessage.getString("id");
            sessionHandler.addMessage(id, content);
        }
	}

	@OnClose
	public void onClose(Session session) {
		sessionHandler.removeSession(session);
	}
	
	@OnError
	public void onError(Throwable error) {
		System.out.println("Error occurs: " + error.getMessage());
	}
}
