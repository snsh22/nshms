package music.streaming.dev.snsh.musicstreaming.frag;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import music.streaming.dev.snsh.musicstreaming.App;
import music.streaming.dev.snsh.musicstreaming.R;
import music.streaming.dev.snsh.musicstreaming.rest.RequestInterface2;
import music.streaming.dev.snsh.musicstreaming.rest.ServiceGenerator;
import music.streaming.dev.snsh.musicstreaming.utly.AndroidUtilCode.EmptyUtils;
import music.streaming.dev.snsh.musicstreaming.utly.MConstants;
import music.streaming.dev.snsh.musicstreaming.utly.MLanguage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action0;
import rx.functions.Action1;

public class EditInfoFrag extends Fragment {

    @BindView(R.id.iv_profile)
    ImageView iv_profile;

    @BindView(R.id.tv_error)
    TextView tv_error;

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.tv_phone)
    TextView tv_phone;

    @BindView(R.id.tv_phone_value)
    TextView tv_phone_value;

    @BindView(R.id.tv_gender)
    TextView tv_gender;

    @BindView(R.id.rg_gender)
    RadioGroup rg_gender;

    @BindView(R.id.rb_male)
    RadioButton rb_male;

    @BindView(R.id.rb_female)
    RadioButton rb_female;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ib_nav_back)
    ImageButton ib_nav_back;

    @BindView(R.id.tv_done)
    TextView tv_done;

    @OnClick(R.id.tv_done)
    public void done() {

        tv_error.setText("");

        if (file == null) {
            tv_error.setText("Please choose photo.");
            return;
        } else if (et_name.getText().toString().trim().length() == 0) {
            tv_error.setText("please input name.");
            return;
        } else if (!rb_male.isChecked() & !rb_female.isChecked()) {
            tv_error.setText("Please choose gender.");
            return;
        }

        materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show();

        RequestBody reqFile = RequestBody.create(MediaType.parse("image"), file);

        MultipartBody.Part body;
        body = MultipartBody.Part.createFormData("photo", file.getName(), reqFile);
//        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        RequestBody custId = RequestBody.create(MediaType.parse("text/plain"), cusId);
        RequestBody stQty;
        if (isLowQuality)
            stQty = RequestBody.create(MediaType.parse("text/plain"), "low");
        else
            stQty = RequestBody.create(MediaType.parse("text/plain"), "high");

        RequestBody gender;
        if (rb_male.isChecked())
            gender = RequestBody.create(MediaType.parse("text/plain"), "male");
        else
            gender = RequestBody.create(MediaType.parse("text/plain"), "female");
        RequestBody newName = RequestBody.create(MediaType.parse("text/plain"), et_name.getText().toString());

        retrofit2.Call<okhttp3.ResponseBody> req;
        if (file.toString().startsWith("http")) {
            req = requestInterface2.updateUserInfo(null, custId, stQty, gender, newName);
        } else {
            req = requestInterface2.updateUserInfo(body, custId, stQty, gender, newName);
        }
//        retrofit2.Call<okhttp3.ResponseBody> req = requestInterface2.updateUserInfo(body, name, custId, stQty, gender, newName);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    materialDialog.dismiss();

                    Log.e("asdf", "fff : " + file);
                    if (!file.toString().startsWith("http"))
                        Picasso.with(getActivity())
                                .load(file)
                                .transform(new CropCircleTransformation())
                                .into((ImageView) getActivity().findViewById(R.id.iv_profile));
                    Hawk.init(App.getApplication()).build();
                    if (Hawk.contains(MConstants.ACCOUNT_PHOTO))
                        Hawk.delete(MConstants.ACCOUNT_PHOTO);
                    Hawk.put(MConstants.ACCOUNT_PHOTO, file.toString());
                    /*Picasso.with(getActivity())
                            .load(getPhotoUrl)
                            .error(R.drawable.ic_account_circle_gray_24px)
                            .transform(new CropCircleTransformation())
                            .into(iv_profile);*/
                    TextView tv_name_value = (TextView) getActivity().findViewById(R.id.tv_name_value);
                    tv_name_value.setText(et_name.getText());
                    TextView tv_gender_value = (TextView) getActivity().findViewById(R.id.tv_gender_value);
                    tv_gender_value.setText(rb_male.isChecked() ? "Male" : "Female");

                    Log.e("asdf", "is successful : " + response.message());
                    back();
                } else {
                    materialDialog.dismiss();
                    Log.e("asdf", response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                materialDialog.dismiss();
                Toast.makeText(getActivity(), "Can't Update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.ib_nav_back)
    public void back() {
        getFragmentManager().popBackStackImmediate();
    }

    @OnClick(R.id.iv_profile)
    public void selectImage() {
        /*if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique
            return;
        }
        Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);*/

//        EditInfoFragPermissionsDispatcher.onPickPhotoWithCheck(this);
        /*FilePickerBuilder.getInstance().setMaxCount(5)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.FilePickerTheme)
                .pickPhoto(this);*/

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EditInfoFragPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }*/

    /*@NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        int maxCount = MAX_ATTACHMENT_COUNT-docPaths.size();
        if((docPaths.size()+photoPaths.size())==MAX_ATTACHMENT_COUNT)
            Toast.makeText(getActivity(), "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.AppTheme)
                    .pickPhoto(this);
    }*/

    private String getPhotoUrl, getName, getPhoneNumber, getGender, getLanguage;

    public static final int PICK_IMAGE = 100;
    private File file;
    private RequestInterface2 requestInterface2;
    private String cusId;
    private Boolean isLowQuality;

    private MaterialDialog materialDialog;

    private RxPermissions rxPermissions;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);
        ButterKnife.bind(this, view);
        requestInterface2 = ServiceGenerator.createService(RequestInterface2.class);
        Hawk.init(getActivity()).build();
        if (Hawk.contains(MConstants.CUS_ID)) {
            cusId = String.valueOf(Hawk.get(MConstants.CUS_ID, ""));
        }
        isLowQuality = Hawk.get(MConstants.LOW_AUDIO_QUALITY, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            getPhotoUrl = bundle.getString(MConstants.PASS_ID);
            getName = bundle.getString(MConstants.PASS_NAME);
            getPhoneNumber = bundle.getString(MConstants.PASS_IMAGE);
            getGender = bundle.getString(MConstants.PASS_INFO);

            Hawk.init(App.getApplication()).build();
            if (Hawk.contains(MConstants.ACCOUNT_PHOTO)) {
                getPhotoUrl = String.valueOf(Hawk.get(MConstants.ACCOUNT_PHOTO,""));
                if (getPhotoUrl.startsWith("http"))
                    Picasso.with(getActivity())
                            .load(getPhotoUrl)
                            .error(R.drawable.ic_account_circle_gray_24px)
                            .transform(new CropCircleTransformation())
                            .into(iv_profile);
                else
                    Picasso.with(getActivity())
                            .load(new File(getPhotoUrl))
                            .error(R.drawable.ic_account_circle_gray_24px)
                            .transform(new CropCircleTransformation())
                            .into(iv_profile);
                Log.e("asdf", "aaa");
            } else {
                Log.e("asdf", "bbb");
                Picasso.with(getActivity())
                        .load(getPhotoUrl)
                        .error(R.drawable.ic_account_circle_gray_24px)
                        .transform(new CropCircleTransformation())
                        .into(iv_profile);
            }
            if (EmptyUtils.isNotEmpty(getPhotoUrl))
                file = new File(getPhotoUrl);
            Log.e("asdf", "getphotourl : " + getPhotoUrl);
            Log.e("asdf", "file : " + file);
            et_name.setText(getName);
            tv_phone_value.setText(getPhoneNumber);

            if (getGender.equalsIgnoreCase("male"))
                rb_male.setChecked(true);
            else
                rb_female.setChecked(true);

        }

        if (MLanguage.getSelectedLanguage()) {
            setEnglishLanguage();
        } else {
            setMyanmarLanguage();
        }

        profileUpload(view);

    }

    private void profileUpload(View view) {
        rxPermissions = new RxPermissions(getActivity());
        rxPermissions.setLogging(true);
        RxView.clicks(view.findViewById(R.id.iv_profile))
                // Ask for permissions when button is clicked
                .compose(rxPermissions.ensureEach(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(new Action1<Permission>() {
                               @Override
                               public void call(Permission permission) {
                                   Log.i("rxPermissions", "Permission result " + permission);
                                   if (permission.granted) {
                                       FilePickerBuilder.getInstance().setMaxCount(1)
                                               .setSelectedFiles(photoPaths)
                                               .setActivityTheme(R.style.FilePickerTheme)
                                               .pickPhoto(EditInfoFrag.this);
                                   } else if (permission.shouldShowRequestPermissionRationale) {
                                       // Denied permission without ask never again
                                       Toast.makeText(getActivity(),
                                               "Denied permission without ask never again",
                                               Toast.LENGTH_SHORT).show();
                                   } else {
                                       // Denied permission with ask never again
                                       // Need to go to the settings
                                       Toast.makeText(getActivity(),
                                               "Permission denied, please enable in setting",
                                               Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent();
                                       intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                       Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                       intent.setData(uri);
                                       startActivity(intent);
                                   }
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable t) {
                                Log.e("rxPermissions", "onError", t);
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() {
                                Log.i("rxPermissions", "OnComplete");
                            }
                        });
    }

    private void setEnglishLanguage() {
        tv_name.setText(R.string.tv_name_eng);
        tv_phone.setText(R.string.tv_phone_eng);
        tv_gender.setText(R.string.tv_gender_eng);
        tv_done.setText(R.string.tv_done_eng);
    }

    private void setMyanmarLanguage() {
        tv_name.setText(R.string.tv_name_mm);
        tv_phone.setText(R.string.tv_phone_mm);
        tv_gender.setText(R.string.tv_gender_mm);
        tv_done.setText(R.string.tv_done_mm);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            String filePath = "";
            if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                android.net.Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                android.database.Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor == null)
                    return;

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
                Log.e("asdf", "filePath : " + filePath);
            }
            android.net.Uri selectedImage = data.getData();
            String wholeID = null;
//            && android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                Log.e("asdf", "SelectedImage : " + selectedImage);
                wholeID = DocumentsContract.getDocumentId(selectedImage);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = getActivity().getContentResolver().
                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);

//            String filePath = "";

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                Log.e("asdf", "filePath2 : " + filePath);
            }

            file = new File(filePath);

            Log.e("asdf", "file : " + file);
            Picasso.with(getActivity())
                    .load(file)
                    .transform(new CropCircleTransformation())
                    .into(iv_profile);
        }*/
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

                    String filePath = photoPaths.get(0);
                    for (String photo : photoPaths) {
                        Log.e("asdf", "photoPaths: " + photo);
                    }

                    file = new File(filePath);
                    Picasso.with(getActivity())
                            .load(file)
                            .transform(new CropCircleTransformation())
                            .into(iv_profile);
                }
                break;
            /*case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;*/
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

                } else {
//                    Toast.makeText(getActivity(), "Please enable Storage Permissions!", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/
}
