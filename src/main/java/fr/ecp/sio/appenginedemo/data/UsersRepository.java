package fr.ecp.sio.appenginedemo.data;

import fr.ecp.sio.appenginedemo.model.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Michaël on 02/11/2015.
 */
public class UsersRepository {

    // TODO: Implement with Google Datastore and Objectify
    // https://cloud.google.com/appengine/docs/java/datastore/

    private static final List<User> mUsers = new ArrayList<>();

    static {
        User john = new User();
        john.id = 2;
        john.login = "john";
        john.password = DigestUtils.sha256Hex("toto" + john.id);
        mUsers.add(john);
    }

    public static User getUser(final String login) {

        // Simple iteration
        for (User user : mUsers) {
            if (user.login.equals(login)) return user;
        }
        return null;

        // Lambda + streams (Java 8+)
        /*return mUsers.stream()
                .filter(user -> user.login.equals(login))
                .findFirst()
                .get();*/
    }

    public static User getUser(long id) {
        for (User user : mUsers) {
            if (user.id == id) return user;
        }
        return null;
    }

}