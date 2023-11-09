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
                        + "WHERE ru.id = ?";;

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

        /**
         * Creates a procedure to handle the insertion of new users, throwing exceptions
         * if there are any errors and automatically handling transactional integrity.
         * See <a href="file:../resources/insertProcedure.sql"> the non obfuscated
         * insert procedure</a>
         */
        public static final String INSERT_PROCEDURE = "CREATE OR REPLACE PROCEDURE insert_new_user(IN login VARCHAR(255), IN passwd VARCHAR(255), IN fullName VARCHAR(255), IN street VARCHAR(255), IN zip INT, IN city VARCHAR(255), IN phone VARCHAR(12)) AS $$ DECLARE partner_id INT; user_id INT; BEGIN INSERT INTO public.res_partner (name, type, street, zip, city, email, phone, active, is_company, partner_share) VALUES (fullName, 'contact', street, zip, city, login, phone, true, false, true) RETURNING id INTO partner_id; INSERT INTO public.res_users (company_id, partner_id, login, password) VALUES (2, partner_id, login, passwd) RETURNING id INTO user_id; INSERT INTO public.res_company_users_rel (cid, user_id) VALUES (2, user_id); EXCEPTION WHEN OTHERS THEN ROLLBACK; RAISE EXCEPTION 'An error occurred: %', SQLERRM; END; $$ LANGUAGE plpgsql";

}
