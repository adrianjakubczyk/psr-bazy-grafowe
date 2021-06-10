import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM;

public class Main {
    public static Result createStudent(Transaction transaction, String studentName) {
        String command = "CREATE (:Student {nazwisko:$studentName})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("studentName", studentName);
        return transaction.run(command, parameters);
    }

    public static Result createGroup(Transaction transaction, String groupName) {
        String command = "CREATE (:Grupa {nazwa:$groupName})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("groupName", groupName);
        return transaction.run(command, parameters);
    }

    public static Result createRelationship(Transaction transaction, String studentName, String groupName) {
        String command =
                "MATCH (s:Student),(g:Grupa) " +
                        "WHERE s.nazwisko = $studentName AND g.nazwa = $groupName "
                        + "CREATE (s)-[r:JEST_W]->(g)" +
                        "RETURN type(r)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("studentName", studentName);
        parameters.put("groupName", groupName);
        System.out.println("Executing: " + command);
        return transaction.run(command, parameters);
    }

    public static Result readAllNodes(Transaction transaction) {
        String command =
                "MATCH (n)" +
                        "RETURN n";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result readAllRealtionships(Transaction transaction) {
        String command =
                "MATCH ()-[r]->()" +
                        "RETURN r;";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result readAllNodesWithRealationships(Transaction transaction) {
        String command =
                "MATCH (n1)-[r]-(n2) " +
                        "RETURN n1, r, n2 ";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result readAllNodesWithLabel(Transaction transaction) {
        String command =
                "MATCH (s:Student)-[r]-(n) " +
                        "RETURN s, r, n";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static void printField(Pair<String, Value> field) {
        System.out.println("field = " + field);
        Value value = field.value();
        if (TYPE_SYSTEM.NODE().isTypeOf(value))
            printNode(field.value().asNode());
        else if (TYPE_SYSTEM.RELATIONSHIP().isTypeOf(value))
            printRelationship(field.value().asRelationship());
        else
            throw new RuntimeException();
    }

    public static void printNode(Node node) {
        System.out.println("id = " + node.id());
        System.out.println("labels = " + " : " + node.labels());
        System.out.println("asMap = " + node.asMap());
    }

    public static void printRelationship(Relationship relationship) {
        System.out.println("id = " + relationship.id());
        System.out.println("type = " + relationship.type());
        System.out.println("startNodeId = " + relationship.startNodeId());
        System.out.println("endNodeId = " + relationship.endNodeId());
        System.out.println("asMap = " + relationship.asMap());
    }

    public static Result deleteEverything(Transaction transaction) {
        String command = "MATCH (n) DETACH DELETE n";
        System.out.println("Executing: " + command);
        return transaction.run(command);
    }
    public static void main(String[] args) {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jpassword"));
             Session session = driver.session()) {
            session.writeTransaction(tx -> deleteEverything(tx));

            Menu menu = new Menu(session);
            menu.selectOperation();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
