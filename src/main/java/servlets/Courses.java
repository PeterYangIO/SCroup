package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import models.Course;
import models.StudyGroup;

@WebServlet("/api/courses")
public class Courses extends HttpServlet {
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        if (method.equals("GET")) {
            this.doGet(request, response);
        }
        // TODO Check Authentication Header for permissions
        else if (method.equals("POST") || method.equals("PUT")) {
            Course Course = new Gson().fromJson(request.getReader(), models.Course.class);
            boolean success = true;
            switch (method) {
                case "POST":
                    success = Course.dbInsert();
                    break;
                case "PUT":
                    success = Course.dbUpdate();
                    break;
            }
            if (!success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
       String query = request.getParameter("query");
       
       try {
    	    String[] Params = query.split("\\s+");
    	    if(Params[0] == "") {
    	    	Params = new String[0];
    	    }
            ArrayList<Course> Courses = Course.dbSelect(Params);
            Gson gson = new Gson();
            response.setContentType("application/json");
            response.getWriter().print(gson.toJson(Courses));
    	} catch (PatternSyntaxException ex) {
    	    // 
    	}


    }
}
