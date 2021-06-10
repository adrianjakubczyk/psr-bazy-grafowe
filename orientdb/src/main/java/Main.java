import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class Main {

    public static void main(String[] args) {

        OrientDB orient = new OrientDB("remote:localhost", "root", "rootpwd", OrientDBConfig.defaultConfig());

//        if(orient.exists("carService")){
//            orient.drop("carService");
//        }
//        orient.createIfNotExists("carService", ODatabaseType.PLOCAL);
        ODatabaseSession db = orient.open("carService", "admin", "admin");

        //createSchema(db);

        createTestData(db);

        Menu menu = new Menu(db);

        menu.selectOperation();

        db.close();
        orient.close();

    }

    private static void createSchema(ODatabaseSession db) {
        //CAR BRAND
        OClass carBrand = db.getClass("CarBrand");

        if (carBrand == null) {
            carBrand = db.createVertexClass("CarBrand");
        }

        if (carBrand.getProperty("brand") == null) {
            carBrand.createProperty("brand", OType.STRING);
            carBrand.createIndex("CarBrand_brand_index", OClass.INDEX_TYPE.UNIQUE, "brand");
        }

        //CAR
        OClass car = db.getClass("Car");

        if (car == null) {
            car = db.createVertexClass("Car");
        }

        if (car.getProperty("registrationNumber") == null) {
            car.createProperty("registrationNumber", OType.STRING);
            car.createIndex("Car_registrationNumber_index", OClass.INDEX_TYPE.UNIQUE, "registrationNumber");
        }

        //REPAIR
        OClass repair = db.getClass("Repair");

        if (repair == null) {
            repair = db.createVertexClass("Repair");
        }

        if (repair.getProperty("description") == null) {
            repair.createProperty("description", OType.STRING);
            repair.createIndex("Repair_description_index", OClass.INDEX_TYPE.NOTUNIQUE, "description");
        }
        if (repair.getProperty("cost") == null) {
            repair.createProperty("cost", OType.DOUBLE);
            //repair.createIndex("Repair_cost_index", OClass.INDEX_TYPE.NOTUNIQUE, "cost");
        }

        //RELATIONS
        if (db.getClass("BRAND") == null) {
            db.createEdgeClass("BRAND");
        }
        if (db.getClass("DONE_FOR") == null) {
            db.createEdgeClass("DONE_FOR");
        }

    }

    private static void createTestData(ODatabaseSession db) {
        System.out.println("Inserting test data");

        TransactionManager.createCar(db, "XXX1234", "Toyota");
        TransactionManager.createCar(db, "CCC1234", "Toyota");
        TransactionManager.createCar(db, "VVV1234", "Mercedes");
        TransactionManager.createCar(db, "BBB1234", "Polonez");

        TransactionManager.createRepair(db, "XXX1234", "Oil change", 50);
        TransactionManager.createRepair(db, "CCC1234", "Tire change", 60);
        TransactionManager.createRepair(db, "CCC1234", "Windshield change", 300);
        TransactionManager.createRepair(db, "VVV1234", "Speed test", 30);

    }


}