var alert2 = function(s) {
  //alert(s);
}

const reader = new FileReader();
var queue = [];
// This fires after the blob has been read/loaded.
reader.addEventListener('loadend', (e) => {
    const text = e.srcElement.result;
    console.log("Server:"+text);
    var v = cjCall("phi.net.CheerpJSocket", "pipeWrite", text).then()
});

open = false;
timeout = 250

var socket;

var clientConnect = function(host, port, timeout) {
  console.log("clientConnect! "+host+"-"+port+"-"+timeout);
  var v = cjCall("phi.net.CheerpJSocket", "cheerpJNotify", "connected").then(console.warn)
  socket = new WebSocket("ws://localhost");
  socket.onmessage = function(event) {
    console.warn(`[message] Data received from server: ${event.data}`);
    //reader.readAsText(event.data);
    queue.push(event.data)
    //const text = await new Response(event.data).text()
  };

  socket.onclose = function(event) {
    if (event.wasClean) {
      alert(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`);
    } else {
      // e.g. server process killed or network down
      // event.code is usually 1006 in this case
      alert('[close] Connection died');
    }
  };

  opened = 0

  socket.onopen = function(error) {
    console.warn(`[message] OPEN`);
    opened = 1
  };

  socket.onerror = function(error) {
    alert(`[error] ${error.message}`);
  };
  open = true;
  console.warn("clientConnected!");
}

var clientClose = function() {
  console.log("clientClose!")
  var v = cjCall("phi.net.CheerpJSocket", "cheerpJNotify", "closed").then(console.warn)
}

var clientReset = function() {
  console.log("RESET ME");
}

var clientNotifyData = function(d) {
  console.log("NEW DATA AVAIL:"+d);
  //a = "c new\n"
  b = new Blob([d+"\n"]);
  socket.send(b);
  console.log("BLOB SEND")
}

var clientSetTimeout = function(timeout) {
  console.log("SetTimeout="+timeout);
}

var clientGetTimeout = function() {
  
}

testWrite = function() {
  if (open) {
    console.log("testWrite");
    var v = cjCall("phi.net.CheerpJSocket", "pipeWrite", "TESTWRITE").then()
  }
}
//setInterval(testWrite, 10000);

manageQueue = function() {
  if (reader.readyState != FileReader.LOADING && queue.length > 0) {
    var i = queue.shift();
    reader.readAsText(i);
  }
}
setInterval(manageQueue, 10);