package edu.stts;

import android.app.Activity;
import java.io.InputStream;
import java.net.URL;

abstract class Opener {

	abstract InputStream open(String path) throws Exception;

	static class Local extends Opener {

		Activity activity;

		Local(Activity a) {
			activity = a;
		}

		InputStream open(String path) throws Exception {
			return activity.getAssets().open(path);
		}
	}

	static class Remote extends Opener {

		InputStream open(String path) throws Exception {
			return new URL("http://192.168.100.48/" + path).openStream();
		}
	}
}
