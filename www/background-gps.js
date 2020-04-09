var exec = cordova.require("cordova/exec");

function BackgroundGPS() 
{
	this.settings = {
		OPTION1: "mitja",
		OPTION2: "test"
	};
}

BackgroundGPS.prototype.test = function (settings, successCallback, errorCallback) 
{
	if (errorCallback == null) {
		errorCallback = function () {
		};
	}

	if (typeof errorCallback != "function") {
		console.log("failure parameter not a function");
		return;
	}

	if (typeof successCallback != "function") {
		console.log("success callback parameter must be a function");
		return;
	}

	exec(successCallback, errorCallback, 'BackgroundGPS', 'test', [
		{"settings": settings}
	]);
};
		
var backgroundGPS = new BackgroundGPS();
module.exports = backgroundGPS;