package fr.ecp.sio.appenginedemo.data;

import com.googlecode.objectify.ObjectifyService;
import fr.ecp.sio.appenginedemo.model.Message;

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

}