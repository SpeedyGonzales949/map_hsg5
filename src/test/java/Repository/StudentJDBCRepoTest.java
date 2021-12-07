package Repository;

import DB_handler.JDBConnection;
import Model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for {@link StudentJDBCRepo}
 */
class StudentJDBCRepoTest {
    private StudentJDBCRepo studentJDBCRepo;
    @BeforeEach
    void setUp() {
        Connection connection = JDBConnection.getJDBConnection();
        assert connection != null;
        this.studentJDBCRepo=new StudentJDBCRepo(connection);

    }

    /**
     * test for {@link StudentJDBCRepo#findOne(UUID)}
     * @throws SQLException error when handling the database
     */
    @Test
    void findOne() throws SQLException {
        //check if each student can be found in the database
        assertEquals(
                this.studentJDBCRepo.findAll(),
                this.studentJDBCRepo.findAll()
                        .stream()
                        .map(
                                student -> {
                    try {
                        return this.studentJDBCRepo.findOne(student.getStudentId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                        .collect(Collectors.toList())
        );
    }

    /**
     * test for {@link StudentJDBCRepo#save(Student)} and {@link StudentJDBCRepo#delete(UUID)}
     * @throws SQLException error when handling the database
     */
    @org.junit.jupiter.api.Test
    void save_delete() throws SQLException {
        Student student=new Student("Test1","Test1");
        Student student1= new Student("Test2","Test2");

        assertNull(this.studentJDBCRepo.save(student));//check for unsaved entity
        assertEquals(this.studentJDBCRepo.save(student),student);//check for already saved entity
        assertNull(this.studentJDBCRepo.save(student1));//check for unsaved entity
        assertEquals(this.studentJDBCRepo.save(student1),student1);//check for already saved entity

        assertEquals(this.studentJDBCRepo.delete(student.getStudentId()),student);//check for deleting existing object
        assertEquals(this.studentJDBCRepo.delete(student1.getStudentId()),student1);
        assertNull(this.studentJDBCRepo.delete(UUID.randomUUID()));//check for deleting not existing object

    }

    /**
     * test for {@link StudentJDBCRepo#update(Student)}
     * @throws SQLException error when handling the database
     */
    @org.junit.jupiter.api.Test
    void update() throws SQLException {
        List<Student>students=this.studentJDBCRepo.findAll();
        students.forEach(student -> student.setFirstName("Ghita"));
        students.forEach(student -> {
            try {
                assertNull(this.studentJDBCRepo.update(student));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }
}