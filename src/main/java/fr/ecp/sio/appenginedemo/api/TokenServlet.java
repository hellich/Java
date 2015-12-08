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
 * This servlet handle the authorization process, it receives the "/auth/token" requests.
 * It receives a login and a password, and send an API token that the client application should keep.
 * Then the client will only use this token to sign the subsequent API calls.
 */
public class TokenServlet extends JsonServlet {

    // This is not very REST-ful, but we always avoid sending critical data (password) as a query parameter.
    // So, we use a POST method to handle the login and password as a JSON request body.
    @Override
    protected String doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        // Extract login and password from request
        JsonObject params = getJsonRequestBody(req);
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
            // Build an hash with the provided password and the user id as a salt
            String hash = DigestUtils.sha256Hex(password + user.id);
            // Compare it with the hash from the user object
            if (hash.equals(user.password)) {
                // Everything is OK, create and return a token (actually just the encrypted id)
                return TokenUtils.generateToken(user.id);
            } else {
                throw new ApiException(403, "invalidPassword", "Incorrect password");
            }
        } else {
            throw new ApiException(404, "invalidLogin", "User not found");
        }

    }

}