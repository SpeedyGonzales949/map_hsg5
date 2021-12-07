package Repository;

import DB_handler.JDBConnection;
import Model.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * test class for {@link TeacherJDBCRepo}
 */
class TeacherJDBCRepoTest {
    private TeacherJDBCRepo teacherJDBCRepo;

    @BeforeEach
    void setUp() {
        Connection connection= JDBConnection.getJDBConnection();
        assert connection != null;
        this.teacherJDBCRepo=new TeacherJDBCRepo(connection);
    }

    /**
     * test for {@link TeacherJDBCRepo#findOne(UUID)}
     * @throws SQLException error when handling the database
     */
    @Test
    void findOne() throws SQLException {
        assertEquals(
                this.teacherJDBCRepo.findAll(),
                this.teacherJDBCRepo.findAll()
                        .stream()
                        .map(
                                teacher -> {
                                    try {
                                        return this.teacherJDBCRepo.findOne(teacher.getId());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                })
                        .collect(Collectors.toList())
        );
    }

    /**
     * test for {@link TeacherJDBCRepo#save(Teacher)} and {@link TeacherJDBCRepo#delete(UUID)}
     * @throws SQLException error when handling the database
     */
    @Test
    void save_delete() throws SQLException {

        Teacher teacher=new Teacher("Test1","Test1");
        Teacher teacher1= new Teacher("Test2","Test2");

        assertNull(this.teacherJDBCRepo.save(teacher));//check for unsaved entity
        assertEquals(this.teacherJDBCRepo.save(teacher),teacher);//check for already saved entity
        assertNull(this.teacherJDBCRepo.save(teacher1));//check for unsaved entity
        assertEquals(this.teacherJDBCRepo.save(teacher1),teacher1);//check for already saved entity

        assertEquals(this.teacherJDBCRepo.delete(teacher.getId()),teacher);//check for deleting existing object
        assertEquals(this.teacherJDBCRepo.delete(teacher1.getId()),teacher1);
        assertNull(this.teacherJDBCRepo.delete(UUID.randomUUID()));//check for deleting not existing object
    }

    /**
     * test for {@link TeacherJDBCRepo#update(Teacher)}
     * @throws SQLException
     */
    @Test
    void update() throws SQLException {
        List<Teacher> teachers=this.teacherJDBCRepo.findAll();
        teachers.forEach(teacher -> teacher.setFirstName("Ghita"));
        teachers.forEach(teacher -> {
            try {
                assertNull(this.teacherJDBCRepo.update(teacher));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}