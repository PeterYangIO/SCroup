package websockets;

import com.google.gson.Gson;
import models.Message;
import models.User;
import models.WebSocketResponse;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


@ServerEndpoint(value="/study-groups/{groupId}")
public class ChatMessageWS {
    private static final String AUTHENTICATION = "AUTHENTICATION";
    private static final Gson gson = new Gson();

    private static HashMap<Integer, Vector<Session>> sessionVectors = new HashMap<>();

    /**
     * Opening a connection will subscribe a user to sessions grouped by the course id.
     * This is analogous to having multiple "chat rooms" where each chat room has
     * a set of users sending messages (join / leave requests) for a group in the course
     */
    @OnOpen
    public void open(Session session, @PathParam("groupId") Integer groupId) {
        if (sessionVectors.get(groupId) == null) {
            Vector<Session> sessionVector = new Vector<>();
            sessionVector.add(session);
            sessionVectors.put(groupId, sessionVector);
        }
        else {
            sessionVectors.get(groupId).add(session);
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
                if (authenticateConnection(message, session)) {
                    WebSocketResponse response = new WebSocketResponse(
                        true, AUTHENTICATION
                    );
                    session.getBasicRemote().sendText(gson.toJson(response));
                }
                else {
                    WebSocketResponse response = new WebSocketResponse(
                        false, AUTHENTICATION
                    );
                    session.getBasicRemote().sendText(gson.toJson(response));
                    close(session, groupId);
                }
            }
            else {
                Message m = new Message(((User)session.getUserProperties().get("user")).getID(), groupId, message);
              	processMessage(m, session, groupId);
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
        Vector<Session> sessionVector = sessionVectors.get(groupId);
        sessionVector.remove(session);
        if (sessionVector.size() == 0) {
            sessionVectors.remove(groupId);
        }
    }

    @OnError
    public void error(Throwable error) {
        System.out.println(error.toString());
    }

    /**
     * When the connection is opened, the frontend should send the auth token
     *
     * @param authToken token to authenticate user with
     * @param session session user will belong to
     * @return true if valid user
     */
    private boolean authenticateConnection(String authToken, Session session) {
        User user = User.lookUpByAuthToken(authToken);
        session.getUserProperties().put("user", user);
        return user != null;
    }
  
  	private void processMessage(Message message, Session session ,int groupId) {
      // 1. Put message into database
      // 2. Broadcast message to everyone connected
      Vector<Session> sessionVector = sessionVectors.get(groupId);
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