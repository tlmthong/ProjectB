import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public ObservableList<ExerciseType> getExerciseTypes() {
        return FXCollections.observableArrayList(ExerciseType.values());
    }


    public void addupdateDailyLog(DailyLog dailyLog, ExerciseType exercisetype, String rep) {
        DailyLog daily = this.sys.currentUser.history.getDailyLog(dailyLog);
        Exercise exercise;
        if (sys.currentUser.caloriesCalculation == CalculateExcerciseCalories.DURATION_OF_EXCERCISE) {
            double repetition = convertStringToDouble(rep);
            exercise = new Exercise(new SimpleDoubleProperty(repetition), new SimpleIntegerProperty(0), exercisetype);
        } else {
            int repetition = convertStringToInt(rep);
            exercise = new Exercise(new SimpleDoubleProperty(0), new SimpleIntegerProperty(repetition), exercisetype);
        }
        daily.setFeature(exercise);
    }

    public void deleteDailyLog(DailyLog daily) {
        this.sys.currentUser.history.getHistory().remove(daily);
    }

    public void addExercise(SimpleIntegerProperty day, SimpleIntegerProperty month, SimpleIntegerProperty year,
            ExerciseType exerciseType, SimpleIntegerProperty repetitions) {
        DailyLog dailyLog = sys.currentUser.getFitnessHistory().getDailyLog(day, month, year);
        if (dailyLog == null) {
            dailyLog = new DailyLog(day, month, year);
        }

        SimpleDoubleProperty hours = new SimpleDoubleProperty(0);
        SimpleIntegerProperty count = repetitions;
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);
        System.out.println(exercise.getCount() + "" + exercise.getHours());
        sys.currentUser.getFitnessHistory().addOrUpdateDailyLog(dailyLog);
        sys.currentUser.calculateCalories(dailyLog);
        sys.currentUser.calculateImprovement(dailyLog, sys.currentUser.getGoal());
    }

    public void addExercise(SimpleIntegerProperty day, SimpleIntegerProperty month, SimpleIntegerProperty year,
            ExerciseType exerciseType, SimpleDoubleProperty hours) {
        DailyLog dailyLog = sys.currentUser.getFitnessHistory().getDailyLog(day, month, year);
        if (dailyLog == null) {
            dailyLog = new DailyLog(day, month, year);
        }
        SimpleIntegerProperty count = new SimpleIntegerProperty(0);
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);
        System.out.println(exercise.getCount() + "" + exercise.getHours());
        sys.currentUser.getFitnessHistory().addOrUpdateDailyLog(dailyLog);
        System.out.println("a");
        sys.currentUser.calculateCalories(dailyLog);
        System.out.println("a");
        sys.currentUser.calculateImprovement(dailyLog, sys.currentUser.getGoal());
        System.out.println("a");
    }

    public void addExercise(DailyLog dailyLog, ExerciseType exerciseType, SimpleIntegerProperty repetitions) {
        SimpleDoubleProperty hours = new SimpleDoubleProperty(0);
        SimpleIntegerProperty count = repetitions;
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);
        System.out.println(exercise.getCount() + "" + exercise.getHours());
        sys.currentUser.getFitnessHistory().addOrUpdateDailyLog(dailyLog);
        sys.currentUser.calculateCalories(dailyLog);
        sys.currentUser.calculateImprovement(dailyLog, sys.currentUser.getGoal());
    }

    public void addExercise(DailyLog dailyLog, ExerciseType exerciseType, SimpleDoubleProperty hours) {
        SimpleIntegerProperty count = new SimpleIntegerProperty(0);
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);
        System.out.println(exercise.getCount() + "" + exercise.getHours());
        sys.currentUser.getFitnessHistory().addOrUpdateDailyLog(dailyLog);
        sys.currentUser.calculateCalories(dailyLog);
        sys.currentUser.calculateImprovement(dailyLog, sys.currentUser.getGoal());
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

    public void addSleep(String day, String month, String year, String hours) {
        System.out.println(day + month + year + hours);
        DailyLog daily = sys.currentUser.getFitnessHistory().getDailyLog(
                new SimpleIntegerProperty(Integer.parseInt(day)), new SimpleIntegerProperty(Integer.parseInt(month)),
                new SimpleIntegerProperty(Integer.parseInt(year)));
        if (daily == null) {
            daily = new DailyLog(new SimpleIntegerProperty(Integer.parseInt(day)),
                    new SimpleIntegerProperty(Integer.parseInt(month)),
                    new SimpleIntegerProperty(Integer.parseInt(year)));
            System.out.println("all");
        }
        Double hour = Double.parseDouble(hours);
        System.out.println(hour);
        daily.addFeature(new SimpleDoubleProperty(hour));
        System.out.println(daily.viewDailyLog(sys.currentUser.caloriesCalculation));
    }

    public void addCalories(String cal, DailyLog daily) {
        daily.addCalories(Double.parseDouble(cal));
    }

    public void burnCalories(String cal, DailyLog daily) {
        daily.burnCalories(Double.parseDouble(cal));
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
        return Integer.parseInt(s);
    }

    public double convertStringToDouble(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if ("-".equals(s)) {
            return 0;
        }
        return Double.parseDouble(s);
    }

}
