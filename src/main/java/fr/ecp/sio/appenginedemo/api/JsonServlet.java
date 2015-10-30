package fr.ecp.sio.appenginedemo.api;

import com.google.gson.Gson;

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
        Object response = doGet(req);
        sendResponse(response,resp);
    }

    protected Object doGet(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object response = doPost(req);
        sendResponse(response,resp);
    }

    protected Object doPost(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object response = doDelete(req);
        sendResponse(response,resp);
    }

    protected Object doDelete(HttpServletRequest req) throws ServletException, IOException {
        return null;
    }

    private void sendResponse(Object response, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        new Gson().toJson(response, resp.getWriter());
    }

}
