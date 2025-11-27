package davinci.edu.ar.excusasSA.controller;


import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("GET /empleados debe devolver lista de empleados en formato JSON")
     void debeRetornarUnaListaDeEmepleados() throws Exception {
        employeeRepository.save(new Receptionist( "Kevinha", "kevinha@excusas.com", 100L, new Normal()));

        mockMvc.perform(get("/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].name").value("TestUser"))
                .andExpect((ResultMatcher) jsonPath("$[0].email").value("test@mail.com"));
    }

}
