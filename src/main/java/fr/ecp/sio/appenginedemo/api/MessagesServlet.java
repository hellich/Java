package fr.ecp.sio.appenginedemo.api;

import com.googlecode.objectify.Ref;
import fr.ecp.sio.appenginedemo.data.MessagesRepository;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.Message;
import fr.ecp.sio.appenginedemo.model.User;
import fr.ecp.sio.appenginedemo.utils.Enums;
import fr.ecp.sio.appenginedemo.utils.Global;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElementDecl;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A servlet to handle all the requests on a list of messages
 * All requests on the exact path "/messages" are handled here.
 */
public class MessagesServlet extends JsonServlet {

    // A GET request should return a list of messages
    @Override
    protected List<Message> doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        //get user
        User currentUser = getAuthenticatedUser(req);
        if(currentUser == null)
            throw new ApiException(400, "invalidRequest", "User is not authenticated");

        // add a parameter to get the messages of a user given its id (i.e. /messages?author=256439)
        String author = req.getParameter(Global.AUTHOR);
        if(author != null) {
            //request is /messages?author=256439
            if(author.equals(Global.ME))
                return MessagesRepository.getUserMessages(currentUser.id);
            else {
                Long authorId = null;
                //parse authorId
                try {
                    authorId = Long.parseLong(author);
                    return MessagesRepository.getUserMessages(authorId);
                } catch (NumberFormatException e) {
                    //authorId is not correctly defined
                }
            }
        }

        // filter the messages that the user can see (security!)
        //a user can see his messages
        List<Message> ListMessages = MessagesRepository.getUserMessages(currentUser.id);

        //a user can see the messages for followed users
        UsersRepository.UsersList ListFollowed = UsersRepository.getUserFollowed(currentUser.id);
        if(ListFollowed != null && ListFollowed.users != null && ListFollowed.users.size()>0) {
            for (User curUser : ListFollowed.users) {
                List<Message> tmpList = MessagesRepository.getUserMessages(curUser.id);
                if (tmpList != null && tmpList.size() > 0)
                    ListMessages.addAll(tmpList);
            }
        }
        // filter the list based on some parameters (order, limit, scope...)
        //get order
        String order = req.getHeader(Global.ORDER);
        if (order != null)
        {
            try {
                Enums.ORDER eOrder = Enums.ORDER.valueOf(order.toUpperCase());
                ListMessages = MessagesRepository.orderMessages(ListMessages, eOrder);
            }catch(Exception ex) {
                throw new ApiException(400, "invalidRequest", "Order is not defined correctly. It should be ASC or DESC");
            }
        }
        //get limit
        String limitHeader = req.getHeader(Global.LIMIT);
        if(limitHeader != null) {
            Integer limit = null;
            //parse limit
            try {
                limit = Integer.parseInt(limitHeader);
                if(limit <= 0)
                    throw new ApiException(400, "invalidRequest", "Limit should be greater than 0");
                if (limit < ListMessages.size())
                    ListMessages = ListMessages.subList(0, limit);
            } catch (NumberFormatException e) {
                //limit is not correctly defined
            }
        }

        return ListMessages;
    }

    // A POST request on a collection endpoint should create an entry and return it
    @Override
    protected Message doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        // The request should be a JSON object describing a new message
        Message message = getJsonRequestBody(req, Message.class);
        if (message == null) {
            throw new ApiException(400, "invalidRequest", "Invalid JSON body");
        }

        //get user
        User currentUser = getAuthenticatedUser(req);
        // Not found?
        if(currentUser == null)
            throw new ApiException(400, "invalidRequest", "User is not authenticated");

        // validate the message here (minimum length, etc.)
        if(message.text.replaceAll("\\s","").length() == 0)
            throw new ApiException(400, "invalidRequest", "Text message can not be empty!");

        // Some values of the Message should not be sent from the client app
        // Instead, we give them here explicit value
        message.user = Ref.create(currentUser);
        message.date = new Date();
        message.id = null;

        // Our message is now ready to be persisted into our repository
        // After this call, our repository should have given it a non-null id
        MessagesRepository.insertMessage(message);

        return message;
    }

}
