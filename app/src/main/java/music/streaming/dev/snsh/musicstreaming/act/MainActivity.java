package music.streaming.dev.snsh.musicstreaming.act;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.orhanobut.hawk.Hawk;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.SearchAllAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.dto.SearchSong;
import music.streaming.dev.snsh.musicstreaming.frag.ArtistsListFrag;
import music.streaming.dev.snsh.musicstreaming.frag.DiscoverFrag;
import music.streaming.dev.snsh.musicstreaming.frag.DownloadFrag;
import music.streaming.dev.snsh.musicstreaming.frag.MiniPlayerInfoFragment;
import music.streaming.dev.snsh.musicstreaming.frag.PlaylistFrag;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class MainActivity extends AppCompatActivity {


    private SearchView searchView;
    private MenuItem searchMenuItem;
    //    private ObservableScrollView observableScrollView;
//    private int mParallaxImageHeight;


    private SearchAllAdapter searchAllAdapter;
    private ArrayList<SearchSong.Search_data> dataAlbum = new ArrayList<>();
    private SearchSong.Search_data[] datums;
    private PlaylistManager playlistManager;
    private LinkedList<MediaItem> mediaItems;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rl_search)
    RelativeLayout rl_search;

    @BindView(R.id.rv_search_result)
    RecyclerView rv_search_result;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aesthetic.attach(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /*if (Aesthetic.isFirstTime()) {
            Aesthetic.get()
                    .activityTheme(R.style.AppTheme)
                    .isDark(true)
                    .colorPrimaryRes(R.color.colorPrimary)
                    .colorAccentRes(R.color.md_amber)
                    .textColorPrimary(ContextCompat.getColor(this, android.R.color.primary_text_dark))
                    .textColorSecondary(ContextCompat.getColor(this, android.R.color.secondary_text_dark))
                    .textColorPrimaryInverse(ContextCompat.getColor(this, android.R.color.primary_text_light))
                    .textColorSecondaryInverse(ContextCompat.getColor(this, android.R.color.secondary_text_light))
                    .colorStatusBarAuto()
                    .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
                    .bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
                    .apply();
        }*/

        Hawk.init(this).build();
        if (Hawk.contains(MConstants.IS_LOGIN))
            Hawk.delete(MConstants.IS_LOGIN);
        Hawk.put(MConstants.IS_LOGIN, true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*Aesthetic.get()
                .colorPrimary()
                .take(1)
                .subscribe(color -> {
                    // Use color (an integer)
                    toolbar.setBackgroundColor(color);
                });*/

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
//        getToolBarTextView();

//        fl_mini_player
//
//                fl_mini_player_info = (FrameLayout) findViewById(R.id.fl_mini_player_info);
        MiniPlayerInfoFragment fragment = new MiniPlayerInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_mini_player, fragment)
                .commit();

//        fl_mini_player_info.setVisibility(View.GONE);
//        asdf

        /*Aesthetic.get()
                .isDark()
                .subscribe(
                        isDark -> {
                            if (isDark) {
                                toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp_toolbar);
                            } else {
                                toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
                                toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_secondary));
                                toolbar.setSubtitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_secondary));
                            }
                        });*/

        /*observableScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        observableScrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                Aesthetic.get()
                        .colorPrimary()
                        .take(1)
                        .subscribe(color -> {
                            // Use color (an integer)
//                            int baseColor = getResources().getColor(R.color.colorPrimary);
                            float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
                            toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, color));
                            ViewHelper.setTranslationY(findViewById(R.id.image), scrollY / 2);
                        });
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {

            }
        });*/

    }

    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_playlist).setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        search(searchView);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                rl_search.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                rl_search.setVisibility(View.GONE);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                    startActivity(SettingActivity.newIntent(this));
                else
                    onBackPressed();
                return true;
            case R.id.menu_add_playlist:
                return false;
            default:
                return false;
        }
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadSongSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadSongSearch(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Aesthetic.resume(this);
        /*Aesthetic.get()
                .colorPrimary()
                .take(1)
                .subscribe(color -> {
                    // Use color (an integer)
                    toolbar.setBackgroundColor(color);
                });*/
    }

    @Override
    protected void onPause() {
        Aesthetic.pause(this);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        switch (getSupportFragmentManager().getBackStackEntryCount()) {
            /*case 0:
                startActivity(SettingActivity.newIntent(this));
                break;*/
            case 1:
                Hawk.init(MainActivity.this).build();
                if (Hawk.contains(MConstants.TITLE1)) {
                    getSupportActionBar().setTitle(Hawk.get(MConstants.TITLE1, ""));
                }
                super.onBackPressed();
                break;
            case 2:
                Hawk.init(MainActivity.this).build();
                if (Hawk.contains(MConstants.TITLE2)) {
                    getSupportActionBar().setTitle(Hawk.get(MConstants.TITLE2, ""));
                }
                super.onBackPressed();
                break;
            case 3:
                Hawk.init(MainActivity.this).build();
                if (Hawk.contains(MConstants.TITLE3)) {
                    getSupportActionBar().setTitle(Hawk.get(MConstants.TITLE3, ""));
                }
                super.onBackPressed();
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    private void loadSongSearch(String searchString) {

        RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);

        Call<SearchSong> call = request.getSearchSong(searchString);
        call.enqueue(new Callback<SearchSong>() {
            @Override
            public void onResponse(Call<SearchSong> call, Response<SearchSong> response) {

                dataAlbum = new ArrayList<>();
                datums = new SearchSong.Search_data[response.body().getSearch_data().size()];
                for (int i = 0; i < response.body().getSearch_data().size(); i++) {
                    datums[i] = (response.body().getSearch_data().get(i));
                    dataAlbum.add(datums[i]);
                }

                addData();

            }

            @Override
            public void onFailure(Call<SearchSong> call, Throwable t) {
                Log.e("asdf", "error");
            }
        });
    }

    private void addData() {
//        rv_search_result = (RecyclerView) findViewById(R.id.rv_search_result);
        rv_search_result.setLayoutManager(new LinearLayoutManager(this));
        searchAllAdapter = new SearchAllAdapter(dataAlbum, this);
        searchAllAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        rv_search_result.setAdapter(searchAllAdapter);

        rv_search_result.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                playlistManager = App.getPlaylistManager();

                Hawk.init(App.getApplication()).build();
                boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
                if (isLowQuality) {
                    RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(dataAlbum.get(position).getId()));
                    call.enqueue(new retrofit2.Callback<LowQualityDTO>() {

                        @Override
                        public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                            mediaItems = new LinkedList<>();
                            MediaItem mediaItem = new MediaItem(String.valueOf(dataAlbum.get(position).getId()),
                                    dataAlbum.get(position).getSong_name(), dataAlbum.get(position).getArtist_name(),
                                    dataAlbum.get(position).getAlbum_name(), DOMAIN_TEST + dataAlbum.get(position).getSong_high_path(),
                                    DOMAIN_TEST + response.body().getPath().getSong_low_path(), DOMAIN_TEST + dataAlbum.get(position).getAlbum_image());
                            mediaItems.add(mediaItem);

                            playlistManager.setParameters(mediaItems, 0);
                            playlistManager.setId(20);
                            playlistManager.play(0, false);

                            MUtility.updateNowPlayingMediaItem(MainActivity.this, mediaItems, 0);

                        }

                        @Override
                        public void onFailure(Call<LowQualityDTO> call, Throwable t) {
                            Log.e("asdf", "error in low qty " + t.getMessage());
                        }
                    });
                } else {
                    mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(dataAlbum.get(position).getId()),
                            dataAlbum.get(position).getSong_name(), dataAlbum.get(position).getArtist_name(),
                            dataAlbum.get(position).getAlbum_name(), DOMAIN_TEST + dataAlbum.get(position).getSong_high_path(),
                            DOMAIN_TEST + dataAlbum.get(position).getSong_low_path(), DOMAIN_TEST + dataAlbum.get(position).getAlbum_image());
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);

                    MUtility.updateNowPlayingMediaItem(MainActivity.this, mediaItems, 0);
                }
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_discover:
                getSupportActionBar().setTitle("Discover");
                DiscoverFrag discoverFrag = new DiscoverFrag();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    discoverFrag.setEnterTransition(new Fade().setDuration(300));
                    discoverFrag.setExitTransition(new Fade().setDuration(100));
                    discoverFrag.setAllowEnterTransitionOverlap(true);
                    discoverFrag.setAllowReturnTransitionOverlap(true);
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_body, discoverFrag)
                        .commit();
                return true;
            case R.id.navigation_playlist:
                PlaylistFrag playlistFrag = new PlaylistFrag();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    playlistFrag.setEnterTransition(new Fade().setDuration(300));
                    playlistFrag.setExitTransition(new Fade().setDuration(100));
                    playlistFrag.setAllowEnterTransitionOverlap(true);
                    playlistFrag.setAllowReturnTransitionOverlap(true);
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_body, playlistFrag)
                        .commit();

                return true;
            case R.id.navigation_artist:
//                getSupportActionBar().setTitle("Artist");
                ArtistsListFrag artistsListFrag = new ArtistsListFrag();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    artistsListFrag.setEnterTransition(new Fade().setDuration(300));
                    artistsListFrag.setExitTransition(new Fade().setDuration(100));
                    artistsListFrag.setAllowEnterTransitionOverlap(true);
                    artistsListFrag.setAllowReturnTransitionOverlap(true);
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_body, artistsListFrag)
                        .commit();
                return true;
            case R.id.navigation_offline:
                DownloadFrag downloadFrag = new DownloadFrag();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    downloadFrag.setEnterTransition(new Fade().setDuration(300));
                    downloadFrag.setExitTransition(new Fade().setDuration(100));
                    downloadFrag.setAllowEnterTransitionOverlap(true);
                    downloadFrag.setAllowReturnTransitionOverlap(true);
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_body, downloadFrag)
                        .commit();
                return true;
        }
        return false;
    };

    private TextView getToolBarTextView() {
        TextView titleTextView = null;

        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(toolbar);

            Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Zawgyi-One.ttf");
            titleTextView.setTypeface(font);

        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return titleTextView;
    }
}
