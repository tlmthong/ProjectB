import java.util.*;
import javafx.beans.property.*;

public class FitnessUser extends User implements UserAuthentication {
    private SimpleDoubleProperty weight;
    private SimpleDoubleProperty height;
    private Goal goal;
    private SimpleStringProperty password;
    private CalculateExcerciseCalories caloriesCalculation;
    private HashMap<Character, Character> encryptionMap = new HashMap<>();
    private FitnessHistory history = new FitnessHistory();
    private SimpleDoubleProperty calorieBalance = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty bmi = new SimpleDoubleProperty();

    FitnessUser(SimpleStringProperty username, SimpleStringProperty password, SimpleStringProperty email,
            SimpleDoubleProperty weight, SimpleDoubleProperty height, Goal goal,
            CalculateExcerciseCalories caloriesCalculation) {
        super(username, password, email);
        this.height = height;
        this.weight = weight;
        this.caloriesCalculation = caloriesCalculation;
        this.goal = goal;
        this.password = password;
        this.bmi.bind(weight.divide((height.doubleValue() * height.doubleValue())));
        this.encryptionMap.put('a', 'm');
        this.encryptionMap.put('b', 'n');
        this.encryptionMap.put('c', 'o');
        this.encryptionMap.put('d', 'p');
        this.encryptionMap.put('e', 'q');
        this.encryptionMap.put('f', 'r');
        this.encryptionMap.put('g', 's');
        this.encryptionMap.put('h', 't');
        this.encryptionMap.put('i', 'u');
        this.encryptionMap.put('j', 'v');
        this.encryptionMap.put('k', 'w');
        this.encryptionMap.put('l', 'x');
        this.encryptionMap.put('m', 'y');
        this.encryptionMap.put('n', 'z');
        this.encryptionMap.put('o', 'a');
        this.encryptionMap.put('p', 'b');
        this.encryptionMap.put('q', 'c');
        this.encryptionMap.put('r', 'd');
        this.encryptionMap.put('s', 'e');
        this.encryptionMap.put('t', 'f');
        this.encryptionMap.put('u', 'g');
        this.encryptionMap.put('v', 'h');
        this.encryptionMap.put('w', 'i');
        this.encryptionMap.put('x', 'j');
        this.encryptionMap.put('y', 'k');
        this.encryptionMap.put('z', 'l');

        // Numeric characters
        this.encryptionMap.put('0', '5');
        this.encryptionMap.put('1', '6');
        this.encryptionMap.put('2', '7');
        this.encryptionMap.put('3', '8');
        this.encryptionMap.put('4', '9');
        this.encryptionMap.put('5', '0');
        this.encryptionMap.put('6', '1');
        this.encryptionMap.put('7', '2');
        this.encryptionMap.put('8', '3');
        this.encryptionMap.put('9', '4');

        // Special characters
        this.encryptionMap.put('!', '@');
        this.encryptionMap.put('@', '#');
        this.encryptionMap.put('#', '$');
        this.encryptionMap.put('$', '%');
        this.encryptionMap.put('%', '^');
        this.encryptionMap.put('^', '&');
        this.encryptionMap.put('&', '*');
        this.encryptionMap.put('*', '(');
        this.encryptionMap.put('(', ')');
        this.encryptionMap.put(')', '!');

    }

    public SimpleDoubleProperty getHeight() {
        return this.height;
    }

    public SimpleDoubleProperty getWeight() {
        return this.weight;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public void updateBMI(DailyLog d) {

        d.setDailyBMI(this.bmi);
    }

    public SimpleStringProperty encrypt(SimpleStringProperty password) {
        String encryptString = "";
        for (int i = 0; i < password.get().length(); i++) {
            char currChar = password.get().charAt(i);
            encryptString += this.encryptionMap.get(currChar);
        }
        SimpleStringProperty encryptPassword = new SimpleStringProperty(encryptString);
        return encryptPassword;
    }

    private SimpleBooleanProperty decrypt(SimpleStringProperty password) {
        SimpleBooleanProperty matchPass = new SimpleBooleanProperty(
                encrypt(password).getValue().equals(this.password.getValue()));
        return matchPass;
    }

    @Override
    public void initializeAccount() {
        this.password = encrypt(this.password);
    }

    @Override
    public SimpleBooleanProperty authorized(SimpleStringProperty password) {
        return decrypt(password);
    }

    public SimpleStringProperty getName() {
        return this.username;
    }

    public CalculateExcerciseCalories getCaloriesCalculation() {
        return this.caloriesCalculation;
    }

    public FitnessHistory getFitnessHistory() {
        return this.history;
    }

    public void viewHistory() {
        this.history.viewFitnessHistory(this.caloriesCalculation);
    }

    public void burnCalories(SimpleDoubleProperty calories) {
        this.calorieBalance.subtract(calories.doubleValue());
    }

    public ArrayList<PhysicalMonitor> getPhysicalList(DailyLog daily) {
        ArrayList<PhysicalMonitor> m = new ArrayList<>();
        for (Feature f : daily.getFeatures()) {
            if (f instanceof PhysicalMonitor) {
                m.add((PhysicalMonitor) f);
            }
        }
        return m;
    }

    public void calculateCalories(DailyLog d) {
        double calBurn = 0;
        if (d.getTotalExcercises() != null) {
            for (Exercise e : d.getTotalExcercises()) {
                if (getCaloriesCalculation() == CalculateExcerciseCalories.PER_EXCERCISE) {
                    calBurn += (e.getExerciseType().METvalue.getValue() * this.getWeight().getValue())
                            / (60 * e.getExerciseType().amountOfExercisePerMinute.getValue()) * e.getCount().getValue();
                } else {
                    calBurn += e.getExerciseType().METvalue.getValue() * this.getWeight().doubleValue()
                            * e.getHours().doubleValue();
                }
            }
        }
        for (PhysicalMonitor f : getPhysicalList(d)) {
            ((PhysicalMonitor) f).setCaloriesBurnt(new SimpleDoubleProperty(calBurn));
        }
    }

    void calculateImprovement(DailyLog d, Goal g) {
        adjustWeight(d);
        d.setDailyBMI(this.bmi);
        int position = this.history.getHistory().indexOf(d);
        int previousPos = position - 1;

        if (position > 0) {
            SimpleDoubleProperty currentBMI = this.history.getHistory().get(position).getBMI();
            SimpleDoubleProperty previousBMI = this.history.getHistory().get(previousPos).getBMI();
            double improve = (currentBMI.doubleValue() - previousBMI.doubleValue()) / 100;
            SimpleDoubleProperty improvement = new SimpleDoubleProperty(improve);
            SimpleDoubleProperty improvementGain = new SimpleDoubleProperty(-improve);
            SimpleDoubleProperty improvementMaintain = new SimpleDoubleProperty(Math.abs(improve) * -1);
            if (g.equals(Goal.LOSE_WEIGHT)) {
                d.setImprovementPercentage(improvementGain);// negative improvement is good for weight loss
            } else if (g.equals(Goal.GAIN_WEIGHT)) {
                d.setImprovementPercentage(improvement); // positive improvement is good for weight gain
            } else if (g.equals(Goal.MAINTAIN_WEIGHT)) {
                d.setImprovementPercentage(improvementMaintain); // any change is considered negative for
                                                                 // maintaining weight
            }
        } else {
            d.setImprovementPercentage(new SimpleDoubleProperty(0)); // No previous log to compare with
        }
    }

    public void setCalories(DailyLog d) {
        double cal = 0;
        for (PhysicalMonitor pm : getPhysicalList(d)) {
            cal += (d.caloriesComsume.getValue() - pm.getCaloriesBurnt().getValue() - d.getGeneralCal().getValue());
        }
        this.calorieBalance.add(cal);
    }

    public void adjustWeight(DailyLog daily) {
        setCalories(daily);
        double weightChangeKg = calorieBalance.getValue() * 3500 / 0.45;
        this.weight.add(weightChangeKg);
        // this.calorieBalance %= 3500; // Keep the remainder for future calculations

        // Update BMI in the most recent DailyLog
        this.updateBMI(daily);
    }

    public void changePassword(SimpleStringProperty password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return super.toString() + ". BMI: " + this.bmi;
    }
}

interface UserAuthentication {
    void initializeAccount();

    SimpleBooleanProperty authorized(SimpleStringProperty password);
}

abstract class User {
    SimpleStringProperty username;
    SimpleStringProperty password;
    SimpleStringProperty email;

    User(SimpleStringProperty username, SimpleStringProperty password, SimpleStringProperty email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String toString() {
        return "User name: " + this.username + ". Email:" + this.email;
    }
}

enum Goal {
    LOSE_WEIGHT,
    GAIN_WEIGHT,
    MAINTAIN_WEIGHT
}

enum CalculateExcerciseCalories {
    PER_EXCERCISE,
    DURATION_OF_EXCERCISE
}

class DailyLog {
    private SimpleIntegerProperty days;
    private SimpleIntegerProperty months;
    private SimpleIntegerProperty year;
    static final Comparator<DailyLog> comparatorForDates = Comparator
            .comparing(DailyLog::getIntYear)
            .thenComparing(DailyLog::getIntMonth)
            .thenComparing(DailyLog::getIntDay);
    public SimpleDoubleProperty caloriesComsume;
    private final SimpleStringProperty date = new SimpleStringProperty();
    private List<Feature> features;
    private SimpleDoubleProperty improvementPercentage;
    private SimpleDoubleProperty dailyBMI = new SimpleDoubleProperty(0);
    private SimpleDoubleProperty generalCalBurn;

    DailyLog(SimpleIntegerProperty days, SimpleIntegerProperty months, SimpleIntegerProperty year) {
        this.days = days;
        this.months = months;
        this.year = year;
        SimpleStringProperty dateString = new SimpleStringProperty(
                this.days.getValue() + "/" + this.months.getValue() + "/" + this.year.getValue());
        this.date.bind(dateString);
        this.features = new ArrayList<Feature>();// use polymorphism here
        this.caloriesComsume = new SimpleDoubleProperty(2000);
        this.generalCalBurn = new SimpleDoubleProperty(0);
    }

    public SimpleDoubleProperty getBMI() {
        return this.dailyBMI;
    }

    public SimpleDoubleProperty getGeneralCal() {
        return this.generalCalBurn;
    }

    public void burnCalories(double cal) {
        this.generalCalBurn.subtract(cal);
    }

    public void setImprovementPercentage(SimpleDoubleProperty improve) {
        this.improvementPercentage = improve;
    }

    public SimpleDoubleProperty getImprovementPercentage() {
        return this.improvementPercentage;
    }

    public void setDailyBMI(SimpleDoubleProperty bmi) {
        this.dailyBMI = bmi;
    }

    public void addFeature(Exercise exercises) {
        PhysicalMonitor pm = new PhysicalMonitor();
        pm.addExercise(exercises);
        this.features.add(pm);
    }

    public void addFeature(SimpleDoubleProperty hours) {
        StressMonitor stress = new StressMonitor(hours);
        this.features.add(stress);
    }

    public void addCalories(double cal) {
        this.caloriesComsume.subtract(cal);
    }

    // accessor method
    SimpleStringProperty getDate() {
        return this.date;
    }

    // accessor method
    SimpleIntegerProperty getDays() {
        return this.days;
    }

    // accessor method
    SimpleIntegerProperty getMonths() {
        return this.months;
    }

    // accessor method
    SimpleIntegerProperty getYear() {
        return this.year;
    }

    int getIntYear() {
        return this.year.get();
    }

    int getIntMonth() {
        return this.months.get();
    }

    int getIntDay() {
        return this.days.get();
    }

    List<Feature> getFeatures() {
        return this.features;
    }

    // this method allows the user to view a specific day from the history array
    public ArrayList<Exercise> getTotalExcercises() {
        ArrayList<Exercise> arr = new ArrayList<>();
        for (Feature feature : this.features) {
            if (feature instanceof PhysicalMonitor) {
                arr.add(((PhysicalMonitor) feature).getExercises());
            }
        }
        return arr;
    }

    public void viewDailyLog(CalculateExcerciseCalories calculate) {
        System.out.println("--------------------------------");
        System.out.println("Date: " + this.date);
        System.out.println("BMI: " + this.dailyBMI);
        System.out.println("Improvement Percentage: " + this.improvementPercentage + "%");
        SimpleDoubleProperty totalCal = new SimpleDoubleProperty();
        for (Feature feature : this.features) {
            if (feature instanceof PhysicalMonitor) {
                totalCal.add(((PhysicalMonitor) feature).getCaloriesBurnt());
            }
        }
        System.out.println("Calories Burnt from Exercises: " + totalCal.doubleValue());

        System.out.println("Exercises: ");

        if (this.getTotalExcercises() != null && !this.getTotalExcercises().isEmpty()) {
            for (Exercise e : this.getTotalExcercises()) {
                if (calculate == CalculateExcerciseCalories.PER_EXCERCISE) {
                    System.out.println("  - " + e.getExerciseType().name + ": \n     - Repetition: " + e.getCount());
                } else {
                    System.out.println("  - " + e.getExerciseType().name + ": \n     - Hours: " + e.getHours());
                }
            }
        } else {
            System.out.println("No exercises recorded for this day.");
        }
        SimpleDoubleProperty totalSleep = new SimpleDoubleProperty();
        for (Feature feature : this.features) {
            if (feature instanceof StressMonitor) {
                totalSleep.add(((StressMonitor) feature).getSleep());
            }
        }

        System.out.println("Sleep Hours: " + totalSleep);
        System.out.println("--------------------------------");

    }

    public String toString() {
        return "Date: " + this.date + ". BMI: " + this.dailyBMI + ". Improvement Percentage:"
                + this.improvementPercentage + "%";
    }
}

class FitnessHistory {
    // holds the history of the user
    private List<DailyLog> history;
    private SimpleIntegerProperty currentIndex = new SimpleIntegerProperty(0);

    FitnessHistory() {
        this.history = new ArrayList<>();
    }

    public SimpleIntegerProperty getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(SimpleIntegerProperty current) {
        this.currentIndex = current;
    }

    List<DailyLog> getHistory() {
        return this.history;
    }

    // method to add dailylogs to history

    void addOrUpdateDailyLog(DailyLog dailyLog) {
        for (int i = 0; i < this.history.size(); i++) {
            if (this.history.get(i).getDate().equals(dailyLog.getDate())) {
                this.history.set(i, dailyLog);
                return;
            }
        }
        this.history.add(dailyLog);
    }

    public DailyLog getDailyLog(SimpleIntegerProperty day, SimpleIntegerProperty month, SimpleIntegerProperty year) {
        SimpleStringProperty date = new SimpleStringProperty(day + "/" + month + "/" + year);
        for (DailyLog log : this.history) {
            if (log.getDate().equals(date)) {
                return log;
            }
        }
        return null;
    }

    public DailyLog getDailyLog(DailyLog daily) {
        for (DailyLog dailyLog : this.history) {
            if (daily.getDate().equals(dailyLog.getDate())) {
                return dailyLog;
            }
        }
        return daily;
    }

    // method to allow the user to view all of the history but with less details
    void viewFitnessHistory(CalculateExcerciseCalories c) {
        if (this.history.isEmpty()) {
            System.out.println("No fitness history available.");
            return;
        }

        // sorts the history list using the comparator declared above
        Collections.sort(this.history, DailyLog.comparatorForDates);

        int index = 1;
        // iterating through dailylog objects
        for (DailyLog d : this.history) {
            System.out.println(index + ".");
            System.out.println("--------------------------------");
            System.out.println("Date: " + d.getDate());
            System.out.println("Improvement Percentage: " + d.getImprovementPercentage() + "%");
            if (d.getTotalExcercises() != null && !d.getTotalExcercises().isEmpty()) {
                for (Exercise e : d.getTotalExcercises()) {
                    if (c == CalculateExcerciseCalories.PER_EXCERCISE) {
                        System.out
                                .println("  • " + e.getExerciseType().name + ": \n     • Repetition: " + e.getCount());
                    } else {
                        System.out.println("  • " + e.getExerciseType().name + ": \n     • Hours: " + e.getHours());
                    }
                }
            } else {
                System.out.println("No exercises recorded for this day.");
            }
            System.out.println("--------------------------------");
            index++;
        }
    }

    public String toString() {
        return "Currently holding " + this.history.size() + " Daily Logs.";
    }
}