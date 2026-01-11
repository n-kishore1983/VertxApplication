Scope: The scope of this application is to create a vert.x application with verticles in java, grrovy
This application has 3 verticles. <br/>
Customers Verticle: To retrieve from 3 different verticles and consolidate the response <br/>
CreditScore Verticle: To retrieve credit score of a customer<br/>
CustomerContactsVerticle: To retrieve the address details<br/>
Employment Verticle: Retrieve the employment details of a customer<br/>

This application is deployed as an Azure Container App. <br/>
Steps to Deploy:<br/>
1. mvn clean install
2. create a docker image <br/>
   docker build --platform linux/amd64 -t vertx_test . <br/>
3. Login to Azure <br/>
   az acr login --name vertxcontainerregistry <br/>
4. Tag the docker image <br/>
   docker tag vertx_test vertxcontainerregistry.azurecr.io/vertx_test <br/>
5. Push the docker image to ACR <br/>
   docker push vertxcontainerregistry.azurecr.io/vertx_test <br/>
6. In Azure create container instance
