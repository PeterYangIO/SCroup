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

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams);
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(studyGroups));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) {

    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {

    }
}
