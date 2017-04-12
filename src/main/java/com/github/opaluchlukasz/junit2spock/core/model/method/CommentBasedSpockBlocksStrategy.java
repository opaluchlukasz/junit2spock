package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.then;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.when;
import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static java.lang.String.format;
import static java.util.Collections.reverseOrder;

public class CommentBasedSpockBlocksStrategy implements SpockBlockApplier {

    private static final Logger LOG = LoggerFactory.getLogger(CommentBasedSpockBlocksStrategy.class);

    static final String GIVEN_BLOCK_START_MARKER_METHOD = "givenBlockStart";
    static final String WHEN_BLOCK_START_MARKER_METHOD = "whenBlockStart";
    static final String THEN_BLOCK_START_MARKER_METHOD = "thenBlockStart";

    private final List<Object> body;
    private final String methodName;

    public CommentBasedSpockBlocksStrategy(List<Object> body, String methodName) {
        this.body = body;
        this.methodName = methodName;
    }

    @Override
    public boolean apply() {
        List<Integer> givenIndexes = markerMethod(GIVEN_BLOCK_START_MARKER_METHOD);
        List<Integer> whenIndexes = markerMethod(WHEN_BLOCK_START_MARKER_METHOD);
        List<Integer> thenIndexes = markerMethod(THEN_BLOCK_START_MARKER_METHOD);

        Map<Integer, SpockBlockNode> blocksToBeAdded = new HashMap<>();
        if (givenIndexes.size() > 0) {
            blocksToBeAdded.put(givenIndexes.get(0), given());
        }

        if (whenIndexes.size() != thenIndexes.size()) {
            LOG.warn(format("Numbers of when/then blocks do not match for test method: %s", methodName));
        }

        whenIndexes.forEach(index -> blocksToBeAdded.put(index, when()));
        thenIndexes.forEach(index -> blocksToBeAdded.put(index, then()));

        blocksToBeAdded.keySet().stream().sorted(reverseOrder()).forEach(key -> body.add(key, blocksToBeAdded.get(key)));

        body.removeIf(node -> methodInvocation(node, new String[]{GIVEN_BLOCK_START_MARKER_METHOD,
            WHEN_BLOCK_START_MARKER_METHOD, THEN_BLOCK_START_MARKER_METHOD}).isPresent());

        return thenIndexes.size() != 0;
    }

    private List<Integer> markerMethod(String markerMethodName) {
        List<Integer> indexes = new LinkedList<>();
        for (int i = 0; i < body.size(); i++) {
            if (methodInvocation(body.get(i), markerMethodName).isPresent()) {
                indexes.add(i);
            }
        }
        return indexes;
    }
}
