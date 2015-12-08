package fr.ecp.sio.appenginedemo.api;

import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.User;
import fr.ecp.sio.appenginedemo.utils.TokenUtils;
import fr.ecp.sio.appenginedemo.utils.ValidationUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * A servlet to handle all the requests on a list of users
 * All requests on the exact path "/users" are handled here.
 */
public class UsersServlet extends JsonServlet {

    // A GET request should return a list of users
    @Override
    protected List<User> doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        // TODO: define parameters to search/filter users by login, with limit, order...
        // TODO: define parameters to get the followings and the followers of a user given its id
        return UsersRepository.getUsers();
    }

    // A POST request can be used to create a user
    // We can use it as a "register" endpoint; in this case we return a token to the client.
    @Override
    protected String doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        // The request should be a JSON object describing a new user
        User user = getJsonRequestBody(req, User.class);
        if (user == null) {
            throw new ApiException(400, "invalidRequest", "Invalid JSON body");
        }

        // Perform all the usul checkings
        if (!ValidationUtils.validateLogin(user.login)) {
            throw new ApiException(400, "invalidLogin", "Login did not match the specs");
        }
        if (!ValidationUtils.validatePassword(user.password)) {
            throw new ApiException(400, "invalidPassword", "Password did not match the specs");
        }
        if (!ValidationUtils.validateEmail(user.email)) {
            throw new ApiException(400, "invalidEmail", "Invalid email");
        }

        if (UsersRepository.getUserByLogin(user.login) != null) {
            throw new ApiException(400, "duplicateLogin", "Duplicate login");
        }
        if (UsersRepository.getUserByEmail(user.email) != null) {
            throw new ApiException(400, "duplicateEmail", "Duplicate email");
        }

        // Explicitly give a fresh id to the user (we need it for next step)
        user.id = UsersRepository.allocateNewId();

        // TODO: find a solution to receive an store profile pictures

        // Hash the user password with the id a a salt
        user.password = DigestUtils.sha256Hex(user.password + user.id);

        // Persist the user into the repository
        UsersRepository.insertUser(user);

        // Create and return a token for the new user
        return TokenUtils.generateToken(user.id);

    }

}
