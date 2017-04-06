package leszcz_team.wulkanowy.activity.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import leszcz_team.wulkanowy.R;

public class MainActivity extends Activity {

    private static final String[] COUNTRIES = new String[] {
            "Powiat jarosławski", "Powiat przeworski"
    };

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.countyText);
        textView.setAdapter(adapter);

    }

    public void login(View a){
        EditText adressEmail = (EditText)findViewById(R.id.emailText);
        EditText passwordText = (EditText)findViewById(R.id.passwordText);
        EditText countyText = (EditText)findViewById(R.id.countyText);
        String password = passwordText.getText().toString();
        String email = adressEmail.getText().toString();
        String county = countyText.getText().toString();

        if (password.isEmpty() || email.isEmpty() || county.isEmpty()){
            Toast.makeText(this, R.string.data_text, Toast.LENGTH_SHORT).show();
        }
        else{
            new Login(email, password, county, this).execute();
        }

    }
}
