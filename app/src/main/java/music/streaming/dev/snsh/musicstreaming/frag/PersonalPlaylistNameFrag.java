package music.streaming.dev.snsh.musicstreaming.frag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.PersonalPlaylistNameAdapter2;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistModel;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistNameModel;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistName;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalPlaylistNameFrag extends Fragment {

    private RecyclerView rv_personal_playlist_name;
    private PersonalPlaylistNameAdapter2 personalPlaylistNameAdapter2;

    RequestInterface2 requestInterface2;

    private String cusId;
    private List<PersonalPlaylistName.Data> playlistNameList;

    private EditText et_name;
    private View positiveAction;
    private MaterialDialog createNewPlaylist, playListOption, editPlaylistNameDialog;
    private String playlistNameOld;
    private int playlistId;

    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main, menu);
        // You can look up you menu item here and store it in a global variable by
        // 'mMenuItem = menu.findItem(R.id.my_menu_item);'
    }*/

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_playlist).setVisible(true);
        menu.findItem(R.id.search).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_playlist:
                createPlaylistName();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_playlist_name, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = savedInstanceState;

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Playlist");
        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.TITLE2)) {
            Hawk.delete(MConstants.TITLE2);
        }
        Hawk.put(MConstants.TITLE2, "My Playlist");

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.PERSONAL_PLAYLIST_NAME)) {
            Hawk.delete(MConstants.PERSONAL_PLAYLIST_NAME);
        }
        Hawk.put(MConstants.PERSONAL_PLAYLIST_NAME, true);

        if (Hawk.contains(MConstants.CUS_ID)) {
            cusId = String.valueOf(Hawk.get(MConstants.CUS_ID, ""));
        }

        requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);

        rv_personal_playlist_name = (RecyclerView) view.findViewById(R.id.rv_personal_favourite_list);
        playlistNameList = new ArrayList<>();
        personalPlaylistNameAdapter2 = new PersonalPlaylistNameAdapter2(playlistNameList, getActivity());
        rv_personal_playlist_name.setHasFixedSize(true);
        rv_personal_playlist_name.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv_personal_playlist_name.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        loadPlaylistName();

        rv_personal_playlist_name.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                switch (view.getId()) {
                    case R.id.ib_details:
                        moreDetails(position);
                        break;
                }
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                moreDetails(position);
            }

            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (playlistNameList.size() > 0) {

                    String pass_Id = String.valueOf(playlistNameList.get(position).playlistid);
                    String name = playlistNameList.get(position).playlist_name;

                    Bundle bundle = new Bundle();
                    bundle.putString(MConstants.PASS_ID, pass_Id);
                    bundle.putString(MConstants.PASS_NAME, name);
//                Intent intent = new Intent(getActivity(), PersonalPlaylistSongActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);

                    PersonalPlaylistSongListFrag personalPlaylistSongListFrag = new PersonalPlaylistSongListFrag();
                    personalPlaylistSongListFrag.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.fl_body, personalPlaylistSongListFrag);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.addToBackStack("PersonalPlaylistSongListFrag");
                    fragmentTransaction.commit();
                }
            }
        });
    }

    private void loadPlaylistName() {
        playlistNameList = new ArrayList<>();
        Call<PersonalPlaylistName> call = requestInterface2.getPersonalPlaylistName(Integer.parseInt(cusId));
        call.enqueue(new Callback<PersonalPlaylistName>() {
            @Override
            public void onResponse(Call<PersonalPlaylistName> call, Response<PersonalPlaylistName> response) {
                if (response.isSuccessful()) {
                    if (response.body().data.size() > 0)
                        playlistNameList.addAll(response.body().data);
                    personalPlaylistNameAdapter2 = new PersonalPlaylistNameAdapter2(playlistNameList, getActivity());
                    personalPlaylistNameAdapter2.setEmptyView(getLayoutInflater(bundle).inflate(R.layout.activity_personal_playlist_name_empty, (ViewGroup) rv_personal_playlist_name.getParent(), false));
                    rv_personal_playlist_name.setAdapter(personalPlaylistNameAdapter2);
                }
            }

            @Override
            public void onFailure(Call<PersonalPlaylistName> call, Throwable t) {

            }
        });
    }

    private void createPlaylistName() {
        createNewPlaylist = new MaterialDialog.Builder(getActivity())
                .title("Create New Playlist")
                .titleColorRes(R.color.text1)
                .customView(R.layout.dialog_customview, true)
                .positiveText("Create")
                .negativeText(android.R.string.cancel)
                .backgroundColorRes(R.color.colorPrimary)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                                                editPlaylistName(playlistId, et_name.getText().toString());
                        Call<CreatePlaylistNameModel> call = requestInterface2.createPlaylistName((et_name.getText()).toString(), Integer.parseInt(cusId));
                        call.enqueue(new Callback<CreatePlaylistNameModel>() {
                            @Override
                            public void onResponse(Call<CreatePlaylistNameModel> call, Response<CreatePlaylistNameModel> response) {
                                loadPlaylistName();

//                                String playlistId = response.body().getPlaylistId();
                                /*call = requestInterface2.addToPlaylist(Integer.parseInt(playlistId), Integer.parseInt(songId));
                                call.enqueue(new Callback<CreatePlaylistModel>() {
                                    @Override
                                    public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                        Toast.makeText(NowPlayingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                        Log.e("error", t.getMessage());
                                    }
                                });*/
                            }

                            @Override
                            public void onFailure(Call<CreatePlaylistNameModel> call, Throwable t) {
                                Log.e("error", t.getMessage());
                            }
                        });
                    }
                })
                .show();
        positiveAction = createNewPlaylist.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        et_name = (EditText) createNewPlaylist.getCustomView().findViewById(R.id.et_name);

        if (et_name.requestFocus()) {
            createNewPlaylist.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void editPlaylistNameDialog(final String playlistNameOld, final int playlistId) {
        editPlaylistNameDialog = new MaterialDialog.Builder(getActivity())
                .title("Edit Playlist Name")
                .customView(R.layout.dialog_customview, true)
                .titleColorRes(R.color.text1)
                .itemsColorRes(R.color.text1)
                .backgroundColorRes(R.color.colorPrimary)
                .positiveText("Save")
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        editPlaylistName(playlistId, et_name.getText().toString());
                    }
                })
                .show();

        positiveAction = editPlaylistNameDialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        et_name = (EditText) editPlaylistNameDialog.getCustomView().findViewById(R.id.et_name);
        et_name.setText(playlistNameOld);

        if (et_name.requestFocus()) {
            editPlaylistNameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
                if (playlistNameOld.equalsIgnoreCase(String.valueOf(s)))
                    positiveAction.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void editPlaylistName(int playlistId, String playlistNameNew) {

        Call<CreatePlaylistModel> call = requestInterface2.editPlaylistName(playlistId, Integer.parseInt(cusId), playlistNameNew);
        call.enqueue(new Callback<CreatePlaylistModel>() {
            @Override
            public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                loadPlaylistName();
            }

            @Override
            public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {

            }
        });
    }

    private void removePlaylist(int playlistId) {

        Call<CreatePlaylistModel> call = requestInterface2.removePlaylist(playlistId, Integer.parseInt(cusId));
        call.enqueue(new Callback<CreatePlaylistModel>() {
            @Override
            public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                loadPlaylistName();
            }

            @Override
            public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {

            }
        });
    }

    private void moreDetails(int position) {
        playlistNameOld = playlistNameList.get(position).playlist_name;
        playlistId = playlistNameList.get(position).playlistid;

        playListOption = new MaterialDialog.Builder(getActivity())
                .items(R.array.PlaylistOptionArray)
                .itemsColorRes(R.color.text1)
                .backgroundColorRes(R.color.colorPrimary)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                editPlaylistNameDialog(playlistNameOld, playlistId);
                                break;
                            case 1:
                                removePlaylist(playlistId);
                                break;
                        }
                    }
                })
                .show();
    }
}
