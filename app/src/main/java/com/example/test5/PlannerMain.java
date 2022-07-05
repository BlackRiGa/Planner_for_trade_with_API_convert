package com.example.test5;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.example.test5.contentprovider.PlannerContentProvider;
import com.example.test5.database.PlannerTable;

public class PlannerMain extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int Menu_test_ID = Menu.FIRST + 1;
	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_plan);
		this.getListView().setDividerHeight(2);
		UpdateData();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createTask();
			return true;
		case R.id.convert:
			createConverter();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createConverter() {
		Intent i = new Intent(this, BackActivity.class);
		startActivity(i);
	}

	private void createTask() {
		Intent i = new Intent(this, PlannerEdit.class);
		startActivity(i);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu_test_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Uri uri = Uri.parse(PlannerContentProvider.CONTENT_URI + "/"
					+ info.id);
			getContentResolver().delete(uri, null, null);
			UpdateData();
			return true;
		}
		return super.onContextItemSelected(item);
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, PlannerEdit.class);
		Uri EditUri = Uri.parse(PlannerContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(PlannerContentProvider.CONTENT_ITEM_TYPE, EditUri);
		startActivity(i);
	}

	private void UpdateData() {
		String[] from = new String[] { PlannerTable.COLUMN_BRIEF };
		int[] to = new int[] { R.id.label };
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.row_plan, null, from, to, 0);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu_test_ID, 0, R.string.delete);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { PlannerTable.COLUMN_ID, PlannerTable.COLUMN_BRIEF };
		CursorLoader cursorLoader = new CursorLoader(this, PlannerContentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}