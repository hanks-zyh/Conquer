package app.hanks.com.conquer.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import app.hanks.com.conquer.R;
import app.hanks.com.conquer.util.PhotoUtil;
import app.hanks.com.conquer.util.SDUtil;

/**
 * 说明：主要用于选择文件操作
 */

public class SelectPicActivity extends Activity implements OnClickListener {

	/***
	 * 使用照相机拍照获取图片
	 */
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	/***
	 * 使用相册中的图片
	 */
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

	/***
	 * 从Intent获取图片路径的KEY
	 */
	public static final String KEY_PHOTO_PATH = "photo_path";

	private static final String TAG = "SelectPicActivity";
	private static final int RESULT_CORD = RESULT_OK;

	private LinearLayout dialogLayout;
	private Button takePhotoBtn, pickPhotoBtn, cancelBtn;

	/** 获取到的图片路径 */
	private String picPath;

	private Intent lastIntent;

	private Uri photoUri;
	private String filePath;
	private boolean isShuoShuo;
	private int cutW;
	private int cutH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_pic_layout);
		isShuoShuo = getIntent().getBooleanExtra("noCut", true);
		cutW = getIntent().getIntExtra("cutW", 200);
		cutH = getIntent().getIntExtra("cutH", 200);
		initView();
	}

	/**
	 * 初始化加载View
	 */
	private void initView() {
		dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
		dialogLayout.setOnClickListener(this);
		takePhotoBtn = (Button) findViewById(R.id.btn_take_photo);
		takePhotoBtn.setOnClickListener(this);
		pickPhotoBtn = (Button) findViewById(R.id.btn_pick_photo);
		pickPhotoBtn.setOnClickListener(this);
		cancelBtn = (Button) findViewById(R.id.btn_cancel);
		cancelBtn.setOnClickListener(this);
		lastIntent = getIntent();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_layout:
				finish();
				break;
			case R.id.btn_take_photo:
				takePhoto();
				break;
			case R.id.btn_pick_photo:
				pickPhoto();
				break;
			default:
				finish();
				break;
		}
	}

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {

			File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 原图
			File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
			filePath = file.getAbsolutePath();
			Uri imageUri = Uri.fromFile(file);

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, 1);

		} else {
			Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 1:// 相机
				if (resultCode == RESULT_OK) {
					if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						Toast.makeText(this, "SD不可用", 0).show();
						return;
					}
					File file = new File(filePath);
					if (isShuoShuo) {
						Intent i = new Intent();
						i.putExtra(KEY_PHOTO_PATH, filePath);
						setResult(RESULT_CORD, i);
						finish();
					} else startImageAction(Uri.fromFile(file), cutW, cutH, 3, true);
				} else {
					finish();
					return;
				}
				break;
			case 2:// 相册
				if (data == null || data.getData() ==null) {
					finish();
					return;
				}
				Uri uri = data.getData();
				if (isShuoShuo) {
					String[] pojo = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(uri, pojo, null, null, null);
					String path = "";
					if (cursor != null) {
						int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
						cursor.moveToFirst();
						path = cursor.getString(columnIndex);
						cursor.close();
					}
					Intent i = new Intent();
					i.putExtra(KEY_PHOTO_PATH, path);
					setResult(RESULT_CORD, i);
					finish();
				} else startImageAction(uri, cutW, cutH, 3, true);
				break;
			case 3:
				saveCropAvator(data);
			default:
				break;
		}

	}

	private void startImageAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}

//	Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//	intent.setType("image/*");
//	intent.putExtra("crop", "true");
//	intent.putExtra("aspectX", 2);
//	intent.putExtra("aspectY", 1);
//	intent.putExtra("outputX", 600);
//	intent.putExtra("outputY", 300);
//	intent.putExtra("scale", true);
//	intent.putExtra("return-data", false);
//	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//	intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//	intent.putExtra("noFaceDetection", true); // no face detection
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", outputX);
		intent.putExtra("aspectY", outputY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	private void saveCropAvator(Intent data) {
		if (data != null) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap bitmap = extras.getParcelable("data");
				if (bitmap != null) {
					String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
					PhotoUtil.saveBitmap(SDUtil.getProjectDir(), filename, bitmap, true);
					data.putExtra(KEY_PHOTO_PATH, SDUtil.getProjectDir() + "/" + filename);
					setResult(RESULT_CORD, data);
					PhotoUtil.recycle(bitmap);
				}
			}
		}
		this.finish();
	}
}
