package com.jepp.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

import org.json.JSONObject;

import com.jepp.objects.Person;

@ApplicationScoped
public class ChatSessionHandler {
	private final Set<Session> sessions = new HashSet<>();
	private final Set<Person> people = new HashSet<>();

	public void addSession(Session session) {
		this.sessions.add(session);
	}

	public void removeSession(Session session) {
		this.sessions.remove(session);
		this.removePerson(session.getId());
	}

	public List<Person> getPeoples() {
		return new ArrayList<>(this.people);
	}

	public void addPerson(Session session, Person person) {
		this.people.add(person);
		this.sendToSession(session, this.identifyMessage(person));

		JSONObject payload = this.addPersonMessage(person);
		this.sendToAllConnectedSessions(payload);
	}

	public void removePerson(String id) {
		Person person = this.getPersonById(id);
		if (person != null) {
			this.people.remove(person);
			this.sendToAllConnectedSessions(this.deletePersonMessage(person));
		}
	}
	
	public void addMessage(String personId, String message) {
		this.sendToAllConnectedSessions(this.newChatMessage(personId, message));
	}

	public Person getPersonById(String id) {
		for (Person person : this.people) {
			if (person.getId().equals(id)) {
				return person;
			}
		}
		return null;
	}

	public JSONObject identifyMessage(Person person) {
		JSONObject object = new JSONObject();
		object.put("action", "identify");
		object.put("id", person.getId());
		object.put("people", this.people);
		return object;
	}
	
	public JSONObject addPersonMessage(Person person) {
		JSONObject object = new JSONObject();
		object.put("action", "addPerson");
		object.put("nickName", person.getNickname());
		object.put("id", person.getId());
		return object;
	}
	
	public JSONObject deletePersonMessage(Person person) {
		JSONObject object = new JSONObject();
		object.put("action", "deletePerson");
		object.put("id", person.getId());
		return object;
	}
	
	public JSONObject newChatMessage(String personId, String message) {
		JSONObject object = new JSONObject();
		object.put("action", "newChat");
		object.put("id", personId);
		object.put("message", message);
		return object;
	}

	public void sendToAllConnectedSessions(JSONObject message) {
		for (Session session : this.sessions) {
			sendToSession(session, message);
		}
	}

	public void sendToSession(Session session, JSONObject message) {
		try {
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException ex) {
			this.sessions.remove(session);
		}
	}
}
