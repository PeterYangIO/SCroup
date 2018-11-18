package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        if (!event.insertEvent()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String test = request.getParameter("id");
        System.out.println(test);
        ArrayList<models.Event> events = models.Event.getEvent(1);
        System.out.println(events.toString());
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(events));
    }

//    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String method = request.getMethod();
//        if (method.equals("GET")) {
//            this.doGet(request, response);
//        } else if (method.equals("POST")) {
//            this.doPost(request,response);
//        }
//    }
}
