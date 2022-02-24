## Implementation
### Metadata GET entry points
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
  - 409: patient already exists with the same RegNo, or with the same PatientRecordID
    ```
    {
      "timestamp": 1645171082093,
      "status": 409,
      "error": "Conflict",
      "message": "Patient already exists with the same RegNo",
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
  - 400: validation error
  - 404: the linked patient is not found
  - 409: tumour already exists
- POST /api/source: create a source
  - 201: source created
  - 400: validation error
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

- PUT /api/patients: edit exists patient in holding db with idRecord 
  - 200: patient updated
  - 400: validation error
  - 404: the patient is not found
- PUT /api/tumours: edit exists tumour in holding db with id
  - 200: tumour updated
  - 400: validation error
  - 404: the tumour is not found
  - 409: the linked patient is not found

- PUT /api/sources: edit exists source in holding db with idRecord
  - 200: source updated
  - 400: validation error
  - 404: the source is not found
  - 409: the linked tumour is not found


### Business GET entry points
- Only for development purposes, will not be delivered (= no risk of data leak outside of Canreg)
- TODO

### Bulk import
- POST /bulk/import/{dataType}/{encodingName}/{separatorName}/{behaviour}/{writeOrTest}
  - input data
    - csvFile: multipart/form-data
    - dataType: PATIENT or TUMOUR or SOURCE
    - encodingName: a valid charset name, like UTF-8
    - separatorName: TAB or COMMA
    - behaviour: 
      - CREATE_ONLY: create records only (no update of existing record)
      - to be implemented: REJECT, UPDATE, OVERWRITE
    - writeOrTest: 
      - WRITE: write the data 
      - TEST: test only
  - result:
    - 200: the file was processed, with or without warning and errors
    ```
    Starting to import patients from sources_small.tsv
    1: OK
    2: KO: Tumour does not exist: INSERT on table 'SOURCE' caused a violation of foreign key constraint 'SQL220209160256010' for key (200662160101).  The statement has been rolled back.
    3: KO: [{level='error', variable='date', value='1945-01-13', message='this date is not a valid date yyyyMMdd'}, {level='error', variable='source', value='062', message='this code is not in the dictionary'}]
    
    Finished: 3 items in input: 1 written, 2 skipped.    
    ```
    - 400: error in an input parameter
    ```
    {
    "timestamp": 1645721039266,
    "status": 400,
    "error": "Bad Request",
    "message": "behaviour must be a valid value, like: CREATE_ONLY",
    "path": "/bulk/import/SOURCE/UTF-8/TAB/CREATE_ONLY2/WRITE"
    }    
    ```
    - 500: server error

- TODO: Improvement to be implemented:
  - Asynchronous process to avoid a too long http request (risk of timeout): 
    - create a worker 
    - return the id of the worker 
    - add entry point to get the status of the worker
    - add entry point to get the final report when the worker has finished 
  

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
