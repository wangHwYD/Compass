package dac.zjms.com.compass;

import android.annotation.SuppressLint;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dac.zjms.com.compass.provider.AccelerometerCompassProvider;

import static android.widget.Toast.LENGTH_LONG;

/**
 * 作者：wanghaowen on 2018/11/19 11:31
 * 邮箱：13409909996@163.com
 * >_<
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.gv_hv)
    LevelView gv_hv;
    @BindView(R.id.fl_frame)
    FrameLayout fl_frame;
    @BindView(R.id.compass)
    ImageView compass;
    Unbinder mBind;
    private AccelerometerCompassProvider currentOrientationProvider;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = ButterKnife.bind(this);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            currentOrientationProvider = new AccelerometerCompassProvider(sensorManager);
            currentOrientationProvider.start();
            currentOrientationProvider.callback = callback;
            mHandler.postDelayed(statusUpdater, time);
            compass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stop = !stop;
                    if (stop) {
                        currentOrientationProvider.start();
                        mHandler.postDelayed(statusUpdater, time);
                    } else {
                        currentOrientationProvider.stop();
                    }
                }
            });
        }else {
            Toast.makeText(MainActivity.this,"该设备无传感器",LENGTH_LONG).show();
        }

    }

    AccelerometerCompassProvider.Callback callback = new AccelerometerCompassProvider.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onOrientationChanged(double o, double p, double r) {

            orientation = o;
            pi = p;
            ro = r;

            LogUtils.e("1.------"+o+"\r\n2.------"+p+"\r\n3.------"+r);
        }
    };
    boolean stop = true;
    // 记录指南针图片转过的角度
    float currentDegree = 0f;

    double theFilterValue = 0.02;

    double orientation1;
    double orientation;
    double pi;
    double ro;
    long time = 300;
    protected final Handler mHandler = new Handler();

    protected Runnable statusUpdater = new Runnable() {

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            if (stop) {
                float du = (float) normalizeDegree(Math.toDegrees(orientation));
                if (Math.abs(du) - Math.abs(orientation1) > 1) {
                    RotateAnimation ra = new RotateAnimation(currentDegree, -du,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    // 设置动画的持续时间
                    ra.setDuration(200);
                    // 运行动画
                    ra.setFillAfter(true); // 设置保持动画最后的状态
                    compass.startAnimation(ra);

                }
                currentDegree = -du;
                //水平仪
                // gv_hv.setAngle(pi, ro);
                gv_hv.setAngle(ro, pi);
                mHandler.postDelayed(statusUpdater, time);
            }

        }
    };
    private double normalizeDegree(double paramFloat) {
        return (720.0F + paramFloat) % 360.0F;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentOrientationProvider.stop();
        stop = false;
        mHandler.removeCallbacks(statusUpdater);
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
