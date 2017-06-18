package music.streaming.dev.snsh.musicstreaming.frag;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
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

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.ArtistDetailsAlbumsAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.ArtistDetails;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class ArtistDetailsFrag extends Fragment implements ObservableScrollViewCallbacks {

    private String pass_Id;
    private String pass_Name;
    private String pass_Image;
    //    private String pass_artist_name;
    private String pass_album_name;
    private RecyclerView rv_related_albums;
    private ArtistDetailsAlbumsAdapter artistDetailsAlbumsAdapter;
    private ImageView iv_photo;
    private ObservableScrollView mScrollView;

    private TextView tv_header, tv_title, tv_info, tv_title2;
    private ArrayList<ArtistDetails.Albums_data> dataAlbum = new ArrayList<>();
    private ArtistDetails.Albums_data[] datums;

    public ArtistDetailsFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);


        Bundle bundle = getArguments();
        if (bundle != null) {
            pass_Id = bundle.getString(MConstants.PASS_ID);
            pass_Name = bundle.getString(MConstants.PASS_NAME);
            pass_Image = bundle.getString(MConstants.PASS_IMAGE);

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(pass_Name);

            Hawk.init(getActivity()).build();
            if (Hawk.contains(MConstants.TITLE2)) {
                Hawk.delete(MConstants.TITLE2);
            }
            Hawk.put(MConstants.TITLE2, pass_Name);
        }

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        toolbar.setTitle(pass_Name);

//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

       /* CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Zawgyi-One.ttf");
        collapsingToolbarLayout.setExpandedTitleTypeface(typeface);
        collapsingToolbarLayout.setCollapsedTitleTypeface(typeface);*/

        init(view);
        setupListener();
        loadJSON();

    }

    private void setupListener() {
        tv_header.setText(pass_Name);
        Picasso.with(getContext())
                .load(pass_Image)
                .into(iv_photo);

        rv_related_albums.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
//                Toast.makeText(activity, Integer.toString(position), Toast.LENGTH_LONG).show();

                String pass_Id = String.valueOf(dataAlbum.get(position).id);
                String name = dataAlbum.get(position).album_name;
                String title = dataAlbum.get(position).album_title;
                String info = dataAlbum.get(position).album_info;
                String image = DOMAIN_TEST + dataAlbum.get(position).album_image;
                pass_album_name = dataAlbum.get(position).album_name;

                Bundle bundle = new Bundle();
                bundle.putString(MConstants.PASS_ID, pass_Id);
                bundle.putString(MConstants.PASS_NAME, name);
                bundle.putString(MConstants.PASS_TITLE, title);
                bundle.putString(MConstants.PASS_INFO, info);
                bundle.putString(MConstants.PASS_IMAGE, image);
//                bundle.putString(MConstants.PASS_ARTIST_NAME, pass_artist_name);
                bundle.putString(MConstants.PASS_ALBUM_NAME, pass_album_name);
//                Intent intent = new Intent(getActivity(), AlbumActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);

                AlbumDetailsFrag albumDetails = new AlbumDetailsFrag();
                albumDetails.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager()
                        .beginTransaction();
                fragmentTransaction.add(R.id.fl_body, albumDetails);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack("AlbumDetailsFrag");
                fragmentTransaction.commit();

                mScrollView.smoothScrollTo(0, 0);
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
    }

    private void loadJSON() {

        dataAlbum = new ArrayList<>();
        RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);

        Call<ArtistDetails> callAlbums = request.getArtistDetails(Integer.parseInt(pass_Id));
        callAlbums.enqueue(new Callback<ArtistDetails>() {
            @Override
            public void onResponse(Call<ArtistDetails> call, Response<ArtistDetails> response) {

                tv_title.setText(response.body().artists_data.artist_title);
                tv_title2.setText("Albums");
                tv_info.setText(response.body().artists_data.artist_info);
//                pass_artist_name = response.body().artists_data.artist_name;

                datums = new ArtistDetails.Albums_data[response.body().albums_data.size()];
                for (int i = 0; i < response.body().albums_data.size(); i++) {
                    datums[i] = (response.body().albums_data.get(i));
                    dataAlbum.add(datums[i]);
                }
                rv_related_albums.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
                artistDetailsAlbumsAdapter = new ArtistDetailsAlbumsAdapter(dataAlbum, getActivity(), pass_Name);
                artistDetailsAlbumsAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
//        personalPlaylistSongAdapter.setAutoLoadMoreSize(3);
                rv_related_albums.setAdapter(artistDetailsAlbumsAdapter);
//        mCurrentCounter = personalPlaylistSongAdapter.getData().size();
            }

            @Override
            public void onFailure(Call<ArtistDetails> call, Throwable t) {

            }
        });
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