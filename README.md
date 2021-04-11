# ktor-chat
Small chat application with Ktor and WebSockets

###Run locally

```shell
./gradlew run
```

You need a WebSocket client to use the app.
We recommend using wscat from the command line with this command:

```shell
wscat -c ws://localhost:8080/chat
```