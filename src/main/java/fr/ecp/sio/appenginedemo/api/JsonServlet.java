package fr.ecp.sio.appenginedemo.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.gson.GsonFactory;
import fr.ecp.sio.appenginedemo.model.User;
import fr.ecp.sio.appenginedemo.utils.TokenUtils;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JsonServlet is a base servlet class for all the endpoint servlet we create for our API.
 * It encapsulate the code to :
 *  - parse the request as JSON
 *  - check the connected user (token)
 *  - send a response as JSON
 */
public class JsonServlet extends HttpServlet {

    // A constant Pattern, a regex class used to validate and parse the authorization header.
    // The Pattern is built from the regex string using the static method compile(), then it is ready to be used.
    protected static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("Bearer (.+)");

    // All servlets behave the same: they receive request (req) and are supposed to write to the response (resp).
    // Note that the method does not return the response, instead it can write to (like a stream).
    // We override this default behaviour to handle the writing of the response as JSON.
    // This implementation is final, so our subclasses cannot override it.
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Call our custom doGet() below to get the response as an Object
            Object response = doGet(req);
            // Write this object as JSON into the response
            sendResponse(response, resp);
        } catch (ApiException e) {
            // Our subclasses can raise a custom ApiException for the API to send well-formatted errors
            // If we catch one of these, we set the HTTP response code to the defined value and send the error object as JSON
            resp.setStatus(e.getError().status);
            sendResponse(e.getError(), resp);
        }
    }

    // Instead of overriding the default doGet() method above, our subclasses are supposed to override this one.
    // This method returns an object that will be written to the response.
    // The method can throw ServletException and IOException (default for servlet), but also our custom ApiException
    protected Object doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return null;
    }

    // Same behavior as for doGet(), we must take care of all the HTTP methods!
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object response = doPost(req);
            sendResponse(response,resp);
        } catch (ApiException e) {
            resp.setStatus(e.getError().status);
            sendResponse(e.getError(), resp);
        }
    }

    // Our custom doPost(), to be optionally overwritten by sub-servlets.
    protected Object doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return null;
    }

    // Same behavior as for doGet(), we must take care of all the HTTP methods!
    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object response = doDelete(req);
            sendResponse(response,resp);
        } catch (ApiException e) {
            resp.setStatus(e.getError().status);
            sendResponse(e.getError(), resp);
        }
    }

    // Our custom doDelete(), to be optionally overwritten by sub-servlets.
    protected Object doDelete(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return null;
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Call our custom doGet() below to get the response as an Object
            Object response = doPut(req);
            // Write this object as JSON into the response
            sendResponse(response, resp);
        } catch (ApiException e) {
            // Our subclasses can raise a custom ApiException for the API to send well-formatted errors
            // If we catch one of these, we set the HTTP response code to the defined value and send the error object as JSON
            resp.setStatus(e.getError().status);
            sendResponse(e.getError(), resp);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    // Instead of overriding the default doGet() method above, our subclasses are supposed to override this one.
    // This method returns an object that will be written to the response.
    // The method can throw ServletException and IOException (default for servlet), but also our custom ApiException
    protected Object doPut(HttpServletRequest req) throws ServletException, IOException, ApiException, FileUploadException {
        return null;
    }

    // Private common place for writing a response Object as JSON into the response stream
    private void sendResponse(Object response, HttpServletResponse resp) throws IOException {
        // Before writing the actual response, we can send response headers (key-value pairs describing the response)
        // HttpServletResponse has methods to write some headers (here, "Content-Type")
        resp.setContentType("application/json");
        // After the headers are written, we can go for the response body
        // We rely on the Gson library, giving it the object and a Writer opened on the response OutputStream
        GsonFactory.getGson().toJson(response, resp.getWriter());
    }

    // This method can be used by our sub-servlets to get the User sending the request
    // We parse the response header, check it against our repository and return it
    protected static User getAuthenticatedUser(HttpServletRequest req) throws ApiException {
        // Client applications are supposed to send their token in a "Authorization" header
        String auth = req.getHeader("Authorization");
        if (auth != null) {
            // We use our static pattern to both validate the pattern and get the token
            // We create a Matcher that can be used (single use) to validate an input string
            Matcher m = AUTHORIZATION_PATTERN.matcher(auth);
            if (!m.matches()) {
                // The header is not well formatted (should be "Bearer xxxxxxxx")
                throw new ApiException(401, "invalidAuthorization", "Invalid authorization header format");
            }
            try {
                // Our tokens actually are just and encrypted id, lets decrypt it
                // m.group(1) is the first value that was captured by the regex pattern (the token itself)
                long id = TokenUtils.parseToken(m.group(1));
                // We have the id, lets simply get the user from our repository
                return UsersRepository.getUser(id);
            } catch (SignatureException e) {
                // The decryption of the token failed!
                throw new ApiException(401, "invalidAuthorization", "Invalid token");
            }
        } else {
            return null;
        }
    }

    // This method can be used by our sub-servlets to get the request JSON body as a JsonObject (generic parsing)
    protected static JsonObject getJsonRequestBody(HttpServletRequest req) throws IOException {
        // Here again we simply rely on the Gson library, giving it a Reader opened on the request InputStream
        // The request is assumed to be a JSON object { ... } with this method
        return new JsonParser()
                .parse(req.getReader())
                .getAsJsonObject();
    }

    // This method can be used by our sub-servlets to get the request JSON body as an object
    // It will parse the request and convert it to an instance of the specified type
    // This is a generic method: the return type T depends on ("is bound to") the second parameter
    protected static <T> T getJsonRequestBody(HttpServletRequest req, Class<T> type) throws IOException {
        // We used the Gson library for parsing
        return GsonFactory.getGson().fromJson(req.getReader(), type);
    }

    private static boolean checkRight(HttpServletRequest reqCurrentUser, long idTargetUser) throws ApiException
    {
        return getAuthenticatedUser(reqCurrentUser).id == idTargetUser;
    }
}
