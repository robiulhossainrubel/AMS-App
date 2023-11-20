package com.rhr.ams;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.rhr.ams.Adapter.ClassAdapter;
import com.rhr.ams.Model.ClassItems;
import com.rhr.ams.Utis.Constants;
import com.rhr.ams.Utis.RequestHandler;
import com.rhr.ams.Utis.SharePrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    ArrayList<ClassItems> classItems = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    TextView title, Section,empty_notes_view;
    ImageView back, logout;
    private ProgressDialog pd;
    String email,department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SharePrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LogActivity.class));
        }
        email = SharePrefManager.getInstance(this).getEmail();
        department = SharePrefManager.getInstance(this).getDepartment();

        empty_notes_view = findViewById(R.id.empty_notes_view);
        recyclerView = findViewById(R.id.recyclerview);
        title = findViewById(R.id.title_tool);
        Section = findViewById(R.id.section_tool);
        back = findViewById(R.id.back);
        logout = findViewById(R.id.logout);
        pd = new ProgressDialog(this);
        //toolbar set
        title.setText("Pabna University of Science and Technology");
        Section.setText("Attendance Management System");
        back.setVisibility(View.INVISIBLE);
        logout.setOnClickListener(v -> {

            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("Do You Want To LogOut?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {

                        SharePrefManager.getInstance(this).logout();
                        Intent intent = new Intent(MainActivity.this, LogActivity.class);
                        startActivity(intent);
                        finish();
                    })

                    .setNegativeButton("No", (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        });
        //RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);

        recyclerView.setAdapter(classAdapter);
        loadClass();
        if (classItems.size() >0){
            empty_notes_view.setVisibility(View.GONE);
        }else {
            empty_notes_view.setVisibility(View.VISIBLE);
        }
        //floatingActionButton.setOnClickListener(view -> fab());
        classAdapter.setOnItemClickListener(this::gotoItem);


    }
    private void gotoItem(int position) {
        Intent i = new Intent(this, StudentActivity.class);
        i.putExtra("session", classItems.get(position).getSession());
        i.putExtra("coursecode", classItems.get(position).getCc());
        i.putExtra("position", position);
        i.putExtra("cid", classItems.get(position).getId());
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void loadClass(){
        pd.setMessage("Loading Classes.....");
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_loadClassInfo, response -> {
            classItems.clear();
            pd.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean error = jsonObject.getBoolean("error");
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                if (!error){
                    for (int i = 0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);

                        String in = object.getString("id");
                        int id =Integer.parseInt(in);
                        String session = object.getString("session");
                        String coursecode = object.getString("coursecode");
                        String ct = object.getString("ct");

                        classItems.add(new ClassItems(id, session, coursecode,ct));
                        classAdapter.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Problem", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}