package models;

public class WebSocketResponse {
    private boolean success;
    private String message;

    public WebSocketResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
