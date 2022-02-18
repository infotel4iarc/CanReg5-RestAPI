package fr.iarc.canreg.restapi.service;

import canreg.common.checks.CheckRecordService;
import canreg.common.database.Dictionary;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.exception.ServerException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = MetaDataService.class)
@ActiveProfiles("junit")
class MetaDataServiceTest {

    @MockBean
    private CanRegDAO canRegDAO;

    @MockBean
    private CheckRecordService checkRecordService;
    
    @Autowired
    private MetaDataService service;

    @Value("${registryFilesFolder:}")
    private String registryFilesFolder;

    private File testFileTRN;
    
    @BeforeEach
    void before() throws IOException {
        Mockito.reset(canRegDAO);
        testFileTRN = new File("src/test/resources/testFiles/TRN.xml");
        Assertions.assertTrue(StringUtils.isNotBlank(registryFilesFolder));
        File registryFileTRN = new File(registryFilesFolder, "TRN.xml");
        FileUtils.deleteQuietly(registryFileTRN.getParentFile());
        registryFileTRN.getParentFile().mkdirs();
        Files.copy(testFileTRN.toPath(), registryFileTRN.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    @Test
    void testGetXmlFileOk() throws IOException {
        String fileContent = service.getXmlRegistryFileContent("TRN");
        Assertions.assertNotNull(fileContent);
        
        String expectedContent = FileUtils.readFileToString(testFileTRN, "UTF-8");
        Assertions.assertEquals(expectedContent.replace("\r", ""), fileContent.replace("\r", ""));
    }

    @Test
    void testGetXmlFileNotFound() {
        String fileContent = service.getXmlRegistryFileContent("AAA");
        Assertions.assertNull(fileContent);
    }
    
    @Test
    void testGetDictionaries() {
        Map<Integer, Dictionary> dictionaryMap = new HashMap<>();
        dictionaryMap.put(0, new Dictionary());
        Mockito.when(canRegDAO.getDictionary()).thenReturn(dictionaryMap);
        
        Map<Integer, Dictionary> map = service.getDictionaries();
        Assertions.assertEquals(dictionaryMap, map);
    }

    @Test
    void testGetDictionary() {
        Map<Integer, Dictionary> dictionaryMap = new HashMap<>();
        Dictionary dictionary = new Dictionary();
        dictionary.setDictionaryID(123);
        dictionaryMap.put(1, dictionary);
        Mockito.when(canRegDAO.getDictionary()).thenReturn(dictionaryMap);
        
        Dictionary result = service.getDictionary(1);
        Assertions.assertEquals(result, dictionary);
    }

    void testGetXmlFileException() {
        String filePath = "src/test/resources/fileNotExisting.xml";
        ServerException exception = Assertions.assertThrows(ServerException.class, () -> service.getXmlRegistryFileContent(filePath));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Metadata file cannot be found: fileNotExisting.xml", exception.getMessage());
    }
}