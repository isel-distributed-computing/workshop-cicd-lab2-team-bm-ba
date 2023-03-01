package todolist.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import todolist.model.ToDoListItem;
import todolist.service.ToDoListService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("nodb")
public class ToDoListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoListService toDoListService;

    @Test
    public void testCreateToDoListItem() throws Exception {
        // Mock ToDoListItem returned by service
        ToDoListItem expectedItem = new ToDoListItem("JohnDoe", "Do laundry");

        // Mock authorization header
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        // Mock ToDoListService.createToDoListItem() method to return the expected item
        when(toDoListService.createToDoListItem(any(String.class), any(String.class), any(String.class)))
                .thenReturn(expectedItem);

        // Send request to the controller
        mockMvc.perform(MockMvcRequestBuilders.post("/todolist")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"JohnDoe\", \"description\": \"Do laundry\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(expectedItem.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedItem.getDescription()));
    }

}
