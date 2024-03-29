package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.DeviantArtApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.ImgurAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PixivAPI;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;
import shortbread.Shortcut;

@Shortcut(id = "post", icon = R.drawable.ic_add_black_24dp, shortLabel = "Post")
public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private Bitmap post_pic = null;
    private String type_image;
    private Uri imageuri;
    private Context context = this;
    private String selected_name = null;
    private String title, desc;
    private int selected_pos = -1;
    //    private List<Bitmap> imgs;
    private List<IApi> lapi = new ArrayList<>();
    private EditText title_post, desc_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        FrameLayout Pics_btn = (FrameLayout) findViewById(R.id.pics_selector);
        final Button button = (Button) findViewById(R.id.upload_button);
        title_post = (EditText) findViewById(R.id.title_post);
        desc_post = (EditText) findViewById(R.id.desc_post);
        title = getString(R.string.title);
        desc = getString(R.string.desc);
        final Handler mainHandler = new Handler(context.getMainLooper());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_name != null && !selected_name.equals("none") && post_pic != null) {
                    List<Bitmap> imgs = new ArrayList<Bitmap>();
                    if (title_post.getText().toString().trim().length() > 0)
                        title = title_post.getText().toString();
                    if (desc_post.getText().toString().trim().length() > 0)
                        desc = desc_post.getText().toString();
                    imgs.add(post_pic);
                    button.setClickable(false);
                    lapi.get(selected_pos).SendPic(title, desc, imgs, new IApi.SendPictureCallback() {
                        @Override
                        public void onSuccess() {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.uploaded, Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        }

                        @Override
                        public void onFailed() {
                            button.setClickable(true);
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    Toast.makeText(context, R.string.uploading, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "faut que ça up sur: " + selected_name, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.cantUp, Toast.LENGTH_SHORT).show();
                }
            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        List<String> categories = new ArrayList<String>();

        IApi api = new FlickrAPI(this);
        if (api.CanUpload() && api.isConnected()) {
            lapi.add(api);
        }
        api = new ImgurAPI(this);
        if (api.CanUpload() && api.isConnected()) {
            lapi.add(api);
        }
        api = new PixivAPI(this);
        if (api.CanUpload() && api.isConnected()) {
            lapi.add(api);
        }
        api = new DeviantArtApi(this);
        if (api.CanUpload() && api.isConnected()) {
            lapi.add(api);
        }

        for (IApi iapi : lapi) {
            categories.add(iapi.getName());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_name = adapterView.getItemAtPosition(i).toString();
                selected_pos = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Pics_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                int SELECT_IMAGE = 1234;
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });

    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        imageuri = data.getData();
                        type_image = getMimeType(data.getData());
                        ImageView image_icon = (ImageView) findViewById(R.id.preview_icon);
                        image_icon.setVisibility(View.INVISIBLE);

                        ImageView image_Pic = (ImageView) findViewById(R.id.preview_image);
                        image_Pic.setVisibility(View.VISIBLE);
                        ImagesTools.LoadPictures(data.getData(), this, image_Pic);
                        post_pic = bitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_post:
                title = ((EditText) view).getText().toString();
                break;
            case R.id.desc_post:
                desc = ((EditText) view).getText().toString();
                break;
        }

    }
}
