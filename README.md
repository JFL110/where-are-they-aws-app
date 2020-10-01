# Where are they : AWS App
![Java CI](https://github.com/JFL110/where-are-they-aws-app/workflows/Java%20CI/badge.svg) ![Upload to AWS Lambda](https://github.com/JFL110/where-are-they-aws-app/workflows/Upload%20to%20AWS%20Lambda/badge.svg) [![codecov](https://codecov.io/gh/JFL110/where-are-they-aws-app/branch/master/graph/badge.svg)](https://codecov.io/gh/JFL110/where-are-they-aws-app)

AWS application to track and display my location and photos.

## How it works
<p align="center">
  <img src="https://s3.eu-west-2.amazonaws.com/jamesleach.dev/static/map-back-end-diagram.svg" alt="Operation diagram"/>
</p>

- An Android application periodically uploads loactions to the this AWS Lambda in the background that stores them in a DynamoDB table. 
- A manual task takes photos that have been uploaded to an S3 bucket and extracts the GPS EXIF data.
- A cron job triggers a digestion of all the location and photo data and produces a JSON file hosted in S3.
- A JS React interface displays the location data on a map. The map is hosted [here](https://www.jamesleach.dev/where-are-they), repo [here](https://github.com/JFL110/jamesleach.dev)


## DevOps
This repo is built automatically on push using GitHub Actions. Changes to the 'version.properties' file trigger a redeployment to AWS Lambda. 

## Running locally
See [these notes](https://github.com/JFL110/where-are-they-aws-app/blob/master/running-locally.md).
