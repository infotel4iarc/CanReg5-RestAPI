## Implementation
### Metadata GET entry points
DONE  
- GET /api/meta/system/{registryCode}
- GET /api/meta/dictionary/{dictionaryId}
- GET /api/meta/dictionary/all

### Business GET entry points
- Only for development purposes, will not be delivered (= no risk of data leak outside of Canreg)
- TODO

### Security
TODO
- CanReg5: implement a new role in CanReg: REST-API
  - can only access to CanReg through the Rest Api
  - cannot log in CanReg client
  - 
- Rest-api
  - Use Spring Security
  - Step 1: Basic Authentication
  - Step 2: Custom authentication with a custom token "username" + "-" + "user password"

### Import the data in a holding database
TODO
- The client wants the data to be imported in a holding database = not the current database
- Errors have to be returned to the caller: validation errors, warning messages...
- Look at the existing "import" feature in CanReg
    - src/canreg/client/gui/importers/Import.java
- Create one holding database for each connected rest user
    - Probably use the username to name the holding database
- As a first step, if this is easier, it would be possible to import csv files, like the existing import feature.


## Local run
- Main class: CanRegApiApplication
- VM options:  
  the following option is needed to use application-local.properties:
  ```
  -Dspring.profiles.active=local
  ```
