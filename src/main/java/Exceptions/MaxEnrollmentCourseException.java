package Exceptions;

/**
 * this class implements the exception for the limit of enrolled Students for a course
 */
public class MaxEnrollmentCourseException extends Exception{
    public MaxEnrollmentCourseException(String message) {
        super(message);
    }
}
