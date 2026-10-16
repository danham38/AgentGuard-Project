package com.agentguard.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DocumentReadTool implements Tool {
    private final Map<String, String> documentsMap;

    public DocumentReadTool(Map<String, String> documentsMap) {
        this.documentsMap = Map.copyOf(documentsMap);
    }


    public Optional<String> readDocument(String docId) {
        if (docId == null || docId.isBlank()) {
            throw new IllegalArgumentException("Doc ID cannot be blank or null");
        }
        return Optional.ofNullable(documentsMap.get(docId));

    }


    @Override
    public String getName() {
        //identifying what the tool does
       return "documents.read";
    }

    @Override
    public Optional<String> execute(Map<String, String> args) {
        //reject null args map
        if (args == null || args.isEmpty()) {
            throw new IllegalArgumentException("Argument map cannot be null or empty");
        }
        //extract docID
        String docId = args.get("documentId");
        //pass that into readDoc method to return actual document
        return readDocument(docId);
    }

}
