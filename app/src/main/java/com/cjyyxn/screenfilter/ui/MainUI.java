package com.cjyyxn.screenfilter.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjyyxn.screenfilter.AppConfig;
import com.cjyyxn.screenfilter.GlobalStatus;
import com.cjyyxn.screenfilter.MainActivity;
import com.cjyyxn.screenfilter.R;
import com.cjyyxn.screenfilter.utils.CombinationControl;
import com.cjyyxn.screenfilter.utils.TimerControl;

@SuppressLint({"DefaultLocale", "UseSwitchCompatOrMaterialCode"})
public class MainUI {

    private final MainActivity mainActivity;
    private final TextView tv_main_light;
    private final TextView tv_main_brightness;
    private final LinearLayout ll0_list_main;
    private final CombinationControl combinationControl;
    private TimerControl timerControl;

    public MainUI(MainActivity act) {
        mainActivity = act;

        tv_main_light = mainActivity.findViewById(R.id.tv_main_light);
        tv_main_brightness = mainActivity.findViewById(R.id.tv_main_brightness);
        ll0_list_main = mainActivity.findViewById(R.id.ll0_list_main);

        combinationControl = new CombinationControl(ll0_list_main, mainActivity);

        setUI();
        setTimer();
    }

    private void setUI() {
        Resources res = mainActivity.getResources();

        combinationControl.addSwitchControl(
                res.getString(R.string.filter_switch),
                (buttonView, isChecked) -> AppConfig.setFilterOpenMode(isChecked),
                (sw) -> sw.setChecked(AppConfig.isFilterOpenMode())
        );
        combinationControl.addSwitchControl(
                res.getString(R.string.intelligent_brightness_switch),
                (buttonView, isChecked) -> AppConfig.setIntelligentBrightnessOpenMode(isChecked),
                (sw) -> sw.setChecked(AppConfig.isIntelligentBrightnessOpenMode())
        );
        combinationControl.addSwitchControl(
                res.getString(R.string.hide_in_recents_switch),
                (buttonView, isChecked) -> AppConfig.setHideInMultitaskingInterface(isChecked),
                (sw) -> sw.setChecked(AppConfig.isHideInMultitaskingInterface())
        );

        combinationControl.addLine();

        /**
         * 主界面用户设置屏幕亮度 1,128 -> 0,1
         * 应与系统状态栏亮度同步
         */
        combinationControl.addSeekBarControl(
                res.getString(R.string.screen_brightness_settings), 1, AppConfig.SETTING_SCREEN_BRIGHTNESS,
                (P) -> String.format("%.0f %%", GlobalStatus.getSystemBrightness() * 100),
                (sb, P, fromUser) -> {
                    if (fromUser) {
                        GlobalStatus.setSystemBrightnessProgress(P);
                        GlobalStatus.setBrightness(GlobalStatus.getSystemBrightness());
                    }
                },
                (sb) -> AppConfig.setTempControlMode(true),
                (sb) -> AppConfig.setTempControlMode(false),
                (sb) -> sb.setProgress(GlobalStatus.getSystemBrightnessProgress())
        );
        combinationControl.addSeekBarControl(
                res.getString(R.string.bright_mode_threshold), 1, 100,
                (P) -> String.format("%.0f lux", AppConfig.getHighLightThreshold()),
                (sb, P, fromUser) -> {
                    if (fromUser) {
                        AppConfig.setHighLightThreshold(((float) P) * (100f));
                    }
                },
                (sb) -> CombinationControl.pass(),
                (sb) -> CombinationControl.pass(),
                (sb) -> sb.setProgress((int) (AppConfig.getHighLightThreshold() / 100f + 0.5))
        );
        combinationControl.addSeekBarControl(
                res.getString(R.string.dark_mode_threshold), 0, 20,
                (P) -> String.format("%.0f lux", AppConfig.getLowLightThreshold()),
                (sb, P, fromUser) -> {
                    if (fromUser) {
                        AppConfig.setLowLightThreshold((float) P);
                    }
                },
                (sb) -> CombinationControl.pass(),
                (sb) -> CombinationControl.pass(),
                (sb) -> sb.setProgress((int) (AppConfig.getLowLightThreshold() + 0.5f))
        );
        combinationControl.addSeekBarControl(
                res.getString(R.string.min_hardware_brightness), 0, 100,
                (P) -> String.format("%.0f %%", AppConfig.getMinHardwareBrightness() * 100),
                (sb, P, fromUser) -> {
                    if (fromUser) {
                        AppConfig.setMinHardwareBrightness(((float) P) / 100f);
                    }
                },
                (sb) -> CombinationControl.pass(),
                (sb) -> CombinationControl.pass(),
                (sb) -> sb.setProgress((int) (AppConfig.getMinHardwareBrightness() * 100 + 0.5f))
        );
        combinationControl.addSeekBarControl(
                res.getString(R.string.max_filter_opacity), 60, 100,
                (P) -> String.format("%.0f %%", AppConfig.getMaxFilterOpacity() * 100),
                (sb, P, fromUser) -> {
                    if (fromUser) {
                        AppConfig.setMaxFilterOpacity(((float) P) / 100f);
                    }
                },
                (sb) -> CombinationControl.pass(),
                (sb) -> CombinationControl.pass(),
                (sb) -> sb.setProgress((int) (AppConfig.getMaxFilterOpacity() * 100f + 0.5f))
        );
        combinationControl.addSeekBarControl(
                res.getString(R.string.brightness_increase_tolerance), 0, 30,
                (P) -> String.format("%.2f", AppConfig.getBrightnessAdjustmentIncreaseTolerance()),
                (sb, P, fromUser) -> {
                    if (fromUser) {
                        AppConfig.setBrightnessAdjustmentIncreaseTolerance(((float) P) / 100f);
                    }
                },
                (sb) -> CombinationControl.pass(),
                (sb) -> CombinationControl.pass(),
                (sb) -> sb.setProgress((int) (AppConfig.getBrightnessAdjustmentIncreaseTolerance() * 100f + 0.5f))
        );
        combinationControl.addSeekBarControl(
                res.getString(R.string.brightness_decrease_tolerance), 0, 50,
                (P) -> String.format("%.2f", AppConfig.getBrightnessAdjustmentDecreaseTolerance()),
                (sb, P, fromUser) -> {
                    if (fromUser) {
                        AppConfig.setBrightnessAdjustmentDecreaseTolerance(((float) P) / 100f);
                    }
                },
                (sb) -> CombinationControl.pass(),
                (sb) -> CombinationControl.pass(),
                (sb) -> sb.setProgress((int) (AppConfig.getBrightnessAdjustmentDecreaseTolerance() * 100f + 0.5f))
        );

        combinationControl.addLine();

        combinationControl.addJumpLabel(
                res.getString(R.string.enter_setup),
                () -> mainActivity.startActivity(new Intent(mainActivity, PreparatoryActivity.class))
        );
        combinationControl.addJumpLabel(
                res.getString(R.string.enter_readme),
                () -> mainActivity.startActivity(new Intent(mainActivity, ReadmeActivity.class))
        );
        combinationControl.addJumpLabel(
                res.getString(R.string.enter_brightness_curve_settings),
                () -> mainActivity.startActivity(new Intent(mainActivity, BrightnessPointActivity.class))
        );
        combinationControl.addJumpLabel(
                res.getString(R.string.load_default_config),
                () -> {
                    AppConfig.loadDefaultConfig();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(mainActivity, res.getString(R.string.default_config_loaded_toast), Toast.LENGTH_SHORT).show();
                    });
                }
        );
        combinationControl.addJumpLabel(
                res.getString(R.string.enter_debug),
                () -> mainActivity.startActivity(new Intent(mainActivity, DebugActivity.class))
        );

    }

    private void setTimer() {
        timerControl = new TimerControl(() -> {
            combinationControl.update();

            new Handler(Looper.getMainLooper()).post(() -> {
                // 在UI线程中更新UI组件
//                        Log.d("ccjy", "更新 mainUI");
                tv_main_light.setText(String.format("%s: %.1f lux", mainActivity.getResources().getString(R.string.current_environment_light), GlobalStatus.light));
                tv_main_brightness.setText(String.format("%s: %.1f %%", mainActivity.getResources().getString(R.string.current_screen_brightness), GlobalStatus.getBrightness() * 100));

            });
        });
    }

    public void onResume() {
        timerControl.start(0, 200);
    }

    public void onPause() {
        timerControl.stop();
    }

}
