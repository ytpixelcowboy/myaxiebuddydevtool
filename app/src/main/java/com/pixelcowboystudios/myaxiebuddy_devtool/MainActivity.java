package com.pixelcowboystudios.myaxiebuddy_devtool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> array_filter_name = new ArrayList<>();
    ArrayList<String> array_filter_id = new ArrayList<>();
    String val_filter = "cards";


    Button btn_format;
    EditText etxt_output;
    EditText etxt_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_format = findViewById(R.id.btn_format);
        etxt_input = findViewById(R.id.etxt_input);
        etxt_output = findViewById(R.id.etxt_output);


        //Insert Filter Variables
        array_filter_name.add("Cards");
        array_filter_id.add("card");

        array_filter_name.add("Runes");
        array_filter_id.add("runes");

        array_filter_name.add("Charms");
        array_filter_id.add("charms");

        array_filter_name.add("Status");
        array_filter_id.add("status");


        //
        Spinner filter = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item);
        adapter.addAll(array_filter_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                val_filter = array_filter_id.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ///
        btn_format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (val_filter){
                    case "card" :
                        etxt_output.setText("Cards formatter unsupported");
                        break;
                    case "runes" :
                        etxt_output.setText("Runes formatter unsupported");
                        break;
                    case "charms" :
                        try{
                            JSONArray jsonData = new JSONArray(); //holds the output

                            JSONObject jsonObject = new JSONObject(etxt_input.getText().toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            //Dev Note: parse the "items"
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject j = jsonArray.getJSONObject(i);

                                JSONObject j_season = null;
                                if(j.has("season") && !j.getString("season").equals("null")){
                                    j_season = j.getJSONObject("season");
                                }else{
                                    j_season = new JSONObject();
                                    j_season.put("id", 0);
                                    j_season.put("name", "null");
                                }

                                //Only get the charm if its inside the season range
                                if(j_season.getInt("id") == jsonObject.getInt("seasonIdLatest")){
                                    jsonData.put(j);
                                }
                            }

                            etxt_output.setText(jsonData.toString(4));
                        }catch (Exception e){
                            etxt_output.setText(e.toString());
                            Log.e("status", e.toString());
                        }
                        break;
                    case "status" :
                        try{
                            JSONArray j_input = new JSONArray(etxt_input.getText().toString());

                            JSONArray data = new JSONArray();
                            for(int i = 0; i < j_input.length(); i++){
                                JSONObject j = j_input.getJSONObject(i);

                                //Check if the data is a status before evauluating
                                if(j.getBoolean("status")){
                                    JSONObject J_data = j.getJSONObject("data");
                                    String valType = "Card Property";
                                    String valId = J_data.getString("name").toLowerCase(Locale.ROOT).trim().replaceAll(" ", "");


                                    if((J_data.getString("image").endsWith("_debuff") && J_data.getString("image").endsWith("_debuff.png")) || J_data.getString("image").contains("debuff_")){
                                        if(!valId.contains("secret-") || valId.contains("classbonus-")){
                                            valType = "Debuff";
                                        }
                                    }else if((J_data.getString("image").contains("_buff") && J_data.getString("image").endsWith("_buff.png")) || J_data.getString("image").contains("buff_")){
                                        if(!valId.contains("secret-") || valId.contains("classbonus-")){
                                            valType = "Buff";
                                        }
                                    }else if((J_data.getString("image").contains("_neutral") && J_data.getString("image").endsWith("_neutral.png")) || J_data.getString("image").contains("neutral_") && !J_data.getString("image").contains("_neutral_")){
                                        valType = "Neutral";
                                    }

                                    //
                                    data.put(new JSONObject()
                                            .put("id", J_data.getString("name").toLowerCase(Locale.ROOT).trim().replaceAll(" ", ""))
                                            .put("name", J_data.getString("name"))
                                            .put("description",J_data.getString("description"))
                                            .put("image", J_data.getString("image").replaceAll("status/", ""))
                                            .put("type", valType)
                                    );
                                }

                            }

                            etxt_output.setText(data.toString(4));
                        }catch (Exception e){
                            etxt_output.setText(e.toString());
                            Log.e("status", e.toString());
                        }
                        break;
                }
            }
        });
    }


}