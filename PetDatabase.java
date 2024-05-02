/*
Adding and removing pets from database while catching exceptions
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

// PetDatabase with pets array capacity 100
public class PetDatabase {
    private static final int CAPACITY = 100;
    private static Pet[] pets = new Pet[CAPACITY];
    private static int petCount = 0;
    private static Scanner s = new Scanner(System.in);

    //main function
    public static void main(String[] args) {
        // open file and view contents
        loadDatabase();
        int choice;
        do {
            // on user input select function
            choice = getUserChoice();
            switch (choice) {
                case 1:
                    showAllPets();
                    break;
                case 2:
                    addPets();
                    break;
                case 3:
                    removePet();
                    break;
                case 4:
                    saveDatabase();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        // exit message
        } while (choice != 4);
        System.out.println("Goodbye!");
    }

    // loadDatabase examines file and catches errors if found when loading database
    private static void loadDatabase() {
        // opens file
        File file = new File("pets.txt");
        try {
            Scanner s = new Scanner(file);
            // reads file line by line
            while (s.hasNextLine()) {
                String[] parts = parseArgument(s.nextLine());
                addPet(parts[0], Integer.parseInt(parts[1]));
            }
            s.close();
        // exceptions for file not found, input not correct, or database is full
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException | FullDatabaseException e) {
            e.printStackTrace();
        }
    }

    // save database writes new entries into file
    private static void saveDatabase() {
        // writes new pets (name and age) into pets document
        try {
            PrintWriter writer = new PrintWriter("pets.txt");
            for (int i = 0; i < petCount; i++) {
                Pet pet = pets[i];
                writer.println(pet.getName() + " " + pet.getAge());
            }
            writer.close();
        // exception if cant find document specified
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // get user choice / main menu
    private static int getUserChoice() {
        System.out.println("Pet Database Program.");
        System.out.println("What would you like to do?");
        System.out.println("1) View all pets");
        System.out.println("2) Add new pets");
        System.out.println("3) Remove a pet");
        System.out.println("4) Exit program");
        System.out.print("Your choice: ");
        try {
            return s.nextInt();
        // throw exception if option typed not available
        } catch (InputMismatchException e) {
            s.nextLine();
            return -1;
        }
    }

    // addPets method allows input until 'done' is typed
    private static void addPets() {
        s.nextLine();
        while (true) {
            // pet name and age input
            System.out.print("add pet (name, age): ");
            String input = s.nextLine();
            // exit if 'done'
            if (input.equals("done")) {
                break;
            }
            // taking input and putting first part into name and second part into age
            try {
                String[] parts = parseArgument(input);
                addPet(parts[0], Integer.parseInt(parts[1]));
            // throw exception if invalid input or database full
            } catch (InvalidArgumentException | FullDatabaseException e) {
                e.printStackTrace();
            }
        }
        System.out.println(petCount + " pets added.");
    }

    // addPet either adds pet or throws exception
    private static void addPet(String name, int age) throws InvalidArgumentException, FullDatabaseException {
        if (petCount == CAPACITY) {
            throw new FullDatabaseException("Database is full.");
        }
        pets[petCount++] = new Pet(name, age);
    }

    // if input contains != 2 inputs on the line
    private static String[] parseArgument(String line) throws InvalidArgumentException {
        String[] parts = line.split("\\s+");
        if (parts.length != 2) {
            throw new InvalidArgumentException("Invalid input: " + line);
        }
        return parts;
    }

    // showAllPets displays pets in the database
    private static void showAllPets() {
        printTableHeader();
        for (int i = 0; i < petCount; i++) {
            Pet pet = pets[i];
            printTableRow(i, pet.getName(), pet.getAge());
        }
        printTableFooter(petCount);
    }

    // removePet removes pet at ID or throws an issue of some sort
    private static void removePet() {
        // if no IDs
        if (petCount == 0) {
            System.out.println("No pets to remove.");
            return;
        }
        // if pets more than one
        showAllPets();
        System.out.print("Enter the pet ID to remove: ");
        int id = s.nextInt();
        s.nextLine();
        try {
            // no ID found
            if (id < 0 || id >= petCount) {
                throw new InvalidIdException("ID " + id + " does not exist.");
            }
            // finds pet and removes pet prints confirmation
            for (int i = id; i < petCount - 1; i++) {
                pets[i] = pets[i + 1];
            }
            pets[--petCount] = null;
            System.out.println("Pet at ID " + id + " is removed.");
            // exception for ID not found
        } catch (InvalidIdException e) {
            e.printStackTrace();
        }
    }

    // heading formatting
    private static void printTableHeader() {
        System.out.println("+-----------------------+");
        System.out.println("| ID |    NAME    | AGE |");
        System.out.println("+-----------------------+");
    }

    // mid-table formatting
    private static void printTableRow(int id, String name, int age) {
        System.out.printf("| %2d | %-10s | %3d |\n", id, name, age);
    }

    // footer formatting
    private static void printTableFooter(int rowCount) {
        System.out.println("+-----------------------+");
        System.out.println(rowCount + " rows in set.");
    }
}

// pet class
class Pet {
    private String name;
    private int age;

    // sets pet age and name
    public Pet(String name, int age) {
        this.name = name;
        try {
            setAge(age);
        } catch (InvalidAgeException e) {
            e.printStackTrace();
        }
    }

    // get name
    public String getName() {
        return name;
    }

    // get age
    public int getAge() {
        return age;
    }

    // set name
    public void setName(String name) {
        this.name = name;
    }

    // setter for age unless age out of range
    public void setAge(int age) throws InvalidAgeException {
        if (age < 1 || age > 50) {
            throw new InvalidAgeException("Invalid age: " + age);
        }
        this.age = age;
    }
}

// exception if age not valid
class InvalidAgeException extends Exception {
    public InvalidAgeException(String message) {
        super(message);
    }
}

// exception if input not valid
class InvalidArgumentException extends Exception {
    public InvalidArgumentException(String message) {
        super(message);
    }
}

// exception for wrong id
class InvalidIdException extends Exception {
    public InvalidIdException(String message) {
        super(message);
    }
}

// exception for a full database
class FullDatabaseException extends Exception {
    public FullDatabaseException(String message) {
        super(message);
    }
}
