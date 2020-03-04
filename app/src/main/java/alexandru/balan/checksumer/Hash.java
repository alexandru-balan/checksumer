package alexandru.balan.checksumer;

import android.Manifest;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash extends AppCompatActivity {

    public static final int pick_result_code = 1;
    public static final int PERMISSION_READ_EXTERNAL = 1;

    private Uri fileUri;
    private FilePickFragment filePickFragment;

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

        filePickFragment = new FilePickFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, filePickFragment, "picker").commit();

        /*Creating the second fragment*/
        AlgoHashFragment algoHashFragment = new AlgoHashFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, algoHashFragment, "controls").commit();
    }

    public void ChooseFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent = Intent.createChooser(intent,"Choose a file");
        startActivityForResult(intent, pick_result_code);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (requestCode == pick_result_code) {
            if (resultCode == -1) {
                fileUri = data.getData();

                try {
                    File file = new File(fileUri.getPath());
                }
                catch (NullPointerException npe) {
                    System.out.println("No file selected");
                    return;
                }

                String filePath = fileUri.getPath().replace("%3A", ":").replace("%2F", "/");

                TextView tv = findViewById(R.id.file_pick_fragment_text);
                tv.setText(filePath);
            }
        }
    }

    public void HashFile (View view) throws IOException, NoSuchAlgorithmException, NullPointerException {
        InputStream fileInputStream = getContentResolver().openInputStream(fileUri);

        byte[] bytes = new byte[2048];
        int bytesCount = 0;

        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        while((bytesCount = fileInputStream.read(bytes)) != -1) {
            digest.update(bytes, 0, bytesCount);
        }

        fileInputStream.close();

        byte[] hashbytes = digest.digest();

        // Converting the decimal bytes of the hash to hexadecimal format
        StringBuilder stringBuilder = new StringBuilder();
        for (byte hashbyte : hashbytes) {
            stringBuilder.append(Integer.toString((hashbyte & 0xff) + 0x100, 16).substring(1));
        }

        /*Replacing the first fragment with the third one*/
        if (filePickFragment.isVisible()) {
            FinalHashFragment finalHashFragment = new FinalHashFragment();
            Bundle args = new Bundle();
            args.putString("hashValue", stringBuilder.toString());
            finalHashFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.file_pick_fragment_layout, finalHashFragment, "hash");
            transaction.commit();
        }
    }

    public void DeleteFirstFragment (View view) {
        Fragment ff = getSupportFragmentManager().findFragmentByTag("picker");

        if (ff != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(filePickFragment);
            transaction.commit();
        }
    }

    public void OpenFirstFragment (View view) {
        Fragment ff = getSupportFragmentManager().findFragmentByTag("picker");

        if (ff == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, filePickFragment, "picker");
            transaction.commit();
        }
    }

    public void CloseActivity(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}