package com.rhr.ams;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.rhr.ams.Utis.MyCalendar;
import com.rhr.ams.Utis.RequestHandler;
import com.rhr.ams.Adapter.StudentAdapter;
import com.rhr.ams.Utis.Constants;
import com.rhr.ams.Model.StudentItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {

    TextView title,section;
    MyCalendar myCalendar=new MyCalendar();
    String intent_session,intent_coursecode;
    int position;
    StudentAdapter studentAdapter;
    ArrayList<StudentItems> studentItems=new ArrayList<>();
    FloatingActionButton floatingActionButton,fab_date,fab_sheet;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    public  long cid;
    ImageView back,save;
    String tbl_name;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        //recyclerview
        recyclerView=findViewById(R.id.studentrecycle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        studentAdapter=new StudentAdapter(this,studentItems);
        recyclerView.setAdapter(studentAdapter);
        //toolbar
        title=findViewById(R.id.title_tool);
        section=findViewById(R.id.section_tool);
        back=findViewById(R.id.back);
        save=findViewById(R.id.save);
        pd = new ProgressDialog(this);
        //other
        Intent intent = new Intent();
        intent=getIntent();
        //dialoge_title=findViewById(R.id.title_of_classdialogedt);
        intent_session = intent.getStringExtra("session");
        intent_coursecode = intent.getStringExtra("coursecode");
        cid=intent.getIntExtra("cid",-1);
        position = intent.getIntExtra("position", -1);
        tbl_name = intent_coursecode+intent_session;

        loadStudent(tbl_name);

        if(intent_session!=null&&intent_coursecode!=null) {
            title.setText(intent_session);
            section.setText(intent_coursecode);
        }

        back.setOnClickListener(v->onBackPressed());
        save.setOnClickListener(v->saveStatus(tbl_name));
        floatingActionButton=findViewById(R.id.fab_student);
        fab_date=findViewById(R.id.fab_date);
        fab_sheet=findViewById(R.id.fab_sheet);

        fab_date.setOnClickListener(v->showDateDialog());
        studentAdapter.setOnItemClickListener(this::makechange);
    }

    private void saveStatus(String table) {
        pd.setMessage("Loading Students.....");
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_saveAttendanceData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String data = new Gson().toJson(studentItems);
                System.out.println(data);
                params.put("data",data);
                params.put("tblname",table);
                params.put("colname", myCalendar.getData());
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showDateDialog() {
        myCalendar.show(getSupportFragmentManager(),"");
        String mota= (String) section.getText();
        if(mota.contains("|")) Toast.makeText(this,"You already have a date specified", Toast.LENGTH_LONG).show();
        else myCalendar.setOnCalendarClickListener(this::OnOKClicked);
    }
    private void OnOKClicked(int year, int month, int day) {
        String bt=myCalendar.getData();
        myCalendar.SetData(year, month, day);
        myCalendar.SetData(year, month, day);

        //loadStatus();
    }
    public void makechange(int position) {
        String status=studentItems.get(position).getStatus();
        if(status.equals("P")) {
            status="A";
        } else {
            status="P";
        }
        studentItems.get(position).setStatus(status);
        studentAdapter.notifyDataSetChanged();
    }
    private void loadStudent(String table){
        pd.setMessage("Loading.....");
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_loadStudentInfo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                studentItems.clear();
                pd.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (!error){
                        for (int i = 0;i < jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            String in = object.getString("id");
                            int id =Integer.parseInt(in);
                            String roll = object.getString("Roll");
                            String name = object.getString("Name");
                            studentItems.add(new StudentItems(id,roll,name));
                            studentAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("tblname",table);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}