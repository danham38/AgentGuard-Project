package com.agentguard.tool;

import java.util.HashMap;
import java.util.Optional;

public class ToolRegistry {
    //the toolsMap mapping
    private final HashMap<String, Tool> toolsMap = new HashMap<>();


    //find tool -> if exists return it, else return null
    public Optional<Tool> findTool(String name) {
        return Optional.ofNullable(toolsMap.get(name));
    }

    public void register(Tool tool) {

        //check null
        if (tool == null) {
            throw new IllegalArgumentException("Tool is Null");
        }
        //get tool name as cannot be null
        String name = tool.getName();
        //then input exists and check for not blank
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is Empty/Null");
        }
        //key exists and isnt empty, check if already in hashmap
        if (toolsMap.containsKey(name)) {
            throw new IllegalStateException("Tool already found");
        }
        //else put in hashmap
        toolsMap.put(name, tool);
    }




}
