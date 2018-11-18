package servlets;

import com.google.gson.Gson;
import models.User;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MultipartConfig
@WebServlet("/api/login")
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        User user = new Gson().fromJson(request.getReader(), models.User.class);
        String token = user.authenticate();
        if (token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        User populatedUser = User.lookUpByAuthToken(token);
        if (populatedUser == null) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        response.setContentType("application/json");

        response.getWriter().print(gson.toJson(populatedUser));
    }
    
    // Log out
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        String token = request.getHeader("autToken");
        boolean out = User.onLogOut(token);
        if (!out) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }
    
    // Update profile
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        User user = new Gson().fromJson(request.getReader(), models.User.class);
        user.setAuthToken(request.getHeader("authorization"));
        if (!user.updateProfile()) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
