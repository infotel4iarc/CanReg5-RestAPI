package fr.iarc.canreg.restapi.service;

import canreg.common.checks.CheckRecordService;
import canreg.server.database.CanRegDAO;
import java.security.Principal;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Parent class for tests.
 */
public class DataServiceParentForJunit {

    @InjectMocks
    protected DataService dataService;
    @Mock
    protected Principal apiUserPrincipal;
    @Mock
    protected HoldingDbHandler holdingDbHandler;
    @Mock
    protected CanRegDAO canRegDAO;
    @Mock
    protected CheckRecordService checkRecordService;

    @Mock
    protected DerbySQLIntegrityConstraintViolationException dbException;
    
}
