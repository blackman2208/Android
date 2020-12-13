package com.example.apimock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Xe> Xes = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private  XeAdapter xeAdapter;
    Dialog dialog;
    private  String url="https://5fd63c9fea55c40016041d85.mockapi.io/api/trongnhan/xe";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh =findViewById(R.id.swipedown);
        recyclerView = findViewById(R.id.rcv_catetory);

        dialog = new Dialog(this);
        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
                public void run() {
                    Xes.clear();
                    GetData();
                }
        });
    }


    @Override
    public void onRefresh() {
        Xes.clear();
        GetData();
    }

    private void GetData() {
        refresh.setRefreshing(true);
        arrayRequest= new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Xe x = new Xe();
                        x.setId(jsonObject.getInt("id"));
                        x.setName(jsonObject.getString("name"));
                        Xes.add(x);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(Xes);
                refresh.setRefreshing(false);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error make by API server!!!", Toast.LENGTH_SHORT);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(arrayRequest);

    }


    private void adapterPush(ArrayList<Xe> Xes){
        xeAdapter = new XeAdapter(this, Xes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(xeAdapter);

    }

    public void addXe(View v){
        TextView close;
        EditText edt_name;
        Button btnSubmit;

        dialog.setContentView(R.layout.activity_modcat);


        close= dialog.findViewById(R.id.txt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edt_name= dialog.findViewById(R.id.name);
        btnSubmit= dialog.findViewById(R.id.submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data= edt_name.getText().toString();
                Submit(data);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void Submit(String data){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                refresh.post(new Runnable() {
                    @Override
                    public void run() {
                        Xes.clear();
                        GetData();
                    }
                });
                Toast.makeText(getApplicationContext(), "Them Thanh cong", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Them That bai", Toast.LENGTH_SHORT ).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name", data);
                return  params;
            }
        };
        Volley.newRequestQueue(this).add(request);

    }
}