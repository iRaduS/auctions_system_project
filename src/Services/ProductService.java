package Services;

import java.util.Map;

import Bootstrapers.DatabaseBootstrapper;
import Entities.ProductEntity;

public class ProductService extends CrudService<ProductEntity> {
    private static ProductService productServiceInstance;

    private final DatabaseBootstrapper databaseInstance;

    private ProductService(DatabaseBootstrapper databaseBootstrapper) {
        this.dbTable = ProductEntity.dbTable;
        this.databaseInstance = databaseBootstrapper;
    }

    public static ProductService getInstance(DatabaseBootstrapper databaseBootstrapper) {
        if (productServiceInstance == null) {
            productServiceInstance = new ProductService(databaseBootstrapper);
        }

        return productServiceInstance;
    }

    public void create(Map<String, ?> dataToFill) {
        super.create(this.databaseInstance.getConnectionInstance(), dataToFill);
    }
}
