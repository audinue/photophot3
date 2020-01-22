package edu.stts;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.util.Log;
import org.w3c.dom.Node;

public class Page extends Fragment {

	View view;

	public View onCreateView(LayoutInflater l, final ViewGroup c, Bundle b) {
		if (view == null) {
			PhotoSpot activity = ((PhotoSpot) getActivity());
			try {
				view = activity.parser.parse(activity.nodes.remove(getArguments().getString("path")), null);
			} catch (Exception e) {
				Log.e("PhotoSpot", e.getMessage(), e);
			}
		}
		return view;
	}
}
