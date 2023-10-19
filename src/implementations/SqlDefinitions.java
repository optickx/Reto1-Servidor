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
     * after verifying a users identity
     */
    public static final String COMPLETE_USER_SELECT = "SELECT ru.login, ru.password, rp.name, rp.street, rcs.name as state, rp.zip, rp.city, rc.name as country, rp.phone "
            + "JOIN public.res_users ru on ru.partner_id = rp.id "
            + "JOIN public.res_country rc on rp.country_id = rc.id "
            + "JOIN public.res_country_state rcs on rp.state_id = rcs.id " + "WHERE ru.id =?";

    /**
     * Selects user id from an existing user.
     * Intended use: to know if the users' password is correct
     */
    public static final String USER_ID_SELECT = "SELECT id FROM public.res_users"
            + "WHERE login = ? and password = ?";

    /**
     * Selects user id from received user.
     * Intended use: to check if the user exists
     */
    public static final String USER_SELECT = "SELECT id FROM public.res_users"
            + "WHERE login = ?";
}
