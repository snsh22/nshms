package music.streaming.dev.snsh.musicstreaming.frag;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.orhanobut.hawk.Hawk;

import org.encryptor4j.util.FileEncryptor;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;

import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.act.NowPlayingActivity;
import music.streaming.dev.snsh.musicstreaming.adpt.DownloadAdapter;
import music.streaming.dev.snsh.musicstreaming.playlistCore.data.MediaItem;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.utly.AndroidUtilCode.FileUtils;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import music.streaming.dev.snsh.musicstreaming.utly.SendDurationToServer;

public class DownloadFrag extends Fragment {

    private RecyclerView rvDownload;
    private DownloadAdapter downloadAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<MediaItem> downloadMediaItemList = new ArrayList<>();
    ArrayList<MediaItem> downloadMediaItemListTmp = new ArrayList<>();

    private PlaylistManager playlistManager;
    private LinkedList<MediaItem> mediaItems;

    MaterialDialog downloadOption;
    String pathPrefix;
    int adapterPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Offline");


        rvDownload = (RecyclerView) view.findViewById(R.id.rv_download);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);

        retrieveData(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                downloadAdapter.setEnableLoadMore(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                downloadAdapter.setNewData(DataServer.getSampleData(PAGE_SIZE));
//                isErr = false;
//                mCurrentCounter = PAGE_SIZE;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(DownloadFrag.this).attach(DownloadFrag.this).commit();

                        mSwipeRefreshLayout.setRefreshing(false);
//                downloadAdapter.setEnableLoadMore(true);
                    }
                }, 700);
            }
        });

        rvDownload.addOnItemTouchListener(new OnItemClickListener() {

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                adapterPosition = position;
                switch (view.getId()) {
                    case R.id.ib_song_detail:
                        downloadOption = new MaterialDialog.Builder(getActivity())
                                .backgroundColor(MUtility.getWindowBackground(getActivity()))
                                .items(R.array.DownloadOptionArray).typeface(Typeface.createFromAsset(getActivity().getAssets(),
                                        "fonts/Zawgyi-One.ttf"), Typeface.createFromAsset(getActivity().getAssets(),
                                        "fonts/Zawgyi-One.ttf"))
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                        switch (position) {
                                            case 0:
                                                deleteSong(position);
                                                break;
                                        }
                                    }
                                })
                                .theme(Theme.DARK)
                                .show();
                        break;
                }
            }

            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {

                if (SendDurationToServer.isPlayingSong()) {
                    SendDurationToServer.sendDuration();
                }

                playlistManager = App.getPlaylistManager();

                pathPrefix = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "root" + File.separator + downloadMediaItemList.get(position).getSongId() + File.separator + downloadMediaItemList.get(position).getSongId();
                decryptDownloadedFile();


                mediaItems = new LinkedList<>();
                MediaItem mediaItem = new MediaItem(String.valueOf(downloadMediaItemList.get(position).getSongId()), downloadMediaItemList.get(position).getSongName(), downloadMediaItemList.get(position).getMediaUrl(), downloadMediaItemList.get(position).getAlbumImage(), downloadMediaItemList.get(position).getAlbum(), downloadMediaItemList.get(position).getArtist(), downloadMediaItemList.get(position).getLyric(), true);
                mediaItems.add(mediaItem);

                playlistManager.setParameters(mediaItems, 0);
                playlistManager.setId(30);
                playlistManager.play(0, false);

                mediaItems = new LinkedList<>();
                for (int i = 0; i < downloadMediaItemList.size(); i++) {
                    MediaItem mI = new MediaItem(String.valueOf(downloadMediaItemList.get(i).getSongId()), downloadMediaItemList.get(i).getSongName(), downloadMediaItemList.get(i).getMediaUrl(), downloadMediaItemList.get(i).getAlbumImage(), downloadMediaItemList.get(i).getAlbum(), downloadMediaItemList.get(i).getArtist(), downloadMediaItemList.get(i).getLyric(), true);
                    mediaItems.add(mI);
                }
                MUtility.updateNowPlayingMediaItem(getActivity(), mediaItems, position);
            }
        });

    }

    private void decryptDownloadedFile() {
        File srcFile = new File(pathPrefix + ".snsh.enc");
        File destFile = new File(pathPrefix + ".snsh");

        try {
            FileEncryptor fileEncryptor = new FileEncryptor(NowPlayingActivity.aes);
//            fileEncryptor.encrypt(srcFile, destFile);
            fileEncryptor.decrypt(srcFile, destFile);
//            FileUtils.deleteFile(srcFile);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Log.e("asdf", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("asdf", e.getMessage());
        }


    }

    private void retrieveData(Bundle savedInstanceState) {

        downloadMediaItemList = new ArrayList<>();
        downloadMediaItemListTmp = new ArrayList<>();

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.DOWNLOAD_MEDIA_ITEM_LIST)) {
            downloadMediaItemListTmp = Hawk.get(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);

            for (MediaItem i : downloadMediaItemListTmp) {
                if (FileUtils.isFileExists(i.getMediaUrl() + ".enc")) {
                    downloadMediaItemList.add(i);
                }
            }
        }

        rvDownload.setLayoutManager(new LinearLayoutManager(getContext()));
        downloadAdapter = new DownloadAdapter(downloadMediaItemList, getActivity());
        downloadAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
//        downloadAdapter.setAutoLoadMoreSize(3);
        rvDownload.setAdapter(downloadAdapter);
        downloadAdapter.setEmptyView(getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_download_empty, (ViewGroup) rvDownload.getParent(), false));

    }

    private void deleteSong(int position) {
        String songId = downloadMediaItemList.get(adapterPosition).getSongId();
        pathPrefix = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "root" + File.separator + songId;
        deleteRecursive(new File(pathPrefix));

        downloadMediaItemList.remove(adapterPosition);

        downloadAdapter = new DownloadAdapter(downloadMediaItemList, getActivity());
        downloadAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        rvDownload.setAdapter(downloadAdapter);

        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.DOWNLOAD_MEDIA_ITEM_LIST)) {
            Hawk.delete(MConstants.DOWNLOAD_MEDIA_ITEM_LIST);
            Hawk.put(MConstants.DOWNLOAD_MEDIA_ITEM_LIST, downloadMediaItemList);
        }
        if (Hawk.contains(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST))
            Hawk.delete(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST);
        Hawk.put(MConstants.NOW_PLAYING_MEDIA_ITEM_LIST, downloadMediaItemList);
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }
}