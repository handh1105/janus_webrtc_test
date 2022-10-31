package in.minewave.janusvideoroom.v2;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

import java.math.BigInteger;

import in.minewave.janusvideoroom.JanusRTCInterface;
import in.minewave.janusvideoroom.R;
import in.minewave.janusvideoroom.WebSocketChannel;

//import in.minewave.janusvideoroom.PeerConnectionClient.PeerConnectionParameters;
//import in.minewave.janusvideoroom.PeerConnectionClient.PeerConnectionEvents;

public class MainActivityV2 extends AppCompatActivity implements JanusRTCInterface { //, PeerConnectionEvents {
    private static final String TAG = "MainActivity";
    //public static final Integer ROOMID = 7777;

//    private PeerConnectionClient peerConnectionClient;
//    private PeerConnectionParameters peerConnectionParameters;

    private SurfaceViewRenderer localRender;
    private SurfaceViewRenderer remoteRender;
    private VideoCapturer videoCapturer;
    private EglBase rootEglBase;
    private WebSocketChannel mWebSocketChannel;
    LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = (LinearLayout) findViewById(R.id.activity_main);

        String roomId = getIntent().getStringExtra("roomId");

        if(roomId==null) {
            finish();
        }

        mWebSocketChannel = new WebSocketChannel();
        mWebSocketChannel.initConnection("wss://poc-media1.saramin.co.kr:8189/janus", Integer.parseInt(roomId));
        //mWebSocketChannel.initConnection("wss://janus.conf.meetecho.com/ws");
        //mWebSocketChannel.initConnection("wss://poc-media1.saramin.co.kr:8189");

        mWebSocketChannel.setDelegate(this);

        createLocalRender();
        remoteRender = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);
        remoteRender.init(rootEglBase.getEglBaseContext(), null);

//        peerConnectionParameters  = new PeerConnectionParameters(false, 360, 480, 20, "H264", true, 0, "opus", false, false, false, false, false);
//        peerConnectionClient = PeerConnectionClient.getInstance();
//        peerConnectionClient.createPeerConnectionFactory(this, peerConnectionParameters, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        peerConnectionClient.startVideoSource();
    }

    private void createLocalRender() {
        localRender = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
        rootEglBase = EglBase.create();
        localRender.init(rootEglBase.getEglBaseContext(), null);
        localRender.setEnableHardwareScaler(true);
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }

    private boolean captureToTexture() {
        return true;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Log.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Log.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Log.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = null;
        if (useCamera2()) {
            Log.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Log.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            Log.e(TAG, "Failed to open camera");
            return null;
        }
        return videoCapturer;
    }


    private void offerPeerConnection(BigInteger handleId) {
        Log.e(TAG,"offerPeerConnection");
        videoCapturer = createVideoCapturer();
//        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(), localRender, videoCapturer, handleId);
//        peerConnectionClient.createOffer(handleId);
    }

    // interface JanusRTCInterface
    @Override
    public void onPublisherJoined(final BigInteger handleId) {
        offerPeerConnection(handleId);
    }

    @Override
    public void onPublisherRemoteJsep(BigInteger handleId, JSONObject jsep) {
        Log.e(TAG,"onPublisherRemoteJsep = "+ jsep.toString());
        SessionDescription.Type type = SessionDescription.Type.fromCanonicalForm(jsep.optString("type"));
        String sdp = jsep.optString("sdp");
        SessionDescription sessionDescription = new SessionDescription(type, sdp);

//        peerConnectionClient.setRemoteDescription(handleId, sessionDescription);
    }

    @Override
    public void subscriberHandleRemoteJsep(BigInteger handleId, JSONObject jsep) {
        Log.e(TAG,"subscriberHandleRemoteJsep = "+ jsep.toString());
        SessionDescription.Type type = SessionDescription.Type.fromCanonicalForm(jsep.optString("type"));
        String sdp = jsep.optString("sdp");
        SessionDescription sessionDescription = new SessionDescription(type, sdp);
//        peerConnectionClient.subscriberHandleRemoteJsep(handleId, sessionDescription);
    }

    @Override
    public void onLeaving(BigInteger handleId) {
    }

    // interface PeerConnectionClient.PeerConnectionEvents
//    @Override
//    public void onLocalDescription(SessionDescription sdp, BigInteger handleId) {
//        Log.e(TAG,"onLocalDescription :"+sdp.type.toString());
//        mWebSocketChannel.publisherCreateOffer(handleId, sdp);
//    }
//
//    @Override
//    public void onRemoteDescription(SessionDescription sdp, BigInteger handleId) {
//        Log.e(TAG, "onRemoteDescription :" +sdp.type.toString());
//        mWebSocketChannel.subscriberCreateAnswer(handleId, sdp);
//    }
//
//    @Override
//    public void onIceCandidate(IceCandidate candidate, BigInteger handleId) {
//        Log.e(TAG, "=========onIceCandidate========");
//        if (candidate != null) {
//            mWebSocketChannel.trickleCandidate(handleId, candidate);
//        } else {
//            mWebSocketChannel.trickleCandidateComplete(handleId);
//        }
//    }
//
//    @Override
//    public void onIceCandidatesRemoved(IceCandidate[] candidates) {
//
//    }
//
//    @Override
//    public void onIceConnected() {
//        Log.e(TAG,"onIceConnected");
//    }
//
//    @Override
//    public void onIceDisconnected() {
//        Log.e(TAG,"onIceDisconnected");
//    }
//
//    @Override
//    public void onPeerConnectionClosed() {
//        Log.e(TAG,"onPeerConnectionClosed");
//    }
//
//    @Override
//    public void onPeerConnectionStatsReady(StatsReport[] reports) {
//        Log.e(TAG,"onPeerConnectionStatsReady");
//    }
//
//    @Override
//    public void onPeerConnectionError(String description) {
//        Log.e(TAG,"onPeerConnectionError - " +description);
//    }
//
//    @Override
//    public void onRemoteRender(final JanusConnection connection) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                remoteRender = new SurfaceViewRenderer(MainActivity.this);
////                remoteRender.init(rootEglBase.getEglBaseContext(), null);
////                LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////                rootView.addView(remoteRender, params);
//
//
//
//                connection.videoTrack.addRenderer(new VideoRenderer(remoteRender));
//            }
//        });
//    }

    @Override
    protected void onPause() {
        super.onPause();
//        peerConnectionClient.stopVideoSource();
    }

    @Override
    public void onClosing() {
        Log.e(TAG,"onClosing");
//        peerConnectionClient.close();
//        remoteRender.release();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    private void disconnect() {

        if(mWebSocketChannel!=null) {
            mWebSocketChannel.close();
            mWebSocketChannel = null;
        }

        if(localRender!=null) {
            localRender.release();
            localRender = null;
        }

        if(remoteRender!=null) {
            remoteRender.release();
            remoteRender = null;
        }

//        if(peerConnectionClient != null) {
//            peerConnectionClient.close();
//            peerConnectionClient = null;
//        }
    }
}
