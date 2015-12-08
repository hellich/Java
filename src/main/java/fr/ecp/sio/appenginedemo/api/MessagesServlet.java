package fr.ecp.sio.appenginedemo.api;

import com.googlecode.objectify.Ref;
import fr.ecp.sio.appenginedemo.data.MessagesRepository;
import fr.ecp.sio.appenginedemo.model.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * A servlet to handle all the requests on a list of messages
 * All requests on the exact path "/messages" are handled here.
 */
public class MessagesServlet extends JsonServlet {

    // A GET request should return a list of messages
    @Override
    protected List<Message> doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        // TODO: filter the messages that the user can see (security!)
        // TODO: filter the list based on some parameters (order, limit, scope...)
        // TODO: e.g. add a parameter to get the messages of a user given its id (i.e. /messages?author=256439)
        return MessagesRepository.getMessages();
    }

    // A POST request on a collection endpoint should create an entry and return it
    @Override
    protected Message doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        // The request should be a JSON object describing a new message
        Message message = getJsonRequestBody(req, Message.class);
        if (message == null) {
            throw new ApiException(400, "invalidRequest", "Invalid JSON body");
        }

        // TODO: validate the message here (minimum length, etc.)

        // Some values of the Message should not be sent from the client app
        // Instead, we give them here explicit value
        message.user = Ref.create(getAuthenticatedUser(req));
        message.id = null;

        // Our message is now ready to be persisted into our repository
        // After this call, our repository should have given it a non-null id
        MessagesRepository.insertMessage(message);

        return message;
    }

}
