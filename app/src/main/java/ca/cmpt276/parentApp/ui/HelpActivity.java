package ca.cmpt276.parentApp.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import ca.cmpt276.parentApp.R;


/**
 * Help Activity - display information about team members and resource links
 */
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setTitle(R.string.title_activity_help);

        setupTeam();
        displayLinks();

    }

    private void setupTeam(){
        TextView teamText = findViewById(R.id.team_info);
        teamText.setText(getString(R.string.team_info));
    }

    private void displayLinks(){
        setupLinks(findViewById(R.id.icon_link));
        setupLinks(findViewById(R.id.bg_link));
        setupLinks(findViewById(R.id.other_link));
    }

    private void setupLinks(TextView tv){
        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
