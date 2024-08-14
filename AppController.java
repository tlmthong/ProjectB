import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class AppController {
    Systems sys;
    Feature feature;
    AdminUser admin;

    AppController(Systems sys, AdminUser admin) {
        this.sys = sys;
        this.admin = admin;
    }

    public void createAccount(String username, String password, String email,
            String weight, String height, Goal goal, CalculateExcerciseCalories caloriesCalculation) {
        SimpleStringProperty usernameProperty = new SimpleStringProperty(username);
        SimpleStringProperty passwordProperty = new SimpleStringProperty(password);
        SimpleStringProperty emailProperty = new SimpleStringProperty(email);
        SimpleDoubleProperty weightProperty = new SimpleDoubleProperty(convertStringToDouble(weight));
        SimpleDoubleProperty heightProperty = new SimpleDoubleProperty(convertStringToDouble(height));
        sys.addAccount(usernameProperty, passwordProperty, emailProperty, weightProperty,
                heightProperty, goal, caloriesCalculation);
    }

    public void logIn(String username, String password) {
        SimpleStringProperty usernameProperty = new SimpleStringProperty(username);
        SimpleStringProperty passwordProperty = new SimpleStringProperty(password);
        SimpleBooleanProperty log = new SimpleBooleanProperty(sys.logIn(usernameProperty, passwordProperty));
        if (!log.getValue()) {
            throw new Error("Cant logIn");
        }
    }

    public void logIn(int id, String password) {
        SimpleIntegerProperty idProperty = new SimpleIntegerProperty(id);
        SimpleStringProperty passwordProperty = new SimpleStringProperty(password);
        SimpleBooleanProperty log = new SimpleBooleanProperty(sys.logIn(idProperty, passwordProperty));
        if (!log.getValue()) {
            throw new Error("Cant logIn");
        }
    }

    public SimpleStringProperty getName() {
        String name = sys.currentUser.getName().getValue();
        SimpleStringProperty returnMessage = new SimpleStringProperty(name);
        return returnMessage;
    }

    public void addOrUpdateDailyLog(DailyLog dailyLog) {
        if (sys.currentUser.getFitnessHistory().getDailyLog(dailyLog).getDate().equals(dailyLog.getDate())) {
            return;
        }
        sys.currentUser.getFitnessHistory().addOrUpdateDailyLog(dailyLog);
    }

    public void removeAccout(String idString) {
        int id = convertStringToInt(idString);
        sys.removeAccount(new SimpleIntegerProperty(id));
    }

    public boolean verifyKey(String key) {
        SimpleStringProperty keyPropery = new SimpleStringProperty(key);
        return admin.VerifyKey(keyPropery);
    }

    private int convertStringToInt(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if ("-".equals(s)) {
            return 0;
        }
        return Integer.parseInt(s); // Convert string into integer
    }

    private double convertStringToDouble(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if ("-".equals(s)) {
            return 0;
        }
        return Double.parseDouble(s); // Convert string into double
    }

}