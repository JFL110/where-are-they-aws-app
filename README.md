# Where are they : AWS App
![Java CI](https://github.com/JFL110/where-are-they-aws-app/workflows/Java%20CI/badge.svg) ![Upload to AWS Lambda](https://github.com/JFL110/where-are-they-aws-app/workflows/Upload%20to%20AWS%20Lambda/badge.svg) [![codecov](https://codecov.io/gh/JFL110/where-are-they-aws-app/branch/master/graph/badge.svg)](https://codecov.io/gh/JFL110/where-are-they-aws-app)

AWS application to track and display my location.

## Running locally

### DynamoDB
Start DynamoDB locally with
```
java -Djava.library.path=./DynamoDBLocal_lib/ -jar DynamoDBLocal.jar -sharedDb
```
- Access shell at : http://localhost:8000/shell/ 
- Template statement for deleting table is in the shell interface.

### SAM
- Install DynamoDB and SAM local
- Configure DynamoDB endpoint address as the local actual address of your machine (e.g. via 'ip addr show'). As SAM runs within docker, localhost of the 127.0.0.1 cannot be used.
- Create a fat jar of the Lambda
```
gradle shadowJar
```

- Start SAM local
```
sudo sam local start-api -t sam.yml
```
