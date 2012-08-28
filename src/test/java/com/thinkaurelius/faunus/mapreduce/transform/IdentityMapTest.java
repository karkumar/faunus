package com.thinkaurelius.faunus.mapreduce.transform;

import com.thinkaurelius.faunus.BaseTest;
import com.thinkaurelius.faunus.FaunusEdge;
import com.thinkaurelius.faunus.FaunusVertex;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
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
public class IdentityMapTest extends BaseTest {

    MapReduceDriver<NullWritable, FaunusVertex, NullWritable, FaunusVertex, NullWritable, FaunusVertex> mapReduceDriver;

    public void setUp() {
        mapReduceDriver = new MapReduceDriver<NullWritable, FaunusVertex, NullWritable, FaunusVertex, NullWritable, FaunusVertex>();
        mapReduceDriver.setMapper(new IdentityMap.Map());
        mapReduceDriver.setReducer(new Reducer<NullWritable, FaunusVertex, NullWritable, FaunusVertex>());
    }

    public void testIdentityNoPaths() throws IOException {
        mapReduceDriver.withConfiguration(new Configuration());

        Map<Long, FaunusVertex> results = runWithGraph(generateGraph(BaseTest.ExampleGraph.TINKERGRAPH), mapReduceDriver);

        assertEquals(results.size(), 6);
        for (FaunusVertex vertex : results.values()) {
            assertEquals(vertex.pathCount(), 0);
            assertFalse(vertex.hasPaths());
            for (Edge edge : vertex.getEdges(Direction.BOTH)) {
                assertEquals(((FaunusEdge) edge).pathCount(), 0);
                assertFalse(((FaunusEdge) edge).hasPaths());
            }
        }

        assertEquals(mapReduceDriver.getCounters().findCounter(IdentityMap.Counters.VERTEX_COUNT).getValue(), 6);
        assertEquals(mapReduceDriver.getCounters().findCounter(IdentityMap.Counters.EDGE_COUNT).getValue(), 12);

        identicalStructure(results, BaseTest.ExampleGraph.TINKERGRAPH);
    }

    public void testIdentityPaths() throws IOException {
        mapReduceDriver.withConfiguration(new Configuration());

        Map<Long, FaunusVertex> results = runWithGraph(startPath(generateGraph(BaseTest.ExampleGraph.TINKERGRAPH), Vertex.class), mapReduceDriver);

        assertEquals(results.size(), 6);
        for (FaunusVertex vertex : results.values()) {
            assertEquals(vertex.pathCount(), 1);
            assertTrue(vertex.hasPaths());
            for (Edge edge : vertex.getEdges(Direction.BOTH)) {
                assertEquals(((FaunusEdge) edge).pathCount(), 0);
                assertFalse(((FaunusEdge) edge).hasPaths());
            }
        }

        assertEquals(mapReduceDriver.getCounters().findCounter(IdentityMap.Counters.VERTEX_COUNT).getValue(), 6);
        assertEquals(mapReduceDriver.getCounters().findCounter(IdentityMap.Counters.EDGE_COUNT).getValue(), 12);

        identicalStructure(results, BaseTest.ExampleGraph.TINKERGRAPH);
    }
}