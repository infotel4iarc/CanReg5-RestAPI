package fr.iarc.canreg.restapi.controller;

import canreg.common.database.Patient;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.service.DataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


//@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {DataController.class, DataService.class})
@ActiveProfiles("junit")
@AutoConfigureMockMvc
public class DataControllerTest {

    @Autowired
    DataController controller;

    @MockBean
    private CanRegDAO canRegDAO;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPatientFound() throws Exception {
        Patient patient = new Patient();
        patient.setVariable("PRID", 123);
        patient.setVariable("famn", "Smith");
        Mockito.when(canRegDAO.getRecord(123, "Patient", false)).thenReturn(patient);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/123")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.famn").value("Smith"))
        ;
        Mockito.verify(canRegDAO).getRecord(123, "Patient", false);
        // ResponseEntity<Patient> patient;
        // patient = controller.getPatient(0);
        // Assertions.assertFalse(Assertions.assertEquals(patient));
    }

    @Test
    void testGetPatientNotFound() throws Exception {
         mockMvc.perform(
             MockMvcRequestBuilders.get("/api/patients/123")
         ).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(canRegDAO).getRecord(123, "Patient", false);
        // ResponseEntity<Patient> patient;
        // patient = controller.getPatient(0);
        // Assertions.assertFalse(Assertions.assertEquals(patient));
    }


    @Test
    void testgetSourcesNotFound()throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sources/12")).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(canRegDAO).getRecord(12,"Source",false);

    }


}
