package implementations;

/**
 * This class contains all the sql actions required for the implementations to
 * work properly and to reduce bloat from that class
 * 
 * @author Alexander Epelde
 */
public abstract class SqlDefinitions {
        /**
         * Selects everything you need to know about a said user, only able to be used
         * after verifying a users identity with both LOGIN_CHECK and PASSWORD_CHECK in
         * that order
         */
        public static final String USER_SELECT = "SELECT ru.login, ru.password, rp.name, rp.street, rp.zip, rp.city, rp.phone "
                        + "FROM public.res_partner rp "
                        + "JOIN public.res_users ru on ru.partner_id = rp.id " 
                        + "WHERE ru.id = ?";
;

        /**
         * Selects user id from an existing user.
         * Intended use: to know if the users' password is correct
         */
        public static final String PASSWORD_CHECK = "SELECT id FROM public.res_users "
                        + "WHERE login = ? and password = ?";

        /**
         * Selects user id from received user.
         * Intended use: to check if the user exists
         */
        public static final String LOGIN_CHECK = "SELECT id FROM public.res_users "
                        + "WHERE login = ?";

        /**
         * Calls a procedure that handles the insertion of a new user
         * The fields to fill are in the same order as the User class
         */
        public static final String INSERT_NEW_USER = "CALL insert_new_user(?,?,?,?,?,?,?)";
}
