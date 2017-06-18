package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devbrackets.android.playlistcore.event.MediaProgress;
import com.devbrackets.android.playlistcore.event.PlaylistItemChange;
import com.devbrackets.android.playlistcore.listener.PlaylistListener;
import com.devbrackets.android.playlistcore.listener.ProgressListener;
import com.devbrackets.android.playlistcore.service.BasePlaylistService;
import com.devbrackets.android.playlistcore.service.PlaylistServiceCore;
import com.squareup.picasso.Picasso;

import java.io.File;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.act.NowPlayingActivity;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.utly.AndroidUtilCode.FileUtils;

/**
 * Created by androiddeveloper on 24-Jan-17.
 */

public class MiniPlayerInfoFragment extends Fragment implements PlaylistListener<MediaItem>, ProgressListener {

    private ImageView artworkView;
    private ImageButton playPauseButton;
    //    private ImageButton nextButton;
    private Picasso picasso;
    private TextView tv_song_name;

    private PlaylistManager playlistManager;

    private ProgressBar progressBar;
    private boolean shouldSetDuration;
    private boolean userInteracting;

    //    private FirebaseAnalytics mFirebaseAnalytics;
    private String songUrl;

    View view;


    public MiniPlayerInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mini_player_info, container, false);
        playlistManager = App.getPlaylistManager();
        playlistManager.registerPlaylistListener(this);
        playlistManager.registerProgressListener(this);
        viewByID(view);
        setupListeners();
        updateCurrentPlaybackInformation();

        view.setVisibility(View.INVISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(NowPlayingActivity.newIntent(getActivity()));
            }
        });

        return view;
    }

    private void viewByID(View view) {
        artworkView = (ImageView) view.findViewById(R.id.iv_audio_player_image);
        playPauseButton = (ImageButton) view.findViewById(R.id.ib_play_pause);
//        nextButton = (ImageButton) view.findViewById(R.id.ib_next);
        picasso = Picasso.with(getActivity());
        tv_song_name = (TextView) view.findViewById(R.id.tv_song_name);
        tv_song_name.setText("");
        progressBar = (ProgressBar) view.findViewById(R.id.sb_player);

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

    @Override
    public boolean onPlaylistItemChanged(@Nullable MediaItem currentItem, boolean hasNext, boolean hasPrevious) {

//        rl_footer.setVisibility(View.VISIBLE);
        shouldSetDuration = true;

        //Updates the button states
//        nextButton.setEnabled(hasNext);

        //Loads the new image
        if (currentItem != null) {

            songUrl = currentItem.getMediaUrl();

            if (currentItem.getAlbumImage() != null) {
                if (currentItem.getAlbumImage().startsWith("/storage/")) {
                    picasso.load(new File(currentItem.getAlbumImage())).into(artworkView);
                } else {
                    picasso.load(currentItem.getAlbumImage())
                            .placeholder(R.drawable.placeholder_song)
                            .into(artworkView);
                }
            }
            tv_song_name.setText(currentItem.getSongName());

//            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
//            Bundle bundle = new Bundle();
//            bundle.putInt("SONG_NAME", R.id.tv_song_name);
//            mFirebaseAnalytics.logEvent(currentItem.getSongName(), bundle);
        }

        return true;
    }

    @Override
    public boolean onPlaybackStateChanged(@NonNull BasePlaylistService.PlaybackState playbackState) {
        switch (playbackState) {
            case STOPPED:
//                finish();
                Log.e("asdf", "stop visible");
                view.setVisibility(View.INVISIBLE);
                break;

            case RETRIEVING:
                break;
            case PREPARING:
                break;
            case SEEKING:
                restartLoading();
                break;

            case PLAYING:
                view.setVisibility(View.VISIBLE);
                if (FileUtils.isFileExists(songUrl)) {
                    FileUtils.deleteFile(songUrl);
                    Log.e("asdf", "deleted encrypted file from miniplayer");
                }
                doneLoading(true);
                break;

            case PAUSED:
                doneLoading(false);
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

//        if (!userInteracting) {
//            progressBar.setProgress((int) progress.getPosition());
//        }
        progressBar.setProgress((int) progress.getPosition());

        return true;
    }

    public void restartLoading() {
        playPauseButton.setVisibility(View.INVISIBLE);
//        nextButton.setVisibility(View.INVISIBLE);
    }

    private void doneLoading(boolean isPlaying) {
        loadCompleted();
        updatePlayPauseImage(isPlaying);
    }

    private void setDuration(long duration) {
        progressBar.setMax((int) duration);
    }

    public void loadCompleted() {
        playPauseButton.setVisibility(View.VISIBLE);
//        nextButton.setVisibility(View.VISIBLE);
    }

    private void updatePlayPauseImage(boolean isPlaying) {
        int resId = isPlaying ? R.drawable.playlistcore_ic_pause_white_mini : R.drawable.playlistcore_ic_play_arrow_white_mini;
        playPauseButton.setImageResource(resId);
    }

    private void setupListeners() {
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistManager.invokePausePlay();
            }
        });

        /*nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*if (playlistManager.getCurrentPosition() == playlistManager.getItemCount() - 1) {
                    playlistManager.setCurrentPosition(0);
                    playlistManager.play(0, false);
                } else
                    playlistManager.invokeNext();*//*
                NowPlayingActivity.playNextPolicy();
            }
        });*/
    }

    @Override
    public void onPause() {
        super.onPause();
        playlistManager = App.getPlaylistManager();
        playlistManager.unRegisterPlaylistListener(this);
        playlistManager.unRegisterProgressListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        playlistManager = App.getPlaylistManager();
        playlistManager.registerPlaylistListener(this);
        playlistManager.registerProgressListener(this);

        updateCurrentPlaybackInformation();
    }

    @Override
    public void onStart() {
        super.onStart();
        playlistManager = App.getPlaylistManager();
        playlistManager.registerPlaylistListener(this);
        playlistManager.registerProgressListener(this);
    }
}
