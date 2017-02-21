package anton.tcpclient;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final int RESULT_CODE = 1;
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

                // Opens up a filechooser which lets you choose which file you would like to transfer
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Open with ..."), RESULT_CODE);
                  getProgress();
            }
        });
    }

    // When a file has been chosen this method is called
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When a file has been chosen this method is called
        // Check which request we're responding to
        if (requestCode == RESULT_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri uri;
                uri = data.getData();

                String filepath = getFileName(uri);
                System.out.println(filepath);

                // Gets the data from the file chosen

                try {
                    new TCPClient(getContentResolver().openInputStream(uri),filepath).execute();
                    // Passing the inputstream across to the TCPClient
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void secondAct(View view){
        // Jumps to second activity when a certain button is clicked
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
    public void thirdAct(View view){
        // Jumps to third activity when a certain button is clicked
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
    }



    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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

                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return ++progress;
            }
        }).start();
    }
}
