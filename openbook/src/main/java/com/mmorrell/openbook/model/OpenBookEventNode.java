package com.mmorrell.openbook.model;

import lombok.Builder;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class OpenBookEventNode {

    // 2 shorts = 4 bytes
    // 4 bytes of padding
    // AnyEvent x 1 = 144 bytes
    // 152 bytes total

    public static final int SIZE = 2 + 2 + 4 + OpenBookAnyEvent.SIZE;
    private static final int NEXT_OFFSET = 0;
    private static final int PREV_OFFSET = 2;
    private static final int EVENT_OFFSET = 8;

    private short next;
    private short prev;
    private OpenBookAnyEvent event;

    public static List<OpenBookEventNode> readEventNodes(byte[] bytes) {
        List<OpenBookEventNode> results = new ArrayList<>();
        int numNodes = bytes.length / SIZE;
        for (int i = 0; i < numNodes; i++) {
            byte[] nodeBytes = Arrays.copyOfRange(bytes, i * SIZE, (i + 1) * SIZE);
            OpenBookEventNode node = OpenBookEventNode.builder()
                    .next(ByteBuffer.wrap(nodeBytes, NEXT_OFFSET, 2).getShort())
                    .prev(ByteBuffer.wrap(nodeBytes, PREV_OFFSET, 2).getShort())
                    .event(
                            OpenBookAnyEvent.readOpenBookAnyEvent(
                                    Arrays.copyOfRange(
                                            nodeBytes,
                                            EVENT_OFFSET,
                                            nodeBytes.length
                                    )
                            )
                    )
                    .build();
            results.add(node);
        }

        return results;
    }
}
