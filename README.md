STEP Capstone project "Notesboard" by apodob@ and paulkovalov@

To deploy the app, run `./deploy.sh`

To run frontend locally, execute
```
cd sticknotes-frontend
ng serve
```

To run backend locally, run datastore emulator firstly using 
`gcloud beta emulators datastore start --host-port=localhost:8484`.
Next execute
```
cd sticknotesbackend
./run.sh
```

To run tests, launch datastore emulator with `gcloud beta emulators datastore start --host-port=localhost:8484`.
Next execute 
```
cd sticknotesbackend
mvn test
```
