package fr.ecp.sio.appenginedemo.api;

import com.google.gson.JsonObject;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.User;
import fr.ecp.sio.appenginedemo.utils.Global;
import fr.ecp.sio.appenginedemo.utils.MD5Utils;
import fr.ecp.sio.appenginedemo.utils.TokenUtils;
import fr.ecp.sio.appenginedemo.utils.ValidationUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

/**
 * A servlet to handle all the requests on a list of users
 * All requests on the exact path "/users" are handled here.
 */
public class UsersServlet extends JsonServlet {

    // A GET request should return a list of users
    @Override
    protected List<User> doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        //define parameters to search/filter users by login, with limit, order...
        //get limit and continuationToken
        String limitHeader = req.getHeader(Global.LIMIT);
        String continuationTokenHeader = req.getHeader(Global.CONTINUATION_TOKEN);
        int limit = -1;
        //parse limit
        try {
            limit = Integer.parseInt(limitHeader);
        }catch (NumberFormatException e){
            //limit is not correctly defined
        }

        // define parameters to get the followings and the followers of a user given its id
        //get the followedBy & followersOf
        String followedBy = req.getParameter(Global.FOLLOWED_BY);
        String followerOf = req.getParameter(Global.FOLLOWER_OF);

        if(followedBy == null && followerOf == null)
        {
            //request is /users
            return UsersRepository.getUsers().users;
        }

        if(followedBy != null)
        {
            //request is /users/{id}/followed
            long idfollowedBy=-1;
            if(followedBy != Global.ME)
            {
                try {
                    //try parse to long if it's an id
                    idfollowedBy = Long.parseLong(followedBy);
                    UsersRepository.UsersList ListUserFollowed;
                    if(limit>0)
                        ListUserFollowed = UsersRepository.getUserFollowed(idfollowedBy, limit);
                    else
                        ListUserFollowed = UsersRepository.getUserFollowed(idfollowedBy);
                    return ListUserFollowed != null ?  ListUserFollowed.users : null;
                }catch (NumberFormatException e)
                {
                    //it's not an id and it's not "me"
                    throw new ApiException(400, "invalidRequest", "Id followedBy is not valid");
                }
            }
            //followedBy parameter is equal to "me"
            else
            {
                User MeUser = getAuthenticatedUser(req);
                if(MeUser != null)
                {
                    return UsersRepository.getUserFollowed(MeUser.id, limit).users;
                }
                else
                    throw new ApiException(400, "invalidRequest", "Token is needed");
            }
        }

        if(followerOf != null)
        {
            //request is /users/{id}/follower
            long idfollowerOf=-1;
            if(followerOf != Global.ME)
            {
                try {
                    //try parse to long if it's an id
                    idfollowerOf = Long.parseLong(followerOf);
                    UsersRepository.UsersList ListUserFollowers;
                    if(limit>0)
                        ListUserFollowers = UsersRepository.getUserFollowed(idfollowerOf, limit);
                    else
                        ListUserFollowers = UsersRepository.getUserFollowed(idfollowerOf);
                    return ListUserFollowers != null ?  ListUserFollowers.users : null;
                }catch (NumberFormatException e)
                {
                    //it's not an id and it's not "me"
                    throw new ApiException(400, "invalidRequest", "Id followedBy is not valid");
                }
            }
            //followerOf parameter is equal to "me"
            else
            {
                User MeUser = getAuthenticatedUser(req);
                if(MeUser != null)
                {
                    return UsersRepository.getUserFollowers(MeUser.id, limit).users;
                }
                else
                    throw new ApiException(400, "invalidRequest", "Token is needed");
            }
        }
        return null;
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
        // Simulate an avatar image using Gravatar API
        user.avatar = "http://www.gravatar.com/avatar/" + MD5Utils.md5Hex(user.email) + "?d=wavatar";

        // Hash the user password with the id a a salt
        user.password = DigestUtils.sha256Hex(user.password + user.id);

        // Persist the user into the repository
        UsersRepository.saveUser(user);

        // Create and return a token for the new user
        return TokenUtils.generateToken(user.id);

    }

}
