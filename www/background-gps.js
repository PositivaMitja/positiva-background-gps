var exec = cordova.require("cordova/exec");

function BackgroundGPS() 
{
	this.settings = {
		API_URL: "https://",
		USER_ID: 0,
		VEHICLE_ID: 0,
		INTERVAL: 10,
		FILE_PATH: "file:///"
	};
}

BackgroundGPS.prototype.init = function (settings, successCallback) 
{
	exec(successCallback, null, 'BackgroundGPS', 'init', [settings]);
};

BackgroundGPS.prototype.startBackground = function (successCallback) 
{
	exec(successCallback, null, 'BackgroundGPS', 'startBackground', []);
};

BackgroundGPS.prototype.stopBackground = function () 
{
	exec(null, null, 'BackgroundGPS', 'stopBackground', []);
};

BackgroundGPS.prototype.startTracking = function (settings, successCallback) 
{
	exec(successCallback, null, 'BackgroundGPS', 'startTracking', [settings]);
};

BackgroundGPS.prototype.stopTracking = function () 
{
	exec(null, null, 'BackgroundGPS', 'stopTracking', []);
};

BackgroundGPS.prototype.getLocation = function (successCallback) 
{
	exec(successCallback, null, 'BackgroundGPS', 'getLocation', []);
};
		
var backgroundGPS = new BackgroundGPS();
module.exports = backgroundGPS;