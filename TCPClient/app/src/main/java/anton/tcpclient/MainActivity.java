package anton.tcpclient;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLOutput;


public class MainActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 1;

    public static ProgressBar progressBar;
    Button progressButton;
    EditText mEdit;
    EditText mEdit2;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdit = (EditText) findViewById(R.id.editText);
        mEdit2 = (EditText) findViewById(R.id.editText2);
        txt = (TextView) findViewById(R.id.textView2);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progressButton = (Button) findViewById(R.id.startProgressButton);

        progressButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Opens up a filechooser which lets you choose which file you would like to transfer
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Open with ..."), RESULT_CODE);
            }
        });
    }

    // When a file has been chosen this method is called
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
                System.out.println(filepath);

                // Gets the data from the file chosen

                try {
                    new TCPClient(getContentResolver().openInputStream(uri), filepath, ip, port).execute();
                    // Passing the inputstream across to the TCPClient
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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

