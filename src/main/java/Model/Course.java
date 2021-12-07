package Model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * this class implements the logic for the model course
 */
public class Course {
    private String name;
    private UUID teacher;
    private int maxEnrollment;
    private int credits;
    private List<UUID> studentsEnrolled;
    private UUID id;



    public Course(String name, UUID teacher, int maxEnrollment, int credits) {
        this.name = name;
        this.teacher = teacher;
        this.maxEnrollment = maxEnrollment;
        this.credits = credits;
        this.studentsEnrolled= new ArrayList<>();
        this.id=UUID.randomUUID();
    }

    public Course(String name, UUID teacher, int maxEnrollment, int credits, List<UUID> studentsEnrolled, UUID id) {
        this.name = name;
        this.teacher = teacher;
        this.maxEnrollment = maxEnrollment;
        this.credits = credits;
        this.studentsEnrolled = studentsEnrolled;
        this.id = id;
    }

    public Course() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getTeacher() {
        return teacher;
    }

    public void setTeacher(UUID teacher) {
        this.teacher = teacher;
    }

    public int getMaxEnrollment() {
        return maxEnrollment;
    }

    public void setMaxEnrollment(int maxEnrollment) {
        this.maxEnrollment = maxEnrollment;
    }
    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public List<UUID> getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", teacher=" + teacher +
                ", maxEnrollment=" + maxEnrollment +
                ", credits=" + credits +
                ", studentsEnrolled=" + studentsEnrolled +
                ", id=" + id +
                "}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return getMaxEnrollment() == course.getMaxEnrollment() && getCredits() == course.getCredits() && getName().equals(course.getName()) && getTeacher().equals(course.getTeacher()) && getStudentsEnrolled().equals(course.getStudentsEnrolled()) && getId().equals(course.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTeacher(), getMaxEnrollment(), getCredits(), getStudentsEnrolled(), getId());
    }
}
