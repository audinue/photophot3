package edu.stts;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Node;

public class PhotoSpot extends Activity {

	Executor executor = Executors.newFixedThreadPool(4);
	HashMap<String, Node> nodes = new HashMap<>();
	Opener opener;
	Parser parser;

	public void onCreate(Bundle b) {
		super.onCreate(b);
		try {
			getAssets().open("index.xml").close();
			opener = new Opener.Local(this);
		} catch (Exception e) {
			opener = new Opener.Remote();
		}
		parser = new Parser(this, opener);
		replace("index.xml");
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == e.KEYCODE_1) {
			reload();
			return true;
		}
		return super.dispatchKeyEvent(e);
	}

	void replace(final int id, final String path) {
		loadNode(path, new Consumer<Node>() {
			public void consume(Node n) {
				nodes.put(path, n);
				Page page = new Page();
				Bundle bundle = new Bundle();
				bundle.putString("path", path);
				page.setArguments(bundle);
				getFragmentManager()
					.beginTransaction()
					.replace(id, page)
					.commit();
			}
		});
	}

	void replace(String path) {
		replace(android.R.id.content, path);
	}

	void push(final int id, final String path) {
		loadNode(path, new Consumer<Node>() {
			public void consume(Node n) {
				nodes.put(path, n);
				Page page = new Page();
				Bundle bundle = new Bundle();
				bundle.putString("path", path);
				page.setArguments(bundle);
				getFragmentManager()
					.beginTransaction()
					.replace(id, page)
					.addToBackStack(null)
					.commit();
			}
		});
	}

	void push(String path) {
		push(android.R.id.content, path);
	}

	void pop() {
		getFragmentManager()
			.popBackStack();
	}

	void reload() {
		getFragmentManager()
			.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		replace("index.xml");
	}

	void loadNode(final String path, final Consumer<Node> consumer) {
		executor.execute(new Runnable() {
			public void run() {
				try {
					final Node node = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder()
						.parse(opener.open(path))
						.getDocumentElement();
					runOnUiThread(new Runnable() {
						public void run() {
							consumer.consume(node);
						}
					});
				} catch (Exception e) {
				}
			}
		});
	}
}
