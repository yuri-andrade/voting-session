# voting-session-service
As the back end challenge demands, this application serves the purpose of creating a voting session on a topic, in which users can vote in favor or against.

# Getting Started
It was provided a docker-compose.yml file to facilitate the application review, in order to use it, it must be built by gradle before
`> gradle build` and then `> docker-compose up -d`

### Resources
* Once the application has been deployed, all the endpoint documentation should be available at http://server:port/swagger-ui.html
(eg: http://localhost:8080/swagger-ui.html).
* RabbitMQ: http://localhost:15672/ (guest:guest)
* MongoDB: mongodb://localhost:27017 (no auth)

## Voting Session
* To create a voting session, one must specify the id from the topic under discussion.
* The session will last as the specified in the request 'duration' field, if no 'duration' is specified, the session will last 1 minute.
* The result can be followed in real time under the GET /v1/session/result/{id} endpoint.
* When the session is created, a message with ttl as the session duration is sent to a message broker.
* The application listens to the dead letter queue in order to generate a voting session result message to be consumed by others services.

## Voting
* One user can vote only once in a voting session, his cpf will be validated on another service to guarantee that he's able to vote.
* One user can only vote on an active voting session, otherwise the vote will be discarded.

### Stack
Information about the technology used in the application:
* Java 11
* Lombok
* Spring boot 2.5.0
* RabbitMQ
* MongoDB
* springdoc-openapi

## TODO
* Increase code coverage
* Add architecture documentation
* Cache
* Work on performance/stress test (my first approach "failed" because the system I was using to generate valid CPFs was bottlenecking the test)
