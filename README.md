# Ryanair - Java/Spring - Interconnecting Flights

The purpose of this project is to create a Spring Boot based RESTful API application which serves information about possible direct and interconnected flights (maximum 1 stop) based on the data consumed from external APIs.

## Notes
* I'm not using immutable classes due to some conflicts with Jackson (solvable with `@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)`) and Flux libraries and spend too much time searching for a solution.
* I could have used a better folder structured, dividing components by its functionality, but I didn't for the sake of simplicity. It would be something like this:
```
java/com/ryanair/flights
|
\_ interconnections
  |
  \_ controller
  |
  \_ model
  |
  \_ service
  .......
|
\_ other functionality with similar structure
```

