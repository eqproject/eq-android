package org.eq.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airsaid.pickerviewlibrary.TimePickerView;
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.TContextWrap;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.model.TakePhotoOptions;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;
import org.eq.android.R;
import org.eq.android.api.NetFactory;
import org.eq.android.api.ResponseData;
import org.eq.android.api.UserApi;
import org.eq.android.common.Constant;
import org.eq.android.entity.OverDue;
import org.eq.android.entity.User;
import org.eq.android.utils.BaseSystem;
import org.eq.android.utils.DisplayUtil;
import org.eq.android.utils.ImageFactory;
import org.eq.android.utils.PermissionUtil;
import org.eq.android.utils.SystemPreferences;
import org.eq.android.utils.ToastUtil;
import org.eq.android.view.SelectDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by liufan on 2019/6/21.
 */
//个人资料
public class PersonalDataActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.iv_head)
    ImageView iv_head;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.iv_niname)
    EditText iv_niname;
    @BindView(R.id.ivin12)
    ImageView ivin12;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.tv_birthday)
    TextView tv_birthday;
    @BindView(R.id.et_info)
    EditText et_info;
    @BindView(R.id.tv_num)
    TextView tv_num;

    String headUrl = null;


    //集成 takephoto
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_personal_data;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorwhite), true);
        DisplayUtil.expandTouchArea(iv_back);
        DisplayUtil.expandTouchArea(ivin12);
        getTakePhoto().onCreate(savedInstanceState);
        getData();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.relativelayout1, R.id.relativelayout4, R.id.relativelayout5, R.id.iv_back, R.id.save, R.id.ivin12})
    public void onclick(View view) {
        switch (view.getId()) {
            //更换头像
            case R.id.relativelayout1:
                dialogHead();
                break;
            //性别
            case R.id.relativelayout4:
                dialogSex();
                break;
            //生日
            case R.id.relativelayout5:
                dialogTime();
                break;
            //返回
            case R.id.iv_back:
                finish();
                break;
            //保存
            case R.id.save:
                saveChangeData();
                break;
            case R.id.ivin12:
                if(iv_niname.isEnabled() ==  false){
                    iv_niname.setEnabled(true);
                    ivin12.setVisibility(View.INVISIBLE);
                    iv_niname.setText("");

                    iv_niname.setFocusable(true);
                    iv_niname.setFocusableInTouchMode(true);
                    iv_niname.requestFocus();
                    iv_niname.requestFocusFromTouch();
                    InputMethodManager inputManager =
                            (InputMethodManager)iv_niname.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(iv_niname, 0);
                }
                break;

        }
    }

    private void saveChangeData() {
        long id = SystemPreferences.getLong(Constant.USER_ID);
        String name = tv_username.getText().toString().trim();
        final String nickname = iv_niname.getText().toString().trim();
        String birthday = tv_birthday.getText().toString().trim();

        String intro = et_info.getText().toString().trim();
        String sexString = sex.getText().toString().trim();
        int sexType = 1;
        if (!TextUtils.isEmpty(sexString)) {
            if (sexString.equals("男")) {
                sexType = 1;
            } else {
                sexType = 2;
            }
        }
        NetFactory.create(UserApi.class).modifyUser(id, name, nickname, birthday, headUrl, intro, sexType).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {

                if (response.body().getStatus() == 200) {
                    ToastUtil.showLongToast("保存成功");
                    SystemPreferences.save(Constant.NICKNAME, nickname);
                    PersonalDataActivity.this.finish();
                } else {
                    ToastUtil.showLongToast(response.body().getErrMsg());
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private void toOperate(int pos) {
        String fileName = "eq" + File.separator + "img" + File.separator + "avatar_crop_" + new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss_").format(new Date()) + new Random().nextInt(10000) + ".jpg";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);

        if (pos == 0) {
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        } else if (pos == 1) {
            takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
        }
    }

    private void dialogHead() {
        final SelectDialog dialog = new SelectDialog(this, getResources().getStringArray(R.array.upload_select));
        dialog.show(null).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        dialog.dismiss();
                        toOperate(position);

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 获取TakePhoto实例
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
            configTakePhoto(takePhoto);
        }
        return takePhoto;
    }

    /**
     * takePhoto 配置
     */
    public void configTakePhoto(TakePhoto takePhoto) {
        //压缩
        CompressConfig config = new CompressConfig.Builder().setMaxSize(102400)//大小
                .setMaxPixel(800)//长宽
                .enableReserveRaw(true)//保留原始照片
                .create();
        takePhoto.onEnableCompress(config, true);
        //陪孩子
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(false); //自带相册
        builder.setCorrectImage(true); //修正照片角度
        takePhoto.setTakePhotoOptions(builder.create());
    }

    /**
     * 剪裁配置
     */
    private CropOptions getCropOptions() {
        int height = 800;
        int width = 800;
        boolean withWonCrop = true;
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(width).setAspectY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }


    /**
     * 集成 takepthoto
     */
    @Override
    public void takeSuccess(TResult result) {
        String compressPath = result.getImage().getCompressPath();
        File file = new File(compressPath);

        if (file.exists()) {
            showProcessDialog("正在上传");
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("imgFile", file.getName(), body);

            NetFactory.create(UserApi.class).uploadFile(builder.build().part(0)).enqueue(new Callback<ResponseData<User>>() {
                @Override
                public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                    Logger.d(response.body());
                    ResponseData<User> responseData = response.body();
                    if (responseData.getStatus() == 200) {
                        headUrl = Constant.baseURL + "/upload/images/" + responseData.getData().getImg();
                        Glide.with(PersonalDataActivity.this).load(headUrl).into(iv_head);
                    } else {
                        ToastUtil.showLongToast(responseData.getErrMsg());
                    }
                    hideProcessDialog();
                }

                @Override
                public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                    hideProcessDialog();
                    ToastUtil.showLongToast("网络错误");
                }
            });
        }
    }

    /**
     * 集成 takepthoto
     */
    @Override
    public void takeFail(TResult result, String msg) {
        ToastUtil.showLongToast("设置头像失败");
    }

    /**
     * 集成 takepthoto
     */
    @Override
    public void takeCancel() {
        Logger.d("照片取消");
    }

    /**
     * 集成 takepthoto
     */
    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 集成 takepthoto
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }


    //选择时间
    private void dialogTime() {
        //     TimePickerView 同样有上面设置样式的方法
        TimePickerView mTimePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        // 设置是否循环
//        mTimePickerView.setCyclic(true);
        // 设置滚轮文字大小
//        mTimePickerView.setTextSize(TimePickerView.TextSize.SMALL);
        // 设置时间可选范围(结合 setTime 方法使用,必须在)
//        Calendar calendar = Calendar.getInstance();
//        mTimePickerView.setRange(calendar.get(Calendar.YEAR) - 100, calendar.get(Calendar.YEAR));
        // 设置选中时间
//        mTimePickerView.setTime(new Date());
        mTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                tv_birthday.setText(getTime(date));
            }
        });
        mTimePickerView.show();
    }

    private String getTime(Date date) {
        String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(date);
        return dateStr;
    }

    //获取用户数据
    private void getData() {
        NetFactory.create(UserApi.class).getInfo(SystemPreferences.getLong(Constant.USER_ID)).enqueue(new Callback<ResponseData<User>>() {
            @Override
            public void onResponse(Call<ResponseData<User>> call, Response<ResponseData<User>> response) {
                ResponseData<User> responseData = response.body();
                if (responseData.getStatus() == 200) {
                    User overDue = responseData.getData();
                    initData(overDue);
                }else{
                    ToastUtil.showLongToast(responseData.getErrMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<User>> call, Throwable t) {
                ToastUtil.showLongToast("网络错误");
            }
        });
    }

    private void initData(User overDue) {
        String sexString = "男";
        int sexInt = overDue.getSex();
        if (sexInt == 1) {
            sexString = "男";
        } else if (sexInt == 2) {
            sexString = "女";
        }

        if(!TextUtils.isEmpty(overDue.getPhotoHead())){
            Glide.with(this).load(overDue.getPhotoHead()).into(iv_head);
        }

        tv_username.setText(overDue.getMobile());
        iv_niname.setText(overDue.getNickname());
        sex.setText(sexString);
        tv_birthday.setText(overDue.getBirthday());
        et_info.setText(overDue.getIntro());


        et_info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                tv_num.setText(len + "/" + 120);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private View contentViewi;
    private TextView tv_cancel, tv_ok, tv_man, tv_woman;
    private LinearLayout linearlayout1, linearlayout2;
    private String midString;

    private void dialogSex() {
        midString = "男";
        contentViewi = View.inflate(this, R.layout.select_sex, null);
        tv_cancel = (TextView) contentViewi.findViewById(R.id.tv_cancel);
        tv_ok = (TextView) contentViewi.findViewById(R.id.tv_ok);
        linearlayout1 = (LinearLayout) contentViewi.findViewById(R.id.linearlayout1);
        linearlayout2 = (LinearLayout) contentViewi.findViewById(R.id.linearlayout2);
        tv_man = (TextView) contentViewi.findViewById(R.id.tv_man);
        tv_woman = (TextView) contentViewi.findViewById(R.id.tv_woman);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(contentViewi);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                sex.setText(midString);
            }
        });
        //选择男
        linearlayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayout1.setBackground(getResources().getDrawable(R.drawable.select_sex));
                linearlayout2.setBackground(null);
                tv_man.setTextColor(getResources().getColor(R.color.color_F76646));
                tv_woman.setTextColor(getResources().getColor(R.color.color_8B8989));
                midString = "男";
            }
        });
        //选择女
        linearlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayout2.setBackground(getResources().getDrawable(R.drawable.select_sex));
                linearlayout1.setBackground(null);
                tv_man.setTextColor(getResources().getColor(R.color.color_8B8989));
                tv_woman.setTextColor(getResources().getColor(R.color.color_F76646));
                midString = "女";
            }
        });
    }

}
