package music.streaming.dev.snsh.musicstreaming.frag.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.adpt.MyBottomSheetAdpt;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistModel;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistNameModel;
import music.streaming.dev.snsh.musicstreaming.dto.CustomerPlayListName;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by androiddeveloper on 15-Jun-17.
 */

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private Activity activity;
    private boolean isOffline;
    private String cusId;
    private String songId;
    private String songName;
    private String artistName;
    private String albumName;
    private String photoUrl;

    private EditText et_name;
    private View positiveAction;
    private MaterialDialog createNewPlaylist;

    public MyBottomSheetDialogFragment() {
    }

    public MyBottomSheetDialogFragment(Activity activity, boolean isOffline, String cusId, String songId,
                                       String songName, String artistName, String albumName, String photoUrl
    ) {
        this.activity = activity;
        this.isOffline = isOffline;
        this.cusId = cusId;
        this.songId = songId;
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.photoUrl = photoUrl;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        RecyclerView rv_bottom_sheet = (RecyclerView) v.findViewById(R.id.rv_bottom_sheet);
        rv_bottom_sheet.setLayoutManager(new LinearLayoutManager(activity));
        MyBottomSheetAdpt myBottomSheetAdpt = new MyBottomSheetAdpt(MyBottomSheetAdpt.MyBottomSheetDTO.getMyBottomSheetList(), activity);
        myBottomSheetAdpt.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        rv_bottom_sheet.setAdapter(myBottomSheetAdpt);

        myBottomSheetAdpt.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                case 0:
                    addToPlaylist();
                    break;
                case 1:
                    ShareDialog shareDialog = new ShareDialog(activity);
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://www.blueoceanmgt.com/index.php?p=companies&catagory=blueplanet&tbl_com=tbl_category"))
                            .setContentTitle(songName)
                            .setContentDescription(artistName + "." + albumName)
                            .setImageUrl(Uri.parse(photoUrl))
                            .build();
                    shareDialog.show(content);
                    break;
                case 2:
                    PlaylistManager playlistManager = App.getPlaylistManager();
                    playlistManager.invokeStop();
                    activity.onBackPressed();
                    break;
                case 3:
                    break;
            }
            dismiss();
        });

        return v;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void addToPlaylist() {
        if (isOffline) {
            Toast.makeText(activity, "downloaded song can't add!", Toast.LENGTH_SHORT).show();
        } else {
            final RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
            Call<CustomerPlayListName> call = requestInterface2.getCustomerPlaylistName(Integer.parseInt(cusId));
            call.enqueue(new retrofit2.Callback<CustomerPlayListName>() {
                @Override
                public void onResponse(Call<CustomerPlayListName> call, Response<CustomerPlayListName> response) {
                    if (response.isSuccessful()) {

                        final List<String> itemName = new ArrayList<>();
                        final int[] itemId = new int[response.body().data.size() + 1];
                        itemName.add("Create New Playlist");
                        itemId[0] = -1;
                        for (int i = 0; i < response.body().data.size(); i++) {
                            itemName.add(response.body().data.get(i).playlist_name);
                            itemId[i + 1] = response.body().data.get(i).playlistid;
                        }
//                                                Log.e("asdf", itemId.length + " : " + itemName.size());
//                                                for (int i = 0; i < itemName.size(); i++) {
//                                                    Log.e("asdf", itemId[i] + " : " + itemName.get(i));
//                                                }
//                                                        .titleColorRes(R.color.text1)
//                                                        .itemsColorRes(R.color.text2)
//                                                        .backgroundColorRes(R.color.colorPrimary)

                        new MaterialDialog.Builder(activity)
                                .title("Add to Playlist")
                                .itemsIds(itemId)
                                .items(itemName)
                                .backgroundColor(MUtility.getWindowBackground(activity))
                                .itemsCallback((dialog, view, which, text) -> {
                                    if (which == 0) {//create new playlist name and add current song to new list
                                        createNewPlaylist(requestInterface2);
                                    } else {//add current song to the selected playlist //addToPlaylist
                                        Log.e("asdf", "bbb : " + itemId[which]);
                                        Call<CreatePlaylistModel> call1 = requestInterface2.addToPlaylist(itemId[which], Integer.parseInt(songId));
                                        call1.enqueue(new retrofit2.Callback<CreatePlaylistModel>() {
                                            @Override
                                            public void onResponse(Call<CreatePlaylistModel> call1, Response<CreatePlaylistModel> response1) {
                                                Toast.makeText(activity, response1.body().message, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Call<CreatePlaylistModel> call1, Throwable t) {
                                                Log.e("error", t.getMessage());
                                            }
                                        });

                                    }
                                })
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<CustomerPlayListName> call, Throwable t) {
                    Log.e("error", "aaa" + t.getMessage());
                    Toast.makeText(activity, "Can't Add Playlist!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createNewPlaylist(final RequestInterface2 requestInterface2) {
        createNewPlaylist = new MaterialDialog.Builder(activity)
                .title("Create New Playlist")
                .customView(R.layout.dialog_customview, true)
                .positiveText("Create")
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) -> {
                    //editPlaylistName(playlistId, et_name.getText().toString());
                    Call<CreatePlaylistNameModel> call = requestInterface2.createPlaylistName((et_name.getText()).toString(), Integer.parseInt(cusId));
                    call.enqueue(new retrofit2.Callback<CreatePlaylistNameModel>() {
                        @Override
                        public void onResponse(Call<CreatePlaylistNameModel> call, Response<CreatePlaylistNameModel> response) {
                            String playlistId = response.body().playlistid;

                            Call<CreatePlaylistModel> c = requestInterface2.addToPlaylist(Integer.parseInt(playlistId), Integer.parseInt(songId));
                            c.enqueue(new retrofit2.Callback<CreatePlaylistModel>() {
                                @Override
                                public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                    Toast.makeText(activity, response.body().message, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                    Log.e("error", t.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<CreatePlaylistNameModel> call, Throwable t) {
                            Log.e("error", t.getMessage());
                        }
                    });
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
}