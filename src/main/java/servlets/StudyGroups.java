package servlets;

import com.google.gson.Gson;
import models.StudyGroup;
import models.User;

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
        else if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
            // Check for authentication
            User user = User.lookUpByAuthToken(request.getHeader("authorization"));
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            StudyGroup studyGroup = new Gson().fromJson(request.getReader(), models.StudyGroup.class);
            // Set owner id from authenticated user object
            studyGroup.setOwnerId(user.getID());

            // Validate that editing or deleting a study group is done by the owner
            // Note we make a DB call instead of checking studyGroup.getOwnerId() in case
            // a malicious user modifies the payload so that the ownerId always equals the uid
            if (method.equals("PUT") || method.equals("DELETE")
                && StudyGroup.dbSelectOwnerId(studyGroup.getId()) != user.getID()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Execute the database function
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

        User user = User.lookUpByAuthToken(request.getHeader("authorization"));
        int userId = user == null ? -1 : user.getID();
        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(studyGroups));
    }
}
