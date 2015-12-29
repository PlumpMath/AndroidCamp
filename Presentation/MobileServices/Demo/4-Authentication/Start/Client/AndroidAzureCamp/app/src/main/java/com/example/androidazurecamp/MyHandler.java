package com.example.androidazurecamp;

import android.content.Context;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.Registration;
import com.microsoft.windowsazure.mobileservices.RegistrationCallback;
import com.microsoft.windowsazure.notifications.NotificationsHandler;


public class MyHandler extends NotificationsHandler {

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
}
