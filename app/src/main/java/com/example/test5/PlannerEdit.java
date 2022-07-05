package com.example.test5;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.test5.contentprovider.PlannerContentProvider;
import com.example.test5.database.PlannerTable;

public class PlannerEdit extends Activity {
	private Spinner EditUrgent;
	private EditText EditTheme;
	private EditText EditDescription;
	private Uri EditUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.edit_plan);

		EditDescription = (EditText) findViewById(R.id.todo_edit_description);
		EditTheme = (EditText) findViewById(R.id.todo_edit_brief);
		EditUrgent = (Spinner) findViewById(R.id.urgent);
		Button confirmButton = (Button) findViewById(R.id.todo_edit_button);

		Bundle extras = getIntent().getExtras();

		EditUri = (bundle == null) ? null : (Uri) bundle.getParcelable(PlannerContentProvider.CONTENT_ITEM_TYPE);

		if (extras != null) {
			EditUri = extras
					.getParcelable(PlannerContentProvider.CONTENT_ITEM_TYPE);
			UpdateData(EditUri);
		}
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(EditTheme.getText().toString())) {
					makeToast();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}
		});
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		savePoint();
		outState.putParcelable(PlannerContentProvider.CONTENT_ITEM_TYPE, EditUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		savePoint();
	}

	private void makeToast() {
		Toast.makeText(PlannerEdit.this, "Please maintain a brief", Toast.LENGTH_LONG).show();
	}

	private void savePoint() {
		String urgent = (String) EditUrgent.getSelectedItem();
		String brief = EditTheme.getText().toString();
		String description = EditDescription.getText().toString();

		if (description.length() == 0 && brief.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(PlannerTable.COLUMN_URGENT, urgent);
		values.put(PlannerTable.COLUMN_BRIEF, brief);
		values.put(PlannerTable.COLUMN_DESCRIPTION, description);

		if (EditUri == null) {
			EditUri = getContentResolver().insert(
					PlannerContentProvider.CONTENT_URI, values);
		} else {
			getContentResolver().update(EditUri, values, null, null);
		}
	}
	private void UpdateData(Uri uri) {
		String[] projection = { PlannerTable.COLUMN_BRIEF,
				PlannerTable.COLUMN_DESCRIPTION, PlannerTable.COLUMN_URGENT };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			String category = cursor.getString(cursor
					.getColumnIndexOrThrow(PlannerTable.COLUMN_URGENT));

			for (int i = 0; i < EditUrgent.getCount(); i++) {

				String s = (String) EditUrgent.getItemAtPosition(i);
				if (s.equalsIgnoreCase(category)) {
					EditUrgent.setSelection(i);
				}
			}

			EditTheme.setText(cursor.getString(cursor.getColumnIndexOrThrow(PlannerTable.COLUMN_BRIEF)));
			EditDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(PlannerTable.COLUMN_DESCRIPTION)));
			cursor.close();
		}
	}
}
