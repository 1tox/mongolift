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
        <version>${project.version}</version>
    </dependency>

#### Gradle

through your build.gradle file

    dependencies {
        implementation("tech.ideo:mongolift4spring:${project.version}")
    }

### Migrating referential data

In the src/main/resources/db/migrations/referential folder, place a json file that contains referential data you want to
migrate. It should follow this convention:
`<MY_COLLECTION>`.json

You're already done!

Next time you will restart your spring boot application, the collection `<MY_COLLECTION>` will be updated.
Also, a new collection named `migrations` will be added so that you can audit the applied migrations

### Migrating indexes

In the src/main/resources/db/migration/indexes folder, place a json file following this convention:
`<MY_COLLECTION>`.json

    [
      {
        key: {
          name: 1
        },
        name: "name_1"
      },
      {
        key: {
          continent: 1
        },
        name: "continent_1"
      }
    ]

This will create, if not present, or update, if its definition changed, the above indexes into the collection
`<MY_COLLECTION>`.json

### Migrating any kind of scripts

In the src/main/resources/db/migration/scripts folder, place a json file that contains any Json object that could be
interpreted by mongosh command [runCommand](https://www.mongodb.com/docs/manual/reference/method/db.runCommand/)
