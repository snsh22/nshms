package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.AlbumDetailsAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.AlbumDetails;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
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

public class AlbumDetailsFrag extends Fragment implements ObservableScrollViewCallbacks {

    private String pass_Id;
    private String pass_Name, pass_Title, pass_Info, pass_Image, pass_album_name;
//    private String pass_artist_name;

    private RecyclerView rv_related_albums;
    private AlbumDetailsAdapter albumDetailsAdapter;
    private ImageView iv_photo;
    private ObservableScrollView mScrollView;

//    private LinearLayout ll_info, ll_data;

    private MaterialDialog processDialog;

    private TextView tv_header, tv_title, tv_info, tv_title2;
    private ArrayList<AlbumDetails.Song_data> datumArrayList = new ArrayList<>();
    private AlbumDetails.Song_data[] datums;

    private PlaylistManager playlistManager;

    public AlbumDetailsFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.ALBUM_DETAILS)) {
            Hawk.delete(MConstants.ALBUM_DETAILS);
        }
        Hawk.put(MConstants.ALBUM_DETAILS, true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            pass_Id = bundle.getString(MConstants.PASS_ID);
            pass_Name = bundle.getString(MConstants.PASS_NAME);
            pass_Title = bundle.getString(MConstants.PASS_TITLE);
            pass_Info = bundle.getString(MConstants.PASS_INFO);
            pass_Image = bundle.getString(MConstants.PASS_IMAGE);
//            pass_artist_name = bundle.getString(MConstants.PASS_ARTIST_NAME);
            pass_album_name = bundle.getString(MConstants.PASS_ALBUM_NAME);

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(pass_Name);

            Hawk.init(getActivity()).build();
            if (Hawk.contains(MConstants.TITLE3)) {
                Hawk.delete(MConstants.TITLE3);
            }
            Hawk.put(MConstants.TITLE3, pass_Name);
        }

        showProcessDialog();

        playlistManager = App.getPlaylistManager();

        init(view);
        setupListener();
        loadJSON();
    }

    private void setupListener() {
        rv_related_albums.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {

                rvClick(position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                super.onItemChildClick(adapter, view, position);
                switch (view.getId()) {
                    case R.id.ib_song_detail:
                        /*PopupMenu popup = new PopupMenu(getActivity(), rv_related_albums.getChildAt(position).findViewById(R.id.ib_song_detail));
                        popup.getMenuInflater().inflate(R.menu.activity_queue_song_detail, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_add_to_playlist:
//                                        Toast.makeText(AlbumActivity.this, "aaa" + String.valueOf(datumArrayList.get(position).getSongId()), Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt(MConstants.PASS_FLAG, 0);
                                        bundle.putString(MConstants.PASS_ID, String.valueOf(datumArrayList.get(position).getSongId()));
                                        Intent intent = new Intent(getActivity(), PersonalPlaylistNameActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
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
    }

    private void init(View view) {
        tv_header = (TextView) view.findViewById(R.id.tv_header);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title2 = (TextView) view.findViewById(R.id.tv_title2);
        tv_info = (TextView) view.findViewById(R.id.tv_info);
        rv_related_albums = (RecyclerView) view.findViewById(R.id.rv_related_albums);
        iv_photo = (ImageView) view.findViewById(R.id.iv_photo);

        mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        tv_header.setText(pass_Name);
        tv_title.setText(pass_Title);
        tv_title2.setText("Songs");
        tv_info.setText(pass_Info);

        Picasso.with(getContext())
                .load(pass_Image)
                .placeholder(R.drawable.placeholder_album)
                .into(iv_photo);
    }

    private void loadJSON() {

        RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);
        Call<AlbumDetails> callAlbums = request.getAlbumDetails(Integer.parseInt(pass_Id));
        callAlbums.enqueue(new Callback<AlbumDetails>() {
            @Override
            public void onResponse(Call<AlbumDetails> call, Response<AlbumDetails> response) {

                datums = new AlbumDetails.Song_data[response.body().song_data.size()];
                for (int i = 0; i < response.body().song_data.size(); i++) {
                    datums[i] = (response.body().song_data.get(i));
                    datumArrayList.add(datums[i]);
                }
                rv_related_albums.setLayoutManager(new LinearLayoutManager(getActivity()));

                albumDetailsAdapter = new AlbumDetailsAdapter(datumArrayList, getActivity(), pass_album_name);
                albumDetailsAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
//        albumDetailsAdapter.setAutoLoadMoreSize(3);
                processDialog.dismiss();
                rv_related_albums.setAdapter(albumDetailsAdapter);
//        mCurrentCounter = albumDetailsAdapter.getData().size();
                albumDetailsAdapter.setEmptyView(getLayoutInflater(null).inflate(R.layout.empty_album, (ViewGroup) rv_related_albums.getParent(), false));

            }

            @Override
            public void onFailure(Call<AlbumDetails> call, Throwable t) {
            }
        });
    }

    private void rvClick(final int position) {

        if (SendDurationToServer.isPlayingSong()) {
            SendDurationToServer.sendDuration();
        }
        playlistManager = App.getPlaylistManager();
        Hawk.init(App.getApplication()).build();
        boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);
        if (isLowQuality) {
            RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
            Call<LowQualityDTO> call = requestInterface2.getLowQualitySongUrl(String.valueOf(datumArrayList.get(position).id));
            call.enqueue(new Callback<LowQualityDTO>() {

                @Override
                public void onResponse(Call<LowQualityDTO> call, Response<LowQualityDTO> response) {
                    LinkedList<MediaItem> mediaItems = new LinkedList<>();
                    MediaItem mediaItem = new MediaItem(String.valueOf(datumArrayList.get(position).id),
                            datumArrayList.get(position).song_name, datumArrayList.get(position).artist_name,
                            pass_album_name, DOMAIN_TEST + datumArrayList.get(position).song_high_path,
                            DOMAIN_TEST + response.body().getPath().getSong_low_path(), pass_Image);
                    mediaItems.add(mediaItem);

                    playlistManager.setParameters(mediaItems, 0);
                    playlistManager.setId(20);
                    playlistManager.play(0, false);

                    mediaItems = new LinkedList<>();
                    for (int i = 0; i < datumArrayList.size(); i++) {
                        MediaItem mI = new MediaItem(String.valueOf(datumArrayList.get(i).id),
                                datumArrayList.get(i).song_name, datumArrayList.get(position).artist_name,
                                pass_album_name, DOMAIN_TEST + datumArrayList.get(i).song_high_path,
                                "", pass_Image);
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
            MediaItem mediaItem = new MediaItem(String.valueOf(datumArrayList.get(position).id),
                    datumArrayList.get(position).song_name, datumArrayList.get(position).artist_name,
                    pass_album_name, DOMAIN_TEST + datumArrayList.get(position).song_high_path,
                    "", pass_Image);
            mediaItems.add(mediaItem);

            playlistManager.setParameters(mediaItems, 0);
            playlistManager.setId(20);
            playlistManager.play(0, false);

            mediaItems = new LinkedList<>();
            for (int i = 0; i < datumArrayList.size(); i++) {
                MediaItem mI = new MediaItem(String.valueOf(datumArrayList.get(i).id),
                        datumArrayList.get(i).song_name, datumArrayList.get(i).artist_name,
                        pass_album_name, DOMAIN_TEST + datumArrayList.get(i).song_high_path,
                        "", pass_Image);
                mediaItems.add(mI);
            }

            MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
        }

        /*LinkedList<MediaItem> mediaItems;
        mediaItems = new LinkedList<>();
        MediaItem mediaItem = new MediaItem(String.valueOf(datumArrayList.get(position).getSongId()), datumArrayList.get(position).getSongName(), datumArrayList.get(position).getSongPath(), datumArrayList.get(position).getSongPhotoPath(), datumArrayList.get(position).getAlbumName(), datumArrayList.get(position).getArtistName(), datumArrayList.get(position).getLyricPath(), true);
        mediaItems.add(mediaItem);

        playlistManager.setParameters(mediaItems, 0);
        playlistManager.setId(20);
        playlistManager.play(0, false);

        mediaItems = new LinkedList<>();
        for (int i = 0; i < datumArrayList.size(); i++) {
            MediaItem mI = new MediaItem(String.valueOf(datumArrayList.get(i).getSongId()), datumArrayList.get(i).getSongName(), datumArrayList.get(i).getSongPath(), datumArrayList.get(i).getSongPhotoPath(), datumArrayList.get(i).getAlbumName(), datumArrayList.get(i).getArtistName(), datumArrayList.get(i).getLyricPath(), true);
            mediaItems.add(mI);
        }

        updateNowPlayingMediaItem(mediaItems, position);*/
    }

    private void showProcessDialog() {
        processDialog = new MaterialDialog.Builder(getActivity())
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        Aesthetic.get()
                .colorPrimary()
                .take(1)
                .subscribe(color -> {
                    // Use color (an integer)
                    float alpha = Math.min(1, (float) scrollY / iv_photo.getHeight());
                    getActivity().findViewById(R.id.toolbar).setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, color));
                    ViewHelper.setTranslationY(iv_photo, scrollY / 2);
                });
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}