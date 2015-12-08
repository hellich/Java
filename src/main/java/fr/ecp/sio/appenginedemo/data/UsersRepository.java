package fr.ecp.sio.appenginedemo.data;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import fr.ecp.sio.appenginedemo.model.Message;
import fr.ecp.sio.appenginedemo.model.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This is a repository class for the users.
 * It could be backed by any kind of persistent storage engine.
 * Here we use the Datastore from Google Cloud Platform, and we access it using the high-level Objectify library.
 */
public class UsersRepository {

    // A static initializer to register the model class with the Objectify service.
    // This is required per Objectify documentation.
    static {
        ObjectifyService.register(User.class);
    }

    public static User getUserByLogin(final String login) {
        // We can add filter of a property if this property has the @Index annotation in the model class
        // first() returns only one result
        return ObjectifyService.ofy()
                .load()
                .type(User.class)
                .filter("login", login)
                .first()
                .now();
    }

    public static User getUserByEmail(final String email) {
        return ObjectifyService.ofy()
                .load()
                .type(User.class)
                .filter("email", email)
                .first()
                .now();
    }

    public static User getUser(long id) {
        return ObjectifyService.ofy()
                .load()
                .type(User.class)
                .id(id)
                .now();
    }

    public static List<User> getUsers() {
        return ObjectifyService.ofy()
                .load()
                .type(User.class)
                .list();
    }

    public static long allocateNewId() {
        // Sometime we need to allocate an id before persisting, the library allows it
        return new ObjectifyFactory().allocateId(User.class).getId();
    }

    public static void insertUser(User user) {
        user.id = ObjectifyService.ofy()
                .save()
                .entity(user)
                .now()
                .getId();
    }

}