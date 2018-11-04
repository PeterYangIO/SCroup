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
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = new User(email, password);
        Gson gson = new Gson();
        response.setContentType("application/json");
        String token = user.authenticate();

        System.out.println(User.lookUpByAuthToken(token));
        response.getWriter().print(gson.toJson(user));
    }
}
