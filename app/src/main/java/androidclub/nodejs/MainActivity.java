package androidclub.nodejs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView mac,res;
    Button go;
    EditText reg;
    String macAdd="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting Mac Address
        mac=findViewById(R.id.mac);
        macAdd=getMacAddr();
        mac.setText("Mac Address : \n"+macAdd);

        res=findViewById(R.id.res);
        reg=findViewById(R.id.reg);
        go=findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResults(reg.getText().toString().substring(reg.length() - 4));
            }
        });
    }
    public void getResults(final String data)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    URL u = new URL("https://android-club-project.herokuapp.com/upload_details?reg_no="+data+"&mac="+macAdd);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();
                    InputStream in = c.getInputStream();
                    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    in.read(buffer);
                    bo.write(buffer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                res.setText("Secret Text Recieved :\n"+bo.toString().trim());
                                bo.close();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {return "";}
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {res1.append(Integer.toHexString(b & 0xFF) + ":");}
                if (res1.length() > 0) {res1.deleteCharAt(res1.length() - 1);}
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }
}
