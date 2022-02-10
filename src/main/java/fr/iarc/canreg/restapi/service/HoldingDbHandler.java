package fr.iarc.canreg.restapi.service;

import canreg.common.database.HoldingDbCommon;
import canreg.server.CanRegServerImpl;
import canreg.server.database.CanRegDAO;
import canreg.server.management.SystemDescription;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Handler for the holding databases.
 */
@RequiredArgsConstructor
public class HoldingDbHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HoldingDbHandler.class);

    private final Properties dbProperties;
    private final SystemDescription mainSystemDescription;

    /**
     * The map of the CanRegDAOs: key = userName of api user, value = CanRegDAO
     */
    private final Map<String, CanRegDAO> mapDaos = new TreeMap<>();

    /**
     * Returns a CanRegDAO for the holding database of the API user
     * @param apiUserName the userName of the api user
     * @return CanRegDAO
     */
    public CanRegDAO getDaoForApiUser(String apiUserName) {
        return mapDaos.computeIfAbsent(apiUserName, s -> {
            // Additional variables for holding db
            SystemDescription systemDescriptionForHoldingDB
                    = HoldingDbCommon.buildSystemDescriptionForHoldingDB(mainSystemDescription);

            String registryCode = CanRegServerImpl.getRegistryCodeForApiHolding(
                    mainSystemDescription.getRegistryCode(), apiUserName, false);
            Document document = systemDescriptionForHoldingDB.getSystemDescriptionDocument();
            // Build the dao
            return new CanRegDAO(registryCode, document, dbProperties);
        });
    }

}
