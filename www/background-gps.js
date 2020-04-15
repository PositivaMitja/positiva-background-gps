var exec = cordova.require("cordova/exec");

function BackgroundGPS() 
{
	this.settings = {
		API_URL: "https://",
		API_TRACKING: "/api/vehicle-trackings/create",
		API_TOKEN_REFRESH: "/api/user/token/refresh",
		TOKEN: "",
		USER_ID: 0,
		VEHICLE_ID: 0,
		INTERVAL: 10,
		FILE_PATH: "file:///"
	};
}

BackgroundGPS.prototype.init = function (settings) 
{
	exec(null, null, 'BackgroundGPS', 'init', [settings]);
};

BackgroundGPS.prototype.startBackground = function () 
{
	exec(null, null, 'BackgroundGPS', 'startBackground', []);
};

BackgroundGPS.prototype.stopBackground = function () 
{
	exec(null, null, 'BackgroundGPS', 'stopBackground', []);
};

BackgroundGPS.prototype.getLocation = function (successCallback) 
{
	exec(successCallback, null, 'BackgroundGPS', 'getLocation', []);
};
		
var backgroundGPS = new BackgroundGPS();
module.exports = backgroundGPS;