package Repository;


import Model.Student;
import org.jetbrains.annotations.NotNull;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * this class implements the logic for StudentRepository
 */
public  class StudentJDBCRepo implements Repository.CrudRepo<Student> {
    private PreparedStatement preparedStatement;
    private final Connection connection;

    public StudentJDBCRepo(@NotNull Connection connection) {
        this.connection=connection;
    }

    /**
     * this method searches for Student in the list
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Student findOne(@NotNull UUID id) throws SQLException {
            ResultSet resultSet;
            this.preparedStatement=this.connection.prepareStatement("Select * from students "
                    +"inner join students_enrolled on students_enrolled.idstudent=students.id "
                    +"inner join courses on courses.id=students_enrolled.idcourse "
                    +"where students.studentid=? ");
            this.preparedStatement.setString(1,id.toString());
            resultSet=this.preparedStatement.executeQuery(); //search if the student is enrolled to some courses

            List<UUID> enrolled_courses=new ArrayList<>();
            while(resultSet.next()){
                enrolled_courses.add(UUID.fromString(resultSet.getString("courseid")));
            }

            this.preparedStatement=this.connection.prepareStatement("SELECT * from students where students.studentid=?");
            this.preparedStatement.setString(1,id.toString());
            resultSet=this.preparedStatement.executeQuery();

            if(!resultSet.next()) //check if student exists
                return null;

            return new Student(resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    UUID.fromString(resultSet.getString("studentid")),
                    resultSet.getInt("totalcredits"),
                    enrolled_courses);

    }

    /**
     * retrieves all the data from the database
     * {@inheritDoc}
     * @throws SQLException error when handling database
     * @return
     */
    @Override
    public List<Student> findAll() throws SQLException {

        List<Student> students=new ArrayList<>();
        ResultSet resultSet;
        this.preparedStatement=this.connection.prepareStatement("Select * from students");
        resultSet=this.preparedStatement.executeQuery();

        while(resultSet.next()){//loop for retrieving all the Students from the database

            String id=resultSet.getString("studentid");
            this.preparedStatement=this.connection.prepareStatement("Select * from students "
                    +"inner join students_enrolled on students_enrolled.idstudent=students.id "
                    +"inner join courses on courses.id=students_enrolled.idcourse "
                    +"where students.studentid=? ");
            this.preparedStatement.setString(1,id);
            ResultSet resultSet1=this.preparedStatement.executeQuery();//get all courses for each student

            List<UUID> enrolled_courses=new ArrayList<>();
            while(resultSet1.next())
                {
                enrolled_courses.add(UUID.fromString(resultSet1.getString("courseid")));
                }
            students.add(
                    new Student(resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            UUID.fromString(resultSet.getString("studentid")),
                            resultSet.getInt("totalcredits"),
                            enrolled_courses)
            );
        }
        return students;

    }

    /**
     * this method save a Student to the database
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public Student save(@NotNull Student entity) throws SQLException {

            this.preparedStatement=this.connection.prepareStatement("Select * from students where studentid=?");
            this.preparedStatement.setString(1,entity.getStudentId().toString());
            ResultSet resultSet=this.preparedStatement.executeQuery();
            if(resultSet.next())//if entity already exists
                return entity;

            this.preparedStatement=this.connection.prepareStatement("insert into students(firstname,lastname,studentid) values(?,?,?)");
            this.preparedStatement.setString(1,entity.getFirstName());
            this.preparedStatement.setString(2,entity.getLastName());
            this.preparedStatement.setString(3,entity.getStudentId().toString());
            this.preparedStatement.execute();
            return null;//entity was saved to database

    }
    /**
     * this method deletes a student from the database
     * {@inheritDoc}
     * @throws SQLException error when handling database
     */
    @Override
    public  Student delete(@NotNull UUID id) throws SQLException {

            Student student=this.findOne(id);
            if(student!=null){//check if entity exists
                this.preparedStatement=this.connection.prepareStatement("delete  from students_enrolled where students_enrolled.idstudent=(Select id from students where studentid=?)");
                this.preparedStatement.setString(1,id.toString());
                this.preparedStatement.execute();

                this.preparedStatement=this.connection.prepareStatement("delete from students where students.studentid=?");
                this.preparedStatement.setString(1,id.toString());
                this.preparedStatement.execute();
                return student;
            }
            return null;
    }

    /**
     * this method updates a student-credentials
     * @throws SQLException error when handling database
     * {@inheritDoc}
     */
    @Override
    public  Student update(@NotNull Student entity) throws SQLException {
        int idstudent;//primary key
        this.preparedStatement=this.connection.prepareStatement("select * from students where students.studentid=?");
        this.preparedStatement.setString(1,entity.getStudentId().toString());
        ResultSet resultSet=this.preparedStatement.executeQuery();
        if(!resultSet.next())//check if student exists
            return entity;
        idstudent=resultSet.getInt("id");

        this.preparedStatement=this.connection.prepareStatement("delete from students_enrolled where" +
                " students_enrolled.idstudent=?");
        this.preparedStatement.setInt(1,idstudent);//delete all current enrollment for this specific student
        this.preparedStatement.execute();

        entity.getEnrolledCourses().forEach(course->{
            int idcourse;//primary key for each course
            try {
                this.preparedStatement=this.connection.prepareStatement("SELECT * from courses where courses.courseid=?");
                this.preparedStatement.setString(1,course.toString());
                ResultSet resultSet1=this.preparedStatement.executeQuery();
                resultSet1.next();
                idcourse=resultSet1.getInt("id");
                this.preparedStatement=this.connection.prepareStatement("insert into students_enrolled values(?,?)");
                this.preparedStatement.setInt(1,idcourse);
                this.preparedStatement.setInt(2,idstudent);
                this.preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.preparedStatement=this.connection.prepareStatement("Update students "
        +"set students.firstname=?,students.lastname=?, students.totalcredits=? " +
                "where students.studentid=?");
        this.preparedStatement.setString(1,entity.getFirstName());
        this.preparedStatement.setString(2,entity.getLastName());
        this.preparedStatement.setInt(3,entity.getTotalCredits());
        this.preparedStatement.setString(4,entity.getStudentId().toString());
        this.preparedStatement.execute();
        return null;
    }







}
