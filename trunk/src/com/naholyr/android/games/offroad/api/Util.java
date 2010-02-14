package com.naholyr.android.games.offroad.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.naholyr.android.games.offroad.R;

public class Util {

	/**
	 * Everything is stored in res/values/changelog.xml Keys are : - Version -
	 * Description
	 * 
	 * @return The changelog information, Adapter-ready :)
	 */
	public static List<Map<String, String>> getChangelog(Context context) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		String[] allVersions = context.getResources().getStringArray(R.array.all_versions);
		for (int i = 0; i < allVersions.length; i++) {
			Map<String, String> changelogEntry = new HashMap<String, String>();
			try {
				String version = allVersions[i];
				int descriptionStringId = context.getResources().getIdentifier("version_" + version.replace(".", "_"), "string",
						context.getPackageName());
				changelogEntry.put("Version", version);
				changelogEntry.put("Description", context.getString(descriptionStringId));
				result.add(0, changelogEntry);
			} catch (Exception e) {
				// Causes : out of bounds, resource not found, see changelog.xml
				// to fix
			}
		}

		return result;
	}

	public static Intent showGame(GameParameters parameters) {
		return null;
	}

}
