package music.streaming.dev.snsh.musicstreaming;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.BottomNavBgMode;
import com.afollestad.aesthetic.BottomNavIconTextMode;
import com.afollestad.aesthetic.NavigationViewMode;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.liulishuo.filedownloader.util.FileDownloadLog;

import java.net.Proxy;

import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.utly.font.FontsOverride;


public class App extends Application {

    private static App application;
    private static PlaylistManager playlistManager;

    public static Context CONTEXT;
//    private final static String TAG = "FileDownloadApplication";


    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Zawgyi-One.ttf");

        /*FontsOverride.setDefaultFont(this, "CASUAL", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "CURSIVE", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF-BLACK", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF-CONDENSED", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF-CONDENSED-LIGHT", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF-LIGHT", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF-MEDIUM", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF-SMALLCAPS", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SANS-SERIF-THIN", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Zawgyi-One.ttf");
        FontsOverride.setDefaultFont(this, "SERIF-MONOSPACE", "fonts/Zawgyi-One.ttf");*/

        application = this;
        playlistManager = new PlaylistManager();


        CONTEXT = this;
// just for open the log in this demo project.
        FileDownloadLog.NEED_LOG = false;

        /**
         * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
         * by below code, so please do not worry about performance.
         * @see FileDownloader#init(Context)
         */
        FileDownloader.init(getApplicationContext(), new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                        .proxy(Proxy.NO_PROXY) // set proxy
                )));

        /*// below codes just for monitoring thread pools in the FileDownloader:
        IThreadDebugger debugger = ThreadDebugger.install(
                ThreadDebuggers.create() *//** The ThreadDebugger with known thread Categories **//*
                        // add Thread Category
                        .add("OkHttp").add("okio").add("Binder")
                        .add(FileDownloadUtils.getThreadPoolName("Network"), "Network")
                        .add(FileDownloadUtils.getThreadPoolName("Flow"), "FlowSingle")
                        .add(FileDownloadUtils.getThreadPoolName("EventPool"), "Event")
                        .add(FileDownloadUtils.getThreadPoolName("LauncherTask"), "LauncherTask")
                        .add(FileDownloadUtils.getThreadPoolName("BlockCompleted"), "BlockCompleted"),

                2000, *//** The frequent of Updating Thread Activity information **//*

                new ThreadDebugger.ThreadChangedCallback() {
                    *//**
         * The threads changed callback
         **//*
                    @Override
                    public void onChanged(IThreadDebugger debugger) {
                        // callback this method when the threads in this application has changed.
                        Log.d(TAG, debugger.drawUpEachThreadInfoDiff());
                        Log.d(TAG, debugger.drawUpEachThreadSizeDiff());
                        Log.d(TAG, debugger.drawUpEachThreadSize());
                    }
                });*/

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        application = null;
        playlistManager = null;
    }

    public static PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public static App getApplication() {
        return application;
    }
}
