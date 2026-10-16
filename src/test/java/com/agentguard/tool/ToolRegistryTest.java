package com.agentguard.tool;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


//ARRANGE, ACT, ASSERT
public class ToolRegistryTest {

    public static class TestTool implements Tool {
        private final String name;
        TestTool(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Optional<String> execute(Map<String, String> args) {
            throw new UnsupportedOperationException("Execution unsupported in registry tests");
        }
    }

    @Test
    public void shouldRegisterAndFindTool() {
        //lambda used as Tool is abstract so cant be instantiated
        //use lamda instead to init
        //ARRANGE
        ToolRegistry registry = new ToolRegistry();
        TestTool trueTool = new TestTool("documents.read");

        //ACT
        String trueToolName = trueTool.getName();

        registry.register(trueTool);

        //ASSERT
        //check exists
        assertNotNull(trueToolName);
        Optional<Tool> result = registry.findTool(trueToolName);
        assertSame(trueTool, result.orElseThrow());
        //check returns optional empty
        assertTrue(registry.findTool(trueToolName).isPresent());
    }

    @Test
    public void shouldReturnEmptyForUnknownType() {
        //ARRANGE
        ToolRegistry registry = new ToolRegistry();
        //ACT
        TestTool tool = new TestTool("not-real");

        //ASSERT
       assertTrue(registry.findTool(tool.getName()).isEmpty());

    }

    @Test
    public void shouldRejectDuplicateEntry() {
        //ARRANGE
        ToolRegistry registry = new ToolRegistry();
        //duplicate entries
        TestTool tool1 = new TestTool("dup");
        Tool tool2 = new TestTool("dup");

        registry.register(tool1);

        //ASSERT
        assertThrows(IllegalStateException.class, () -> {
            //ACT
            registry.register(tool2);
        });

    }

    @Test
    public void shouldRejectBlankInput() {
        Tool tool = new TestTool("");
        ToolRegistry reg = new ToolRegistry();
        assertThrows(IllegalArgumentException.class, () -> {
            reg.register(tool);
        });
    }

    @Test
    public void shouldRejectOtherBlankInput() {
        Tool tool = new TestTool(" ");
        ToolRegistry reg = new ToolRegistry();
        assertThrows(IllegalArgumentException.class, () -> {
            reg.register(tool);
        });
    }



    @Test
    public void shouldRejectNullCase() {
        ToolRegistry registry = new ToolRegistry();
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(null);
        });
    }

}
