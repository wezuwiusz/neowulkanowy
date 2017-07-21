package io.github.wulkanowy.activity.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedHashMap;

import io.github.wulkanowy.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AlertDialog.Builder(this)
                .setTitle(R.string.warning_label)
                .setMessage(R.string.warning_text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        autoComplete();
    }

    private void autoComplete(){

        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.countyText);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.counties);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        textView.setAdapter(adapter);
    }

    public void login(View a){
        EditText adressEmail = (EditText)findViewById(R.id.emailText);
        EditText passwordText = (EditText)findViewById(R.id.passwordText);
        EditText countyText = (EditText)findViewById(R.id.countyText);
        String password = passwordText.getText().toString();
        String email = adressEmail.getText().toString();
        String county = countyText.getText().toString();

        String[] keys = this.getResources().getStringArray(R.array.counties);
        String[] values = this.getResources().getStringArray(R.array.counties_values);
        LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();

        for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
            map.put(keys[i], values[i]);
        }

        if(map.containsKey(county)) {
            county = map.get(county);
        }

        if (!email.isEmpty() && !password.isEmpty() && !county.isEmpty()){
            new LoginTask(this).execute(email, password, county);
        } else {
            Toast.makeText(this, R.string.data_text, Toast.LENGTH_SHORT).show();
        }
    }
}
