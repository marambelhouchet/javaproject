package model;

import java.io.Serializable;

public class Registration implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { EN_ATTENTE, ACCEPTEE, REFUSEE }

    private int id;
    private int memberId;
    private int activityId;
    private Status status;
    private String registrationDate;

    public Registration(int id, int memberId, int activityId, String registrationDate) {
        this.id = id; this.memberId = memberId;
        this.activityId = activityId;
        this.status = Status.EN_ATTENTE;
        this.registrationDate = registrationDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String d) { this.registrationDate = d; }

    public String getStatusLabel() {
        switch (status) {
            case EN_ATTENTE: return "En attente";
            case ACCEPTEE:   return "Acceptée";
            case REFUSEE:    return "Refusée";
            default:         return "Inconnu";
        }
    }
}