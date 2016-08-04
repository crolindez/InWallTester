/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.carlosrolindez.inwall_tester;

/**
 * Defines several constants used between A2dpService and the UI.
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
/*    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;*/

    // Key names 
	public static final String a2dpFilter = "es.carlosrolindez.InWallTester.A2dpService.FILTER";
	public static final String NameFilter = "es.carlosrolindez.InWallTester.A2dpService.NAME";
	public static final String LAUNCH_MAC = "es.carlosrolindez.InWallTester.A2dpService.MAC";

	public static final String DEVICE_NAME = "es.carlosrolindez.InWallTester.A2dpService.DEVICE_NAME";
	public static final String DEVICE_MAC = "es.carlosrolindez.InWallTester.A2dpService.DEVICE_MAC";
	public static final String FTP_MODE = "es.carlosrolindez.InWallTester.A2dpService.FTP_MODE";
	
    // Intent request codes
	public static final int REQUEST_ENABLE_BT = 1;
	

}
