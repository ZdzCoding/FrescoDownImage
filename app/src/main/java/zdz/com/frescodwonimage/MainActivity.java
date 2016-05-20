package zdz.com.frescodwonimage;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

public class MainActivity extends AppCompatActivity {

    private SimpleDraweeView draweeView;
    private Button btn_down;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_down = (Button) findViewById(R.id.btn_down);
        Uri uri = Uri.parse("http://pic.meizitu.com/wp-content/uploads/2012a/01/24/06.jpg");
        draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
        draweeView.setImageURI(uri);

        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrescoUtil.savePicture("http://pic.meizitu.com/wp-content/uploads/2012a/01/24/06.jpg",MainActivity.this);
                Toast.makeText(MainActivity.this,"下载成功！路径："+FrescoUtil.IMAGE_PIC_CACHE_DIR+"down.jpg",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
