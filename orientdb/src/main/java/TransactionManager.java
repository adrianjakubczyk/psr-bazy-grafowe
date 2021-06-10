import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TransactionManager {

    public static OVertex createCarBrand(ODatabaseSession db, String brand) {

        OVertex result = db.newVertex("CarBrand");
        result.setProperty("brand", brand);

        ORecordIteratorClass<ODocument> vertices = db.browseClass("CarBrand");
        for (ODocument d : vertices) {
            if (d.asVertex().isPresent()) {
                OVertex v = d.asVertex().get();
                if (v.getProperty("brand").toString().equalsIgnoreCase(brand)) {
                    return v;
                }
            }
        }
        result.save();
        return result;
    }

    public static OVertex createCar(ODatabaseSession db, String registrationNumber, String brand) {
        OVertex result = db.newVertex("Car");
        result.setProperty("registrationNumber", registrationNumber);


        boolean doSave = true;
        ORecordIteratorClass<ODocument> vertices = db.browseClass("Car");
        for (ODocument d : vertices) {
            if (d.asVertex().isPresent()) {
                OVertex v = d.asVertex().get();
                if (v.getProperty("registrationNumber").toString().equalsIgnoreCase(registrationNumber)) {
                    doSave = false;
                    result = v;
                    break;
                }
            }
        }

        if (doSave) {
            result.save();
        }
        System.out.println("creating edge " + registrationNumber + " -BRAND-> " + brand);
        OEdge edge = result.addEdge(createCarBrand(db, brand), "BRAND");
        edge.save();

        return result;
    }

    public static OVertex createRepair(ODatabaseSession db, String registrationNumber, String description, double cost) {
        OVertex car = null;
        ORecordIteratorClass<ODocument> vertices = db.browseClass("Car");
        for (ODocument d : vertices) {
            if (d.asVertex().isPresent()) {
                OVertex v = d.asVertex().get();
                if (v.getProperty("registrationNumber").toString().equalsIgnoreCase(registrationNumber)) {
                    car = v;
                    break;
                }
            }
        }
        OVertex result = null;
        if (car != null) {
            result = db.newVertex("Repair");
            result.setProperty("description", description);
            result.setProperty("cost", cost);
            result.save();
            System.out.println("creating edge {" + description + ", cost: " + cost + "} -DONE_FOR-> " + registrationNumber);
            OEdge edge = result.addEdge(car, "DONE_FOR");
            edge.save();

        } else {
            System.out.println("Could not find car with registration number: " + registrationNumber);
        }
        return result;
    }
}
