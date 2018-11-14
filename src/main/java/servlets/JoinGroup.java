package servlets;

import com.google.gson.Gson;
import models.JoinedGroup;
import models.StudyGroup;
import models.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/api/join-group")
public class JoinGroup extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        response.setContentType("application/json");

        String groupIdParam = request.getParameter("groupId");
        if (!groupIdParam.isEmpty()) {
            try {
                int groupId = Integer.parseInt(groupIdParam);
                ArrayList<User> users = JoinedGroup.dbSelectByGroup(groupId);
                response.getWriter().print(gson.toJson(users));
            }
            catch (NumberFormatException nfe) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        else {
            User user = User.lookUpByAuthToken(request.getHeader("authorization"));
            int userId = user == null ? -1 : user.getID();
            ArrayList<StudyGroup> studyGroups = JoinedGroup.dbSelectByUser(userId);
            response.getWriter().print(gson.toJson(studyGroups));
        }
    }
}
