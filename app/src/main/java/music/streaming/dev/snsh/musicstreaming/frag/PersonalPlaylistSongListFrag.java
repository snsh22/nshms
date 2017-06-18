package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.PersonalPlaylistSongListAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistModel;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistSong;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import music.streaming.dev.snsh.musicstreaming.utly.SendDurationToServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class PersonalPlaylistSongListFrag extends Fragment {

    private RecyclerView rv_personal_playlist_song_list;
    private PersonalPlaylistSongListAdapter personalPlaylistSongListAdapter;

    private FragmentManager fragmentManager;

    private RequestInterface2 requestInterface2;

    //    private String cusId, songId;
    private List<PersonalPlaylistSong.Data> playlistSongList;
    private String pass_Id, pass_Name;
    private LinkedList<MediaItem> mediaItems;
    private PlaylistManager playlistManager;

    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_playlist_song_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = savedInstanceState;

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.PERSONAL_PLAYLIST_SONG_LIST)) {
            Hawk.delete(MConstants.PERSONAL_PLAYLIST_SONG_LIST);
        }
        Hawk.put(MConstants.PERSONAL_PLAYLIST_SONG_LIST, true);

        /*if (Hawk.contains(MConstants.CUS_ID)) {
            cusId = String.valueOf(Hawk.get(MConstants.CUS_ID));
        }*/

        Bundle arguments = getArguments();
        if (arguments != null) {
            pass_Id = arguments.getString(MConstants.PASS_ID);
            pass_Name = arguments.getString(MConstants.PASS_NAME);

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(pass_Name);
            Hawk.init(getActivity()).build();
            if (Hawk.contains(MConstants.TITLE2)) {
                Hawk.delete(MConstants.TITLE2);
            }
            Hawk.put(MConstants.TITLE2, pass_Name);
        }

        requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);

        rv_personal_playlist_song_list = (RecyclerView) view.findViewById(R.id.rv_personal_favourite_list);
        playlistSongList = new ArrayList<>();
        personalPlaylistSongListAdapter = new PersonalPlaylistSongListAdapter(playlistSongList, getActivity());
        rv_personal_playlist_song_list.setHasFixedSize(true);
        rv_personal_playlist_song_list.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv_personal_playlist_song_list.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));

        loadPlaylistSong();

        rv_personal_playlist_song_list.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.PlaylistSongOptionArray)
                        .itemsColorRes(R.color.text1)
                        .backgroundColorRes(R.color.colorPrimary)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                switch (position) {
                                    case 0:
                                        removeSongFromPlaylist(position);
                                        break;
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);

                new MaterialDialog.Builder(getActivity())
                        .items(R.array.PlaylistSongOptionArray)
                        .itemsColorRes(R.color.text1)
                        .backgroundColorRes(R.color.colorPrimary)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                switch (position) {
                                    case 0:
                                        removeSongFromPlaylist(position);
                                        break;
                                }
                            }
                        })
                        .show();

            }

            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, final int position) {
                if (playlistSongList.size() > 0) {
                    if (SendDurationToServer.isPlayingSong()) {
                        SendDurationToServer.sendDuration();
                    }
                    playlistManager = App.getPlaylistManager();

                    Hawk.init(App.getApplication()).build();
                    boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                    if (isLowQuality) {
                        RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                        Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(playlistSongList.get(position).songid));
                        call.enqueue(new Callback<LowQualityDTO>() {
                            @Override
                            public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                                mediaItems = new LinkedList<>();
                                MediaItem mediaItem = new MediaItem(String.valueOf(playlistSongList.get(position).songid),
                                        playlistSongList.get(position).song_name, playlistSongList.get(position).artist_name,
                                        playlistSongList.get(position).album_name, DOMAIN_TEST + playlistSongList.get(position).song_high_path,
                                        DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + playlistSongList.get(position).album_image);
                                mediaItems.add(mediaItem);

                                playlistManager.setParameters(mediaItems, 0);
                                playlistManager.setId(20);
                                playlistManager.play(0, false);

                                mediaItems = new LinkedList<>();
                                for (int i = 0; i < playlistSongList.size(); i++) {
                                    MediaItem mI = new MediaItem(String.valueOf(playlistSongList.get(i).songid),
                                            playlistSongList.get(i).song_name, playlistSongList.get(i).artist_name,
                                            playlistSongList.get(i).album_name, DOMAIN_TEST + playlistSongList.get(i).song_high_path,
                                            DOMAIN_TEST + playlistSongList.get(i).song_low_path, DOMAIN_TEST + playlistSongList.get(i).album_image);
                                    mediaItems.add(mI);
                                    MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
                                }

                            }

                            @Override
                            public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                                Log.e("asdf", "error in low qty " + t.getMessage());
                            }
                        });
                    } else {

                        mediaItems = new LinkedList<>();
                        for (int i = 0; i < playlistSongList.size(); i++) {
                            MediaItem mI = new MediaItem(String.valueOf(playlistSongList.get(i).songid),
                                    playlistSongList.get(i).song_name, playlistSongList.get(i).artist_name,
                                    playlistSongList.get(i).album_name, DOMAIN_TEST + playlistSongList.get(i).song_high_path,
                                    DOMAIN_TEST + playlistSongList.get(i).song_low_path, DOMAIN_TEST + playlistSongList.get(i).album_image);
                            mediaItems.add(mI);
                        }

                        playlistManager.setParameters(mediaItems, position);
                        playlistManager.setId(20);
                        playlistManager.play(0, false);
                        MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
                    }
                }
            }
        });
    }

    private void loadPlaylistSong() {
        playlistSongList = new ArrayList<>();
        Call<PersonalPlaylistSong> call = requestInterface2.getPersonalPlaylistSong(Integer.parseInt(pass_Id));
        call.enqueue(new Callback<PersonalPlaylistSong>() {
            @Override
            public void onResponse(Call<PersonalPlaylistSong> call, Response<PersonalPlaylistSong> response) {
                if (response.isSuccessful()) {
                    playlistSongList.addAll(response.body().data);
                    personalPlaylistSongListAdapter = new PersonalPlaylistSongListAdapter(playlistSongList, getActivity());
                    personalPlaylistSongListAdapter.setEmptyView(getLayoutInflater(bundle).inflate(R.layout.activity_personal_playlist_song_empty, (ViewGroup) rv_personal_playlist_song_list.getParent(), false));
                    rv_personal_playlist_song_list.setAdapter(personalPlaylistSongListAdapter);
                }
            }

            @Override
            public void onFailure(Call<PersonalPlaylistSong> call, Throwable t) {
                Log.e("asdf", "error");
            }
        });
    }

    private void removeSongFromPlaylist(int position) {
        Call<CreatePlaylistModel> call = requestInterface2.removeSongFromPlaylist(playlistSongList.get(position).playlistid,
                playlistSongList.get(position).songid);
        call.enqueue(new Callback<CreatePlaylistModel>() {
            @Override
            public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                if (response.isSuccessful()) {
                    loadPlaylistSong();
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {

            }
        });
    }
}
