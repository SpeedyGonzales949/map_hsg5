package Exceptions;

/**
 * this class implements the exception for a teacher that wants to delete a course, which he did not create
 */
public class UnAuthorizedDeleteCourseException extends Exception{
    public UnAuthorizedDeleteCourseException(String message) {
        super(message);
    }
}
