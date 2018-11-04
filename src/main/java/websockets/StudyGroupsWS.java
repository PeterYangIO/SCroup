package websockets;

import com.google.gson.Gson;
import models.JoinedGroupRequest;
import models.WebSocketResponse;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

@ServerEndpoint(value="/study-groups/{groupId}")
public class StudyGroupsWS {
    private static HashMap<Integer, Vector<Session>> sessionVectors = new HashMap<>();

    @OnOpen
    public void open(Session session, @PathParam("groupId") Integer groupId) {
        // TODO validate authentication
        System.out.println("Connection made!");
        if (sessionVectors.get(groupId) == null) {
            Vector<Session> sessionVector = new Vector<>();
            sessionVector.add(session);
            StudyGroupsWS.sessionVectors.put(groupId, sessionVector);
        }
        else {
            StudyGroupsWS.sessionVectors.get(groupId).add(session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("groupId") Integer groupId) {
        System.out.println("Received message: " + message);
        Gson gson = new Gson();
        try {
            // 1. Read message and parse from json to JoinedGroupRequest object
            JoinedGroupRequest joinedGroupRequest = gson.fromJson(
                message, models.JoinedGroupRequest.class
            );

            // 2. Depending on specified method, join or leave the group
            // TODO handle fail of db, let user know something went wrong
            // TODO validate that user in the data is the authorized user
            switch (joinedGroupRequest.getMethod()) {
                case "join":
                    joinedGroupRequest.getData().dbInsert();
                    break;
                case "leave":
                    joinedGroupRequest.getData().dbDelete();
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
            Vector<Session> sessionVector = StudyGroupsWS.sessionVectors.get(groupId);
            for (Session s: sessionVector) {
                WebSocketResponse response = new WebSocketResponse(
                    true, "Client should refresh content"
                );
                s.getBasicRemote().sendText(gson.toJson(response));
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            close(session, groupId);
        }
    }

    @OnClose
    public void close(Session session, @PathParam("groupId") Integer groupId) {
        System.out.println("Disconnecting!");
        Vector<Session> sessionVector = StudyGroupsWS.sessionVectors.get(groupId);
        if (sessionVector.size() == 0) {
            StudyGroupsWS.sessionVectors.remove(groupId);
        }
        sessionVector.remove(session);
    }

    @OnError
    public void error(Throwable error) {
        System.out.println(error.toString());
    }
}
