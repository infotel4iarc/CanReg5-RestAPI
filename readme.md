## Implementation
### Metadata GET entry points
DONE  
- GET /api/meta/system/{registryCode}
- GET /api/meta/dictionary/{dictionaryId}
- GET /api/meta/dictionary/all

### Business GET entry points
- SET /api/patients: this method save a patient if not exist or returns exception (duplicate key)
- SET /api/tumour: this method save a tumour
                     * if it does not exist in the database and also if the linked patient exists 
                      and otherwise returns a patient exception does not exist
                     *if the tumor exists returns a duplicate key exception
- SET /api/source: this method save a source
                     * if it does not exist in the database and also if the linked tumour exists
                       and otherwise returns a tumour exception does not exist
                     *if the source exists returns a duplicate key exception

### Business GET entry points
- Only for development purposes, will not be delivered (= no risk of data leak outside of Canreg)
- TODO

### Security
- CanReg5: TODO 
  - implement a new role in CanReg: REST-API
  - can only access to CanReg through the Rest Api
  - cannot log in CanReg client
- Rest-api:
  - Use Spring Security
  - Use Basic Authentication that is a standard = the api clients will know how to use it 

### Import the data in a holding database
- The data are imported in a holding database = not the current database.   
  This is similar to what is done in the existing "import" feature in CanReg5.
- At startup, if not already existing, CanReg5 server creates one holding database for each rest user. 
  - The database schema is: "HOLDING_" + registryCode + "_" + userName (without spaces and quotes) 
    See CanRegServerImpl.getRegistryCodeForApiHolding in CanReg5.
- One CanRegDAO is created for each api user that calls the API: see HoldingDBHandler
  - The dao are stored in a mps to avoid creating them at each call. 
- TODO: Errors have to be returned to the caller: validation errors, warning messages...

### Ids
- Delete technical IDs: remove technical ids in enter set methods because it generates automatic
  *patient: prid
  *tumour: trid
  *source: srid

## Local run
- Main class: CanRegApiApplication
- VM options:  
  the following option is needed to use application-local.properties:
  ```
  -Dspring.profiles.active=local
  ```
