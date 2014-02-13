package com.nazarchukvitalii.asynctaskpluspdfviewer;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	public ProgressDialog pDialog;
	private static String url = "http://www.ex.ua/get/93706495";
	public static final int progress_bar_type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button download = (Button) findViewById(R.id.btnDownload);
		download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DownloadFile().execute(url);
			}
		});
		Button open = (Button) findViewById(R.id.btnOpen);
		open.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, PdfViewer.class);
				intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME,
						"/sdcard/downloadedfile.pdf");
				startActivity(intent);

			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case progress_bar_type:
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(true);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}
	}

	class DownloadFile extends AsyncTask<String, String, String> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				int lenghtOfFile = conection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream(),
						8192);
				OutputStream output = new FileOutputStream(
						"/sdcard/downloadedfile.pdf");

				byte data[] = new byte[1024];
				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}
			return null;
		}

		protected void onProgressUpdate(String... progress) {
			pDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(String file_url) {
			dismissDialog(progress_bar_type);
		}
	}
}
