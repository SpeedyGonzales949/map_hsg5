package Repository;

import DB_handler.JDBConnection;
import Model.Course;
import Model.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test for class for {@link CourseJDBCRepo}
 */
class CourseJDBCRepoTest {
    private CourseJDBCRepo courseJDBCRepo;
    private Teacher teacher;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        this.connection = JDBConnection.getJDBConnection();
        assert this.connection != null;
        this.courseJDBCRepo=new CourseJDBCRepo(connection);
        this.teacher=new Teacher("TestTeacherFName","TestTeacherLName");
        new TeacherJDBCRepo(connection).save(teacher);
    }

    @AfterEach
    void tearDown() throws SQLException {
        new TeacherJDBCRepo(this.connection).delete(teacher.getId());
    }

    /**
     * test if each existent course can be found
     * test for {@link CourseJDBCRepo#findOne(UUID)}
     * @throws SQLException error when handling the database
     */
    @Test
    void findOne() throws SQLException {
        assertEquals(
                this.courseJDBCRepo.findAll(),
                this.courseJDBCRepo.findAll()
                        .stream()
                        .map(
                                course -> {
                                    try {
                                        return this.courseJDBCRepo.findOne(course.getId());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                })
                        .collect(Collectors.toList())
        );
    }

    /**
     * test for {@link CourseJDBCRepo#save(Course)} and {@link CourseJDBCRepo#delete(UUID)}
     * @throws SQLException error when handling the database
     */
    @Test
    void save_delete() throws SQLException {

        Course course=new Course("Test1",this.teacher.getId(),30,6);

        assertNull(this.courseJDBCRepo.save(course));//check for unsaved entity
        assertEquals(this.courseJDBCRepo.save(course),course);//check for already saved entity

        assertEquals(this.courseJDBCRepo.delete(course.getId()),course);
        assertNull(this.courseJDBCRepo.delete(UUID.randomUUID()));//check for deleting not existing object
    }

    /**
     * test for {@link CourseJDBCRepo#update(Course)}
     * @throws SQLException error when handling the database
     */
    @Test
    void update() throws SQLException {
        List<Course> courses=this.courseJDBCRepo.findAll();
        courses.forEach(course -> course.setName("TestName"));
        courses.forEach(course -> {
            try {
                assertNull(this.courseJDBCRepo.update(course));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Course course=new Course("Test1",this.teacher.getId(),30,6);
        assertEquals(course,this.courseJDBCRepo.update(course));

    }
}