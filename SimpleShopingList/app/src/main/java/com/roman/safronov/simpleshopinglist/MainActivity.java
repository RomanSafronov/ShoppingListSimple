package com.roman.safronov.simpleshopinglist;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    private static final int CM_ADD_ID =1 ;
    private static final int CM_EDIT_ID = 2;
    private static final int CM_DELETE_ID = 3;

    ListView lvData;
    DB db;
    SimpleCursorAdapter scAdapter;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // open connect to data base
        db = new DB(this);
        db.open();

        // forming collation columns
        String[] from = new String[] { DB.COLUMN_QUNTITY, DB.COLUMN_TXT, DB.COLUMN_STATE };
        int[] to = new int[] { R.id.tvQuantity, R.id.tvText};


        // add adapter
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        scAdapter.setViewBinder(new MyViewBinder());
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);



        /*
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "itemClick: position = " +
                                position + ", id = " + id + ", " + parent.getAdapter().getItem(position),
                        Toast.LENGTH_LONG).show();
            }
        });
*/

        // add ContextMenu to ListView
        registerForContextMenu(lvData);

        // create Loader for data reading
        getSupportLoaderManager().initLoader(0, null, this);
    }

    // AddButon press
    public void onAddButtonClick(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("action", CM_ADD_ID);
        startActivityForResult(intent, CM_ADD_ID);
    }

    // ClearAllButton press
    public void onClearAllButtonClick(View view) {
        db.clearTable();
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    // create context menu
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_ADD_ID, 0 , R.string.add_record);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    // action item selected in context menu
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case CM_ADD_ID:
                Intent addintent = new Intent(this, EditActivity.class);
                addintent.putExtra("action", CM_ADD_ID);
                startActivityForResult(addintent, CM_ADD_ID);
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;

            case CM_DELETE_ID:
                db.delRec(acmi.id);
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;



            case CM_EDIT_ID:
                Intent editintent = new Intent(this, EditActivity.class);
                Cursor c = db.getData(acmi.id);
                editintent.putExtra("action", CM_EDIT_ID);
                editintent.putExtra("id", acmi.id);
                editintent.putExtra("name", c.getString(c.getColumnIndex(DB.COLUMN_TXT)) );
                editintent.putExtra("quantity", c.getString(c.getColumnIndex(DB.COLUMN_QUNTITY)));

                startActivityForResult(editintent, CM_EDIT_ID);
                getSupportLoaderManager().getLoader(0).forceLoad();
                break;

        }
        return super.onContextItemSelected(item);


    }






    // add and changes record in database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name = "";
        float quant = 0;

        switch (data.getIntExtra("action", 0)) {
            case CM_ADD_ID:
                if (data == null) {
                    return;
                }
                name = data.getStringExtra("name");
                quant = data.getFloatExtra("quantity", 1);
                db.addNewRec(name, quant, 0);
                getSupportLoaderManager().getLoader(0).forceLoad();
            break;
            case CM_EDIT_ID:
                name = data.getStringExtra("name");
                quant = data.getFloatExtra("quantity", 1);

                db.editRec(data.getLongExtra("id", 0), name, quant, 0);
                getSupportLoaderManager().getLoader(0).forceLoad();
            break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

        int red = getResources().getColor(R.color.Red);
        int green = getResources().getColor(R.color.Green);

        @Override
        public boolean setViewValue(View view, Cursor data, int columnIndex){
            int i = data.getInt(data.getColumnIndex(DB.COLUMN_STATE));
            switch (view.getId()){
              /*  case (R.id.tvQuantity):
                    if (i==1) ((TextView)view).setPaintFlags(((TextView)view).getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

                return true;
                case (R.id.tvText):
                    if (i==1) ((TextView)view).setPaintFlags(((TextView)view).getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                return true;*/
                case R.id.llItem:
                   view.setBackgroundColor(green);

                return true;
            }
        return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }



    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllData();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;
        }

    }
}