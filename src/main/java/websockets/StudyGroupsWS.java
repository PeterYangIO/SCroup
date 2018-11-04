package websockets;

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
        System.out.println(message);
        try {
            Vector<Session> sessionVector = StudyGroupsWS.sessionVectors.get(groupId);
            for (Session s: sessionVector) {
                s.getBasicRemote().sendText(message);
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
        sessionVector.remove(session);
    }

    @OnError
    public void error(Throwable error) {
        System.out.println(error.toString());
    }
}
