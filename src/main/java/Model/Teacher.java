package Model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * this class implements the model Teacher
 */
public class Teacher extends Person{
    private List<UUID> courses;
    private UUID id;
    public Teacher(String firstName, String lastName) {
        super(firstName, lastName);
        this.id=UUID.randomUUID();
        this.courses=new ArrayList<>();
    }

    public Teacher(String firstName, String lastName, List<UUID> courses, UUID id) {
        super(firstName, lastName);
        this.courses = courses;
        this.id = id;
    }

    public List<UUID> getCourses() {
        return courses;
    }

    public void setCourses(List<UUID> courses) {
        this.courses = courses;
    }

    public void addCourse(@NotNull Course course){this.courses.add(course.getId()); }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "courses=" + courses +
                ", id=" + id +
                '}';
    }
    /**
     *
     * @param o class object to compare with
     * @return true if the objects match, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher)) return false;
        Teacher teacher = (Teacher) o;
        return getCourses().equals(teacher.getCourses()) && getId().equals(teacher.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourses(), getId());
    }

}
