package Controller;

import Exceptions.MaxEnrollmentCourseException;
import Exceptions.StudentCreditsOverflowException;
import Model.Course;
import Model.Person;
import Model.Student;
import Model.Teacher;
import Repository.CourseJDBCRepo;
import Repository.StudentJDBCRepo;
import Repository.TeacherJDBCRepo;
import org.jetbrains.annotations.NotNull;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * this class handles the data flow
 */
public class RegistrationSystem {
    protected TeacherJDBCRepo teacherJDBCRepo;
    protected StudentJDBCRepo studentJDBCRepo;
    protected CourseJDBCRepo courseJDBCRepo;

    public  RegistrationSystem(Connection connection){
        this.teacherJDBCRepo=new TeacherJDBCRepo(connection);
        this.courseJDBCRepo=new CourseJDBCRepo(connection);
        this.studentJDBCRepo=new StudentJDBCRepo(connection);
    }

    public TeacherJDBCRepo getTeacherJDBCRepo() {
        return teacherJDBCRepo;
    }

    public StudentJDBCRepo getStudentJDBCRepo() {
        return studentJDBCRepo;
    }

    public CourseJDBCRepo getCourseJDBCRepo() {
        return courseJDBCRepo;
    }


    /**
     * register a student to a course
     * @param course , must not be null
     * @param student , must not be null
     * @return true if student was registered to specified course, false if course has no more free places
     * @throws SQLException Error when handling the database
     * @throws StudentCreditsOverflowException Can't register to this course, because the limit of the student credits is exceeded(>30)
     *@throws MaxEnrollmentCourseException Can't register to this course, because there are no more free places
     */
    public boolean register(@NotNull Course course, @NotNull Student student) throws StudentCreditsOverflowException, MaxEnrollmentCourseException,  SQLException {
        if(course.getStudentsEnrolled().size()< course.getMaxEnrollment()){
            if(student.getTotalCredits()+course.getCredits()<=30){

                if(!student.getEnrolledCourses().contains(course.getId())){
                    course.getStudentsEnrolled().add(student.getStudentId());
                    student.getEnrolledCourses().add(course.getId());
                    student.setTotalCredits(student.getTotalCredits()+course.getCredits());
                    this.studentJDBCRepo.update(student);
                    return true;
                }else{
                    return false;
                }
            }else{
                throw new StudentCreditsOverflowException("Courses credits exceeded");
            }

        }else{
            throw new MaxEnrollmentCourseException("Max Enrollment in this Course exceeded");
        }
    }

    /**
     * this method retrieves all Courses with free Place from {@link RegistrationSystem#courseJDBCRepo}
     * @return {@code List<Course>}
     * @throws SQLException Error when handling the database
     */
    public List<Course> retrieveCoursesWithFreePlaces() throws SQLException {
        //(Filter+Sort)-Function
        List<Course> courses=this.courseJDBCRepo.findAll().stream().filter(course -> course.getMaxEnrollment()-course.getStudentsEnrolled().size()>0).collect(Collectors.toList());
        Comparator<Course> comparatorbyPopularity= Comparator.comparing((Course course) -> String.valueOf(course.getStudentsEnrolled().size()));
        courses.sort(comparatorbyPopularity.reversed());
        return courses;
    }

    /**
     * this method retrieves all the students enrolled for a course
     * @param course Course, for whom students are enrolled
     * @return {@code List<Student>}
     * @throws SQLException Error when handling the database
     */
    public List<Student> retrieveStudentsEnrolledForACourse(@NotNull Course course) throws SQLException {
        //(Filter+Sort)-Function
        List<Student> students = this.studentJDBCRepo.findAll().stream().filter(student -> student.getEnrolledCourses().contains(course.getId())).collect(Collectors.toList());
        Comparator<Student> comparatorbyName= Comparator.comparing((Student student) -> (student.getLastName() + student.getFirstName()));
        students.sort(comparatorbyName.reversed());
        return students;
    }

    /**
     * this method adds a new course to {@link RegistrationSystem#courseJDBCRepo}
     * @param course the new created Course
     * @return true, if operation was successful
     * @throws SQLException Error when handling the database
     */
    public boolean addCourse( @NotNull Course course) throws SQLException {
        return this.courseJDBCRepo.save(course) == null;
    }

    /**
     * this method adds a new Student to {@link RegistrationSystem#studentJDBCRepo}
     * @param student new Student
     * @return unique Identifier of the student
     * @throws SQLException Error when handling the database
     */
    public UUID addStudent(@NotNull Student student) throws SQLException {
        if(this.studentJDBCRepo.save(student)==null)
            return student.getStudentId();
        else
                return null;
    }

    /**
     * this method adds a new Teacher to {@link RegistrationSystem#teacherJDBCRepo}
     * @param teacher new Teacher
     * @return unique Identifier of the teacher
     * @throws SQLException Error when handling the database
     */
    public UUID addTeacher(@NotNull Teacher teacher) throws  SQLException {
        if(this.teacherJDBCRepo.save(teacher)==null)
            return teacher.getId();
        else
            return null;
    }

    /**
     * this method searches for a user in {@link RegistrationSystem#studentJDBCRepo} or {@link RegistrationSystem#teacherJDBCRepo}
     * @param id unique identifier of the person
     * @return the person, that was found
     * @throws SQLException Error when handling the database
     */
    public Person findPerson(UUID id) throws SQLException {
        Person person = this.studentJDBCRepo.findOne(id);
        if(person!=null){
            return person;
        }
        person=this.teacherJDBCRepo.findOne(id);
        return person;
    }

    /**
     * this method searches for a course in {@link RegistrationSystem#courseJDBCRepo}
     * @param id unique identifier of the course
     * @return the course,that was found
     * @throws SQLException Error when handling the database
     */
    public Course findCourse(UUID id) throws SQLException {
        return this.courseJDBCRepo.findOne(id);
    }

    /**
     * this method changes the number of credits of a course
     * @param course the course, which details will be changed
     * @param credits the new number of credits
     * @throws StudentCreditsOverflowException Can't change, because students enrolled to this course would exceed their limit
     * @throws SQLException Error when handling the database
     */
    public void changeCredits(@NotNull Course course, int credits) throws StudentCreditsOverflowException, SQLException {
        int difference=course.getCredits()-credits;
        if(difference<0){
            for (UUID id:course.getStudentsEnrolled()){
                Student student=this.studentJDBCRepo.findOne(id);
                if(student.getTotalCredits()-difference>30)
                    throw new StudentCreditsOverflowException("Student maximal credits limit exceeded");
            }
        }
        course.setCredits(credits);
        this.courseJDBCRepo.update(course);
        for (UUID id:course.getStudentsEnrolled()){
            Student student=this.studentJDBCRepo.findOne(id);
            student.setTotalCredits(student.getTotalCredits()-difference);
            this.studentJDBCRepo.update(student);
        }
    }

    /**
     * this method deletes a course from
     * @param course the course, that will be deleted
     * @throws SQLException Error when handling the database
     */
    public void deleteCourse(@NotNull Course course) throws SQLException {
        this.courseJDBCRepo.delete(course.getId());
        for (Student student :this.studentJDBCRepo.findAll()
        ) {
            //student.getEnrolledCourses().remove(course.getId()); we do not need to use this cause enrollment is automatically deleted when course is deleted
            student.setTotalCredits(student.getTotalCredits()-course.getCredits());
            this.studentJDBCRepo.update(student);
        }


    }
}
