import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import java.util.Scanner;

public class Menu {
    final static private Scanner scanner = new Scanner(System.in);
    final private ODatabaseSession session;

    public Menu(ODatabaseSession session) {
        this.session = session;
    }

    private void printMenu() {
        int i = 1;
        System.out.println("===================================================");
        System.out.println("Please choose operation:");
        System.out.println(i++ + ": Add Car");
        System.out.println(i++ + ": Add Car's repair job");
        System.out.println(i++ + ": Select by ID");
        System.out.println(i++ + ": Select repairs where cost is higher than value");
        System.out.println(i++ + ": Select All");
        System.out.println(i++ + ": Delete by ID");
        System.out.println(i + ": Calculate average repair cost");

        System.out.println("9: Exit");
        System.out.println("===================================================");
    }

    public void selectOperation() {
        boolean doExit = false;
        while (!doExit) {
            printMenu();
            int op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1:
                    System.out.println("Add Car");
                    addCar();
                    break;
                case 2:
                    System.out.println("Add Car's repair job");
                    addRepairJob();
                    break;
                case 3:
                    System.out.println("Select by ID");
                    selectById();
                    break;
                case 4:
                    System.out.println("Select repairs where cost is higher than value");
                    selectByCost();
                    break;
                case 5:
                    System.out.println("Select All");
                    selectAll();
                    break;
                case 6:
                    System.out.println("Delete");
                    deleteById();
                    break;
                case 7:
                    System.out.println("Calculate average repair cost");
                    calculateAvgCost();
                    break;
                case 9:
                    System.out.println("Exit");
                    doExit = true;
                    break;
                default:
                    System.out.println("Not a recognizable choice");
                    break;
            }
        }

    }

    private void addCar() {
        System.out.println("Car's brand:");
        String brand = scanner.nextLine();
        System.out.println("Registration number:");
        String registrationNumber = scanner.nextLine();

        TransactionManager.createCar(session, registrationNumber, brand);
    }

    private void addRepairJob() {
        System.out.println("Registration number:");
        String registrationNumber = scanner.nextLine();
        System.out.println("Repair description:");
        String desc = scanner.nextLine();
        System.out.println("Repair cost:");
        String cost = scanner.nextLine();

        TransactionManager.createRepair(session, registrationNumber, desc, Double.parseDouble(cost));
    }

    private void selectById() {
        System.out.println("Vertex id:");
        String id = scanner.nextLine();

        String query = "SELECT * from V where @rid = ?";
        OResultSet rs = session.query(query, id);

        while (rs.hasNext()) {
            OResult item = rs.next();
            System.out.println(item.toString());
        }

        rs.close();
    }

    private void selectByCost() {
        System.out.println("Cost:");
        String cost = scanner.nextLine();

        String query = "SELECT * from Repair where cost >= ?";
        OResultSet rs = session.query(query, Double.parseDouble(cost));

        while (rs.hasNext()) {
            OResult item = rs.next();
            System.out.println(item.toString());
        }

        rs.close();
    }

    private void selectAll() {
        String query = "SELECT * from V";
        OResultSet rs = session.query(query);

        while (rs.hasNext()) {
            OResult item = rs.next();
            System.out.println(item.toString());
        }

        rs.close();
    }

    private void deleteById() {
        System.out.println("Vertex id:");
        String id = scanner.nextLine();

        OResultSet rs = session.command("DELETE VERTEX V where @rid = ?", id);

        while (rs.hasNext()) {
            OResult item = rs.next();
            System.out.println(item.toString());
        }

        rs.close();
    }

    private void calculateAvgCost() {
        String query = "SELECT avg(cost) from Repair";
        OResultSet rs = session.query(query);

        while (rs.hasNext()) {
            OResult item = rs.next();
            System.out.println(item.toString());
        }

        rs.close();
    }
}
