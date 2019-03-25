package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import tv.zender.ZenderAuthentication;
import tv.zender.ZenderError;
import tv.zender.ZenderSession;
import tv.zender.ZenderUserDevice;
import tv.zender.api.ZenderApiClient;
import tv.zender.api.ZenderApiLoginCallback;
import tv.zender.api.ZenderApiRegisterDeviceCallback;
import tv.zender.player.ZenderPlayerConfig;
import tv.zender.player.ZenderPlayerListener;
import tv.zender.player.ZenderPlayerView;
import tv.zender.player.video.ZenderMediaPlayerView;
import tv.zender.player.video.ZenderPhenixVideoView;

public class RNZenderPlayerManager extends ViewGroupManager<ZenderPlayerView> implements LifecycleEventListener, ZenderPlayerListener {

    public static final String REACT_CLASS = "RNZenderPlayer";
    private static final String TAG = "RNZenderPlayer";

    private ZenderPlayerView zenderPlayerView = null;
    private ThemedReactContext context;
    private String mDeviceToken = null;
    private String mTargetId = null;
    private String mChannelId = null;
    private Boolean mDebugEnabled = false;
    private HashMap<String, Object> mAuthentication;
    private ZenderAuthentication zenderAuthentication;
    private ZenderUserDevice zenderUserDevice;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {

        // Maps the function on
        //return super.getExportedCustomBubblingEventTypeConstants();
        /**
         * This method maps the sending of the "onZenderPlayerClose" event to the JS "onZenderPlayerClose" function.
         */

        return MapBuilder.<String, Object>builder()
                .put("onIosZenderPlayerQuizShareCode",
                        MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onIosZenderPlayerQuizShareCode")))
                .put("onIosZenderPlayerClose",
                        MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onIosZenderPlayerClose")))
                .build();

    }


    @Override
    protected ZenderPlayerView createViewInstance(ThemedReactContext reactContext) {

        context = reactContext;
        Log.d(TAG, "create ZenderPlayer Instance");
        zenderPlayerView = new ZenderPlayerView(reactContext);

        // Cleaning devices
        mTargetId = null;
        mChannelId = null;
        mDebugEnabled = null;
        mDeviceToken = null;
        mAuthentication = null;
        zenderUserDevice = null;
        zenderAuthentication = null;

        // We listen to lifecycle events to get resume/pause/destroy requests
        reactContext.addLifecycleEventListener(this);

        return zenderPlayerView;
    }

    /*
    @Override
    public boolean needsCustomLayoutForChildren() {
        return true;
    }
    */

    /*
    @Override
    public boolean shouldPromoteGrandchildren() {
        return true;
    }
    */

    protected void trylaunchPlayer() {
        if (mTargetId==null) return;
        if (mChannelId ==null) return;
        if (mAuthentication==null) return;
       // if (mDeviceToken==null) return;

        Log.d(TAG, "launching zender player");

        ZenderPlayerConfig playerConfig = new ZenderPlayerConfig(mTargetId, mChannelId);
        // Temporary override endpong for new styling
        String playerEndpointPrefix = "https://player2-native.zender.tv";
        playerConfig.overridePlayerEndpointPrefix(playerEndpointPrefix);

        // Register video player
        ZenderPhenixVideoView phenixVideoView = new ZenderPhenixVideoView(context);
        playerConfig.registerVideoView(phenixVideoView);

        ZenderMediaPlayerView mediaPlayerView = new ZenderMediaPlayerView(context);
        playerConfig.registerVideoView(mediaPlayerView);

        if (mDebugEnabled != null) {
            playerConfig.debugEnabled = mDebugEnabled;
        }

        // Prepare zenderAuthentication
        String providerType = (String)mAuthentication.get("provider");
        HashMap<String, Object> authenticationMap = (HashMap<String, Object>) mAuthentication.get("payload");

        zenderAuthentication = new ZenderAuthentication(authenticationMap, providerType);
        zenderPlayerView.setAuthentication(zenderAuthentication);

        // Register device if we got a token
        if (mDeviceToken!=null) {
            zenderUserDevice = new ZenderUserDevice();
            zenderUserDevice.token = mDeviceToken;
            playerConfig.setUserDevice(zenderUserDevice);
            registerDeviceViaApi();
        }

        // Prepare for final configuration
        zenderPlayerView.setConfig(playerConfig);

        this.zenderPlayerView.registerZenderPlayerListener(this);

        zenderPlayerView.start();

    }
    @ReactProp(name="targetId")
    public void setTargetId(ZenderPlayerView view, String targetId) {
        Log.d(TAG, "setTargetId : "+targetId);

        mTargetId = targetId;
        trylaunchPlayer();

        // view.setConfig(targetId,channelId);
    }

    @ReactProp(name="channelId")
    public void setChannelId(ZenderPlayerView view, String channelId) {
        Log.d(TAG, "setChannelId : "+channelId);

        mChannelId = channelId;
        trylaunchPlayer();


    }

    @ReactProp(name="authentication")
    public void setAuthentication(ZenderPlayerView view, ReadableMap authentication) {
        Log.d(TAG, "setAuthentication : "+authentication.toString());

        mAuthentication = authentication.toHashMap();
        trylaunchPlayer();

    }

    @ReactProp(name="config")
    public void setConfig(ZenderPlayerView view, ReadableMap config) {

        Log.d(TAG, "setConfig : "+config.toString());

        if (config.hasKey("deviceToken")) {
            String deviceToken=config.getString("deviceToken");

            if (deviceToken != null) {
                mDeviceToken = deviceToken;
            }
        }

        if (config.hasKey("debugEnabled")) {
            mDebugEnabled = config.getBoolean("debugEnabled");
        } else {
            mDebugEnabled = false;
        }

        trylaunchPlayer();

    }


    @Override
    public void onHostPause() {
        Log.d(TAG, "LifeCycleEvent onHostPause");

        if (zenderPlayerView != null) {
            zenderPlayerView.pause();
        }
    }

    @Override
    public void onHostResume() {
        Log.d(TAG, "LifeCycleEvent onHostResume");

        if (zenderPlayerView != null) {
            zenderPlayerView.resume();
        }


    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "LifeCycleEvent onHostDestroy");

        if (zenderPlayerView != null) {
            zenderPlayerView.stop();
            zenderPlayerView.unregisterZenderPlayerListener(this);
            zenderPlayerView.release();
            zenderPlayerView = null;
        }



    }

    private void dispatchOnZenderPlayerClose() {

        Log.d(TAG, "onZenderPlayerClose");
        WritableMap event = Arguments.createMap();
        event.putString("event","zender-close");

        context.getJSModule(RCTEventEmitter.class).receiveEvent(
                zenderPlayerView.getId(),
                "onIosZenderPlayerClose",
                event
        );

    }

    private void dispatchOnZenderPlayerQuizShareCode(LinkedTreeMap linkedTreeMap) {

        String shareCode = null;
        String shareText = null;

        if (linkedTreeMap.containsKey("shareCode")) {
            shareCode = (String) linkedTreeMap.get("shareCode");
        }

        if (linkedTreeMap.containsKey("shareText")) {
            shareText = (String) linkedTreeMap.get("shareText");
        }

        if (linkedTreeMap.containsKey("text")) {
            shareText = (String) linkedTreeMap.get("text");
        }

        if (shareCode==null) {
            return;
        }

        Log.d(TAG, "onZenderPlayerQuizShareCode"+shareCode);
        WritableMap event = Arguments.createMap();
        event.putString("shareCode",shareCode);
        event.putString("shareText",shareText);

        context.getJSModule(RCTEventEmitter.class).receiveEvent(
                zenderPlayerView.getId(),
                "onIosZenderPlayerQuizShareCode",
                event
        );

    }


    void registerDeviceViaApi() {

        // We don't register if there is no zenderAuthentication
        if (zenderAuthentication == null) {
            return;
        }

        if (zenderUserDevice == null) {
            return;
        }

        if (mTargetId == null) {
            return;
        }

        if (mChannelId == null) {
            return;
        }

        final ZenderApiClient apiClient = new ZenderApiClient(context,mTargetId, mChannelId);
        apiClient.authentication = zenderAuthentication;

        //apiClient.overrideApiEndpoint("https://api.zender.tv/v1");

        apiClient.login(new ZenderApiLoginCallback(){
            @Override
            public void completionHandler(ZenderError error, ZenderSession session) {

                if (error == null ) {
                    Log.d(TAG, "API Client Session login token: "+session.token);
                    apiClient.registerDevice(zenderUserDevice, new ZenderApiRegisterDeviceCallback() {
                        @Override
                        public void completionHandler(ZenderError error) {
                            if (error != null) {
                                Log.d(TAG, "API Client Register Device Error occurred"+ error.description);
                            } else {
                                Log.d(TAG , "API Client Register Device successful");
                            }

                        }
                    });
                } else {
                    Log.d(TAG, "API Client Login error occurred" + error.description);
                }

            };
        });
    }

    @Override
    public void onZenderReady(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderUpdate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderFail(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPlayerFail(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPlayerReady(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPlayerClose(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

        dispatchOnZenderPlayerClose();
    }

    @Override
    public void onZenderPlayerLobbyEnter(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPlayerLobbyLeave(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAuthenticationInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAuthenticationRequired(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAuthenticationClear(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAuthenticationFail(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAuthenticationSuccess(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderTargetsInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderChannelsStreamsInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderChannelsStreamsPublish(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderChannelsStreamsUnpublish(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderStreamsInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderStreamsUpdate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderStreamsDelete(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderStreamsStats(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderMediaInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderMediaUpdate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderMediaDelete(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderMediaPlay(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxUpdate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxReplies(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxShout(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxShouts(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxShoutsDelete(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxShoutSent(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxDisable(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderShoutboxEnable(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderEmojisInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderEmojisUpdate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderEmojisStats(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderEmojisTrigger(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAvatarsStats(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAvatarsTrigger(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPollsInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPollsUpdate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPollsDelete(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPollsReset(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPollsVote(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPollsResults(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderPollsResultsAnimate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAppActivate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAppDeactivate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizInit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizUpdate(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizStart(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizStop(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizReset(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizDelete(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizQuestion(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizAnswer(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizAnswerTimeout(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizAnswerSubmit(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizAnswerCorrect(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizAnswerIncorrect(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizExtralifeUse(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizExtralifeIgnore(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizEliminated(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizEliminatedContinue(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizWinner(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizLoser(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizQuestionResults(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderQuizShareCode(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

        dispatchOnZenderPlayerQuizShareCode(linkedTreeMap);

    }

    @Override
    public void onZenderQuizResults(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderUiStreamsOverview(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderOpenUrl(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderAdsShow(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderLoaderShow(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderLoaderHide(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }

    @Override
    public void onZenderLoaderTimeout(com.google.gson.internal.LinkedTreeMap linkedTreeMap) {

    }
}
