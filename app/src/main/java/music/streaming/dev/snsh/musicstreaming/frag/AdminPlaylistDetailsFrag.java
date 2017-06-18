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
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.AdminPlaylistDetailsAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistAdminPlaylistDetails;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import music.streaming.dev.snsh.musicstreaming.utly.SendDurationToServer;
import music.streaming.dev.snsh.musicstreaming.utly.custom.recyclerloadmore.CustomRVItemTouchListener;
import music.streaming.dev.snsh.musicstreaming.utly.custom.recyclerloadmore.RecyclerViewItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class AdminPlaylistDetailsFrag extends Fragment {

    private String pass_Id;
    private String pass_Name, pass_Title, pass_Info, pass_Image;

    private RecyclerView rv_admin_playlist_details;
    private AdminPlaylistDetailsAdapter adminPlaylistDetailsAdapter;
    private ImageButton ib_nav_back;

    private MaterialDialog processDialog;

    //    private ImageView iv_photo;
//    private ObservableScrollView mScrollView;
//    private TextView tv_header, tv_title, tv_info, tv_title2;
    private int pass_flag;
//    private ArrayList<PlaylistAdminPlaylistDetails.Data> adminPlaylistDataList = new ArrayList<>();
//    private PlaylistAdminPlaylistDetails.Data[] datums;

    private PlaylistManager playlistManager;
    public static FragmentManager fragmentManager;

    private List<PlaylistAdminPlaylistDetails.Data> adminPlaylistDataList;
    //    private int currentPage, lastPage;
    private RequestInterface2 requestAPI;
    private String TAG = this.getClass().getSimpleName();

    public AdminPlaylistDetailsFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_playlist_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.ADMIN_PLAYLIST_DETAILS)) {
            Hawk.delete(MConstants.ADMIN_PLAYLIST_DETAILS);
        }
        Hawk.put(MConstants.ADMIN_PLAYLIST_DETAILS, true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            pass_Id = bundle.getString(MConstants.PASS_ID);
            pass_Name = bundle.getString(MConstants.PASS_NAME);
            pass_Title = bundle.getString(MConstants.PASS_TITLE);
            pass_Info = bundle.getString(MConstants.PASS_INFO);
            pass_Image = bundle.getString(MConstants.PASS_IMAGE);
            pass_flag = bundle.getInt(MConstants.PASS_FLAG);
        }

        showProcessDialog();

        playlistManager = App.getPlaylistManager();
        requestAPI = ServiceGenerator.createService(RequestInterface2.class);


        init(view);
        adminPlaylistDataList = new ArrayList<>();
        adminPlaylistDetailsAdapter = new AdminPlaylistDetailsAdapter(getActivity(), adminPlaylistDataList);
        /*adminPlaylistDetailsAdapter.setLoadMoreListener(new GenreDetailsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_admin_playlist_details.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPage <= lastPage)
                            loadMore(currentPage + 1);
                    }
                });
                //Calling loadMore function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });*/
        rv_admin_playlist_details.setHasFixedSize(true);
        rv_admin_playlist_details.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv_genre_details.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        rv_admin_playlist_details.setAdapter(adminPlaylistDetailsAdapter);
        load(1);
        setupListener();
//        loadJSON();
    }

    private void setupListener() {

        rv_admin_playlist_details.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_admin_playlist_details, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                rvClick(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void init(View view) {
//        tv_header = (TextView) view.findViewById(R.id.tv_header);
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title2 = (TextView) view.findViewById(R.id.tv_title2);
//        tv_info = (TextView) view.findViewById(R.id.tv_info);
        rv_admin_playlist_details = (RecyclerView) view.findViewById(R.id.rv_admin_playlist_details);
//        iv_photo = (ImageView) view.findViewById(R.id.iv_photo);

//        mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
//        mScrollView.setScrollViewCallbacks(this);


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(pass_Name);
        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.TITLE2)) {
            Hawk.delete(MConstants.TITLE2);
        }
        Hawk.put(MConstants.TITLE2, pass_Name);

//        tv_header.setText(pass_Name);
//        tv_title.setText(pass_Title);
//        tv_title.setVisibility(View.GONE);
//        tv_title2.setText("Songs");
//        tv_info.setText(pass_Info);

//        Picasso.with(getContext())
//                .load(pass_Image)
//                .placeholder(R.drawable.art)
//                .into(iv_photo);
    }

    private void load(int index) {
        Call<PlaylistAdminPlaylistDetails> call = requestAPI.getAdminPlayListDetail(Integer.parseInt(pass_Id));
        call.enqueue(new Callback<PlaylistAdminPlaylistDetails>() {
            @Override
            public void onResponse(Call<PlaylistAdminPlaylistDetails> call, Response<PlaylistAdminPlaylistDetails> response) {
                if (response.isSuccessful()) {

//                    currentPage = response.body().getMusictypedata().getCurrentPage();
//                    lastPage = response.body().getMusictypedata().getLastPage();

                    processDialog.dismiss();
                    adminPlaylistDataList.addAll(response.body().data);
                    adminPlaylistDetailsAdapter.notifyDataChanged();
                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<PlaylistAdminPlaylistDetails> call, Throwable t) {
                processDialog.dismiss();
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadJSON() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MConstants.MAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<PlaylistAdminPlaylistDetails> callAlbums = request.getAdminPlayListDetail(Integer.parseInt(pass_Id));
        callAlbums.enqueue(new Callback<PlaylistAdminPlaylistDetails>() {
            @Override
            public void onResponse(Call<PlaylistAdminPlaylistDetails> call, Response<PlaylistAdminPlaylistDetails> response) {

                datums = new PlaylistAdminPlaylistDetails.Data[response.body().getData().size()];
                for (int i = 0; i < response.body().getData().size(); i++) {
                    datums[i] = (response.body().getData().get(i));
                    adminPlaylistDataList.add(datums[i]);
                }
                rv_admin_playlist_details.setLayoutManager(new LinearLayoutManager(getActivity()));

                adminPlaylistDetailsAdapter = new AdminPlaylistDetailsAdapter(adminPlaylistDataList, getActivity());
                adminPlaylistDetailsAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
//        adminPlaylistDetailsAdapter.setAutoLoadMoreSize(3);
                processDialog.dismiss();
                rv_admin_playlist_details.setAdapter(adminPlaylistDetailsAdapter);
//        mCurrentCounter = adminPlaylistDetailsAdapter.getData().size();
                adminPlaylistDetailsAdapter.setEmptyView(getLayoutInflater(null).inflate(R.layout.activity_album_empty, (ViewGroup) rv_admin_playlist_details.getParent(), false));

            }

            @Override
            public void onFailure(Call<PlaylistAdminPlaylistDetails> call, Throwable t) {
            }
        });
    }*/

    private void rvClick(final int position) {

        if (SendDurationToServer.isPlayingSong()) {
            SendDurationToServer.sendDuration();
        }
        playlistManager = App.getPlaylistManager();
        Hawk.init(App.getApplication()).build();
        boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
        if (isLowQuality) {
            RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
            Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(adminPlaylistDataList.get(position).id));
            call.enqueue(new Callback<LowQualityDTO>() {

                @Override
                public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                    LinkedList<MediaItem> mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(adminPlaylistDataList.get(position).id),
                            adminPlaylistDataList.get(position).song_name, adminPlaylistDataList.get(position).artist_name,
                            adminPlaylistDataList.get(position).album_name, DOMAIN_TEST + adminPlaylistDataList.get(position).song_high_path,
                            DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + adminPlaylistDataList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);

                    mediaItems = new LinkedList<>();
                    for (int i = 0; i < adminPlaylistDataList.size(); i++) {
                        MediaItem mI = new MediaItem(String.valueOf(adminPlaylistDataList.get(i).id),
                                adminPlaylistDataList.get(i).song_name, adminPlaylistDataList.get(i).artist_name,
                                adminPlaylistDataList.get(i).album_name, DOMAIN_TEST + adminPlaylistDataList.get(i).song_high_path,
                                DOMAIN_TEST + adminPlaylistDataList.get(i).song_low_path, DOMAIN_TEST + adminPlaylistDataList.get(i).album_image);
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

            LinkedList<MediaItem> mediaItems = new LinkedList<>();
            MediaItem mediaItem = new MediaItem(String.valueOf(adminPlaylistDataList.get(position).id),
                    adminPlaylistDataList.get(position).song_name, adminPlaylistDataList.get(position).artist_name,
                    adminPlaylistDataList.get(position).album_name, DOMAIN_TEST + adminPlaylistDataList.get(position).song_high_path,
                    DOMAIN_TEST + adminPlaylistDataList.get(position).song_low_path, DOMAIN_TEST + adminPlaylistDataList.get(position).album_image);
            mediaItems.add(mediaItem);

            playlistManager.setParameters(mediaItems, 0);
            playlistManager.setId(20);
            playlistManager.play(0, false);

            mediaItems = new LinkedList<>();
            for (int i = 0; i < adminPlaylistDataList.size(); i++) {
                MediaItem mI = new MediaItem(String.valueOf(adminPlaylistDataList.get(i).id),
                        adminPlaylistDataList.get(i).song_name, adminPlaylistDataList.get(i).artist_name,
                        adminPlaylistDataList.get(i).album_name, DOMAIN_TEST + adminPlaylistDataList.get(i).song_high_path,
                        DOMAIN_TEST + adminPlaylistDataList.get(i).song_low_path, DOMAIN_TEST + adminPlaylistDataList.get(i).album_image);
                mediaItems.add(mI);
            }

            MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
        }

        /*LinkedList<MediaItem> mediaItems;
        mediaItems = new LinkedList<>();
        MediaItem mediaItem = new MediaItem(String.valueOf(adminPlaylistDataList.get(position).getSongId()), adminPlaylistDataList.get(position).getSongName(), adminPlaylistDataList.get(position).getSongPath(), adminPlaylistDataList.get(position).getSongPhotoPath(), adminPlaylistDataList.get(position).getAlbumName(), adminPlaylistDataList.get(position).getArtistName(), adminPlaylistDataList.get(position).getLyricPath(), true);
        mediaItems.add(mediaItem);

        playlistManager.setParameters(mediaItems, 0);
        playlistManager.setId(20);
        playlistManager.play(0, false);

        mediaItems = new LinkedList<>();
        for (int i = 0; i < adminPlaylistDataList.size(); i++) {
            MediaItem mI = new MediaItem(String.valueOf(adminPlaylistDataList.get(i).getSongId()), adminPlaylistDataList.get(i).getSongName(), adminPlaylistDataList.get(i).getSongPath(), adminPlaylistDataList.get(i).getSongPhotoPath(), adminPlaylistDataList.get(i).getAlbumName(), adminPlaylistDataList.get(i).getArtistName(), adminPlaylistDataList.get(i).getLyricPath(), true);
            mediaItems.add(mI);
        }

        updateNowPlayingMediaItem(mediaItems, position);*/
    }

    private void showProcessDialog() {
        processDialog = new MaterialDialog.Builder(getActivity())
                .content("Loading...")
                .theme(Theme.DARK)
                .cancelable(false)
                .progress(true, 0)
                .show();
    }
}