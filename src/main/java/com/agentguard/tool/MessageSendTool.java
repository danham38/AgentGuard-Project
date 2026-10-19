package com.agentguard.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MessageSendTool implements Tool {
    private final List<SentMessage> messageList = new ArrayList<>();

    //tool name
    @Override
    public String getName() {
        return "messages.send";
    }

    @Override
    public synchronized Optional<String> execute(Map<String, String> args) {
        //validate arguments
        if (args == null || args.isEmpty()) {
            throw new IllegalArgumentException("Argument map cannot be null or empty");
        }
        String recipient = args.get("recipient");
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient cannot be missing");
        }

        String message = args.get("message");
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be missing");
        }

        //create new sent message object
        SentMessage sm = new SentMessage(recipient, message);
        //add to list
        messageList.add(sm);
        //return the message has been recorded
        return Optional.of("message recorded");
    }

    //return snapshot of sent messages to make messageList immutable
    public synchronized List<SentMessage> getSentMessages() {
        List<SentMessage> snapshot = List.copyOf(messageList);
        return snapshot;
    }

}
