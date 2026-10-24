package model;

// This code defines the Project class representing a project entity
public class Project {
    private int id;
    private String title;
    private String location;
    private String day;
    private double hourlyValue;
    private int registeredSlots;
    private int totalSlots;
    private boolean enabled;
    private Integer volunteerHours;

    // This code initializes a Project object with given parameters
    public Project(int id, String title, String location, String day,
                   double hourlyValue, int registeredSlots, int totalSlots, boolean enabled) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.day = day;
        this.hourlyValue = hourlyValue;
        this.registeredSlots = registeredSlots;
        this.totalSlots = totalSlots;
        this.enabled = enabled;
        this.volunteerHours = 0;
    }

    // This code provides getter methods for the Project fields
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDay() { return day; }
    public double getHourlyValue() { return hourlyValue; }
    public int getRegisteredSlots() { return registeredSlots; }
    public int getTotalSlots() { return totalSlots; }
    public boolean isEnabled() { return enabled; }

    // This code provides getter and setter methods for volunteerHours
    public Integer getVolunteerHours() { return volunteerHours; }
    public void setVolunteerHours(Integer volunteerHours) { this.volunteerHours = volunteerHours; }
}
