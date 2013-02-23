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

## Group API

Pretty basic API for providing JSON and JSONP output describing channels and
message counts.

## URL

    GET /api/groups

### Parameters

  * `callback` - string, optional. for use with jsonp

## Search API

There is a pretty basic search API that provides JSON and JSONP output.

### URL

    GET /api/search

### Parameters

  * `callback` - string, optional. for use with jsonp
  * `query` - string, optional. defaults to `*:*`.
  * `size` - int, optional. defaults to 10, max 500.
  * `page` - int, optional. defaults to 0.
  * `sort` - string, optional. defaults to desc. asc is also valid.

### Query Details

Queries are basically naked solr queries. The fields that can be searched on
are:

  * `timestamp` - time of message
  * `group` - the irc channel the message was in
  * `message` - the body of the message
  * `user_nick_meta_s` - nickname of user

Valid queries include:

    query=fizz # Searches all fields for fizz
    query=message:fizz+AND+group:Channel # Returns messages from Channel matching fizz
    query=user_nick_meta_s:bmatheny+AND+message:icinga # Matches nick and message

## Disclaimer

This is not fancy, just a quick and dirty hack.
