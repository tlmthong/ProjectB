import javafx.beans.property.*;;;

enum ExerciseType { // enum to represent various exercise types

    PUSH_UPS("Push-ups", new SimpleIntegerProperty(8), new SimpleIntegerProperty(20), new SimpleIntegerProperty(0)), // push-ups
    SIT_UPS("Sit-ups", new SimpleIntegerProperty(6), new SimpleIntegerProperty(30), new SimpleIntegerProperty(1)), // sit_ups
    STAR_JUMPS("Star Jumps", new SimpleIntegerProperty(8), new SimpleIntegerProperty(40), new SimpleIntegerProperty(2)), // star-jumps
    TRICEP_DIPS("Tricep Dips", new SimpleIntegerProperty(5), new SimpleIntegerProperty(20),
            new SimpleIntegerProperty(3)), // tricep-dips
    SQUATS("Squats", new SimpleIntegerProperty(5), new SimpleIntegerProperty(25), new SimpleIntegerProperty(4)), // squats
    BURPEES("Burpees", new SimpleIntegerProperty(10), new SimpleIntegerProperty(10), new SimpleIntegerProperty(6)), // burpees
    PULL_UPS("Pull-ups", new SimpleIntegerProperty(8), new SimpleIntegerProperty(10), new SimpleIntegerProperty(7)), // pull-ups
    CHIN_UPS("Chin-ups", new SimpleIntegerProperty(8), new SimpleIntegerProperty(10), new SimpleIntegerProperty(10)); // chin-ups

    String name; // string value for each exercise to represent its name
    SimpleIntegerProperty METvalue; // this is a value that represents the energy cost of physical activites
    SimpleIntegerProperty amountOfExercisePerMinute; // the amount of repetitions per minute, this is needed to calulate
                                                     // the calories
    // burnt for repetitions
    SimpleIntegerProperty identifierID; // a way to identify the exercise

    ExerciseType(String name, SimpleIntegerProperty METvalue, SimpleIntegerProperty amountOfExercisePerMinute,
            SimpleIntegerProperty identifierID) { // to initialise the
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
    protected SimpleIntegerProperty id; // user attribute to assign each feature for specific user
    protected static SimpleIntegerProperty nextID = new SimpleIntegerProperty(0);

    Feature() {
        this.id = nextID;
        this.nextID.add(1);
    }

    public String toString() {
        return "This feature has the id is" + this.id;
    }
}

class Exercise {
    private SimpleDoubleProperty hours; // hours of each exercise done
    private SimpleIntegerProperty count; // count of each exericse done
    private ExerciseType exerciseType; // the exercise type

    // intialise attributes
    Exercise(SimpleDoubleProperty hours, SimpleIntegerProperty count, ExerciseType exerciseType) {
        this.hours = hours;
        this.count = count;
        this.exerciseType = exerciseType;
    }

    void addHours(double hours) {
        this.hours.add(hours);
    }

    void addCount(SimpleIntegerProperty count) {
        this.count.add(count);
    }

    // accessor method for hours
    SimpleDoubleProperty getHours() {
        return this.hours;
    }

    // accessor method for count
    SimpleIntegerProperty getCount() {
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
    private SimpleDoubleProperty caloriesBurnt; // total calories burnt

    // initialises attributes
    PhysicalMonitor() {
        this.caloriesBurnt = new SimpleDoubleProperty(0);
    }

    public SimpleDoubleProperty getCaloriesBurnt() {
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
    public void setCaloriesBurnt(SimpleDoubleProperty cal) {
        this.caloriesBurnt = cal;
    }

    public String toString() {
        return "this record is hold for " + this.exercises + " burnt " + this.caloriesBurnt;
    }

}

class StressMonitor extends Feature implements SleepTracker {
    private SimpleDoubleProperty hoursOfSleep;

    StressMonitor(SimpleDoubleProperty hoursOfSleep) {
        super();
        this.hoursOfSleep = hoursOfSleep;
    }

    // overrides the methods from SleepTracker
    @Override
    // tracks sleep hours
    public void trackSleepHours(double hours) {
        hoursOfSleep.add(hours);
    }

    public SimpleDoubleProperty getSleep() {
        return this.hoursOfSleep;
    }

    public String toString() {
        return "this sleep duration is" + this.hoursOfSleep;
    }
}
