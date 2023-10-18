package factories;

import implementations.SignableImplementation;
import interfaces.Signable;

public abstract class SignableFactory { 
    public static Signable getSignable(){
        return new SignableImplementation();
    }
}
