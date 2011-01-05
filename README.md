# Overview

This is Metadata Store (MDS) for DataCite central infrastructure. This
app has UI and API for DataCite members and their datacentres. The
main functionality is minting DOIs and storing metadata for the
datasets.

To learn more about DataCite please visit [our website](http://www.datacite.org)

To use this software please go to [https://api.datacite.org](https://api.datacite.org)

# Installation (for development only)

## Tools

The developers of this package use Oracle XE but the project is based
on Hibernate so it should be possible to use any SQL database.

This project is uses Spring Roo for code generation. You don't need
Roo to run the code. Although you will most likely need it for
development.

It's assumed you have Oracle XE Universal and Roo 1.1 installed. You
also will need Maven 2 and JDK 6 in your system.

## Java dependencies

Most dependencies are managed by Maven public repositories. There are
two jars you need to download and add to your local maven repo
manually.

### Oracle JDBC driver

get ojdbc14.jar from [Oracle](http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-10201-088211.html)

and add it to maven local repo:

    mvn install:install-file -Dfile=ojdbc14.jar -DgroupId=com.oracle \
     -Dversion=10.2.0.4 -Dpackaging=jar -DgeneratePom=true -DartifactId=ojdbc14

#### Handle API client

MDS uses Handle System Java API to make calls to the Handle Service. You
need the Handle API client jar.

Download the Java package from [Handle.net](http://handle.net/client_download.html)

Extract files and add handle-client.jar to your local maven repo:

    mvn install:install-file -Dfile=handle.jar -DgroupId=handle.net \
     -Dversion=6 -Dpackaging=jar -DgeneratePom=true -DartifactId=hcj

### Local SSL cert

By default MDS uses https for all URLs. In order to use SSL locally
you need a SSL certificate. This certificate can be self generated -
the browser will complain but all will work OK.

    keytool -genkey -alias tomcat -keyalg RSA -dname "cn=localhost"

The default password is 'changeit'

## Configure the source code 

I assume you had created a fork from the master DataCite
repository. Now you need to configure the code before compiling. 

The git repository has a bunch of *.template files. You can find them
with:

find . -name *.template

Those files are templates for the various configuration files which
are machine specific i.e. passwords, IP addresses etc.

To customise them you need to make a copy omitting (.template from
file name) e.g.:

    cp src/main/resources/META-INF/persistence.xml.template \
     src/main/resources/META-INF/persistence.xml

Now in such created file you need to adjust values according to your
local environment.

### src/main/resources/META-INF/persistence.xml

    <property name="hibernate.hbm2ddl.auto" value="create"/>

set the value to 'create' for the first run (DDL script will be run),
for consecutive runs use 'validate'.

### src/main/resources/META-INF/spring/salt.properties

values put there will be used for password hashing.

### src/main/resources/META-INF/spring/database.properties

your database configuration. 

SQL for creating user in Oracle:

    create user datacite identified by <<HERE YOUR PASS>>;
    grant connect, resource to datacite;

### src/main/resources/META-INF/spring/handle.properties

Handle service authentication info. If you don't know what to put here
check DataCite wiki.

### src/main/resources/META-INF/spring/email.properties

your SMTP settings.

### src/main/resources/META-INF/spring/xml-validator.properties

location of XSD and flag if XML should be validated.

### src/main/resources/log4j.properties

your usual log4j stuff.

## Running locally 

### First run

At this stage you should be able to run application.

    mvn compile tomcat:run

point your browser at https://localhost:8443/mds/

it will complain about untrusted SSL certificate but you say it's OK
and the main page of MDS should be presented.

Now kill tomcat and change src/main/resources/META-INF/persistence.xml

### Creating user accounts

To login and create accounts for the users you need to insert admin
account directly to the database.

First hash your password:

    echo -n password{salt}| sha256sum 

Then use this SQL:

    INSERT INTO ALLOCATOR 
    (ID, CONTACT_EMAIL, CONTACT_NAME, DOI_QUOTA_ALLOWED, 
    DOI_QUOTA_USED, IS_ACTIVE, NAME, PASSWORD, ROLE_NAME, SYMBOL, VERSION) 
    VALUES 
    (0, '<<YOUR EMAIL HERE>>', 'Admin', '0', '0', '1', 
    'Admin', '<<YOUR HASHED PASSWORD HERE>>', 'ROLE_ADMIN', 'ADMIN', '1');

### That's all!

You can run: 

    mvn compile tomcat:run 

again and create appropriate accounts and prefixes.


