package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@MultipartConfig
@WebServlet("/api/event")
public class Event extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        reader.close();
        System.out.println(buffer.toString());
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> eventDetail = gson.fromJson(buffer.toString(), type);
        models.Event newEvent = new models.Event(eventDetail.get("title"), eventDetail.get("location"), eventDetail.get("date"), eventDetail.get("time"), 1);
        boolean success = newEvent.insertEvent();
        System.out.println(success);

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

    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        if (method.equals("GET")) {
            this.doGet(request, response);
        } else if (method.equals("POST")) {
            this.doPost(request,response);
        }
    }
}
