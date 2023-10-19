package implementations;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        con = Pool.getConnection();
    
        try {
            stmt = con.prepareStatement();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void signUp(User user) throws ServerCapacityException, ServerErrorException {
    }

}
