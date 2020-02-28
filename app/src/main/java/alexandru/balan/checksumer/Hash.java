package alexandru.balan.checksumer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class Hash extends AppCompatActivity {

    public static final int pick_result_code = 1;
    public static final int PERMISSION_READ_EXTERNAL = 1;

    private Uri fileUri;
    private String filePath;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hash_layout);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Requesting

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL);
        }

        /*Creating the first fragment*/
        FilePickFragment filePickFragment = new FilePickFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, filePickFragment).commit();
    }

    public void ChooseFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent = Intent.createChooser(intent,"Choose a file");
        startActivityForResult(intent, pick_result_code);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case pick_result_code:
                if (resultCode == -1) {
                    fileUri = data.getData();

                    file = new File(fileUri.getPath());
                    filePath = file.getPath();

                    TextView tv = findViewById(R.id.file_pick_fragment_text);
                    tv.setText(filePath);
                }
                break;
        }
    }
}