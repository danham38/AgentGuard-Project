package com.agentguard.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.HttpStatus;

@SpringBootTest
@AutoConfigureMockMvc
class ToolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDocumentWithHttp200() throws Exception {
        String json = """
    {
        "toolName": "documents.read",
        "arguments": {
            "documentId": "DOC-001"
        }
    }
    """;

        mockMvc.perform(
                post("/api/tools/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isOk()) // 200 : tool authorised and doc found
                .andExpect(content().string("Security policy"));
    }

    @Test
    void shouldReturnHttp404ForUnknownDocument() throws Exception {
        String json = """
                {
                    "toolName": "documents.read",
                    "arguments": {
                        "documentId": "DOC-999"
                        }
                }
                """;

        mockMvc.perform(
                post("/api/tools/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isNotFound()); //404 error : doc not found
    }

    @Test
    void shouldReturnHttp403ForBlockedTool() throws Exception {
        String json = """
        {
            "toolName": "messages.send",
            "arguments": {
                "recipient": "example@gmail.com",
                "message": "hello"
            }
        }
        """;
        mockMvc.perform(
                post("/api/tools/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isForbidden()); //403 error : not authorised tool
    }

    @Test
    void shouldReturnHttp400ForInvalidArguments() throws Exception {
        String json = """
        {
            "toolName": "documents.read",
                "arguments": {
            "documentId": ""
            }
        }
        """;
        mockMvc.perform(
                post("/api/tools/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request arguments"));
    }

}
