package com.thinkaurelius.faunus.mapreduce;

import com.thinkaurelius.faunus.BaseTest;
import com.thinkaurelius.faunus.FaunusVertex;
import com.thinkaurelius.faunus.Tokens;
import com.thinkaurelius.faunus.mapreduce.derivations.Identity;
import com.thinkaurelius.faunus.mapreduce.derivations.Properties;
import com.tinkerpop.blueprints.Vertex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;

import java.io.IOException;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class MapSequenceTest extends BaseTest {
    MapReduceDriver<NullWritable, FaunusVertex, NullWritable, FaunusVertex, NullWritable, FaunusVertex> mapReduceDriver;

    public void setUp() throws Exception {
        mapReduceDriver = new MapReduceDriver<NullWritable, FaunusVertex, NullWritable, FaunusVertex, NullWritable, FaunusVertex>();
        mapReduceDriver.setMapper(new MapSequence.Map());
        mapReduceDriver.setReducer(new Reducer<NullWritable, FaunusVertex, NullWritable, FaunusVertex>());
    }

    public void testLongSequence() throws Exception {

        Configuration config = new Configuration();
        config.setStrings(Properties.KEYS + "-2", "name");
        config.set(Properties.ACTION + "-2", Tokens.Action.DROP.name());
        config.set(Properties.CLASS + "-2", Vertex.class.getName());
        config.setStrings(MapSequence.MAP_CLASSES, Identity.Map.class.getName(), Identity.Map.class.getName(), Properties.Map.class.getName());
        this.mapReduceDriver.withConfiguration(config);
        final Map<Long, FaunusVertex> results = runWithToyGraph(BaseTest.ExampleGraph.TINKERGRAPH, this.mapReduceDriver);
        assertEquals(results.size(), 6);
        for (FaunusVertex vertex : results.values()) {
            assertNull(vertex.getProperty("name"));
            assertFalse(vertex.getPropertyKeys().contains("name"));
            assertEquals(vertex.getPropertyKeys().size(), 1);
        }
    }

    public void testBadSequenceId() throws Exception {
        Configuration config = new Configuration();
        config.setStrings(Properties.KEYS + "-3", "name"); // should be 2
        config.set(Properties.ACTION + "-3", Tokens.Action.DROP.name());
        config.set(Properties.CLASS + "-3", Vertex.class.getName());
        config.setStrings(MapSequence.MAP_CLASSES, Identity.Map.class.getName(), Identity.Map.class.getName(), Properties.Map.class.getName());
        this.mapReduceDriver.withConfiguration(config);
        try {
            final Map<Long, FaunusVertex> results = runWithToyGraph(BaseTest.ExampleGraph.TINKERGRAPH, this.mapReduceDriver);
            assertFalse(true);
        } catch (IOException e) {
            assertTrue(true);
        }
    }
}