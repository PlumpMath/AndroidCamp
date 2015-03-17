Notification Hubs Session Demos
==========

This describes the demos used during the Notification Hubs session of the Android Azure camp content.

# Prerequisites

1.  Install Android Studio
2.  Ensure you have latest Android SDK as well as SDKs from 18 up
3.  Create a Mobile Service in Azure to use for triggering pushes from your Notification Hub
4.  Ensure you have an emulator running capable of receiving push notifications through GCM
5.  You will need a Google account both for accessing Google's portal as well as for performing authentication within your app
5.  Create a project in the [Google Code Console](https://console.developers.google.com) to use for these demos
6.  Download the full [Notification Hubs SDK(https://go.microsoft.com/fwLink/?LinkID=280126&clcid=0x409) and ensure you have the notifications-1.0.1.jar and notification-hubs-0.4.jar files
7.  Part of the presentation involves doing a geography based push, for that follow the steps below to set up the Geo Demo
8.  Another part of the push involves showing off cross platform push.  For that demo set up either (or both) the WinStore Demo or the iOS Demo with the steps below
1.  For the purposes of this demo and saving time, the current recommendation is to start from the **Demos/Android-NotificationHubs/End/Client** Android Studio project
2.  This project has all of the code and UI done, you'll just need to supply the settings you'll get throughout the demo

# Setting up the Geo Demo

1.  Use the Android Studio project located in **Demos/NotificationHubs/Android-Geo** and the project you created in the Google Console
2.  Follow the instructions to [create a Google Maps V2 API Key](https://developers.google.com/maps/documentation/android/start)
3.  In Android Studio, open the project's **AndroidManifest.xml**
4.  Look for **android.maps.v2.API_KEY**
5.  Replace it's value with the key you have generated
6.  Run the app and ensure maps are showing up
7.  Run through setting the emulator's GPS coordinates using Android Device Monitor or ADB
8.  Select a location to set the GPS coords to during the demo later and know what city it will correspond to

# Setting up the WinStore Demo

1.  Use the Windows Store project located in **Demos/NotificationHubs/WinStoreNotificationHubs**
2.  You'll need to create your own app in the Windows Store marketplace and follow the steps found [here](http://azure.microsoft.com/en-us/documentation/articles/notification-hubs-windows-store-dotnet-get-started/) to connect the Visual Studio project to your app in the store
3.  Follow the same instructions to obtain the **Package SID** and **Client Secret**

# Setting up the iOS Demo

1.   Note that to run this app, you'll need to use a device as simulators do NOT support Push Notifications
2.  For best delivery, you'll also want to screen share the device on to your computer.  I recommend the [Reflector app](http://www.airsquirrels.com/reflector/) for this.
3.  Use the Xcode project located in **Demos/NotificationHubs/iOS-NotificationHubs**
2.  The easiest way to get this setup is to follow the steps outlined [here](http://azure.microsoft.com/en-us/documentation/articles/notification-hubs-ios-get-started/)
3.  Follow steps 1 - 3 to handle all of the steps in the Apple Dev Portal
4.  You should be left with a certificate, a bundle identifier (app ID), and a provisioning profile, all tied together
5.  Ensure that when you set the Xcode project's **Bundle Identifier** in the project's **Info** tab, you are able to set the **Provisioning Profile** and **Code Signing Identity** to the matching values
6.  Save the certificate for later.

# Setting up the Mobile Service for Push Notifications

1.  In this session, you'll be using the Custom API functionality of a Mobile Service to trigger push notifications
2.  This is done just as an easy method of exposing the functionality to trigger push notifications but it's important you highlight that there are many different ways to talk to a Notification Hub to trigger a push and this is just an example
3.  Start by creating the following Custom APIs in your Mobile Service.  Ensure they all have the **GET** permission set to **Everyone**:
     1.  PushTestNoTag
     2.  PushTestTag
     3.  PushTestTemplate
     4.  RegisterForPush
4.  Copy the script functionality from **Demos/Android-NotificationHubs/End/Server/service/api/*.js** into the custom APIs you've generated on the server


# Demo: Hubs

1.  Go to [http://manage.windowsazure.com](http://manage.windowsazure.com)
2.  Create a new Notification Hub
3.  Open the Hub and go to the **Configure** tab
4.  Before you can configure your hub, you need to set up Push with GCM
5.  Go to the [Google Code Console](https://console.developers.google.com)
6.  Open the project you created as a pre-req
7.  Make note of the **Project Number** at the top of the dashboard (**NOT** Project ID)
8.  Click **APIs and Auth** from left navigation and then **APIs**
9.  Find **Google Cloud Messaging for Android** and click the Off button on the right side
10.  Under **APIs and Auth** in the navigation, click **Credentials**
11.  Under **Public API Access**, click **Create new key**
12.  Choose **Server key**
13.  Leave the IP Restrictions box blank and click **Create**
14.  After the key is created, copy the **API KEY**
15.  Return to the Azure portal
16.  Put the GCM API Key under **Google Cloud Messaging Settings**
17.  Save your changes
18.  Go to the **Dashboard** tab
19.  Click on the **Connection Information** button at the bottom
20.  Explain the difference between the **Listen** shared access signature and the **Full** shared access signature
21.  Copy the **Listen** shared access signature
22.  Go to the project in Android Studio
23.  Open the **MainActivity.java** file
24.  Replace the **SenderId** value with the **Project Number** from your Google Project
25.  Replace the **ListenConnectionString** with the **Listen** shared access signature from your Notification Hub
26.  Replace the **HubName** with the name of your hub (note that this is not the URL or the Service Bus namespace, just the Hub's name)
26.  Walk through the code in **MainActivity** for the following:
    1.  **onCreate** - getting a GCM instance and creating the hub
    2.  **registerWithGcm** - calling **register** on gcm
    3.  **registerWithNoTags** - calling **register** on the hub
27.  Walk through the code in **myHandler.java** for receiving messages and displaying notifications
27.  Run the app
28.  Tap the **Register with GCM** button
29.  You should now be displaying the registration ID from GCM
30.  Tap the **Register without Tags** button
31.  Return to the portal
32.  Copy the **Full** shared access signature
33.  Open the Mobile Service you created in the pre-reqs
34.  Go to the **API** tab
35.  Open the **PushTestNoTag** script
36.  Replace the **HubName** with the name of your Notification Hub
37.  Replace the **FullConnectionString** with the **Full** shared access signature
38.  Save your changes
39.  In a browser window, navigate to https://YourService.azure-mobile.net/apis/PushTestNoTag
40.  After a few moments you should get a message back saying a notification was sent
41.  You should see a push notification go through to your Android emulator

# Demo: Tags

1.  Return to your app in Android Studio
2.  Walk through the **MainActivity** code for **registerWithTags**
3.  Return to the app
4.  Tap **Register with Tags**
5.  Return to the Azure portal and your Mobile Service
6.  Open the **PushTestTag** custom API
7.  Replace the **HubName** with the name of your Notification Hub
8.  Replace the **FullConnectionString** with the **Full** shared access signature
9.  Save your changes
10.  In a browser window, navigate to https://YourService.azure-mobile.net/apis/PushTestTag
11.  After a moment you should get a response in the browser and a push should appear
12.  Return to the Notification Hub in the portal
13.  Go to the **Debug** tab
14.  Explain how the debug tab can be used to test out push
15.  Select **Android** from the platforms drop down
16.  Turn **OFF** random broadcast
17.  Sender a tag used with the app (i.e. **AndroidUser**)
18.  Change the body to use **msg** instead of **message**
19.  Click **Send**
20.  After a few moments the progress indicator should stop and you should receive a push in your app
21.  Return to the portal and change the tag to something the app did **NOT** register for (i.e. iOSUser)
22.  Click **Send**
23.  Demonstrate that no push is received

# Demo: Geo Push

1.  You should have already done the setup necessary to ensure that the maps will work in your app
2.  Open the **MainActivity.java** file
3.  Replace the **SenderId** value with the **Project Number** from your Google Project
4.  Replace the **ListenConnectionString** with the **Listen** shared access signature from your Notification Hub
5.  Replace the **HubName** with the name of your hub (note that this is not the URL or the Service Bus namespace, just the Hub's name)
6.  Walk through the **registerWithCity** method
7.  Highlight that the city name (with spaces removed) is used as a tag
6.  Run the app
7.  Use ADM or ADB to set the GPS coordinates to whatever location you choose
8.  The city that is tied to that location should appear on screen briefly in a moment
9.  Return to the **Debug** page in the portal
10.  Change the *Send to Tag** to be the city that was used (without spaces)
11.  Send a push
12.  Within a few moments you should receive a push

# Demo: Templates

1.  Return to Android Studio (for the non-geo app)
2.  Go to the **registerWithTemplates** method
3.  Highlight the new JSON template sent as a parameter to registering with the hub
4.  Highlight the **$(message)** wild card within the template
5.  Return to the app and tap **Register with Templates**
6.  Open up either the iOS app or Windows Store app
7.  In the iOS app, set the **HubName** and **ListenConnectionString** in the **ViewController.m** file
8.  In the WinStore app's **app.xaml.cs** file, set the **HubName** and **ListenConnectionString**
9.  Run whichever app you prefer and ensure that it registers and gets a token (iOS) or a channel URI (windows)
10.  Tap the **Register with Templates** button in whichever app you're running
11.  Return to your Azure Mobile Service in the portal.
12.  Open the **PushTestTemplate** API
13.  Replace the **HubName** with the name of your Notification Hub
14.  Replace the **FullConnectionString** with the **Full** shared access signature
15.  Save your changes
16.  In a browser window, navigate to https://YourService.azure-mobile.net/apis/PushTestTemplate
11.  After a moment you should get a response in the browser and a push should appear in both the Android app and whatever app you're also running

# Demo: Server Registration

1.  Return to your Mobile Service in the Azure Portal
2.  Open the **RegisterForPush** custom API
3.  Highlight the Azure module used for talking to the hub
3.  Note how you're removing all existing registrations for a device
4.  Note how you're creating a template depending on the platform (which would be sent by the calling app)
5.  Note the registration method called depending on the platform
