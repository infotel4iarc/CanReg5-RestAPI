package fr.iarc.canreg.restapi.service;

import fr.iarc.canreg.restapi.exception.ServerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = MetaDataService.class)
@ActiveProfiles("junit")
class MetaDataServiceTest {

    @Autowired
    private MetaDataService service;

    @Value("${registryFilesFolder:}")
    private String registryFilesFolder;

    private File testFileTRN;
    
    @BeforeEach
    void before() throws IOException {
        testFileTRN = new File("src/test/resources/testFiles/TRN.xml");
        Assertions.assertTrue(StringUtils.isNotBlank(registryFilesFolder));
        File registryFileTRN = new File(registryFilesFolder, "TRN.xml");
        FileUtils.deleteQuietly(registryFileTRN.getParentFile());
        registryFileTRN.getParentFile().mkdirs();
        Files.copy(testFileTRN.toPath(), registryFileTRN.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    @Test
    void testGetXmlFileOk() throws IOException {
        String fileContent = service.getXmlRegistryFile("TRN");
        Assertions.assertNotNull(fileContent);
        
        String expectedContent = FileUtils.readFileToString(testFileTRN, "UTF-8");
        Assertions.assertEquals(expectedContent.replace("\r", ""), fileContent.replace("\r", ""));
    }

    @Test
    void testGetXmlFileNotFound() {
        String fileContent = service.getXmlRegistryFile("AAA");
        Assertions.assertNull(fileContent);
    }
    
    void testGetXmlFileException() {
        String filePath = "src/test/resources/fileNotExisting.xml";
        ServerException exception = Assertions.assertThrows(ServerException.class, () -> service.getXmlRegistryFile(filePath));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Metadata file cannot be found: fileNotExisting.xml", exception.getMessage());
    }
}