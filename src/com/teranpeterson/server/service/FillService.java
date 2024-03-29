package com.teranpeterson.server.service;

import com.teranpeterson.server.dao.DAOException;
import com.teranpeterson.server.dao.Database;
import com.teranpeterson.server.dao.UserDAO;
import com.teranpeterson.server.helpers.Generator;
import com.teranpeterson.server.model.User;
import com.teranpeterson.server.request.FillRequest;
import com.teranpeterson.server.result.FillResult;

import java.sql.Connection;

/**
 * Populates the server's database with generated data for the specified user name.
 *
 * @author Teran Peterson
 * @version v0.1.2
 */
public class FillService {
    /**
     * Creates a blank fill service object
     */
    public FillService() {

    }

    /**
     * Populates the server's database with generated data for the specified user name.
     * The required "userName" parameter must be a user already registered with the server. If there is
     * any data in the database already associated with the given user name, it is deleted. The
     * optional “generations” parameter lets the caller specify the number of generations of ancestors
     * to be generated, and must be a non-negative integer (the default is 4)
     *
     * @param request Information about the user to generate data for
     * @return Information about the generated persons
     */
    public FillResult fill(FillRequest request) {
        if (request.getGenerations() < 0) return new FillResult("ERROR: Invalid number of generations");

        Database db = new Database();
        try {
            // Load the user's information
            db.createTables();
            Connection conn = db.openConnection();
            UserDAO userDAO = new UserDAO(conn);
            User user = userDAO.find(request.getUserName());

            // Validate the provided username
            if (user == null) {
                try {
                    db.closeConnection(false);
                    return new FillResult("ERROR: Invalid userName");
                } catch (DAOException e) {
                    e.printStackTrace();
                    return new FillResult(e.getMessage());
                }
            }
            db.closeConnection(true);

            // Create ancestral information for the user
            Generator generator = new Generator();
            generator.generate(user.getPersonID(), request.getGenerations());

            // Return the number of person's and event's added
            int x = (int) Math.pow(2, (request.getGenerations() + 1)) - 1; // Calculate number of added persons (2^(n+1) - 1)
            return new FillResult(x, (x - 1) * 4 + 2); // Calculate number of events (4 per person except user)
        } catch (DAOException e) {
            e.printStackTrace();
            try {
                db.closeConnection(false);
                return new FillResult(e.getMessage());
            } catch (DAOException d) {
                d.printStackTrace();
                return new FillResult(d.getMessage());
            }
        }
    }
}
