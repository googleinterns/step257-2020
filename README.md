STEP Capstone project "Notesboard" by apodob@ and paulkovalov@

Our project is an attempt to improve WFH experience for googlers who got used to notesboards they have in offices. Our app is a virtual notesboard where you can leave notes and invite other people to collaborate.

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
