package websockets;

import com.google.gson.Gson;
import models.JoinedGroup;
import models.JoinedGroupRequest;
import models.WebSocketResponse;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

@ServerEndpoint(value="/study-groups/{courseId}")
public class StudyGroupsWS {
    private static HashMap<Integer, Vector<Session>> sessionVectors = new HashMap<>();

    /**
     * Opening a connection will subscribe a user to sessions grouped by the course id.
     * This is analogous to having multiple "chat rooms" where each chat room has
     * a set of users sending messages (join / leave requests) for a group in the course
     */
    @OnOpen
    public void open(Session session, @PathParam("courseId") Integer courseId) {
        System.out.println("Connection made! " + courseId.toString());

        if (sessionVectors.get(courseId) == null) {
            Vector<Session> sessionVector = new Vector<>();
            sessionVector.add(session);
            sessionVectors.put(courseId, sessionVector);
        }
        else {
            sessionVectors.get(courseId).add(session);
        }
    }

    /**
     * Frontend first needs to send an authentication token before the backend will
     * accept any requests. (TODO)
     *
     * Frontend will then send messages to join or leave a specific group within a course id.
     * This request is processed into the database and the backend (this function) sends
     * back a message of success / fail to let the user know to make a request to the API
     * to get new data.
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("courseId") Integer courseId) {
        System.out.println("Received message: " + message);
        if (session.getUserProperties().get("user") == null) {
            authenticateConnection(message, session, courseId);
        }
        else {
            processJoinedGroupRequest(message, session, courseId);
        }
    }

    /**
     * Removes session from static hash map and if no one is connected
     * to the group, the vector for that group is removed as well
     */
    @OnClose
    public void close(Session session, @PathParam("courseId") Integer courseId) {
        System.out.println("Disconnecting! " + courseId.toString());
        Vector<Session> sessionVector = sessionVectors.get(courseId);
        sessionVector.remove(session);
        if (sessionVector.size() == 0) {
            sessionVectors.remove(courseId);
        }
    }

    @OnError
    public void error(Throwable error) {
        System.out.println(error.toString());
    }

    /**
     * TODO
     */
    private void authenticateConnection(String message, Session session, int courseId) {
        session.getUserProperties().put("user", "user");
    }

    /**
     * Processes request to join or leave a group.
     * Input is a message string formatted as a JoinedGroupRequest
     */
    private void processJoinedGroupRequest(String message, Session session, int courseId) {
        Gson gson = new Gson();
        try {
            // 1. Read message and parse from json to JoinedGroupRequest object
            JoinedGroupRequest joinedGroupRequest = gson.fromJson(
                message, models.JoinedGroupRequest.class
            );

            // 2. Depending on specified method, join or leave the group
            // TODO handle fail of db, let user know something went wrong
            // TODO validate that user in the data is the authorized user
            JoinedGroup joinedGroup = joinedGroupRequest.getData();
            joinedGroup.setUserId(1);  // TODO get user id from session
            switch (joinedGroupRequest.getMethod()) {
                case "join":
                    joinedGroup.dbInsert();
                    break;
                case "leave":
                    joinedGroup.dbDelete();
                    break;
                default:
                    WebSocketResponse response = new WebSocketResponse(
                        false, "Invalid method"
                    );
                    session.getBasicRemote().sendText(gson.toJson(response));
                    break;
            }

            // 3. Tell all listeners that something has changed
            // (ask them to make a GET request to /api/study-groups)
            Vector<Session> sessionVector = sessionVectors.get(courseId);
            for (Session s: sessionVector) {
                WebSocketResponse response = new WebSocketResponse(
                    true, "Client should refresh content"
                );
                s.getBasicRemote().sendText(gson.toJson(response));
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            close(session, courseId);
        }
    }
}
