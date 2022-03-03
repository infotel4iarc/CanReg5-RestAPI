package fr.iarc.canreg.restapi.controller;

import canreg.common.Globals;
import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.Patient;
import canreg.common.database.User;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.security.config.WebSecurityConfig;
import fr.iarc.canreg.restapi.security.service.CanregDbDetailService;
import fr.iarc.canreg.restapi.service.DataService;
import fr.iarc.canreg.restapi.service.HoldingDbHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DataController.class, CanregDbDetailService.class, WebSecurityConfig.class})
@AutoConfigureMockMvc
@WebMvcTest(properties = {"server.error.include-message=always", "role=ANALYST"})
@WithMockUser(value = "junituser", roles = "ANALYST")
class DataControllerPatientTest {

    @Autowired
    private DataController controller;

    @MockBean
    private static CanRegDAO canRegDAO;

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
    void beforeEach() {
        User user = new User();
        user.setUserName("junituser");
        user.setUserRightLevel(Globals.UserRightLevels.ANALYST);
        Mockito.when(canRegDAO.getUserByUsername("junituser")).thenReturn(user);
    }
    
    @Test
    void testGetPatientFound() throws Exception {
        Patient patient = new Patient();
        patient.setVariable("regno", "20044892");
        patient.setVariable("famn", "Smith");
        Mockito.when(dataService.getPatient(123)).thenReturn(patient);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/patients/123")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.regno").value("20044892"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.famn").value("Smith"))
        ;
        Mockito.verify(dataService).getPatient(123);
    }

    @Test
    void testGetPatientNotFound() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/patients/123")
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
        ;
        Mockito.verify(dataService).getPatient(123);
    }

    @Test
    void testCreatePatient() throws Exception {
        Patient patient = new Patient();
        patient.setVariable("regno", "20044892");
        patient.setVariable("famn", "Smith");
        
        PatientDTO resultPatient = PatientDTO.from(patient, null);
        Mockito.when(dataService.savePatient(Mockito.any(PatientDTO.class), Mockito.any())).thenReturn(resultPatient);
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("" +
                                "{" +
                                "  \"variables\": {" +
                                "    \"regno\": \"20044892\"," +
                                "    \"sex\": \"2\"," +
                                "    \"patientupdatedby\": \"userjunit\"," +
                                "    \"famn\": \"Smith\"," +
                                "    \"patientupdatedate\": \"20110510\"," +
                                "    \"patientrecordid\": \"2004489201\"," +
                                "    \"birthd\": \"20150120\"" +
                                "  }" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                // .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.regno").value("20044892"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.famn").value("Smith"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getPatient(Mockito.any());
    }

    @Test
    void testCreatePatientWithError() throws Exception {
        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("birthd", "1905-01-20", "this date is not a valid date yyyyMMdd", true));
        Mockito.when(dataService.savePatient(Mockito.any(PatientDTO.class), Mockito.any())).thenThrow(new VariableErrorException(checkMessages.toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("" +
                                "{" +
                                "  \"variables\": {" +
                                "    \"regno\": \"20044892\"," +
                                "    \"sex\": \"2\"," +
                                "    \"patientupdatedby\": \"userjunit\"," +
                                "    \"famn\": \"Smith\"," +
                                "    \"patientupdatedate\": \"20110510\"," +
                                "    \"patientrecordid\": \"2004489201\"," +
                                "    \"birthd\": \"1905-01-20\"" +
                                "  }" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.status().reason(
                        "Validation failed: [{level='error', variable='birthd', value='1905-01-20', message='this date is not a valid date yyyyMMdd'}]"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getPatient(Mockito.any());
    }

    @Test
    void testCreatePatientExists() throws Exception {
        Mockito.when(dataService.savePatient(Mockito.any(PatientDTO.class), Mockito.any()))
                .thenThrow(new DuplicateRecordException("The Patient exists"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("" +
                                "{" +
                                "  \"variables\": {" +
                                "    \"regno\": \"20044892\"" +
                                "  }" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.status().reason("The Patient exists"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getPatient(Mockito.any());
    }


}
