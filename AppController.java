import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class AppController {
    Systems system;
    Feature feature;
    AdminUser admin;
    CaloriesCalulator caloriesCalulator;

    AppController(Systems system, AdminUser admin) {
        this.system = system;
        this.admin = admin;
    }

    public void setCalculator(CaloriesCalulator calulator) {
        this.caloriesCalulator = calulator;
    }

    public void changeCalculatorHour(String hour) {
        this.caloriesCalulator.changeHour(convertStringToDouble(hour));
        ;
    }

    public void changeCalculatorRep(String rep) {
        this.caloriesCalulator.changeRep(convertStringToInt(rep));
    }

    public void changeCalculatorExercise(ExerciseType exerciseType) {
        this.caloriesCalulator.changeExcerciseType(exerciseType);
    }

    public void changeCalculatorWeight() {
        this.caloriesCalulator.setWeight(this.system.getCurrentUser().getWeight().doubleValue());
    }

    public void reformatHistory() {
        ArrayList<DailyLog> oldArray = this.system.getCurrentUser().getFitnessHistory().sortedHistory();
        FitnessHistory formattedFitnessHistory = new FitnessHistory();
        this.system.getCurrentUser().setInitalBMI();
        this.system.getCurrentUser().setInitalWeight();
        this.system.getCurrentUser().setHistory(formattedFitnessHistory);
        for (DailyLog dailyLog : oldArray) {
            DailyLog workingDaily = new DailyLog(dailyLog.getDays().get(), dailyLog.getMonths().get(),
                    dailyLog.getYear().get());
            system.getCurrentUser().getFitnessHistory().addOrUpdateDailyLog(workingDaily);
            for (Feature feature : dailyLog.getFeatures()) {
                if (feature instanceof PhysicalMonitor) {
                    workingDaily.addFeature(((PhysicalMonitor) feature).getExercises());
                    system.getCurrentUser().calculateCalories(workingDaily);
                    system.getCurrentUser().calculateImprovement(workingDaily, system.getCurrentUser().getGoal());
                } else {
                    workingDaily.addFeature(feature);
                }
            }
        }
    }

    public void addupdateDailyLog(DailyLog dailyLog, ExerciseType exercisetype, String rep) {
        DailyLog daily = this.system.getCurrentUser().getFitnessHistory().getDailyLog(dailyLog);
        Exercise exercise;
        if (system.getCurrentUser().getCaloriesCalculation() == CalculateExcerciseCalories.DURATION_OF_EXCERCISE) {
            double repetition = convertStringToDouble(rep);
            exercise = new Exercise(new SimpleDoubleProperty(repetition), new SimpleIntegerProperty(0), exercisetype);
        } else {
            int repetition = convertStringToInt(rep);
            exercise = new Exercise(new SimpleDoubleProperty(0), new SimpleIntegerProperty(repetition), exercisetype);
        }
        daily.setFeature(exercise);
        reformatHistory();
    }

    public void deleteDailyLog(DailyLog daily) {
        this.system.getCurrentUser().getFitnessHistory().getHistory().remove(daily);
        reformatHistory();
    }

    public void addExercise(int day, int month, int year,
            ExerciseType exerciseType, SimpleIntegerProperty repetitions) {
        DailyLog dailyLog = system.getCurrentUser().getFitnessHistory().getDailyLog(day, month, year);
        if (dailyLog == null) {
            dailyLog = new DailyLog(day, month, year);
        }

        SimpleDoubleProperty hours = new SimpleDoubleProperty(0); // Set to 0 as we're using repetitions
        SimpleIntegerProperty count = repetitions;
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);
        system.getCurrentUser().getFitnessHistory().addOrUpdateDailyLog(dailyLog);
        system.getCurrentUser().calculateCalories(dailyLog);
        system.getCurrentUser().calculateImprovement(dailyLog, system.getCurrentUser().getGoal());
    }

    public void addExercise(int day, int month, int year,
            ExerciseType exerciseType, SimpleDoubleProperty hours) {
        DailyLog dailyLog = system.getCurrentUser().getFitnessHistory().getDailyLog(day, month, year);
        if (dailyLog == null) {
            dailyLog = new DailyLog(day, month, year);
        }
        SimpleIntegerProperty count = new SimpleIntegerProperty(0);
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);

        system.getCurrentUser().getFitnessHistory().addOrUpdateDailyLog(dailyLog);

        system.getCurrentUser().calculateCalories(dailyLog);

        system.getCurrentUser().calculateImprovement(dailyLog, system.getCurrentUser().getGoal());

    }

    public void addExercise(DailyLog dailyLog, ExerciseType exerciseType, SimpleIntegerProperty repetitions) {
        SimpleDoubleProperty hours = new SimpleDoubleProperty(0); // Set to 0 as we're using repetitions
        SimpleIntegerProperty count = repetitions;
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);
        system.getCurrentUser().getFitnessHistory().addOrUpdateDailyLog(dailyLog);
        system.getCurrentUser().calculateCalories(dailyLog);
        system.getCurrentUser().calculateImprovement(dailyLog, system.getCurrentUser().getGoal());
    }

    public void addExercise(DailyLog dailyLog, ExerciseType exerciseType, SimpleDoubleProperty hours) {
        SimpleIntegerProperty count = new SimpleIntegerProperty(0);
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);
        system.getCurrentUser().getFitnessHistory().addOrUpdateDailyLog(dailyLog);
        system.getCurrentUser().calculateCalories(dailyLog);
        system.getCurrentUser().calculateImprovement(dailyLog, system.getCurrentUser().getGoal());
    }

    public void createAccount(String username, String password, String email,
            String weight, String height, Goal goal, CalculateExcerciseCalories caloriesCalculation) {

        system.addAccount(username, password, email, convertStringToDouble(weight),
                convertStringToDouble(height), goal, caloriesCalculation);
    }

    public void logIn(String username, String password) {
        SimpleBooleanProperty log = new SimpleBooleanProperty(system.logIn(username, password));
        if (!log.getValue()) {
            throw new Error("Cant logIn");
        }
    }

    public void logIn(int id, String password) {
        SimpleBooleanProperty log = new SimpleBooleanProperty(system.logIn(id, password));
        if (!log.getValue()) {
            throw new Error("Cant logIn");
        }
    }

    public void addSleep(String day, String month, String year, String hours) {
        DailyLog daily = system.getCurrentUser().getFitnessHistory().getDailyLog(
                convertStringToInt(day), convertStringToInt(month), convertStringToInt(year));
        if (daily == null) {
            daily = new DailyLog(convertStringToInt(day), convertStringToInt(month), convertStringToInt(year));
        }
        Double hour = Double.parseDouble(hours);
        daily.addFeature(new SimpleDoubleProperty(hour));
    }

    public void addCalories(String cal, DailyLog daily) {
        daily.addCalories(Double.parseDouble(cal));
    }

    public void burnCalories(String cal, DailyLog daily) {
        daily.burnCalories(Double.parseDouble(cal));
    }

    public void addOrUpdateDailyLog(DailyLog dailyLog) {
        if (system.getCurrentUser().getFitnessHistory().getDailyLog(dailyLog).getDate().equals(dailyLog.getDate())) {
            return;
        }
        system.getCurrentUser().getFitnessHistory().addOrUpdateDailyLog(dailyLog);
    }

    public void removeAccout(String idString) {
        int id = convertStringToInt(idString);
        system.removeAccount(id);
    }

    public boolean verifyKey(String key) {
        SimpleStringProperty keyProperty = new SimpleStringProperty(key);
        return admin.VerifyKey(keyProperty);
    }

    public int convertStringToInt(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if ("-".equals(s)) {
            return 0;
        }
        return Integer.parseInt(s); // Convert string into integer
    }

    public double convertStringToDouble(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if ("-".equals(s)) {
            return 0;
        }
        return Double.parseDouble(s); // Convert string into double
    }

}