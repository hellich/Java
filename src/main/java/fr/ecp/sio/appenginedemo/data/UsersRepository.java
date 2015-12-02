package fr.ecp.sio.appenginedemo.data;

import com.googlecode.objectify.ObjectifyService;
import fr.ecp.sio.appenginedemo.model.Message;
import fr.ecp.sio.appenginedemo.model.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michaël on 02/11/2015.
 */
public class UsersRepository {

    static {
        ObjectifyService.register(User.class);
    }

    public static User getUserByLogin(final String login) {
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

    public static long insertUser(User user) {
        return ObjectifyService.ofy()
                .save()
                .entity(user)
                .now()
                .getId();
    }

}