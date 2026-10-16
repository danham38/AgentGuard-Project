package com.agentguard.tool;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentReadToolTest {
    //ARRANGE
    //ACT
    //ASSERT
    private final Map<String, String> testMap = Map.of(
            "DOC-001", "Security policy",
            "DOC-002", "Financial report"
    );

    private final Map<String, String> args = Map.of(
            "documentId", "DOC-001"

    );

    DocumentReadTool tool = new DocumentReadTool(testMap);


    @Test
    public void shouldReturnCorrectToolName() {
        assertEquals("documents.read", tool.getName());
    }

    @Test
    public void shouldReadExistingDocument() {
        assertEquals("Security policy", tool.readDocument("DOC-001").orElseThrow());
    }

    @Test
    public void shouldReturnEmptyForUnknownDocument() {
        assertTrue(tool.readDocument("DOC-100").isEmpty());
    }

    @Test
    public void shouldRejectNullDocumentId() {
        assertThrows(IllegalArgumentException.class, () -> {
            tool.readDocument(null);
        });
    }

    @Test
    public void shouldRejectBlankDocumentId() {
        assertThrows(IllegalArgumentException.class, () -> {
            tool.readDocument("");
        });
    }
    @Test
    public void shouldRejectOtherBlankDocumentId() {
        assertThrows(IllegalArgumentException.class, () -> {
            tool.readDocument(" ");
        });
    }
    @Test
    public void shouldNotReflectExternalMapChanges() {
        Map<String, String> map = new HashMap<>();
        map.put("DOC-101", "test-doc");
        DocumentReadTool tool2 = new DocumentReadTool(map);
        map.put("DOC-201", "test-immutability");
        assertFalse(tool2.readDocument("DOC-201").isPresent());
        assertTrue(tool2.readDocument("DOC-101").isPresent());
    }

    @Test
    public void shouldExecuteDocumentRead() {
        assertEquals("Security policy", tool.execute(args).orElseThrow());
    }

    @Test
    public void shouldReturnEmptyWhenExecutingUnknownDoc() {
        Map<String, String> args = Map.of(
                "documentId", "DOC-100"
        );
        assertTrue(tool.execute(args).isEmpty());
    }

    @Test
    public void shouldRejectNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(null);
        });
    }
    @Test
    public void shouldRejectEmptyDocId() {
        Map<String, String> args = Map.of(
                "documentId", ""
        );
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(args);
        });
    }

    @Test
    public void shouldRejectMissingDocumentIdArg() {
        Map<String, String> args = Map.of(
                "recipient", "DOC-001"
        );
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(args);
        });
    }

    @Test
    public void shouldRejectEmptyAgrsMap() {
        Map<String, String> args = Map.of();
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(args);
        });
    }

}
