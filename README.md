![MongoLift logo](https://github.com/1tox/mongolift/blob/main/doc/logo.png)

Lightweight and production-ready **Java tool for synchronizing Mongo data and indexes between dev and prod
environments**

## Key features

* Helps maintaining similar referential data between various environments
* Keeps audit trace of migrations applied, successful or not
* Ensures migrations are successfully applied once and for all avoiding already applied migrations
* Spring Boot integration with auto configuration mechanism
* No Mongo shell knowledge needed to apply referential data migration
* Allows migration of home made Mongo scripts

## Operating principles

![mongolift operating principles](https://github.com/1tox/mongolift/blob/main/doc/operating_principles.png)

## Usage

### Installation

In your spring boot application, download latest version:

#### Maven

through your pom.xml file

    <dependency>
        <groupId>tech.ideo</groupId>
        <artifactId>mongolift4spring</artifactId>
        <version>1.0</version>
    </dependency>

#### Gradle

through your build.gradle file

    dependencies {
        implementation("tech.ideo:mongolift4spring:1.0")
    }

### Applying one migration

In the src/main/resources/db/migration/referential folder, place a json file following this convention:
`<MY_COLLECTION>`.json

You're already done!

Next time you will restart your spring boot application, the collection `<MY_COLLECTION>` will be updated.
A new collection named `migrations` will be added so that you can audit the applied migrations