package com.example.sqlandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAdd, btnClear;
    EditText brand, model,cost;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        brand = (EditText) findViewById(R.id.brand);
        model = (EditText) findViewById(R.id.model);
        cost = (EditText) findViewById(R.id.cost);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        updateTable();
    }

    public void updateTable() {
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int brandIndex = cursor.getColumnIndex(DBHelper.KEY_BRAND);
            int modelIndex = cursor.getColumnIndex(DBHelper.KEY_MODEL);
            int costIndex = cursor.getColumnIndex(DBHelper.KEY_COST);

            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do {
                TableRow dbOutputRow = new TableRow(this);

                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

                TextView outputID = new TextView(this);
                params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputID);

                TextView outputBrand = new TextView(this);
                params.weight = 3.0f;
                outputBrand.setLayoutParams(params);
                outputBrand.setText(cursor.getString(brandIndex));
                dbOutputRow.addView(outputBrand);

                TextView outputModel = new TextView(this);
                params.weight = 3.0f;
                outputModel.setLayoutParams(params);
                outputModel.setText(cursor.getString(modelIndex));
                dbOutputRow.addView(outputModel);

                TextView outputCost = new TextView(this);
                params.weight = 3.0f;
                outputCost.setLayoutParams(params);
                outputCost.setText(cursor.getString(costIndex));
                dbOutputRow.addView(outputCost);

                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight=1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить запись");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                dbOutput.addView(dbOutputRow);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAdd:
                String sbrand = brand.getText().toString();
                String smodel = model.getText().toString();
                String scost = cost.getText().toString();

                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_BRAND, sbrand);
                contentValues.put(DBHelper.KEY_MODEL, smodel);
                contentValues.put(DBHelper.KEY_COST, scost);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                updateTable();
                brand.setText("");
                model.setText("");
                cost.setText("");
                break;

            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutput = findViewById(R.id.dbOutput);
                dbOutput.removeAllViews();
                updateTable();
                break;

            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();

                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf((v.getId()))});

                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int brandIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_BRAND);
                    int modelIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MODEL);
                    int costIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_COST);
                    int realID=1;
                    do {
                        if(cursorUpdater.getInt(idIndex)>realID)
                        {
                            contentValues.put(DBHelper.KEY_ID,realID);
                            contentValues.put(DBHelper.KEY_BRAND,cursorUpdater.getString(brandIndex));
                            contentValues.put(DBHelper.KEY_MODEL,cursorUpdater.getString(modelIndex));
                            contentValues.put(DBHelper.KEY_COST,cursorUpdater.getString(costIndex));
                            database.replace(DBHelper.TABLE_CONTACTS,null,contentValues);
                        }
                        realID++;

                    }while(cursorUpdater.moveToNext());
                    if(cursorUpdater.moveToLast())
                    {
                        database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID+" = ?",new String[]{cursorUpdater.getString(idIndex)});
                    }
                    updateTable();
                }
                break;
        }
    }
}