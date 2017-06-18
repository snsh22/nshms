package music.streaming.dev.snsh.musicstreaming.adpt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistGenreMusicTypeDetails;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class GenreDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int TYPE_DATA = 0;
    public final int TYPE_LOAD = 1;
    public final int TYPE_FOOTER = 2;

    static Context context;
    List<PlaylistGenreMusicTypeDetails.Data> genreDataList;
    public GenreDetailsAdapter.OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

     /*isLoading - to set the remote loading and complete status to fix back to back load more call
     isMoreDataAvailable - to set whether more data from server available or not.
     It will prevent useless load more request even after all the server data loaded*/


    public GenreDetailsAdapter(Context context, List<PlaylistGenreMusicTypeDetails.Data> genreDataList) {
        this.context = context;
        this.genreDataList = genreDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_DATA) {
            return new GenreDetailsAdapter.MovieHolder(inflater.inflate(R.layout.layout_genre_details_song_list, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new GenreDetailsAdapter.LoadHolder(inflater.inflate(R.layout.layout_footer, parent, false));
        } else {
            return new GenreDetailsAdapter.LoadHolder(inflater.inflate(R.layout.layout_load_more, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_DATA) {
            ((GenreDetailsAdapter.MovieHolder) holder).bindData(genreDataList.get(position));
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if (genreDataList.get(position).type == null) {
            return TYPE_DATA;
        } else {
            if (genreDataList.get(position).type.equals("load")) {
                return TYPE_LOAD;
            } else if (genreDataList.get(position).type.equals("footer")) {
                return TYPE_FOOTER;
            } else {
                return TYPE_DATA;
            }
        }
    }

    @Override
    public int getItemCount() {
        return genreDataList.size();
    }

    static class MovieHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tweetName, tweetText;

        public MovieHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            tweetName = (TextView) itemView.findViewById(R.id.tweetName);
            tweetText = (TextView) itemView.findViewById(R.id.tweetText);
        }

        void bindData(PlaylistGenreMusicTypeDetails.Data genreModel) {
            Picasso.with(context)
                    .load(DOMAIN_TEST + genreModel.album_image)
                    .placeholder(R.drawable.placeholder_song)
                    .into(img);
//                    .transform(new CropCircleTransformation())
            tweetName.setText(genreModel.song_name);
            tweetText.setText(genreModel.album_name + " - " + genreModel.artist_name);
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(GenreDetailsAdapter.OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}
