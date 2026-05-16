package util;

import model.Activity;
import model.Member;
import model.Registration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static final String DATA_DIR           = "data";
    private static final String MEMBERS_FILE       = "data/members.dat";
    private static final String ACTIVITIES_FILE    = "data/activities.dat";
    private static final String REGISTRATIONS_FILE = "data/registrations.dat";

    public static void initDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        if (!new File(MEMBERS_FILE).exists()) {
            List<Member> members = new ArrayList<>();
            Member admin = new Member(1, "admin", "Admin@123",
                    "Admin", "System", "01/01/1990",
                    "Adresse du Club", "00000000", "admin@club.com", 0);
            admin.setAdmin(true);
            admin.setMustChangePassword(false);
            members.add(admin);
            saveMembers(members);
        }
        if (!new File(ACTIVITIES_FILE).exists())    saveActivities(new ArrayList<>());
        if (!new File(REGISTRATIONS_FILE).exists()) saveRegistrations(new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public static List<Member> loadMembers() {
        Object obj = load(MEMBERS_FILE);
        return (obj instanceof List) ? (List<Member>) obj : new ArrayList<>();
    }
    public static void saveMembers(List<Member> members) { save(MEMBERS_FILE, members); }

    @SuppressWarnings("unchecked")
    public static List<Activity> loadActivities() {
        Object obj = load(ACTIVITIES_FILE);
        return (obj instanceof List) ? (List<Activity>) obj : new ArrayList<>();
    }
    public static void saveActivities(List<Activity> activities) { save(ACTIVITIES_FILE, activities); }

    @SuppressWarnings("unchecked")
    public static List<Registration> loadRegistrations() {
        Object obj = load(REGISTRATIONS_FILE);
        return (obj instanceof List) ? (List<Registration>) obj : new ArrayList<>();
    }
    public static void saveRegistrations(List<Registration> regs) { save(REGISTRATIONS_FILE, regs); }

    private static Object load(String path) {
        File f = new File(path);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lecture " + path + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private static void save(String path, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Erreur écriture " + path + ": " + e.getMessage());
        }
    }

    public static int nextMemberId() {
        return loadMembers().stream().mapToInt(Member::getId).max().orElse(0) + 1;
    }
    public static int nextActivityId() {
        return loadActivities().stream().mapToInt(Activity::getId).max().orElse(0) + 1;
    }
    public static int nextRegistrationId() {
        return loadRegistrations().stream().mapToInt(Registration::getId).max().orElse(0) + 1;
    }
}