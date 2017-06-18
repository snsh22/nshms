package music.streaming.dev.snsh.musicstreaming.act;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.BottomNavBgMode;
import com.afollestad.aesthetic.BottomNavIconTextMode;
import com.orhanobut.hawk.Hawk;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MLanguage;
import music.streaming.dev.snsh.musicstreaming.utly.NetworkHelper;
import music.streaming.dev.snsh.musicstreaming.utly.anim.CircularAnim;

public class HomeActivity extends AppCompatActivity {

    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aesthetic.attach(this);
        super.onCreate(savedInstanceState);

        if (Aesthetic.isFirstTime()) {
            Aesthetic.get()
                    .isDark(true)
                    .activityTheme(R.style.AppThemeDark)
                    .colorPrimaryRes(R.color.colorPrimary)
                    .colorAccentRes(R.color.md_amber)
                    .textColorPrimaryRes(R.color.text_color_primary_dark)
                    .textColorSecondaryRes(R.color.text_color_secondary_dark)
                    .textColorPrimaryInverseRes(R.color.text_color_primary)
                    .textColorSecondaryInverseRes(R.color.text_color_secondary)
                    .colorIconTitleActive(Color.WHITE)
                    .colorIconTitleInactive(Color.WHITE)
                    .colorStatusBarAuto()
                    .bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
                    .bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
                    .apply();

            /*.textColorPrimary(ContextCompat.getColor(this, android.R.color.primary_text_dark))
                    .textColorSecondary(ContextCompat.getColor(this, android.R.color.secondary_text_dark))
                    .textColorPrimaryInverse(ContextCompat.getColor(this, android.R.color.primary_text_light))
                    .textColorSecondaryInverse(ContextCompat.getColor(this, android.R.color.secondary_text_light))*/
        }

        Hawk.init(this).build();
        if (Hawk.contains(MConstants.IS_LOGIN)) {
            if (Hawk.get(MConstants.IS_LOGIN)) {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        btn_login = (Button) findViewById(R.id.btn_proceed);

        getAndSetLanguage();

        btn_login.setOnClickListener(v -> Aesthetic.get()
                .colorPrimary()
                .take(1)
                .subscribe(color -> {

                    if (!NetworkHelper.isOnline(HomeActivity.this)) {
                        NetworkHelper.showNetworkErrorDialog(HomeActivity.this, color);
                        return;
                    }

                    CircularAnim.fullActivity(HomeActivity.this, v)
                            .colorOrImageRes(color)
                            .go(() -> startActivity(new Intent(HomeActivity.this, LoginActivity.class)));
                }));

    }

    private void getAndSetLanguage() {
        if (MLanguage.getSelectedLanguage()) {
            setEnglishLanguage();
        } else
            setMyanmarLanguage();
    }

    private void setEnglishLanguage() {
        btn_login.setText(R.string.btn_login_eng);
        MLanguage.setSelectedLanguage(true);
    }

    private void setMyanmarLanguage() {
        btn_login.setText(R.string.btn_login_mm);
        MLanguage.setSelectedLanguage(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_english:
                setEnglishLanguage();
                return true;

            case R.id.action_myanamr:
                setMyanmarLanguage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
