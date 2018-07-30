# Overview

This software was running the DataCite Metadata Store (MDS) from 2011 until July 2018. Please go to [https://github.com/datacite/poodle](https://github.com/datacite/poodle) for the current software running the MDS API.

To learn more about DataCite please visit [our website](http://www.datacite.org)

# Installation (for development only)

## Tools

It's assumed you have mysql 5.5 (or mariadb) and Roo 1.1.5 installed. Roo is not needed to
run the application, only if you want to re-generate code from database. You also
will need Maven 2.2.1 or newer. Minimum version of JDK is 6 (OpenJDK from Ubuntu
works fine).

### mysql setup

Create database with UTF-8 support

    create database datacite character set utf8;
    create database datacite_test character set utf8;

make sure transactions are supported - add in my.cnf:

    default-storage-engine=innodb

## Java dependencies

Most dependencies are managed by Maven public repositories. There is
one jar you need to download and add to your local maven repo
manually.

#### Handle API client

MDS uses Handle System Java API to make calls to the Handle Service. You
need the Handle API client jar.

Download the Java package from [Handle.net](http://handle.net/client_download.html)

Extract files and add handle-client.jar to your local maven repo:

    mvn install:install-file -Dfile=handle-client.jar -DgroupId=handle.net \
     -Dversion=7 -Dpackaging=jar -DgeneratePom=true -DartifactId=hcj

### Local SSL cert

By default MDS uses https for all URLs. In order to use SSL locally
you need a SSL certificate. This certificate can be self generated -
the browser will complain but all will work OK.

    keytool -genkey -alias tomcat -keyalg RSA -dname "cn=localhost"

The default password is 'changeit'

This self-generated certificate is required only for local development. If keystore password is changed
the procedure to run the app locally will not work as pom.xml (responsible for configuration of maven's
 build-in tomcat) doesn't contain the new password.

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

Tip: to quickly generate random sequence:

    openssl rand -base64 32

### src/main/resources/META-INF/spring/database.properties

your database configuration, password etc as you typed then creating
the users.

### src/test/resources/META-INF/spring/database.properties

your test database configuration. This database is recreated from
scratch every time test run via "mvn clean test"

### src/main/resources/META-INF/spring/handle.properties

Handle service authentication info. If you don't know what to put here
check DataCite wiki (only for members) or setup your own Handle
Service.

### src/main/resources/META-INF/spring/email.properties

your SMTP settings. Use your own SMPT or check DataCite wiki.

### src/main/resources/META-INF/spring/xml-validator.properties

location of XSD and flag if XML should be validated.

### src/main/resources/log4j.properties

your usual log4j stuff.

## Running locally

### First run

At this stage you should be able to run application.

    mvn compile tomcat:run

point your browser at:

> https://localhost:8443/mds/

it will complain about untrusted SSL certificate but you say it's OK
and the main page of MDS should be presented.

Please note that although you access on port 8443, port 8080 must be unbound (the app does redirect from http
8080 to https 8443). If
you need to change this add specific port when running the above command e.g.

    mvn -Dmaven.tomcat.port=8181 compile tomcat:run

If you set value 'create' in src/main/resources/META-INF/persistence.xml (see above),
now you can kill tomcat and change to 'validate'.

### Creating user accounts

To login and create accounts for the users you need to insert admin
account. Therefore run

    mvn exec:java -Dexec.mainClass=org.datacite.mds.tools.AdminAccountCreator

You will be asked to specify symbol, password and e-mail for the admin account.

This command works only if database doesn't have Admin account already. If this is not the case, please
remove Admin from the database manually.

### That's all!

You can run:

    mvn compile tomcat:run

again and create appropriate accounts and prefixes.


