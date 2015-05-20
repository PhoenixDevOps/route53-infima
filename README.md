# Phoenix DevOps Fork of Amazon Route 53 Infima

This is fork of **Amazon Route53 Infima**, a library for managing service-level fault isolation using [Amazon Route 53][route53]. 

## Requirements

To run this code, you will need to:

1. Install Java 1.8
2. Install Maven

## Running the Code

Once you check out the code from GitHub, you can build it using Maven with the following command:

`mvn clean install exec:java -Dexec.mainClass="Main" -Dgpg.skip=true`

This creates a JAR file of the Route53 Infima Library which can be used anywhere.  It disables signing that JAR, which is not necessary because we don't intend to publicly distribute this code through, for example, Maven Central.  It also runs the code in the `Main` class.

You may receive certain errors, but you can ignore them.