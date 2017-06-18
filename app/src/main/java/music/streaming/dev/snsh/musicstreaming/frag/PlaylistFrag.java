package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.GenresAdapter;
import music.streaming.dev.snsh.musicstreaming.adpt.PersonalPlaylistAdapter;
import music.streaming.dev.snsh.musicstreaming.adpt.PlaylistAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistData;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistAdminPlaylist;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistGenreMusicType;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.NetworkHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistFrag extends Fragment {

    private RecyclerView rvPlaylist, rv_genre, rv_personal_playlist;
    private PlaylistAdapter playlistAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private GenresAdapter genersAdapter;
    private PersonalPlaylistAdapter personalPlaylistAdapter;

    private ArrayList<PlaylistAdminPlaylist.Admin_playlist_data> dataPlaylist = new ArrayList<>();
    private ArrayList<PlaylistGenreMusicType.Data> dataGenre = new ArrayList<>();

    //    CardView cv_my_playlist;
    private RelativeLayout rl_offline;
    private LinearLayout ll_data;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Playlist");
        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.TITLE1)) {
            Hawk.delete(MConstants.TITLE1);
        }
        Hawk.put(MConstants.TITLE1, "Playlist");

        rl_offline = (RelativeLayout) view.findViewById(R.id.rl_offline);
        ll_data = (LinearLayout) view.findViewById(R.id.ll_data);

        rl_offline.setVisibility(View.GONE);
        if (!NetworkHelper.isOnline(getContext())) {
            rl_offline.setVisibility(View.VISIBLE);
            ll_data.setVisibility(View.GONE);
        }

        rvPlaylist = (RecyclerView) view.findViewById(R.id.rvPlaylist);
        rv_genre = (RecyclerView) view.findViewById(R.id.rv_genre);
        rv_personal_playlist = (RecyclerView) view.findViewById(R.id.rv_personal_playlist);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);

        loadJSON();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                playlistAdapter.setEnableLoadMore(false);
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                playlistAdapter.setNewData(DataServer.getSampleData(PAGE_SIZE));
//                isErr = false;
//                mCurrentCounter = PAGE_SIZE;

//                playlistAdapter.setEnableLoadMore(true);
                    }
                }, 700);*/
                loadJSON();
            }
        });
        /*cv_my_playlist = (CardView) view.findViewById(R.id.cv_my_playlist);
        cv_my_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(MConstants.PASS_FLAG, 1);
                Intent intent = new Intent(getActivity(), PersonalPlaylistNameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });*/

        ArrayList<PersonalPlaylistData> personalPlaylistDataList = new ArrayList<>();
        PersonalPlaylistData personalPlaylistData = new PersonalPlaylistData(R.drawable.personalplaylist);
        personalPlaylistDataList.add(personalPlaylistData);
        personalPlaylistData = new PersonalPlaylistData(R.drawable.favourite);
        personalPlaylistDataList.add(personalPlaylistData);

        rv_personal_playlist.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        personalPlaylistAdapter = new PersonalPlaylistAdapter(personalPlaylistDataList, getActivity());
        personalPlaylistAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        rv_personal_playlist.setAdapter(personalPlaylistAdapter);
        rv_personal_playlist.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putInt(MConstants.PASS_FLAG, 1);
//                        Intent intent = new Intent(getActivity(), PersonalPlaylistNameActivity.class);
//                        intent.putExtras(bundle);
//                        startActivity(intent);

                        PersonalPlaylistNameFrag personalPlaylistNameFrag = new PersonalPlaylistNameFrag();
//                        personalPlaylistNameFrag.setArguments(bundle);

                        FragmentTransaction fragmentTransaction = getFragmentManager()
                                .beginTransaction();
                        fragmentTransaction.replace(R.id.fl_body, personalPlaylistNameFrag);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack("PersonalPlaylistNameFrag");
                        fragmentTransaction.commit();
                        break;
                    case 1:
                        PersonalFavouriteListFrag personalFavouriteListFrag = new PersonalFavouriteListFrag();
//                        personalPlaylistNameFrag.setArguments(bundle);

                        FragmentTransaction fragmentTransaction1 = getFragmentManager()
                                .beginTransaction();
                        fragmentTransaction1.replace(R.id.fl_body, personalFavouriteListFrag);
                        fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction1.addToBackStack("PersonalFavouriteListFrag");
                        fragmentTransaction1.commit();
                        break;
                }
            }
        });

        rvPlaylist.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {

                String pass_Id = String.valueOf(dataPlaylist.get(position).id);
                String name = dataPlaylist.get(position).playlist_name;
                String title = "";
                String info = "";
                String image = dataPlaylist.get(position).playlist_photo;

                Bundle bundle = new Bundle();
                bundle.putString(MConstants.PASS_ID, pass_Id);
                bundle.putString(MConstants.PASS_NAME, name);
                bundle.putString(MConstants.PASS_TITLE, title);
                bundle.putString(MConstants.PASS_INFO, info);
                bundle.putString(MConstants.PASS_IMAGE, image);
                bundle.putInt(MConstants.PASS_FLAG, 1);

//                Intent intent = new Intent(getActivity(), AlbumActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);

                AdminPlaylistDetailsFrag adminPlaylistDetailsFrag = new AdminPlaylistDetailsFrag();
                adminPlaylistDetailsFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.fl_body, adminPlaylistDetailsFrag);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack("AdminPlaylistDetailsFrag");
                fragmentTransaction.commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        rv_genre.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                String pass_Id = String.valueOf(dataGenre.get(position).id);
                String name = dataGenre.get(position).genere;
                String title = "";
                String info = "";
                String image = dataGenre.get(position).genere_image;

                Bundle bundle = new Bundle();
                bundle.putString(MConstants.PASS_ID, pass_Id);
                bundle.putString(MConstants.PASS_NAME, name);
                bundle.putString(MConstants.PASS_TITLE, title);
                bundle.putString(MConstants.PASS_INFO, info);
                bundle.putString(MConstants.PASS_IMAGE, image);
                bundle.putInt(MConstants.PASS_FLAG, 2);

                /*Intent intent = new Intent(getActivity(), AlbumActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);*/

                GenreDetailsFrag genreDetailsFrag = new GenreDetailsFrag();
                genreDetailsFrag.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.fl_body, genreDetailsFrag);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack("GenreDetailsFrag");
                fragmentTransaction.commit();
            }
        });
    }

    private void loadJSON() {

        dataPlaylist = new ArrayList<>();
        dataGenre = new ArrayList<>();

        RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);

        /*genres*/
        Call<PlaylistGenreMusicType> call = request.getGenreMusicTypeList();
        call.enqueue(new Callback<PlaylistGenreMusicType>() {
            @Override
            public void onResponse(Call<PlaylistGenreMusicType> call, Response<PlaylistGenreMusicType> response) {
                final PlaylistGenreMusicType.Data[] datums = new PlaylistGenreMusicType.Data[response.body().data.size()];
                for (int i = 0; i < response.body().data.size(); i++) {
                    datums[i] = (response.body().data.get(i));
                    dataGenre.add(datums[i]);
                }
                addGenresData();


                 /*playlist*/
                RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);
                Call<PlaylistAdminPlaylist> callPlaylist = request.getAdminPlaylist();
                callPlaylist.enqueue(new Callback<PlaylistAdminPlaylist>() {
                    @Override
                    public void onResponse(Call<PlaylistAdminPlaylist> call, Response<PlaylistAdminPlaylist> response) {


                        final PlaylistAdminPlaylist.Admin_playlist_data[] datums = new PlaylistAdminPlaylist.Admin_playlist_data[response.body().admin_playlist_data.size()];
                        for (int i = 0; i < response.body().admin_playlist_data.size(); i++) {
                            datums[i] = (response.body().admin_playlist_data.get(i));
                            dataPlaylist.add(datums[i]);
                        }

                        addPlaylistData();

                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onFailure(Call<PlaylistAdminPlaylist> call, Throwable t) {
                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call<PlaylistGenreMusicType> call, Throwable t) {
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void addPlaylistData() {
        rvPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        playlistAdapter = new PlaylistAdapter(dataPlaylist, getActivity());
        playlistAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        rvPlaylist.setAdapter(playlistAdapter);
    }

    private void addGenresData() {
        /*Geners*/
        rv_genre.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        genersAdapter = new GenresAdapter(dataGenre, getActivity());
        genersAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        rv_genre.setAdapter(genersAdapter);
    }

}
