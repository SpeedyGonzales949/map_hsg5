package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * this class implements the logic for model Student
 */
public class Student extends Person {
    private UUID studentId;
    private int totalCredits;
    private List<UUID> enrolledCourses;

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
        this.studentId = UUID.randomUUID();
        this.totalCredits = 0;
        this.enrolledCourses=new ArrayList<>();
    }

    public Student(String firstName, String lastName, UUID studentId, int totalCredits, List<UUID> enrolledCourses) {
        super(firstName, lastName);
        this.studentId = studentId;
        this.totalCredits = totalCredits;
        this.enrolledCourses = enrolledCourses;
    }

    public Student() {
    }

    public void setTotalCredits(int totalCredits)  {
        this.totalCredits = totalCredits;
    }

    public void setEnrolledCourses(List<UUID> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public List<UUID> getEnrolledCourses() {
        return enrolledCourses;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", totalCredits=" + totalCredits +
                ", enrolledCourses=" + enrolledCourses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return getTotalCredits() == student.getTotalCredits() && getStudentId().equals(student.getStudentId()) && getEnrolledCourses().equals(student.getEnrolledCourses());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStudentId(), getTotalCredits(), getEnrolledCourses());
    }
}
