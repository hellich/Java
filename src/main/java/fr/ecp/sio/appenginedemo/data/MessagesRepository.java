package fr.ecp.sio.appenginedemo.data;

import com.googlecode.objectify.ObjectifyService;
import fr.ecp.sio.appenginedemo.model.Message;
import fr.ecp.sio.appenginedemo.model.User;

import java.util.List;

/**
 * Created by Michaël on 30/10/2015.
 */
public class MessagesRepository {

    static {
        ObjectifyService.register(Message.class);
    }

    public static Message getMessage(long id) {
        return ObjectifyService.ofy()
                .load()
                .type(Message.class)
                .id(id)
                .now();
    }

    public static List<Message> getMessages() {
        return ObjectifyService.ofy()
                .load()
                .type(Message.class)
                .list();
    }

    public static long insertMessage(Message message) {
        return ObjectifyService.ofy()
                .save()
                .entity(message)
                .now()
                .getId();
    }

}