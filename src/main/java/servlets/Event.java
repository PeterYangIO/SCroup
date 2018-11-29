package servlets;

import com.google.gson.Gson;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@MultipartConfig
@WebServlet("/api/event")
public class Event extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        models.Event event = new Gson().fromJson(request.getReader(), models.Event.class);
        String groupId = request.getParameter("id");
        if (!event.insertEvent(groupId)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String groupId = request.getParameter("id");
        ArrayList<models.Event> events = models.Event.getEvent(groupId);
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(events));
    }
}
