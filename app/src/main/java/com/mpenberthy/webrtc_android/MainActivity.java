package com.mpenberthy.webrtc_android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

public class MainActivity extends AppCompatActivity implements Session.SessionListener,
        SubscriberKit.SubscriberListener, PublisherKit.PublisherListener, SubscriberKit.VideoListener {

    private static final int RC = 0x001;

    private Session session;
    private String apiKey;
    private String sessionId;
    private String token;


    private FrameLayout publisherLayout;
    private FrameLayout subscriberLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiKey = getString(R.string.api_key);
        sessionId = getString(R.string.sessionId);
        token = getString(R.string.token);

        publisherLayout = (FrameLayout) findViewById(R.id.publisherLayout);
        subscriberLayout = (FrameLayout) findViewById(R.id.subscriberLayout);

        requestAllPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC){
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.d("", "onRequestPermissionsResult: Permissions Granted");
            } else {
                requestAllPermission();
            }
        }
    }

    private void requestAllPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},RC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_connect){
            if (session == null){
                session = new Session(this, apiKey, sessionId);
                session.setSessionListener(this);
                session.connect(token);
            }
            return true;
        }

        if (id == R.id.action_disconnect){
            if (session != null){
                session.disconnect();

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        //This is called before the session disconnects
        publisherLayout.removeView(publisherKit.getView());
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(Session session) {
        //Check when session is connected
        Publisher publisher = new Publisher(this);
        publisher.setPublisherListener(this);
        publisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        publisherLayout.addView(publisher.getView());
        session.publish(publisher);
    }

    @Override
    public void onDisconnected(Session session) {
        //When the session is disconnected.
        this.session = null;
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        //Detect incoming streams
        Subscriber subscriber = new Subscriber(this, stream);
        subscriber.setSubscriberListener(this);
        subscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        session.subscribe(subscriber);
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        this.session = null;
        publisherLayout.removeAllViews();
        subscriberLayout.removeAllViews();
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        //When both users are connected
        subscriberLayout.addView(subscriberKit.getView());
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {

        subscriberLayout.removeView(subscriberKit.getView());

    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

    }

    @Override
    public void onVideoDataReceived(SubscriberKit subscriberKit) {

    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriberKit, String s) {

    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriberKit, String s) {

    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriberKit) {

    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {

    }
}
