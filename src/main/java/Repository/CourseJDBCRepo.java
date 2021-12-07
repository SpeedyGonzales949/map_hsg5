package Repository;

import Model.Course;
import org.jetbrains.annotations.NotNull;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * this class implements the logic for Course Repository
 */
public class CourseJDBCRepo implements CrudRepo<Course>{
    private PreparedStatement preparedStatement;
    private final Connection connection;

    public CourseJDBCRepo(Connection connection) {
        this.connection = connection;
    }

    /**
     * this method searches for Course in the list
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Course findOne(@NotNull UUID id) throws SQLException {
        ResultSet resultSet;
        this.preparedStatement=this.connection.prepareStatement(("Select * from courses"
        +" inner join students_enrolled se on courses.id = se.idcourse"
        +" inner join students s on se.idstudent = s.id"
        +" where courses.courseid=?"));
        this.preparedStatement.setString(1,id.toString());
        resultSet=this.preparedStatement.executeQuery();//search if there are students enrolled for this course
        List<UUID>enrolled_students=new ArrayList<>();
        while(resultSet.next()){
            enrolled_students.add(UUID.fromString(resultSet.getString("studentid")));
        }
        this.preparedStatement=this.connection.prepareStatement("SELECT * from courses"
                +" inner join teachers t on courses.idteacher = t.id"
                +" where courseid=?");
        this.preparedStatement.setString(1,id.toString());
        resultSet=this.preparedStatement.executeQuery();

        if(!resultSet.next())
            return null;
        return new Course(resultSet.getString("name"),
                UUID.fromString(resultSet.getString("teacherid")),
                Integer.parseInt(resultSet.getString("maxenrollment")),
                Integer.parseInt(resultSet.getString("credits")),
                enrolled_students,
                id
                );
    }

    /**
     * retrieves all the data from the database
     * {@inheritDoc}
     * @throws SQLException error when handling database
     * @return
     */
    @Override
    public List<Course> findAll() throws SQLException {
        List<Course>courses=new ArrayList<>();
        ResultSet resultSet;
        this.preparedStatement=this.connection.prepareStatement("Select * from courses"
        +" inner join teachers t on courses.idteacher = t.id");
        resultSet=this.preparedStatement.executeQuery();
        while(resultSet.next()){//loop for retrieving all the Courses from the database

            String id=resultSet.getString("courseid");

            this.preparedStatement=this.connection.prepareStatement("Select * from courses"
            +" inner join students_enrolled se on courses.id = se.idcourse"
            +" inner join students s on se.idstudent = s.id"
            +" where courses.courseid=?");
            this.preparedStatement.setString(1,id);
            ResultSet resultSet1=this.preparedStatement.executeQuery();//get all student enrolled for each course

            List<UUID>enrolled_students=new ArrayList<>();
            while(resultSet1.next()){
                enrolled_students.add(UUID.fromString(resultSet1.getString("studentid")));
            }
            courses.add(
                    new Course(resultSet.getString("name"),
                            UUID.fromString(resultSet.getString("teacherid")),
                            Integer.parseInt(resultSet.getString("maxenrollment")),
                            Integer.parseInt(resultSet.getString("credits")),
                            enrolled_students,
                            UUID.fromString(id))
            );
        }
        return courses;
    }

    /**
     * this method save a Course to the database
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Course save(@NotNull Course entity) throws SQLException {
        this.preparedStatement=this.connection.prepareStatement("SELECT  * from courses"
        +" where courses.courseid=?");
        this.preparedStatement.setString(1,entity.getId().toString());
        ResultSet resultSet=this.preparedStatement.executeQuery();
        if(resultSet.next())//entity already exists
            return entity;

        int  idteacher;
        this.preparedStatement= this.connection.prepareStatement("SELECT * from teachers"
                +" where teacherid=?");
        this.preparedStatement.setString(1,entity.getTeacher().toString());
        resultSet=this.preparedStatement.executeQuery();
        resultSet.next();
        idteacher=resultSet.getInt("id");//Foreign Key id for the database

        this.preparedStatement=this.connection.prepareStatement("insert into courses(name, idteacher, maxenrollment, credits, courseid) values(?,?,?,?,?) ");
        this.preparedStatement.setString(1,entity.getName());
        this.preparedStatement.setInt(2,idteacher);
        this.preparedStatement.setInt(3,entity.getMaxEnrollment());
        this.preparedStatement.setInt(4,entity.getCredits());
        this.preparedStatement.setString(5,entity.getId().toString());
        this.preparedStatement.execute();
        return null;//entity was saved to database

    }

    /**
     * this method deletes a course from the database
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Course delete(UUID id) throws SQLException {
        Course course=this.findOne(id);
        if(course!=null){//check if entity exists

            this.preparedStatement=this.connection.prepareStatement("delete from students_enrolled " +
                    "where idcourse=(SELECT id from courses where courseid=? )");//delete all entries for enrolled students
            this.preparedStatement.setString(1,id.toString());
            this.preparedStatement.execute();

            this.preparedStatement=this.connection.prepareStatement("delete from courses where courseid=?");
            this.preparedStatement.setString(1,id.toString());
            this.preparedStatement.execute();


            return course;

        }
        return null;
    }

    /**
     * this method updates a course-credentials
     * @throws SQLException error when handling database
     * {@inheritDoc}
     */
    @Override
    public Course update(@NotNull Course entity) throws SQLException {
        int idteacher;
        this.preparedStatement=this.connection.prepareStatement("Select * from teachers where teacherid=?");
        this.preparedStatement.setString(1,entity.getTeacher().toString());
        ResultSet resultSet=this.preparedStatement.executeQuery();
        resultSet.next();
        idteacher=resultSet.getInt("id");//get the primary key of the teacher

        this.preparedStatement=this.connection.prepareStatement("Update courses"
        +" set name=?,idteacher=?,maxenrollment=?,credits=? where courseid=?");
        this.preparedStatement.setString(1,entity.getName());
        this.preparedStatement.setInt(2,idteacher);
        this.preparedStatement.setInt(3,entity.getMaxEnrollment());
        this.preparedStatement.setInt(4,entity.getCredits());
        this.preparedStatement.setString(5,entity.getId().toString());
        if(this.preparedStatement.executeUpdate()!=0)
            return null;
        else
            return entity;
    }
}
