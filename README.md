#BD2K Crawler
## Description:

Spring MVC Web project that provides crawling services on all BD2K websites as of June 2016 and center publications found on PubMed. There are two separate crawlers for each service and they can be ran in parallel (but multiple instances of the same crawler cannot be running). Utilizes [Crawler4j](https://github.com/yasserg/crawler4j) for crawling web pages.

##Building

This is a standard Maven project, so the easiest way to get a local version of the web service running is through [Maven](https://maven.apache.org/). For this walkthrough, I will assume that [Apache tomcat](http://tomcat.apache.org/) is the Application server/container used, and that one wants to deploy to a local running instance.

###Dependencies

If you have not already, install [Maven](https://maven.apache.org/) and [Apache tomcat](http://tomcat.apache.org/). Also, this project utilizes [MongoDB](https://www.mongodb.com/) as its datastore, so be sure to install and start a Mongo server before testing and running the web service. If you are on OSX, you can use something like [Homebrew](http://brew.sh/) to manage these packages.

To verify that Maven is correctly installed, run:

```bash
mvn -v
```

And you should see the Maven version number as well as other metadata, such as the home directory for Maven and the Java version found.

Now that Maven is installed, head over to the root directory of the project and run

```bash
mvn compile
```

in order to compile the project source code. The default location of the .class files will be in the target/** directory. This step is good for a sanity check that there are no compilation errors, but overall, this is optional.

To compile and package the result into a WAR file for Tomcat deployment, run

```bash
mvn package
```

This should compile, run tests, and package the compiled bytecode into a WAR file located in the target/** directory.

Simply copy and paste (or through an IDE like Eclipse) the WAR file into the webapps directory of your Tomcat installation. If you are not certain, see the official Tomcat deployment documentation.

After (re)starting Tomcat, you should see the login page for BD2KCrawler.

###Application dependencies

Though the build dependencies should be ready to go, there is one more thing to do to get the web service working locally: creating an authorized user to access the dashboard and initiate crawling. In the future, we can add a registration service, but as of now it must be done manually.

We need to create a new database named BD2KCrawlerDB, and a collection named "Users". Add a minimal document 

```javascript
{
	firstName:"",
	lastName:"",
	email:"test@email.com",
	password:"<Some BCRYPT hashed password>",
	role: "ROLE_ADMIN" 
}
```

Note that it is important to use a BCRYPT hashed password, as the authentication service (spring-security) is configured to hash input passwords automatically. Use something like [BCrypt Hash Generator](http://bcrypthashgenerator.apphb.com/) to quickly obtain some hash.

After this, you are set to login and access all services from the site.