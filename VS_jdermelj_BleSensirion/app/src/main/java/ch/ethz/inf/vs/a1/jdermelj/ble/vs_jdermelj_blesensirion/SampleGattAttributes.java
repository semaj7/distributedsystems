package ch.ethz.inf.vs.a1.jdermelj.ble.vs_jdermelj_blesensirion;

/**
 * Created by Andres on 24.09.15.
 */
/*
 * Copyright (C) 2013 The Android Open Source Project
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

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String UUID_RHT_TEMPERATUREHUMIDITY = "0000AA20-0000-1000-8000-00805f9b34fb";
    public static String RHT_CHARACTERISTIC = "0000AA21-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.

        // Sample Characteristics.
        attributes.put(UUID_RHT_TEMPERATUREHUMIDITY, "RHT Sensor Messung");
        attributes.put(RHT_CHARACTERISTIC, "RHT Feuchtigkeit & Temperatur Charakteristik");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}