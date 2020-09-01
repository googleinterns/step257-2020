#!/bin/bash

# this script is for local run only
# it sets the necessary environment variable for the local datastore initializing
export RUNMODE="local"
mvn package appengine:run
