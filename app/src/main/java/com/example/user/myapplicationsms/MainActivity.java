package com.example.user.myapplicationsms;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener{
    // Initialize variables
    AutoCompleteTextView textView=null;
    private ArrayAdapter<String> adapter;
    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();
    String toNumberValue="";
    String number =" ";
    EditText textSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button Send = (Button) findViewById(R.id.button);
        // Initialize AutoCompleteTextView values

        textView = (AutoCompleteTextView) findViewById(R.id.toNumber);
        textSMS = (EditText) findViewById(R.id.editTextSMS);
        //Create adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);

        // Read contact data and add data to ArrayAdapter
        // ArrayAdapter used by AutoCompleteTextView
        readContactData();
    }
        /********** Button Click pass textView object ***********/
    private View.OnClickListener BtnAction() {
        return new View.OnClickListener() {
            public void onClick(View v) {
				/* */
                make_message();
                makePhone_message();
            }

        };
    }

    private void make_message(){

        String message = textSMS.getText().toString();
        String phoneNo = textView.getText().toString();
        if (phoneNo.length() > 0 && message.length() > 0) {
            sendMessage(phoneNo, message);
        }else {
            Toast.makeText(this,"Please enter phone number and message", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendMessage(String phoneNo,String message) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(this,"Message sent", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {

            Toast.makeText(this, "Message fail.Please try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void makePhone_message(){
        String message = textSMS.getText().toString();
        if (toNumberValue.length() > 0 && message.length() > 0) {
            send_Message(toNumberValue, message);
        }else {
            Toast.makeText(this,"Please enter phone number and message", Toast.LENGTH_SHORT).show();
        }
    }

    private void send_Message(String number,String message) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toNumberValue, null, message, null, null);
            Toast.makeText(this,"Message sent", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {

            Toast.makeText(this,"Message fail.Please try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    private void readContactData() {

        try {

            /*********** Reading Contacts Name And Number **********/

            String phoneNumber = "";
            ContentResolver cr =
                    this.getContentResolver();

            //Query to get contact name

            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            // If data data found in contacts
            if (cur.getCount() > 0) {
                Log.i("AutocompleteContacts", "Reading   contacts..");
                int k=0;
                String name = "";
                while (cur.moveToNext())
                {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //Check contact have phone number
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {

                        //Create query to get phone number by contact id
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[] { id }, null);
                        int j=0;

                        while (pCur.moveToNext())
                        {
                            // Sometimes get multiple data
                            if(j==0)
                            {
                                // Get Phone number
                                phoneNumber =""+pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                // Add contacts names to adapter
                                adapter.add(name);

                                // Add ArrayList names to adapter
                                phoneValueArr.add(phoneNumber.toString());
                                nameValueArr.add(name.toString());

                                j++;
                                k++;
                            }
                        }
                        pCur.close();
                    }

                }

            }
            cur.close();


        } catch (Exception e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        // Get Array index value for selected name
        int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));

        // If name exist in name ArrayList
        if (i >= 0) {

            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        }

    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

}