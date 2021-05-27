# AdSense Management API Ruby Samples

This is a set of simple samples written in Ruby, which provide a minimal
example of Google AdSense integration within a command line application.

This starter project provides a great place to start your experimentation into
the AdSense Management API.

## Prerequisites

Please make sure that you're running Ruby 2.5+ and you've run
`gem install google-apis-adsense_v2` to install the Ruby client library for the
AdSense Management API v2.

## Setup Authentication

Before getting started, check the README on the Ruby client library
documentation page:
https://github.com/googleapis/google-api-ruby-client

To authenticate your application, you will need to add your Client ID and Client
Secret to the `client_secrets.json` file.

## Running the Samples

1. Run a sample from this directory:

        $ ruby get_all_ad_clients.rb

1. Some examples require an argument to be passed in on the command line. If
   the example shows a message to this effect, you might have to run a different
   example to get the ID required to pass in.

1. If this is the first example you've run, or if there's some other reason
   your OAuth tokens are not stored locally, complete the authorization steps
   in your browser.

1. Examine your shell output and verify the results.
