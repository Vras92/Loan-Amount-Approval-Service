# Loan Amount Approval Service API

## Description

It's a RESTful service that would enable loan preparators to send loan contracts to loan managers 
for approval. Loan preparator must specify the customer’s ID, the amount that customer wants to 
loan and pick managers which need to approve loan request. After the specified managers have added 
their 
approvals the contract will be automatically sent to the customer.

## Dependencies

There are a number of dependencies used in the project. Browse the build.gradle file for details of libraries and versions used.

## Building the project

You will need:
* Java JDK 17 or higher
* Gradle
* Git

## Docker

Build:
* docker build -t approval .

Run:
* docker run -p 9080:9080 approval

## A collection for Postman

There is a json file: Loan Approval Requests.postman_collection.json

You can import a collection (json) to Postman. There are prepared bodies as examples for GET, POST 
and PUT requests.

## application.properties file

#### Sent contracts statistics during a period in minutes:
statistics.sent.contracts.minutes.default=1

#### Cache update time in minutes for statistics:
cache.update.time=1

## URLs

#### All contracts - GET
http://localhost:9080/api/contracts

#### Contract by customer ID - GET
http://localhost:9080/api/contracts/1-123-12345G

#### Statistics - GET
http://localhost:9080/api/contracts/statistics

#### New loan request - POST
http://localhost:9080/api/contracts/create

    {
        "customerId": "1-123-12345G",
        "loanAmount": 4050.0,
        "approvers": [
            {
                "username": "P998ABC"
            }
        ],
        "loanType": "GENERAL"
    }

#### Manager's decision - PUT
http://localhost:9080/api/contracts/decision

    {
        "approvers": [
            {
                "username": "P998ABC",
                "status": "APPROVED"
            }
        ],
        "customerId": "1-123-12345G"
    }


#### Delete loan request by customer ID - DELETE
http://localhost:9080/api/contracts/1-123-12345G
