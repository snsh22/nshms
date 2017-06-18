package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.EnglishAdapter;
import music.streaming.dev.snsh.musicstreaming.adpt.JapanAdapter;
import music.streaming.dev.snsh.musicstreaming.adpt.KoreaAdapter;
import music.streaming.dev.snsh.musicstreaming.adpt.MyanmarAdapter;
import music.streaming.dev.snsh.musicstreaming.adpt.NewThisWeekAdapter;
import music.streaming.dev.snsh.musicstreaming.adpt.ThaiAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverEnglish;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverJapan;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverKorea;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverMyanmar;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverNewThisWeek;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverThai;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import music.streaming.dev.snsh.musicstreaming.utly.NetworkHelper;
import music.streaming.dev.snsh.musicstreaming.utly.SendDurationToServer;
import music.streaming.dev.snsh.musicstreaming.utly.custom.recyclerloadmore.CustomRVItemTouchListener;
import music.streaming.dev.snsh.musicstreaming.utly.custom.recyclerloadmore.RecyclerViewItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class DiscoverFrag extends Fragment {

    private RecyclerView rv_myanmar, rv_english, rv_new_this_week, rv_korea, rv_thai, rv_japan;
    private TextView tv_myanmar, tv_english, tv_new_this_week, tv_korea, tv_thai, tv_japan;

    private MyanmarAdapter myanmarAdapter;
    private NewThisWeekAdapter newThisWeekAdapter;
    private EnglishAdapter englishAdapter;
    private KoreaAdapter koreaAdapter;
    private ThaiAdapter thaiAdapter;
    private JapanAdapter japanAdapter;

//    private ArrayList<DiscoverNewThisWeek.Data> dataWeek = new ArrayList<>();
//    private ArrayList<DiscoverNewThisWeek.Data> dataWeekTmp = new ArrayList<>();
//    private ArrayList<DiscoverMyanmar.Data> dataMyanmarList = new ArrayList<>();
//    private ArrayList<DiscoverMyanmar.Data> dataMyanmarListTmp = new ArrayList<>();
//    private ArrayList<DiscoverEnglish.Data> dataEnglish = new ArrayList<>();
//    private ArrayList<DiscoverEnglish.Data> dataEnglishTmp = new ArrayList<>();

    //    private ArrayList<DiscoverKorea.Data> dataKorea = new ArrayList<>();
//    private ArrayList<DiscoverKorea.Data> dataKoreaTmp = new ArrayList<>();
//    private ArrayList<DiscoverThai.Data> dataThai = new ArrayList<>();
//    private ArrayList<DiscoverThai.Data> dataThaiTmp = new ArrayList<>();
//    private ArrayList<DiscoverJapan.Data> dataJapan = new ArrayList<>();
//    private ArrayList<DiscoverJapan.Data> dataJapanTmp = new ArrayList<>();

    private RequestInterface2 requestAPI;
    private List<DiscoverMyanmar.Data> dataMyanmarList;
    private List<DiscoverEnglish.Data> dataEnglishList;
    private List<DiscoverNewThisWeek.Data> dataWeekList;
    private List<DiscoverKorea.Data> dataKoreaList;
    private List<DiscoverThai.Data> dataThaiList;
    private List<DiscoverJapan.Data> dataJapanList;

    private String TAG = this.getClass().getSimpleName();


    private PlaylistManager playlistManager;
    private LinkedList<MediaItem> mediaItems;

    private LinearLayout ll_data;
    private RelativeLayout rl_offline;
    private MaterialDialog processDialog;

//    private ImageButton ib_setting, ib_search;

    int currentPageWeek, lastPageWeek;
    int currentPageMyanmar, lastPageMyanmar;
    int currentPageEnglish, lastPageEnglish;
    int currentPageKorea, lastPageKorea;
    int currentPageThai, lastPageThai;
    int currentPageJapan, lastPageJapan;

//    boolean isLoadingWeek = false;
//    boolean isLoadingEnglish = false;
//    boolean isLoadingKorea = false;
//    boolean isLoadingThai = false;
//    boolean isLoadingJapan = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestAPI = ServiceGenerator.createService(RequestInterface2.class);

        rv_new_this_week = (RecyclerView) view.findViewById(R.id.rv_new_this_week);
        rv_myanmar = (RecyclerView) view.findViewById(R.id.rv_myanmar);
        rv_english = (RecyclerView) view.findViewById(R.id.rv_english);
        rv_korea = (RecyclerView) view.findViewById(R.id.rv_korea);
        rv_thai = (RecyclerView) view.findViewById(R.id.rv_thai);
        rv_japan = (RecyclerView) view.findViewById(R.id.rv_japan);

        tv_new_this_week = (TextView) view.findViewById(R.id.tv_new_this_week);
        tv_myanmar = (TextView) view.findViewById(R.id.tv_myanmar);
        tv_english = (TextView) view.findViewById(R.id.tv_english);
        tv_korea = (TextView) view.findViewById(R.id.tv_korea);
        tv_thai = (TextView) view.findViewById(R.id.tv_thai);
        tv_japan = (TextView) view.findViewById(R.id.tv_japan);

        rl_offline = (RelativeLayout) view.findViewById(R.id.rl_offline);
        rl_offline.setVisibility(View.GONE);

        if (!NetworkHelper.isOnline(getContext()))
            rl_offline.setVisibility(View.VISIBLE);

        tv_new_this_week.setVisibility(View.INVISIBLE);
        tv_myanmar.setVisibility(View.INVISIBLE);
        tv_english.setVisibility(View.INVISIBLE);
        tv_korea.setVisibility(View.INVISIBLE);
        tv_thai.setVisibility(View.INVISIBLE);
        tv_japan.setVisibility(View.INVISIBLE);

        rv_new_this_week.setVisibility(View.INVISIBLE);
        rv_myanmar.setVisibility(View.INVISIBLE);
        rv_english.setVisibility(View.INVISIBLE);
        rv_korea.setVisibility(View.INVISIBLE);
        rv_thai.setVisibility(View.INVISIBLE);
        rv_japan.setVisibility(View.INVISIBLE);

        /*week*/
        dataWeekList = new ArrayList<>();
        newThisWeekAdapter = new NewThisWeekAdapter(getActivity(), dataWeekList);
        newThisWeekAdapter.setLoadMoreListener(new NewThisWeekAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_new_this_week.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPageWeek < lastPageWeek)
                            loadMoreWeek(currentPageWeek + 1);
                    }
                });
                //Calling loadMoreMyanmar function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });

        rv_new_this_week.setHasFixedSize(true);
//        rv_new_this_week.setLayoutManager(new LinearLayoutManager(context));
        rv_new_this_week.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        rv_new_this_week.setAdapter(newThisWeekAdapter);
        loadWeek(1);

        /*myanmar*/
        dataMyanmarList = new ArrayList<>();
        myanmarAdapter = new MyanmarAdapter(getActivity(), dataMyanmarList);
        myanmarAdapter.setLoadMoreListener(new MyanmarAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_myanmar.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPageMyanmar < lastPageMyanmar)
                            loadMoreMyanmar(currentPageMyanmar + 1);
                    }
                });
                //Calling loadMoreMyanmar function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });

        rv_myanmar.setHasFixedSize(true);
//        rv_myanmar.setLayoutManager(new LinearLayoutManager(context));
        rv_myanmar.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        rv_myanmar.setAdapter(myanmarAdapter);
        loadMyanmar(1);

        /*english*/
        dataEnglishList = new ArrayList<>();
        englishAdapter = new EnglishAdapter(getActivity(), dataEnglishList);
        englishAdapter.setLoadMoreListener(new EnglishAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_english.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPageEnglish < lastPageEnglish)
                            loadMoreEnglish(currentPageEnglish + 1);
                    }
                });
                //Calling loadMoreMyanmar function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });
        rv_english.setHasFixedSize(true);
//        rv_english.setLayoutManager(new LinearLayoutManager(context));
        rv_english.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        rv_english.setAdapter(englishAdapter);
        loadEnglish(1);

        /*korea*/
        dataKoreaList = new ArrayList<>();
        koreaAdapter = new KoreaAdapter(getActivity(), dataKoreaList);
        koreaAdapter.setLoadMoreListener(new KoreaAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_korea.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPageKorea < lastPageKorea)
                            loadMoreKorea(currentPageKorea + 1);
                    }
                });
                //Calling loadMoreMyanmar function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });

        rv_korea.setHasFixedSize(true);
//        rv_korea.setLayoutManager(new LinearLayoutManager(context));
        rv_korea.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        rv_korea.setAdapter(koreaAdapter);
        loadKorea(1);

        /*thai*/
        dataThaiList = new ArrayList<>();
        thaiAdapter = new ThaiAdapter(getActivity(), dataThaiList);
        thaiAdapter.setLoadMoreListener(new ThaiAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_thai.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPageThai < lastPageThai)
                            loadMoreThai(currentPageThai + 1);
                    }
                });
                //Calling loadMoreMyanmar function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });

        rv_thai.setHasFixedSize(true);
//        rv_thai.setLayoutManager(new LinearLayoutManager(context));
        rv_thai.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        rv_thai.setAdapter(thaiAdapter);
        loadThai(1);

        /*japan*/
        dataJapanList = new ArrayList<>();
        japanAdapter = new JapanAdapter(getActivity(), dataJapanList);
        japanAdapter.setLoadMoreListener(new JapanAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                rv_japan.post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPageJapan < lastPageJapan)
                            loadMoreJapan(currentPageJapan + 1);
                    }
                });
                //Calling loadMoreMyanmar function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });

        rv_japan.setHasFixedSize(true);
//        rv_japan.setLayoutManager(new LinearLayoutManager(context));
        rv_japan.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        rv_japan.setAdapter(japanAdapter);
        loadJapan(1);


        ll_data = (LinearLayout) view.findViewById(R.id.ll_data);
        ll_data.setVisibility(View.INVISIBLE);
        ll_data.setVisibility(View.VISIBLE);
//        showProcessDialog();
//        loadWeek(1);
//        loadMyanmar(1);
//        loadEnglish(1);
//        loadKorea(1);
//        loadThai(1);
//        loadJapan(1);

        rv_new_this_week.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_new_this_week, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {

                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }

                playlistManager = App.getPlaylistManager();

                Hawk.init(App.getApplication()).build();
                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(dataWeekList.get(position).id));
                    call.enqueue(new Callback<LowQualityDTO>() {

                        @Override
                        public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                            mediaItems = new LinkedList<>();
                            MediaItem mediaItem = new MediaItem(String.valueOf(dataWeekList.get(position).id),
                                    dataWeekList.get(position).song_name, dataWeekList.get(position).artist_name,
                                    dataWeekList.get(position).album_name, DOMAIN_TEST + dataWeekList.get(position).song_high_path,
                                    DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + dataWeekList.get(position).album_image);
                            mediaItems.add(mediaItem);

                            playlistManager.setParameters(mediaItems, 0);
                            playlistManager.setId(20);
                            playlistManager.play(0, false);

                        }

                        @Override
                        public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                            Log.e("asdf", "error in low qty " + t.getMessage());
                        }
                    });
                } else {
                    mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(dataWeekList.get(position).id),
                            dataWeekList.get(position).song_name, dataWeekList.get(position).artist_name,
                            dataWeekList.get(position).album_name, DOMAIN_TEST + dataWeekList.get(position).song_high_path,
                            DOMAIN_TEST + dataWeekList.get(position).song_low_path, DOMAIN_TEST + dataWeekList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);
                }

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataWeekList.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataWeekList.get(i).id),
                            dataWeekList.get(i).song_name, dataWeekList.get(i).artist_name,
                            dataWeekList.get(i).album_name, DOMAIN_TEST + dataWeekList.get(i).song_high_path,
                            DOMAIN_TEST + dataWeekList.get(i).song_low_path, DOMAIN_TEST + dataWeekList.get(i).album_image);
                    mediaItems.add(mI);
                }

                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*rv_new_this_week.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                playlistManager = App.getPlaylistManager();

                mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(dataWeek.get(position).getSongId()),
                        dataWeek.get(position).getSongName(), dataWeek.get(position).getArtistName(),
                        dataWeek.get(position).getAlbumName(), dataWeek.get(position).getSongHighPath(),
                        dataWeek.get(position).getSongLowPath(), dataWeek.get(position).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataWeek.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataWeek.get(i).getSongId()),
                            dataWeek.get(i).getSongName(), dataWeek.get(i).getArtistName(),
                            dataWeek.get(i).getAlbumName(), dataWeek.get(i).getSongHighPath(),
                            dataWeek.get(i).getSongLowPath(), dataWeek.get(i).getAlbumImage());
                    mediaItems.add(mI);
                }

                updateNowPlayingMediaItem(mediaItems, position);

            }
        });*/

        rv_myanmar.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_myanmar, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }

                playlistManager = App.getPlaylistManager();

                Hawk.init(App.getApplication()).build();
                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(dataMyanmarList.get(position).id));
                    call.enqueue(new Callback<LowQualityDTO>() {

                        @Override
                        public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                            mediaItems = new LinkedList<>();
                            MediaItem mediaItem = new MediaItem(String.valueOf(dataMyanmarList.get(position).id),
                                    dataMyanmarList.get(position).song_name, dataMyanmarList.get(position).artist_name,
                                    dataMyanmarList.get(position).album_name, DOMAIN_TEST + dataMyanmarList.get(position).song_high_path,
                                    DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + dataMyanmarList.get(position).album_image);
                            mediaItems.add(mediaItem);

                            playlistManager.setParameters(mediaItems, 0);
                            playlistManager.setId(20);
                            playlistManager.play(0, false);

                        }

                        @Override
                        public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                            Log.e("asdf", "error in low qty " + t.getMessage());
                        }
                    });
                } else {

                    mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(dataMyanmarList.get(position).id),
                            dataMyanmarList.get(position).song_name, dataMyanmarList.get(position).artist_name,
                            dataMyanmarList.get(position).album_name, DOMAIN_TEST + dataMyanmarList.get(position).song_high_path,
                            DOMAIN_TEST + dataMyanmarList.get(position).song_low_path, DOMAIN_TEST + dataMyanmarList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);

                }
                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataMyanmarList.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataMyanmarList.get(i).id),
                            dataMyanmarList.get(i).song_name, dataMyanmarList.get(i).artist_name,
                            dataMyanmarList.get(i).album_name, DOMAIN_TEST + dataMyanmarList.get(i).song_high_path,
                            DOMAIN_TEST + dataMyanmarList.get(i).song_low_path, DOMAIN_TEST + dataMyanmarList.get(i).album_image);
                    mediaItems.add(mI);
                }

                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        rv_myanmar.addOnItemTouchListener(new OnItemClickListener() {
//            @Override
//            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
//                playlistManager = App.getPlaylistManager();
//
//                mediaItems = new LinkedList<>();
//                MediaItem mediaItem = new MediaItem(String.valueOf(dataMyanmarList.get(position).getSongId()),
//                        dataMyanmarList.get(position).getSongName(), dataMyanmarList.get(position).getArtistName(),
//                        dataMyanmarList.get(position).getAlbumName(), dataMyanmarList.get(position).getSongHighPath(),
//                        dataMyanmarList.get(position).getSongLowPath(), dataMyanmarList.get(position).getAlbumImage());
//                mediaItems.add(mediaItem);
//
//                playlistManager.setParameters(mediaItems, 0);
//                playlistManager.setId(20);
//                playlistManager.play(0, false);
//
//                mediaItems = new LinkedList<>();
//                for (int i = 0; i < dataMyanmarList.size(); i++) {
//                    MediaItem mI = new MediaItem(String.valueOf(dataMyanmarList.get(i).getSongId()),
//                            dataMyanmarList.get(i).getSongName(), dataMyanmarList.get(i).getArtistName(),
//                            dataMyanmarList.get(i).getAlbumName(), dataMyanmarList.get(i).getSongHighPath(),
//                            dataMyanmarList.get(i).getSongLowPath(), dataMyanmarList.get(i).getAlbumImage());
//                    mediaItems.add(mI);
//                }
//
//                updateNowPlayingMediaItem(mediaItems, position);
//
//            }
//        });

        rv_english.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_english, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }
                playlistManager = App.getPlaylistManager();

                Hawk.init(App.getApplication()).build();
                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(dataEnglishList.get(position).id));
                    call.enqueue(new Callback<LowQualityDTO>() {

                        @Override
                        public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                            mediaItems = new LinkedList<>();
                            MediaItem mediaItem = new MediaItem(String.valueOf(dataEnglishList.get(position).id),
                                    dataEnglishList.get(position).song_name, dataEnglishList.get(position).artist_name,
                                    dataEnglishList.get(position).album_name, DOMAIN_TEST + dataEnglishList.get(position).song_high_path,
                                    DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + dataEnglishList.get(position).album_image);
                            mediaItems.add(mediaItem);

                            playlistManager.setParameters(mediaItems, 0);
                            playlistManager.setId(20);
                            playlistManager.play(0, false);

                        }

                        @Override
                        public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                            Log.e("asdf", "error in low qty " + t.getMessage());
                        }
                    });
                } else {

                    mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(dataEnglishList.get(position).id),
                            dataEnglishList.get(position).song_name, dataEnglishList.get(position).artist_name,
                            dataEnglishList.get(position).album_name, DOMAIN_TEST + dataEnglishList.get(position).song_high_path,
                            DOMAIN_TEST + dataEnglishList.get(position).song_low_path, DOMAIN_TEST + dataEnglishList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);
                }

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataEnglishList.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataEnglishList.get(i).id),
                            dataEnglishList.get(i).song_name, dataEnglishList.get(i).artist_name,
                            dataEnglishList.get(i).album_name, DOMAIN_TEST + dataEnglishList.get(i).song_high_path,
                            DOMAIN_TEST + dataEnglishList.get(i).song_low_path, DOMAIN_TEST + dataEnglishList.get(i).album_image);
                    mediaItems.add(mI);
                }

                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*rv_english.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                playlistManager = App.getPlaylistManager();

                mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(dataEnglish.get(position).getSongId()),
                        dataEnglish.get(position).getSongName(), dataEnglish.get(position).getArtistName(),
                        dataEnglish.get(position).getAlbumName(), dataEnglish.get(position).getSongHighPath(),
                        dataEnglish.get(position).getSongLowPath(), dataEnglish.get(position).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataEnglish.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataEnglish.get(i).getSongId()),
                            dataEnglish.get(i).getSongName(), dataEnglish.get(i).getArtistName(),
                            dataEnglish.get(i).getAlbumName(), dataEnglish.get(i).getSongHighPath(),
                            dataEnglish.get(i).getSongLowPath(), dataEnglish.get(i).getAlbumImage());
                    mediaItems.add(mI);
                }

                updateNowPlayingMediaItem(mediaItems, position);

            }
        });*/

        rv_korea.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_korea, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }
                playlistManager = App.getPlaylistManager();

                Hawk.init(App.getApplication()).build();
                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(dataKoreaList.get(position).id));
                    call.enqueue(new Callback<LowQualityDTO>() {

                        @Override
                        public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                            mediaItems = new LinkedList<>();
                            MediaItem mediaItem = new MediaItem(String.valueOf(dataKoreaList.get(position).id),
                                    dataKoreaList.get(position).song_name, dataKoreaList.get(position).artist_name,
                                    dataKoreaList.get(position).album_name, DOMAIN_TEST + dataKoreaList.get(position).song_high_path,
                                    DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + dataKoreaList.get(position).album_image);
                            mediaItems.add(mediaItem);

                            playlistManager.setParameters(mediaItems, 0);
                            playlistManager.setId(20);
                            playlistManager.play(0, false);

                        }

                        @Override
                        public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                            Log.e("asdf", "error in low qty " + t.getMessage());
                        }
                    });
                } else {

                    mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(dataKoreaList.get(position).id),
                            dataKoreaList.get(position).song_name, dataKoreaList.get(position).artist_name,
                            dataKoreaList.get(position).album_name, DOMAIN_TEST + dataKoreaList.get(position).song_high_path,
                            DOMAIN_TEST + dataKoreaList.get(position).song_low_path, DOMAIN_TEST + dataKoreaList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);
                }

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataKoreaList.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataKoreaList.get(i).id),
                            dataKoreaList.get(i).song_name, dataKoreaList.get(i).artist_name,
                            dataKoreaList.get(i).album_name, DOMAIN_TEST + dataKoreaList.get(i).song_high_path,
                            DOMAIN_TEST + dataKoreaList.get(i).song_low_path, DOMAIN_TEST + dataKoreaList.get(i).album_image);
                    mediaItems.add(mI);
                }

                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*rv_korea.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                playlistManager = App.getPlaylistManager();

                mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(dataKorea.get(position).getSongId()),
                        dataKorea.get(position).getSongName(), dataKorea.get(position).getArtistName(),
                        dataKorea.get(position).getAlbumName(), dataKorea.get(position).getSongHighPath(),
                        dataKorea.get(position).getSongLowPath(), dataKorea.get(position).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataKorea.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataKorea.get(i).getSongId()),
                            dataKorea.get(i).getSongName(), dataKorea.get(i).getArtistName(),
                            dataKorea.get(i).getAlbumName(), dataKorea.get(i).getSongHighPath(),
                            dataKorea.get(i).getSongLowPath(), dataKorea.get(i).getAlbumImage());
                    mediaItems.add(mI);
                }

                updateNowPlayingMediaItem(mediaItems, position);

            }
        });*/

        rv_thai.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_thai, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }
                playlistManager = App.getPlaylistManager();

                Hawk.init(App.getApplication()).build();
                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(dataThaiList.get(position).id));
                    call.enqueue(new Callback<LowQualityDTO>() {

                        @Override
                        public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                            mediaItems = new LinkedList<>();
                            MediaItem mediaItem = new MediaItem(String.valueOf(dataThaiList.get(position).id),
                                    dataThaiList.get(position).song_name, dataThaiList.get(position).artist_name,
                                    dataThaiList.get(position).album_name, DOMAIN_TEST + dataThaiList.get(position).song_high_path,
                                    DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + dataThaiList.get(position).album_image);
                            mediaItems.add(mediaItem);

                            playlistManager.setParameters(mediaItems, 0);
                            playlistManager.setId(20);
                            playlistManager.play(0, false);

                        }

                        @Override
                        public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                            Log.e("asdf", "error in low qty " + t.getMessage());
                        }
                    });
                } else {
                    mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(dataThaiList.get(position).id),
                            dataThaiList.get(position).song_name, dataThaiList.get(position).artist_name,
                            dataThaiList.get(position).album_name, DOMAIN_TEST + dataThaiList.get(position).song_high_path,
                            DOMAIN_TEST + dataThaiList.get(position).song_low_path, DOMAIN_TEST + dataThaiList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);
                }
                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataThaiList.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataThaiList.get(i).id),
                            dataThaiList.get(i).song_name, dataThaiList.get(i).artist_name,
                            dataThaiList.get(i).album_name, DOMAIN_TEST + dataThaiList.get(i).song_high_path,
                            DOMAIN_TEST + dataThaiList.get(i).song_low_path, DOMAIN_TEST + dataThaiList.get(i).album_image);
                    mediaItems.add(mI);
                }

                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*rv_thai.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                playlistManager = App.getPlaylistManager();

                mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(dataThai.get(position).getSongId()),
                        dataThai.get(position).getSongName(), dataThai.get(position).getArtistName(),
                        dataThai.get(position).getAlbumName(), dataThai.get(position).getSongHighPath(),
                        dataThai.get(position).getSongLowPath(), dataThai.get(position).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataThai.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataThai.get(i).getSongId()),
                            dataThai.get(i).getSongName(), dataThai.get(i).getArtistName(),
                            dataThai.get(i).getAlbumName(), dataThai.get(i).getSongHighPath(),
                            dataThai.get(i).getSongLowPath(), dataThai.get(i).getAlbumImage());
                    mediaItems.add(mI);
                }

                updateNowPlayingMediaItem(mediaItems, position);

            }
        });*/

        rv_japan.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_japan, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }
                playlistManager = App.getPlaylistManager();

                Hawk.init(App.getApplication()).build();
                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(dataJapanList.get(position).id));
                    call.enqueue(new Callback<LowQualityDTO>() {

                        @Override
                        public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                            mediaItems = new LinkedList<>();
                            MediaItem mediaItem = new MediaItem(String.valueOf(dataJapanList.get(position).id),
                                    dataJapanList.get(position).song_name, dataJapanList.get(position).artist_name,
                                    dataJapanList.get(position).album_name, DOMAIN_TEST + dataJapanList.get(position).song_high_path,
                                    DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + dataJapanList.get(position).album_image);
                            mediaItems.add(mediaItem);

                            playlistManager.setParameters(mediaItems, 0);
                            playlistManager.setId(20);
                            playlistManager.play(0, false);

                        }

                        @Override
                        public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                            Log.e("asdf", "error in low qty " + t.getMessage());
                        }
                    });
                } else {

                    mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(dataJapanList.get(position).id),
                            dataJapanList.get(position).song_name, dataJapanList.get(position).artist_name,
                            dataJapanList.get(position).album_name, DOMAIN_TEST + dataJapanList.get(position).song_high_path,
                            DOMAIN_TEST + dataJapanList.get(position).song_low_path, DOMAIN_TEST + dataJapanList.get(position).album_image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);

                }

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataJapanList.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataJapanList.get(i).id),
                            dataJapanList.get(i).song_name, dataJapanList.get(i).artist_name,
                            dataJapanList.get(i).album_name, DOMAIN_TEST + dataJapanList.get(i).song_high_path,
                            DOMAIN_TEST + dataJapanList.get(i).song_low_path, DOMAIN_TEST + dataJapanList.get(i).album_image);
                    mediaItems.add(mI);
                }

                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*rv_japan.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                playlistManager = App.getPlaylistManager();

                mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(dataJapan.get(position).getSongId()),
                        dataJapan.get(position).getSongName(), dataJapan.get(position).getArtistName(),
                        dataJapan.get(position).getAlbumName(), dataJapan.get(position).getSongHighPath(),
                        dataJapan.get(position).getSongLowPath(), dataJapan.get(position).getAlbumImage());
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(20);
                playlistManager.play(0, false);

                mediaItems = new LinkedList<>();
                for (int i = 0; i < dataJapan.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(dataJapan.get(i).getSongId()),
                            dataJapan.get(i).getSongName(), dataJapan.get(i).getArtistName(),
                            dataJapan.get(i).getAlbumName(), dataJapan.get(i).getSongHighPath(),
                            dataJapan.get(i).getSongLowPath(), dataJapan.get(i).getAlbumImage());
                    mediaItems.add(mI);
                }

                updateNowPlayingMediaItem(mediaItems, position);

            }
        });*/

    }

    private void loadWeek(int page) {

        Call<DiscoverNewThisWeek> call = requestAPI.getWeekList(page);
        call.enqueue(new Callback<DiscoverNewThisWeek>() {
            @Override
            public void onResponse(Call<DiscoverNewThisWeek> call, Response<DiscoverNewThisWeek> response) {
                if (response.isSuccessful()) {

                    if (response.body().weekdata.data.size() == 0) {
                        tv_new_this_week.setVisibility(View.GONE);
                        rv_new_this_week.setVisibility(View.GONE);
                    } else {
                        tv_new_this_week.setVisibility(View.VISIBLE);
                        rv_new_this_week.setVisibility(View.VISIBLE);

                        currentPageWeek = response.body().weekdata.current_page;
                        lastPageWeek = response.body().weekdata.last_page;

                        dataWeekList.addAll(response.body().weekdata.data);
                        newThisWeekAdapter.notifyDataChanged();
                    }

                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverNewThisWeek> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMoreWeek(int index) {

        //add loading progress view
        dataWeekList.add(new DiscoverNewThisWeek.Data("load"));
        newThisWeekAdapter.notifyItemInserted(dataWeekList.size() - 1);

        Call<DiscoverNewThisWeek> call = requestAPI.getWeekList(index);
        call.enqueue(new Callback<DiscoverNewThisWeek>() {
            @Override
            public void onResponse(Call<DiscoverNewThisWeek> call, Response<DiscoverNewThisWeek> response) {
                if (response.isSuccessful()) {

                    currentPageWeek = response.body().weekdata.current_page;
                    lastPageWeek = response.body().weekdata.last_page;

                    //remove loading view
                    dataWeekList.remove(dataWeekList.size() - 1);

                    List<DiscoverNewThisWeek.Data> result = response.body().weekdata.data;
                    if (currentPageWeek <= lastPageWeek) {
                        //add loaded data
                        dataWeekList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        newThisWeekAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    newThisWeekAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverNewThisWeek> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadWeek(int page) {
        isLoadingWeek = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MConstants.MAIN_URL)
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<DiscoverNewThisWeek> call = request.getWeekList(page);
        call.enqueue(new Callback<DiscoverNewThisWeek>() {
            @Override
            public void onResponse(Call<DiscoverNewThisWeek> call, Response<DiscoverNewThisWeek> response) {

                ll_data.setVisibility(View.VISIBLE);

                currentPageWeek = response.body().getWeekdata().getCurrentPage();
                lastPageWeek = response.body().getWeekdata().getLastPage();

                final DiscoverNewThisWeek.Data[] datums2 = new DiscoverNewThisWeek.Data[response.body().getWeekdata().getData().size()];

                int scrollPosition = 0;
                scrollPosition = dataWeek.size();
                dataWeekTmp = new ArrayList<>();
                for (int i = 0; i < response.body().getWeekdata().getData().size(); i++) {
                    datums2[i] = (response.body().getWeekdata().getData().get(i));
                    dataWeekTmp.add(datums2[i]);
                    dataWeek.add(datums2[i]);
                }
                rv_new_this_week.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
//                rv_new_this_week.setLayoutManager(new LinearLayoutManager(getContext()));

                if (currentPageWeek == 1) {
                    newThisWeekAdapter = new NewThisWeekAdapter(dataWeekTmp, getActivity());
//                    newThisWeekAdapter.setOnLoadMoreListener(DiscoverFrag.this, rv_new_this_week);
                    newThisWeekAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                        @Override
                        public void onLoadMoreRequested() {
                            if (currentPageWeek >= lastPageWeek) {
//                              pullToRefreshAdapter.loadMoreEnd();//default visible
                                newThisWeekAdapter.loadMoreEnd(true);//true is gone,false is visible
                            } else {
                                if (!isLoadingWeek) {
                                    loadWeek(currentPageWeek + 1);

                                }
//                                newThisWeekAdapter.addData(dataMyanmarList);
//                                newThisWeekAdapter.loadMoreComplete();
                            }
                        }
                    }, rv_new_this_week);
                    newThisWeekAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                    newThisWeekAdapter.setEnableLoadMore(true);
                    rv_new_this_week.setAdapter(newThisWeekAdapter);
                    newThisWeekAdapter.setLoadMoreView(new CustomLoadMoreView());
//                newThisWeekAdapter.setAutoLoadMoreSize(3);
                } else {
                    newThisWeekAdapter.addData(dataWeekTmp);
                    rv_new_this_week.scrollToPosition(scrollPosition - 2);
                    newThisWeekAdapter.loadMoreComplete();
                }
                isLoadingWeek = false;
            }

            @Override
            public void onFailure(Call<DiscoverNewThisWeek> call, Throwable t) {
                isLoadingWeek = false;
//                processDialog.dismiss();
            }
        });
    }*/

    private void loadMyanmar(int page) {
        Call<DiscoverMyanmar> call = requestAPI.getMyanmarList(page);
        call.enqueue(new Callback<DiscoverMyanmar>() {
            @Override
            public void onResponse(Call<DiscoverMyanmar> call, Response<DiscoverMyanmar> response) {
                if (response.isSuccessful()) {

                    if (response.body().paginate_data.data.size() == 0) {
                        tv_myanmar.setVisibility(View.GONE);
                        rv_myanmar.setVisibility(View.GONE);
                    } else {
                        tv_myanmar.setVisibility(View.VISIBLE);
                        rv_myanmar.setVisibility(View.VISIBLE);

                        currentPageMyanmar = response.body().paginate_data.current_page;
                        lastPageMyanmar = response.body().paginate_data.last_page;

                        dataMyanmarList.addAll(response.body().paginate_data.data);
                        myanmarAdapter.notifyDataChanged();
                    }

                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverMyanmar> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMoreMyanmar(int index) {

        //add loading progress view
        dataMyanmarList.add(new DiscoverMyanmar.Data("load"));
        myanmarAdapter.notifyItemInserted(dataMyanmarList.size() - 1);

        Call<DiscoverMyanmar> call = requestAPI.getMyanmarList(index);
        call.enqueue(new Callback<DiscoverMyanmar>() {
            @Override
            public void onResponse(Call<DiscoverMyanmar> call, Response<DiscoverMyanmar> response) {
                if (response.isSuccessful()) {

                    currentPageMyanmar = response.body().paginate_data.current_page;
                    lastPageMyanmar = response.body().paginate_data.last_page;

                    //remove loading view
                    dataMyanmarList.remove(dataMyanmarList.size() - 1);

                    List<DiscoverMyanmar.Data> result = response.body().paginate_data.data;
                    if (currentPageMyanmar <= lastPageMyanmar) {
                        //add loaded data
                        dataMyanmarList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        myanmarAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    myanmarAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverMyanmar> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadMyanmar(int page) {

        isLoadingMyanmar = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MConstants.MAIN_URL)
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<DiscoverMyanmar> call = request.getMyanmarList(page);
        call.enqueue(new Callback<DiscoverMyanmar>() {
            @Override
            public void onResponse(Call<DiscoverMyanmar> call, Response<DiscoverMyanmar> response) {

                ll_data.setVisibility(View.VISIBLE);

                currentPageMyanmar = response.body().getMyanmarData().getCurrentPage();
                lastPageMyanmar = response.body().getMyanmarData().getLastPage();

                final DiscoverMyanmar.Data[] datums2 = new DiscoverMyanmar.Data[response.body().getMyanmarData().getData().size()];

                int scrollPosition = 0;
                scrollPosition = dataMyanmarList.size();
                dataMyanmarListTmp = new ArrayList<>();
                for (int i = 0; i < response.body().getMyanmarData().getData().size(); i++) {
                    datums2[i] = (response.body().getMyanmarData().getData().get(i));
                    dataMyanmarListTmp.add(datums2[i]);
                    dataMyanmarList.add(datums2[i]);
                }
                Log.e("asdf", " datal : " + dataMyanmarList.size());
                rv_myanmar.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
//                rv_myanmar.setLayoutManager(new LinearLayoutManager(getContext()));

                if (currentPageMyanmar == 1) {
                    myanmarAdapter = new MyanmarAdapter(dataMyanmarListTmp, getActivity());
//                    myanmarAdapter.setOnLoadMoreListener(DiscoverFrag.this, rv_myanmar);
                    myanmarAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                        @Override
                        public void onLoadMoreRequested() {
                            if (currentPageMyanmar >= lastPageMyanmar) {
//                              pullToRefreshAdapter.loadMoreEnd();//default visible
                                myanmarAdapter.loadMoreEnd(true);//true is gone,false is visible
                            } else {
                                if (!isLoadingMyanmar) {
                                    loadMyanmar(currentPageMyanmar + 1);

                                }
//                                myanmarAdapter.addData(dataMyanmarList);
//                                myanmarAdapter.loadMoreComplete();
                            }
                        }
                    }, rv_myanmar);
                    myanmarAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                    myanmarAdapter.setEnableLoadMore(true);
                    rv_myanmar.setAdapter(myanmarAdapter);
                    myanmarAdapter.setLoadMoreView(new CustomLoadMoreView());
//                myanmarAdapter.setAutoLoadMoreSize(3);
                    Log.e("asdf", "if");
                } else {
                    Log.e("asdf", "else");
                    myanmarAdapter.addData(dataMyanmarListTmp);
                    rv_myanmar.scrollToPosition(scrollPosition - 2);
                    myanmarAdapter.loadMoreComplete();
                }
                isLoadingMyanmar = false;
                Log.e("asdf", "currentPage : " + currentPageMyanmar + " lastPage : " + lastPageMyanmar);
                Log.e("asdf", "datals size : " + dataMyanmarList.size() + " datatemp : " + dataMyanmarListTmp.size());
                Log.e("asdf", "scroll position : " + scrollPosition);
            }

            @Override
            public void onFailure(Call<DiscoverMyanmar> call, Throwable t) {
                isLoadingMyanmar = false;
//                processDialog.dismiss();
            }
        });
    }*/

    private void loadEnglish(int page) {
        Call<DiscoverEnglish> call = requestAPI.getEnglishList(page);
        call.enqueue(new Callback<DiscoverEnglish>() {
            @Override
            public void onResponse(Call<DiscoverEnglish> call, Response<DiscoverEnglish> response) {
                if (response.isSuccessful()) {

                    if (response.body().paginate_data.data.size() == 0) {
                        tv_english.setVisibility(View.GONE);
                        rv_english.setVisibility(View.GONE);
                    } else {
                        tv_english.setVisibility(View.VISIBLE);
                        rv_english.setVisibility(View.VISIBLE);

                        currentPageEnglish = response.body().paginate_data.current_page;
                        lastPageEnglish = response.body().paginate_data.last_page;

                        dataEnglishList.addAll(response.body().paginate_data.data);
                        englishAdapter.notifyDataChanged();
                    }
                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverEnglish> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMoreEnglish(int index) {

        //add loading progress view
        dataEnglishList.add(new DiscoverEnglish.Data("load"));
        englishAdapter.notifyItemInserted(dataEnglishList.size() - 1);

        Call<DiscoverEnglish> call = requestAPI.getEnglishList(index);
        call.enqueue(new Callback<DiscoverEnglish>() {
            @Override
            public void onResponse(Call<DiscoverEnglish> call, Response<DiscoverEnglish> response) {
                if (response.isSuccessful()) {

                    currentPageEnglish = response.body().paginate_data.current_page;
                    lastPageEnglish = response.body().paginate_data.last_page;

                    //remove loading view
                    dataEnglishList.remove(dataEnglishList.size() - 1);

                    List<DiscoverEnglish.Data> result = response.body().paginate_data.data;
                    if (currentPageEnglish <= lastPageEnglish) {
                        //add loaded data
                        dataEnglishList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        englishAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    englishAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverEnglish> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadEnglish(int page) {
        isLoadingEnglish = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MConstants.MAIN_URL)
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<DiscoverEnglish> call = request.getEnglishList(page);
        call.enqueue(new Callback<DiscoverEnglish>() {
            @Override
            public void onResponse(Call<DiscoverEnglish> call, Response<DiscoverEnglish> response) {

                ll_data.setVisibility(View.VISIBLE);

                currentPageEnglish = response.body().getEnglishData().getCurrentPage();
                lastPageEnglish = response.body().getEnglishData().getLastPage();

                final DiscoverEnglish.Data[] datums2 = new DiscoverEnglish.Data[response.body().getEnglishData().getData().size()];

                int scrollPosition = 0;
                scrollPosition = dataEnglish.size();
                dataEnglishTmp = new ArrayList<>();
                for (int i = 0; i < response.body().getEnglishData().getData().size(); i++) {
                    datums2[i] = (response.body().getEnglishData().getData().get(i));
                    dataEnglishTmp.add(datums2[i]);
                    dataEnglish.add(datums2[i]);
                }
                rv_english.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
//                rv_english.setLayoutManager(new LinearLayoutManager(getContext()));

                if (currentPageEnglish == 1) {
                    englishAdapter = new EnglishAdapter(dataEnglishTmp, getActivity());
                    englishAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                        @Override
                        public void onLoadMoreRequested() {
                            if (currentPageEnglish >= lastPageEnglish) {
//                              pullToRefreshAdapter.loadMoreEnd();//default visible
                                englishAdapter.loadMoreEnd(true);//true is gone,false is visible
                            } else {
                                if (!isLoadingEnglish) {
                                    loadEnglish(currentPageEnglish + 1);

                                }
                            }
                        }
                    }, rv_english);
                    englishAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                    englishAdapter.setEnableLoadMore(true);
                    rv_english.setAdapter(englishAdapter);
                    englishAdapter.setLoadMoreView(new CustomLoadMoreView());
//                englishAdapter.setAutoLoadMoreSize(3);
                } else {
                    englishAdapter.addData(dataEnglishTmp);
                    rv_english.scrollToPosition(scrollPosition - 2);
                    englishAdapter.loadMoreComplete();
                }
                isLoadingEnglish = false;
            }

            @Override
            public void onFailure(Call<DiscoverEnglish> call, Throwable t) {
                isLoadingEnglish = false;
//                processDialog.dismiss();
            }
        });
    }*/

    private void loadKorea(int page) {
        Call<DiscoverKorea> call = requestAPI.getKoreaList(page);
        call.enqueue(new Callback<DiscoverKorea>() {
            @Override
            public void onResponse(Call<DiscoverKorea> call, Response<DiscoverKorea> response) {
                if (response.isSuccessful()) {

                    if (response.body().paginate_data.data.size() == 0) {
                        tv_korea.setVisibility(View.GONE);
                        rv_korea.setVisibility(View.GONE);
                    } else {
                        tv_korea.setVisibility(View.VISIBLE);
                        rv_korea.setVisibility(View.VISIBLE);

                        currentPageKorea = response.body().paginate_data.current_page;
                        lastPageKorea = response.body().paginate_data.last_page;

                        dataKoreaList.addAll(response.body().paginate_data.data);
                        koreaAdapter.notifyDataChanged();
                    }

                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverKorea> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMoreKorea(int index) {

        //add loading progress view
        dataKoreaList.add(new DiscoverKorea.Data("load"));
        koreaAdapter.notifyItemInserted(dataKoreaList.size() - 1);

        Call<DiscoverKorea> call = requestAPI.getKoreaList(index);
        call.enqueue(new Callback<DiscoverKorea>() {
            @Override
            public void onResponse(Call<DiscoverKorea> call, Response<DiscoverKorea> response) {
                if (response.isSuccessful()) {

                    currentPageKorea = response.body().paginate_data.current_page;
                    lastPageKorea = response.body().paginate_data.last_page;

                    //remove loading view
                    dataKoreaList.remove(dataKoreaList.size() - 1);

                    List<DiscoverKorea.Data> result = response.body().paginate_data.data;
                    if (currentPageKorea <= lastPageKorea) {
                        //add loaded data
                        dataKoreaList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        koreaAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    koreaAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverKorea> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadKorea(int page) {
        isLoadingKorea = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MConstants.MAIN_URL)
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<DiscoverKorea> call = request.getKoreaList(page);
        call.enqueue(new Callback<DiscoverKorea>() {
            @Override
            public void onResponse(Call<DiscoverKorea> call, Response<DiscoverKorea> response) {

                ll_data.setVisibility(View.VISIBLE);

                currentPageKorea = response.body().getKoreaData().getCurrentPage();
                lastPageKorea = response.body().getKoreaData().getLastPage();

                final DiscoverKorea.Data[] datums2 = new DiscoverKorea.Data[response.body().getKoreaData().getData().size()];

                int scrollPosition = 0;
                scrollPosition = dataKorea.size();
                dataKoreaTmp = new ArrayList<>();
                for (int i = 0; i < response.body().getKoreaData().getData().size(); i++) {
                    datums2[i] = (response.body().getKoreaData().getData().get(i));
                    dataKoreaTmp.add(datums2[i]);
                    dataKorea.add(datums2[i]);
                }
                rv_korea.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
//                rv_korea.setLayoutManager(new LinearLayoutManager(getContext()));

                if (currentPageKorea == 1) {
                    koreaAdapter = new KoreaAdapter(dataKoreaTmp, getActivity());
                    koreaAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                        @Override
                        public void onLoadMoreRequested() {
                            if (currentPageKorea >= lastPageKorea) {
//                              pullToRefreshAdapter.loadMoreEnd();//default visible
                                koreaAdapter.loadMoreEnd(true);//true is gone,false is visible
                            } else {
                                if (!isLoadingKorea) {
                                    loadKorea(currentPageKorea + 1);

                                }
                            }
                        }
                    }, rv_korea);
                    koreaAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                    koreaAdapter.setEnableLoadMore(true);
                    rv_korea.setAdapter(koreaAdapter);
                    koreaAdapter.setLoadMoreView(new CustomLoadMoreView());
//                koreaAdapter.setAutoLoadMoreSize(3);
                } else {
                    koreaAdapter.addData(dataKoreaTmp);
                    rv_korea.scrollToPosition(scrollPosition - 2);
                    koreaAdapter.loadMoreComplete();
                }
                isLoadingKorea = false;
            }

            @Override
            public void onFailure(Call<DiscoverKorea> call, Throwable t) {
                isLoadingKorea = false;
//                processDialog.dismiss();
            }
        });
    }*/

    private void loadThai(int page) {
        Call<DiscoverThai> call = requestAPI.getThaiList(page);
        call.enqueue(new Callback<DiscoverThai>() {
            @Override
            public void onResponse(Call<DiscoverThai> call, Response<DiscoverThai> response) {
                if (response.isSuccessful()) {

                    if (response.body().paginate_data.data.size() == 0) {
                        tv_thai.setVisibility(View.GONE);
                        rv_thai.setVisibility(View.GONE);
                    } else {
                        tv_thai.setVisibility(View.VISIBLE);
                        rv_thai.setVisibility(View.VISIBLE);

                        currentPageThai = response.body().paginate_data.current_page;
                        lastPageThai = response.body().paginate_data.last_page;

                        dataThaiList.addAll(response.body().paginate_data.data);
                        thaiAdapter.notifyDataChanged();
                    }

                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverThai> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMoreThai(int index) {

        //add loading progress view
        dataThaiList.add(new DiscoverThai.Data("load"));
        thaiAdapter.notifyItemInserted(dataThaiList.size() - 1);

        Call<DiscoverThai> call = requestAPI.getThaiList(index);
        call.enqueue(new Callback<DiscoverThai>() {
            @Override
            public void onResponse(Call<DiscoverThai> call, Response<DiscoverThai> response) {
                if (response.isSuccessful()) {

                    currentPageThai = response.body().paginate_data.current_page;
                    lastPageThai = response.body().paginate_data.last_page;

                    //remove loading view
                    dataThaiList.remove(dataThaiList.size() - 1);

                    List<DiscoverThai.Data> result = response.body().paginate_data.data;
                    if (currentPageThai <= lastPageThai) {
                        //add loaded data
                        dataThaiList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        thaiAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    thaiAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverThai> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadThai(int page) {
        isLoadingThai = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MConstants.MAIN_URL)
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<DiscoverThai> call = request.getThaiList(page);
        call.enqueue(new Callback<DiscoverThai>() {
            @Override
            public void onResponse(Call<DiscoverThai> call, Response<DiscoverThai> response) {

                ll_data.setVisibility(View.VISIBLE);

                currentPageThai = response.body().getThaiData().getCurrentPage();
                lastPageThai = response.body().getThaiData().getLastPage();

                final DiscoverThai.Data[] datums2 = new DiscoverThai.Data[response.body().getThaiData().getData().size()];

                int scrollPosition = 0;
                scrollPosition = dataThai.size();
                dataThaiTmp = new ArrayList<>();
                for (int i = 0; i < response.body().getThaiData().getData().size(); i++) {
                    datums2[i] = (response.body().getThaiData().getData().get(i));
                    dataThaiTmp.add(datums2[i]);
                    dataThai.add(datums2[i]);
                }
                rv_thai.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
//                rv_thai.setLayoutManager(new LinearLayoutManager(getContext()));

                if (currentPageThai == 1) {
                    thaiAdapter = new ThaiAdapter(dataThaiTmp, getActivity());
                    thaiAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                        @Override
                        public void onLoadMoreRequested() {
                            if (currentPageThai >= lastPageThai) {
//                              pullToRefreshAdapter.loadMoreEnd();//default visible
                                thaiAdapter.loadMoreEnd(true);//true is gone,false is visible
                            } else {
                                if (!isLoadingThai) {
                                    loadThai(currentPageThai + 1);

                                }
                            }
                        }
                    }, rv_thai);
                    thaiAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                    thaiAdapter.setEnableLoadMore(true);
                    rv_thai.setAdapter(thaiAdapter);
                    thaiAdapter.setLoadMoreView(new CustomLoadMoreView());
//                thaiAdapter.setAutoLoadMoreSize(3);
                } else {
                    thaiAdapter.addData(dataThaiTmp);
                    rv_thai.scrollToPosition(scrollPosition - 2);
                    thaiAdapter.loadMoreComplete();
                }
                isLoadingThai = false;
            }

            @Override
            public void onFailure(Call<DiscoverThai> call, Throwable t) {
                isLoadingThai = false;
//                processDialog.dismiss();
            }
        });
    }*/

    private void loadJapan(int page) {
        Call<DiscoverJapan> call = requestAPI.getJapanList(page);
        call.enqueue(new Callback<DiscoverJapan>() {
            @Override
            public void onResponse(Call<DiscoverJapan> call, Response<DiscoverJapan> response) {
                if (response.isSuccessful()) {

                    if (response.body().paginate_data.data.size() == 0) {
                        tv_japan.setVisibility(View.GONE);
                        rv_japan.setVisibility(View.GONE);
                    } else {
                        tv_japan.setVisibility(View.VISIBLE);
                        rv_japan.setVisibility(View.VISIBLE);

                        currentPageJapan = response.body().paginate_data.current_page;
                        lastPageJapan = response.body().paginate_data.last_page;

                        dataJapanList.addAll(response.body().paginate_data.data);
                        japanAdapter.notifyDataChanged();
                    }
                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverJapan> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMoreJapan(int index) {

        //add loading progress view
        dataJapanList.add(new DiscoverJapan.Data("load"));
        japanAdapter.notifyItemInserted(dataJapanList.size() - 1);

        Call<DiscoverJapan> call = requestAPI.getJapanList(index);
        call.enqueue(new Callback<DiscoverJapan>() {
            @Override
            public void onResponse(Call<DiscoverJapan> call, Response<DiscoverJapan> response) {
                if (response.isSuccessful()) {

                    currentPageJapan = response.body().paginate_data.current_page;
                    lastPageJapan = response.body().paginate_data.last_page;

                    //remove loading view
                    dataJapanList.remove(dataJapanList.size() - 1);

                    List<DiscoverJapan.Data> result = response.body().paginate_data.data;
                    if (currentPageJapan <= lastPageJapan) {
                        //add loaded data
                        dataJapanList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        japanAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    japanAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<DiscoverJapan> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }

    /*private void loadJapan(int page) {
        isLoadingJapan = true;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MConstants.MAIN_URL)
                .build();
        RequestInterface2 request = retrofit.create(RequestInterface2.class);
        Call<DiscoverJapan> call = request.getJapanList(page);
        call.enqueue(new Callback<DiscoverJapan>() {
            @Override
            public void onResponse(Call<DiscoverJapan> call, Response<DiscoverJapan> response) {

                ll_data.setVisibility(View.VISIBLE);

                currentPageJapan = response.body().getJapanData().getCurrentPage();
                lastPageJapan = response.body().getJapanData().getLastPage();

                final DiscoverJapan.Data[] datums2 = new DiscoverJapan.Data[response.body().getJapanData().getData().size()];

                int scrollPosition = 0;
                scrollPosition = dataJapan.size();
                dataJapanTmp = new ArrayList<>();
                for (int i = 0; i < response.body().getJapanData().getData().size(); i++) {
                    datums2[i] = (response.body().getJapanData().getData().get(i));
                    dataJapanTmp.add(datums2[i]);
                    dataJapan.add(datums2[i]);
                }
                rv_japan.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
//                rv_japan.setLayoutManager(new LinearLayoutManager(getContext()));

                if (currentPageJapan == 1) {
                    japanAdapter = new JapanAdapter(dataJapanTmp, getActivity());
                    japanAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                        @Override
                        public void onLoadMoreRequested() {
                            if (currentPageJapan >= lastPageJapan) {
//                              pullToRefreshAdapter.loadMoreEnd();//default visible
                                japanAdapter.loadMoreEnd(true);//true is gone,false is visible
                            } else {
                                if (!isLoadingJapan) {
                                    loadJapan(currentPageJapan + 1);

                                }
                            }
                        }
                    }, rv_japan);
                    japanAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                    japanAdapter.setEnableLoadMore(true);
                    rv_japan.setAdapter(japanAdapter);
                    japanAdapter.setLoadMoreView(new CustomLoadMoreView());
//                japanAdapter.setAutoLoadMoreSize(3);
                } else {
                    japanAdapter.addData(dataJapanTmp);
                    rv_japan.scrollToPosition(scrollPosition - 2);
                    japanAdapter.loadMoreComplete();
                }
                isLoadingJapan = false;
            }

            @Override
            public void onFailure(Call<DiscoverJapan> call, Throwable t) {
                isLoadingJapan = false;
//                processDialog.dismiss();
            }
        });
    }*/

    /*private void updateNowPlayingMediaItem(LinkedList<MediaItem> mediaItems, int nowPlayingIndex) {
        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST))
            Hawk.delete(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
        Hawk.put(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST, mediaItems);

        if (Hawk.contains(MConstants.NOW_PLAYING_INDEX))
            Hawk.delete(MConstants.NOW_PLAYING_INDEX);
        Hawk.put(MConstants.NOW_PLAYING_INDEX, nowPlayingIndex);

        if (getActivity().findViewById(R.id.fl_mini_player_info).getVisibility() == View.GONE) {
            getActivity().findViewById(R.id.fl_mini_player_info).setVisibility(View.VISIBLE);
        }
        if (!MUtility.hidePlayScreen())
            startActivity(NowPlayingActivity.newIntent(getActivity()));
    }*/

    private void showProcessDialog() {
        processDialog = new MaterialDialog.Builder(getActivity())
                .content("Loading...")
                .theme(Theme.DARK)
                .cancelable(false)
                .progress(true, 0)
                .show();
    }
}
