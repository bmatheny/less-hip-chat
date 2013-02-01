# Less Hip Chat

There is a commercial chat service that gives you historical search for all
channels. IRC does not offer this by default. This is a little tool to help
support that kind of functionality, using Solr.

## Configuration

I'm not a huge XML fan, I just use a little properties file. This should be
fairly self documenting.

Each channel specified in the configuration can also get a specified purge time,
where messages older than a particular time can be purged.

## Building

Just use the pre-built jar. Or, feel free to built with sbt.

## Running

run `java -Dconfig.file=config.properties -jar lesshipchat.jar`. Or run it via
daemon so it goes into the background and leaves you alone.

## Disclaimer

This is not fancy, just a quick and dirty hack.
