# SSH PoC

## Requirements
- Java 8+

## Usage
- Run `java -jar ssh-poc-standalone-0.1.jar -h`  in order to visualize POC instructions

### Example
`java -jar ssh-poc-0.1-standalone.jar -u user -p password -a localhost:22`
1.  Where _localhost_ is the domain and _22_ is the port

> After the execution, a file called _capture.log_  should be created in the same directory where the jar file is placed
> This file contains information regarding the SSH handshake and hopefully the application welcome screen indicating the POC was a success.

## Build

### Requirements 
- Maven 3.8+

### How to build
- Run `mvn clean package` on the repository root directory
- Jar file will be located in target/
