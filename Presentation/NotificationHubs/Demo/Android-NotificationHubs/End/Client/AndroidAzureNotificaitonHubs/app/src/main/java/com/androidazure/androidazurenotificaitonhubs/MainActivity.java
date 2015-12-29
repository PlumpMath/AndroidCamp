package com.androidazure.androidazurenotificaitonhubs;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;


public class MainActivity extends ActionBarActivity {

    private final static String TAG = "MainActivity";
    private final static String SENDER_ID = "SenderId";
    private Button btnRegisterWithGcm, btnRegisterWithNoTags, btnRegisterWithTags, btnRegisterWithTemplates;
    private TextView lblRegistration, lblStatus;
    private GoogleCloudMessaging gcm;
    private String registrationId;
    private NotificationHub hub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegisterWithGcm = (Button) findViewById(R.id.btnRegisterWithGcm);
        btnRegisterWithGcm.setOnClickListener(registerWithGcmListener);
        btnRegisterWithNoTags = (Button) findViewById(R.id.btnRegisterWithNoTags);
        btnRegisterWithNoTags.setOnClickListener(registerWithNoTags);
        btnRegisterWithTags = (Button) findViewById(R.id.btnRegisterWithTags);
        btnRegisterWithTags.setOnClickListener(registerWithTags);
        btnRegisterWithTemplates = (Button) findViewById(R.id.btnRegisterWithTemplates);
        btnRegisterWithTemplates.setOnClickListener(registerWithTemplates);
        lblRegistration = (TextView) findViewById(R.id.lblRegistrationId);
        lblStatus = (TextView) findViewById(R.id.lblStatus);

        gcm = GoogleCloudMessaging.getInstance(this);

        String connectionString = "ListenConnectionString";
        hub = new NotificationHub("HubName", connectionString, this);
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);

    }

    private OnClickListener registerWithGcmListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Tapped");
            registerWithGcm();
        }
    };

    @SuppressWarnings("unchecked")
    private void registerWithGcm() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    registrationId = gcm.register(SENDER_ID);
                    Log.i(TAG, "Registered with id: " + registrationId);

                } catch (Exception e) {
                    Log.e(TAG, "Exception registering with GCM: " + e.getMessage());
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Object result) {
                lblRegistration.setText(registrationId);
                lblStatus.setText(getResources().getString(R.string.status_registered));
            };
        }.execute(null, null, null);
    }

    private OnClickListener registerWithNoTags = new OnClickListener() {
        @SuppressWarnings("unchecked" )
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Tapped register with no tags");
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        hub.register(registrationId);
                    } catch (Exception e) {
                        Log.e(TAG, "Issue registering with hub: " + e.getMessage());
                        return e;
                    }
                    return null;
                }
                protected void onPostExecute(Object result) {
                    Log.i(TAG, getResources().getString(R.string.status_registered_with_no_tags));
                    lblStatus.setText(getResources().getString(R.string.status_registered_with_no_tags));

                };
            }.execute(null, null, null);
        }
    };

    private OnClickListener registerWithTags = new OnClickListener() {
        @SuppressWarnings("unchecked" )
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Tapped register with tags");

            new AsyncTask() {
                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        hub.register(registrationId, "AndroidUser", "AllUsers");
                    } catch (Exception e) {
                        Log.e(TAG, "Issue registering with hub with tag: " + e.getMessage());
                        return e;
                    }
                    return null;
                }
                protected void onPostExecute(Object result) {
                    Log.i(TAG, getResources().getString(R.string.status_registered_with_tags));
                    lblStatus.setText(getResources().getString(R.string.status_registered_with_tags));

                };
            }.execute(null, null, null);
        }
    };

    private OnClickListener registerWithTemplates = new OnClickListener() {
        @SuppressWarnings("unchecked" )
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Tapped register with templates");

            new AsyncTask() {
                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        hub.registerTemplate(registrationId, "messageTemplate", "{\"data\":{\"msg\":\"$(message)\"}, \"collapse_key\":\"$(collapse_key)\"}", "AllUsers", "AndroidUser");
                    } catch (Exception e) {
                        Log.e(TAG, "Issue registering with hub with tag: " + e.getMessage());
                        return e;
                    }
                    return null;
                }
                protected void onPostExecute(Object result) {
                    Log.i(TAG, getResources().getString(R.string.status_registered_with_templates));
                    lblStatus.setText(getResources().getString(R.string.status_registered_with_templates));

                };
            }.execute(null, null, null);

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
