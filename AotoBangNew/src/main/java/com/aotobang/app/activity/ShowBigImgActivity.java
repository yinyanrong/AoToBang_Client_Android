package com.aotobang.app.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ProgressBar;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.asyntask.LoadLocalBigImgTask;
import com.aotobang.app.entity.ImgMsgBody;
import com.aotobang.app.view.photoview.PhotoView;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.ImageUtils;
@ContentView(R.layout.activity_showbig_img)
public class ShowBigImgActivity extends BaseActivity {

	private ProgressDialog pd;
	@FindViewNoOnClick(R.id.pb_load_local)
	private ProgressBar loadLocalPb;
	@FindViewOnClick(R.id.image)
	private PhotoView image;
	private int default_res = R.drawable.default_image;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;

	@Override
	public void onClick(View v) {
	}

	@Override
	public void initView() {
		Uri uri = getIntent().getParcelableExtra("uri");
		String remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");

		//本地存在，直接显示本地的图片
		if (uri != null && new File(uri.getPath()).exists()) {
			System.err.println("showbigimage file exists. directly show it");
			//DisplayMetrics metrics = new DisplayMetrics();
			//getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;

			bitmap = ImageCache.getInstance().get(uri.getPath());

			if (bitmap == null) {
				LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
						ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			} else {
				image.setImageBitmap(bitmap);
			}
		} else if (remotepath != null) { //去服务器下载图片
			System.err.println("download remote image");
			Map<String, String> maps = new HashMap<String, String>();
			if (!TextUtils.isEmpty(secret)) {
				maps.put("share-secret", secret);
			}
//			(remotepath, maps);
		} else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}

	@Override
	public void handleDataResultOnError(int responseId, int errCode, Object error) {
		super.handleDataResultOnError(responseId, errCode, error);

	}
}
