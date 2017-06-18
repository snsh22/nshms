package music.streaming.dev.snsh.musicstreaming.act;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.devbrackets.android.playlistcore.service.PlaylistServiceCore;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.BuildConfig;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.UserInfo;
import music.streaming.dev.snsh.musicstreaming.frag.EditInfoFrag;
import music.streaming.dev.snsh.musicstreaming.playlistCore.manager.PlaylistManager;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.AndroidUtilCode.EmptyUtils;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MLanguage;
import music.streaming.dev.snsh.musicstreaming.utly.MUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static music.streaming.dev.snsh.musicstreaming.utly.MConstants.DOMAIN_TEST;

public class SettingActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    private boolean isAccent;

    private int primaryPreselect;
    private int accentPreselect;

    private String cusId;
    private String phoneNumber;
    private String photoUrl;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_edit)
    TextView tv_edit;

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_gender)
    TextView tv_gender;

    @BindView(R.id.tv_theme_title)
    TextView tv_theme_title;

    @BindView(R.id.switch_theme)
    SwitchCompat switch_theme;
    private Disposable isDarkSubscription;

    @BindView(R.id.tv_color_primary)
    TextView tv_color_primary;

    @BindView(R.id.tv_advance_title)
    TextView tv_advance_title;

    @BindView(R.id.tv_minimize_play_screen)
    TextView tv_minimize_play_screen;
    @BindView(R.id.tv_quality)
    TextView tv_quality;
    @BindView(R.id.tv_language)
    TextView tv_language;
    @BindView(R.id.tv_version)
    TextView tv_version;


    @BindView(R.id.tv_name_value)
    TextView tv_name_value;
    @BindView(R.id.tv_phone_value)
    TextView tv_phone_value;
    @BindView(R.id.tv_gender_value)
    TextView tv_gender_value;

    @BindView(R.id.fab_primary)
    FloatingActionButton fab_primary;

    @BindView(R.id.s_minimize_play_screen)
    Switch s_minimize_play_screen;
    @BindView(R.id.s_low_quality)
    Switch s_low_quality;

    @BindView(R.id.tv_language_value)
    TextView tv_language_value;
    @BindView(R.id.tv_version_value)
    TextView tv_version_value;

    @BindView(R.id.cv_color_primary)
    CardView cv_color_primary;

    @BindView(R.id.cv_color_accent)
    CardView cv_color_accent;

    @BindView(R.id.cv_language)
    CardView cv_language;


    @BindView(R.id.btn_logout)
    Button btn_logout;

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aesthetic.attach(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        customAction();

    }

    private void customAction() {

        getAndSetLanguage();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Hawk.init(this).build();
        if (Hawk.contains(MConstants.CUS_ID)) {
            cusId = String.valueOf(Hawk.get(MConstants.CUS_ID, ""));
        }
        if (Hawk.contains(MConstants.CUSTOMER_PHONE_NUMBER)) {
            phoneNumber = Hawk.get(MConstants.CUSTOMER_PHONE_NUMBER, "");
        }

        tv_name_value.setText("+" + phoneNumber);
        tv_phone_value.setText("+" + phoneNumber);
        tv_gender_value.setText("");

        loadInfo();

        if (MUtility.hidePlayScreen())
            s_minimize_play_screen.setChecked(true);
        else
            s_minimize_play_screen.setChecked(false);

        s_minimize_play_screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Hawk.put(MConstants.GOTO_NOW_PLAYING, true);
                } else {
                    Hawk.put(MConstants.GOTO_NOW_PLAYING, false);
                }
            }
        });

        Hawk.init(App.getApplication()).build();
        boolean isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);

        if (isLowQuality)
            s_low_quality.setChecked(true);
        else
            s_low_quality.setChecked(false);

        s_low_quality.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Hawk.put(MConstants.LOW_AUDIO_QUALITY, true);
                } else {
                    Hawk.put(MConstants.LOW_AUDIO_QUALITY, false);
                }
            }
        });


        isDarkSubscription =
                Aesthetic.get()
                        .isDark()
                        .subscribe(
                                isDark -> switch_theme.setChecked(isDark));

        switch_theme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Aesthetic.get()
                        .activityTheme(R.style.AppThemeDark)
                        .isDark(true)
                        .textColorPrimaryRes(R.color.text_color_primary_dark)
                        .textColorSecondaryRes(R.color.text_color_secondary_dark)
                        .colorIconTitleActive(Color.WHITE)
                        .colorIconTitleInactive(Color.WHITE)
                        .colorWindowBackgroundRes(R.color.window_background_dark)
                        .apply();
            } else {
                Aesthetic.get()
                        .activityTheme(R.style.AppTheme)
                        .isDark(false)
                        .textColorPrimaryRes(R.color.text_color_primary)
                        .textColorSecondaryRes(R.color.text_color_secondary)
                        .colorIconTitleActive(Color.WHITE)
                        .colorIconTitleInactive(Color.WHITE)
                        .colorWindowBackgroundRes(R.color.window_background)
                        .apply();
            }
        });

        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);

        tv_version_value.setText(BuildConfig.VERSION_NAME);

    }

    private void loadInfo() {
        RequestInterface2 requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
        Call<UserInfo> call = requestInterface2.getUserInformation(Integer.parseInt(cusId));
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    if (EmptyUtils.isNotEmpty(response.body().data)) {
                        if (EmptyUtils.isNotEmpty(response.body().data.photo)) {
                            Picasso.with(SettingActivity.this)
                                    .load(DOMAIN_TEST + response.body().data.photo)
                                    .transform(new CropCircleTransformation())
                                    .into((ImageView) findViewById(R.id.iv_profile));

                            if (Hawk.contains(MConstants.ACCOUNT_PHOTO))
                                Hawk.delete(MConstants.ACCOUNT_PHOTO);
                            Hawk.put(MConstants.ACCOUNT_PHOTO, DOMAIN_TEST + response.body().data.photo);

                            photoUrl = DOMAIN_TEST + response.body().data.photo;
                            tv_name_value.setText(response.body().data.name);
                            if (response.body().data.gender != null) {
                                tv_gender_value.setText(response.body().data.gender.equalsIgnoreCase("male") ? "Male" : "Female");
                            } else
                                tv_gender_value.setText("");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Aesthetic.resume(this);
    }

    @Override
    protected void onPause() {
        Aesthetic.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        isDarkSubscription.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_left);
//        super.onBackPressed();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {

        if (dialog.isAccentMode()) {

            /*Aesthetic.get()
                    .colorAccent(selectedColor)
                    .apply();*/

            Aesthetic.get()
                    .colorPrimary()
                    .take(1)
                    .subscribe(color -> {
                        // Use color (an integer)
                        Aesthetic.get()
                                .colorAccent(selectedColor)
                                .colorPrimary(color)
                                .colorStatusBarAuto()
                                .apply();
                        fab_primary.setBackgroundTintList(ColorStateList.valueOf(color));
                    });

        } else {
            Aesthetic.get()
                    .colorPrimary(selectedColor)
                    .colorStatusBarAuto()
                    .apply();
            fab_primary.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        }

    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

    }

    @OnClick({
            R.id.tv_edit,
            R.id.cv_color_primary,
            R.id.cv_color_accent,
            R.id.cv_language,
            R.id.btn_logout
    })
    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.tv_edit:
                Bundle bundle = new Bundle();
                bundle.putString(MConstants.PASS_ID, photoUrl);
                bundle.putString(MConstants.PASS_NAME, tv_name_value.getText().toString());
                bundle.putString(MConstants.PASS_IMAGE, tv_phone_value.getText().toString());
                bundle.putString(MConstants.PASS_INFO, tv_gender_value.getText().toString().equalsIgnoreCase("male") ? "male" : "female");

                FrameLayout fl_edit_setting = (FrameLayout) findViewById(R.id.fl_edit_setting);
                fl_edit_setting.setVisibility(View.VISIBLE);
                EditInfoFrag editInfoFrag = new EditInfoFrag();
                editInfoFrag.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_edit_setting, editInfoFrag)
                        .addToBackStack("edit_setting")
                        .commit();
                break;
            case R.id.cv_color_primary:
                isAccent = false;
                new ColorChooserDialog.Builder(this, R.string.color_palette)
                        .allowUserColorInput(false)
                        .allowUserColorInputAlpha(false)
                        .titleSub(R.string.colors)  // title of dialog when viewing shades of a color
                        .accentMode(isAccent)  // when true, will display isAccent palette instead of primary palette
                        .doneButton(R.string.md_done_label)  // changes label of the done button
                        .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                        .backButton(R.string.md_back_label)  // changes label of the back button
                        .preselect(isAccent ? accentPreselect : primaryPreselect)  // optionally preselects a color
                        .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                        .show();
                break;

            case R.id.cv_color_accent:
                isAccent = true;
                new ColorChooserDialog.Builder(this, R.string.color_palette)
                        .allowUserColorInput(false)
                        .allowUserColorInputAlpha(false)
                        .titleSub(R.string.colors)  // title of dialog when viewing shades of a color
                        .accentMode(isAccent)  // when true, will display accent palette instead of primary palette
                        .doneButton(R.string.md_done_label)  // changes label of the done button
                        .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                        .backButton(R.string.md_back_label)  // changes label of the back button
                        .preselect(isAccent ? accentPreselect : primaryPreselect)  // optionally preselects a color
                        .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                        .show();
                break;
            case R.id.cv_language:
                PopupMenu popup = new PopupMenu(SettingActivity.this, tv_language_value);
                popup.getMenuInflater().inflate(R.menu.activity_setting_language_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_english:
                                setEnglishLanguage();
                                break;
                            case R.id.action_myanamr:
                                setMyanmarLanguage();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                break;

            case R.id.btn_logout:
                Hawk.init(SettingActivity.this).build();
                if (Hawk.contains(MConstants.IS_LOGIN)) {
                    if (Hawk.get(MConstants.IS_LOGIN)) {
                        Hawk.delete(MConstants.IS_LOGIN);
                    }

                    PlaylistManager playlistManager = App.getPlaylistManager();
                    PlaylistServiceCore.PlaybackState currentPlaybackState = playlistManager.getCurrentPlaybackState();
                    if (currentPlaybackState != PlaylistServiceCore.PlaybackState.PLAYING) {
                    }
                    playlistManager.invokeStop();

                    Intent i = new Intent(SettingActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                break;
        }
    }
    private void setMyanmarLanguage() {
        tv_name.setText(R.string.tv_name_mm);
        tv_phone.setText(R.string.tv_phone_mm);
        tv_gender.setText(R.string.tv_gender_mm);

        tv_theme_title.setText(R.string.tv_title_mm);
        switch_theme.setText(R.string.switch_theme_mm);
        tv_color_primary.setText(R.string.tv_color_primary_mm);

        tv_advance_title.setText(R.string.tv_title_mm);
        tv_minimize_play_screen.setText(R.string.tv_minimize_play_screen_mm);
        tv_quality.setText(R.string.tv_quality_mm);
        tv_language.setText(R.string.tv_language_mm);
        tv_version.setText(R.string.tv_version_mm);

        btn_logout.setText(R.string.btn_logout_mm);

        tv_language_value.setText(R.string.tv_language_value_mm);
        MLanguage.setSelectedLanguage(false);
    }

    private void setEnglishLanguage() {
        tv_name.setText(R.string.tv_name_eng);
        tv_phone.setText(R.string.tv_phone_eng);
        tv_gender.setText(R.string.tv_gender_eng);

        tv_theme_title.setText(R.string.tv_title_eng);
        switch_theme.setText(R.string.switch_theme_eng);
        tv_color_primary.setText(R.string.tv_color_primary_eng);

        tv_advance_title.setText(R.string.tv_title_eng);
        tv_minimize_play_screen.setText(R.string.tv_minimize_play_screen_eng);
        tv_quality.setText(R.string.tv_quality_eng);
        tv_language.setText(R.string.tv_language_eng);
        tv_version.setText(R.string.tv_version_eng);

        btn_logout.setText(R.string.btn_logout_eng);

        tv_language_value.setText(R.string.tv_language_value_eng);
        MLanguage.setSelectedLanguage(true);
    }
    private void getAndSetLanguage() {
        if (MLanguage.getSelectedLanguage()) {
            setEnglishLanguage();
        } else {
            setMyanmarLanguage();
        }
    }
    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "mm.com.blueplanet.mptmusic.mptmusic",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.toString());
        }
    }
    /*Aesthetic.get()
                .colorPrimary()
                .take(1)
                .subscribe(color -> {
                    // Use color (an integer)
                    String hexColor = String.format("#%06X", (0xFFFFFF & color));
                    fab_primary.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));
                });*/
}
