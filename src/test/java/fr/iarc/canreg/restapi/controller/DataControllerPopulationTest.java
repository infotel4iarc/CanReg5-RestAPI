package fr.iarc.canreg.restapi.controller;

import canreg.common.Globals;
import canreg.common.database.PopulationDataset;
import canreg.common.database.User;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.security.config.WebSecurityConfig;
import fr.iarc.canreg.restapi.security.service.CanregDbDetailService;
import fr.iarc.canreg.restapi.service.DataService;
import fr.iarc.canreg.restapi.service.HoldingDbHandler;
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
public class DataControllerPopulationTest {

    @Autowired
    private DataController controller;

    @MockBean
    private static CanRegDAO canRegDAO;

    @MockBean
    private DataService dataService;

    @MockBean
    private HoldingDbHandler holdingDbHandler;

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
    void testGetPopulationFound() throws Exception {
        PopulationDataset populationDataset = new PopulationDataset();

        populationDataset.setPopulationDatasetID(1);
        populationDataset.setPopulationDatasetName("get population");

        Mockito.when(dataService.getPopulation(1)).thenReturn(populationDataset);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/population/1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.populationDatasetID").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.populationDatasetName").value("get population"))
        ;
        Mockito.verify(dataService).getPopulation(1);
    }

    @Test
    void testGetPopulationNotFound() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/population/1")
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
        ;
        Mockito.verify(dataService).getPopulation(1);
    }

    @Test
    void testCreatePopulation() throws Exception {
        PopulationDataset populationDataset = new PopulationDataset();

        populationDataset.setPopulationDatasetID(1);
        populationDataset.setPopulationDatasetName("create population");

        Mockito.when(dataService.createPopulation(Mockito.any())).thenReturn(populationDataset);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/populations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("" +
                                "{" +
                                "    \"populationDatasetID\": \"20\"," +
                                "    \"populationDatasetName\": \"ew Europen Standard Population\"," +
                                "    \"source\": \"World Health Organization\"," +
                                "    \"sizeOfGroups\": \"5\"," +
                                "    \"maxAge\": \"85\"" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.populationDatasetID").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.populationDatasetName").value("create population"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getPopulation(Mockito.any());
    }

    @Test
    void testCreatePopulationExists() throws Exception {
        Mockito.when(dataService.createPopulation(Mockito.any())).thenThrow(new DuplicateRecordException("The population dataSet already exists"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/populations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("" +
                                "{" +
                                "    \"populationDatasetID\": \"20\"," +
                                "    \"populationDatasetName\": \"New Europen Standard Population\"" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.status().reason("The population dataSet already exists"))
        ;
        Mockito.verify(dataService, Mockito.times(0)).getPopulation(Mockito.any());
    }
    @Test
    void testEditPopulation() throws Exception {
        PopulationDataset populationDataset = new PopulationDataset();

        populationDataset.setPopulationDatasetID(20);
        populationDataset.setPopulationDatasetName("edit population");

        Mockito.when(dataService.editPopulation(Mockito.any())).thenReturn(populationDataset);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/populations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("" +
                                "{" +
                                "    \"populationDatasetID\": \"20\"," +
                                "    \"populationDatasetName\": \"edit population\"," +
                                "    \"source\": \"World Health Organization\"," +
                                "    \"sizeOfGroups\": \"5\"," +
                                "    \"maxAge\": \"85\"" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.populationDatasetID").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.populationDatasetName").value("edit population"))
        ;
    }

    @Test
    void testEditPopulationNotExists() throws Exception {
        Mockito.when(dataService.editPopulation(Mockito.any())).thenThrow(new NotFoundException("The population dataSet not exists"));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/populations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("" +
                                "{" +
                                "    \"populationDatasetID\": \"20\"," +
                                "    \"populationDatasetName\": \"New Europen Standard Population\"" +
                                "}")
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("The population dataSet does not Exist"))
        ;
    }
}
