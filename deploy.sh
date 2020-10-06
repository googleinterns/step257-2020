# Copyright 2020 Google LLC
#!/bin/bash

# build angular
cd sticknotes-frontend
ng build --prod

# copy generated output to the "webapp" directory of the maven GAE project
cp -r dist/sticknotes-frontend/* ../sticknotesbackend/src/main/webapp/
cd ../sticknotesbackend

# run deployment
mvn package -DskipTests appengine:deploy 
