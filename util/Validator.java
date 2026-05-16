package util;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{8,15}$");
    }
    public static boolean isValidDate(String date) {
        if (date == null || !date.matches("^\\d{2}/\\d{2}/\\d{4}$")) return false;
        String[] p = date.split("/");
        int d = Integer.parseInt(p[0]), m = Integer.parseInt(p[1]), y = Integer.parseInt(p[2]);
        return d >= 1 && d <= 31 && m >= 1 && m <= 12 && y >= 1900 && y <= 2025;
    }
    public static boolean isValidPassword(String pwd) {
        return pwd != null && pwd.length() >= 6 && pwd.matches(".*\\d.*");
    }
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static List<String> validateMember(String login, String password,
            String firstName, String lastName, String birthDate, String address,
            String phone, String email, String weightStr, boolean isEdit) {
        List<String> errors = new ArrayList<>();
        if (!isEdit && !isNotEmpty(login))      errors.add("• Le login est obligatoire.");
        if (!isEdit && !isNotEmpty(password))   errors.add("• Le mot de passe est obligatoire.");
        if (!isEdit && isNotEmpty(password) && !isValidPassword(password))
            errors.add("• Mot de passe : min 6 caractères dont 1 chiffre.");
        if (!isNotEmpty(firstName))  errors.add("• Le prénom est obligatoire.");
        if (!isNotEmpty(lastName))   errors.add("• Le nom est obligatoire.");
        if (!isNotEmpty(birthDate))  errors.add("• La date de naissance est obligatoire.");
        else if (!isValidDate(birthDate)) errors.add("• Date invalide (format : dd/MM/yyyy).");
        if (!isNotEmpty(address))    errors.add("• L'adresse est obligatoire.");
        if (!isNotEmpty(phone))      errors.add("• Le téléphone est obligatoire.");
        else if (!isValidPhone(phone)) errors.add("• Téléphone invalide (8 à 15 chiffres).");
        if (!isNotEmpty(email))      errors.add("• L'email est obligatoire.");
        else if (!isValidEmail(email)) errors.add("• Format email invalide.");
        if (!isNotEmpty(weightStr))  errors.add("• Le poids est obligatoire.");
        else { try {
            double w = Double.parseDouble(weightStr.replace(",", "."));
            if (w <= 0 || w > 500) errors.add("• Poids invalide (1-500 kg).");
        } catch (NumberFormatException e) { errors.add("• Poids invalide (ex: 70.5)."); } }
        return errors;
    }

    public static List<String> validateActivity(String name, String description,
                                                String capacityStr, String schedule) {
        List<String> errors = new ArrayList<>();
        if (!isNotEmpty(name))        errors.add("• Le nom est obligatoire.");
        if (!isNotEmpty(description)) errors.add("• La description est obligatoire.");
        if (!isNotEmpty(schedule))    errors.add("• Les horaires sont obligatoires.");
        if (!isNotEmpty(capacityStr)) errors.add("• La capacité est obligatoire.");
        else { try {
            int cap = Integer.parseInt(capacityStr.trim());
            if (cap <= 0)    errors.add("• La capacité doit être > 0.");
            if (cap > 1000)  errors.add("• Capacité trop grande (max 1000).");
        } catch (NumberFormatException e) { errors.add("• Capacité invalide (ex: 20)."); } }
        return errors;
    }
}