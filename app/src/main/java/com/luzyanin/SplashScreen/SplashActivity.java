package com.luzyanin.SplashScreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.luzyanin.menu.MainMenuActivity;
import com.xinzhongxinbletester.R;
import me.itangqi.waveloadingview.WaveLoadingView;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class SplashActivity extends Activity implements View.OnClickListener{


    WaveLoadingView mWaveLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setTitle("КТС Солярис-Мобайл");

        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.SQUARE);

        //mWaveLoadingView.setTopTitle("СБТ");
        mWaveLoadingView.setCenterTitleColor(Color.GREEN);
        mWaveLoadingView.setBottomTitleSize(40);

        mWaveLoadingView.setProgressValue(40);
        //mWaveLoadingView.setBorderWidth(10);
        mWaveLoadingView.setAmplitudeRatio(60);
        mWaveLoadingView.setWaveColor(Color.BLUE);
        //mWaveLoadingView.setBorderColor(Color.RED);
        mWaveLoadingView.setTopTitleStrokeColor(Color.RED);

        mWaveLoadingView.setTopTitleStrokeWidth(10);
        mWaveLoadingView.setAnimDuration(500);
        mWaveLoadingView.pauseAnimation();
        mWaveLoadingView.resumeAnimation();
        mWaveLoadingView.cancelAnimation();
        //mWaveLoadingView.startAnimation();

        ((DiscreteSeekBar) findViewById(R.id.seekbar_progress))
                .setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                    @Override
                    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                        mWaveLoadingView.setProgressValue(value);
                        if(value>95){
                            Intent hop = new Intent(SplashActivity.this, MainMenuActivity.class);
                            startActivity(hop);
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {



                    }
                });




    }

    @Override
    public void onClick(View v) {

    }
}
