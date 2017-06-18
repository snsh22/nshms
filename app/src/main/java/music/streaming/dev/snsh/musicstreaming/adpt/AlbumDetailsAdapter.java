package music.streaming.dev.snsh.musicstreaming.adpt;

import android.app.Activity;
import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.AlbumDetails;

public class AlbumDetailsAdapter extends BaseQuickAdapter<AlbumDetails.Song_data, BaseViewHolder> {

    private final Activity activity;
    private String pass_album_name;

    public AlbumDetailsAdapter(ArrayList<AlbumDetails.Song_data> data, Activity activity, String pass_album_name) {
        super(R.layout.layout_songs, data);
        this.activity = activity;
        this.pass_album_name = pass_album_name;
    }

    @Override
    protected void convert(final BaseViewHolder helper, AlbumDetails.Song_data item) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ((CardView) helper.getView(R.id.card_view)).setRadius(0);
        }

        helper.setText(R.id.tweetName, item.song_name);
        helper.setText(R.id.tweetText, pass_album_name);
        /*Picasso.with(activity)
                .load(item.getAlbumImage())
                .placeholder(R.drawable.art)
                .into(((ImageView) helper.getView(R.id.img)));*/

        helper.addOnClickListener(R.id.ib_song_detail);
        /*helper.getView(R.id.ib_song_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, helper.getView(R.id.ib_song_detail));
                popup.getMenuInflater().inflate(R.menu.activity_song_detail, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.clear_queue) {
                            return true;
                        }
                        Toast.makeText(activity, "You Clicked : " + item.getSongName(), Toast.LENGTH_SHORT).show();

                        return true;
                    }
                });
                popup.show();
            }
        });*/
    }
}
