#!/bin/bash

zip -d /Users/Mathieu/projects/streaming/target/streaming-1.jar META-INF/LICENSE
hadoop jar /Users/Mathieu/projects/streaming/target/streaming-1.jar CreateGraphNodes
hadoop jar /Users/Mathieu/projects/streaming/target/streaming-1.jar CreateGraphEdges
