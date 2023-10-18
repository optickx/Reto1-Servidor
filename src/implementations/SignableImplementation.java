package implementations;

import exceptions.BadCredentialsException;
import exceptions.NoSuchUserException;
import exceptions.ServerCapacityException;
import exceptions.ServerErrorException;
import interfaces.Signable;
import packets.User;

public class SignableImplementation implements Signable{

    @Override
    public User signIn(User user) throws BadCredentialsException, NoSuchUserException, ServerCapacityException, ServerErrorException {
    return null;
    }

    @Override
    public void signUp(User user) throws ServerCapacityException, ServerErrorException {
    }
    
}
