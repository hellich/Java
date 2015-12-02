package fr.ecp.sio.appenginedemo.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.Date;

/**
 * Created by Michaël on 30/10/2015.
 */
@Entity
public class Message {

    @Id
    public Long id;
    public String text;
    public Date date;
    public @Load Ref<User> user;

}