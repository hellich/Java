package fr.ecp.sio.appenginedemo.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * A model class to represent a single user
 */
@Entity
public class User {

    @Id
    public long id;

    // Any property of an entity that may be queried (filter, search...) must be indexed for performance
    // The @Index annotation will make the Datastore service create a optimal index on this property
    @Index
    public String login;

    public String avatar;
    public String coverPicture;

    @Index
    public String email;

    public String password;
}