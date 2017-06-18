package music.streaming.dev.snsh.musicstreaming.act;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.devbrackets.android.playlistcore.event.MediaProgress;
import com.devbrackets.android.playlistcore.event.PlaylistItemChange;
import com.devbrackets.android.playlistcore.listener.PlaylistListener;
import com.devbrackets.android.playlistcore.listener.ProgressListener;
import com.devbrackets.android.playlistcore.service.BasePlaylistService;
import com.devbrackets.android.playlistcore.service.PlaylistServiceCore;
import com.facebook.FacebookSdk;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.orhanobut.hawk.Hawk;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.encryptor4j.util.FileEncryptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Locale;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jp.wasabeef.blurry.Blurry;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.NowPlayingAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistModel;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistNameModel;
import music.streaming.dev.snsh.musicstreaming.dto.DownloadModel;
import music.streaming.dev.snsh.musicstreaming.dto.FavouriteModel;
import music.streaming.dev.snsh.musicstreaming.dto.LoadLyric;
import music.streaming.dev.snsh.musicstreaming.frag.dialog.MyBottomSheetDialogFragment;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.AndroidUtilCode.FileUtils;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import music.streaming.dev.snsh.musicstreaming.utly.NetworkHelper;
import music.streaming.dev.snsh.musicstreaming.utly.SendDurationToServer;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static music.streaming.dev.snsh.musicstreaming.utly.PlayLowQty.loadPlayLowQty;

public class NowPlayingActivity extends AppCompatActivity implements PlaylistListener<MediaItem>, ProgressListener {

    private static StringBuilder formatBuilder = new StringBuilder();
    private static Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

    private ProgressBar pb_loading;
    private ImageView iv_now_playing_image, iv_now_playing_image_blur;

    private TextView tv_player_position;
    private TextView tv_player_duration;

    //    private SeekBar sb_player;
    //    private ContentLoadingProgressBar clpb_player;
    private MaterialProgressBar mpb_player;
    private boolean shouldSetDuration;
    private boolean userInteracting;

    private ImageButton ib_previous;
    private ImageButton ib_play_pause;
    private ImageButton ib_next;
    private ImageButton ib_now_playing_song_detail;

    private PlaylistManager playlistManager;
    private int selectedPosition = 0;
    private boolean isDownloading;

    private Picasso picasso;
    private LinkedList<MediaItem> mediaItems;

    private TextView tv_song_name, tv_album_name, tv_artist_name;

    /*private MenuItem repeatMenuItem;
    private MenuItem shuffleMenuItem;
    private MenuItem actionLyrics;*/

    private ArrayList<MediaItem> nowPlayingData;
    private int nowPlayingIndex;
    private RecyclerView rv_now_playing;
    private NowPlayingAdapter nowPlayingAdapter;

    StringBuilder stringBuilder = new StringBuilder();
    private URL url;

    ScrollView sv_lyrics;
    TextView tv_lyrics;

    String songId, pathPrefix, photoUrl, songUrl, songName, artistName, albumName;
//    String lyricUrl, lyricPathPrefix;

    ShineButton sb_fav;
    private String cusId;

    private boolean flag_lyrics = false;
    private boolean play_shuffle = false;
    private boolean play_repeat = false;


    DonutProgress donut_download_progress;
    ImageButton ib_download, ib_lyric, ib_shuffle, ib_repeat;

    int downloadId;

    private EditText et_name;
    private View positiveAction;
    private MaterialDialog createNewPlaylist;

    private RequestInterface2 requestInterface2;
    private boolean isOffline;

    private FirebaseAnalytics mFirebaseAnalytics;

    private static byte[] encoded = {5, 6, 1, 7, 9, 4, 8, 4, 8, 2, 1, 3, 4, 9, 2, 2}; //Key data
    public static SecretKey aes = new SecretKeySpec(encoded, "AES");


    public static Intent newIntent(Context context) {
        return new Intent(context, NowPlayingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aesthetic.attach(this);
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.up_from_bottom, R.anim.fade_out);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_now_playing);

        boolean landscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_expand_more_white_24dp);

        Hawk.init(this).build();
        if (Hawk.contains(MConstants.CUS_ID)) {
            cusId = String.valueOf(Hawk.get(MConstants.CUS_ID, ""));
        }

        if (Hawk.contains(MConstants.PLAY_SHUFFLE)) {
            play_shuffle = Hawk.get(MConstants.PLAY_SHUFFLE);
        } else {
            Hawk.put(MConstants.PLAY_SHUFFLE, false);
            play_shuffle = false;
        }

        if (Hawk.contains(MConstants.PLAY_REPEAT)) {
            play_repeat = Hawk.get(MConstants.PLAY_REPEAT);
        } else {
            Hawk.put(MConstants.PLAY_REPEAT, false);
            play_repeat = false;
        }

        playlistManager = App.getPlaylistManager();
        playlistManager.registerPlaylistListener(this);
        playlistManager.registerProgressListener(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);

        retrieveViews();
        setupListeners();

        nowPlayingData = Hawk.get(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
        nowPlayingIndex = Hawk.get(MConstants.NOW_PLAYING_INDEX);

        rv_now_playing = (RecyclerView) findViewById(R.id.rv_now_playing);

        rv_now_playing.setLayoutManager(new LinearLayoutManager(this));

        nowPlayingAdapter = new NowPlayingAdapter(nowPlayingData, this);
        nowPlayingAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        rv_now_playing.setAdapter(nowPlayingAdapter);


        lyricScroll();

        sb_fav = (ShineButton) findViewById(R.id.sb_fav);
        sb_fav.setShapeResource(R.raw.heart);
        sb_fav.setBtnColor(Color.GRAY);
        sb_fav.setBtnFillColor(Color.WHITE);


        setCustomListener();
    }

    private void downloadSong() {
        /*Log.e("asdf", "getExternalStorageState() = " + Environment.getExternalStorageState());
        Log.e("asdf", "getExternalCacheDir() = " + this.getExternalCacheDir());
        Log.e("asdf", "getExternalFilesDir(null) = " + this.getExternalFilesDir(null));*/

        /*Log.e("asdf", "lyric url : " + lyricUrl);
        Log.e("asdf", "song id : " + songId);
        Log.e("asdf", "photo url : " + photoUrl);
        Log.e("asdf", "song url : " + photoUrl);*/
        Hawk.init(this).build();
        if (Hawk.contains(MConstants.CURRENT_DOWNLOAD_SONG_ID))
            Hawk.delete(MConstants.CURRENT_DOWNLOAD_SONG_ID);
        Hawk.put(MConstants.CURRENT_DOWNLOAD_SONG_ID, songId);

//        createDownloadTaskLyric(lyricUrl, pathPrefix + ".txt").start();
        createDownloadTaskPhoto(photoUrl, pathPrefix + ".pto").start();
        downloadId = createDownloadTaskSong(songUrl, pathPrefix + ".snsh").start();
    }

    private BaseDownloadTask createDownloadTaskLyric(String lyricUrl, String path) {
        return FileDownloader.getImpl().create(lyricUrl)
                .setPath(path, false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400);

    }

    private BaseDownloadTask createDownloadTaskPhoto(String photoUrl, String path) {
        return FileDownloader.getImpl().create(photoUrl)
                .setPath(path, false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400);

    }

    private BaseDownloadTask createDownloadTaskSong(final String songUrl, String path) {
        return FileDownloader.getImpl().create(songUrl)
                .setPath(path, false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadSampleListener() {

                                 @Override
                                 protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                     super.pending(task, soFarBytes, totalBytes);
                                 }

                                 @Override
                                 protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                     super.progress(task, soFarBytes, totalBytes);
                                     if (totalBytes == -1) {
                                         // chunked transfer encoding data
//                            donut_download_progress.setIndeterminate(true);
                                     } else {
                                         donut_download_progress.setMax(totalBytes);
                                         donut_download_progress.setProgress(soFarBytes);
                                         donut_download_progress.setText("");
                                     }

                                 }

                                 @Override
                                 protected void error(BaseDownloadTask task, Throwable e) {
                                     super.error(task, e);
                                     Hawk.init(NowPlayingActivity.this).build();
                                     if (Hawk.contains(MConstants.IS_DOWNLOADING)) {
                                         Hawk.delete(MConstants.IS_DOWNLOADING);
                                     }
//                                     isDownloading = false;
                                     Hawk.put(MConstants.IS_DOWNLOADING, false);
                                 }

                                 @Override
                                 protected void connected(BaseDownloadTask task, String etag,
                                                          boolean isContinue, int soFarBytes, int totalBytes) {
                                     super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                                 }

                                 @Override
                                 protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                     super.paused(task, soFarBytes, totalBytes);
                                 }

                                 @Override
                                 protected void warn(BaseDownloadTask task) {
                                     super.warn(task);
                                     Hawk.init(NowPlayingActivity.this).build();
                                     if (Hawk.contains(MConstants.IS_DOWNLOADING)) {
                                         Hawk.delete(MConstants.IS_DOWNLOADING);
                                     }
//                                     isDownloading = false;
                                     Hawk.put(MConstants.IS_DOWNLOADING, false);
                                 }

                                 @Override
                                 protected void completed(BaseDownloadTask task) {
                                     super.completed(task);
                                     donut_download_progress.setProgress(0);
                                     donut_download_progress.setVisibility(View.INVISIBLE);
//                                     ib_download.setImageResource(R.drawable.ic_file_download_white_24px);
                                     ib_download.getDrawable().setAlpha(255);
                                     ib_download.setEnabled(false);

                                     sendDownloadInfo();

                                     Hawk.init(NowPlayingActivity.this).build();
                                     if (Hawk.contains(MConstants.DOWNLOAD_MEDIA_ITEM_LIST)) {
                                         ArrayList<MediaItem> downloadMediaItemList = Hawk.get(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);

                                         LinkedList<MediaItem> mediaItems = new LinkedList<>();
                                         mediaItems.addAll(downloadMediaItemList);
                                         /*MediaItem addMediaItem = new MediaItem(songId,
                                                 songName, pathPrefix + ".snsh",
                                                 pathPrefix + ".pto", albumName,
                                                 artistName, pathPrefix + ".txt", true);
                                         mediaItems.add(addMediaItem);*/
                                         MediaItem mI = new MediaItem(songId, songName,
                                                 artistName, albumName,
                                                 pathPrefix + ".snsh", pathPrefix + ".snsh", pathPrefix + ".pto",
                                                 String.valueOf(tv_lyrics.getText()));
                                         mediaItems.add(mI);
                                         Hawk.delete(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);
                                         Hawk.put(MConstants.DOWNLOAD_MEDIA_ITEM_LIST, mediaItems);



                                         /*for (int i = 0; i < mediaItems.size(); i++) {
                                             Log.e("asdf", "completed if song id : " + mediaItems.get(i).getSongId() + " : " + mediaItems.size());
                                         }*/
                                     } else {
                                         LinkedList<MediaItem> mediaItems = new LinkedList<>();
                                         /*MediaItem addMediaItem = new MediaItem(songId,
                                                 songName, pathPrefix + ".snsh",
                                                 pathPrefix + ".pto", albumName,
                                                 artistName, pathPrefix + ".txt", true);
                                         mediaItems.add(addMediaItem);*/
                                         MediaItem mI = new MediaItem(songId, songName,
                                                 artistName, albumName,
                                                 pathPrefix + ".snsh", pathPrefix + ".snsh", pathPrefix + ".pto",
                                                 String.valueOf(tv_lyrics.getText()));
                                         mediaItems.add(mI);
                                         Hawk.put(MConstants.DOWNLOAD_MEDIA_ITEM_LIST, mediaItems);
                                     }

                                     Hawk.init(NowPlayingActivity.this).build();
                                     if (Hawk.contains(MConstants.IS_DOWNLOADING))
                                         Hawk.delete(MConstants.IS_DOWNLOADING);
                                     Hawk.put(MConstants.IS_DOWNLOADING, false);
                                     Hawk.delete(MConstants.CURRENT_DOWNLOAD_SONG_ID);

                                     encryptDownloadedFile();
                                 }
                             }

                );
    }

    private void encryptDownloadedFile() {
        File srcFile = new File(pathPrefix + ".snsh");
        File destFile = new File(pathPrefix + ".snsh.enc");

        try {
            FileEncryptor fileEncryptor = new FileEncryptor(aes);
            fileEncryptor.encrypt(srcFile, destFile);
//            fileEncryptor.decrypt(srcFile, destFile);
            FileUtils.deleteFile(srcFile);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Log.e("asdf", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("asdf", e.getMessage());
        }
    }

    public static void decryptDownloadedFile(String src) {
        File srcFile = new File(src + ".enc");
//        File destFile = new File(src.substring(0, src.lastIndexOf("/")));
        File destFile = new File(src);
        try {
            FileEncryptor fileEncryptor = new FileEncryptor(aes);
            fileEncryptor.decrypt(srcFile, destFile);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Log.e("asdf", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("asdf", e.getMessage());
        }
    }

    private void sendDownloadInfo() {
        RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);

        Call<DownloadModel> callAlbums = request.sentDownloadInfo(Integer.valueOf(cusId), Integer.valueOf(songId));
        callAlbums.enqueue(new retrofit2.Callback<DownloadModel>() {
            @Override
            public void onResponse(Call<DownloadModel> call, Response<DownloadModel> response) {
                Toast.makeText(NowPlayingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DownloadModel> call, Throwable t) {

            }
        });
    }
    /*private BaseDownloadTask createDownloadTaskSong(final String songUrl, String path) {
        return FileDownloader.getImpl().create(songUrl)
                .setPath(path, false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(NowPlayingActivity.this)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                        Log.e("asdf", "pending");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        if (totalBytes == -1) {
                            // chunked transfer encoding data
//                            donut_download_progress.setIndeterminate(true);
                        } else {
                            donut_download_progress.setMax(totalBytes);
                            donut_download_progress.setProgress(soFarBytes);
                            donut_download_progress.setText("");
                        }

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        Log.e("asdf", "error : " + e.getMessage());
                        isDownloading = false;
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        Log.e("asdf", "connected");

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        Log.e("asdf", "paused");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                        Log.e("asdf", "warn");
                        isDownloading = false;
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        Log.e("asdf", "complete download!");
                        donut_download_progress.setProgress(0);
                        donut_download_progress.setVisibility(View.INVISIBLE);
                        ib_download.setImageResource(R.drawable.ic_file_download_white_24px);
                        ib_download.setEnabled(false);

                        Hawk.init(NowPlayingActivity.this).build();
                        if (Hawk.contains(MConstants.DOWNLOAD_MEDIA_ITEM_LIST)) {
                            ArrayList<MediaItem> downloadMediaItemList = Hawk.get(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);

                            LinkedList<MediaItem> mediaItems = new LinkedList<>();
                            mediaItems.addAll(downloadMediaItemList);
                            MediaItem addMediaItem = new MediaItem(songId,
                                    songName, pathPrefix + ".snsh",
                                    pathPrefix + ".pto", albumName,
                                    artistName, pathPrefix + ".txt", true);
                            mediaItems.add(addMediaItem);
                            Hawk.delete(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);
                            Hawk.put(MConstants.DOWNLOAD_MEDIA_ITEM_LIST, mediaItems);
                            for (int i = 0; i < mediaItems.size(); i++) {
                                Log.e("asdf", "completed if song id : " + mediaItems.get(i).getSongId() + " : " + mediaItems.size());
                            }
                        } else {
                            LinkedList<MediaItem> mediaItems = new LinkedList<>();
                            MediaItem addMediaItem = new MediaItem(songId,
                                    songName, pathPrefix + ".snsh",
                                    pathPrefix + ".pto", albumName,
                                    artistName, pathPrefix + ".txt", true);
                            mediaItems.add(addMediaItem);
                            Hawk.put(MConstants.DOWNLOAD_MEDIA_ITEM_LIST, mediaItems);
                            for (int i = 0; i < mediaItems.size(); i++) {
                                Log.e("asdf", "completedelse song id : " + mediaItems.get(i).getSongId() + " : " + mediaItems.size());
                            }
                        }
                        isDownloading = false;
                    }
                });
    }*/

    private void setCustomListener() {
        rv_now_playing.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {

                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }

                Hawk.init(NowPlayingActivity.this).build();
                ArrayList<MediaItem> nowPlayingData = Hawk.get(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);

                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    loadPlayLowQty(playlistManager, nowPlayingData, position);
                } else {
                    LinkedList<MediaItem> mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(nowPlayingData.get(position).getSongId()),
                            nowPlayingData.get(position).getSongName(), nowPlayingData.get(position).getArtistName(),
                            nowPlayingData.get(position).getAlbumName(), nowPlayingData.get(position).getSongHighPath(),
                            nowPlayingData.get(position).getSongLowPath(), nowPlayingData.get(position).getAlbumImage());
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);
                }


                if (Hawk.contains(MConstants.NOW_PLAYING_INDEX))
                    Hawk.delete(MConstants.NOW_PLAYING_INDEX);
                Hawk.put(MConstants.NOW_PLAYING_INDEX, position);
                updateCurrentPlaybackInformation();
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                super.onItemChildClick(adapter, view, position);
                switch (view.getId()) {
                    case R.id.ib_song_detail:
                        /*PopupMenu popup = new PopupMenu(NowPlayingActivity.this, rv_now_playing.getChildAt(position).findViewById(R.id.ib_song_detail));
                        popup.getMenuInflater().inflate(R.menu.activity_queue_song_detail, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_add_to_playlist:
                                        Bundle bundle = new Bundle();
                                        bundle.putInt(MConstants.PASS_FLAG, 0);
                                        bundle.putString(MConstants.PASS_ID, nowPlayingData.get(position).getSongId());
                                        Intent intent = new Intent(NowPlayingActivity.this, PersonalPlaylistNameActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        break;
                                    case R.id.action_clear_queue:
                                        playlistManager.invokeStop();
                                        onBackPressed();
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show();*/

                        break;
                }
            }
        });

        sb_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);

                Call<FavouriteModel> call;


                if (sb_fav.isChecked()) {
                    call = request.setFavouriteTrue(Integer.parseInt(cusId), Integer.parseInt(songId));
                    call.enqueue(new retrofit2.Callback<FavouriteModel>() {
                        @Override
                        public void onResponse(Call<FavouriteModel> call, Response<FavouriteModel> response) {
                            Toast.makeText(NowPlayingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<FavouriteModel> call, Throwable t) {

                        }
                    });
                } else {
                    call = request.setFavouriteFalse(Integer.parseInt(cusId), Integer.parseInt(songId));
                    call.enqueue(new retrofit2.Callback<FavouriteModel>() {
                        @Override
                        public void onResponse(Call<FavouriteModel> call, Response<FavouriteModel> response) {
                            Toast.makeText(NowPlayingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<FavouriteModel> call, Throwable t) {

                        }
                    });
                }
            }
        });
        ib_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donut_download_progress.setVisibility(View.VISIBLE);
//                isDownloading = true;
                if (Hawk.contains(MConstants.IS_DOWNLOADING)) {
                    Hawk.delete(MConstants.IS_DOWNLOADING);
                }
                Hawk.init(NowPlayingActivity.this).build();
                Hawk.put(MConstants.IS_DOWNLOADING, true);
                downloadSong();
            }
        });

    }

    private void lyricScroll() {
        sv_lyrics = (ScrollView) findViewById(R.id.sv_lyrics);
        sv_lyrics.setOnTouchListener(new ScrollView.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(NowPlayingActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    updateLyrics();
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ScrollView touch events.
                v.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        /*sv_lyrics.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(NowPlayingActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });*/
    }

    private void retrieveViews() {
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        iv_now_playing_image = (ImageView) findViewById(R.id.iv_now_playing_image);
        iv_now_playing_image_blur = (ImageView) findViewById(R.id.iv_now_playing_image_blur);

        tv_player_position = (TextView) findViewById(R.id.tv_player_position);
        tv_player_duration = (TextView) findViewById(R.id.tv_player_duration);

//        sb_player = (SeekBar) findViewById(R.id.sb_player);
//        sb_player.setPadding(0, 0, 0, 0);
//        sb_player.setClickable(false);

//        clpb_player = (ContentLoadingProgressBar) findViewById(R.id.clpb_player);
        mpb_player = (MaterialProgressBar) findViewById(R.id.mpb_player);

        ib_previous = (ImageButton) findViewById(R.id.ib_previous);
        ib_play_pause = (ImageButton) findViewById(R.id.ib_play_pause);
        ib_next = (ImageButton) findViewById(R.id.ib_next);
        ib_now_playing_song_detail = (ImageButton) findViewById(R.id.ib_now_playing_song_detail);

        picasso = Picasso.with(getApplicationContext());

        tv_song_name = (TextView) findViewById(R.id.tv_song_name);
        tv_album_name = (TextView) findViewById(R.id.tv_album_name);
        tv_artist_name = (TextView) findViewById(R.id.tv_artist_name);

        tv_lyrics = (TextView) findViewById(R.id.tv_lyrics);
        donut_download_progress = (DonutProgress) findViewById(R.id.donut_download_progress);
        ib_download = (ImageButton) findViewById(R.id.ib_download);
        ib_lyric = (ImageButton) findViewById(R.id.ib_lyric);
        ib_shuffle = (ImageButton) findViewById(R.id.ib_shuffle);
        ib_repeat = (ImageButton) findViewById(R.id.ib_repeat);

    }

    private void updateCurrentPlaybackInformation() {
        PlaylistItemChange<MediaItem> itemChange = playlistManager.getCurrentItemChange();
        if (itemChange != null) {
            onPlaylistItemChanged(itemChange.getCurrentItem(), itemChange.hasNext(), itemChange.hasPrevious());
        }

        PlaylistServiceCore.PlaybackState currentPlaybackState = playlistManager.getCurrentPlaybackState();
        if (currentPlaybackState != PlaylistServiceCore.PlaybackState.STOPPED) {
            onPlaybackStateChanged(currentPlaybackState);
        }

        MediaProgress mediaProgress = playlistManager.getCurrentProgress();
        if (mediaProgress != null) {
            onProgressUpdated(mediaProgress);
        }
    }

    private boolean isDownloadFile(String songId) {
        Hawk.init(this).build();
        if (Hawk.contains(MConstants.DOWNLOAD_MEDIA_ITEM_LIST)) {
            ArrayList<MediaItem> downloadMediaItemList = Hawk.get(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);
            for (int i = 0; i < downloadMediaItemList.size(); i++) {
                if (songId.equalsIgnoreCase(downloadMediaItemList.get(i).getSongId())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPlaylistItemChanged(@Nullable MediaItem currentItem, boolean hasNext, boolean hasPrevious) {


        shouldSetDuration = true;

        //Updates the button states
//        ib_next.setEnabled(hasNext);
//        ib_previous.setEnabled(hasPrevious);

        //Loads the new image
        if (currentItem != null) {

            songId = currentItem.getSongId();

            pathPrefix = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "root" + File.separator + songId + File.separator + songId;
//            lyricPathPrefix = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "root" + File.separator + songId;
            /*lyricUrl = nowPlayingData.get(nowPlayingIndex).getLyric();
            photoUrl = nowPlayingData.get(nowPlayingIndex).getAlbumImage();
            songUrl = nowPlayingData.get(nowPlayingIndex).getMediaUrl();*/
//            lyricUrl = currentItem.getLyric();

            photoUrl = currentItem.getAlbumImage();
            songUrl = currentItem.getMediaUrl();

            songName = currentItem.getSongName();
            artistName = currentItem.getArtist();
            albumName = currentItem.getAlbum();

            Log.e("MediaUrl", "mediaUrl : " + songUrl);

            Hawk.init(this).build();
            if (Hawk.contains(MConstants.CURRENT_DOWNLOAD_SONG_ID)) {
                String currentDownloadSongId = Hawk.get(MConstants.CURRENT_DOWNLOAD_SONG_ID);
                if (songId.equalsIgnoreCase(currentDownloadSongId)) {
                    donut_download_progress.setVisibility(View.INVISIBLE);
                    ib_download.setEnabled(false);
                    if (Hawk.contains(MConstants.DOWNLOAD_MEDIA_ITEM_LIST)) {
                        ArrayList<MediaItem> downloadMediaItemList = Hawk.get(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);
                        for (int i = 0; i < downloadMediaItemList.size(); i++) {
                            if (songId.equalsIgnoreCase(downloadMediaItemList.get(i).getSongId())) {
                                donut_download_progress.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                } else {
                    donut_download_progress.setVisibility(View.INVISIBLE);
                }
            }
            if (isDownloadFile(songId)) {
//                ib_download.setImageResource(R.drawable.ic_file_download_white_24px);
                ib_download.getDrawable().setAlpha(255);
                ib_download.setEnabled(false);
            } else {
                ib_download.getDrawable().setAlpha(128);
//                ib_download.setImageResource(R.drawable.ic_file_download_gray_24px);
                ib_download.setEnabled(true);
            }

            isDownloading = Hawk.get(MConstants.IS_DOWNLOADING, false);
            if (isDownloading) {
                ib_download.setEnabled(false);
            }

            checkFavourite(Integer.parseInt(songId), Integer.parseInt(cusId));

            if (currentItem.getMediaUrl().startsWith("/storage/"))
                isOffline = true;

            if (currentItem.getAlbumImage() != null) {
                if (currentItem.getAlbumImage().startsWith("/storage/")) {
                    picasso.load(new File(currentItem.getAlbumImage())).into(iv_now_playing_image);
//                    readDownloadLyrics();
                    tv_lyrics.setText(currentItem.getLyric());
                    picasso.load(new File(currentItem.getAlbumImage())).into(iv_now_playing_image_blur, new Callback() {
                        @Override
                        public void onSuccess() {
                            blurNowPlayingImage();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                } else {
                    picasso.load(currentItem.getAlbumImage()).into(iv_now_playing_image);
                    /*try {
                        url = new URL(currentItem.getLyric());
                        m2(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }*/

                    loadLyric(Integer.parseInt(songId));

//                  picasso.load("http://139.59.250.169/artist/images/340752ahnge.jpg").into(iv_now_playing_image_blur);
                    picasso.load(currentItem.getAlbumImage()).into(iv_now_playing_image_blur, new Callback() {
                        @Override
                        public void onSuccess() {
                            blurNowPlayingImage();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }

            tv_song_name.setText(currentItem.getSongName());
            tv_album_name.setText(currentItem.getAlbum());
            tv_artist_name.setText(currentItem.getArtist());

            rv_now_playing.setAdapter(nowPlayingAdapter);


            Bundle params = new Bundle();
            params.putString("songId", songId);
            params.putString("songName", songName);
            mFirebaseAnalytics.logEvent("playLog", params);
        }

        return true;
    }

    private void loadLyric(int songId) {
        RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);
        Call<LoadLyric> call = request.getLyric(songId);
        call.enqueue(new retrofit2.Callback<LoadLyric>() {
            @Override
            public void onResponse(Call<LoadLyric> call, Response<LoadLyric> response) {
                tv_lyrics.setText(response.body().lyric);
            }

            @Override
            public void onFailure(Call<LoadLyric> call, Throwable t) {

            }
        });
    }

    private void readDownloadLyrics() {
        /*//Get the text file
        File file = new File(lyricPathPrefix, songId + ".txt");
        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        tv_lyrics.setText(text);*/
    }

    private void checkFavourite(int songId, int customerId) {
        RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);

        Call<FavouriteModel> callCheckFavourite = request.checkFavourite(songId, customerId);
        callCheckFavourite.enqueue(new retrofit2.Callback<FavouriteModel>() {
            @Override
            public void onResponse(Call<FavouriteModel> call, Response<FavouriteModel> response) {

                if (response.body().getLike().equalsIgnoreCase("true")) {
                    sb_fav.setChecked(true);
                } else {
                    sb_fav.setChecked(false);
                }
            }

            @Override
            public void onFailure(Call<FavouriteModel> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onPlaybackStateChanged(@NonNull BasePlaylistService.PlaybackState playbackState) {
        switch (playbackState) {
            case STOPPED:
                Hawk.init(NowPlayingActivity.this).build();
                if (Hawk.contains(MConstants.INVOKE_STOP)) {
                    if (Hawk.get(MConstants.INVOKE_STOP)) {
                        onBackPressed();
                        Hawk.put(MConstants.INVOKE_STOP, false);
                    }
                }
                break;

            case RETRIEVING:
                break;
            case PREPARING:
                break;
            case SEEKING:
                restartLoading();
                break;

            case PLAYING:
                if (FileUtils.isFileExists(songUrl)) {
                    FileUtils.deleteFile(songUrl);
                    Log.e("asdf", "deleted encrypted from now playing");
                }
                doneLoading(true);
                break;

            case PAUSED:
                doneLoading(false);
                break;

            case ERROR:
                Aesthetic.get()
                        .colorPrimary()
                        .take(1)
                        .subscribe(color -> {
                            // Use color (an integer)
                            NetworkHelper.showNetworkErrorDialog(this, color);
                        });

                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onProgressUpdated(@NonNull MediaProgress progress) {
        if (shouldSetDuration && progress.getDuration() > 0) {
            shouldSetDuration = false;
            setDuration(progress.getDuration());
        }

        if (!userInteracting) {
//            sb_player.setSecondaryProgress((int) (progress.getDuration() * progress.getBufferPercentFloat()));
//            sb_player.setProgress((int) progress.getPosition());
//            clpb_player.setSecondaryProgress((int) (progress.getDuration() * progress.getBufferPercentFloat()));
//            clpb_player.setProgress((int) progress.getPosition());
            mpb_player.setSecondaryProgress((int) (progress.getDuration() * progress.getBufferPercentFloat()));
            mpb_player.setProgress((int) progress.getPosition());
            tv_player_position.setText(formatMs(progress.getPosition()));
        }

        return true;
    }

    public void restartLoading() {
        ib_play_pause.setVisibility(View.INVISIBLE);
        ib_previous.setVisibility(View.INVISIBLE);
        ib_next.setVisibility(View.INVISIBLE);

        pb_loading.setVisibility(View.VISIBLE);
    }

    private void doneLoading(boolean isPlaying) {
        loadCompleted();
        updatePlayPauseImage(isPlaying);
    }

    private void setDuration(long duration) {
//        sb_player.setMax((int) duration);
//        clpb_player.setMax((int) duration);
        mpb_player.setMax((int) duration);
        tv_player_duration.setText(formatMs(duration));
    }

    public void loadCompleted() {
        ib_play_pause.setVisibility(View.VISIBLE);
        ib_previous.setVisibility(View.VISIBLE);
        ib_next.setVisibility(View.VISIBLE);

        pb_loading.setVisibility(View.INVISIBLE);

    }

    private void updatePlayPauseImage(boolean isPlaying) {
        int resId = isPlaying ? R.drawable.playlistcore_ic_pause_white : R.drawable.playlistcore_ic_play_arrow_white;
        ib_play_pause.setImageResource(resId);
    }

    private void setupListeners() {
//        sb_player.setOnSeekBarChangeListener(new SeekBarChanged());

        ib_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (playlistManager.getCurrentPosition() == 0) {
                    playlistManager.setCurrentPosition(0);
                    playlistManager.play(0, false);
                } else
                    playlistManager.invokePrevious();
                rv_now_playing.setAdapter(nowPlayingAdapter);*/
                playPreviousPolicy();
            }
        });

        ib_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistManager.invokePausePlay();
            }
        });

        ib_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (playlistManager.getCurrentPosition() == playlistManager.getItemCount() - 1) {
                    playlistManager.setCurrentPosition(0);
                    playlistManager.play(0, false);
                } else
                    playlistManager.invokeNext();
                rv_now_playing.setAdapter(nowPlayingAdapter);*/

                playNextPolicy();
            }
        });

        iv_now_playing_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLyrics();
            }
        });

        ib_lyric.getDrawable().setAlpha(128);
        ib_lyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLyrics();
            }
        });


        if (play_shuffle)
            ib_shuffle.getDrawable().setAlpha(255);
        else
            ib_shuffle.getDrawable().setAlpha(128);

        ib_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play_shuffle) {
                    play_shuffle = false;
                } else
                    play_shuffle = true;
                Hawk.init(NowPlayingActivity.this).build();
                if (play_shuffle) {
//                    shuffleMenuItem.getIcon().setAlpha(255);
                    ib_shuffle.getDrawable().setAlpha(255);
//                    shuffleMenuItem.setTitle("Disable shuffle");
                    Hawk.put(MConstants.PLAY_SHUFFLE, true);
                } else {
                    ib_shuffle.getDrawable().setAlpha(128);
//                    shuffleMenuItem.getIcon().setAlpha(128);
//                    shuffleMenuItem.setTitle("Enable shuffle");
                    Hawk.put(MConstants.PLAY_SHUFFLE, false);
                }
            }
        });

        if (play_repeat)
            ib_repeat.getDrawable().setAlpha(255);
        else
            ib_repeat.getDrawable().setAlpha(128);

        ib_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play_repeat) {
                    play_repeat = false;
                } else
                    play_repeat = true;

                if (play_repeat) {
//                    repeatMenuItem.getIcon().setAlpha(255);
                    ib_repeat.getDrawable().setAlpha(255);
                    Hawk.put(MConstants.PLAY_REPEAT, true);
                } else {
//                    repeatMenuItem.getIcon().setAlpha(128);
                    ib_repeat.getDrawable().setAlpha(128);
                    Hawk.put(MConstants.PLAY_REPEAT, false);
                }
            }
        });

        ib_now_playing_song_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopup_NowPlaying();

                BottomSheetDialogFragment bottomSheetDialogFragment =
                        new MyBottomSheetDialogFragment(NowPlayingActivity.this, isOffline, cusId, songId,
                                songName, artistName, albumName, photoUrl);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                /*new BottomSheet.Builder(NowPlayingActivity.this).sheet(R.menu.activity_now_playing_song_detail).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.action_add_to_playlist:
                                if (isOffline) {
                                    Toast.makeText(NowPlayingActivity.this, "downloaded song can't add!", Toast.LENGTH_SHORT).show();
                                } else {
                                    final RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                                    Call<CustomerPlayListName> call = requestInterface2.getCustomerPlaylistName(Integer.parseInt(cusId));
                                    call.enqueue(new retrofit2.Callback<CustomerPlayListName>() {
                                        @Override
                                        public void onResponse(Call<CustomerPlayListName> call, Response<CustomerPlayListName> response) {
                                            if (response.isSuccessful()) {

                                                final List<String> itemName = new ArrayList<>();
                                                final int[] itemId = new int[response.body().data.size() + 1];
                                                itemName.add("Create New Playlist");
                                                itemId[0] = -1;
                                                for (int i = 0; i < response.body().data.size(); i++) {
                                                    itemName.add(response.body().data.get(i).playlist_name);
                                                    itemId[i + 1] = response.body().data.get(i).playlistid;
                                                }
//                                                Log.e("asdf", itemId.length + " : " + itemName.size());
//                                                for (int i = 0; i < itemName.size(); i++) {
//                                                    Log.e("asdf", itemId[i] + " : " + itemName.get(i));
//                                                }
//                                                        .titleColorRes(R.color.text1)
//                                                        .itemsColorRes(R.color.text2)
//                                                        .backgroundColorRes(R.color.colorPrimary)
                                                new MaterialDialog.Builder(NowPlayingActivity.this)
                                                        .title("Add to Playlist")
                                                        .itemsIds(itemId)
                                                        .items(itemName)
                                                        .itemsCallback(new MaterialDialog.ListCallback() {
                                                            @Override
                                                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                                Toast.makeText(PersonalPlaylistNameActivity.this, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();
//                                                                Log.e("asdf", "aaa : " + view.getId() + " : " + text + " : " + which);
                                                                if (which == 0) {//create new playlist name and add current song to new list
                                                                    createNewPlaylist(requestInterface2);
                                                                } else {//add current song to the selected playlist //addToPlaylist
                                                                    Log.e("asdf", "bbb : " + itemId[which]);
                                                                    Call<CreatePlaylistModel> call = requestInterface2.addToPlaylist(itemId[which], Integer.parseInt(songId));
                                                                    call.enqueue(new retrofit2.Callback<CreatePlaylistModel>() {
                                                                        @Override
                                                                        public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                                                            Toast.makeText(NowPlayingActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                                                            Log.e("error", t.getMessage());
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        })
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CustomerPlayListName> call, Throwable t) {
                                            Log.e("error", "aaa" + t.getMessage());
                                            Toast.makeText(NowPlayingActivity.this, "Can't Add Playlist!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                break;

                            case R.id.action_share:
                                ShareDialog shareDialog = new ShareDialog(NowPlayingActivity.this);
                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse("http://www.blueoceanmgt.com/index.php?p=companies&catagory=blueplanet&tbl_com=tbl_category"))
                                        .setContentTitle(songName)
                                        .setContentDescription(artistName + "." + albumName)
                                        .setImageUrl(Uri.parse(photoUrl))
                                        .build();
                                shareDialog.show(content);
                                break;
                            case R.id.action_clear_queue:
                                playlistManager.invokeStop();
                                onBackPressed();
                                break;
                            case R.id.action_cancel:
                                break;
                        }
                    }
                }).show();*/
            }
        });
    }

    private void createNewPlaylist(final RequestInterface2 requestInterface2) {
        createNewPlaylist = new MaterialDialog.Builder(NowPlayingActivity.this)
                .title("Create New Playlist")
                .customView(R.layout.dialog_customview, true)
                .positiveText("Create")
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                                                editPlaylistName(playlistId, et_name.getText().toString());
                        Call<CreatePlaylistNameModel> call = requestInterface2.createPlaylistName((et_name.getText()).toString(), Integer.parseInt(cusId));
                        call.enqueue(new retrofit2.Callback<CreatePlaylistNameModel>() {
                            @Override
                            public void onResponse(Call<CreatePlaylistNameModel> call, Response<CreatePlaylistNameModel> response) {
                                String playlistId = response.body().playlistid;

                                Call<CreatePlaylistModel> c = requestInterface2.addToPlaylist(Integer.parseInt(playlistId), Integer.parseInt(songId));
                                c.enqueue(new retrofit2.Callback<CreatePlaylistModel>() {
                                    @Override
                                    public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                        Toast.makeText(NowPlayingActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                        Log.e("error", t.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<CreatePlaylistNameModel> call, Throwable t) {
                                Log.e("error", t.getMessage());
                            }
                        });
                    }
                })
                .show();
        positiveAction = createNewPlaylist.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        et_name = (EditText) createNewPlaylist.getCustomView().findViewById(R.id.et_name);

        if (et_name.requestFocus()) {
            createNewPlaylist.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void playNextPolicy() {
        Hawk.init(App.getApplication()).build();

        final PlaylistManager playlistManager = App.getPlaylistManager();

        if (SendDurationToServer.isPlayingSong()) {
            SendDurationToServer.sendDuration();
        }

        /*Log.e("asdf", "current seconds : " + formatMs(playlistManager.getCurrentProgress().getPosition()));
        Log.e("asdf", "song id : " + playlistManager.getCurrentItem().getSongId());

        if (Hawk.contains(MConstants.CUS_ID)) {
            String cusId = Hawk.get(MConstants.CUS_ID);
            String songID = playlistManager.getCurrentItem().getSongId();
            String duration = formatMsToServer(playlistManager.getCurrentProgress().getPosition());
            RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
            Call<PlayLog> call = requestInterface2.sendDurationToServer(songID, cusId, duration);
            call.enqueue(new retrofit2.Callback<PlayLog>() {
                @Override
                public void onResponse(Call<PlayLog> call, Response<PlayLog> response) {

                }

                @Override
                public void onFailure(Call<PlayLog> call, Throwable t) {

                }
            });
        }*/


        if (Hawk.get(MConstants.PLAY_SHUFFLE)) {

            final ArrayList<MediaItem> nowPlayingData = Hawk.get(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
            if (nowPlayingData.size() == 0)
                return;
            int nowPlayingIndex = Hawk.get(MConstants.NOW_PLAYING_INDEX);

            final int shuffleIndex = MUtility.getShuffleNumber(nowPlayingData.size(), nowPlayingIndex);

            if (!nowPlayingData.get(shuffleIndex).getMediaUrl().startsWith("http"))
                decryptDownloadedFile(nowPlayingData.get(shuffleIndex).getMediaUrl());


            Hawk.init(App.getApplication()).build();
            boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
            if (isLowQuality) {
                loadPlayLowQty(playlistManager, nowPlayingData, shuffleIndex);
            } else {

                LinkedList<MediaItem> mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(nowPlayingData.get(shuffleIndex).getSongId()),
                        nowPlayingData.get(shuffleIndex).getSongName(), nowPlayingData.get(shuffleIndex).getArtistName(),
                        nowPlayingData.get(shuffleIndex).getAlbumName(), nowPlayingData.get(shuffleIndex).getSongHighPath(),
                        nowPlayingData.get(shuffleIndex).getSongLowPath(), nowPlayingData.get(shuffleIndex).getAlbumImage());

                mediaItems.add(mediaItem);
                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);
            }
            if (Hawk.contains(MConstants.NOW_PLAYING_INDEX))
                Hawk.delete(MConstants.NOW_PLAYING_INDEX);
            Hawk.put(MConstants.NOW_PLAYING_INDEX, shuffleIndex);
        } else {
            final ArrayList<MediaItem> nowPlayingData = Hawk.get(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
            if (nowPlayingData.size() == 0)
                return;
            int nowPlayingIndex = Hawk.get(MConstants.NOW_PLAYING_INDEX);
            int nextPlayingIndex = 0;

            if (nowPlayingIndex == nowPlayingData.size() - 1) {
                boolean isRepeat = Hawk.get(MConstants.PLAY_REPEAT);
                if (!isRepeat) {
                    playlistManager.invokeStop();
                    if (Hawk.contains(MConstants.INVOKE_STOP))
                        Hawk.delete(MConstants.INVOKE_STOP);
                    Hawk.put(MConstants.INVOKE_STOP, true);

//                    SmartTab.hideMiniPlayerInfo();

                    return;
//                    nextPlayingIndex = 0;//fix for only one song on the list
                } else {
                    nextPlayingIndex = 0;
                }
            } else {
                nextPlayingIndex = nowPlayingIndex + 1;
            }

            if (!nowPlayingData.get(nextPlayingIndex).getMediaUrl().startsWith("http"))
                decryptDownloadedFile(nowPlayingData.get(nextPlayingIndex).getMediaUrl());

            Hawk.init(App.getApplication()).build();
            boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
            if (isLowQuality) {
                final int finalNextPlayingIndex = nextPlayingIndex;
                loadPlayLowQty(playlistManager, nowPlayingData, finalNextPlayingIndex);
            } else {
                LinkedList<MediaItem> mediaItems = new LinkedList<>();
//            MediaItem mediaItem = new MediaItem(String.valueOf(nowPlayingData.get(nextPlayingIndex).getSongId()), nowPlayingData.get(nextPlayingIndex).getSongName(), nowPlayingData.get(nextPlayingIndex).getMediaUrl(), nowPlayingData.get(nextPlayingIndex).getAlbumImage(), nowPlayingData.get(nextPlayingIndex).getAlbum(), nowPlayingData.get(nextPlayingIndex).getArtist(), nowPlayingData.get(nextPlayingIndex).getLyric(), true);
                MediaItem mediaItem = new MediaItem(String.valueOf(nowPlayingData.get(nextPlayingIndex).getSongId()),
                        nowPlayingData.get(nextPlayingIndex).getSongName(), nowPlayingData.get(nextPlayingIndex).getArtistName(),
                        nowPlayingData.get(nextPlayingIndex).getAlbumName(), nowPlayingData.get(nextPlayingIndex).getSongHighPath(),
                        nowPlayingData.get(nextPlayingIndex).getSongLowPath(), nowPlayingData.get(nextPlayingIndex).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);

            }


            if (Hawk.contains(MConstants.NOW_PLAYING_INDEX))
                Hawk.delete(MConstants.NOW_PLAYING_INDEX);
            Hawk.put(MConstants.NOW_PLAYING_INDEX, nextPlayingIndex);

        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                PlaylistManager playlistManager = App.getPlaylistManager();
                String mediaUrl = playlistManager.getCurrentItem().getMediaUrl();
                if (FileUtils.isFileExists(mediaUrl)) {
                    FileUtils.deleteFile(mediaUrl);
                    Log.e("asdf", "deleted encrypted file from next policy");
                }
            }
        }, 5000);
    }

    private void playPreviousPolicy() {
        Hawk.init(App.getApplication()).build();
        final PlaylistManager playlistManager = App.getPlaylistManager();


        if (SendDurationToServer.isPlayingSong()) {
            SendDurationToServer.sendDuration();
        }

        /*Log.e("asdf", "current seconds : " + formatMs(playlistManager.getCurrentProgress().getPosition()));
        Log.e("asdf", "song id : " + playlistManager.getCurrentItem().getSongId());

        if (Hawk.contains(MConstants.CUS_ID)) {
            String cusId = Hawk.get(MConstants.CUS_ID);
            String songID = playlistManager.getCurrentItem().getSongId();
            String duration = formatMsToServer(playlistManager.getCurrentProgress().getPosition());
            RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
            Call<PlayLog> call = requestInterface2.sendDurationToServer(songID, cusId, duration);
            call.enqueue(new retrofit2.Callback<PlayLog>() {
                @Override
                public void onResponse(Call<PlayLog> call, Response<PlayLog> response) {

                }

                @Override
                public void onFailure(Call<PlayLog> call, Throwable t) {

                }
            });
        }*/

        if (Hawk.get(MConstants.PLAY_SHUFFLE)) {

            final ArrayList<MediaItem> nowPlayingData = Hawk.get(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
            if (nowPlayingData.size() == 0)
                return;
            int nowPlayingIndex = Hawk.get(MConstants.NOW_PLAYING_INDEX);

            final int shuffleIndex = MUtility.getShuffleNumber(nowPlayingData.size(), nowPlayingIndex);

            if (!nowPlayingData.get(shuffleIndex).getMediaUrl().startsWith("http"))
                decryptDownloadedFile(nowPlayingData.get(shuffleIndex).getMediaUrl());

            Hawk.init(App.getApplication()).build();
            boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
            if (isLowQuality) {
                loadPlayLowQty(playlistManager, nowPlayingData, shuffleIndex);
            } else {
                LinkedList<MediaItem> mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(nowPlayingData.get(shuffleIndex).getSongId()),
                        nowPlayingData.get(shuffleIndex).getSongName(), nowPlayingData.get(shuffleIndex).getArtistName(),
                        nowPlayingData.get(shuffleIndex).getAlbumName(), nowPlayingData.get(shuffleIndex).getSongHighPath(),
                        nowPlayingData.get(shuffleIndex).getSongLowPath(), nowPlayingData.get(shuffleIndex).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);
            }


            if (Hawk.contains(MConstants.NOW_PLAYING_INDEX))
                Hawk.delete(MConstants.NOW_PLAYING_INDEX);
            Hawk.put(MConstants.NOW_PLAYING_INDEX, shuffleIndex);
        } else {
            final ArrayList<MediaItem> nowPlayingData = Hawk.get(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
            if (nowPlayingData.size() == 0)
                return;
            int nowPlayingIndex = Hawk.get(MConstants.NOW_PLAYING_INDEX);
            int nextPlayingIndex = 0;

            if (nowPlayingIndex == 0) {
                /*boolean isRepeat = Hawk.get(MConstants.PLAY_REPEAT);
                if (!isRepeat) {
                    playlistManager.invokeStop();
                    if (Hawk.contains(MConstants.INVOKE_STOP))
                        Hawk.delete(MConstants.INVOKE_STOP);
                    Hawk.put(MConstants.INVOKE_STOP, true);
                    return;
                } else {*/
                nextPlayingIndex = 0;
                /*}*/
            } else {
                nextPlayingIndex = nowPlayingIndex - 1;
            }

            if (!nowPlayingData.get(nextPlayingIndex).getMediaUrl().startsWith("http"))
                decryptDownloadedFile(nowPlayingData.get(nextPlayingIndex).getMediaUrl());

            Hawk.init(App.getApplication()).build();
            boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
            if (isLowQuality) {
                final int finalNextPlayingIndex = nextPlayingIndex;
                loadPlayLowQty(playlistManager, nowPlayingData, finalNextPlayingIndex);
            } else {
                LinkedList<MediaItem> mediaItems = new LinkedList<>();
//            MediaItem mediaItem = new MediaItem(String.valueOf(nowPlayingData.get(nextPlayingIndex).getSongId()), nowPlayingData.get(nextPlayingIndex).getSongName(), nowPlayingData.get(nextPlayingIndex).getMediaUrl(), nowPlayingData.get(nextPlayingIndex).getAlbumImage(), nowPlayingData.get(nextPlayingIndex).getAlbum(), nowPlayingData.get(nextPlayingIndex).getArtist(), nowPlayingData.get(nextPlayingIndex).getLyric(), true);
                MediaItem mediaItem = new MediaItem(String.valueOf(nowPlayingData.get(nextPlayingIndex).getSongId()),
                        nowPlayingData.get(nextPlayingIndex).getSongName(), nowPlayingData.get(nextPlayingIndex).getArtistName(),
                        nowPlayingData.get(nextPlayingIndex).getAlbumName(), nowPlayingData.get(nextPlayingIndex).getSongHighPath(),
                        nowPlayingData.get(nextPlayingIndex).getSongLowPath(), nowPlayingData.get(nextPlayingIndex).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);
            }


            if (Hawk.contains(MConstants.NOW_PLAYING_INDEX))
                Hawk.delete(MConstants.NOW_PLAYING_INDEX);
            Hawk.put(MConstants.NOW_PLAYING_INDEX, nextPlayingIndex);

        }
    }

    /*private void showPopup_NowPlaying() {
        PopupMenu popup = new PopupMenu(NowPlayingActivity.this, ib_now_playing_song_detail);
        popup.getMenuInflater().inflate(R.menu.activity_now_playing_song_detail, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_to_playlist:
                        Bundle bundle = new Bundle();
                        bundle.putInt(MConstants.PASS_FLAG, 0);
                        bundle.putString(MConstants.PASS_ID, songId);
                        Intent intent = new Intent(NowPlayingActivity.this, PersonalPlaylistNameActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.action_clear_queue:
                        playlistManager.invokeStop();
                        onBackPressed();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }*/

    private void showPopup_NowPlaying() {
        /*PopupMenu popup = new PopupMenu(NowPlayingActivity.this, ib_now_playing_song_detail);
        popup.getMenuInflater().inflate(R.menu.activity_now_playing_song_detail, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_to_playlist:
                        Bundle bundle = new Bundle();
                        bundle.putInt(MConstants.PASS_FLAG, 0);
                        bundle.putString(MConstants.PASS_ID, songId);
                        Intent intent = new Intent(NowPlayingActivity.this, PersonalPlaylistNameActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.action_clear_queue:
                        playlistManager.invokeStop();
                        onBackPressed();
                        break;
                }
                return true;
            }
        });
        popup.show();*/
    }

    private class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        private int seekPosition = -1;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            seekPosition = progress;
            tv_player_position.setText(formatMs(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            userInteracting = true;

            seekPosition = seekBar.getProgress();
            playlistManager.invokeSeekStarted();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            userInteracting = false;

            //noinspection Range - seekPosition won't be less than 0
            playlistManager.invokeSeekEnded(seekPosition);
            seekPosition = -1;
        }
    }

    public static String formatMs(long milliseconds) {
        if (milliseconds < 0) {
            return "--:--";
        }

        long seconds = (milliseconds % DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
        long minutes = (milliseconds % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS;
        long hours = (milliseconds % DateUtils.DAY_IN_MILLIS) / DateUtils.HOUR_IN_MILLIS;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }

        return formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    public static String formatMsToServer(long milliseconds) {
        if (milliseconds < 0) {
            return "00:00";
        }

        long seconds = (milliseconds % DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
        long minutes = (milliseconds % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS;
        long hours = (milliseconds % DateUtils.DAY_IN_MILLIS) / DateUtils.HOUR_IN_MILLIS;

        formatBuilder.setLength(0);
        /*if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }*/

        return formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    @Override
    protected void onPause() {
        Aesthetic.pause(this);
        super.onPause();
        playlistManager.unRegisterPlaylistListener(this);
        playlistManager.unRegisterProgressListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Aesthetic.resume(this);
        playlistManager = App.getPlaylistManager();
        playlistManager.registerPlaylistListener(this);
        playlistManager.registerProgressListener(this);

        //Makes sure to retrieve the current playback information
        updateCurrentPlaybackInformation();

        Hawk.init(this).build();
        nowPlayingData = Hawk.get(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
        rv_now_playing.setLayoutManager(new LinearLayoutManager(this));
        nowPlayingAdapter = new NowPlayingAdapter(nowPlayingData, this);
        nowPlayingAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        rv_now_playing.setAdapter(nowPlayingAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_now_playing, menu);
//
//        shuffleMenuItem = menu.findItem(R.id.action_shuffle);
//        repeatMenuItem = menu.findItem(R.id.action_repeat);
//        actionLyrics = menu.findItem(R.id.action_lyrics);
//
//        if (play_shuffle)
//            shuffleMenuItem.getIcon().setAlpha(255);
//        else
//            shuffleMenuItem.getIcon().setAlpha(128);
//
//        if (play_repeat)
//            repeatMenuItem.getIcon().setAlpha(255);
//        else
//            repeatMenuItem.getIcon().setAlpha(128);
//
//        actionLyrics.getIcon().setAlpha(128);

        return true;
    }

    private void updateLyrics() {
        if (flag_lyrics) {
            flag_lyrics = false;
//            actionLyrics.getIcon().setAlpha(128);
            ib_lyric.getDrawable().setAlpha(128);
            sv_lyrics.setVisibility(View.INVISIBLE);
            iv_now_playing_image.setVisibility(View.VISIBLE);

            /*AlphaAnimation alpha = new AlphaAnimation(1F, 1F);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            iv_now_playing_image.startAnimation(alpha);*/
        } else {
            flag_lyrics = true;
            ib_lyric.getDrawable().setAlpha(255);
//            actionLyrics.getIcon().setAlpha(255);
            sv_lyrics.setVisibility(View.VISIBLE);
            sv_lyrics.smoothScrollTo(0, 0);
            iv_now_playing_image.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.down_to_bottom);
    }

    private void m2(final URL songUrl) {
        new Thread() {
            @Override
            public void run() {
                try {
                    // Create a URL for the desired page
//                    URL url = new URL("http://139.59.250.169/song/lyrics/374377deargod.txt");
//                    URL url = new URL("http://139.59.250.169/song/lyrics/959844achit.txt");

                    // Read all the text returned by the server
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(songUrl.openStream()));
                    String str;
                    stringBuilder = new StringBuilder();
                    while ((str = bufferedReader.readLine()) != null) {
                        // str is one line of text; readLine() strips the newline character(s)
                        stringBuilder.append(str + "\n");
                    }
                    bufferedReader.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_lyrics.setText(stringBuilder);
                            sv_lyrics.smoothScrollTo(0, 0);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void blurNowPlayingImage() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Blurry.with(NowPlayingActivity.this)
                        .radius(45)
                        .sampling(4)
                        .async()
                        .capture(iv_now_playing_image_blur)
                        .into(iv_now_playing_image_blur);
            }
        }, 500);
    }

    /*private void setPalette() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                int primary = getResources().getColor(R.color.colorPrimary);
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });}*/
}

