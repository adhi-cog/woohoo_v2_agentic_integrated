package com.sb.woohoo.ui.featuresettings

import com.sb.woohoo.utils.Constants

fun getTestDevices(): ArrayList<DeviceModel> {
    val deviceList = ArrayList<DeviceModel>()
    deviceList.add(DeviceModel(Constants.USE_SYSTEM_ID, "--"))
    deviceList.add(DeviceModel("33cda491362hut90", "test@admin.com"))
    deviceList.add(DeviceModel("bb317f182ba7aca8", "testing@test.com"))
    deviceList.add(DeviceModel("h9s3a8d6c2b1f5e", "testrc@woohoo.com"))
    deviceList.add(DeviceModel("x4t9m2j8h7l6k1n", "testcks@woohoo.com"))
    deviceList.add(DeviceModel("m6t9j8n2s1d4k7f", "testad@woohoo.com"))
    deviceList.add(DeviceModel("t4m6f9s7c8b2p1", "testpr@woohoo.com"))
    deviceList.add(DeviceModel("n2m6j8l3k9h1f7d", "teststa@woohoo.com"))
    deviceList.add(DeviceModel("m7k8n3f2j1d4a6b", "testnh@woohoo.com"))
    deviceList.add(DeviceModel("112adc6347fgoht3", "test@khalidiya.com"))
    deviceList.add(DeviceModel("44ghafraa7fg6745", "test@prp.com"))
    deviceList.add(DeviceModel("b7aa96af05512daa", "0713@prp.com"))
    deviceList.add(DeviceModel("1bbf0021649eabba", "android5@woohoo.com"))
    /*Add more devices here*/
    return deviceList
}

fun getTestEmailId(testDeviceId: String): String {
    var testEmailId = ""
    getTestDevices().forEach { deviceModel ->
        if(deviceModel.testDeviceId.contentEquals(testDeviceId))
            testEmailId = deviceModel.emailId
    }
    return testEmailId
}