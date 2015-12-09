package fr.ecp.sio.appenginedemo.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.Date;

/**
 * A model class to represent a single message
 */
// The annotation comes from the Objectify library.
// It identifies this class as persistable into the Datastore.
@Entity
public class Message {

    // An entity should have a field marked with the @Id annotation; it becomes the id for the Datastore (unicity, index, etc.)
    @Id
    public Long id;
    public String text;
    public Date date;

    // We don't use a simple User field for the author of the message.
    // Instead, we use a special Objectify class, a Ref<>, witch is just a reference to a user entity.
    // This trick allows lazy loading of users, and populating our model classes (remember there is not JOIN in the Datastore).
    // We take care of how this Ref<> will be serialized into JSON in the GsonFactory class.
    // The @Load annotation is required for automatically loading the author when a message is retrieved.
    @Load
    public Ref<User> user;

}