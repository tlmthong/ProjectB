import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class User {
    Systems sys;

    User(Systems sys) {
        this.sys = sys;
    }

    public void createAccount(SimpleStringProperty username, SimpleStringProperty password, SimpleStringProperty email,
            SimpleDoubleProperty weight, SimpleDoubleProperty height, Goal goal,
            CalculateExcerciseCalories caloriesCalculation) {
        sys.addAccount(username.getValue(), password.getValue(), email.getValue(), weight.doubleValue(),
                height.doubleValue(), goal, caloriesCalculation);
    }

    public SimpleBooleanProperty logIn(SimpleStringProperty username, SimpleStringProperty password) {
        SimpleBooleanProperty log = new SimpleBooleanProperty(sys.logIn(username.getValue(), password.getValue()));
        return log;
    }

    public SimpleBooleanProperty logIn(SimpleIntegerProperty username, SimpleStringProperty password) {
        SimpleBooleanProperty log = new SimpleBooleanProperty(sys.logIn(username.getValue(), password.getValue()));
        return log;
    }

    public SimpleStringProperty getName() {
        String name = sys.currentUser.getName();
        SimpleStringProperty returnMessage = new SimpleStringProperty(name);
        return returnMessage;
    }

}