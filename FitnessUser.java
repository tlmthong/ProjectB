import java.util.*;
import javafx.beans.property.*;

public class FitnessUser extends User implements UserAuthentication {
    private final SimpleDoubleProperty weight = new SimpleDoubleProperty();
    private final SimpleDoubleProperty height = new SimpleDoubleProperty();
    private Goal goal;
    private final SimpleStringProperty password = new SimpleStringProperty();
    private CalculateExcerciseCalories caloriesCalculation;
    private HashMap<Character, Character> encryptionMap = new HashMap<>();
    private FitnessHistory history = new FitnessHistory();
    private final SimpleDoubleProperty calorieBalance = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty bmi = new SimpleDoubleProperty();
    public final SimpleDoubleProperty initialBMI = new SimpleDoubleProperty();
    public final SimpleDoubleProperty initialWeight = new SimpleDoubleProperty();

    FitnessUser(String username, String password, String email, double weight, double height, Goal goal,
            CalculateExcerciseCalories caloriesCalculation) {
        super(username, password, email);
        this.height.set(height);
        this.weight.set(weight);
        this.caloriesCalculation = caloriesCalculation;
        this.goal = goal;
        this.password.set(password);
        this.bmi.set(this.weight.doubleValue() / ((this.height.doubleValue() * this.height.doubleValue())));
        this.weight.addListener((obs, old, newWeight) -> {
            this.bmi.set(newWeight.doubleValue() / ((this.height.doubleValue() * this.height.doubleValue())));
        });
        ;
        this.initialBMI.set(this.bmi.doubleValue());
        this.initialWeight.set(this.weight.doubleValue());
        ;
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

    public void reformatHistory() {
        ArrayList<DailyLog> oldArray = this.getFitnessHistory().sortedHistory();
        FitnessHistory formattedFitnessHistory = new FitnessHistory();
        this.setInitalBMI();
        this.setInitalWeight();
        this.setHistory(formattedFitnessHistory);
        for (DailyLog dailyLog : oldArray) {
            DailyLog workingDaily = new DailyLog(dailyLog.getDays().get(), dailyLog.getMonths().get(),
                    dailyLog.getYear().get());
            getFitnessHistory().addOrUpdateDailyLog(workingDaily);
            for (Feature feature : dailyLog.getFeatures()) {
                if (feature instanceof PhysicalMonitor) {
                    workingDaily.addFeature(((PhysicalMonitor) feature).getExercises());
                    calculateCalories(workingDaily);
                    calculateImprovement(workingDaily, getGoal());
                } else {
                    workingDaily.addFeature(feature);
                }
            }
        }
    }

    public CalculateExcerciseCalories getCalculate() {
        return this.caloriesCalculation;
    }

    public void setCalories(CalculateExcerciseCalories calculateExcerciseCalories) {
        this.caloriesCalculation = calculateExcerciseCalories;
    }

    public void setHistory(FitnessHistory history) {
        this.history = history;
    }

    public void setInitalBMI() {
        this.bmi.set(this.initialBMI.doubleValue());
    }

    public void setInitalWeight() {
        this.weight.set(this.initialWeight.doubleValue());
    }

    public SimpleDoubleProperty getInitialWeight() {
        return this.initialWeight;
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

    public void setBmi(double bmi) {
        this.bmi.set(bmi);
        ;
    }

    // public void updateBMI(double weight, DailyLog daily) {
    // this.bmi.set(weight / ((height.doubleValue() * height.doubleValue())));

    // daily.setDailyBMI(this.bmi.doubleValue());
    // }

    public String encrypt(String password) {
        String encryptString = "";
        for (int i = 0; i < password.length(); i++) {
            char currChar = password.charAt(i);
            encryptString += this.encryptionMap.get(currChar);
        }
        return encryptString;
    }

    private SimpleBooleanProperty decrypt(String password) {
        SimpleBooleanProperty matchPass = new SimpleBooleanProperty(
                encrypt(password).equals(this.password.getValue()));
        return matchPass;
    }

    @Override
    public void initializeAccount() {
        this.password.set(encrypt(this.password.getValue()));
    }

    @Override
    public SimpleBooleanProperty authorized(String password) {
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

    public void burnCalories(double calories) {
        this.calorieBalance.subtract(calories);
    }

    public ArrayList<PhysicalMonitor> getPhysicalList(DailyLog daily) {
        ArrayList<PhysicalMonitor> monitor = new ArrayList<>();
        for (Feature f : daily.getFeatures()) {
            if (f instanceof PhysicalMonitor) {
                monitor.add((PhysicalMonitor) f);
            }
        }
        return monitor;
    }

    public void calculateCalories(DailyLog d) {
        ArrayList<Double> calBurn = new ArrayList<>();
        if (d.getTotalExcercises() != null) {
            for (Exercise exercise : d.getTotalExcercises()) {
                if (getCaloriesCalculation() == CalculateExcerciseCalories.PER_EXCERCISE) {
                    calBurn.add((exercise.getExerciseType().METvalue.getValue() * this.getWeight().getValue())
                            / (60 * exercise.getExerciseType().amountOfExercisePerMinute.getValue())
                            * exercise.getCount().getValue());
                } else {
                    calBurn.add(exercise.getExerciseType().METvalue.getValue() * this.getWeight().doubleValue()
                            * exercise.getHours().doubleValue());
                }
            }
        }
        System.out.println("cal" + calBurn);
        for (PhysicalMonitor physicalMonitor : getPhysicalList(d)) {
            ((PhysicalMonitor) physicalMonitor)
                    .setCaloriesBurnt(calBurn.get(getPhysicalList(d).indexOf(physicalMonitor)));
        }
    }

    void calculateImprovement(DailyLog daily, Goal goal) {
        adjustWeight(daily);
        daily.setDailyBMI(this.bmi.doubleValue());
        int position = this.history.getHistory().indexOf(daily);
        int previousPos = position - 1;
        if (position > 0) {
            SimpleDoubleProperty currentBMI = this.history.getHistory().get(position).getBMI();
            SimpleDoubleProperty previousBMI = this.history.getHistory().get(previousPos).getBMI();
            double improve = ((currentBMI.doubleValue() - previousBMI.doubleValue()) / previousBMI.doubleValue()) * 100;
            if (goal.equals(Goal.LOSE_WEIGHT)) {
                daily.setImprovementPercentage(improve);// negative improvement is good for weight loss
            } else if (goal.equals(Goal.GAIN_WEIGHT)) {
                daily.setImprovementPercentage(-improve); // positive improvement is good for weight gain
            } else if (goal.equals(Goal.MAINTAIN_WEIGHT)) {
                daily.setImprovementPercentage(Math.abs(improve)); // any change is considered negative for
                // maintaining weight
            }
        } else {
            daily.setImprovementPercentage(0); // No previous log to compare with
            System.out.println("no");

        }
        System.out.println(daily.getBMI().getValue());
    }

    public void setCalories(DailyLog dailyLog) {
        double cal = 0;
        for (PhysicalMonitor pm : getPhysicalList(dailyLog)) {
            cal += (dailyLog.caloriesComsume.getValue() - pm.getCaloriesBurnt().getValue()
                    - dailyLog.getGeneralCal().getValue());
        }
        this.calorieBalance.set(cal);
    }

    public void adjustWeight(DailyLog daily) {
        setCalories(daily);
        double weightChangeKg = (calorieBalance.getValue() * 45) / 350000;
        double newWeight = this.weight.getValue() + weightChangeKg;
        this.weight.set(newWeight);
        ;
    }

    public void changePassword(String password) {
        this.password.set(password);
        ;
    }

    @Override
    public String toString() {
        return super.toString() + ". BMI: " + this.bmi.getValue();
    }
}

interface UserAuthentication {
    void initializeAccount();

    SimpleBooleanProperty authorized(String password);
}

abstract class User {
    final SimpleStringProperty username = new SimpleStringProperty();
    final SimpleStringProperty password = new SimpleStringProperty();
    final SimpleStringProperty email = new SimpleStringProperty();

    User(String username, String password, String email) {
        this.username.set(username);
        ;
        this.password.set(password);
        this.email.set(email);
    }

    public String toString() {
        return "User name: " + this.username.getValue() + ". Email:" + this.email.getValue();
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
    private final SimpleIntegerProperty days = new SimpleIntegerProperty();
    private final SimpleIntegerProperty months = new SimpleIntegerProperty();
    private final SimpleIntegerProperty year = new SimpleIntegerProperty();
    static final Comparator<DailyLog> comparatorForDates = Comparator
            .comparing(DailyLog::getIntYear)
            .thenComparing(DailyLog::getIntMonth)
            .thenComparing(DailyLog::getIntDay);
    public final SimpleDoubleProperty caloriesComsume;
    private final SimpleStringProperty date = new SimpleStringProperty();
    private List<Feature> features;
    private final SimpleDoubleProperty improvementPercentage = new SimpleDoubleProperty();
    private final SimpleDoubleProperty dailyBMI = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty generalCalBurn;

    DailyLog(int days, int months, int year) {
        this.days.set(days);
        this.months.set(months);
        this.year.set(year);
        String dateString = this.days.getValue() + "/" + this.months.getValue() + "/" + this.year.getValue();
        this.date.set(dateString);
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

    public void setImprovementPercentage(double improve) {
        this.improvementPercentage.set(improve);
        ;
    }

    public SimpleDoubleProperty getImprovementPercentage() {
        return this.improvementPercentage;
    }

    public void setDailyBMI(double bmi) {
        this.dailyBMI.set(bmi);
        ;
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

    public void setFeature(Exercise exercise) {
        System.out.println("reached");
        for (Feature feature : features) {
            if (feature instanceof PhysicalMonitor) {
                System.out.println("reached");
                if (((PhysicalMonitor) feature).getExercises().getExerciseType().name
                        .equals(exercise.getExerciseType().name)) {
                    System.out.println(exercise.getHours() + " " + exercise.getCount());
                    PhysicalMonitor physicalMonitor = new PhysicalMonitor();
                    physicalMonitor.addExercise(exercise);
                    features.remove(feature);
                    features.add(physicalMonitor);
                    System.out.println(((PhysicalMonitor) feature).getExercises().getHours());
                    return;
                }
            }
        }
        throw new Error("can't find");
    }

    public void addFeature(Feature feature) {
        this.features.add(feature);
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
        return this.year.getValue();
    }

    int getIntMonth() {
        return this.months.getValue();
    }

    int getIntDay() {
        return this.days.getValue();
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

    public SimpleStringProperty viewDailyLog(CalculateExcerciseCalories calculate) {
        String detail = "";
        detail += "Date: " + this.getDate().get() + "\n";
        detail += "BMI: " + this.getBMI().get() + "\n";
        detail += "Improvement Percentage: " + this.improvementPercentage.get() + "%" + "\n";
        SimpleDoubleProperty totalCal = new SimpleDoubleProperty();
        for (Feature feature : this.features) {
            if (feature instanceof PhysicalMonitor) {
                totalCal.add(((PhysicalMonitor) feature).getCaloriesBurnt());
            }
        }
        detail += "Calories Burnt from Exercises: " + totalCal.doubleValue() + "\n";

        System.out.println("Exercises: ");

        if (this.getTotalExcercises() != null && !this.getTotalExcercises().isEmpty()) {
            for (Exercise e : this.getTotalExcercises()) {
                if (calculate == CalculateExcerciseCalories.PER_EXCERCISE) {
                    detail += "  - " + e.getExerciseType().name + ": \n     - Repetition: " + e.getCount().get() + "\n";
                } else {
                    detail += "  - " + e.getExerciseType().name + ": \n     - Hours: " + e.getHours().get() + "\n";
                }
            }
        } else {
            System.out.println("No exercises recorded for this day.");
        }
        Double totalSleep = 0.0;
        for (Feature feature : this.features) {
            if (feature instanceof StressMonitor) {
                totalSleep += ((StressMonitor) feature).getSleep().getValue();
            }
        }

        detail += "Sleep Hours: " + totalSleep;
        return new SimpleStringProperty(detail);

    }

    public ArrayList<PhysicalMonitor> getPhysicalMonitors() {
        ArrayList<PhysicalMonitor> physicalMonitors = new ArrayList<>();
        for (Feature feature : this.features) {
            if (feature instanceof PhysicalMonitor) {
                physicalMonitors.add(((PhysicalMonitor) feature));
            }
        }
        return physicalMonitors;
    }

    public SimpleStringProperty getListExercise(CalculateExcerciseCalories cal) {
        String list = "";
        for (PhysicalMonitor physical : getPhysicalMonitors()) {
            Exercise exercise = physical.getExercises();
            if (cal == CalculateExcerciseCalories.PER_EXCERCISE) {
                list += "- " + exercise.getExerciseType().name + ". Count: " + exercise.getCount().getValue() + "\n";
            } else {
                list += "- " + exercise.getExerciseType().name + ". Hours: " + exercise.getHours().getValue() + "\n";
            }
        }
        return new SimpleStringProperty(list);
    }

    public String toString() {
        return "Date: " + this.date.get();
    }
}

class FitnessHistory {
    // holds the history of the user
    private ArrayList<DailyLog> history;
    private final SimpleIntegerProperty currentIndex = new SimpleIntegerProperty(0);

    FitnessHistory() {
        this.history = new ArrayList<>();
    }

    public SimpleIntegerProperty getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int current) {
        this.currentIndex.set(current);
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

    public DailyLog getDailyLog(int day, int month, int year) {
        for (DailyLog log : this.history) {
            if (day == log.getDays().getValue() && month == log.getMonths().getValue()
                    && year == log.getYear().getValue()) {
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

    public ArrayList<DailyLog> sortedHistory() {
        Collections.sort(this.history, DailyLog.comparatorForDates);
        return this.history;
    }

    // method to allow the user to view all of the history but with less details
    void viewFitnessHistory(CalculateExcerciseCalories c) { // ATENTION: THIS METHOD ONLY USED FOR DEBUG AND TESTING
                                                            // PURPOSE
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

class AdminUser extends User {
    Systems supervisedSystem;

    AdminUser(Systems supervisedSystem, String username, String password,
            String email) {
        super(username, password, email);
        this.supervisedSystem = supervisedSystem;
    }

    public ArrayList<FitnessUser> getAllLoggedInAccount() {
        return this.supervisedSystem.getAllFitnessUsers();
    }

    public boolean VerifyKey(SimpleStringProperty key) {
        return key.get().equals(this.password.get());
    }

}

class CaloriesCalulator {
    private ExerciseType exerciseType = ExerciseType.PUSH_UPS;
    private final SimpleDoubleProperty hours = new SimpleDoubleProperty();
    private final SimpleIntegerProperty repetition = new SimpleIntegerProperty();
    private final SimpleDoubleProperty calBurnHour = new SimpleDoubleProperty();
    private final SimpleDoubleProperty calBurnRep = new SimpleDoubleProperty();
    private final SimpleDoubleProperty weight = new SimpleDoubleProperty();

    CaloriesCalulator() {
        this.calBurnRep.bind((exerciseType.METvalue.multiply(weight))
                .divide(exerciseType.amountOfExercisePerMinute.multiply(60))
                .multiply(repetition));
        this.calBurnHour.bind((exerciseType.METvalue.multiply(weight)).multiply(hours));

    }

    SimpleDoubleProperty getHour() {
        return this.hours;
    }

    SimpleIntegerProperty getRep() {
        return this.repetition;
    }

    public SimpleDoubleProperty getCalorieHour() {
        return this.calBurnHour;
    }

    public SimpleDoubleProperty getCalorieRep() {
        return this.calBurnRep;
    }

    public void changeHour(final double hour) {
        this.hours.set(hour);
    }

    public void changeRep(final int rep) {
        this.repetition.set(rep);
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    public void changeExcerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }
}