package in.minewave.janusvideoroom;

import org.json.JSONObject;

import java.math.BigInteger;

interface OnJoined {
    void onJoined(JanusHandle jh);
}

interface OnRemoteJsep {
    void onRemoteJsep(JanusHandle jh, JSONObject jsep);
}

interface OnIsExist {
    void onIsExist(JanusHandle jh);
}

public class JanusHandle {

    public BigInteger handleId;
    public BigInteger feedId;
    public String display;

    public OnJoined onJoined;
    public OnRemoteJsep onRemoteJsep;
    public OnJoined onLeaving;

    public OnIsExist onIsExist;
}
