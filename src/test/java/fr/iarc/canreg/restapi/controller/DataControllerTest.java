package fr.iarc.canreg.restapi.controller;

import canreg.common.checks.CheckRecordService;
import canreg.common.database.Patient;
import canreg.common.database.User;
import fr.iarc.canreg.restapi.service.DataService;
import fr.iarc.canreg.restapi.service.HoldingDbHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


//@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {DataController.class, DataService.class})
@ActiveProfiles("junit")
@AutoConfigureMockMvc
class DataControllerTest {

    @Autowired
    DataController controller;

    @MockBean
    private DataService dataService;

    @MockBean
    private HoldingDbHandler holdingDbHandler;

    @MockBean
    private CheckRecordService checkRecordService;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                // .apply(SecurityMocspringSecurity())
                .build();
    }    
    @Test
    void testGetPatientFound() throws Exception {
        Patient patient = new Patient();
        patient.setVariable("PRID", 123);
        patient.setVariable("famn", "Smith");
        User user = new User();
        user.setUserName("apiUser");
        Mockito.when(dataService.getPatient(123)).thenReturn(patient);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/patients/123")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.famn").value("Smith"))
        ;
        Mockito.verify(dataService).getPatient(123);
        // ResponseEntity<Patient> patient;
        // patient = controller.getPatient(0);
        // Assertions.assertFalse(Assertions.assertEquals(patient));
    }

    @Test
    void testGetPatientNotFound() throws Exception {
         mockMvc.perform(
             MockMvcRequestBuilders.get("/api/patients/123")
         ).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(dataService).getPatient(123);
        // ResponseEntity<Patient> patient;
        // patient = controller.getPatient(0);
        // Assertions.assertFalse(Assertions.assertEquals(patient));
    }


    @Test
    void testgetSourcesNotFound()throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sources/12")).andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(dataService).getSource(12);

    }


}
