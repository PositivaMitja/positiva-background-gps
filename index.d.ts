import { IonicNativePlugin } from '@ionic-native/core';
export declare class BackgroundGPS extends IonicNativePlugin {
    init(settings: any): void;
	startBackground(): void;
	stopBackground(): void;
	getLocation(): Promise<any>;
}