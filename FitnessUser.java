import java.util.*;

public class FitnessUser extends User implements UserAuthentication {
    private double weight;
    private double height;
    private Goal goal;
    private String password;
    private CalculateExcerciseCalories caloriesCalculation;
    private HashMap<Character, Character> encryptionMap = new HashMap<>();
    private FitnessHistory history = new FitnessHistory();
    private double calorieBalance = 0;
    private double bmi;

    FitnessUser(String username, String password, String email, double weight, double height, Goal goal,
            CalculateExcerciseCalories caloriesCalculation) {
        super(username, password, email);
        this.height = height;
        this.weight = weight;
        this.caloriesCalculation = caloriesCalculation;
        this.goal = goal;
        this.password = password;
        this.bmi = this.getWeight() / (this.getHeight() * this.getHeight());
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

    public double getHeight() {
        return this.height;
    }

    public double getWeight() {
        return this.weight;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public void updateBMI(double weight, double height, DailyLog d) {

        this.bmi = weight / (height * height);

        d.setDailyBMI(this.bmi);
    }

    public String encrypt(String password) {
        String encryptString = "";
        for (int i = 0; i < password.length(); i++) {
            char currChar = password.charAt(i);
            encryptString += this.encryptionMap.get(currChar);
        }
        return encryptString;
    }

    private boolean decrypt(String password) {
        boolean matchPass = encrypt(password).equals(this.password);
        return matchPass;
    }

    @Override
    public void initializeAccount() {
        this.password = encrypt(this.password);
    }

    @Override
    public boolean authorized(String password) {
        return decrypt(password);
    }

    public String getName() {
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
        this.calorieBalance -= calories;
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
                    calBurn += (e.getExerciseType().METvalue * this.getWeight())
                            / (60 * e.getExerciseType().amountOfExercisePerMinute) * e.getCount();
                } else {
                    calBurn += e.getExerciseType().METvalue * this.getWeight()
                            * e.getHours();
                }
            }
        }
        for (PhysicalMonitor f : getPhysicalList(d)) {
            ((PhysicalMonitor) f).setCaloriesBurnt(calBurn);
        }
    }

    void calculateImprovement(DailyLog d, Goal g) {
        adjustWeight(d);
        d.setDailyBMI(this.bmi);
        int position = this.history.getHistory().indexOf(d);
        int previousPos = position - 1;

        if (position > 0) {
            double currentBMI = this.history.getHistory().get(position).getBMI();
            double previousBMI = this.history.getHistory().get(previousPos).getBMI();
            double improvement = ((currentBMI - previousBMI) * 100 / previousBMI);

            if (g.equals(Goal.LOSE_WEIGHT)) {
                d.setImprovementPercentage(-improvement);// negative improvement is good for weight loss
            } else if (g.equals(Goal.GAIN_WEIGHT)) {
                d.setImprovementPercentage(improvement); // positive improvement is good for weight gain
            } else if (g.equals(Goal.MAINTAIN_WEIGHT)) {
                d.setImprovementPercentage(Math.abs(improvement) * -1); // any change is considered negative for
                                                                        // maintaining weight
            }
        } else {
            d.setImprovementPercentage(0); // No previous log to compare with
        }
    }

    public void setCalories(DailyLog d) {
        double cal = 0;
        for (PhysicalMonitor pm : getPhysicalList(d)) {
            cal += (d.caloriesComsume - pm.getCaloriesBurnt() - d.getGeneralCal());
        }
        this.calorieBalance = cal;
    }

    public void adjustWeight(DailyLog daily) {
        setCalories(daily);
        double weightChangeKg = (calorieBalance / 3500) * 0.45;
        this.weight += weightChangeKg;
        // this.calorieBalance %= 3500; // Keep the remainder for future calculations

        // Update BMI in the most recent DailyLog
        this.updateBMI(this.weight, this.height, daily);
    }

    @Override
    public String toString() {
        return super.toString() + ". BMI: " + this.bmi;
    }
}

interface UserAuthentication {
    void initializeAccount();

    boolean authorized(String password);
}

abstract class User {
    String username;
    String password;
    String email;

    User(String username, String password, String email) {
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
    private int days;
    private int months;
    private int year;
    static final Comparator<DailyLog> comparatorForDates = Comparator
            .comparing(DailyLog::getYear)
            .thenComparing(DailyLog::getMonths)
            .thenComparing(DailyLog::getDays);
    public double caloriesComsume;
    private String date = this.days + "/" + this.months + "/" + this.year;
    private List<Feature> features;
    private double improvementPercentage;
    private double dailyBMI = 0;
    private double generalCalBurn;

    DailyLog(int days, int months, int year) {
        this.days = days;
        this.months = months;
        this.year = year;
        this.date = this.days + "/" + this.months + "/" + this.year;
        this.features = new ArrayList<Feature>();// use polymorphism here
        this.caloriesComsume = 2000;
        this.generalCalBurn = 0;
    }

    public double getBMI() {
        return this.dailyBMI;
    }

    public double getGeneralCal() {
        return this.generalCalBurn;
    }

    public void burnCalories(double cal) {
        this.generalCalBurn += cal;
    }

    public void setImprovementPercentage(double improve) {
        this.improvementPercentage = improve;
    }

    public double getImprovementPercentage() {
        return this.improvementPercentage;
    }

    public void setDailyBMI(double bmi) {
        this.dailyBMI = bmi;
    }

    public void addFeature(Exercise exercises) {
        PhysicalMonitor pm = new PhysicalMonitor();
        pm.addExercise(exercises);
        this.features.add(pm);
    }

    public void addFeature(double hours) {
        StressMonitor stress = new StressMonitor(hours);
        this.features.add(stress);
    }

    public void addCalories(double cal) {
        this.caloriesComsume += cal;
    }

    // accessor method
    String getDate() {
        return this.date;
    }

    // accessor method
    int getDays() {
        return this.days;
    }

    // accessor method
    int getMonths() {
        return this.months;
    }

    // accessor method
    int getYear() {
        return this.year;
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
        double totalCal = 0;
        for (Feature feature : this.features) {
            if (feature instanceof PhysicalMonitor) {
                totalCal += ((PhysicalMonitor) feature).getCaloriesBurnt();
            }
        }
        System.out.println("Calories Burnt from Exercises: " + totalCal);

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
        double totalSleep = 0;
        for (Feature feature : this.features) {
            if (feature instanceof StressMonitor) {
                totalSleep += ((StressMonitor) feature).getSleep();
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
    private int currentIndex = 0;

    FitnessHistory() {
        this.history = new ArrayList<>();
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int current) {
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

    public DailyLog getDailyLog(int day, int month, int year) {
        String date = day + "/" + month + "/" + year;
        for (DailyLog log : this.history) {
            if (log.getDate().equals(date)) {
                return log;
            }
        }
        return null;
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