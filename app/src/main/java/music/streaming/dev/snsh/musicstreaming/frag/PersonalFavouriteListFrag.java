package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import music.streaming.dev.snsh.musicstreaming.adpt.PersonalFavouriteListAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.FavouriteModel;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalFavouriteList;
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

public class PersonalFavouriteListFrag extends Fragment {

    private RecyclerView rv_personal_favourite_list;
    private PersonalFavouriteListAdapter personalFavouriteListAdapter;

    private FragmentManager fragmentManager;

    private RequestInterface2 requestInterface2;

    private String cusId;
    private List<PersonalFavouriteList.Data> favouriteList;
    private String pass_Id, pass_Name;
    private LinkedList<MediaItem> mediaItems;
    private PlaylistManager playlistManager;

    private Bundle aa;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_favourite_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Favourite");
        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.TITLE2)) {
            Hawk.delete(MConstants.TITLE2);
        }
        Hawk.put(MConstants.TITLE2, "Favourite");

        aa = savedInstanceState;

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.PERSONAL_FAVOURITE_LIST)) {
            Hawk.delete(MConstants.PERSONAL_FAVOURITE_LIST);
        }
        Hawk.put(MConstants.PERSONAL_FAVOURITE_LIST, true);

        if (Hawk.contains(MConstants.CUS_ID)) {
            cusId = String.valueOf(Hawk.get(MConstants.CUS_ID, ""));
        }

        /*Bundle bundle = getArguments();
        if (bundle != null) {
            pass_Id = bundle.getString(MConstants.PASS_ID);
            pass_Name = bundle.getString(MConstants.PASS_NAME);
        }*/

        requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);

        rv_personal_favourite_list = (RecyclerView) view.findViewById(R.id.rv_personal_favourite_list);
        favouriteList = new ArrayList<>();
        personalFavouriteListAdapter = new PersonalFavouriteListAdapter(favouriteList, getActivity());
        rv_personal_favourite_list.setHasFixedSize(true);
        rv_personal_favourite_list.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv_personal_favourite_list.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));

        loadFavouriteList();

        rv_personal_favourite_list.addOnItemTouchListener(new OnItemClickListener() {
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
                                        removeSongFromFavourite(position);
                                        break;
                                }
                            }
                        })
                        .show();

            }

            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, final int position) {
                if (favouriteList.size() > 0) {
                    if (SendDurationToServer.isPlayingSong()) {
                        SendDurationToServer.sendDuration();
                    }

                    Hawk.init(App.getApplication()).build();
                    boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                    if (isLowQuality) {
                        RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                        Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(favouriteList.get(position).song_id));
                        call.enqueue(new Callback<LowQualityDTO>() {

                            @Override
                            public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                                mediaItems = new LinkedList<>();
                                for (int i = 0; i < favouriteList.size(); i++) {
                                    MediaItem mI = new MediaItem(String.valueOf(favouriteList.get(position).song_id),
                                            favouriteList.get(position).song_name, favouriteList.get(position).artist_name,
                                            favouriteList.get(position).album_name, DOMAIN_TEST + favouriteList.get(position).song_high_path,
                                            DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + favouriteList.get(position).album_image);
                                    mediaItems.add(mI);
                                }
                                playlistManager = App.getPlaylistManager();
                                playlistManager.setParameters(mediaItems, position);
                                playlistManager.setId(20);
                                playlistManager.play(0, false);

                                mediaItems = new LinkedList<>();
                                for (int i = 0; i < favouriteList.size(); i++) {
                                    MediaItem mI = new MediaItem(String.valueOf(favouriteList.get(i).song_id),
                                            favouriteList.get(i).song_name, favouriteList.get(i).artist_name,
                                            favouriteList.get(i).album_name, DOMAIN_TEST + favouriteList.get(i).song_high_path,
                                            DOMAIN_TEST + favouriteList.get(i).song_low_path, DOMAIN_TEST + favouriteList.get(i).album_image);
                                    mediaItems.add(mI);
                                }

                                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);

                            }

                            @Override
                            public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                                Log.e("asdf", "error in low qty " + t.getMessage());
                            }
                        });
                    } else {

                        mediaItems = new LinkedList<>();
                        for (int i = 0; i < favouriteList.size(); i++) {
                            MediaItem mI = new MediaItem(String.valueOf(favouriteList.get(i).song_id),
                                    favouriteList.get(i).song_name, favouriteList.get(i).artist_name,
                                    favouriteList.get(i).album_name, DOMAIN_TEST + favouriteList.get(i).song_high_path,
                                    DOMAIN_TEST + favouriteList.get(i).song_low_path, DOMAIN_TEST + favouriteList.get(i).album_image);
                            mediaItems.add(mI);
                        }
                        playlistManager = App.getPlaylistManager();
                        playlistManager.setParameters(mediaItems, position);
                        playlistManager.setId(20);
                        playlistManager.play(0, false);

                        MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
                    }
                }
            }
        });
    }

    private void loadFavouriteList() {
        favouriteList = new ArrayList<>();
        Call<PersonalFavouriteList> call = requestInterface2.getPersonalFavouriteList(Integer.parseInt(cusId));
        call.enqueue(new Callback<PersonalFavouriteList>() {
            @Override
            public void onResponse(Call<PersonalFavouriteList> call, Response<PersonalFavouriteList> response) {
                if (response.isSuccessful()) {
                    if (response.body().data.size() > 0)
                        favouriteList.addAll(response.body().data);
                    personalFavouriteListAdapter = new PersonalFavouriteListAdapter(favouriteList, getActivity());
                    personalFavouriteListAdapter.setEmptyView(getLayoutInflater(aa).inflate(R.layout.activity_personal_playlist_song_empty, (ViewGroup) rv_personal_favourite_list.getParent(), false));
                    rv_personal_favourite_list.setAdapter(personalFavouriteListAdapter);
                }
            }

            @Override
            public void onFailure(Call<PersonalFavouriteList> call, Throwable t) {

            }
        });
    }

    private void removeSongFromFavourite(int position) {
        Log.e("asdf", cusId + favouriteList.get(position).song_id);
        Call<FavouriteModel> call = requestInterface2.setFavouriteFalse(Integer.parseInt(cusId), favouriteList.get(position).song_id);
        call.enqueue(new Callback<FavouriteModel>() {
            @Override
            public void onResponse(Call<FavouriteModel> call, Response<FavouriteModel> response) {
                if (response.isSuccessful()) {
                    loadFavouriteList();
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavouriteModel> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
