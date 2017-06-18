package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import music.streaming.dev.snsh.musicstreaming.R;

public class MyBottomSheetAdpt extends BaseQuickAdapter<MyBottomSheetAdpt.MyBottomSheetDTO, BaseViewHolder> {

    private final Activity activity;

    public MyBottomSheetAdpt(List<MyBottomSheetAdpt.MyBottomSheetDTO> data, Activity activity) {
        super(R.layout.layout_my_bottom_sheet, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyBottomSheetAdpt.MyBottomSheetDTO item) {
        helper.setText(R.id.tweetName, item.getTitle());

        Picasso.with(activity)
                .load(item.getIcon())
                .placeholder(R.drawable.placeholder_song)
                .error(R.drawable.ic_action_action_search)
                .into(((ImageView) helper.getView(R.id.img)));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }

    }

    public static class MyBottomSheetDTO {
        private static final List<MyBottomSheetDTO> myBottomSheetList;
        private int icon;
        private String title;

        public MyBottomSheetDTO(int icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }

        static {

            myBottomSheetList = new LinkedList<>();
            myBottomSheetList.add(new MyBottomSheetDTO(R.drawable.ic_playlist_add_white_24dp, "Add to Playlist..."));
            myBottomSheetList.add(new MyBottomSheetDTO(R.drawable.ic_share, "Share on Facebook"));
            myBottomSheetList.add(new MyBottomSheetDTO(R.drawable.ic_stop_white_24dp, "Clear Queue"));
            myBottomSheetList.add(new MyBottomSheetDTO(R.drawable.ic_clear_white_24dp, "Cancel"));
        }

        public static List<MyBottomSheetDTO> getMyBottomSheetList() {
            return myBottomSheetList;
        }
    }
}
