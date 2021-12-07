package UI;

import Controller.Controller;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * <h1>University</h1>
 * <p>This program implements a console  application that mimics the data flow in a University</p>
 * @author Aleman Mihnea Ioan
 * @version 2.0.0
 * @since 2021-12-05
 */
@SuppressWarnings("ThrowablePrintedToSystemOut")
public class UI {
    private final Controller controller;
    private String uniqueId;//unique identifier of the user
    private boolean flag=true;//true,while application is running, else false

    public UI() {
        this.controller = new Controller();
    }

    /**
     * this method starts the console application
     */
    public void start() throws SQLException {

        Scanner scanner=new Scanner(System.in);//scanner for reading input user
        String option;//user option
        while(this.flag){
            System.out.print("Press 1 for SignUp, 2 for LogIn, 3 for close app:");
            option = scanner.nextLine();
            switch (option){
                case "1":{
                    this.signup(scanner);
                    break;

                }
                case "2":{
                    this.login(scanner);
                    break;
                }
                case "3":{
                    scanner.close();
                    this.close();

                }
                default:break;
            }

        }


    }

    /**
     * this method implements the signup-logic for new user
     * @param scanner Scanner for reading input
     */
    public void signup(@NotNull Scanner scanner){
        System.out.println("------------------------");
        String fname,lname,option;
        System.out.println("Enter credentials:");

        System.out.print("Firstname:");
        fname=scanner.nextLine();

        System.out.print("Lastname:");
        lname=scanner.nextLine();

        System.out.print("Register for 1. Student, Register for 2. Teacher:");
        option=scanner.nextLine();

        System.out.println("Shortly you will receive your uniqueId.If uniqueId is null, error occurred.");
        System.out.println("Waiting for registering...");
        System.out.print("Your uniqueId: ");
        try {
            System.out.println(this.controller.addPerson(fname,lname,option));
        } catch (Exception exception) {
            System.out.println(exception);
            System.out.println("Operation failed");
        }
        System.out.println("------------------------");
    }

    /**
     * this method implements the login-logic for user
     * @param scanner Scanner for reading input
     */
    public void login(@NotNull Scanner scanner) throws SQLException {
        System.out.println("------------------------");
        System.out.print("Enter uniqueId:");
         this.uniqueId=scanner.nextLine();
        String name=null;
        try {
            name=controller.findPerson(this.uniqueId);
        }catch (Exception exception){
            System.out.println(exception);
        }

        if(name!=null){
            System.out.println("uniqueId correct");
            System.out.println("------------------------");
            System.out.println("Welcome back, "+name);

            while(flag){
                this.show_menu(name.split("")[0]);
                this.pick_option(scanner);

            }

        }
        else{
            System.out.println("uniqueId incorrect");
            System.out.println("------------------------");
        }
    }

    /**
     * this method closes the app
     */
    public void close(){
        System.exit(0);
    }

    /**
     * this method shows the menu accordingly to the user status
     * @param status Status can be Teacher or Student
     */
    public void show_menu(@NotNull String status){
        System.out.println("------------------------");
        System.out.println("0.Close App");
        System.out.println("1.Show Courses with free places");
        System.out.println(("2.Show all Students enrolled for a specific Course"));
        if(status.equals("S"))
            System.out.println("3.Register for a course");
        else{
            System.out.println("3.Change credits of a course");
            System.out.println("4.Add a course");
            System.out.println("5.Delete a Course");
        }

    }

    /**
     * this method pick the option from the user and calls the option_method accordingly
     * @param scanner Scanner for reading input
     * @throws SQLException error when handling database
     */
    public void pick_option(@NotNull Scanner scanner) throws SQLException {
        String option;
        System.out.print("Option:");
        option=scanner.nextLine();
        switch (option){
            case "0":{
                this.flag=false;
                break;
            }
            case "1":{
                this.option1(scanner);
                break;
            }
            case "2":{
                this.option2(scanner);
                break;
            }
            case "3":{
                this.option3(scanner);
                break;
            }
            case "4":{
                this.option4(scanner);
                break;
            }
            case "5":{
                this.option5(scanner);
                break;
            }
            default:break;
        }


    }


    /**
     * this method implements the option for Course with free Places
     * @param scanner Scanner for reading input
     * @throws SQLException error when handling database
     */
    public void option1(@NotNull Scanner scanner) throws SQLException {
        System.out.println("------------------------");
        Views.retrieveCoursesWithFreePlacesView(this.controller.retrieveCoursesWithFreePlaces());
        System.out.println("------------------------");
        System.out.println("Hit Enter to continue.....");
        scanner.nextLine();
    }
    /**
     * this method implements the option for Student enrolled a Course
     * @param scanner Scanner for reading input
     */
    public void option2(Scanner scanner){
        System.out.println("------------------------");
        System.out.print("Enter Course uniqueId:");
        try{
            Views.retrieveStudentsEnrolledForACourseView(this.controller.retrieveStudentsEnrolledForACourse(scanner.nextLine()));
        }catch(Exception exception){
            System.out.println(exception);
            System.out.println("Incorrect Id");
        }

        System.out.println("------------------------");
        System.out.println("Hit Enter to continue.....");
        scanner.nextLine();

    }

    /**
     * it is User-dependant, this method implements the option for registering for a Course or changing the credits of a course
     * @param scanner Scanner for reading input
     * @throws SQLException error when handling database
     */
    public void option3(Scanner scanner) throws SQLException {
        if(this.controller.findPerson(this.uniqueId).split("")[0].equals("S")){
            System.out.println("------------------------");
            System.out.println("Select a course");
            Views.registerForCourseView(this.controller.retrieveCoursesWithFreePlaces());
            System.out.print("Option:");
            String option=scanner.nextLine();
            try{
                String course_id=controller.register(this.controller.retrieveCoursesWithFreePlaces().get(Integer.parseInt(option)-1),this.uniqueId);
                System.out.println("The course unique identifier :"+course_id);
            }catch(Exception exception){
                System.out.println(exception);
            }
        }else{
            System.out.print("Enter Course unique identifier: ");
            String course_id=scanner.nextLine();
            System.out.print("Enter new Number of Credits: ");
            String credits=scanner.nextLine();

            try {
                this.controller.changeCredits(this.uniqueId,course_id,credits);
                System.out.println("Operation successful");
            } catch (Exception exception) {
                System.out.println(exception);
                System.out.println("Operation unsuccessful");
            }

        }
        System.out.println("------------------------");
        System.out.println("Hit Enter to continue.....");
        scanner.nextLine();


    }
    /**
     * this method implements the option for adding a course
     * @param scanner Scanner for reading input
     */
    public void option4(@NotNull Scanner scanner){
        System.out.println("------------------------");
        String name,maxenrollment,credits;
        System.out.println("Enter course details...");
        System.out.print("Name:");
        name=scanner.nextLine();
        System.out.print("Max-Enrollment:");
        maxenrollment=scanner.nextLine();
        System.out.print("Credits:");
        credits= scanner.nextLine();
        try {
            String course_id=this.controller.addCourse(this.uniqueId,name,maxenrollment,credits);
            System.out.println("Course Unique Identifier:"+course_id);
            System.out.println("Operation successful");
        } catch (Exception exception) {
            System.out.println(exception);
            System.out.println("Operation unsuccessful");
        }
        System.out.println("------------------------");
        System.out.println("Hit Enter to continue.....");
        scanner.nextLine();

    }

    /**
     * this method implements the option for deleting a course
     * @param scanner Scanner for reading input
     */
    public void option5(Scanner scanner){
        System.out.println("------------------------");
        System.out.print("Enter Course unique identifier:");
        try {
            this.controller.deleteCourse(scanner.nextLine(),this.uniqueId);
            System.out.println("operation successful");
        } catch (Exception exception) {
            System.out.println(exception);
            System.out.println("Operation unsuccessful");
        }
        System.out.println("------------------------");
        System.out.println("Hit Enter to continue.....");
        scanner.nextLine();

    }

}

