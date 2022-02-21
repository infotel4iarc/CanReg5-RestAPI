## Implementation
### Metadata GET entry points
DONE  
- GET /api/meta/system/{registryCode}
- GET /api/meta/dictionary/{dictionaryId}
- GET /api/meta/dictionary/all

### Business SET entry points
- POST /api/patients: create a patient 
  - 201: patient created
    - the content of the created Patient is returned with created ids and with possible warnings in the variable "format_errors"
    ```
    "format_errors": [
      {
        "variableName": "birthd",
        "variableValue": "",
        "message": "this variable is mandatory",
        "error": false
      }
    ]
    ```
  - 409: patient already exists
    ```
    {
      "timestamp": 1645171082093,
      "status": 409,
      "error": "Conflict",
      "message": "The record already exists",
      "path": "/api/setPatients"
    }    
    ```
  - 400: validation error
    ```
    {
      "timestamp": 1645170918683,
      "status": 400,
      "error": "Bad Request",
      "message": "Validation failed: [{level='error', variable='birthd', value='1920-01-20', message='this date is not a valid date yyyyMMdd'}]",
      "path": "/api/setPatients"
    }    
    ```
- POST /api/tumour: create a tumour 
  - 201: tumour created
  - 404: the linked patient is not found
  - 409: tumour already exists
- POST /api/source: create a source
    - 201: source created
    - 404: the linked tumour is not found
    - 409: source already exists
- POST /api/population: create a new population dataset
  - creates a new dataset in the main database
  - uses the same json structure as the export of a dataset in CanReg5
  - error if the dataset already exists
- PUT /api/population: edit a exists population dataset
  - edit exists population dataset in the main database
  - uses the same json structure as the export of a dataset in CanReg5
  - error if the dataset already not exists
  

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
