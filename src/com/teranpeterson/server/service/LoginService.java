package com.teranpeterson.server.service;

import com.teranpeterson.server.dao.*;
import com.teranpeterson.server.model.AuthToken;
import com.teranpeterson.server.model.User;
import com.teranpeterson.server.request.LoginRequest;
import com.teranpeterson.server.result.LoginResult;

import java.sql.Connection;

/**
 * Logs in the user and returns an auth token.
 *
 * @author Teran Peterson
 * @version v0.1.1
 */
public class LoginService {
    /**
     * Creates a blank login service object
     */
    public LoginService() {
    }

    /**
     * Logs in the user and returns an auth token.
     *
     * @param request Information about the user logging in
     * @return Information the user with an auth token for the active session
     */
    public LoginResult login(LoginRequest request) {
        // Error check the provided parameters
        if (request.getUserName() == null || request.getUserName().isEmpty())
            return new LoginResult("ERROR: Missing userName parameter");
        if (request.getPassword() == null || request.getPassword().isEmpty())
            return new LoginResult("ERROR: Missing password parameter");

        Database db = new Database();
        try {
            // Authenticate the user
            db.createTables();
            Connection conn = db.openConnection();
            UserDAO userDAO = new UserDAO(conn);
            User user = userDAO.authenticate(request.getUserName(), request.getPassword());

            // Establish session and return auth token
            if (user != null) {
                AuthToken token = new AuthToken(user.getUserName());
                AuthTokenDAO authTokenDAO = new AuthTokenDAO(conn);
                authTokenDAO.insert(token);

                db.closeConnection(true);
                return new LoginResult(token.getToken(), user.getUserName(), user.getPersonID());
            } else {
                db.closeConnection(false);
                return new LoginResult("ERROR: Invalid userName or password");
            }
        } catch (DAOException e) {
            e.printStackTrace();
            try {
                db.closeConnection(false);
                return new LoginResult(e.getMessage());
            } catch (DAOException d) {
                d.printStackTrace();
                return new LoginResult(d.getMessage());
            }
        }
    }
}
