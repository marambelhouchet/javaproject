package model;

import java.io.Serializable;

public class Activity implements Serializable {
    private String name;
    private String description;
    private int capacity;
    private int enrolled;

    public Activity(String name, String description, int capacity) {
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.enrolled = 0;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCapacity() { return capacity; }
    public int getEnrolled() { return enrolled; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isFull() {
        return enrolled >= capacity;
    }

    public void enroll() {
        if (!isFull()) enrolled++;
    }

    @Override
    public String toString() {
        return name + " (" + enrolled + "/" + capacity + ")";
    }
}