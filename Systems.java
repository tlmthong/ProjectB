
import java.util.*;
import javafx.beans.property.*;

interface VerifyAccount {
    boolean logIn(String username, String password);

    void logOut(String username);

    void addAccount(String username, String password, String email, double weight, double height, Goal goal,
            CalculateExcerciseCalories caloriesCalculation);
}

class Systems implements VerifyAccount {
    private int availableId;
    private Map<Integer, FitnessUser> accounts; //
    private List<FitnessUser> signedInAccount; // Optimize better than Arraylist
    private FitnessUser currentUser;

    Systems() {
        this.accounts = new HashMap<>();
        this.signedInAccount = new LinkedList<>();
        this.availableId = 0;
    }

    public FitnessUser getCurrentUser() {
        return this.currentUser;
    }

    public void addAccount(String username, String password, String email, double weight, double height, Goal goal,
            CalculateExcerciseCalories caloriesCalculation) {
        Random ran = new Random();
        int increment = ran.nextInt(10) + 1;
        this.availableId += increment;
        this.accounts.put(availableId,
                new FitnessUser(username, password, email, weight, height, goal, caloriesCalculation));
        this.accounts.get(availableId).initializeAccount();
        System.out.println("Account created successfully. ID: " + this.availableId + " . Username: " + username
                + ". Email: " + email);
    }

    public boolean logIn(String username, String password) {
        for (Map.Entry<Integer, FitnessUser> entry : this.accounts.entrySet()) {
            if (entry.getValue().getName().getValue().equals(username)) {
                boolean sucess = entry.getValue().authorized(password).getValue();
                if (sucess) {
                    if (this.signedInAccount.contains(entry.getValue())) {
                        System.out.println("Log In unsuccessfully, account logged in");
                        return false; // Check account is logged in -> exit
                    }
                    this.signedInAccount.add(entry.getValue());
                    System.out.println("Log In successfully");
                    this.currentUser = entry.getValue();
                    return true;
                } else {
                    System.out.println("Log In unsuccessfully");
                    return false;
                }
            }
        }
        return false;
    }

    public boolean logIn(int id, String password) {
        try { // Try to see if the id existed
            FitnessUser logInUser = this.accounts.get(id);
            SimpleBooleanProperty sucess = logInUser.authorized(password);
            if (sucess.getValue()) {
                if (this.signedInAccount.contains(logInUser)) {
                    System.out.println("Log In successfully, Account already logged in");
                    return false;// Check account is logged in -> exit
                }
                this.signedInAccount.add(logInUser);
                System.out.println("Log In successfully");
                this.currentUser = logInUser;
                return false;
            } else { // Incorrect Password
                System.out.println("Log In unsuccessfully");
            }
        } catch (Exception e) { // Incorrect ID
            System.out.println("Log In unsuccessfully");
        }
        return false;

    }

    public void logOut(String username) {
        for (FitnessUser user : this.signedInAccount) {
            if (user.getName().getValue().equals(username)) {
                this.signedInAccount.remove(user);
                System.out.println("Log Out successfully");
                this.currentUser = null;
            } else {
                System.out.println("Log Out unsuccessfully");
            }
        }
    }

    public double getHeight() {
        return this.currentUser.getHeight().getValue();
    }

    public double getWeight() {
        return this.currentUser.getWeight().getValue();
    }

    public SimpleStringProperty getName() {
        return this.currentUser.username;
    }

    public void changePassword(String Password) {
        if (currentUser != null) {
            this.currentUser.changePassword(Password);
        }
    }

    public void removeAccount(int id) {
        try {
            this.accounts.remove(id);
            throw new Error("Removed");
        } catch (Exception e) {
        }

    }

    public ArrayList<FitnessUser> getAllFitnessUsers() {
        ArrayList<FitnessUser> loggedIn = new ArrayList<>();
        for (Map.Entry<Integer, FitnessUser> entry : this.accounts.entrySet()) {
            loggedIn.add(entry.getValue());
        }
        return loggedIn;
    }

    public String toString() {
        return "This system have " + this.accounts + ". In that, " + this.signedInAccount + "  are signedIn.";
    }
}
