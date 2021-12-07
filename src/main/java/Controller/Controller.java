package Controller;

import DB_handler.JDBConnection;
import Exceptions.*;
import Model.Course;
import Model.Person;
import Model.Student;
import Model.Teacher;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * this class handles the data flow
 */
public class Controller {
    private final RegistrationSystem registrationSystem;


    public Controller() {
        this.registrationSystem = new RegistrationSystem(JDBConnection.getJDBConnection());
    }

    public RegistrationSystem getRegistrationSystem() {
        return registrationSystem;
    }

    /**
     * this method registers a student to a course
     * @param course course, student will be added
     * @param student_id unique identifier for student
     * @return the unique identifier for course
     * @throws StudentCreditsOverflowException Can't register to this course, because the limit of the student credits is exceeded(>30)
     * @throws MaxEnrollmentCourseException Can't register to this course, because there are no more free places
     * @throws StudentAlreadyEnrolled Can't register to this course, because student is already enrolled
     * @throws SQLException Input or Output error when using a file
     *
     */
    public String register(Course course, String student_id) throws StudentCreditsOverflowException, MaxEnrollmentCourseException, StudentAlreadyEnrolled, SQLException {
        boolean check=registrationSystem.register(course, (Student) registrationSystem.findPerson(UUID.fromString(student_id)));
        if (!check)
            throw new StudentAlreadyEnrolled("Student is already enrolled to this course");
        return course.getId().toString();
    }

    /**
     * this method retrieves a list with all courses that are not full
     * @return {@code List<Course>}
     * @throws SQLException Input or Output error when using a file
     */
    public List<Course> retrieveCoursesWithFreePlaces() throws SQLException {
        return registrationSystem.retrieveCoursesWithFreePlaces();
    }

    /**
     * this method retrieves a list with all students enrolled for a course
     * @param id unique identifier for the course
     * @return {@code List<Student>}
     * @throws SQLException Input or Output error when using a file
     */
    public List<Student> retrieveStudentsEnrolledForACourse(String id) throws SQLException {
        Course course=registrationSystem.findCourse(UUID.fromString(id));
        return registrationSystem.retrieveStudentsEnrolledForACourse(course);
    }

    /**
     * this method retrieves a list with all courses from courseRepo {@link RegistrationSystem#courseJDBCRepo}
     * @return {@code List<Course>}
     * @throws SQLException Input or Output error when using a file
     */
    public List<Course> getAllCourses() throws SQLException {
        return registrationSystem.getCourseJDBCRepo().findAll();
    }

    /**
     * this method creates a new course to  {@link RegistrationSystem#courseJDBCRepo}
     * @param teacher_id unique identifier for a teacher
     * @param name name of the course
     * @param max_enrollment max number of students that can enroll to the course
     * @param credits credits of the course
     * @return String - unique identifier of the new course
     * @throws SQLException Input or Output error when using a file
     */
    public String addCourse(String teacher_id,String name,String max_enrollment,String credits) throws  SQLException {
        Course course=new Course(name,UUID.fromString(teacher_id),Integer.parseInt(max_enrollment),Integer.parseInt(credits));
        boolean check=registrationSystem.addCourse(course);
        if(check)
            return course.getId().toString();
        return null;
    }

    /**
     * this method deletes a course from {@link RegistrationSystem#courseJDBCRepo}
     * @param course_id unique identifier of a course
     * @param teacher_id unique identifier of a teacher
     * @throws UnAuthorizedDeleteCourseException  Permission denied, because teacher did not create this course
     * @throws SQLException Input or Output error when using a file
     */
    public void deleteCourse(String course_id,String teacher_id) throws UnAuthorizedDeleteCourseException, SQLException {
        Course course=this.registrationSystem.findCourse(UUID.fromString(course_id));
        if(course.getTeacher().equals(UUID.fromString(teacher_id)))
            this.registrationSystem.deleteCourse(course);
        else
            throw new UnAuthorizedDeleteCourseException("You don't have permission to delete this course");
    }

    /**
     * this method adds a new Person to the Repo {@link RegistrationSystem#studentJDBCRepo,RegistrationSystem#teacherJDBCRepo}
     * @param fname first name of the person
     * @param lname last name of the person
     * @param option SignUp as Teacher or Student
     * @return UUID the unique identifier of the new person
     * @throws SQLException Input or Output error when using a file
     */
    public UUID addPerson(String fname, String lname, @NotNull String option) throws SQLException {
        if(option.equals("1")){
            Student student=new Student(fname,lname);
            return registrationSystem.addStudent(student);
        }else if(option.equals("2")){
            Teacher teacher=new Teacher(fname,lname);
            return registrationSystem.addTeacher(teacher);
        }
        return null;
    }

    /**
     * this method checks if the unique Identifier of the user exists(when logging in)
     * @param id the unique identifier of a person
     * @return String the name of the person and their status (Teacher or Student)
     * @throws SQLException Input or Output error when using a file
     */
    public String findPerson(String id) throws SQLException {
        Person person=registrationSystem.findPerson(UUID.fromString(id));

        if(person instanceof Student)
            return "S_"+person.getFirstName();
        if(person instanceof Teacher)
            return "T_"+person.getFirstName();

        return null;
    }

    /**
     * this method changes umber of credits of a course
     * @param teacher_id unique identifier for teacher
     * @param course_id unique identifier for  course
     * @param credits the new number of credits
     * @throws StudentCreditsOverflowException Can't change, because students enrolled to this course would exceed their limit
     * @throws UnAuthorizedEditCourseException Can't change, because teacher did not create this course
     * @throws SQLException Input or Output error when using a file
     */
    public void changeCredits(String teacher_id,String course_id,String credits) throws StudentCreditsOverflowException, UnAuthorizedEditCourseException, SQLException {
        Course course=this.registrationSystem.findCourse(UUID.fromString(course_id));
        if(course.getTeacher().equals(UUID.fromString(teacher_id)))
            this.registrationSystem.changeCredits(course,Integer.parseInt(credits));
        else
            throw new UnAuthorizedEditCourseException("You don't have the permission to change this Course");
    }



}
