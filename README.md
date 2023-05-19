# Document Microservice
This microservice provides document scanning functionality using ClamAV antivirus.
It uses Docker Compose to set up the ClamAV container.

## Prerequisites
Before running the microservice, make sure you have the following installed on your machine:

* Docker: Install Docker
* Docker Compose: Install Docker Compose

## Getting Started
Follow these steps to set up and run the microservice:

Clone the repository:

1) Run the Docker Compose command to start the ClamAV
2) Start the Spring Boot application

The microservice is now running and listening for document(resume) upload request. 

### You can send HTTP requests to the microservice to scan documents.

To upload a document, send a POST request to the following endpoint:
http://localhost:8080/documents/upload