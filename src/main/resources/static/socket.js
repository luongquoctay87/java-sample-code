// Create WebSocket connection.
const socket = new WebSocket("ws://localhost:7749/test-websocket");

// Connection opened
socket.addEventListener("open", (event) => {
  socket.send("Hello Server!");
  console.log("Connected !");
});

socket.addEventListener("/topic/greetings", (event) => {
  console.log("Message from server ", event.data);
});
