
var port       = 4274;
var express    = require("express");
var http       = require('http');
var sys        = require('sys')
var app        = express();
//var httpServer = http.createServer(app);
//httpServer.listen(port, "0.0.0.0");
var routers={};
//routers[0] = {"name":"CS4274_01", "slientTime": [{"start":1300, "end":1500}]};
//routers[1] = {"name":"CS4274_02", "slientTime": [{"start":1300, "end":1500},{"start":1600, "end":1630}]};
routers["CS4274_01"] = [{"title":"CS4274_01_Event_One","start":1300, "end":1500}];
routers["CS4274_02"] = [{"title":"CS4274_02_Event_One","start":1300, "end":1500},{"title":"CS4274_02_Event_Two","start":1600, "end":1630}];
var response;

app.use(express.static(__dirname));

    
app.get("/", function(req, res) {
  res.send(JSON.stringify(routers));
});

app.get("/:rID", function(req, res) {
	var id = req.params.rID;
  res.send(JSON.stringify(routers[id]));
});

 /* serves all the static files */
 app.get(/^(.+)$/, function(req, res){ 
     console.log('static file request : ' + req.params);
     res.sendfile( __dirname + req.params[0]); 
 });

 app.listen(port, function() {
    console.log("Listening on " + port);
  });



