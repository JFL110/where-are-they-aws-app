
terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }

  backend "remote" {
    organization = "jim"

    workspaces {
      name = "where-are-they-aws-app"
    }
  }
}

variable "DYNAMO_DB_REMOTE_CREDENTIAL_KEY" {
  type = string
}
variable "DYNAMO_DB_REMOTE_CREDENTIAL_PASSWORD" {
  type = string
}
variable "S3_ACCESS_KEY" {
  type = string
}
variable "S3_JSON_BUCKET_NAME" {
  type = string
}
variable "S3_JSON_FILE_NAME" {
  type = string
}
variable "S3_SECRET_KEY" {
  type = string
}
variable "SECURITY_KEY_LIVE" {
  type = string
}

provider "aws" {
  region = "eu-west-2"
}

resource "aws_iam_role" "iam-my-location-lambda" {
  name = "iam-my-location-lambda"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_lambda_function" "lambda-one" {
  filename      = "./build/distributions/where-are-they-aws-app.zip"
  function_name = "my-location-root-handler"
  description   = "My location points handler"
  role          = aws_iam_role.iam-my-location-lambda.arn
  handler       = "org.jfl110.mylocation.MyLocationRootFunctionHandler"

  timeout     = 250
  memory_size = 640

  source_code_hash = filebase64sha256("./build/distributions/where-are-they-aws-app.zip")

  runtime = "java8"

  environment {
    variables = {
      DYNAMO_DB_REMOTE_CREDENTIAL_KEY      = var.DYNAMO_DB_REMOTE_CREDENTIAL_KEY
      DYNAMO_DB_REMOTE_CREDENTIAL_PASSWORD = var.DYNAMO_DB_REMOTE_CREDENTIAL_PASSWORD
      S3_ACCESS_KEY                        = var.S3_ACCESS_KEY
      S3_JSON_BUCKET_NAME                  = var.S3_JSON_BUCKET_NAME
      S3_JSON_FILE_NAME                    = var.S3_JSON_FILE_NAME
      S3_SECRET_KEY                        = var.S3_SECRET_KEY
      SECURITY_KEY_LIVE                    = var.SECURITY_KEY_LIVE
    }
  }
}


resource "aws_api_gateway_rest_api" "lambda-api" {
  name        = "my-location-api-gateway"
  description = "API Gateway deployed via Terraform"
}

resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda-one.function_name
  principal     = "apigateway.amazonaws.com"

  # The "/*/*" portion grants access from any method on any resource
  # within the API Gateway REST API.
  source_arn = "${aws_api_gateway_rest_api.lambda-api.execution_arn}/*/*"
}

resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.lambda-api.id
  parent_id   = aws_api_gateway_rest_api.lambda-api.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = aws_api_gateway_rest_api.lambda-api.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda" {
  rest_api_id = aws_api_gateway_rest_api.lambda-api.id
  resource_id = aws_api_gateway_method.proxy.resource_id
  http_method = aws_api_gateway_method.proxy.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.lambda-one.invoke_arn
}

resource "aws_api_gateway_method" "proxy_root" {
  rest_api_id   = aws_api_gateway_rest_api.lambda-api.id
  resource_id   = aws_api_gateway_rest_api.lambda-api.root_resource_id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda_root" {
  rest_api_id = aws_api_gateway_rest_api.lambda-api.id
  resource_id = aws_api_gateway_method.proxy_root.resource_id
  http_method = aws_api_gateway_method.proxy_root.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.lambda-one.invoke_arn
}

resource "aws_api_gateway_deployment" "api-demo" {
  depends_on = [
    aws_api_gateway_integration.lambda,
    aws_api_gateway_integration.lambda_root,
  ]

  rest_api_id = aws_api_gateway_rest_api.lambda-api.id
  stage_name  = "main"
}

# This is to optionally manage the CloudWatch Log Group for the Lambda Function.
# If skipping this resource configuration, also add "logs:CreateLogGroup" to the IAM policy below.
resource "aws_cloudwatch_log_group" "example" {
  name              = "/aws/lambda/${aws_lambda_function.lambda-one.function_name}"
  retention_in_days = 14
}

# See also the following AWS managed policy: AWSLambdaBasicExecutionRole
resource "aws_iam_policy" "lambda_logging" {
  name        = "my-location-lambda-logging-policy"
  path        = "/"
  description = "IAM policy for logging from my location lambda"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.iam-my-location-lambda.name
  policy_arn = aws_iam_policy.lambda_logging.arn
}

output "base_url" {
  value = aws_api_gateway_deployment.api-demo.invoke_url
}
