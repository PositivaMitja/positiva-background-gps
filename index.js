var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Injectable } from '@angular/core';
import { Cordova, IonicNativePlugin, Plugin } from '@ionic-native/core';

var BackgroundGPS = (function (_super) {
    __extends(BackgroundGPS, _super);
    function BackgroundGPS() {
        return _super !== null && _super.apply(this, arguments) || this;
    }

    BackgroundGPS.prototype.init = function (settings) { return; };
	BackgroundGPS.prototype.startBackground = function () { return; };
	BackgroundGPS.prototype.stopBackground = function () {};
	BackgroundGPS.prototype.startTracking = function (settings) { return; };
	BackgroundGPS.prototype.stopTracking = function () {};
	BackgroundGPS.prototype.getLocation = function () { return; };
    BackgroundGPS.decorators = [
        { type: Injectable },
    ];
    __decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", [Object]),
        __metadata("design:returntype", Promise)
    ], BackgroundGPS.prototype, "init", null);
	
	__decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", Promise)
    ], BackgroundGPS.prototype, "startBackground", null);
	
	__decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", void 0)
    ], BackgroundGPS.prototype, "stopBackground", null);
	
	__decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", [Object]),
        __metadata("design:returntype", Promise)
    ], BackgroundGPS.prototype, "startTracking", null);
	
	__decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", void 0)
    ], BackgroundGPS.prototype, "stopTracking", null);
	
	__decorate([
        Cordova(),
        __metadata("design:type", Function),
        __metadata("design:paramtypes", []),
        __metadata("design:returntype", Promise)
    ], BackgroundGPS.prototype, "getLocation", null);

    BackgroundGPS = __decorate([
        Plugin({
            pluginName: 'BackgroundGPS',
            plugin: 'positiva-background-gps',
            pluginRef: 'cordova.plugins.backgroundGPS',
            repo: 'https://github.com/PositivaMitja/positiva-background-gps',
            platforms: ['Android']
        })
    ], BackgroundGPS);
    return BackgroundGPS;
}(IonicNativePlugin));
export { BackgroundGPS };