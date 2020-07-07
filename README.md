# Where are they : AWS App
![Java CI](https://github.com/JFL110/where-are-they-aws-app/workflows/Java%20CI/badge.svg) ![Upload to AWS Lambda](https://github.com/JFL110/where-are-they-aws-app/workflows/Upload%20to%20AWS%20Lambda/badge.svg) [![codecov](https://codecov.io/gh/JFL110/where-are-they-aws-app/branch/master/graph/badge.svg)](https://codecov.io/gh/JFL110/where-are-they-aws-app)

AWS application to track and display my location and photos.

## How it works
<p align="center">
  <img src="https://s3.eu-west-2.amazonaws.com/jamesleach.dev/static/map-back-end-diagram.svg" alt="Operation diagram"/>
</p>

- An Android application periodically uploads loactions to the this AWS Lambda in the background that stores them in a DynamoDB table. 
- A manual tasks takes photos that have been uploaded to an S3 bucket and extracts the GPS EXIF data.
- A cron job triggers a digestion of all the location and photo data and produces a JSON file hosted in S3.
- A JS React interface displays the location data on a map. The map is hosted [here](https://jfl110.github.io/where-are-they/), repo [here](https://github.com/JFL110/where-are-they/)


## DevOps
This repo is built automatically on push using GitHub Actions. Changes to the 'version.properties' file trigger a redeployment to AWS Lambda. 

## Running locally
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
