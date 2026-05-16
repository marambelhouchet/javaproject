package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Activity implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String description;
    private int maxCapacity;
    private String schedule;
    private List<Integer> registeredMemberIds;

    public Activity(int id, String name, String description, int maxCapacity, String schedule) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxCapacity = maxCapacity;
        this.schedule = schedule;
        this.registeredMemberIds = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public List<Integer> getRegisteredMemberIds() { return registeredMemberIds; }
    public void setRegisteredMemberIds(List<Integer> list) { this.registeredMemberIds = list; }
    public int getRemainingPlaces() { return maxCapacity - registeredMemberIds.size(); }
    public boolean isFull() { return registeredMemberIds.size() >= maxCapacity; }

    @Override
    public String toString() {
        return name + " (" + schedule + ") - " + getRemainingPlaces() + " places restantes";
    }
}