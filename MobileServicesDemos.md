Mobile Services Session Demos
==========

This describes the demos used during the Mobile Services session of the Android Azure camp content.

# Prerequisites

1.  Install Android Studio
2.  Ensure you have latest Android SDK as well as SDKs from 18 up
3.  Create a Mobile Service in Azure to act as a backup in case the service created below takes too long to spin up.
4.  Ensure you have an emulator running capable of receiving push notifications through GCM
5.  You will need a Google account both for accessing Google's portal as well as for performing authentication within your app
5.  Create a project in the [Google Code Console](https://console.developers.google.com) for use in case the one created later has any issues
6.  Download the full [Mobile Services SDK](http://aka.ms/Iajk6q) and ensure you have the notifications-1.0.1.jar file

# Special Notes

1.  Demo code for the client and server is available for these demos under ROOT\demos\MobileServices
2.  These samples have both **Start** and **End** client and server code
3.  If using the code supplied, you'll need to replace values depending on where you're at in the demos
4.  For all demos, you'll need to put your Mobile Service's name where **<MobileServiceName>** is found
5.  For all demos, you'll need to put your Mobile Service's application key where **MobileServiceApplicationKey** is found
6.  For demos 3 and 4, you will need to replace **SENDERID** with your Google Project's project number
7.  When importing the project into Android Studio, it will create the project in a different directory than where the unzipped version is.  Make sure you're using this new directory when you add the JAR file during the Push Notifications demo

# Demo: Getting Started

1.  Go to [http://manage.windowsazure.com](http://manage.windowsazure.com)
2.  Create a new Mobile Service with a JavaScript backend
3.  Optionally talk while it's spinning up or use the pre-req Mobile Service
4.  Go to the Quick Start page
5.  Select Android
6.  Select "Create a New Android App"
7.  Click "Create TODO Item Table" button
8.  Download the app
9.  Open Android Studio
10.  Choose "Import Non-Android Studio Project"
11.  Navigate to your unzipped project
12.  Choose all default options for the import
13.  Go to Build, Make Project
14.  Go to Run, Run
15.  Choose the emulator / device to deploy to
16.  Once app is deployed, add a few todo items
17.  Go to Data tab in portal, go to Todo Item table
18.  Highlight data being there, talk about Dynamic Schematization
19.  Mark one item complete in app
20.  Refresh data in portal and show updated row
21.  Return to Android Studio
22.  Walk through TodoItem.java code
23.  Walk through TodoActivity
   1. Variables: MobileServicesClient, MobileServiceTable
   2.  Creation of the Mobile Service client including App Key
   3.  mClient.getTable
   4.  refreshItemsFromTable method
   5.  addItem method
   6.  checkItem method

# Demo: Customizing Logic

1.  Return to portal
2.  Go to Data tab
3.  Open TodoItem table
4.  Go to Scripts tab
5.  Highlight drop down for selecting which action you want to see the script for
6.  Explain the parameters passed into the insert script (item, user, request)
7.  Alter the insert script to the following:

        function insert(item, user, request) {
          if (item.text.length < 5) {
            request.respond(statusCodes.BAD_REQUEST, "Your todo item must be at least 5 characters");
          } else {
            request.execute();
          }
      }

8.  Save the script change
9.  Return to your app running in the emulator
10.  Attempt to save a short todo item (such as "abc"), app should show error
11.  Save another item to demonstrate app working (such as "Data test")

# Demo: Adding Push Notifications

1.  Go to the [Google Code Console](https://console.developers.google.com)
2.  Create a new project
3.  After project has been created, you should be redirected to its Dashboard
4.  Make note of the **Project Number** at the top of the dashboard (**NOT** Project ID)
5.  Click **APIs and Auth** from left navigation and then **APIs**
6.  Find **Google Cloud Messaging for Android** and click the Off button on the right side
7.  Under **APIs and Auth** in the navigation, click **Credentials**
8.  Under **Public API Access**, click **Create new key**
9.  Choose **Server key**
10.  Leave the IP Restrictions box blank and click **Create**
11.  After the key is created, copy the **API KEY**
12.  Return to your Mobile Service in the Azure portal
13.  Go to the **PUSH** tab
14.  Paste the key under the **Google Cloud Messaging Settings** and save your changes
15.  Copy notifications-1.0.1.jar file into the /app/libs folder
16.  Return to Android Studio
17.  Open the **build.gradle (Module: app)** file
18.  Under **dependencies** add the following:

        compile 'com.google.android.gms:play-services:+'
        compile files('libs/notifications-1.0.1.jar')

19.  You should be prompted to sync your Gradle file at the top right, do so
20.  You'll now receive an error about the Google Play Services minimum SDK supported being 9, change the **minSdkVersion** value in the gradle file from 8 to 9 and resync
21.  The app should now compile without errors
22.  Open the **AndroidManifest.xml** file and paste the following permissions under the **uses-permission** line:

        <permission android:name="**my_app_package**.permission.C2D_MESSAGE" android:protectionLevel="signature" />
        <uses-permission android:name="**my_app_package**.permission.C2D_MESSAGE" />
        <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
        <uses-permission android:name="android.permission.GET_ACCOUNTS" />
        <uses-permission android:name="android.permission.WAKE_LOCK" />

23.  Replace **my_app_package** with your app package name
24.  Add the following right before the **</application>** tag:

        <receiver android:name="com.microsoft.windowsazure.notifications.NotificationsBroadcastReceiver" android:permission="com.google.android.c2dm.permission.SEND">
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <category android:name="**PACKAGENAME**" />
            </intent-filter>
        </receiver>

25.  Replace the **PACKAGENAME** with your package name
26.  Open **TodoItem.java** and paste the following code inside of the class:

        @com.google.gson.annotations.SerializedName("handle")
        private String mHandle;
        public String getHandle() {
            return mHandle;
        }
        public final void setHandle(String handle) {
            mHandle = handle;
        }

27.  Open **TodoActivity.java** and paste this inside the class:

        public static final String SENDER_ID = "<SENDER_ID>";

28.  Replace the **SENDERID** value with the **Project Number** from your Google project
29.  Change the **mClient** variable to be a public static var.
29.  In the **onCreate** method, paste the following code before the MobileService Client creation:

        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);

30.  Add the import for NotificationsManager to your file
31.  In the **addItem** method, add this code after the call to **setComplete**:

        item.setHandle(MyHandler.getHandle());

32.  Right click on your package in the project explorer and choose New, Java Class
33.  Name the class **MyHandler**
34.  Add the following after the class name:

        extends NotificationsHandler

35.  Add the import for that class to your file
36.  Add the following code inside your class:

        @com.google.gson.annotations.SerializedName("handle")
        private static String mHandle;
        public static String getHandle() {
            return mHandle;
        }
        public static final void setHandle(String handle) {
            mHandle = handle;
        }
        @Override
        public void onRegistered(Context context, final String gcmRegistrationId) {
            super.onRegistered(context, gcmRegistrationId);
            Log.i("NotificationHandler", "registered with id: " + gcmRegistrationId);
            this.setHandle(gcmRegistrationId);
            ToDoActivity.mClient.getPush().register(gcmRegistrationId, null, new RegistrationCallback() {
                @Override
                public void onRegister(Registration registration, Exception exception) {
                    if (exception != null) {
                        // handle error
                        Log.e("MyHandler", "Issue registering with hubs: " + exception.getMessage());
                    }
                    else {
                        Log.i("MyHandler", "Registration with hubs complete");
                    }
                }
            });
        }

38.  Return to the Azure portal
39.  Go to the data tab
40.  Select the TodoItem table
41.  Go to the Scripts tab
42.  Replace the insert script with the following:

        function insert(item, user, request) {
            request.execute({
                success: function() {
                    // Write to the response and then send the notification in the background
                    request.respond();            
                    var payload = {
                        data: {
                            message: item.text 
                        }
                    };             
                    push.gcm.send(null, payload, {
                        success: function(response) {
                            console.log('Push notification sent: ', response);
                        }, error: function(error) {
                            console.log('Error sending push notification: ', error);
                        }
                    });
                }
            });
        }

43.  Save your script change
44.  Rerun your application
45.  Save a new todo item
46.  After a few seconds you should receive a push notification and see a toast with the text from the todo item.

# Demo: Adding Auth

1.  Return to the portal
2.  Go to the Data tab for your service
3.  Go to the Todo Item table
4.  Go to the **Permissions** tab
5.  Change all permissions to **Only Authenticated Users**
6.  Save changes
7.  Return to the app and try to refresh / save data
8.  Show that an unauthorized error is now returned to your app
2.  Return to the [Google Code Console](https://console.developers.google.com)
2.  Under **APIs and Auth** in the navigation, click **Credentials**
3.  Click the **Create new Client ID** button
4.  Select **Web application** as the type and click **Configure consent screen**
5.  Select your email address from the drop down and enter a product name (i.e. Android Azure Demo)
6.  Click **Save** at the bottom
7.  For **Authorized Javascript Origins* paste the url of your Mobile Service (i.e. https://myapp.azure-mobile.net/)
8.  Under **Authorized Redirect URIs** enter the Url of your mobile service with **login/google** at the end (i.e. https://myapp.azure-mobile.net/login/google)
9.  Click **Create Client ID** at the bottom
10.  Copy the **Client ID** and **Client Secret** values
11.  Return to your service in the Azure portal
12.  Go to the **Identity** tab
13.  Under **Google Settings** paste ID and Secret
14.  Save your changes
15.  Return to Android Studio
16.  Open the TodoActivity.java file
17.  Paste the following method inside your class:

        private void authenticate() {
            // Login using the Google provider.
            mClient.login(MobileServiceAuthenticationProvider.Google,
                new UserAuthenticationCallback() {
                @Override
                public void onCompleted(MobileServiceUser user,
                        Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                                    createAndShowDialog(String.format(
                                            "You are now logged in - %1$2s",
                                            user.getUserId()), "Success");
                            createTable();
                        } else {
                                    createAndShowDialog("You must log in. Login Required", "Error");
                        }
                    }
                });
        }

26.  In the **onCreate** method, take out all code after you are creating your Mobile Service Client until the brace before the catch block.
27.  Place this code inside the try catch, after you create the Client:

        authenticate();

28.  Place the following method in the class:

        public void createTable() {
            // Get the Mobile Service Table instance to use
            mToDoTable = mClient.getTable(ToDoItem.class);      
            mTextNewToDo = (EditText) findViewById(R.id.textNewToDo);
            // Create an adapter to bind the items with the view
            mAdapter = new ToDoItemAdapter(this, R.layout.row_list_to_do);
            ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);
            // Load the items from the Mobile Service
            refreshItemsFromTable();
        }

29.  Build your application and run on the emulator
30.  You should first be prompted to authenticate with a Google account
31.  Log in and you should now see the todo item data again
32.  Point out that while you're app is now secure, everyone still has access to everyone's data
32.  Return to the Azure portal
33.  Go to the Data tab
34.  Go to the TodoItem table
35.  Go to the Scripts tab
36.  Add the following line of code before you call **request.execute()**:

        item.userId = user.userId;

37.  Save your changes
37.  Return to the app and save a todo item
38.  Return to the portal and go to the **Browse** tab to see the todo item data
39.  Point out the row you just saved that now has a **userId** column in it
40.  Point out that data that is saved is now always tied to the user that saved it
40.  Return to the Scripts tab and use the drop down to go to the Read script
41.  Add the following line of code before you call **request.execute()**:

        query.where( { userId: user.userId} );

42.  Save your changes
43.  Return to the app and refresh the data
44.  You should now only see the data that is tied to your user account
45.  Point out that your app is now really secure in that only the person that saves the data can see it

# Demo: Using the CLI

1.  Open the Terminal
2.  Perform the following commands:
    1.  azure (prints out main azure help page)
    2.  azure mobile (prints out the mobile help page)
    3.  azure mobile list (prints out all of your mobile services)
    4.  azure mobile config list <MobileServiceName> (prints out config for your service)

# Demo: Scaling

1.  Return to the portal
2.  Go to the **Dashboard** tab
3.  Highlight metrics information shown on that page
4.  Go to the **Logs** tab
5.  Click on any log entry and go to **Details**
6.  Go to the **Scale** tab
7.  Move from **Free** to **Basic** or **Standard**
8.  Show the sliding **Unit count**
9.  Change the **Scale-by-metric** to **API Calls**
10.  Show how you can now specify a minimum and maximum units





