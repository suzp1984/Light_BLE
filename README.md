Light_BLE
=========

Release Note Beta 2
-------------------

* New Architecture

I rewrite this project to compatible with the legendary MVP architecture, which decoupled the View and logical data layer, make your code rocks. And, the MVP don’t just became a pattern, but a boilerplate. But in order to practice it, some top technologies were required, you need to understand those bleeding edge technologies first, such as [Dependency Injection](http://zpcat.blogspot.com/2016/04/dependency-injection-in-android-with.html) and [ReactiveX protocol](http://zpcat.blogspot.com/2016/04/reactivex-operations.html).

The good news is that I already wrote some articles to introduce those two technologies, The most popular Dependency Injection(DI) implementation is Dagger, which is first open sourced by Square, named [Dagger 1](http://square.github.io/dagger/); then refactor and maintained by Google, named [Dagger 2](http://google.github.io/dagger/users-guide.html). Both of them are developed and maintained separately, the version 2 is not more advanced than version 1, but I recommend Google’s [Dagger 2](http://google.github.io/dagger/users-guide.html), which just much more easy to use.

Then, it is the sensational [ReactiveX](http://reactivex.io/), which implemented the Observer pattern, and provides an elegant way to design and write asynchronous code to avoid the callback hell and mess threading codes. I like ReactiveX. There is multiple Language implementation of ReactiveX, Rxjava is just one of them, so to understand Rx is to understand [Rx operations](http://zpcat.blogspot.com/2016/04/reactivex-operations.html). I wrote an article to illustrate [Reactive Operations](http://zpcat.blogspot.com/2016/04/reactivex-operations.html) and also some demo codes.

For gratitude and announcement, I learned MVP pattern by reading [ribot’s blog post](https://labs.ribot.co.uk/android-application-architecture-8b6e34acda65#.g4juxsf55), and also its excellent [android boilerplate](https://github.com/ribot/android-boilerplate) sample code.

* Upgrade to New SDK

The only question that in my emails, which from the Light-BLE users, is that it stop works anymore. Then debugs become the primary target during this upgrade.
The reason of Light-BLE stop works is that the runtime permission features of Android 6.0. Whether you install Light-BLE in Android 6.0 phone or upgrade compiler SDK to 6.0, it can not scan any types of equipments. That’s because, at Android 6.0, it require Location permission during any kinds of Bluetooth scanning, but Location permission belongs to the runtime permission. It should be checked and send request at the code runtime.

* A good way to wrote BLE code

New code support builds multiple Gatt Connections at the same time, and abandoned the dependency on the Service component, the old sample code came from the official document. They are ugly for two reasons: it coupled with an Android component, Service; it can not support multiple connectable channels with more than one BLE peripheral devices. Fortunately, new code solved all those troubles, and I can tell other people I once developed BLE devices because those boilerplate codes can be used in production.

* Still unfinished tasks

From Android 5.0, Bluetooth LE API was upgraded to support wrote code in Peripheral mode, before that, since the first release of Bluetooth LE at Android 4.3, only Central mode programming was supported. At this upgrade, I rewrote the code with the new API and remove the deprecated API in Android 5.0.

But, the peripheral mode coding guide can not be found in the official document, the lack of document was fatal; and my Nexus 5 do not support peripheral mode. For those two reasons, I can not test and develop Peripheral mode anymore. I believe it is time to leave it alone. This may be my last release.

And I also deleted the code to wrote the character to the remote Gatt Service. Again, no equipment to do the test.

Release Note Beta 1
-------------------

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
