package websockets;

import com.google.gson.Gson;
import models.User;
import models.WebSocketResponse;

import javax.websocket.Session;
import java.io.IOException;

class WebSocketUtil {
    private static final String AUTHENTICATION = "AUTHENTICATION";
    private static final Gson gson = new Gson();


    static boolean authenticate(String message, Session session) throws IOException {
        if (authenticateConnection(message, session)) {
            WebSocketResponse response = new WebSocketResponse(
                    true, AUTHENTICATION
            );
            session.getBasicRemote().sendText(gson.toJson(response));
            return true;
        }
        else {
            WebSocketResponse response = new WebSocketResponse(
                    false, AUTHENTICATION
            );
            session.getBasicRemote().sendText(gson.toJson(response));
            return false;
        }
    }

    /**
     * When the connection is opened, the frontend should send the auth token
     *
     * @param authToken token to authenticate user with
     * @param session   session user will belong to
     * @return true if valid user
     */
    private static boolean authenticateConnection(String authToken, Session session) {
        User user = User.lookUpByAuthToken(authToken);
        session.getUserProperties().put("user", user);
        return user != null;
    }
}
