package org.evaporatoronline.mazeapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MazeApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCorrectLength() throws Exception{
        List<String> testCases = List.of("5", "10", "100", "500", "1000");

        for(String testCase: testCases){
            mockMvc.perform(get("/maze")
                            .param("Length", testCase)
                            .param("Width", "10")
                            .param("Algorithm", "GrowingTree")
                            .param("Weight", "50"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/png"));
        }
    }

    @Test
    void testCorrectWidth() throws Exception{
        List<String> testCases = List.of("5", "10", "100", "500", "1000");

        for(String testCase: testCases){
            mockMvc.perform(get("/maze")
                            .param("Length", "10")
                            .param("Width", testCase)
                            .param("Algorithm", "GrowingTree")
                            .param("Weight", "50"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/png"));
        }
    }

    @Test
    void testCorrectAlgorithm() throws Exception{
        mockMvc.perform(get("/maze")
                        .param("Length", "10")
                        .param("Width", "10")
                        .param("Algorithm", "Eller's")
                        .param("Weight", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));

        mockMvc.perform(get("/maze")
                        .param("Length", "10")
                        .param("Width", "10")
                        .param("Algorithm", "GrowingTree")
                        .param("Weight", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"));


    }

    @Test
    void testInvalidLength() throws Exception{
        List<String> testCases = List.of("4", "-5", "1001", "10000");

        for(String testCase: testCases){
            mockMvc.perform(get("/maze")
                            .param("Length", testCase)
                            .param("Width", "10")
                            .param("Algorithm", "GrowingTree")
                            .param("Weight", "50"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid Length parameter"));
        }
    }

    @Test
    void testInvalidWidth() throws Exception{
        List<String> testCases = List.of("4", "-5", "1001", "10000");

        for(String testCase: testCases){
            mockMvc.perform(get("/maze")
                            .param("Length", "10")
                            .param("Width", testCase)
                            .param("Algorithm", "GrowingTree")
                            .param("Weight", "50"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid Width parameter"));
        }
    }

    @Test
    void testInvalidAlgorithm() throws Exception{
        mockMvc.perform(get("/maze")
                        .param("Length", "10")
                        .param("Width", "10")
                        .param("Algorithm", "FakeAlgorithm")
                        .param("Weight", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Algorithm parameter"));

    }

}