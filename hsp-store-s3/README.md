# Getting Started with HSP CDL S3

### Overview
This example describes how you can upload or download a file using S3 access credentials 
generated using HSP CDL APIs. 

First you need to generate temporary access credentials using either of the following HSP APIs:

* `/store/cdl/[OrgId]/Study/[ResearchStudyId]/UploadCredential` API from CDL service
* `/store/cdl/[OrgId]/Study/[ResearchStudyId]/DownloadCredential` API from CDL service

Then using the access credentials, you can call `/s3/upload` API or `/s3/download` API in this project
**Note:** Use upload credential for upload API and download credential for download API

Before running the project, make sure you set your CDL S3 details in `application.properties` 

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Amazon S3 examples using AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-s3.html)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

