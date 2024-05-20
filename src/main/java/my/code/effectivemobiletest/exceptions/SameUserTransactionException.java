package my.code.effectivemobiletest.exceptions;

public class SameUserTransactionException extends RuntimeException{
    public SameUserTransactionException(String message){
        super(message);
    }
}
