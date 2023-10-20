package implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import exceptions.BadCredentialsException;
import exceptions.NoSuchUserException;
import exceptions.ServerCapacityException;
import exceptions.ServerErrorException;
import interfaces.Signable;
import packets.User;
import pool.Pool;

import static implementations.SqlDefinitions.*;

public class SignableImplementation implements Signable {
    private Connection con;
    private PreparedStatement stmt;

    @Override
    public User signIn(User user)
            throws BadCredentialsException, NoSuchUserException, ServerCapacityException, ServerErrorException {
        User newUser = user;
        con = Pool.getConnection();
        if (userExists(user))
            newUser = getUser(checkPassword(user));

        return newUser;
    }

    @Override
    public void signUp(User user) throws ServerCapacityException, ServerErrorException {
    }

    private boolean userExists(User user) throws NoSuchUserException, ServerErrorException {
        try {
            stmt = con.prepareStatement(LOGIN_CHECK);
            stmt.setString(1, user.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return true;
            else
                throw new NoSuchUserException("User does not exist");
        } catch (SQLException e) {
            throw new ServerErrorException("Error checking login mail");
        }
    }

    private int checkPassword(User user) throws BadCredentialsException, ServerErrorException {
        try {
            stmt = con.prepareStatement(PASSWORD_CHECK);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("id");
            else
                throw new BadCredentialsException("Incorrect password");
        } catch (SQLException e) {
            throw new ServerErrorException("Error checking for password");
        }
    }

    private User getUser(int userId) throws ServerErrorException {
        User user = new User();
        try {
            stmt = con.prepareStatement(USER_SELECT);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setEmail(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("name"));
                user.setStreet(rs.getString("street"));
                user.setPostalCode(rs.getInt("zip"));
                user.setCity(rs.getString("city"));
                user.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            throw new ServerErrorException("Error getting user's information");
        }
        return user;
    }
}
