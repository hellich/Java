package fr.ecp.sio.appenginedemo.api;

import fr.ecp.sio.appenginedemo.MessagesRepository;
import fr.ecp.sio.appenginedemo.ResponseUtils;
import fr.ecp.sio.appenginedemo.model.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Michaël on 30/10/2015.
 */
public class MessageServlet extends JsonServlet {

    @Override
    protected Object doGet(HttpServletRequest req) throws ServletException, IOException {
        // Get URL id
        // Check id
        long id = 0;
        // Lookup in repository
        return MessagesRepository.getMessage(id);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

}
