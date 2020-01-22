package edu.stts;

import android.os.AsyncTask;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.util.TypedValue;
import android.util.Log;
import android.graphics.Color;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.w3c.dom.Node;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;

class Parser {

	PhotoSpot activity;
	Opener opener;
	HashMap<String, AsyncTask<Void, Void, Bitmap>> tasks = new HashMap<>();

	Parser(PhotoSpot a, Opener o) {
		activity = a;
		opener = o;
	}

	View parse(Node n, ViewGroup g) throws Exception {
		View view;
		switch (n.getNodeName()) {
			case "frame":
				view = new FrameLayout(activity);
				break;
			case "linear":
				view = new LinearLayout(activity);
				break;
			case "scroll":
				view = new ScrollView(activity);
				break;
			case "text":
				view = new TextView(activity);
				break;
			case "edit":
				view = new EditText(activity);
				break;
			case "button":
				view = new Button(activity);
				break;
			case "image":
				view = new ImageView(activity);
				break;
			case "circle":
				view = new CircleImageView(activity);
				break;
			default:
				throw new RuntimeException("Uknown tag `" + n.getNodeName() + "`.");
		}
		if (g != null) {
			g.addView(view);
		}
		boolean backgroundSet = false;
		int background    = 0;
		float radius      = 0;
		int paddingLeft   = 0;
		int paddingTop    = 0;
		int paddingRight  = 0;
		int paddingBottom = 0;
		int marginLeft    = 0;
		int marginTop     = 0;
		int marginRight   = 0;
		int marginBottom  = 0;
		for (int i = 0; i < n.getAttributes().getLength(); i++) {
			Node attribute = n.getAttributes().item(i);
			String name = attribute.getNodeName();
			switch (name) {
				case "id":
					view.setId(Integer.parseInt(attribute.getNodeValue()));
					break;
				case "clickable":
					view.setClickable(Boolean.parseBoolean(attribute.getNodeValue()));
					break;
				case "visibility":
					switch (attribute.getNodeValue()) {
						case "gone":
							view.setVisibility(View.GONE);
							break;
						case "visible":
							view.setVisibility(View.VISIBLE);
							break;
						case "invisible":
							view.setVisibility(View.INVISIBLE);
							break;
						default:
							throw new RuntimeException("Uknown visibility `" + attribute.getNodeValue() + "`.");
					}
					break;
				case "gravity": {
					int gravity = Gravity.NO_GRAVITY;
					for (String value : attribute.getNodeValue().split(" ")) {
						switch (value) {
							case "left":
								gravity |= Gravity.LEFT;
								break;
							case "right":
								gravity |= Gravity.RIGHT;
								break;
							case "top":
								gravity |= Gravity.TOP;
								break;
							case "bottom":
								gravity |= Gravity.BOTTOM;
								break;
							case "center":
								gravity |= Gravity.CENTER;
								break;
							default:
								throw new RuntimeException("Uknown gravity `" + attribute.getNodeValue() + "`.");
						}
					}
					if (view instanceof LinearLayout) {
						((LinearLayout) view).setGravity(gravity);
					} else if (view instanceof TextView) {
						((TextView) view).setGravity(gravity);
					} else if (view instanceof EditText) {
						((EditText) view).setGravity(gravity);
					} else if (view instanceof Button) {
						((Button) view).setGravity(gravity);
					}
					break;
				}
				case "background":
					background = Color.parseColor(attribute.getNodeValue());
					backgroundSet = true;
					break;
				case "radius":
					radius = Float.parseFloat(attribute.getNodeValue());
					break;
				case "padding":
					paddingLeft =
					paddingTop =
					paddingRight =
					paddingBottom = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "paddingLeft":
					paddingLeft = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "paddingTop":
					paddingTop = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "paddingRight":
					paddingRight = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "paddingBottom":
					paddingBottom = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "margin":
					marginLeft =
					marginTop =
					marginRight =
					marginBottom = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "marginLeft":
					marginLeft = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "marginTop":
					marginTop = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "marginRight":
					marginRight = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "marginBottom":
					marginBottom = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "minWidth": {
					int value = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					view.setMinimumWidth(value);
					if (view instanceof TextView) {
						((TextView) view).setMinWidth(value);
					}
					break;
				}
				case "minHeight": {
					int value = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					view.setMinimumHeight(value);
					if (view instanceof TextView) {
						((TextView) view).setMinHeight(value);
					}
					break;
				}
				case "width":
					switch (attribute.getNodeValue()) {
						case "match":
							view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
							break;
						case "wrap":
							view.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
							break;
						default:
							view.getLayoutParams().width = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					}
					break;
				case "height":
					switch (attribute.getNodeValue()) {
						case "match":
							view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
							break;
						case "wrap":
							view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
							break;
						default:
							view.getLayoutParams().height = dpToPx(Float.parseFloat(attribute.getNodeValue()));
					}
					break;
				case "weight":
					if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
						((LinearLayout.LayoutParams) view.getLayoutParams()).weight = Integer.parseInt(attribute.getNodeValue());
						Log.e("PhotoSpot", "OK");
					} else {
						Log.e("PhotoSpot", "Wew...");
					}
					break;
				case "orientation":
					switch (attribute.getNodeValue()) {
						case "horizontal":
							((LinearLayout) view).setOrientation(LinearLayout.HORIZONTAL);
							break;
						case "vertical":
							((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
							break;
						default:
							throw new RuntimeException("Uknown orientation `" + attribute.getNodeValue() + "`.");
					}
					break;
				case "text":
					((TextView) view).setText(attribute.getNodeValue());
					break;
				case "textSize":
					((TextView) view).setTextSize(Float.parseFloat(attribute.getNodeValue()));
					break;
				case "textColor":
					((TextView) view).setTextColor(Color.parseColor(attribute.getNodeValue()));
					break;
				case "singleLine":
					((TextView) view).setSingleLine(Boolean.parseBoolean(attribute.getNodeValue()));
					break;
				case "hint":
					((EditText) view).setHint(attribute.getNodeValue());
					break;
				case "src":
					view.setBackgroundColor(0xffeeeeee);
					loadBitmap((ImageView) view, attribute.getNodeValue());
					break;
				case "scale":
					switch (attribute.getNodeValue()) {
						case "none":
							((ImageView) view).setScaleType(ImageView.ScaleType.CENTER);
							break;
						case "fit":
							((ImageView) view).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
							break;
						case "fill":
							((ImageView) view).setScaleType(ImageView.ScaleType.CENTER_CROP);
							break;
						default:
							throw new RuntimeException("Uknown scale `" + attribute.getNodeValue() + "`.");
					}
					break;
				case "repeat": {
					Node template = n.getOwnerDocument().createDocumentFragment();
					while (n.getFirstChild() != null) {
						template.appendChild(n.getFirstChild());
					}
					int count = Integer.parseInt(attribute.getNodeValue());
					for (int j = 0; j < count; j++) {
						n.appendChild(template.cloneNode(true));
					}
					break;
				}
				case "fragment": {
					final int id = Integer.parseInt(getAttribute(n, "id"));
					final String path = attribute.getNodeValue();
					view.post(new Runnable() {
						public void run() {
							activity.replace(id, path);
						}
					});
				}
				case "action": {
					final String[] tokens = attribute.getNodeValue().split(" ");
					view.setClickable(true);
					view.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							switch (tokens[0]) {
								case "push":
									if (tokens.length == 3) {
										activity.push(Integer.parseInt(tokens[1]), tokens[2]);
									} else if (tokens.length == 2) {
										activity.push(tokens[1]);
									} else {
										throw new RuntimeException("Invalid push action.");
									}
									break;
								case "replace":
									if (tokens.length == 3) {
										activity.replace(Integer.parseInt(tokens[1]), tokens[2]);
									} else if (tokens.length == 2) {
										activity.replace(tokens[1]);
									} else {
										throw new RuntimeException("Invalid replace action.");
									}
									break;
								case "back":
									activity.pop();
									break;
							}
						}
					});
					break;
				}
				default:
					throw new RuntimeException("Uknown attribute `" + name + "`.");
			}
		}
		if (backgroundSet) {
			GradientDrawable drawable = new GradientDrawable();
			drawable.setColor(background);
			drawable.setCornerRadius(radius);
			view.setBackgroundDrawable(drawable);
		}
		view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(marginLeft, marginTop, marginRight, marginBottom);
		}
		for (int i = 0; i < n.getChildNodes().getLength(); i++) {
			Node node = n.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				parse(node, (ViewGroup) view);
			}
		}
		return view;
	}

	String getAttribute(Node n, String a) {
		for (int i = 0; i < n.getAttributes().getLength(); i++) {
			Node attribute = n.getAttributes().item(i);
			if (attribute.getNodeName().equals(a)) {
				return attribute.getNodeValue();
			}
			return null;
		}
		return null;
	}

	int dpToPx(float dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics());
	}

	void loadBitmap(final ImageView view, final String src) {
		view.post(new Runnable() {
			public void run() {
				try {
					final String key = src + "?" + view.getWidth() + "x" + view.getHeight();
					if (!tasks.containsKey(key)) {
						AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
							public Bitmap doInBackground(Void... args) {
								try {
									Bitmap bitmap = BitmapFactory.decodeStream(opener.open(src));
									float scale = (float) Math.min(
										(float) view.getWidth() / bitmap.getWidth(),
										(float) view.getHeight() / bitmap.getHeight()
									);
									Bitmap scaled = Bitmap.createScaledBitmap(
										bitmap,
										(int) (bitmap.getWidth() * scale),
										(int) (bitmap.getHeight() * scale),
										true
									);
									bitmap.recycle();
									return scaled;
								} catch (Exception e) {
								}
								return null;
							}
						};
						tasks.put(key, task);
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					new AsyncTask<Void, Void, Void>() {
						public Void doInBackground(Void... args) {
							try {
								AsyncTask<Void, Void, Bitmap> task = tasks.get(key);
								final Bitmap bitmap = task.get();
								activity.runOnUiThread(new Runnable() {
									public void run() {
										view.setBackgroundColor(0);
										view.setImageBitmap(bitmap);
									}
								});
							} catch (Exception e) {
							}
							return null;
						}
					}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} catch (Exception e) {
				}
			}
		});
	}
}
