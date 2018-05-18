#!/bin/bash
zip -d /Users/Mathieu/projects/streaming/target/streaming-1.jar META-INF/LICENSE
hadoop jar /Users/Mathieu/projects/streaming/target/streaming-1.jar TwitterStreaming --keywords /Users/Mathieu/projects/RCastex/data/keys.csv --twitter-source.consumerKey jDdY3IbRhZuMZvPq3wWf3rVaF --twitter-source.consumerSecret CfrS5SFMOoip28wVkHHaqNMdD5oPDAkVcvi0CAyaHk7v9xFkd2 --twitter-source.token 19156968-8Paw6UHvpjCnWqUc7J7q8ubhY71Y455L9Q0s8tXnw --twitter-source.tokenSecret QqseTEc3X8pMh2fDf2CWyEdz57zSL81LNfDzLUHaryxxK 2> logs_twitter.log
