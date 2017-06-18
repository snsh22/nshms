package music.streaming.dev.snsh.musicstreaming.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.hawk.Hawk;

import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.dto.LoginModel;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MLanguage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginConfirmationActivity extends AppCompatActivity {

    Button btn_proceed;
    EditText et_otp;
    String phone_no;
    String code;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aesthetic.attach(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_confirmation);

        if (getIntent().getExtras() != null) {
            Bundle p = getIntent().getExtras();
            phone_no = p.getString(MConstants.PASS_ID);
            code = p.getString(MConstants.PASS_NAME);
        }

        et_otp = (EditText) findViewById(R.id.et_otp);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);

        getAndSetLanguage();

        setupListener();
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

    private void getAndSetLanguage() {
        if (MLanguage.getSelectedLanguage()) {
            setEnglishLanguage();
        } else
            setMyanmarLanguage();
    }

    private void setEnglishLanguage() {
        et_otp.setHint(R.string.et_otp_eng);
        btn_proceed.setText(R.string.btn_proceed_eng);
        MLanguage.setSelectedLanguage(true);
    }

    private void setMyanmarLanguage() {
        et_otp.setHint(R.string.et_otp_mm);
        btn_proceed.setText(R.string.btn_proceed_mm);
        MLanguage.setSelectedLanguage(false);
    }

    private void setupListener() {
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Aesthetic.get()
                        .colorPrimary()
                        .take(1)
                        .subscribe(color -> {
                            // Use color (an integer)
                            String hexColor = String.format("#%06X", (0xFFFFFF & color));
//                            fab_primary.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));

                            materialDialog = new MaterialDialog.Builder(LoginConfirmationActivity.this)
                                    .content("Loading...")
                                    .cancelable(false)
                                    .progress(true, 0)
                                    .backgroundColor(color)
                                    .show();
                        });

                if (code.equalsIgnoreCase(et_otp.getText().toString())) {
                    Log.e("asdf", " : " + phone_no + " : " + et_otp.getText());
                    RequestInterface2 request = ServiceGenerator.createService(RequestInterface2.class);
                    Call<LoginModel> callArtists = request.setActiveLogin(Long.parseLong(phone_no), Integer.parseInt(et_otp.getText().toString()));
                    callArtists.enqueue(new Callback<LoginModel>() {
                        @Override
                        public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                            Log.e("asdf", "cusid" + response.body().getCustid());

                            Hawk.init(LoginConfirmationActivity.this).build();
                            if (Hawk.contains(MConstants.CUS_ID))
                                Hawk.delete(MConstants.CUS_ID);
                            Hawk.put(MConstants.CUS_ID, response.body().getCustid());

                            materialDialog.dismiss();

                            Intent intent = new Intent(LoginConfirmationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<LoginModel> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "error",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("asdf", t.getMessage());
                        }
                    });

                } else {
                    materialDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "OTP Not Correct!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
