package task06.model;

import java.sql.Timestamp;

/**
 * Model Entity representing an Event Registration record.
 */
public class Registration {
    private int registrationId;
    private String studentId;
    private String studentName;
    private String email;
    private String department;
    private String eventName;
    private Timestamp registrationTime;

    public Registration() {}

    public Registration(String studentId, String studentName, String email, String department, String eventName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.department = department;
        this.eventName = eventName;
    }

    public Registration(int registrationId, String studentId, String studentName, String email, String department, String eventName, Timestamp registrationTime) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.department = department;
        this.eventName = eventName;
        this.registrationTime = registrationTime;
    }

    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Timestamp getRegistrationTime() { return registrationTime; }
    public void setRegistrationTime(Timestamp registrationTime) { this.registrationTime = registrationTime; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s [%s]", registrationId, studentName, studentId, eventName, department);
    }
}
