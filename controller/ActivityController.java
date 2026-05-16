package controller;

import model.Activity;
import model.Member;
import model.Registration;
import util.FileUtil;
import util.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ActivityController {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Member login(String login, String password) {
        if (!Validator.isNotEmpty(login) || !Validator.isNotEmpty(password)) return null;
        try {
            List<Member> members = FileUtil.loadMembers();
            return members.stream()
                    .filter(m -> m.getLogin().equals(login.trim())
                            && m.getPassword().equals(password))
                    .findFirst().orElse(null);
        } catch (Exception e) {
            System.err.println("Erreur connexion : " + e.getMessage());
            return null;
        }
    }

    public List<Member> getAllMembers() {
        try {
            return FileUtil.loadMembers().stream()
                    .filter(m -> !m.isAdmin())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String addMember(String login, String password, String firstName, String lastName,
                            String birthDate, String address, String phone, String email,
                            String weightStr) {
        List<String> errors = Validator.validateMember(login, password, firstName,
                lastName, birthDate, address, phone, email, weightStr, false);
        if (!errors.isEmpty()) return String.join("\n", errors);
        try {
            List<Member> members = FileUtil.loadMembers();
            boolean exists = members.stream().anyMatch(m -> m.getLogin().equals(login.trim()));
            if (exists) return "• Ce login est déjà utilisé.";
            double weight = Double.parseDouble(weightStr.replace(",", "."));
            int id = FileUtil.nextMemberId();
            Member m = new Member(id, login.trim(), password, firstName.trim(),
                    lastName.trim(), birthDate.trim(), address.trim(),
                    phone.trim(), email.trim(), weight);
            members.add(m);
            FileUtil.saveMembers(members);
            return null;
        } catch (Exception e) {
            return "Erreur interne lors de l'ajout du membre.";
        }
    }

    public String updateMember(int memberId, String firstName, String lastName,
                               String birthDate, String address, String phone,
                               String email, String weightStr) {
        List<String> errors = Validator.validateMember(null, null, firstName,
                lastName, birthDate, address, phone, email, weightStr, true);
        if (!errors.isEmpty()) return String.join("\n", errors);
        try {
            List<Member> members = FileUtil.loadMembers();
            for (Member m : members) {
                if (m.getId() == memberId) {
                    double weight = Double.parseDouble(weightStr.replace(",", "."));
                    m.setFirstName(firstName.trim());
                    m.setLastName(lastName.trim());
                    m.setBirthDate(birthDate.trim());
                    m.setAddress(address.trim());
                    m.setPhone(phone.trim());
                    m.setEmail(email.trim());
                    m.setWeight(weight);
                    FileUtil.saveMembers(members);
                    return null;
                }
            }
            return "Membre introuvable.";
        } catch (Exception e) {
            return "Erreur interne lors de la modification.";
        }
    }

    public String deleteMember(int memberId) {
        try {
            List<Member> members = FileUtil.loadMembers();
            boolean removed = members.removeIf(m -> m.getId() == memberId);
            if (!removed) return "Membre introuvable.";
            List<Registration> regs = FileUtil.loadRegistrations();
            regs.removeIf(r -> r.getMemberId() == memberId);
            FileUtil.saveRegistrations(regs);
            FileUtil.saveMembers(members);
            return null;
        } catch (Exception e) {
            return "Erreur interne lors de la suppression.";
        }
    }

    public String changeMemberPassword(int memberId, String oldPassword,
                                       String newPassword, String confirmPassword) {
        if (!Validator.isNotEmpty(oldPassword)) return "L'ancien mot de passe est obligatoire.";
        if (!Validator.isNotEmpty(newPassword)) return "Le nouveau mot de passe est obligatoire.";
        if (!newPassword.equals(confirmPassword)) return "Les mots de passe ne correspondent pas.";
        if (!Validator.isValidPassword(newPassword))
            return "Nouveau mot de passe : minimum 6 caractères dont 1 chiffre.";
        try {
            List<Member> members = FileUtil.loadMembers();
            for (Member m : members) {
                if (m.getId() == memberId) {
                    if (!m.getPassword().equals(oldPassword))
                        return "L'ancien mot de passe est incorrect.";
                    m.setPassword(newPassword);
                    m.setMustChangePassword(false);
                    FileUtil.saveMembers(members);
                    return null;
                }
            }
            return "Membre introuvable.";
        } catch (Exception e) {
            return "Erreur interne lors du changement de mot de passe.";
        }
    }

    public Member getMemberById(int id) {
        try {
            return FileUtil.loadMembers().stream()
                    .filter(m -> m.getId() == id)
                    .findFirst().orElse(null);
        } catch (Exception e) { return null; }
    }

    public List<Activity> getAllActivities() {
        try {
            return FileUtil.loadActivities();
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public String addActivity(String name, String description,
                              String capacityStr, String schedule) {
        List<String> errors = Validator.validateActivity(name, description, capacityStr, schedule);
        if (!errors.isEmpty()) return String.join("\n", errors);
        try {
            int capacity = Integer.parseInt(capacityStr.trim());
            List<Activity> activities = FileUtil.loadActivities();
            activities.add(new Activity(FileUtil.nextActivityId(),
                    name.trim(), description.trim(), capacity, schedule.trim()));
            FileUtil.saveActivities(activities);
            return null;
        } catch (Exception e) {
            return "Erreur interne lors de l'ajout de l'activité.";
        }
    }

    public String updateActivity(int activityId, String name, String description,
                                 String capacityStr, String schedule) {
        List<String> errors = Validator.validateActivity(name, description, capacityStr, schedule);
        if (!errors.isEmpty()) return String.join("\n", errors);
        try {
            int capacity = Integer.parseInt(capacityStr.trim());
            List<Activity> activities = FileUtil.loadActivities();
            for (Activity a : activities) {
                if (a.getId() == activityId) {
                    a.setName(name.trim());
                    a.setDescription(description.trim());
                    a.setMaxCapacity(capacity);
                    a.setSchedule(schedule.trim());
                    FileUtil.saveActivities(activities);
                    return null;
                }
            }
            return "Activité introuvable.";
        } catch (Exception e) {
            return "Erreur interne lors de la modification.";
        }
    }

    public String deleteActivity(int activityId) {
        try {
            List<Activity> activities = FileUtil.loadActivities();
            boolean removed = activities.removeIf(a -> a.getId() == activityId);
            if (!removed) return "Activité introuvable.";
            List<Registration> regs = FileUtil.loadRegistrations();
            regs.removeIf(r -> r.getActivityId() == activityId);
            FileUtil.saveRegistrations(regs);
            FileUtil.saveActivities(activities);
            return null;
        } catch (Exception e) {
            return "Erreur interne lors de la suppression.";
        }
    }

    public Activity getActivityById(int id) {
        try {
            return FileUtil.loadActivities().stream()
                    .filter(a -> a.getId() == id)
                    .findFirst().orElse(null);
        } catch (Exception e) { return null; }
    }

    public List<Registration> getAllRegistrations() {
        try { return FileUtil.loadRegistrations(); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public List<Registration> getRegistrationsByMember(int memberId) {
        try {
            return FileUtil.loadRegistrations().stream()
                    .filter(r -> r.getMemberId() == memberId)
                    .collect(Collectors.toList());
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public List<Registration> getRegistrationsByActivity(int activityId) {
        try {
            return FileUtil.loadRegistrations().stream()
                    .filter(r -> r.getActivityId() == activityId)
                    .collect(Collectors.toList());
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public String registerMember(int memberId, int activityId) {
        try {
            List<Registration> regs = FileUtil.loadRegistrations();
            boolean alreadyReg = regs.stream().anyMatch(
                    r -> r.getMemberId() == memberId && r.getActivityId() == activityId);
            if (alreadyReg) return "Vous êtes déjà inscrit à cette activité.";
            Activity activity = getActivityById(activityId);
            if (activity == null) return "Activité introuvable.";
            if (activity.isFull()) return "Cette activité est complète.";
            String today = LocalDate.now().format(DATE_FMT);
            regs.add(new Registration(FileUtil.nextRegistrationId(), memberId, activityId, today));
            FileUtil.saveRegistrations(regs);
            return null;
        } catch (Exception e) {
            return "Erreur interne lors de l'inscription.";
        }
    }

    public String cancelRegistration(int memberId, int activityId) {
        try {
            List<Registration> regs = FileUtil.loadRegistrations();
            boolean removed = regs.removeIf(
                    r -> r.getMemberId() == memberId && r.getActivityId() == activityId);
            if (!removed) return "Inscription introuvable.";
            FileUtil.saveRegistrations(regs);
            return null;
        } catch (Exception e) {
            return "Erreur interne lors de l'annulation.";
        }
    }

    public String deleteRegistration(int registrationId) {
        try {
            List<Registration> regs = FileUtil.loadRegistrations();
            boolean removed = regs.removeIf(r -> r.getId() == registrationId);
            if (!removed) return "Inscription introuvable.";
            FileUtil.saveRegistrations(regs);
            return null;
        } catch (Exception e) {
            return "Erreur interne lors de la suppression.";
        }
    }

    public String validateRegistration(int registrationId, boolean accept) {
        try {
            List<Registration> regs = FileUtil.loadRegistrations();
            for (Registration r : regs) {
                if (r.getId() == registrationId) {
                    r.setStatus(accept ? Registration.Status.ACCEPTEE : Registration.Status.REFUSEE);
                    FileUtil.saveRegistrations(regs);
                    if (accept) {
                        List<Activity> activities = FileUtil.loadActivities();
                        activities.stream()
                                .filter(a -> a.getId() == r.getActivityId())
                                .findFirst()
                                .ifPresent(a -> {
                                    if (!a.getRegisteredMemberIds().contains(r.getMemberId()))
                                        a.getRegisteredMemberIds().add(r.getMemberId());
                                });
                        FileUtil.saveActivities(activities);
                    }
                    return null;
                }
            }
            return "Inscription introuvable.";
        } catch (Exception e) {
            return "Erreur interne lors de la validation.";
        }
    }

    public List<Activity> getFullActivities() {
        return getAllActivities().stream().filter(Activity::isFull).collect(Collectors.toList());
    }

    public List<Member> getMostActiveMembers() {
        List<Registration> regs = getAllRegistrations();
        List<Member> members = getAllMembers();
        members.sort(Comparator.comparingLong((Member m) ->
                regs.stream().filter(r -> r.getMemberId() == m.getId()
                        && r.getStatus() == Registration.Status.ACCEPTEE).count()
        ).reversed());
        return members;
    }

    public int getParticipantCount(int activityId) {
        try {
            return (int) FileUtil.loadRegistrations().stream()
                    .filter(r -> r.getActivityId() == activityId
                            && r.getStatus() == Registration.Status.ACCEPTEE)
                    .count();
        } catch (Exception e) { return 0; }
    }

    public Activity getMostPopularActivity() {
        return getAllActivities().stream()
                .max(Comparator.comparingInt(a -> getParticipantCount(a.getId())))
                .orElse(null);
    }

    public long getMemberActivityCount(int memberId) {
        return getAllRegistrations().stream()
                .filter(r -> r.getMemberId() == memberId
                        && r.getStatus() == Registration.Status.ACCEPTEE)
                .count();
    }
}