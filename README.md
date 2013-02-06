# Less Hip Chat

There is a commercial chat service that gives you historical search for all
channels. IRC does not offer this by default. This is a little tool to help
support that kind of functionality, using Solr.

## Configuration

Check out conf/application.conf. This should be fairly self documenting.

Each channel specified in the configuration can also get a specified purge time,
where messages older than a particular time can be purged.

Additionally, some values can be specified via a system property:

  * `-Dsolr.home=/tmp/solr` - A directory for solr to write data to, if using the embedded server
  * `-Dsolr.url=http://host:port` - A URL to use for a remote solr instance

## Building

Just use the pre-built jar. Or, feel free to built with sbt.

## Running

run `java -Dconfig.file=config.properties -jar lesshipchat.jar`. Or run it via
daemon so it goes into the background and leaves you alone.

## Search API

There is a pretty basic search API that provides JSON and JSONP output.

### URL

    GET /search

### Parameters

  * `callback` - string 
  * `query` - string
  * `size` - int
  * `page` - int
  * `sort` - string - asc or desc - defaults to desc

## Disclaimer

This is not fancy, just a quick and dirty hack.
