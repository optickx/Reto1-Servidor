package factories;

import implementations.SignableImplementation;
import interfaces.Signable;

/**
 * Class that is in charge of handing Signable implementations
 * 
 * @author Alexander Epelde
 */
public abstract class SignableFactory {
    /**
     * Method that returns new instanced objects of the Signable interface
     * 
     * @return Signable implementation
     */
    public static Signable getSignable() {
        return new SignableImplementation();
    }
}
