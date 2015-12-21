package fr.ecp.sio.appenginedemo.data;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import fr.ecp.sio.appenginedemo.model.User;

import java.util.ArrayList;
import java.util.List;

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

    //get a user by its login
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

    //get a user by its email
    public static User getUserByEmail(final String email) {
        return ObjectifyService.ofy()
                .load()
                .type(User.class)
                .filter("email", email)
                .first()
                .now();
    }

    //get a user by its id
    public static User getUser(long id) {
        return ObjectifyService.ofy()
                .load()
                .type(User.class)
                .id(id)
                .now();
    }

    //get all users
    public static UsersList getUsers() {
        return new UsersList(
            ObjectifyService.ofy()
                .load()
                .type(User.class)
                .list(),
            "dummyCursor"
        );
    }

    //allocate an id to a user
    public static long allocateNewId() {
        // Sometime we need to allocate an id before persisting, the library allows it
        return new ObjectifyFactory().allocateId(User.class).getId();
    }

    //save a user
    public static void saveUser(User user) {
        user.id = ObjectifyService.ofy()
                .save()
                .entity(user)
                .now()
                .getId();
    }

    //delete a user by passing its id
    public static void deleteUser(long id) {
        ObjectifyService.ofy()
                .delete()
                .type(User.class)
                .id(id)
                .now();
    }

    //get list of users that a user follows
    //we pass the user id, limit and a cursor
    public static UsersList getUserFollowed(long id, Integer limit, String cursor) {
        return getUsers();
    }

    //get list of users that a user follows
    //we pass the user id
    public static UsersList getUserFollowed(long id) {
        return getUsers();
    }

    //get the list of users that follows a user
    //we pass the user id, limit and cursor
    public static UsersList getUserFollowers(long id, Integer limit, String cursor) {
        return getUsers();
    }

    //get the list of users that follows a user
    //we pass the user id
    public static UsersList getUserFollowers(long id) {
        return getUsers();
    }

    //set the relathionship between 2 users
    public static void setUserFollowed(long followerId, long followedId, boolean followed) {
    }

    //update a user
    public static User UpdateUser(User user) {
        return getUser(user.id);
    }

    public static void setUserAvatar(long userId, String avatarUrl){

    }

    public static class UsersList {

        public final List<User> users;
        public final String cursor;

        private UsersList(List<User> users, String cursor) {
            this.users = users;
            this.cursor = cursor;
        }
    }
}