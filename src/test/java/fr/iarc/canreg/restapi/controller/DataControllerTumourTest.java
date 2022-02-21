package fr.iarc.canreg.restapi.controller;

import canreg.common.Globals;
import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.Patient;
import canreg.common.database.Tumour;
import canreg.common.database.User;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.model.TumourDTO;
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
class DataControllerTumourTest {

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
    void testGetTumourFound() throws Exception {
        Tumour tumour = new Tumour();
        tumour.setVariable("tumourid", "199920920101");
        Mockito.when(dataService.getTumour(123)).thenReturn(tumour);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/tumours/123")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.tumourid").value("199920920101"))
        ;
        Mockito.verify(dataService).getTumour(123);
    }

    @Test
    void testGetTumourNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tumours/123"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
        ;
        Mockito.verify(dataService).getTumour(123);
    }

    @Test
    void testCreateTumour() throws Exception {
        Tumour tumour = new Tumour();
        tumour.setVariable("tumourid", "199920920101");
        
        TumourDTO result = TumourDTO.from(tumour, null);
        Mockito.when(dataService.saveTumour(Mockito.argThat(argument -> "199920920101".equals(argument.getVariables().get("tumourid"))), Mockito.any())).thenReturn(result);
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tumours")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"variables\": {" +
                                "    \"tumourid\": \"199920920101\"," +
                                "    \"extent\": \"LC\"" +
                                "  }" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.variables.tumourid").value("199920920101"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getTumour(Mockito.any());
    }

    @Test
    void testCreateTumourWithError() throws Exception {
        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("age", "89", "this value is not an integer", true));
        Mockito.when(dataService.saveTumour(Mockito.any(), Mockito.any())).thenThrow(new VariableErrorException(checkMessages.toString()));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tumours")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"variables\": {" +
                                "    \"tumourid\": \"199920920101\"," +
                                "    \"extent\": \"LC\"," +
                                "    \"age\": \"89\"" +
                                "  }" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.status().reason(
                        "Validation failed: [{level='error', variable='age', value='89', message='this value is not an integer'}]"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getTumour(Mockito.any());
    }

    @Test
    void testCreateTumourExists() throws Exception {
        Mockito.when(dataService.saveTumour(Mockito.any(), Mockito.any())).thenThrow(new DuplicateRecordException("The Tumour exists"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tumours")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"variables\": {" +
                                "    \"tumourid\": \"199920920101\"," +
                                "    \"extent\": \"LC\"," +
                                "    \"age\": \"89\"" +
                                "  }" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.status().reason("The tumour already exists"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getTumour(Mockito.any());
    }

    @Test
    void testCreateTumourPatientNotFound() throws Exception {
        Mockito.when(dataService.saveTumour(Mockito.any(), Mockito.any())).thenThrow(new NotFoundException("The Patient does not exist"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/tumours")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{" +
                                "  \"variables\": {" +
                                "    \"tumourid\": \"199920920101\"," +
                                "    \"extent\": \"LC\"," +
                                "    \"age\": \"89\"" +
                                "  }" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("A record is not found"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getTumour(Mockito.any());
    }


}
