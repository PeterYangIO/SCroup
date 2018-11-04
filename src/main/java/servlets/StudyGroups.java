package servlets;

import com.google.gson.Gson;
import models.StudyGroup;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/study-groups")
public class StudyGroups extends HttpServlet {
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        if (method.equals("GET")) {
            this.doGet(request, response);
        }
        // TODO Check Authentication Header for permissions
        // (any for POST, requesting user equals studyGroup's owner for PUT and DELETE
        else if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
            StudyGroup studyGroup = new Gson().fromJson(request.getReader(), models.StudyGroup.class);
            boolean success = true;
            switch (method) {
                case "POST":
                    success = studyGroup.dbInsert();
                    break;
                case "PUT":
                    success = studyGroup.dbUpdate();
                    break;
                case "DELETE":
                    success = studyGroup.dbDelete();
                    break;
            }
            if (!success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> filterParams = request.getParameterMap()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, entry -> entry.getValue()[0]
                )
            );
        if (filterParams.get("courseId") == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // TODO authentication and userId
        int userId = 1;
        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(studyGroups));
    }
}
