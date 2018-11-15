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
        	System.out.println("Account successfully registered!");
        }else if (errorCode == 2) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }else {
        	System.out.println("Internal error with account registration");
        }
    }
    
    // Forget Password
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        String email = request.getHeader("email");
        if (!User.forgetPassword(email)) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
