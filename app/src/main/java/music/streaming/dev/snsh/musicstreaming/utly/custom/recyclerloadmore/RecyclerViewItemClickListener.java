package music.streaming.dev.snsh.musicstreaming.utly.custom.recyclerloadmore;

import android.view.View;

/**
 * Created by androiddeveloper on 22-Mar-17.
 */

public interface RecyclerViewItemClickListener {

    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}
