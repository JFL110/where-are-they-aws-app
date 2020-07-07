
##  Notes on Running Locally
Follow guidelines to install DynamoDB and SAM locally.

### DynamoDB
Start DynamoDB locally with
```
java -Djava.library.path=./DynamoDBLocal_lib/ -jar DynamoDBLocal.jar -sharedDb
```
- Access shell at : http://localhost:8000/shell/ 
- Template statements for deleting and modifying tables are in the shell interface.

### SAM
- Install DynamoDB and SAM local
- Configure DynamoDB endpoint address as the local actual address of your machine (e.g. via 'ip addr show'). As SAM runs within docker, localhost of the 127.0.0.1 cannot be used.
- Create a fat jar of the Lambda
```
gradle shadowJar
```

- Start SAM local
```
sudo sam local start-api -t sam.yml --skip-pull-image
```

- Single command to run do both:
```
clear;  gradle shadowjar; sudo sam local start-api -t sam.yml --skip-pull-image
```
