package fr.ecp.sio.appenginedemo.api;

import com.google.gson.JsonObject;
import fr.ecp.sio.appenginedemo.data.MessagesRepository;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.Message;
import fr.ecp.sio.appenginedemo.model.User;
import fr.ecp.sio.appenginedemo.utils.Global;
import fr.ecp.sio.appenginedemo.utils.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * A servlet to handle all the requests on a specific user
 * All requests with path matching "/users/*" where * is the id of the user are handled here.
 */
public class UserServlet extends JsonServlet {

    // A GET request should simply return the user
    @Override
    protected User doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        User targetUser = getUser(req);
        // Not found?
        if (targetUser == null)
            throw new ApiException(400, "invalidRequest", "User not found");

        User currentUser = getAuthenticatedUser(req);
        // Not found?
        if(currentUser == null)
            throw new ApiException(400, "invalidRequest", "User is not authenticated");

        // Add some mechanism to hide private info about a user (email) except if he is the caller
        //if the target user is not the current user, we hide the email of the target user
        if(currentUser.id != targetUser.id)
        {
            targetUser.email="";
        }
        return targetUser;
    }

    // A POST request could be used by a user to edit its own account
    // It could also handle relationship parameters like "followed=true"
    @Override
    protected User doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {
        // Get the user as below
        User userToEdit = getUser(req);
        // Not found?
        if (userToEdit == null)
            throw new ApiException(400, "invalidRequest", "User not found");

        // Apply some changes on the user (after checking for the connected user)
        User currentUser = getAuthenticatedUser(req);
        if(currentUser == null)
            throw new ApiException(400, "invalidRequest", "User is not authenticated");

        //get parameter followed true|false
        String followedParam = req.getHeader(Global.FOLLOWED);
        if(followedParam != null && (followedParam.toLowerCase().equals("true")||followedParam.toLowerCase().equals("false"))) {
            //start working on handling relationships
            boolean followed = Boolean.parseBoolean(followedParam);
            //user can't follow himself
            if(currentUser.id != userToEdit.id)
                UsersRepository.setUserFollowed(currentUser.id, userToEdit.id, followed);
        }

        //get new login, new password, new email
        User modifiedUser = getJsonRequestBody(req, User.class);
        if (modifiedUser == null)
        {
            //request with no JSON body
            //return current user
            return currentUser;
        }

        //validate inputs
        if (modifiedUser.password != null && !ValidationUtils.validatePassword(modifiedUser.password)) {
            throw new ApiException(400, "invalidPassword", "Password did not match the specs");
        }
        if (modifiedUser.email != null) {
            if(!ValidationUtils.validateEmail(modifiedUser.email))
                throw new ApiException(400, "invalidEmail", "Invalid email");

            User tempUser = UsersRepository.getUserByEmail(modifiedUser.email);
            if (tempUser != null && modifiedUser.id != tempUser.id) {
                throw new ApiException(400, "duplicateEmail", "Duplicate email");
            }
        }

        //a user can modify his own account
        if(userToEdit.id == currentUser.id) {
            modifiedUser.id = userToEdit.id;
            return UsersRepository.UpdateUser(modifiedUser);
        }
        else {
                throw new ApiException(400, "invalidRequest", "You don't have the right to modify other user's account");
        }
    }

    // A user can DELETE its own account
    @Override
    protected Void doDelete(HttpServletRequest req) throws ServletException, IOException, ApiException {
        // Get the user as below
        User targetUser = getUser(req);
        // Not found?
        if (targetUser == null)
            throw new ApiException(400, "invalidRequest", "User not found");

        User currentUser = getAuthenticatedUser(req);
        // Not found?
        if(currentUser == null)
            throw new ApiException(400, "invalidRequest", "User is not authenticated");

        // Security checks
        if(currentUser.id == targetUser.id)
        {
            // Delete the messages
            List<Message> ListMessagesUser =  MessagesRepository.getUserMessages(currentUser.id);
            if(ListMessagesUser != null && ListMessagesUser.size()> 0)
                MessagesRepository.deleteListMessages(ListMessagesUser);

            // Delete the relationships
            deleteRelationshipsForUser(currentUser);

            // Delete user
            UsersRepository.deleteUser(currentUser.id);
        }
        else
            throw new ApiException(400, "invalidRequest", "You don't have the right to delete this user");

        // A DELETE request shall not have a response body
        return null;
    }

    //get user Id from URL
    private User getUser(HttpServletRequest req) throws ApiException
    {
        //test if it's ME in URL
        String param = req.getPathInfo().substring(1);
        if(param != null && param.equals(Global.ME))
            return getAuthenticatedUser(req);

        //Extract the id of the user from the last part of the path of the request
        long id = -1 ;
        try {
            id = Long.parseLong(param);
        }
        catch (Exception e)
        {
            throw new ApiException(400, "invalidRequest", "Id is not valid");
        }
        // Check if this id is syntactically correct
        if(id < 0)
            throw new ApiException(400, "invalidRequest", "Id is not valid");
        // Lookup in repository
        return UsersRepository.getUser(id);
    }

    //delete all relationships for a user
    private void deleteRelationshipsForUser(User currentUser)
    {
        //get user followed
        UsersRepository.UsersList UserFollowed = UsersRepository.getUserFollowed(currentUser.id);
        List<User> UserFollowedList = UserFollowed != null ? UserFollowed.users : null;
        //Delete the relationships with followed
        if(UserFollowedList != null && UserFollowedList.size()> 0) {
            for (User followed : UserFollowedList)
                UsersRepository.setUserFollowed(currentUser.id, followed.id, false);
        }
        //get user followers
        UsersRepository.UsersList UserFollowers = UsersRepository.getUserFollowers(currentUser.id);
        List<User> UserFollowersList = UserFollowers != null ? UserFollowers.users : null;
        //Delete the relationships with followers
        if(UserFollowersList != null && UserFollowersList.size()> 0) {
            for (User follower : UserFollowersList)
                UsersRepository.setUserFollowed(follower.id, currentUser.id, false);
        }
    }
}