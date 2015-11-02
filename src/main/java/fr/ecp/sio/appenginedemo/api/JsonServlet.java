package fr.ecp.sio.appenginedemo.api;

import com.google.gson.Gson;
import fr.ecp.sio.appenginedemo.data.UsersRepository;
import fr.ecp.sio.appenginedemo.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Michaël on 30/10/2015.
 */
public class JsonServlet extends HttpServlet {

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object response = doGet(req);
            sendResponse(response, resp);
        } catch (ApiException e) {
            resp.setStatus(e.getError().status);
            sendResponse(e.getError(), resp);
        }
    }

    protected Object doGet(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return null;
    }

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

    protected Object doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return null;
    }

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

    protected Object doDelete(HttpServletRequest req) throws ServletException, IOException, ApiException {
        return null;
    }

    private void sendResponse(Object response, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        new Gson().toJson(response, resp.getWriter());
    }

    protected User getAuthenticatedUser(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth != null) {
            // Check that auth is "Bearer {a token}" (Pattern)
            // Check token
            // Handle possible error
            // Get the id of the user
            long id = 2;
            return UsersRepository.getUser(id);
        } else {
            return null;
        }
    }

}
