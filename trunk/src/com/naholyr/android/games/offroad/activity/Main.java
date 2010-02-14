package com.naholyr.android.games.offroad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.naholyr.android.games.offroad.R;
import com.naholyr.android.games.offroad.api.Constants;
import com.naholyr.android.games.offroad.api.Util;

public class Main extends Activity {

	private static final int DIALOG_ABOUT = 0;
	private static final int DIALOG_NOT_IMPLEMENTED_YET = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((Button) findViewById(R.id.ButtonNewLocalGame)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				newLocalGame();
			}
		});

		((Button) findViewById(R.id.ButtonNewRemoteGame)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				newRemoteGame();
			}
		});

		((Button) findViewById(R.id.ButtonAbout)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				about();
			}
		});
	}

	private void newLocalGame() {
		int nbPlayers = 2; // FIXME Choose number of players

		Intent intent = new Intent(this, Constants.NEW_GAME_ACTIVITY);
		intent.putExtra(Constants.EXTRA_NB_PLAYERS, nbPlayers);
		startActivity(intent);
	}

	private void newRemoteGame() {
		showDialog(DIALOG_NOT_IMPLEMENTED_YET);
	}

	private void about() {
		showDialog(DIALOG_ABOUT);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;

		switch (id) {
			case DIALOG_ABOUT:
				dialog = new Dialog(this);
				dialog.setContentView(R.layout.about);
				dialog.setTitle(R.string.about);
				((ListView) dialog.findViewById(R.id.ChangelogListView)).setAdapter(new SimpleAdapter(this, Util.getChangelog(this),
						R.layout.changelog_entry, new String[] { "Version", "Description" }, new int[] { R.id.ChangelogEntryVersion,
								R.id.ChangelogEntryDescription }));
				break;
			case DIALOG_NOT_IMPLEMENTED_YET:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.not_implemented_yet))
				// .setTitle(android.R.string.dialog_alert_title)
						// .setIcon(android.R.drawable.ic_dialog_alert)
						.setCancelable(true).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				dialog = builder.create();
				break;
			default:
				dialog = null;
		}

		return dialog;
	}

}
