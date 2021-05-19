# AdSense Management API v2 Java Examples

## Instructions for the AdSense Management API v2 Command-Line Sample

### Browse Online

  - [Browse Source](http://code.google.com/p/google-api-java-client/source/browse?repo=samples#hg/adsense-cmdline-sample)
  - Main file to run all examples:
    [AdSenseSample.java](http://code.google.com/p/google-api-java-client/source/browse/adsense-cmdline-sample/src/main/java/com/google/api/services/samples/adsense/cmdline/AdSenseSample.java?repo=samples)

### Register Your Application

  1. Visit the [Google Cloud console](https://cloud.google.com/console/start/api?id=adsense)
  1. If necessary, sign in to your Google Account, select or create a project,
     and agree to the terms of service.  Click Continue.
  1. From the
     [API Library](https://console.cloud.google.com/start/api?id=adsense.googleapis.com),
     enable the **AdSense Management API**.
  1. Click on **APIs & Services > Credentials** in the left navigation menu.
  1. Click **CREATE CREDENTIALS > OAuth client ID**.
  1. Select **Desktop app** as the application type, give it a name, then click
     **Create**.
  1. From the Credentials page, click **Download JSON** next to the client ID you
     just created and save the file as `client_secrets.json` in the
     *src/main/resources/* directory. If you skip this step, when trying
     to run the sample you will get a `401 INVALID_CLIENT` error in the browser.

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
