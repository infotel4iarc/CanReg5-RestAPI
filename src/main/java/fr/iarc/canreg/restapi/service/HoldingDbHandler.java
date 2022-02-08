package fr.iarc.canreg.restapi.service;

import canreg.common.database.User;
import canreg.server.CanRegServerImpl;
import canreg.server.database.CanRegDAO;
import canreg.server.management.SystemDescription;
import java.io.IOException;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for the holding databases.
 */
@RequiredArgsConstructor
public class HoldingDbHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(HoldingDbHandler.class);

  public final CanRegDAO canRegDAO;
  public final Properties dbProperties;
  public final SystemDescription mainSystemDescription;

  /**
   * Returns a CanRegDAO for the holding database of the API user
   * @param apiUser the api user
   * @return CanRegDAO
   */
  public CanRegDAO getDaoForApiUser(User apiUser) {
    String registryCode = CanRegServerImpl.getRegistryCodeForApiHolding(
        mainSystemDescription.getRegistryCode(), apiUser, false);
    return new CanRegDAO(registryCode, mainSystemDescription.getSystemDescriptionDocument(), dbProperties);
  }

}
