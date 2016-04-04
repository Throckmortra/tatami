package fr.ippon.tatami;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

/**
 * Base class for starting/stopping Cassandra during tests.
 */
public class AbstractCassandraTest {

    public static final String CASSANDRA_UNIT_KEYSPACE = "cassandra_unit_keyspace";

    @BeforeClass
    public static void startServer() throws InterruptedException, TTransportException, ConfigurationException, IOException, URISyntaxException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        Cluster cluster = new Cluster.Builder().addContactPoints("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();
        CQLDataLoader dataLoader = new CQLDataLoader(session);
        dataLoader.load(new ClassPathCQLDataSet("config/cql/create-tables.cql", true, "cassandra_unit_keyspace"));

        applyScripts(dataLoader, "config/cql/", "*_added_entity_*.cql");
        applyScripts(dataLoader, "config/cql/migration/", "V*.cql");
    }

    private static void applyScripts(CQLDataLoader dataLoader, String cqlDir, String pattern) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(cqlDir).toURI());
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, pattern)) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                dataLoader.load(new ClassPathCQLDataSet(cqlDir + fileName, false, false, CASSANDRA_UNIT_KEYSPACE));
            }
        }
    }

    @AfterClass
    public static void cleanupServer() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }
}
