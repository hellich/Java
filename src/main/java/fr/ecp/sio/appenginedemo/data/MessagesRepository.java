package fr.ecp.sio.appenginedemo.data;

import com.googlecode.objectify.ObjectifyService;
import fr.ecp.sio.appenginedemo.model.Message;

import java.util.List;

/**
 * This is a repository class for the messages.
 * It could be backed by any kind of persistent storage engine.
 * Here we use the Datastore from Google Cloud Platform, and we access it using the high-level Objectify library.
 */
public class MessagesRepository {

    // A static initializer to register the model class with the Objectify service.
    // This is required per Objectify documentation.
    static {
        ObjectifyService.register(Message.class);
    }

    public static Message getMessage(long id) {
        // The Objectify library uses common syntax that helps chaining calls (like the Builder pattern)
        // Here we get a static Objectify service instance (ofy), create a query (load), specify a kind of desired results (type) and an id, then execute synchronously with now()
        return ObjectifyService.ofy()
                .load()
                .type(Message.class)
                .id(id)
                .now();
    }

    public static List<Message> getMessages() {
        // Same as above, without id, returns multiple results as a list
        return ObjectifyService.ofy()
                .load()
                .type(Message.class)
                .list();
    }

    public static void insertMessage(Message message) {
        // Persisting an entity is just a save() query
        message.id = ObjectifyService.ofy()
                .save()
                .entity(message)
                .now()
                .getId();
    }

    public static void deleteMessage(long id) {
        ObjectifyService.ofy()
                .delete()
                .type(Message.class)
                .id(id)
                .now();
    }

}