Light_BLE
=========

An enhanced android app to communicate with Bluetooth LE peripheral.

This app maybe useful in Bluetooth Low Energy peripheral equipment 
development. It is based on an example in Android's SDK source code 
smaple which located in sdk/samples/android-19/connectivity/BluetoothLeGatt/
, but that exmaple is so sucks(yeah, Light BLE is still sucks, but 
much better than that sample).

Following is what I emproved based on that sample.

1. Change the LocalService and Activity communication 
mechanism from broadcast intent into callback hooks.
2. Add RSSI signal detect.
3. Add write characteristic dialog(still need improve).

Why I make above emprovment?

1. Broadcast Intents was not dependable, I guess that's
the reason that I missed a lot of messages. So I studied 
the communicaiton between Service and Acitivity, and 
decided to use callback hooks to send feedback or messages
to the forground Activity.

2. Since, I wanna develop an Loss-Resistant device, the most 
silly implementation is to detect the RSSI signal strength, 
and write a characteristic value back to BLE devices back to
inform whether it is losed or not. So I add an RSSI signal 
detection function into that APP.

3. Some time, we just need to write an characteristic value 
into the BLE database to operating the peripheral device.
That's an practical function in BLE device development. There
are an ios APP named LightBlue, I refered its function to 
add above functions. Actually, that's another reason why I develop
this app, I can't found any available APP in android platform. 