STEP Capstone project "Notesboard" by apodob@ and paulkovalov@

To deploy the app, run `./deploy.sh`

To run frontend locally, execute
```
cd sticknotes-frontend
ng serve
```

To run backend locally, execute
```
cd sticknotesbackend
mvn package appengine:run
```

To run tests, you need to setup `gcloud` project:

* 1. Set gcloud project id `gcloud config set project notesboard`
* 2. Authenticate in `gcloud` command line tool `gcloud beta auth application-default login`
* 3. Run datastore emulator `gcloud beta emulators datastore start`

Steps 1-2 need to be completed only once, step 3 must be executed each time before running tests
