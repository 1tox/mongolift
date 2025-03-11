<body>
  <hr style="border: 1px solid #4FB4BF; margin-top: 20px; margin-bottom: 20px;">

  <div style="display: flex; align-items: center;">
    <img src="assets/logo.png" align="left" width="300px" height="300px"/>
    <div style="margin-left: 20px;">
      <h2 style="font-family: 'Arial', sans-serif;">MongoLift</h2>
        <div class="centered-icons">
            <img src="https://shields.io/github/actions/workflow/status/1tox/mongolift/maven.yml?style=plastic" alt="CI">
            <img src="https://shields.io/maven-central/v/tech.ideo/mongolift?style=plastic" alt="Latest version">
            <img src="https://img.shields.io/badge/java-21+-yellow?style=plastic" alt="Java versions">
            <img src="https://img.shields.io/badge/springboot-3+-lightgrey?style=plastic" alt="Spring boot versions">
            <img src="https://img.shields.io/badge/mongodb-4+-brightgreen?style=plastic" alt="Mongo versions">
        </div>

  <p style="font-family: 'Arial', sans-serif; font-size: 14px;">
    Lightweight and production-ready <strong>Java tool for synchronizing MongoDB data from local to prod environments</strong>
  </p>

<h3 style="font-family: 'Arial', sans-serif;">General purpose</h3>

  <p style="font-family: 'Arial', sans-serif; font-size: 14px;">
    MongoLift helps to maintain cohesion between MongoDB data that is not manually handled by the user and your codebase.
    It means that each time you deploy a new version of your application, MongoLift can perform updates of referential data, indexes, or any kind of custom scripts.
  </p>



<h3 style="font-family: 'Arial', sans-serif;">Features</h3>

  <ul style="font-family: 'Arial', sans-serif; font-size: 14px;">
    <li>
        <strong>Cohesion: </strong>Helps maintaining similar referential data between various environments 
    </li>
    <li>
        <strong>Traceability: </strong>Keeps audit trace of migrations applied, successful or not 
    </li>
    <li>
        <strong>Efficiency: </strong>Ensures migrations are successfully applied once and for all avoiding already applied migrations 
    </li>
    <li>
        <strong>Spring boot integration: </strong>Takes advantage of Spring Boot auto configuration mechanism. Support for Spring profiles. 
    </li>
    <li>
        <strong>Java client: </strong>MongoLift core API allows to programmatically performs migrations with few lines of code 
    </li>
    <li>
        <strong>Easy to use: </strong>No Mongo shell knowledge needed to apply referential data migration 
    </li>
    <li>
        <strong>Customization: </strong>Allows migration of homemade Mongo scripts 
    </li>
  </ul>

<h3 style="font-family: 'Arial', sans-serif;">Installation</h3>
  <p style="font-family: 'Arial', sans-serif; font-size: 14px;">
    In your spring boot application, download latest version:
    <h4>Maven</h4>
    through your pom.xml file

```
<dependency>
    <groupId>tech.ideo</groupId>
    <artifactId>mongolift4spring</artifactId>
    <version>${project.version}</version>
</dependency>
``` 

<h4>Gradle</h4>
through your build.gradle file

```
dependencies {
    implementation("tech.ideo:mongolift4spring:${project.version}")
}
```

<h3 style="font-family: 'Arial', sans-serif;">Usage</h3>

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

<h3 style="font-family: 'Arial', sans-serif;">Next to come</h3>

  <p style="font-family: 'Arial', sans-serif; font-size: 14px;">
    <ul>
        <li>Allow to order homemade scripts</li>
        <li>Support for Spring boot tests</li>
    </ul>
  </p>
  </div>
</div>
  <hr style="border: 1px solid #4FB4BF; margin-top: 20px; margin-bottom: 20px;">
</body>