# AdSense Management API v2 Java Examples

## Instructions for the AdSense Management API v2 Command-Line Sample

### Browse Online

  - [Browse Source](http://code.google.com/p/google-api-java-client/source/browse?repo=samples#hg/adsense-cmdline-sample)
  - Main file to run all examples: [AdSenseSample.java](http://code.google.com/p/google-api-java-client/source/browse/adsense-cmdline-sample/src/main/java/com/google/api/services/samples/adsense/cmdline/AdSenseSample.java?repo=samples)

### Register Your Application

  - Visit the [Google Cloud console](https://cloud.google.com/console/start/api?id=adsense)
  - If necessary, sign in to your Google Account, select or create a project,
  and agree to the terms of service.  Click Continue.
  - Select "Installed application" and choose type "Other" under the Installed
  Application type.
  - Within "OAuth 2.0 Client ID", click on "Download JSON". Later on, after you
  check out the sample project, you will copy this downloaded file (e.g.
  `~/Downloads/client_secrets.json`) to
  `src/main/resources/client_secrets.json`. If you skip this step, when trying
  to run the sample you will get a `400 INVALID_CLIENT` error in the browser.

### Checkout Instructions

**Prerequisites:** install [Java](http://java.com) (version 7 or later), and
[Maven](http://maven.apache.org/download.html). You may need to set your
`JAVA_HOME`.

```
cd *[someDirectory]*
git clone https://github.com/googleads/googleads-adsense-examples
cd googleads-adsense-examples/v2/java/
cp ~/Downloads/client_secrets.json src/main/resources/client_secrets.json
mvn compile
mvn -q exec:java
```

To enable logging of HTTP requests and responses (highly recommended when
developing), please take a look at the `logging.properties` file.
