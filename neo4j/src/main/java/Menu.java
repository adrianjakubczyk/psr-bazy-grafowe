import org.neo4j.driver.*;
import org.neo4j.driver.util.Pair;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM;

public class Menu {
    final static private Scanner scanner = new Scanner(System.in);
    final private Session session;

    public Menu(Session session) {
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
        System.out.println(i+ ": Calculate average repair cost");

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
    private void addCar(){
        System.out.println("Car's brand:");
        String brand = scanner.nextLine();
        System.out.println("Registration number:");
        String registrationNumber = scanner.nextLine();

        session.writeTransaction(transaction -> addCarBrand(transaction,brand));
        session.writeTransaction(transaction -> addRegistrationNumber(transaction,registrationNumber));
        session.writeTransaction(transaction -> createCarBrandRelationship(transaction,registrationNumber,brand));
    }

    private static Result addCarBrand(Transaction transaction, String carBrand) {
        String command = "MERGE (:Brand {brand:$carBrand})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("carBrand", carBrand);
        return transaction.run(command, parameters);
    }
    private static Result addRegistrationNumber(Transaction transaction, String registrationNumber) {
        String command = "MERGE (:Car {registrationNumber:$registrationNumber})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("registrationNumber", registrationNumber);
        return transaction.run(command, parameters);
    }
    public static Result createCarBrandRelationship(Transaction transaction, String registrationNumber, String carBrand) {
        String command =
                "MATCH (c:Car),(b:Brand) " +
                        "WHERE c.registrationNumber = $registrationNumber AND b.brand = $carBrand "
                        + "CREATE (c)-[r:BRAND]->(b)" +
                        "RETURN type(r)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("registrationNumber", registrationNumber);
        parameters.put("carBrand", carBrand);
        System.out.println("Executing: " + command);
        return transaction.run(command, parameters);
    }

    private void addRepairJob() {
        System.out.println("Registration number:");
        String registrationNumber = scanner.nextLine();


        if(session.run("MATCH (c:Car) WHERE c.registrationNumber = '"+registrationNumber+"' RETURN c").hasNext()) {
            System.out.println("Repair description:");
            String desc = scanner.nextLine();
            System.out.println("Repair cost:");
            String cost = scanner.nextLine();
            String datetime = LocalDateTime.now().toString();
            session.writeTransaction(transaction -> addRepair(transaction,desc,cost, datetime));
            session.writeTransaction(transaction -> createRepairCarRelationship(transaction,datetime,registrationNumber));
        } else{
            System.out.println("Vehicle registration number:"+registrationNumber+" not found");
        }


    }
    private static Result addRepair(Transaction transaction, String desc,String cost,String datetime) {
        String command = "CREATE (:Repair {desc:$desc,cost:$cost,datetime:$datetime})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("desc", desc);
        parameters.put("cost", Double.valueOf(cost));
        parameters.put("datetime", datetime);
        return transaction.run(command, parameters);
    }
    public static Result createRepairCarRelationship(Transaction transaction, String datetime, String registrationNumber) {
        String command =
                "MATCH (e:Repair),(c:Car) " +
                        "WHERE e.datetime = $datetime AND c.registrationNumber = $registrationNumber "
                        + "CREATE (e)-[r:DONE_FOR]->(c)" +
                        "RETURN type(r)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("datetime", datetime);
        parameters.put("registrationNumber", registrationNumber);
        System.out.println("Executing: " + command);
        return transaction.run(command, parameters);
    }

    private void selectById() {
        System.out.println("Node id:");
        String id = scanner.nextLine();

        session.writeTransaction(transaction -> selectById(transaction,id));
    }
    public static Result selectById(Transaction transaction , String id) {
        String command =
                "MATCH (n) WHERE id(n) = "+id +
                        " RETURN n";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                System.out.println("ID: "+field.toString()+"\nVALUES:"+field.value().asMap());
        }
        return result;
    }
    public static Result selectAll(Transaction transaction) {
        String command =
                "MATCH (n) "+
                        "RETURN n";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                System.out.println("ID: "+field.toString()+"\nVALUES:"+field.value().asMap());
        }
        return result;
    }

    private void selectByCost() {
        System.out.println("cost filter value:");
        String cost = scanner.nextLine();

        String command ="match (r:Repair) where r.cost >= "+cost+" return r";
        System.out.println("Executing: " + command);
        Result result = session.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                System.out.println("ID: "+field.toString()+"\nVALUES:"+field.value().asMap());
        }
    }

    private void selectAll() {
        session.writeTransaction(transaction -> selectAll(transaction));

    }

    private void deleteById() {
        System.out.println("Node id:");
        String id = scanner.nextLine();

        String command ="match (n) where id(n) = "+id+" detach delete n";
        System.out.println("Executing: " + command);
        Result result = session.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                System.out.println("ID: "+field.toString()+"\nVALUES:"+field.value().asMap());
        }
    }

    private void calculateAvgCost() {
        String command ="match (n:Repair) return avg(n.cost)";
        System.out.println("Executing: " + command);
        Result result = session.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record.toString());
        }
    }
}
