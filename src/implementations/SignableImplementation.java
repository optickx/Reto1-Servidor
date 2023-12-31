package implementations;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.postgresql.util.PSQLException;

import exceptions.BadCredentialsException;
import exceptions.NoSuchUserException;
import exceptions.ServerCapacityException;
import exceptions.ServerErrorException;
import exceptions.UserAlreadyExistsException;
import interfaces.Signable;
import packets.User;
import pool.Pool;
import static server.Server.*;

import static implementations.SqlDefinitions.*;

public class SignableImplementation implements Signable {
    /**
     * Object used to store connections to the database
     */
    private Connection con;
    /**
     * Object used to execute queries
     */
    private PreparedStatement stmt;
    /**
     * Object used to execute calls to functions or procedures
     */
    private CallableStatement cstmt;

    @Override
    public User signIn(User user)
            throws BadCredentialsException, NoSuchUserException, ServerCapacityException, ServerErrorException {
        con = Pool.getConnection(); // gets an open connection to the database
        if (!isUser(user)) // check if user exists
            throw new NoSuchUserException(); // login information is incorrect
        return getUser(checkPassword(user)); // return built user
    }

    @Override
    public void signUp(User user) throws UserAlreadyExistsException, ServerCapacityException, ServerErrorException {
        con = Pool.getConnection(); // gets an open connection to the database
        if (isUser(user))
            throw new UserAlreadyExistsException();
        insertNewUser(user); // create a new user
        doClosing();

    }

    /**
     * Method that checks that the login information provided by the user is correct
     * 
     * @param user Provided user
     * @return true if exists, false if it does not
     * @throws NoSuchUserException
     * @throws ServerErrorException
     */
    private boolean isUser(User user) throws ServerErrorException {
        try {
            // prepares sql statement
            stmt = con.prepareStatement(LOGIN_CHECK);
            stmt.setString(1, user.getEmail());
            ResultSet rs = stmt.executeQuery(); // gets the user info
            if (rs.next())
                return true; // if user exists

            return false; // if it doesnt exist
        } catch (Exception e) { // if there are any unhandled errors
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerErrorException();
        }
    }

    /**
     * Method that checks if the provided password is correct for the login
     * information provided
     * 
     * @param user Provided user
     * @return User id
     * @throws BadCredentialsException
     * @throws ServerErrorException
     */
    private int checkPassword(User user) throws BadCredentialsException, ServerErrorException {
        try {
            // prepare sql statement
            stmt = con.prepareStatement(PASSWORD_CHECK);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery(); // gets user id
            if (rs.next())
                return rs.getInt("id"); // returns user id if password is correct
            throw new BadCredentialsException(); // if the password is incorrect
        } catch (SQLException e) { // if there are any unhandled exceptions
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerErrorException();
        }
    }

    /**
     * Method that gets all the required user information if the provided user
     * information is correct
     * 
     * @param userId User id
     * @return Built user object
     * @throws ServerErrorException
     */
    private User getUser(int userId) throws ServerErrorException {
        User user = null;
        try {
            user = new User();
            stmt = con.prepareStatement(USER_SELECT); // prepare sql statement
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery(); // get user info
            if (rs.next()) {
                // set all user information
                user.setEmail(rs.getString(1));
                user.setPassword(rs.getString(2));
                user.setFullName(rs.getString(3));
                user.setStreet(rs.getString(4));
                user.setPostalCode(rs.getInt(5));
                user.setCity(rs.getString(6));
                user.setPhone(rs.getString(7));
            }
            doClosing();
            return user;
        } catch (Exception e) { // if there are any unhandled exceptions
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerErrorException();
        }
    }

    /**
     * Method that closes all the necessary objects
     * 
     * @throws ServerErrorException
     */
    private void doClosing() throws ServerErrorException {
        try {
            Pool.returnConnection(con);
            if (stmt != null)
                stmt.close();
            if (cstmt != null)
                cstmt.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());// if there are any execution errors
            throw new ServerErrorException();
        }
    }

    /**
     * Method that handles the insertion of a new user
     * 
     * @param user Not existing user
     */
    private void insertNewUser(User user) throws ServerErrorException {
        try {
            createProcedure();//insert a new procedure

            cstmt = con.prepareCall(INSERT_NEW_USER); // prepare sql statement
            // set all the information
            cstmt.setString(1, user.getEmail());
            cstmt.setString(2, user.getPassword());
            cstmt.setString(3, user.getFullName());
            cstmt.setString(4, user.getStreet());
            cstmt.setInt(5, user.getPostalCode());
            cstmt.setString(6, user.getCity());
            cstmt.setString(7, user.getPhone());
            // execute the call
            cstmt.execute();
        } catch (PSQLException e) {
            // this catch will only be entered if there are any errors with the insertion,
            // namely if the username already exists, which is already handled above so it
            // shouldnt happen
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerErrorException();

        } catch (Exception e) { // if there are any unhandled exceptions
            LOGGER.log(Level.SEVERE, e.getMessage());// if there are any execution errors
            throw new ServerErrorException();
        }

    }

    /**
     * This method is in charge of loading into the database a procedure that
     * handles the insertion of new users
     * 
     * @throws ServerErrorException
     */
    private void createProcedure() throws ServerErrorException {
        try {
            stmt = con.prepareStatement(INSERT_PROCEDURE);
            stmt.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new ServerErrorException();
        }

    }
}
