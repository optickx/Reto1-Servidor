package pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;

import exceptions.ServerErrorException;

/**
 * 
 * @author
 */

public abstract class Pool {

    private static Stack<Connection> stack = new Stack<>();
    private static Connection con;
    private static final ResourceBundle config = ResourceBundle.getBundle("resources.database_access");

    /**
     * @return the connection with the selected database
     */
    public static Connection openConnection() throws ServerErrorException {
        con = null;
        try {
            con = DriverManager
                    .getConnection(config.getString("URL"),
                            config.getString("USER"),
                            config.getString("PASSWORD"));

            stack.add(con);
        } catch (SQLException ex) {
            throw new ServerErrorException("Error creating a new connection");
        }
        return con;
    }

    /**
     *
     * @return if the stack is empty it gives a new connection, else the stack
     *         returns a connection
     */
    public static Connection getConnection() throws ServerErrorException{
        return !stack.empty() ? (Connection) stack.pop() : openConnection();
    }

    public static void returnConnection(Connection con) {
        stack.push(con);
    }

    /**
     * When the server is closed, it gets all the connectios from the stack and
     * closes them
     */
    public static void removeAll() throws ServerErrorException {
        while (!stack.isEmpty()) {
            con = (Connection) stack.pop();
            try {
                con.close();
            } catch (SQLException ex) {
                throw new ServerErrorException("Error closing connections to the database");
            }
        }
        stack.clear();
    }

}
