package anton.tcpclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {


    private static int progress;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    Button progressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressButton = (Button)findViewById(R.id.startProgressButton);
        progressButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  getProgress();
                new TCPClient().execute();
            }
        });
    }

    public void secondAct(View view){
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
    public void thirdAct(View view){
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
    }

    public void getProgress(){
        final int maxValueProgressBar=600;
        progress = 0;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(maxValueProgressBar);

        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < maxValueProgressBar) {
                    progressStatus = doSomeWork();
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                }
                handler.post(new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
            }

            private int doSomeWork() {
                // Here the sending of files is done or called.
                try {
                    // ---simulate doing some work---
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return ++progress;
            }
        }).start();
    }
}
