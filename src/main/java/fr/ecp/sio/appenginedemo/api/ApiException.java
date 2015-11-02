package fr.ecp.sio.appenginedemo.api;

import fr.ecp.sio.appenginedemo.model.*;
import fr.ecp.sio.appenginedemo.model.Error;

/**
 * Created by Michaël on 02/11/2015.
 */
public class ApiException extends Exception {

    private Error mError;

    public ApiException(int status, String code, String message) {
        super(message);
        mError = new Error();
        mError.status = status;
        mError.code = code;
        mError.message = message;
    }

    public Error getError() {
        return mError;
    }

}
