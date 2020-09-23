## STEP Capstone project "Notesboard" by apodob@ and paulkovalov@

To deploy the app, run `./deploy.sh`

To run frontend locally, execute
```
cd sticknotes-frontend
ng serve
```

To run backend locally, run datastore emulator firstly using 
`gcloud beta emulators datastore start --host-port=localhost:8484  --no-store-on-disk`.
Next execute
```
cd sticknotesbackend
./run.sh
```

To run tests, launch datastore emulator with `gcloud beta emulators datastore start --host-port=localhost:8484 --no-store-on-disk`.
Next execute 
```
cd sticknotesbackend
mvn test
```

### Running locally caveats 
Backend part is written in Java 8, and needs Java 8 JDK for running. Make sure that env variable's JAVA_HOME value is a path to the jdk,
and `java --version` outputs something like `1.8.___`

Frontend requires angular cli to be installed. To install it, you need to install `npm` package manager. It comes with Node.js, so you can install Node.js, and `npm` must be installed automatically [link](https://www.npmjs.com/get-npm). To check the installation, run `npm -v`.
Next, install angular using this command `npm install -g @angular/cli`, the installation command is taken from [here](https://cli.angular.io/).

### Deployment caveats
App was designed for Google App Engine **Standard Environment**. This is very important detail, app will **not** work in any other GAE environment or anywhere else, because it uses Google API SDKs provided by App Engine. When deploying app, make sure gc project has enabled:
1. Translation API
2. Memcache (not Memcache**d**), though it was enabled by default, it may change over time, so it is a good idea to double check that it is active.
3. Cloud Storage API. In Cloud Storage, create a bucket, choose whatever location type you wish (we chose same region as where app was deployed), select "standard" for storage class, select "uniform" for access-control. When bucket is created, make sure read access is granted to everyone who needs access to app (we used @google.com). Next, go to appengine-web.xml and update environment variables there. Put GC project id to `PROJECT_ID` and name of the new bucket to the `GCS_BUCKET_NAME`.

