package fr.ecp.sio.appenginedemo.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.utils.ValidationUtils;
import fr.ecp.sio.appenginedemo.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Michaël on 02/11/2015.
 */
public class TokenServlet extends JsonServlet {

    @Override
    protected Object doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        // Extract login and password from request
        JsonObject params = new JsonParser()
                .parse(
                    new InputStreamReader(req.getInputStream())
                ).getAsJsonObject();
        String login = params.get("login").getAsString();
        String password = params.get("password").getAsString();

        // Check validity
        if (!ValidationUtils.validateLogin(login)) {
            throw new ApiException(400, "invalidLogin", "Login did not match the specs");
        }
        if (!ValidationUtils.validatePassword(password)) {
            throw new ApiException(400, "invalidPassword", "Password did not match the specs");
        }

        // Get user from login
        User user = UsersRepository.getUser(login);
        if (user != null) {
            // SHA 256 password (salt = id)
            // Check password (hash...)
            // Generate token
            return "a567z";
        } else {
            throw new ApiException(404, "invalidLogin", "User not found");
        }

    }

}