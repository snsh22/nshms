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

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.GenreDetailsAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistGenreMusicTypeDetails;
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


public class GenreDetailsFrag extends Fragment {

    private String pass_Id;
    private String pass_Name, pass_Title, pass_Info, pass_Image;

    private RecyclerView rv_genre_details;
    private GenreDetailsAdapter genreDetailsAdapter;
    private ImageButton ib_nav_back;


//    private MaterialDialog processDialog;

    //    private LinearLayout ll_info, ll_data;
    //    private ImageView iv_photo;
    //    private TextView tv_header, tv_title, tv_info, tv_title2;
    private int pass_flag;

    private PlaylistManager playlistManager;
    public static FragmentManager fragmentManager;

    private List<PlaylistGenreMusicTypeDetails.Data> genreDataList;
    //    private ArrayList<PlaylistGenreMusicTypeDetails.Data> genreDataList = new ArrayList<>();
//    private ArrayList<PlaylistGenreMusicTypeDetails.Data> dataGenreTmp = new ArrayList<>();
    private boolean isLoadingGenre = false;

    private int currentPage, lastPage;
    private RequestInterface2 requestAPI;
    private String TAG = this.getClass().getSimpleName();

    public GenreDetailsFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.GENRE_DETAILS)) {
            Hawk.delete(MConstants.GENRE_DETAILS);
        }
        Hawk.put(MConstants.GENRE_DETAILS, true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            pass_Id = bundle.getString(MConstants.PASS_ID);
            pass_Name = bundle.getString(MConstants.PASS_NAME);
            pass_Title = bundle.getString(MConstants.PASS_TITLE);
            pass_Info = bundle.getString(MConstants.PASS_INFO);
            pass_Image = bundle.getString(MConstants.PASS_IMAGE);
            pass_flag = bundle.getInt(MConstants.PASS_FLAG);

        }

//        showProcessDialog();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rl_back.getLayoutParams();
            int dpValue = 15; // margin in dips
            float d = getContext().getResources().getDisplayMetrics().density;
            int margin = (int) (dpValue * d); // margin in pixels
            params.topMargin = margin;
            rl_back.setLayoutParams(params);
        }*/

        playlistManager = App.getPlaylistManager();
        requestAPI = ServiceGenerator.createService(RequestInterface2.class);

        init(view);
//        loadGenre(1);

        genreDataList = new ArrayList<>();
        genreDetailsAdapter = new GenreDetailsAdapter(getActivity(), genreDataList);
        genreDetailsAdapter.setLoadMoreListener(new GenreDetailsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_genre_details.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPage <= lastPage)
                            loadMore(currentPage + 1);
                    }
                });
                //Calling loadMore function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });
        rv_genre_details.setHasFixedSize(true);
        rv_genre_details.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv_genre_details.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        rv_genre_details.setAdapter(genreDetailsAdapter);
        load(1);
        setupListener();
    }

    private void setupListener() {
        /*rv_genre_details.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                rvClick(position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                super.onItemChildClick(adapter, view, position);
                switch (view.getId()) {
                    case R.id.ib_song_detail:
                        *//*PopupMenu popup = new PopupMenu(getActivity(), rv_genre_details.getChildAt(position).findViewById(R.id.ib_song_detail));
                        popup.getMenuInflater().inflate(R.menu.activity_queue_song_detail, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_add_to_playlist:
//                                        Toast.makeText(AlbumActivity.this, "aaa" + String.valueOf(datumArrayList.get(position).getSongId()), Toast.LENGTH_SHORT).show();
                                        *//**//*Bundle bundle = new Bundle();
                                        bundle.putInt(MConstants.PASS_FLAG, 0);
                                        bundle.putString(MConstants.PASS_ID, String.valueOf(genreDataList.get(position).getSongId()));
                                        Intent intent = new Intent(getActivity(), PersonalPlaylistNameActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);*//**//*
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show();*//*
                        break;
                }
            }
        });*/
        rv_genre_details.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_genre_details, new RecyclerViewItemClickListener() {
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
        rv_genre_details = (RecyclerView) view.findViewById(R.id.rv_genre_details);
//        iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
//        ll_info = (LinearLayout) view.findViewById(R.id.ll_info);
//        ll_data = (LinearLayout) view.findViewById(R.id.ll_data);


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
        Call<PlaylistGenreMusicTypeDetails> call = requestAPI.getGenreDetails(Integer.parseInt(pass_Id), index);
        call.enqueue(new Callback<PlaylistGenreMusicTypeDetails>() {
            @Override
            public void onResponse(Call<PlaylistGenreMusicTypeDetails> call, Response<PlaylistGenreMusicTypeDetails> response) {
                if (response.isSuccessful()) {

                    currentPage = response.body().MusicTypeData.current_page;
                    lastPage = response.body().MusicTypeData.last_page;

                    genreDataList.addAll(response.body().MusicTypeData.data);
                    genreDetailsAdapter.notifyDataChanged();
                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<PlaylistGenreMusicTypeDetails> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMore(int index) {

        //add loading progress view
        genreDataList.add(new PlaylistGenreMusicTypeDetails.Data("load"));
        genreDetailsAdapter.notifyItemInserted(genreDataList.size() - 1);

        Call<PlaylistGenreMusicTypeDetails> call = requestAPI.getGenreDetails(Integer.parseInt(pass_Id), index);
        call.enqueue(new Callback<PlaylistGenreMusicTypeDetails>() {
            @Override
            public void onResponse(Call<PlaylistGenreMusicTypeDetails> call, Response<PlaylistGenreMusicTypeDetails> response) {
                if (response.isSuccessful()) {

                    currentPage = response.body().MusicTypeData.current_page;
                    lastPage = response.body().MusicTypeData.last_page;

                    //remove loading view
                    genreDataList.remove(genreDataList.size() - 1);

                    List<PlaylistGenreMusicTypeDetails.Data> result = response.body().MusicTypeData.data;
                    if (currentPage <= lastPage) {
                        //add loaded data
                        genreDataList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        genreDetailsAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        genreDataList.add(new PlaylistGenreMusicTypeDetails.Data("footer"));
                        genreDetailsAdapter.notifyItemInserted(genreDataList.size() - 1);
//                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    genreDetailsAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<PlaylistGenreMusicTypeDetails> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadGenre(int page) {

        isLoadingGenre = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MConstants.MAIN_URL)
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<PlaylistGenreMusicTypeDetails> call = request.getGenreDetails(Integer.parseInt(pass_Id), page);
        call.enqueue(new Callback<PlaylistGenreMusicTypeDetails>() {
            @Override
            public void onResponse(Call<PlaylistGenreMusicTypeDetails> call, Response<PlaylistGenreMusicTypeDetails> response) {

                currentPage = response.body().getMusictypedata().getCurrentPage();
                lastPage = response.body().getMusictypedata().getLastPage();

                final PlaylistGenreMusicTypeDetails.Data[] datums2 = new PlaylistGenreMusicTypeDetails.Data[response.body().getMusictypedata().getData().size()];

                int scrollPosition = 0;
                scrollPosition = genreDataList.size();
                dataGenreTmp = new ArrayList<>();
                for (int i = 0; i < response.body().getMusictypedata().getData().size(); i++) {
                    datums2[i] = (response.body().getMusictypedata().getData().get(i));
                    dataGenreTmp.add(datums2[i]);
                    genreDataList.add(datums2[i]);
                }
//                rv_genre_details.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
                rv_genre_details.setLayoutManager(new LinearLayoutManager(getContext()));

                if (currentPage == 1) {
//                    processDialog.dismiss();
                    genreDetailsAdapter = new GenreDetailsAdapter(dataGenreTmp, getActivity());
                    genreDetailsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                        @Override
                        public void onLoadMoreRequested() {
                            if (currentPage >= lastPage) {
//                              pullToRefreshAdapter.loadMoreEnd();//default visible
                                genreDetailsAdapter.loadMoreEnd(true);//true is gone,false is visible
                            } else {
                                if (!isLoadingGenre) {
                                    loadGenre(currentPage + 1);

                                }
                            }
                        }
                    }, rv_genre_details);
                    genreDetailsAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                    genreDetailsAdapter.setEnableLoadMore(true);
                    rv_genre_details.setAdapter(genreDetailsAdapter);
                    genreDetailsAdapter.setLoadMoreView(new CustomLoadMoreView());
//                genreDetailsAdapter.setAutoLoadMoreSize(3);
                } else {
                    genreDetailsAdapter.addData(dataGenreTmp);
                    rv_genre_details.scrollToPosition(scrollPosition - 2);
                    genreDetailsAdapter.loadMoreComplete();
                }
                isLoadingGenre = false;
            }

            @Override
            public void onFailure(Call<PlaylistGenreMusicTypeDetails> call, Throwable t) {
                isLoadingGenre = false;
//                processDialog.dismiss();
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
            Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(genreDataList.get(position).id));
            call.enqueue(new Callback<LowQualityDTO>() {

                @Override
                public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                    LinkedList<MediaItem> mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(genreDataList.get(position).id),
                            genreDataList.get(position).song_name, genreDataList.get(position).artist_name,
                            genreDataList.get(position).album_name, DOMAIN_TEST + genreDataList.get(position).song_high_path,
                            DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + genreDataList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);

                    mediaItems = new LinkedList<>();
                    for (int i = 0; i < genreDataList.size(); i++) {
                        MediaItem mI = new MediaItem(String.valueOf(genreDataList.get(i).id),
                                genreDataList.get(i).song_name, genreDataList.get(i).artist_name,
                                genreDataList.get(i).album_name, DOMAIN_TEST + genreDataList.get(i).song_high_path,
                                DOMAIN_TEST + genreDataList.get(i).song_low_path, DOMAIN_TEST + genreDataList.get(i).album_image);
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
            MediaItem mediaItem = new MediaItem(String.valueOf(genreDataList.get(position).id),
                    genreDataList.get(position).song_name, genreDataList.get(position).artist_name,
                    genreDataList.get(position).album_name, DOMAIN_TEST + genreDataList.get(position).song_high_path,
                    DOMAIN_TEST + genreDataList.get(position).song_low_path, DOMAIN_TEST + genreDataList.get(position).album_image);
            mediaItems.add(mediaItem);

            playlistManager.setParameters(mediaItems, 0);
            playlistManager.setId(20);
            playlistManager.play(0, false);

            mediaItems = new LinkedList<>();
            for (int i = 0; i < genreDataList.size(); i++) {
                MediaItem mI = new MediaItem(String.valueOf(genreDataList.get(i).id),
                        genreDataList.get(i).song_name, genreDataList.get(i).artist_name,
                        genreDataList.get(i).album_name, DOMAIN_TEST + genreDataList.get(i).song_high_path,
                        DOMAIN_TEST + genreDataList.get(i).song_low_path, DOMAIN_TEST + genreDataList.get(i).album_image);
                mediaItems.add(mI);
            }

            MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
        }
    }

    private void showProcessDialog() {
        /*processDialog = new MaterialDialog.Builder(getActivity())
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();*/
    }

}