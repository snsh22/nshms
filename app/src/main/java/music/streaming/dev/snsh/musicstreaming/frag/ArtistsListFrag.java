package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.afollestad.aesthetic.Aesthetic;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.ArtistsListAdapter;
import music.streaming.dev.snsh.musicstreaming.dto.ArtistsListModel;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.NetworkHelper;
import music.streaming.dev.snsh.musicstreaming.utly.custom.recyclerloadmore.CustomRVItemTouchListener;
import music.streaming.dev.snsh.musicstreaming.utly.custom.recyclerloadmore.RecyclerViewItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class ArtistsListFrag extends Fragment {

    private RecyclerView rv_artists;
    private ArtistsListAdapter artistsListAdapter;
//    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RequestInterface2 requestAPI;
    private int currentPage, lastPage;

    private List<ArtistsListModel.Data> artistsDataList;

    private String TAG = this.getClass().getSimpleName();
    private RelativeLayout rl_offline;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Aesthetic.get()
                .colorPrimary()
                .take(1)
                .subscribe(color -> {
                    // Use color (an integer)
                    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                    toolbar.setBackgroundColor(color);
                    toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp_toolbar);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Artist");

                    Hawk.init(getActivity()).build();
                    if (Hawk.contains(MConstants.TITLE1)) {
                        Hawk.delete(MConstants.TITLE1);
                    }
                    Hawk.put(MConstants.TITLE1, "Artist");
                });


        rl_offline = (RelativeLayout) view.findViewById(R.id.rl_offline);
        rl_offline.setVisibility(View.GONE);

        if (!NetworkHelper.isOnline(getContext()))
            rl_offline.setVisibility(View.VISIBLE);

        requestAPI = ServiceGenerator.createService(RequestInterface2.class);
        rv_artists = (RecyclerView) view.findViewById(R.id.rv_download);
        artistsDataList = new ArrayList<>();
        artistsListAdapter = new ArtistsListAdapter(getActivity(), artistsDataList);
        artistsListAdapter.setLoadMoreListener(() -> {

            rv_artists.post(() -> {
                if (currentPage <= lastPage)
                    loadMore(currentPage + 1);
            });
            //Calling loadMore function in Runnable to fix the
            // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
        });
        rv_artists.setHasFixedSize(true);
//        rv_artists.setLayoutManager(new LinearLayoutManager(context));
        rv_artists.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        rv_artists.setAdapter(artistsListAdapter);
        load(1);

        rv_artists.addOnItemTouchListener(new CustomRVItemTouchListener(getActivity(), rv_artists, new RecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
//                CardView card_view = (CardView) view.findViewById(R.id.card_view);
//                card_view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
                String pass_Id = String.valueOf(artistsDataList.get(position).id);
                String pass_ArtistName = artistsDataList.get(position).artist_name;
                String pass_image = DOMAIN_TEST + artistsDataList.get(position).artist_photo;

                Bundle bundle = new Bundle();
                bundle.putString(MConstants.PASS_ID, pass_Id);
                bundle.putString(MConstants.PASS_NAME, pass_ArtistName);
                bundle.putString(MConstants.PASS_IMAGE, pass_image);
//                Intent intent = new Intent(getActivity(), ArtistActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);

                ArtistDetailsFrag artistDetailsFrag = new ArtistDetailsFrag();
                artistDetailsFrag.setArguments(bundle);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Slide slideTransition = new Slide(Gravity.RIGHT);
                    slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));

//                    ChangeBounds changeBoundsTransition = new ChangeBounds();
//                    changeBoundsTransition.setDuration(300);

                    artistDetailsFrag.setEnterTransition(new Fade().setDuration(300));
                    artistDetailsFrag.setExitTransition(new Fade().setDuration(100));
                    artistDetailsFrag.setAllowEnterTransitionOverlap(true);
                    artistDetailsFrag.setAllowReturnTransitionOverlap(true);

                }
                FragmentTransaction fragmentTransaction = getFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.fl_body, artistDetailsFrag);
//                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack("ArtistDetailsFrag");
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void load(int index) {
        Call<ArtistsListModel> call = requestAPI.getArtistsList(index);
        call.enqueue(new Callback<ArtistsListModel>() {
            @Override
            public void onResponse(Call<ArtistsListModel> call, Response<ArtistsListModel> response) {
                if (response.isSuccessful()) {

                    currentPage = response.body().paginate.current_page;
                    lastPage = response.body().paginate.last_page;

                    artistsDataList.addAll(response.body().paginate.data);
                    artistsListAdapter.notifyDataChanged();
                } else {
                    Log.e(TAG, " Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ArtistsListModel> call, Throwable t) {
                Log.e(TAG, " Response Error " + t.getMessage());
            }
        });
    }

    private void loadMore(int index) {

        //add loading progress view
        artistsDataList.add(new ArtistsListModel.Data("load"));
        artistsListAdapter.notifyItemInserted(artistsDataList.size() - 1);

        Call<ArtistsListModel> call = requestAPI.getArtistsList(index);
        call.enqueue(new Callback<ArtistsListModel>() {
            @Override
            public void onResponse(Call<ArtistsListModel> call, Response<ArtistsListModel> response) {
                if (response.isSuccessful()) {

                    currentPage = response.body().paginate.current_page;
                    lastPage = response.body().paginate.last_page;

                    //remove loading view
                    artistsDataList.remove(artistsDataList.size() - 1);

                    List<ArtistsListModel.Data> result = response.body().paginate.data;
                    if (currentPage <= lastPage) {
                        //add loaded data
                        artistsDataList.addAll(result);
                    } else {//result size 0 means there is no more data available at server
                        artistsListAdapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        artistsDataList.add(new ArtistsListModel.Data("footer"));
                        artistsListAdapter.notifyItemInserted(artistsDataList.size() - 1);
//                        Toast.makeText(getContext(), "No More Data Available", Toast.LENGTH_LONG).show();
                    }
                    artistsListAdapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                } else {
                    Log.e(TAG, " Load More Response Error " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ArtistsListModel> call, Throwable t) {
                Log.e(TAG, " Load More Response Error " + t.getMessage());
            }
        });
    }
}
