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

    public ObservableList<DailyLogEntry> getFitnessHistoryData() {
        ObservableList<DailyLogEntry> data = FXCollections.observableArrayList();
        try {
            System.out.println("Entering getFitnessHistoryData");
            if (sys.currentUser == null) {
                System.out.println("No user is currently logged in");
                throw new IllegalStateException("No user is currently logged in");
            }
            FitnessHistory history = sys.currentUser.getFitnessHistory();
            if (history == null) {
                System.out.println("Current user has no fitness history");
                throw new IllegalStateException("Current user has no fitness history");
            }
            List<DailyLog> sortedHistory = new ArrayList<>(history.getHistory());
            System.out.println("Number of daily logs: " + sortedHistory.size());
            Collections.sort(sortedHistory, DailyLog.comparatorForDates);
    
            for (int i = 0; i < sortedHistory.size(); i++) {
                DailyLog log = sortedHistory.get(i);
                if (log != null) {
                    System.out.println("Processing log for date: " + log.getDate().get());
                    String exercises = formatExercises(log.getTotalExcercises());
                    double improvementPercentage = 0.0;
                    if (log.getImprovementPercentage() != null) {
                        improvementPercentage = log.getImprovementPercentage().get();
                    }
                    data.add(new DailyLogEntry(i + 1, log.getDate().get(), improvementPercentage, exercises));
                } else {
                    System.out.println("Null log encountered at index " + i);
                }
            }
            System.out.println("Number of DailyLogEntry objects created: " + data.size());
        } catch (Exception e) {
            System.err.println("Error in getFitnessHistoryData: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }


    private String formatExercises(ArrayList<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            return "No exercises recorded";
        }
        StringBuilder sb = new StringBuilder();
        for (Exercise exercise : exercises) {
            if (exercise != null && exercise.getExerciseType() != null) {
                sb.append(exercise.getExerciseType().name).append(": ");
                if (sys.currentUser.getCaloriesCalculation() == CalculateExcerciseCalories.PER_EXCERCISE) {
                    sb.append("Repetition: ").append(exercise.getCount().get());
                } else {
                    sb.append("Hours: ").append(exercise.getHours().get());
                }
                sb.append("\n");
            }
        }
        return sb.toString().trim();
    }
    public static class DailyLogEntry {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty date;
        private final SimpleDoubleProperty improvementPercentage;
        private final SimpleStringProperty exercises;

        public DailyLogEntry(int id, String date, double improvementPercentage, String exercises) {
            this.id = new SimpleIntegerProperty(id);
            this.date = new SimpleStringProperty(date);
            this.improvementPercentage = new SimpleDoubleProperty(improvementPercentage);
            this.exercises = new SimpleStringProperty(exercises);
        }

        public int getId() { return id.get(); }
        public String getDate() { return date.get(); }
        public double getImprovementPercentage() { return improvementPercentage.get(); }
        public String getExercises() { return exercises.get(); }

        // Add setter methods if needed for editing functionality
        public void setDate(String date) { this.date.set(date); }
        public void setImprovementPercentage(double percentage) { this.improvementPercentage.set(percentage); }
        public void setExercises(String exercises) { this.exercises.set(exercises); }
    }

    public void editDailyLog(DailyLogEntry entry, String newDate, double newImprovementPercentage, String newExercises) {
        FitnessHistory history = sys.currentUser.getFitnessHistory();
        
        // Parse the date string to get day, month, and year
        String[] dateParts = entry.getDate().split("/");
        if (dateParts.length == 3) {
            SimpleIntegerProperty day = new SimpleIntegerProperty(Integer.parseInt(dateParts[0]));
            SimpleIntegerProperty month = new SimpleIntegerProperty(Integer.parseInt(dateParts[1]));
            SimpleIntegerProperty year = new SimpleIntegerProperty(Integer.parseInt(dateParts[2]));
            
            DailyLog logToEdit = history.getDailyLog(day, month, year);
            
            if (logToEdit != null) {
                // Update the DailyLog object
                logToEdit.getDate().set(newDate);
                logToEdit.setImprovementPercentage(new SimpleDoubleProperty(newImprovementPercentage));
                
                // Clear existing exercises and add new ones
                logToEdit.getTotalExcercises().clear();
                for (String exerciseInfo : newExercises.split("\n")) {
                    String[] parts = exerciseInfo.split(":");
                    if (parts.length == 2) {
                        ExerciseType type = ExerciseType.valueOf(parts[0].trim());
                        String[] valueParts = parts[1].trim().split(" ");
                        if (valueParts.length == 2) {
                            double value = Double.parseDouble(valueParts[1]);
                            if (valueParts[0].equals("Repetition:")) {
                                logToEdit.addFeature(new Exercise(new SimpleDoubleProperty(0), new SimpleIntegerProperty((int)value), type));
                            } else if (valueParts[0].equals("Hours:")) {
                                logToEdit.addFeature(new Exercise(new SimpleDoubleProperty(value), new SimpleIntegerProperty(0), type));
                            }
                        }
                    }
                }
                
                // Recalculate calories and improvement
                sys.currentUser.calculateCalories(logToEdit);
                sys.currentUser.calculateImprovement(logToEdit, sys.currentUser.getGoal());
            }
        }
    }

    public void deleteDailyLog(DailyLogEntry entry) {
        FitnessHistory history = sys.currentUser.getFitnessHistory();
        
        // Parse the date string to get day, month, and year
        String[] dateParts = entry.getDate().split("/");
        if (dateParts.length == 3) {
            SimpleIntegerProperty day = new SimpleIntegerProperty(Integer.parseInt(dateParts[0]));
            SimpleIntegerProperty month = new SimpleIntegerProperty(Integer.parseInt(dateParts[1]));
            SimpleIntegerProperty year = new SimpleIntegerProperty(Integer.parseInt(dateParts[2]));
            
            DailyLog logToDelete = history.getDailyLog(day, month, year);
            
            if (logToDelete != null) {
                history.getHistory().remove(logToDelete);
            }
        }
    }

    public ObservableList<ExerciseType> getExerciseTypes() {
        return FXCollections.observableArrayList(ExerciseType.values());
    }

    public DailyLog viewDailyLog(SimpleIntegerProperty day, SimpleIntegerProperty month, SimpleIntegerProperty year) {
        return sys.currentUser.getFitnessHistory().getDailyLog(day, month, year);
    }

    public String formatDailyLogInfo(DailyLog dailyLog) {
        if (dailyLog == null) {
            return "No log found for the specified date.";
        }

        StringBuilder info = new StringBuilder();
        info.append("Date: ").append(dailyLog.getDate().get()).append("\n");
        info.append("BMI: ").append(String.format("%.14f", dailyLog.getBMI().get())).append("\n");
        info.append("Improvement Percentage: ").append(String.format("%.14f", dailyLog.getImprovementPercentage().get())).append("%\n");

        double totalCaloriesBurnt = 0;
        for (Feature feature : dailyLog.getFeatures()) {
            if (feature instanceof PhysicalMonitor) {
                totalCaloriesBurnt += ((PhysicalMonitor) feature).getCaloriesBurnt().get();
            }
        }
        info.append("Calories Burnt from Exercises: ").append(String.format("%.14f", totalCaloriesBurnt)).append("\n");

        info.append("Exercises:\n");
        for (Exercise exercise : dailyLog.getTotalExcercises()) {
            info.append("- ").append(exercise.getExerciseType().name).append(":\n");
            info.append("  - Hours: ").append(exercise.getHours().get()).append("\n");
        }

        double totalSleep = 0;
        for (Feature feature : dailyLog.getFeatures()) {
            if (feature instanceof StressMonitor) {
                totalSleep += ((StressMonitor) feature).getSleep().get();
            }
        }
        info.append("Sleep Hours: ").append(String.format("%.1f", totalSleep)).append("\n");

        return info.toString();
    }

    public void addExercise(SimpleIntegerProperty day, SimpleIntegerProperty month, SimpleIntegerProperty year, 
                            ExerciseType exerciseType, SimpleIntegerProperty repetitions) {
        DailyLog dailyLog = sys.currentUser.getFitnessHistory().getDailyLog(day, month, year);
        if (dailyLog == null) {
            dailyLog = new DailyLog(day, month, year);
        }

        SimpleDoubleProperty hours = new SimpleDoubleProperty(0); // Set to 0 as we're using repetitions
        SimpleIntegerProperty count = repetitions;
        Exercise exercise = new Exercise(hours, count, exerciseType);
        dailyLog.addFeature(exercise);

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