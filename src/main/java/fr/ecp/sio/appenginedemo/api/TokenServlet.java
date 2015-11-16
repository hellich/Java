package fr.ecp.sio.appenginedemo.api;

import com.google.gson.JsonObject;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.utils.TokenUtils;
import fr.ecp.sio.appenginedemo.utils.ValidationUtils;
import fr.ecp.sio.appenginedemo.model.User;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Michaël on 02/11/2015.
 */
public class TokenServlet extends JsonServlet {

    @Override
    protected String doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        // Extract login and password from request
        JsonObject params = getJsonParameters(req);
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
        User user = UsersRepository.getUserByLogin(login);
        if (user != null) {
            String hash = DigestUtils.sha256Hex(password + user.id);
            if (hash.equals(user.password)) {
                return TokenUtils.generateToken(user.id);
            } else {
                throw new ApiException(403, "invalidPassword", "Incorrect password");
            }
        } else {
            throw new ApiException(404, "invalidLogin", "User not found");
        }

    }

}