package com.rhr.ams;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rhr.ams.Adapter.ClassAdapter;
import com.rhr.ams.Utis.RequestHandler;
import com.rhr.ams.Model.ClassItems;
import com.rhr.ams.Utis.Constants;
import com.rhr.ams.Utis.SharePrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    ArrayList<ClassItems> classItems = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    TextView title, Section;
    ImageView back, save;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SharePrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, LogActivity.class));
        }

        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerview);
        title = findViewById(R.id.title_tool);
        Section = findViewById(R.id.section_tool);
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        pd = new ProgressDialog(this);
        //toolbar set
        title.setText("Pabna University of Science and Technology");
        Section.setText("Attendance Management");
        back.setVisibility(View.INVISIBLE);
        save.setOnClickListener(v -> {
            SharePrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, LogActivity.class));
        });
        //RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);

        recyclerView.setAdapter(classAdapter);
        loadClass();
        //floatingActionButton.setOnClickListener(view -> fab());
        classAdapter.setOnItemClickListener(this::gotoItem);


    }
    private void gotoItem(int position) {
        Intent i = new Intent(this, StudentActivity.class);
        i.putExtra("session", classItems.get(position).getName());
        i.putExtra("coursecode", classItems.get(position).getSection());
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_loadClassInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

                            classItems.add(new ClassItems(id, session, coursecode));
                            classAdapter.notifyDataSetChanged();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Problem", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}