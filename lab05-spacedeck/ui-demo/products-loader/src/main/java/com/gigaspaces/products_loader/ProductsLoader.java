package com.gigaspaces.products_loader;

import com.gigaspaces.common.Product;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.logging.Logger;

import static com.gigaspaces.common.Constants.*;
import static org.openspaces.extensions.QueryExtension.max;


public class ProductsLoader implements InitializingBean, DisposableBean {

    Logger log = Logger.getLogger(this.getClass().getName());

    @GigaSpaceContext
    private GigaSpace gigaSpace;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::populateSpaceWithProducts).start();
    }

    private void populateSpaceWithProducts() {
        SQLQuery<Product> query = new SQLQuery<>(Product.class, "");

        int highestId = 0;
        if (gigaSpace.count(new Product()) > 0) {
            highestId = max(gigaSpace, query, "id");
        }
        int id = highestId + 1;
        log.info("Start populating space with products");

        long startTime = System.currentTimeMillis();
        long currentTime = 0;
        boolean toStop = false;
        int maxEntries = 100_000;
        int current = 0;

        while (current < maxEntries) {
            long iterationStartTime = System.currentTimeMillis();

            Product[] products = new Product[NUM_OF_PRODUCTS_TO_LOAD];

            for (int i = 0; i < NUM_OF_PRODUCTS_TO_LOAD; i++) {
                products[i] = Product.createProduct(id);
                id++;
            }

            current += NUM_OF_PRODUCTS_TO_LOAD;

            gigaSpace.writeMultiple(products);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentTime = System.currentTimeMillis();

            long differenceTime = currentTime - iterationStartTime;
            if ((differenceTime + currentTime) > (startTime + TIME_PERIOD_OF_5_MINUTES * MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE)) {
                toStop = true;
            }
        }
        log.info("Finish populating space with " + gigaSpace.count(new Product()) + " products");
    }


    @Override
    public void destroy() throws Exception { }
}
