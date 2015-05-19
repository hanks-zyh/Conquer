package app.hanks.com.conquer.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.hanks.com.conquer.R;

public class AboutActivity extends BaseActivity {

	private ProgressBar pb;
	private final String aboutUrl = "http://wap.ev123.com/wap_q1w2e4.html";
	private final String useUrl = "http://wap.ev123.com/wap/blank.php?username=q1w2e4&channel_id=10829090";
	private final String helpUrl = "http://wap.ev123.com/wap/blank.php?username=q1w2e4&channel_id=10829041";

	private String url = aboutUrl;
	private String title = "关于我们";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String type = getIntent().getStringExtra("type");
		if (type.endsWith("about")) {
			url = aboutUrl;
			title = "关于我们";
		} else if (type.endsWith("use")) {
			url = useUrl;
			title = "用户协议";
		} else if (type.endsWith("help")) {
			url = helpUrl;
			title = "使用帮助";
		}
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		WebView webView = (WebView) findViewById(R.id.webview);
		pb = (ProgressBar) findViewById(R.id.pb);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		// 为WebView设置WebViewClient处理某些操作
		webView.setWebChromeClient(new webViewClient());
		webView.loadUrl(url);
	}

	class webViewClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			pb.setProgress(newProgress);
			if (newProgress == 100) {
				pb.setVisibility(View.GONE);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	@Override
	public void initTitleBar(ViewGroup rl_title, TextView tv_title, ImageButton ib_back, ImageButton ib_right, View shadow) {
		tv_title.setText(title);
		ib_back.setImageResource(R.drawable.ic_clear_white_24dp);
		shadow.setVisibility(View.GONE);
	}

	@Override
	public View getContentView() {
		return View.inflate(context, R.layout.activity_about, null);
	}
}
