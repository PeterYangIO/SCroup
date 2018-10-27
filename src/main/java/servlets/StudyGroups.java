package servlets;

import com.google.gson.Gson;
import models.StudyGroup;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class StudyGroups extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameterNames = request.getParameterMap();

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(parameterNames);
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
