package websockets;

import com.google.gson.Gson;
import models.*;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


@ServerEndpoint(value="/study-groups/{courseId}")
public class StudyGroupsWS {
    private static final String INVALID = "INVALID";
    private static final String REFRESH = "REFRESH";
    private static final Gson gson = new Gson();

    private static HashMap<Integer, Vector<Session>> sessionVectors = new HashMap<>();

    /**
     * Opening a connection will subscribe a user to sessions grouped by the course id.
     * This is analogous to having multiple "chat rooms" where each chat room has
     * a set of users sending messages (join / leave requests) for a group in the course
     */
    @OnOpen
    public void open(Session session, @PathParam("courseId") Integer courseId) {
        if (StudyGroupsWS.sessionVectors.get(courseId) == null) {
            Vector<Session> sessionVector = new Vector<>();
            sessionVector.add(session);
            StudyGroupsWS.sessionVectors.put(courseId, sessionVector);
        }
        else {
            StudyGroupsWS.sessionVectors.get(courseId).add(session);
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
    public void onMessage(String message, Session session, @PathParam("courseId") Integer courseId) {
        try {
            if (session.getUserProperties().get("user") == null) {
                if (!WebSocketUtil.authenticate(message, session)) {
                    close(session, courseId);
                }
            }
            else {
                processJoinedGroupRequest(message, session, courseId);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            close(session, courseId);
        }
    }

    /**
     * Removes session from static hash map and if no one is connected
     * to the group, the vector for that group is removed as well
     */
    @OnClose
    public void close(Session session, @PathParam("courseId") Integer courseId) {
        Vector<Session> sessionVector = StudyGroupsWS.sessionVectors.get(courseId);
        sessionVector.remove(session);
        if (sessionVector.size() == 0) {
            StudyGroupsWS.sessionVectors.remove(courseId);
        }
    }

    @OnError
    public void error(Throwable error) {
        System.out.println(error.toString());
    }

    public static void broadcastRefresh(int courseId) throws IOException {
        Vector<Session> sessionVector = StudyGroupsWS.sessionVectors.get(courseId);
        if (sessionVector == null) {
            // No one connected so don't broadcast (otherwise null ptr exception)
            return;
        }
        for (Session s: sessionVector) {
            WebSocketResponse response = new WebSocketResponse(
                true, REFRESH
            );
            s.getBasicRemote().sendText(gson.toJson(response));
        }
    }

    /**
     * Processes request to join or leave a group.
     * Input is a message string formatted as a JoinedGroupRequest
     */
    private void processJoinedGroupRequest(String message, Session session, int courseId) {
        try {
            // 1. Get user from session - we know it is not null when entering this function
            User user = (User) session.getUserProperties().get("user");

            // 2. Read message and parse from json to JoinedGroupRequest object
            JoinedGroupRequest joinedGroupRequest = gson.fromJson(
                message, models.JoinedGroupRequest.class
            );

            // 3. Depending on specified method, join or leave the group
            boolean success = false;
            JoinedGroup joinedGroup = joinedGroupRequest.getData();
            joinedGroup.setUserId(user.getID());
            switch (joinedGroupRequest.getMethod()) {
                case "join":
                    success = joinedGroup.dbInsert();
                    break;
                case "leave":
                    success = joinedGroup.dbDelete();
                    break;
                case "update":
                    success = true;
                    break;
                default:  // Invalid user input
                    WebSocketResponse response = new WebSocketResponse(
                        false, INVALID
                    );
                    session.getBasicRemote().sendText(gson.toJson(response));
                    break;
            }
            // Database error
            if (!success) {
                WebSocketResponse response = new WebSocketResponse(
                    false, INVALID
                );
                session.getBasicRemote().sendText(gson.toJson(response));
                return;
            }

            // 4. Tell all listeners that something has changed
            // (ask them to make a GET request to /api/study-groups)
            broadcastRefresh(courseId);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            close(session, courseId);
        }
    }
}
