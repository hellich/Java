package fr.ecp.sio.appenginedemo.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * We may use Gson in different parts of our potentially large server application, so it is a good idea to have some common utils for it.
 */
public class GsonFactory {

    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // This method creates a instance of Gson properly configured to be used everywhere on our server
    public static Gson getGson() {
        // A builder pattern, with chained calls to set things up, then create()
        return new GsonBuilder()
                .disableHtmlEscaping()
                // This configures Gson to properly handle the Ref<?> fields defined in our model class
                .registerTypeAdapterFactory(new RefAdapterFactory())
                .setDateFormat(ISO_DATE_FORMAT)
                .create();
    }

}
