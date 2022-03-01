package ca.cmpt276.parentApp.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import ca.cmpt276.parentApp.R;
import ca.cmpt276.parentApp.model.CoinFlip;
import ca.cmpt276.parentApp.model.CoinFlipManager;
import ca.cmpt276.parentApp.model.GeneralManager;

/**
 *  CoinFlipsAdapter class - an array adapter sets list view for coin flip history
 */
public class CoinFlipsAdapter extends ArrayAdapter<CoinFlip> {

    private final int HEADS = 0;
    private Context context;
    private int resource;

    private GeneralManager genManager = GeneralManager.getInstance();
    private CoinFlipManager coinFlipManager = genManager.getCoinFlipManager();

    public CoinFlipsAdapter(Context context, int resource, ArrayList<CoinFlip> coinFlips) {
        super(context, resource, coinFlips);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String dateTime;
        String picker;
        String result;
        int resultImageID;
        CoinFlip coinFlip = getItem(position);

        dateTime = coinFlip.getDateTime();


        int coinFlipResult = coinFlip.getCoinResult();
        if (coinFlipResult == HEADS) {
            result = context.getString(R.string.heads);
        } else {
            result = context.getString(R.string.tails);
        }

        if (coinFlip.getPickerName() == null) {
            resultImageID = R.drawable.na;
        } else {
            if (coinFlip.isWon()) {
                resultImageID = R.drawable.ic_launcher_check;
            } else {
                resultImageID = R.drawable.ic_launcher_x;
            }
        }

        if (coinFlip.getPickerName() == null) {
            picker = context.getString(R.string.no_child_picked);
        } else {
            picker = coinFlip.getPickerName();
        }



        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
        }

        TextView textViewDateTime = (TextView) convertView.findViewById(R.id.textViewDateTime);
        TextView textViewPicker = (TextView) convertView.findViewById(R.id.textViewPicker);
        TextView textViewFlipResult = (TextView) convertView.findViewById(R.id.textViewFlipResult);
        ImageView imageViewResult = (ImageView) convertView.findViewById(R.id.imageViewResult);
        ImageView imageViewPicker = (ImageView) convertView.findViewById(R.id.imageViewPicker);

        textViewDateTime.setText(dateTime);
        textViewPicker.setText(picker);
        textViewFlipResult.setText(result);
        imageViewResult.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), resultImageID, null));

        if (coinFlip.getPickerName() == null) {
            imageViewPicker.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.na, null));
        }else {
            int index = genManager.getChildren().getIndexOfChild(picker);
            Uri pickerImageID = genManager.getChildren().getChildPicAt(index);
            if (pickerImageID != null) {
                imageViewPicker.setImageURI(pickerImageID);
            } else {
                imageViewPicker.setImageResource(R.drawable.default_photo);
            }
        }

        return convertView;
    }
}
