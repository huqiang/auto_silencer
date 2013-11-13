
var port       = 4274;
var express    = require("express");
var http       = require('http');
var sys        = require('sys')
var app        = express();
//var httpServer = http.createServer(app);
//httpServer.listen(port, "0.0.0.0");
var routers=[];
var routers_index={index:3};
routers_index["CS4274_01"] = 0;
routers_index["CS4274_02"] = 1;
routers_index["NUS"] = 2;
//routers[0] = {"name":"CS4274_01", "slientTime": [{"start":1300, "end":1500}]};
//routers[1] = {"name":"CS4274_02", "slientTime": [{"start":1300, "end":1500},{"start":1600, "end":1630}]};
routers[routers_index["CS4274_01"]] = [{"ssid":"NUS","title":"NUS_TEST","position_name":"SR1", "region":[380,120,2,470,230,2], "startHour":13, "startMinute":45, "endHour":13, "endMinute":50}, 
								{"ssid":"NUS","title":"NUS_TEST2","position_name":"SR1", "region":[380,120,2,470,230,2],"startHour":16, "startMinute":30, "endHour":16, "endMinute":50}];
routers[routers_index["CS4274_02"]] = [{"ssid":"CS4274_02","title":"CS4274_02_Event_One","startHour":13, "startMinute":0, "endHour":15, "endMinute":0},{"ssid":"CS4274_02","title":"CS4274_02_Event_Two","startHour":16, "startMinute":0, "endHour":16, "endMinute":30}];
//routers[routers_index["NUS"]] = [{"ssid":"NUS","title":"NUS_TEST","position_name":"SR1", "region":[380,120,2,470,230,2], "startHour":13, "startMinute":45, "endHour":13, "endMinute":50}, 
								//{"ssid":"NUS","title":"NUS_TEST2","position_name":"SR1", "region":[380,120,2,470,230,2],"startHour":16, "startMinute":30, "endHour":16, "endMinute":50}];
//routers["NUS"] = [{"title":"NUS_TEST","startHour":16, "startMinute":10, "endHour":17, "endMinute":11}];
//,{"title":"CS4274_02_Event_Two","startHour":16, "startMinute":0, "endHour":16, "endMinute":30}];
var response;

app.use(express.static(__dirname));
app.configure(function() {
  app.use(express.bodyParser());
});
    
app.get("/all", function(req, res) {
  res.send(JSON.stringify(routers));
});

app.get("/:rID", function(req, res) {

	var id = req.params.rID;
	console.log(id);
  res.send(JSON.stringify(routers[routers_index[id]]));
});

 /* serves all the static files */
 app.get(/^(.+)$/, function(req, res){ 
     console.log('static file request : ' + req.params);
     res.sendfile( __dirname + req.params[0]); 
 });

 app.listen(port, function() {
    console.log("Listening on " + port);
  });

app.post("/new_event", function(req,res){
	var params = req.body;
	console.log(params);
	var index = routers_index[params.ssid];
		console.log("index is "+ index+  "  "+routers_index.index);
	if (index == null){
		index = routers_index.index;

		console.log("in if index is "+ index);
		routers_index.index++;
		routers_index[params.ssid] = index;
		console.log(routers_index);
		routers[index] = [];
	}
	else{
	}
	routers[index].push(params);
	res.send(JSON.stringify(routers))
});


