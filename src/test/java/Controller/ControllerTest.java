package Controller;

import Exceptions.*;
import Model.Course;
import Model.Student;
import Model.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for {@link Controller}
 */
class ControllerTest {
    private Controller controller;
    private Student student;
    private Student student1;
    private Teacher teacher;
    private Teacher teacher1;
    private Course course;



    @BeforeEach
    void setUp() throws SQLException {
        this.controller=new Controller();
        this.student=new Student("Aleman","Mihnea");
        this.student1=new Student("Aleman1","Mihnea");
        this.teacher=new Teacher("John","Doe");
        this.teacher1=new Teacher("John1","Doe");
        this.controller.getRegistrationSystem().getStudentJDBCRepo().save(student);
        this.controller.getRegistrationSystem().getStudentJDBCRepo().save(student1);
        this.controller.getRegistrationSystem().getTeacherJDBCRepo().save(teacher);
        UUID course_id=UUID.fromString(this.controller.addCourse(teacher.getId().toString(),"BD","30","6"));
        this.course=this.controller.getRegistrationSystem().getCourseJDBCRepo().findOne(course_id);
    }
    @AfterEach
    void tearDown() throws SQLException {
        this.controller.getRegistrationSystem().getStudentJDBCRepo().delete(student.getStudentId());
        this.controller.getRegistrationSystem().getStudentJDBCRepo().delete(student1.getStudentId());
        this.controller.getRegistrationSystem().getCourseJDBCRepo().delete(course.getId());
        this.controller.getRegistrationSystem().getTeacherJDBCRepo().delete(teacher.getId());
        this.controller.getRegistrationSystem().getTeacherJDBCRepo().delete(teacher1.getId());

    }

    /**
     * test for {@link Controller#register(Course, String)}
     */
    @Test
    void register() throws StudentAlreadyEnrolled, SQLException, StudentCreditsOverflowException, MaxEnrollmentCourseException {
        assert this.controller.register(this.course,this.student.getStudentId().toString()).equals(this.course.getId().toString());

        assertThrows(StudentAlreadyEnrolled.class,()-> this.controller.register(this.course,this.student.getStudentId().toString()));

        this.course.setMaxEnrollment(1);
        this.controller.getRegistrationSystem().getCourseJDBCRepo().update(course);
        assertThrows(MaxEnrollmentCourseException.class,()-> this.controller.register(this.course,this.student1.getStudentId().toString()));


        this.course.setMaxEnrollment(3);
        this.controller.getRegistrationSystem().getCourseJDBCRepo().update(course);
        this.student1.setTotalCredits(29);
        this.controller.getRegistrationSystem().getStudentJDBCRepo().update(student1);
        assertThrows(StudentCreditsOverflowException.class,()-> this.controller.register(this.course,this.student1.getStudentId().toString()));
    }



    /**
     * test for {@link Controller#retrieveStudentsEnrolledForACourse(String)}
     */
    @Test
    void retrieveStudentsEnrolledForACourse() throws SQLException {
        List<Student> studentList=new ArrayList<>();
        assertEquals(studentList,this.controller.retrieveStudentsEnrolledForACourse(this.course.getId().toString()));
        try {
            this.controller.register(this.course,student.getStudentId().toString());
        } catch (StudentCreditsOverflowException | StudentAlreadyEnrolled | SQLException | MaxEnrollmentCourseException e) {
            e.printStackTrace();
        }
        studentList.add(this.controller.getRegistrationSystem().getStudentJDBCRepo().findOne(student.getStudentId()));
        assertEquals(studentList,this.controller.retrieveStudentsEnrolledForACourse(this.course.getId().toString()));

    }

    /**
     * test for {@link Controller#deleteCourse(String, String)}
     */
    @Test
    void deleteCourse() throws UnAuthorizedDeleteCourseException, SQLException {
        assertThrows(UnAuthorizedDeleteCourseException.class,()-> this.controller.deleteCourse(this.course.getId().toString(),this.student.getStudentId().toString()));
        this.controller.deleteCourse(this.course.getId().toString(),this.teacher.getId().toString());
        assertNull(this.controller.getRegistrationSystem().getCourseJDBCRepo().findOne(this.course.getId()));
    }

    /**
     * test for {@link Controller#addPerson(String, String, String)}
     */
    @Test
    void addPerson() throws SQLException {
        UUID person_id=this.controller.addPerson("fname","lname","1");
        assertInstanceOf(Student.class,this.controller.getRegistrationSystem().getStudentJDBCRepo().findOne(person_id));

        person_id=this.controller.addPerson("fname","lname","2");
        assertInstanceOf(Teacher.class,this.controller.getRegistrationSystem().getTeacherJDBCRepo().findOne(person_id));
    }

    /**
     * test for {@link Controller#changeCredits(String, String, String)}
     */
    @Test
    void changeCredits() throws UnAuthorizedEditCourseException, StudentCreditsOverflowException, SQLException {
        assertThrows(UnAuthorizedEditCourseException.class,()-> this.controller.changeCredits(this.teacher1.getId().toString(),
                this.course.getId().toString(),
                "5"));
        try {
            assert this.controller.register(this.course,this.student.getStudentId().toString()).equals(this.course.getId().toString());
        } catch (StudentCreditsOverflowException | MaxEnrollmentCourseException | StudentAlreadyEnrolled | SQLException e) {
            e.printStackTrace();
        }
        assertThrows(StudentCreditsOverflowException.class,()-> this.controller.changeCredits(this.teacher.getId().toString(),this.course.getId().toString(),"31"));

        this.controller.changeCredits(this.teacher.getId().toString(),this.course.getId().toString(),"7");
        assertEquals(this.controller.getRegistrationSystem().getCourseJDBCRepo().findOne(course.getId()).getCredits(),7);
    }
}