# MixPanel to Split Events Integration

![alt text](https://static.matomo.org/wp-content/uploads/2018/10/matomo-logo-winner.jpg)
![alt text](https://www.split.io/wp-content/uploads/2017/11/split-logo-light-background-transparent.png)

The Matomo to Split event integration uses a public Matomo REST API (designed with this purpose in mind) to extract raw event data and tranform it for batch send to Split.

To run, build the executable JAR file and run with a JSON configuration as argument.

Compile with Maven:

mvn clean compile assembly:single

Executable JAR takes name of configuration file as argument.

Sample configuration file.
```
{
  "splitTrafficType" : "user",
  "splitEnvironment" : "Prod-Default",
  "splitServerSideApiKey" : "secret key",
  "matomoAuthKey" : "secret key",
  "siteId" : "1",
  "batchSize" : 1000,
  "retries" : 5,
  "debugDirectory" : "/tmp/split"
}
```
Configuration Fields:

* "splitTrafficType" - are events for user traffic, anonymous traffic, or some other Split traffic type?  "user" is a good default 
* "splitEnvironment" - for which Split environment is the event traffic? 
* "splitServerSideApiKey" - as the name says, get a Split server-side api key
* "matomoAuthKey" - follow Matomo instructions to create an auth key
* "siteId" - what is your Matomo site id? Find in Matomo console.
* "batchSize" - how many Split events to send across in a single API request
* "retries" - how many attempts should be made to resend data before a fail?
* "debugDirectory" - events that could not be written to split are deposited here for debugging
