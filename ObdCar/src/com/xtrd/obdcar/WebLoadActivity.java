package com.xtrd.obdcar;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.MyWebViewClient;
import com.xtrd.obdcar.view.XtWebView;

public class WebLoadActivity extends BaseActivity {

	private XtWebView webView;
	private ProgressBar progressBar;
	private boolean currentStop = false;
	public WebLoadActivity() {
		layout_id = R.layout.activity_car_book;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = getIntent().getStringExtra("title");
		initTitle(0, R.drawable.btn_back_bg, title, 0, 0);
		initView();
		webView.loadUrl(getIntent().getStringExtra("url"));
	}

	private void initView() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		webView = (XtWebView) findViewById(R.id.webView);
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setWebViewClient(webViewClient);
	}
	
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;

		default:
			break;
		}
		
	};

	WebViewClient webViewClient = new MyWebViewClient() {

		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			if (progressBar != null)
				progressBar.setVisibility(View.GONE);
			super.doUpdateVisitedHistory(view, url, isReload);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			if (!currentStop) {
				super.onLoadResource(view, url);
			}
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

	};

	@Override
	protected void onDestroy() {
		currentStop = true;
		webView.stopLoading();
		super.onDestroy();
	}

	final class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int progress) {
			super.onProgressChanged(view, progress);
			if (view.getUrl() == null
					|| "about:blank".equalsIgnoreCase(view.getUrl())) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(10);
				return;
			}
			progressBar.setMax(100);
			if (progress < 100) {
				progressBar.setVisibility(View.VISIBLE);
				if (progress < 10) {
					progressBar.setProgress(10);
				} else {
					progressBar.setProgress(progress);
				}
			} else {
				progressBar.setProgress(100);
				progressBar.setVisibility(View.GONE);
			}
		}
	}

}
