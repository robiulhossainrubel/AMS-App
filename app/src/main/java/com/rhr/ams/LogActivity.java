package com.rhr.ams;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rhr.ams.Utis.Constants;
import com.rhr.ams.Utis.RequestHandler;
import com.rhr.ams.Utis.SharePrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogActivity extends AppCompatActivity {

    private EditText email,pass;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        if (SharePrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        Button login = findViewById(R.id.login);

        pd = new ProgressDialog(this);
        pd.setMessage("Plese wait...");

        login.setOnClickListener(v -> {
            teacherLogin();
        });

    }
    private void teacherLogin(){
        final String username = email.getText().toString().trim();
        final String password = pass.getText().toString().trim();

        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_teacherLOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("error")){
                        SharePrefManager.getInstance(getApplicationContext()).userLogin(object.getString("name"),object.getString("email"),object.getString("department"));
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",username);
                params.put("password",password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}