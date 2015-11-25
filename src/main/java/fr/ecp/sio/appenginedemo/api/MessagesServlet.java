package fr.ecp.sio.appenginedemo.api;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Ref;
import fr.ecp.sio.appenginedemo.data.MessagesRepository;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.Message;
import fr.ecp.sio.appenginedemo.model.User;
import fr.ecp.sio.appenginedemo.utils.TokenUtils;
import fr.ecp.sio.appenginedemo.utils.ValidationUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Michaël on 30/10/2015.
 */
public class MessagesServlet extends JsonServlet {

    @Override
    protected Object doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return MessagesRepository.getMessages();
    }

    @Override
    protected Object doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        Message message = getJsonParameters(req, Message.class);
        if (message == null) {
            throw new ApiException(400, "invalidRequest", "Invalid JSON body");
        }

        // TODO: validate message

        message.user = Ref.create(getAuthenticatedUser(req));
        message.id = null;
        message.id = MessagesRepository.insertMessage(message);

        return message;

    }

}
