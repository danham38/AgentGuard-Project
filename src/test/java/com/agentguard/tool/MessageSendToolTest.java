package com.agentguard.tool;

import com.agentguard.audit.AuditLog;
import com.agentguard.executor.ToolExecutor;
import com.agentguard.policy.PolicyEngine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MessageSendToolTest {

    MessageSendTool tool = new MessageSendTool();
    Map<String, String> args = Map.of(
            "recipient", "example@gmail.com",
            "message", "test-message"
    );
    AuditLog log = new AuditLog();


    @Test
    public void shouldReturnCorrectToolName() {
        assertEquals("messages.send", tool.getName());
    }

    @Test
    public void shouldRecordValidMessage() {
        //message should appear in valid messages
        tool.execute(args);
        SentMessage sm = new SentMessage("example@gmail.com", "test-message");
        List<SentMessage> listSM = List.of(sm);
        assertEquals(listSM, tool.getSentMessages());
    }

    @Test
    public void shouldRecordMultipleMessages() {
        Map<String,String> multipleMessagesMap = Map.of(
                "recipient", "test@gmail.com",
                "message", "hello"

        );
        tool.execute(args);
        tool.execute(multipleMessagesMap);
        SentMessage sm2 = new SentMessage("test@gmail.com", "hello");
        SentMessage sm = new SentMessage("example@gmail.com", "test-message");
        List<SentMessage> listSM = List.of(sm, sm2);
        assertEquals(listSM, tool.getSentMessages());
    }

    @Test
    public void shouldRejectNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(null);
        });
    }

    @Test
    public void shouldRejectMissingRecipient() {
        Map<String, String> missingRecipientArgs = Map.of(
                "message", "hello"
        );
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(missingRecipientArgs);
        });
    }

    @Test
    public void shouldRejectBlankRecipient() {
        Map<String, String> blankRecipientArgs = Map.of(
                "recipient" , " ",
                "message", "hello"
        );
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(blankRecipientArgs);
        });
    }

    @Test
    public void shouldRejectMissingMessage() {
        Map<String, String> missingMessageArgs = Map.of(
                "recipient" , "test@gmail.com"

        );
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(missingMessageArgs);
        });
    }

    @Test
    public void shouldRejectBlankMessage() {
        Map<String, String> blankMessageArgs = Map.of(
                "recipient" , "test@gmail.com",
                "message", " "

        );
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(blankMessageArgs);
        });
    }

    @Test
    public void shouldReturnImmutableMessageSnapshot() {
        //prove snapshot doesnt clear
        tool.execute(args);
        List<SentMessage> sm = tool.getSentMessages();
        assertThrows(UnsupportedOperationException.class, () -> {
            sm.clear();
        });
        Map<String, String> secondArgs = Map.of(
                "recipient", "hello@gmail.com",
                "message", "hello2"
        );
        tool.execute(secondArgs);

        assertEquals(1, sm.size());
        assertEquals(2, tool.getSentMessages().size());
    }

    @Test
    public void shouldPreventUnauthorisedMessageSend() {
        ToolRegistry registry = new ToolRegistry();
        Map<String, Set<String>> config = Map.of(
                "agent-001", Set.of("documents.read")
        );
        registry.register(tool);
        PolicyEngine policyEngine = new PolicyEngine(config);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);

        assertThrows(SecurityException.class, () -> {
            exe.execute("agent-001", "messages.send", args);
        });
        assertTrue(tool.getSentMessages().isEmpty());
    }

    @Test
    public void shouldAllowAuthorisedMessageSend() {
        ToolRegistry registry = new ToolRegistry();
        Map<String, Set<String>> config = Map.of(
                "agent-001", Set.of("messages.send")
        );
        registry.register(tool);
        PolicyEngine policyEngine = new PolicyEngine(config);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        Optional<String> result = exe.execute("agent-001", "messages.send", args);
        assertEquals("message recorded", result.orElseThrow());
        assertEquals(1, tool.getSentMessages().size());
    }


}
