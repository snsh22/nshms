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
import music.streaming.dev.snsh.musicstreaming.utly.NetworkHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button btn_proceed;
    EditText et_phone_number;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aesthetic.attach(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_phone_number.requestFocus();
        btn_proceed = (Button) findViewById(R.id.btn_proceed);

        getAndSetLanguage();

        setupListener();
    }

    private void getAndSetLanguage() {
        if (MLanguage.getSelectedLanguage()) {
            setEnglishLanguage();
        } else
            setMyanmarLanguage();
    }

    private void setEnglishLanguage() {
        btn_proceed.setText(R.string.btn_proceed_eng);
        MLanguage.setSelectedLanguage(true);
    }

    private void setMyanmarLanguage() {
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
                            if (!NetworkHelper.isOnline(LoginActivity.this)) {
                                NetworkHelper.showNetworkErrorDialog(LoginActivity.this, color);
                                return;
                            }

                            materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                                    .content("Loading...")
                                    .cancelable(false)
                                    .progress(true, 0)
                                    .backgroundColor(color)
                                    .show();

                            String string = String.valueOf(et_phone_number.getText());

                            if (string.startsWith("09")) {
                                string = string.substring(2, string.length());
                            } else if (string.startsWith("959")) {
                                string = string.substring(3, string.length());
                            }

                            if (string.startsWith("2") | string.startsWith("4") | string.startsWith("6") |
                                    string.startsWith("73") | string.startsWith("8") | string.startsWith("5")) {
                                Log.e("asdf", "959" + string);

                                final String pass_Id = "959" + string;

                                RequestInterface2 requestInterface = ServiceGenerator.createService(RequestInterface2.class);
                                Call<LoginModel> callArtists = requestInterface.setLogin(Long.parseLong(pass_Id));
                                callArtists.enqueue(new Callback<LoginModel>() {
                                    @Override
                                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                                        materialDialog.dismiss();
                                        Hawk.init(LoginActivity.this).build();
                                        if (Hawk.contains(MConstants.CUSTOMER_PHONE_NUMBER)) {
                                            Hawk.delete(MConstants.CUSTOMER_PHONE_NUMBER);
                                        }
                                        Hawk.put(MConstants.CUSTOMER_PHONE_NUMBER, pass_Id);

                                        Bundle bundle = new Bundle();
                                        bundle.putString(MConstants.PASS_ID, pass_Id);
                                        bundle.putString(MConstants.PASS_NAME, String.valueOf(response.body().getOtp()));
                                        Intent intent = new Intent(LoginActivity.this, LoginConfirmationActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }

                                    @Override
                                    public void onFailure(Call<LoginModel> call, Throwable t) {
                                        materialDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Login Error!", Toast.LENGTH_SHORT).show();
                                        Log.d("Login", t.getMessage());
                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), "Please Enter Valid MPT Number!",
                                        Toast.LENGTH_SHORT).show();
                                materialDialog.dismiss();
                            }
                        });
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
}
