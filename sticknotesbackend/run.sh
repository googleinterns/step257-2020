# Copyright 2020 Google LLC

#!/bin/bash

# this script is for local run only
# it sets the necessary environment variable for the local datastore initializing and app configuration variables
export RUNMODE="local"
export PROJECT_ID="notesboard"
export GCS_BUCKET_NAME="notesboard-file-uploads"
mvn package appengine:run
