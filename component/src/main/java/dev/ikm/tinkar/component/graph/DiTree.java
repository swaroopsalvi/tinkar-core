package dev.ikm.tinkar.component.graph;

import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.map.primitive.ImmutableIntIntMap;

import java.util.Optional;

public interface DiTree<V extends Vertex> extends Graph<V> {

    /**
     * A tree can have only one root.
     *
     * @return The root of a directed tree.
     */
    V root();

    /**
     * @param vertex
     * @return The predecessor of the provided vertex. Optional.empty() if
     * the root node.
     */
    Optional<V> predecessor(V vertex);

    ImmutableIntIntMap predecessorMap();


    @Override
    default ImmutableIntList successors(int vertexIndex) {
        return successorMap().get(vertexIndex);
    }

}
