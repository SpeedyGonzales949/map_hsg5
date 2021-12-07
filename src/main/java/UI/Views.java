package UI;

import Model.Course;
import Model.Student;
import Controller.Controller;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * this class implements the view of the output data in the console
 */
public class Views {
    /**
     * thsi method implements the special View for {@link Controller#retrieveStudentsEnrolledForACourse(String)}
     * @param students Students enrolled for the course
     */
    public static void retrieveStudentsEnrolledForACourseView(@NotNull List<Student> students){
        System.out.println("Students:");
        students.forEach(student -> System.out.println(student.getFirstName()+" "+student.getLastName()));
    }

    /**
     * this method implements the special View for {@link Controller#retrieveCoursesWithFreePlaces()}
     * @param courses Courses with free places
     */
    public static void retrieveCoursesWithFreePlacesView(@NotNull List<Course>courses){
        courses.forEach(course -> System.out.println("Name:"+course.getName()+" Free Places:"+ (course.getMaxEnrollment() - course.getStudentsEnrolled().size()) +"/"+course.getMaxEnrollment()));
    }

    /**
     * this method implements the special View for {@link Controller#register(Course, String)}
     * @param courses Courses for regisyering
     */
    public static void registerForCourseView(@NotNull List<Course>courses){
        AtomicReference<Integer> number= new AtomicReference<>(1);
        courses.forEach(course ->System.out.println((number.getAndSet(number.get() + 1))+"."+course.getName()+" Free Places:"+ (course.getMaxEnrollment() - course.getStudentsEnrolled().size())));

    }
}
