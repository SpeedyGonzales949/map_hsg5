package Repository;


import Model.Teacher;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * this class implements the logic for the repository for model Teacher
 */
public class TeacherJDBCRepo implements CrudRepo<Teacher> {

    private PreparedStatement preparedStatement;
    private final Connection connection;

    public TeacherJDBCRepo(Connection connection) {
        this.connection = connection;
    }

    /**
     * this method searches for Teacher in the list
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Teacher findOne(@NotNull UUID id) throws SQLException {

        ResultSet resultSet;
        this.preparedStatement=this.connection.prepareStatement("Select * from courses " +"where courses.idteacher=?");//retrieve all courses that the teacher created
        this.preparedStatement.setString(1,id.toString());
        resultSet=this.preparedStatement.executeQuery();
        List<UUID> teaching_courses=new ArrayList<>();
        while(resultSet.next()){
            teaching_courses.add(UUID.fromString(resultSet.getString("courseid")));
            }
        this.preparedStatement=this.connection.prepareStatement("SELECT * from teachers where teachers.teacherid=?");
        this.preparedStatement.setString(1,id.toString());
        resultSet=this.preparedStatement.executeQuery();

        if(!resultSet.next())//check if teacher exists
            return null;

        return new Teacher(resultSet.getString("firstname"),
                                resultSet.getString("lastname"),
                                teaching_courses,
                                UUID.fromString(resultSet.getString("teacherid")));


    }

    /**
     * retrieves all the data in the repository
     * {@inheritDoc}
     * @throws SQLException error when handling database
     * @return
     */
    @Override
    public List<Teacher> findAll() throws SQLException {
        List<Teacher>teachers=new ArrayList<>();
        ResultSet resultSet;
        this.preparedStatement=this.connection.prepareStatement("Select * from teachers");
        resultSet=this.preparedStatement.executeQuery();

        while(resultSet.next()){
            String id=resultSet.getString("teacherid");
            this.preparedStatement=this.connection.prepareStatement("Select * from courses"
            +" where courses.idteacher=?");
            this.preparedStatement.setString(1,id);
            ResultSet resultSet1=this.preparedStatement.executeQuery();

            List<UUID>teaching_courses=new ArrayList<>();
            while(resultSet1.next()){
                teaching_courses.add(UUID.fromString(resultSet1.getString("courseid")));
            }
            teachers.add(
                    new Teacher(resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        teaching_courses,
                        UUID.fromString(resultSet.getString("teacherid")))
            );
        }
        return teachers;
    }

    /**
     * this method save a Student to the list
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Teacher save(@NotNull Teacher entity) throws SQLException {
        this.preparedStatement=this.connection.prepareStatement("Select * from teachers"
        +" where teacherid=?");
        this.preparedStatement.setString(1,entity.getId().toString());
        ResultSet resultSet=this.preparedStatement.executeQuery();
        if(resultSet.next())//entity already exists
            return entity;

        this.preparedStatement=this.connection.prepareStatement("insert into teachers(teachers.firstname,teachers.lastname,teachers.teacherid) values(?,?,?)");
        this.preparedStatement.setString(1,entity.getFirstName());
        this.preparedStatement.setString(2,entity.getLastName());
        this.preparedStatement.setString(3,entity.getId().toString());
        this.preparedStatement.execute();
        return null;//entity was saved to database
    }

    /**
     * this method deletes a student from the list
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Teacher delete(UUID id) throws SQLException {
        Teacher teacher=this.findOne(id);
        if(teacher!=null){//check if entity exists
            this.preparedStatement=this.connection.prepareStatement("delete from teachers where teachers.teacherid=?");
            this.preparedStatement.setString(1,id.toString());
            this.preparedStatement.execute();
            return teacher;
        }

        return null;
    }

    @Override
    public Teacher update(@NotNull Teacher entity) throws SQLException {
        this.preparedStatement=this.connection.prepareStatement("Update teachers "
                +"set teachers.firstname=?,teachers.lastname=? where teachers.teacherId=?");
        this.preparedStatement.setString(1,entity.getFirstName());
        this.preparedStatement.setString(2,entity.getLastName());
        this.preparedStatement.setString(3,entity.getId().toString());

        if(this.preparedStatement.executeUpdate()!=0)//check if teacher credentials were updated
            return null;
        else
            return entity;

    }
}
