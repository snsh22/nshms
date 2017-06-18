package music.streaming.dev.snsh.musicstreaming.rest;

import music.streaming.dev.snsh.musicstreaming.dto.AlbumDetails;
import music.streaming.dev.snsh.musicstreaming.dto.ArtistDetails;
import music.streaming.dev.snsh.musicstreaming.dto.ArtistsListModel;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistModel;
import music.streaming.dev.snsh.musicstreaming.dto.CreatePlaylistNameModel;
import music.streaming.dev.snsh.musicstreaming.dto.CustomerPlayListName;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverEnglish;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverJapan;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverKorea;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverMyanmar;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverNewThisWeek;
import music.streaming.dev.snsh.musicstreaming.dto.DiscoverThai;
import music.streaming.dev.snsh.musicstreaming.dto.DownloadModel;
import music.streaming.dev.snsh.musicstreaming.dto.FavouriteModel;
import music.streaming.dev.snsh.musicstreaming.dto.LoadLyric;
import music.streaming.dev.snsh.musicstreaming.dto.LoginModel;
import music.streaming.dev.snsh.musicstreaming.dto.LowQualityDTO;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalFavouriteList;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistName;
import music.streaming.dev.snsh.musicstreaming.dto.PersonalPlaylistSong;
import music.streaming.dev.snsh.musicstreaming.dto.PlayLog;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistAdminPlaylist;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistAdminPlaylistDetails;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistGenreMusicType;
import music.streaming.dev.snsh.musicstreaming.dto.PlaylistGenreMusicTypeDetails;
import music.streaming.dev.snsh.musicstreaming.dto.SearchSong;
import music.streaming.dev.snsh.musicstreaming.dto.UserInfo;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by saingaungsenghein on 1/5/17.
 */

public interface RequestInterface2 {

    /*LogIn*/
    @FormUrlEncoded
    @POST("login")
    Call<LoginModel> setLogin(@Field("phoneno") long phoneNo);

    @FormUrlEncoded
    @POST("active_login")
    Call<LoginModel> setActiveLogin(@Field("phoneno") long phoneNo, @Field("code") int code);

    /*search song*/
    @FormUrlEncoded
    @POST("song_search")
    Call<SearchSong> getSearchSong(@Field("val") String searchString);

    /*lowquality*/
    @GET("lowquality/{songid}")
    Call<LowQualityDTO> getLowQualitySongUrl(@Path("songid") String songId);

    /*discover page*/
    @GET("week")
    Call<DiscoverNewThisWeek> getWeekList(@Query("page") int nextPageUrl);

    @GET("myanmar")
    Call<DiscoverMyanmar> getMyanmarList(@Query("page") int nextPageUrl);

    @GET("english")
    Call<DiscoverEnglish> getEnglishList(@Query("page") int nextPageUrl);

    @GET("korea")
    Call<DiscoverKorea> getKoreaList(@Query("page") int nextPageUrl);

    @GET("thai")
    Call<DiscoverThai> getThaiList(@Query("page") int nextPageUrl);

    @GET("japan")
    Call<DiscoverJapan> getJapanList(@Query("page") int nextPageUrl);

    /*playlist page*/
    @GET("music_type")
    Call<PlaylistGenreMusicType> getGenreMusicTypeList();

    @GET("admin_playlist")
    Call<PlaylistAdminPlaylist> getAdminPlaylist();

    @GET("music_type/{id}")
    Call<PlaylistGenreMusicTypeDetails> getGenreDetails(@Path("id") int playlistId, @Query("page") int nextPageUrl);

    @GET("admin_playlist/{id}")
    Call<PlaylistAdminPlaylistDetails> getAdminPlayListDetail(@Path("id") int playlistId);

    /*artist page*/
    @GET("artists")
    Call<ArtistsListModel> getArtistsList(@Query("page") int nextPageUrl);

    @GET("artist/{id}")
    Call<ArtistDetails> getArtistDetails(@Path("id") int artistId);

    @GET("album/{id}")
    Call<AlbumDetails> getAlbumDetails(@Path("id") int albumId);

    /*now playing music*/
    @GET("lyric/{songid}")
    Call<LoadLyric> getLyric(@Path("songid") int songId);

    @FormUrlEncoded
    @POST("playlog")
    Call<PlayLog> sendDurationToServer(@Field("songID") String songId, @Field("custID") String custId, @Field("duration") String duration);

    //    /*search song*/
//    @FormUrlEncoded
//    @POST("song_search")
//    Call<SearchSong> getSearchSong(@Field("val") String searchString);

    /*User playlist*/
    @GET("get_playlist/{customer_id}")
    Call<CustomerPlayListName> getCustomerPlaylistName(@Path("customer_id") int cusId);

    /*admin playlist*/
    @FormUrlEncoded
    @POST("playlist")
    Call<CreatePlaylistNameModel> createPlaylistName(@Field("playlistname") String playlistName, @Field("custid") int customerId);

    @FormUrlEncoded
    @POST("create_playlist_songs")
    Call<CreatePlaylistModel> addToPlaylist(@Field("playlistid") int playlistId, @Field("songid") int songId);

    /*personal playlist*/
    @GET("get_playlist/{customer_id}")
    Call<PersonalPlaylistName> getPersonalPlaylistName(@Path("customer_id") int cusId);

    @GET("playlist_songs/{playlist_id}")
    Call<PersonalPlaylistSong> getPersonalPlaylistSong(@Path("playlist_id") int playlistId);

    @FormUrlEncoded
    @POST("edit_playlist")
    Call<CreatePlaylistModel> editPlaylistName(@Field("playlistid") int playlistId, @Field("custid") int customerId, @Field("playlistname") String playlistName);

    @FormUrlEncoded
    @POST("delete_playlist")
    Call<CreatePlaylistModel> removePlaylist(@Field("playlistid") int playlistId, @Field("custid") int customerId);

    @FormUrlEncoded
    @POST("delete_playlistsong")
    Call<CreatePlaylistModel> removeSongFromPlaylist(@Field("playlistid") int playlistId, @Field("songid") int songId);

    @GET("get_fav_playlist_song/{customer_id}")
    Call<PersonalFavouriteList> getPersonalFavouriteList(@Path("customer_id") int customerId);

    @FormUrlEncoded
    @POST("delete_fav_playlist")
    Call<FavouriteModel> setFavouriteFalse(@Field("custid") int customerId, @Field("songid") int songId);

    /*setting*/
    @GET("userInfo/{custid}")
    Call<UserInfo> getUserInformation(@Path("custid") int customerId);

    @Multipart
    @POST("userInfo")
    Call<ResponseBody> updateUserInfo(@Part MultipartBody.Part image,
                                      @Part("custId") RequestBody customerId, @Part("stQty") RequestBody stQty,
                                      @Part("gender") RequestBody gender, @Part("name") RequestBody asdf);//photo

    /*@Multipart
    @POST("userInfo")
    Call<ResponseBody> updateUserInfo(@Part MultipartBody.Part image, @Part("photo") RequestBody name,
                                      @Part("custId") RequestBody customerId, @Part("stQty") RequestBody stQty,
                                      @Part("gender") RequestBody gender, @Part("name") RequestBody asdf);*/


    @FormUrlEncoded
    @POST("download")
    Call<DownloadModel> sentDownloadInfo(@Field("custid") int customerId, @Field("songid") int songId);

    @FormUrlEncoded
    @POST("create_fav_playlist")
    Call<FavouriteModel> setFavouriteTrue(@Field("custid") int playlistId, @Field("songid") int songId);

    @GET("get_fav_playlist/songid/{song_id}/custid/{customer_id}")
    Call<FavouriteModel> checkFavourite(@Path("song_id") int songId, @Path("customer_id") int customerId);
}
