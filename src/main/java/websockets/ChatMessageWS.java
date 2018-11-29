package websockets;

import com.google.gson.Gson;
import models.Message;
import models.User;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


@ServerEndpoint(value="/chat-message/{groupId}")
public class ChatMessageWS {
    private static final Gson gson = new Gson();

    private static HashMap<Integer, Vector<Session>> sessionVectors = new HashMap<>();

    /**
     * Opening a connection will subscribe a user to sessions grouped by the course id.
     * This is analogous to having multiple "chat rooms" where each chat room has
     * a set of users sending messages (join / leave requests) for a group in the course
     */
    @OnOpen
    public void open(Session session, @PathParam("groupId") Integer groupId) {
        if (ChatMessageWS.sessionVectors.get(groupId) == null) {
            Vector<Session> sessionVector = new Vector<>();
            sessionVector.add(session);
            ChatMessageWS.sessionVectors.put(groupId, sessionVector);
        }
        else {
            ChatMessageWS.sessionVectors.get(groupId).add(session);
        }
    }

    /**
     * Frontend first needs to send an authentication token before the backend will
     * accept any requests.
     *
     * Frontend will then send messages to join or leave a specific group within a course id.
     * This request is processed into the database and the backend (this function) sends
     * back a message of success / fail to let the user know to make a request to the API
     * to get new data.
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("groupId") Integer groupId) {
        try {
            if (session.getUserProperties().get("user") == null) {
                if (!WebSocketUtil.authenticate(message, session)) {
                    close(session, groupId);
                }
            }
            else {
                User u = (User)session.getUserProperties().get("user");
                Message m = new Message(u.getID(), u.getFullName(), groupId, message);
                m.insertToDatabase();
              	processMessage(m, groupId);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            close(session, groupId);
        }
    }

    /**
     * Removes session from static hash map and if no one is connected
     * to the group, the vector for that group is removed as well
     */
    @OnClose
    public void close(Session session, @PathParam("groupId") Integer groupId) {
        Vector<Session> sessionVector = ChatMessageWS.sessionVectors.get(groupId);
        sessionVector.remove(session);
        if (sessionVector.size() == 0) {
            ChatMessageWS.sessionVectors.remove(groupId);
        }
    }

    @OnError
    public void error(Throwable error) {
        System.out.println(error.toString());
    }
  
  	private void processMessage(Message message, int groupId) {
      // 1. Put message into database
      // 2. Broadcast message to everyone connected
      Vector<Session> sessionVector = ChatMessageWS.sessionVectors.get(groupId);
        if (sessionVector == null) {
            // No one connected so don't broadcast (otherwise null ptr exception)
            return;
        }
        for (Session s: sessionVector) {
            try {
				s.getBasicRemote().sendText(gson.toJson(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}