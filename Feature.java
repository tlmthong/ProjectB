enum ExerciseType { // enum to represent various exercise types

    PUSH_UPS("Push-ups", 8, 20, 0000), // push-ups
    SIT_UPS("Sit-ups", 6, 30, 0001), // sit_ups
    STAR_JUMPS("Star Jumps", 8, 40, 0002), // star-jumps
    TRICEP_DIPS("Tricep Dips", 5, 20, 0003), // tricep-dips
    SQUATS("Squats", 5, 25, 0004), // squats
    BURPEES("Burpees", 10, 10, 0005), // burpees
    PULL_UPS("Pull-ups", 8, 10, 0007), // pull-ups
    CHIN_UPS("Chin-ups", 8, 10, 0010); // chin-ups

    String name; // string value for each exercise to represent its name
    int METvalue; // this is a value that represents the energy cost of physical activites
    int amountOfExercisePerMinute; // the amount of repetitions per minute, this is needed to calulate the calories
                                   // burnt for repetitions
    int identifierID; // a way to identify the exercise

    ExerciseType(String name, int METvalue, int amountOfExercisePerMinute, int identifierID) { // to initialise the
                                                                                               // attribute values
        this.name = name;
        this.METvalue = METvalue;
        this.amountOfExercisePerMinute = amountOfExercisePerMinute;
        this.identifierID = identifierID;
    }

    public String toString() {
        return "This excercise is " + this.name;
    }
}

// interface for exercise methods
interface ExerciseTracker {
    void addExercise(Exercise exericse); // add exercises to exercise arraylist
}

// interface for sleep methods
interface SleepTracker {
    void trackSleepHours(double hours); // tracks amount of hours of sleep
}

class Feature {
    protected int id; // user attribute to assign each feature for specific user
    protected static int nextID = 0;

    Feature() {
        this.id = nextID;
        this.nextID += 1;
    }

    public String toString() {
        return "This feature has the id is" + this.id;
    }
}

class Exercise {
    private double hours; // hours of each exercise done
    private int count; // count of each exericse done
    private ExerciseType exerciseType; // the exercise type

    // intialise attributes
    Exercise(double hours, int count, ExerciseType exerciseType) {
        this.hours = hours;
        this.count = count;
        this.exerciseType = exerciseType;
    }

    void addHours(double hours) {
        this.hours += hours;
    }

    void addCount(int count) {
        this.count += count;
    }

    // accessor method for hours
    double getHours() {
        return this.hours;
    }

    // accessor method for count
    int getCount() {
        return this.count;
    }

    // accessor method for exercise type
    ExerciseType getExerciseType() {
        return this.exerciseType;
    }

    public String toString() {
        return this.exerciseType.toString() + "Duration: " + this.hours + "Count: " + this.count;
    }
}

class PhysicalMonitor extends Feature implements ExerciseTracker {
    private Exercise exercises; // holds all exercises done
    private double caloriesBurnt; // total calories burnt

    // initialises attributes
    PhysicalMonitor() {
        this.exercises = new Exercise(0, 0, null);
        this.caloriesBurnt = 0;
    }

    public double getCaloriesBurnt() {
        return this.caloriesBurnt;
    }

    public Exercise getExercises() {
        return this.exercises;
    }

    // overrides the methods from ExerciseTracker
    @Override
    // method to add exercises to the exercises done list
    public void addExercise(Exercise exercise) {
        this.exercises = exercise;
    }

    // tracks calories burnt from exercises by each repetition
    public void setCaloriesBurnt(double cal) {
        this.caloriesBurnt = cal;
    }

    public String toString() {
        return "this record is hold for " + this.exercises + " burnt " + this.caloriesBurnt;
    }

}

class StressMonitor extends Feature implements SleepTracker {
    private double hoursOfSleep;

    StressMonitor(double hoursOfSleep) {
        super();
        this.hoursOfSleep = hoursOfSleep;
    }

    // overrides the methods from SleepTracker
    @Override
    // tracks sleep hours
    public void trackSleepHours(double hours) {
        hoursOfSleep += hours;
    }

    public double getSleep() {
        return this.hoursOfSleep;
    }

    public String toString() {
        return "this sleep duration is" + this.hoursOfSleep;
    }
}
