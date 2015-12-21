package fr.ecp.sio.appenginedemo.api.Upload;

import fr.ecp.sio.appenginedemo.Upload.Upload;
import fr.ecp.sio.appenginedemo.api.ApiException;
import fr.ecp.sio.appenginedemo.api.JsonServlet;
import fr.ecp.sio.appenginedemo.data.UsersRepository;

import javax.servlet.http.HttpServlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Helmi-PC on 20/12/2015.
 */
public class UploadServlet extends JsonServlet {

    @Override
    protected String doPost(HttpServletRequest req) throws ServletException, IOException, ApiException {

        //Upload is tested with a JSP page named "test.jsp".
        //You can test it locally on http://localhost:8080/test.jsp
        //Upload works fine from the JSP page
        String UploadedFile = Upload.UploadFile(req, "avatar");
        //get target user id
        /*String stringId = req.getParameter("Id");
        long targerUserId = -1 ;
        try {
            targerUserId = Long.parseLong(stringId);
        }
        catch (Exception e) {
            throw new ApiException(400, "invalidRequest", "Id is not valid");
        }
        //update the avatar url of the target user
        UsersRepository.setUserAvatar(targerUserId, UploadedFile);*/
        //return the avatar url
        return UploadedFile;
    }
}
