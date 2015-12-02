package fr.ecp.sio.appenginedemo.api;

import com.googlecode.objectify.ObjectifyFactory;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.User;
import fr.ecp.sio.appenginedemo.utils.MD5Utils;
import fr.ecp.sio.appenginedemo.utils.TokenUtils;
import fr.ecp.sio.appenginedemo.utils.ValidationUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Michaël on 30/10/2015.
 */
public class UsersServlet extends JsonServlet {

    @Override
    protected Object doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return UsersRepository.getUsers();
    }

    @Override
    protected String doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        User user = getJsonParameters(req, User.class);
        if (user == null) {
            throw new ApiException(400, "invalidRequest", "Invalid JSON body");
        }

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

        user.avatar = "http://www.gravatar.com/avatar/" + MD5Utils.md5Hex(user.email) + "?d=wavatar";

        // Explicitly give a fresh id to the user (we need it for next step)
        user.id = new ObjectifyFactory().allocateId(User.class).getId();

        // Hash password
        user.password = DigestUtils.sha256Hex(user.password + user.id);

        // Save user
        long id = UsersRepository.insertUser(user);

        // Return a token
        return TokenUtils.generateToken(id);
    }

}
