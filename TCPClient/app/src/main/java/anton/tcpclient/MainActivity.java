package anton.tcpclient;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 1;
    public static ProgressBar progressBar;
    Button progressButton;
    EditText mEdit;
    EditText mEdit2;
    TextView txt;
    Button playNextSong;
    Button stopSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the elements by id
        mEdit = (EditText) findViewById(R.id.editText);
        mEdit2 = (EditText) findViewById(R.id.editText2);
        txt = (TextView) findViewById(R.id.textView2);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progressButton = (Button) findViewById(R.id.startProgressButton);
        playNextSong = (Button) findViewById(R.id.playnext);
        stopSong = (Button) findViewById(R.id.stopsong);

        progressButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Opens up a filechooser which lets you choose which file you would like to transfer
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*"); // Limits the selection to only audio files
                startActivityForResult(Intent.createChooser(intent, "Open with ..."), RESULT_CODE);
            }
        });

        playNextSong.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                System.out.println("Klickade på play");
                String ip = mEdit.getText().toString();
                int port = Integer.parseInt(mEdit2.getText().toString());
                new sendButtonValues(0,ip,port).execute(); // Sends values to other class to indicate which button that was clicked
            }
        });

        stopSong.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                System.out.println("Klickade på stop");
                String ip = mEdit.getText().toString();
                int port = Integer.parseInt(mEdit2.getText().toString());
                new sendButtonValues(1,ip,port).execute(); // Sends values to other class to indicate which button that was clicked
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When a file has been chosen this method is called
        // Check which request we're responding to
        String ip = mEdit.getText().toString();
        int port = Integer.parseInt(mEdit2.getText().toString());
        if (requestCode == RESULT_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri uri;
                uri = data.getData();
                String filepath = getFileName(uri);
                // Gets the data from the file chosen
                try {
                    new TCPClient(getContentResolver().openInputStream(uri), filepath, ip, port).execute(); // Calls TCPClient to initiate a TCPStream later on
                    // Passing the inputstream across to the TCPClient
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
}