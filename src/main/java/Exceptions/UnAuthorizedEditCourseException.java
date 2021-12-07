package Exceptions;

/**
 * this class implements the exception for not having permission to edit the course, if the teacher did not create it
 */
public class UnAuthorizedEditCourseException extends Exception{
    public UnAuthorizedEditCourseException(String message) {
        super(message);
    }
}
