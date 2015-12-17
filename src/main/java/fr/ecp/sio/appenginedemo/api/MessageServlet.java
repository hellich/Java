package fr.ecp.sio.appenginedemo.api;

import fr.ecp.sio.appenginedemo.data.MessagesRepository;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.Message;
import fr.ecp.sio.appenginedemo.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A servlet to handle all the requests on a specific message
 * All requests with path matching "/messages/*" where * is the id of the message are handled here.
 */
public class MessageServlet extends JsonServlet {

    // A GET request should simply return the message
    @Override
    protected Message doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        //Extract the id of the message from the last part of the path of the request
        //Check if this id is syntactically correct
        Message message = getMessage(req);
        if(message == null)
            throw new ApiException(400, "invalidRequest", "Message does not exist");

        return message;
    }

    // A POST request could be made to modify some properties of a message after it is created
    @Override
    protected Message doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {
        //Get the message as below
        Message messageToEdit = getMessage(req);
        if(messageToEdit == null)
            throw new ApiException(400, "invalidRequest", "Message does not exist");

        User currentUser = getAuthenticatedUser(req);
        if(currentUser == null)
            throw new ApiException(400, "invalidRequest", "User is not authenticated");

        //Check that the calling user is the author of the message (security!)
        if(currentUser.id == messageToEdit.user.getValue().id) {
            //Apply the changes
            Message targetMessage = getJsonRequestBody(req, Message.class);
            if (targetMessage == null)
                throw new ApiException(400, "invalidRequest", "Invalid JSON body");

            targetMessage.id = messageToEdit.id;
            //Return the modified message
            return MessagesRepository.updateMessage(targetMessage);
        }
        else
            throw new ApiException(400, "invalidRequest", "You don't have the right to edit this message");
    }

    // A DELETE request should delete a message (if the user)
    @Override
    protected Void doDelete(HttpServletRequest req) throws ServletException, IOException, ApiException {
        //Get the message
        Message message = getMessage(req);
        if(message == null)
            throw new ApiException(400, "invalidRequest", "Message does not exist");

        //Check that the calling user is the author of the message (security!)
        User currentUser = getAuthenticatedUser(req);
        if(currentUser == null)
            throw new ApiException(400, "invalidRequest", "User is not authenticated");

        if(currentUser.id == message.user.getValue().id)
        {
            //Delete the message
            MessagesRepository.deleteMessage(message.id);
        }
        else
            throw new ApiException(400, "invalidRequest", "You don't have the right to delete this message");

        // A DELETE request shall not have a response body
        return null;
    }

    //get message from Id in URL
    private Message getMessage(HttpServletRequest req) throws ApiException
    {
        //Extract the id of the user from the last part of the path of the request
        long id = -1 ;
        try {
            id = Long.parseLong(req.getPathInfo().substring(1));
        }
        catch (Exception e)
        {
            throw new ApiException(400, "invalidRequest", "Id is not valid");
        }
        // Check if this id is syntactically correct
        if(id < 0)
            throw new ApiException(400, "invalidRequest", "Id is not valid");
        // Lookup in repository
        return MessagesRepository.getMessage(id);
    }

}
