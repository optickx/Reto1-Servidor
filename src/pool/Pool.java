package pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;

import static server.Server.*;

import exceptions.ServerErrorException;

/**
 * Wrapper for the connection stack
 * 
 * @author Alexander Epelde
 */

public abstract class Pool {
    /**
     * Collection of unused connections
     */
    private static Stack<Connection> stack = new Stack<>();
    /**
     * Config file for the database connection
     */
    private static final ResourceBundle config = ResourceBundle.getBundle("resources.database_access");

    /**
     * Opens a new Connection with the database
     * 
     * @return Connection: New Connection
     */
    private static Connection openConnection() throws ServerErrorException {
        try {
            return DriverManager.getConnection(config.getString("URL"),
                    config.getString("USER"),
                    config.getString("PASSWORD"));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());// if there are any execution errors
            throw new ServerErrorException();
        }
    }

    /**
     * Gets an unused connection from the stack
     * 
     * @return Connection: Open connection
     */
    public static synchronized Connection getConnection() throws ServerErrorException {
        return !stack.empty() ? (Connection) stack.pop() : openConnection();
    }

    /**
     * Returns the connection to the stack after being used
     * 
     * @param con Used connection
     */
    public static synchronized void returnConnection(Connection con) {
        stack.push(con);
    }

    /**
     * When the server is entering closing state, closes all connections and clears
     * the stack
     */
    public static void removeAll() throws ServerErrorException {
        try {
            while (!stack.isEmpty())
                stack.pop().close();

            stack.clear();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());// if there are any execution errors
            throw new ServerErrorException();
        }
    }
}
