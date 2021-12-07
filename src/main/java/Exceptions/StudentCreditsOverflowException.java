package Exceptions;

/**
 * this class implements the exception for the limit of available Credits for each Student
 */
public class StudentCreditsOverflowException extends Exception{
    public StudentCreditsOverflowException(String message) {
        super(message);
    }
}
