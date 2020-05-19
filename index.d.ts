import { IonicNativePlugin } from '@ionic-native/core';
export declare class BackgroundGPS extends IonicNativePlugin {
    init(settings: any): Promise<any>;
	startBackground(): Promise<any>;
	stopBackground(): void;
	startTracking(settings: any): Promise<any>;
	stopTracking(): void;
	getLocation(): Promise<any>;
}