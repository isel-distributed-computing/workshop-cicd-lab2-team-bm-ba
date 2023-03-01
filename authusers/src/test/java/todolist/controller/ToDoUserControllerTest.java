package todolist.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import todolist.service.ToDoUserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("nodb")
public class ToDoUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testValidToken() throws Exception {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6Impvc2UifQ.uIvbXJ9yFo1WEtKzznJXI8ElQcNS6F-rmCsMEYOUpVE";

        // Valid token
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/validate")
                        .contentType("text/plain;charset=UTF-8")
                        .content(token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Token validation was successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").value("The provided token is valid"))
                .andReturn();
    }

}

