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
@WebServlet("/api/register")
public class Register extends HttpServlet {
	// Register account
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = new Gson().fromJson(request.getReader(), models.User.class);
        int errorCode = user.insertToDatabase();
        if (errorCode == 0) {
        	response.setStatus(HttpServletResponse.SC_CREATED);
        }else if (errorCode == 2) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }else {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    // Update password
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        String password = request.getParameter("password");
        User user = new Gson().fromJson(request.getReader(), models.User.class);
        user.setAuthToken(request.getHeader("authorization"));
        user.setPassword(password);
        if (!user.updatePassword()) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    // Forget Password
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        String email = request.getParameter("email");
        int errorCode = User.forgetPassword(email);
        if (errorCode == 1) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }else if (errorCode == 2) {
        	// Too many requests within a short time span
        	response.setStatus(429);
        }
    }
}
