package com.foxioo.ritfclient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient
{
    private String url;
    private int timeout;
    
    public ApiClient(String url, int timeout)
    {
        this.url = url;
        this.timeout = timeout;
    }

    /** change host URL */
    public void setUrl(String url) {
        this.url = url;
    }

    /** change timeout */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /** sends an HTTP GET request to the host; returns whatever the host returns or null if nothing is returned; */
    public String get(String endpoint)
    {
        try {
            URL urlex = new URL(url + endpoint);
            HttpURLConnection conn = (HttpURLConnection) urlex.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);

            if (conn.getResponseCode() != 200) {
                return null; 
            }

            BufferedReader buffread = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder strbuild = new StringBuilder();
            String line;
            
            while ((line = buffread.readLine()) != null) {
                strbuild.append(line);
            }

            buffread.close();
            conn.disconnect();

            return strbuild.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /** sends an HTTP GET request to the host; returns byte table */
    public byte[] getbytes(String endpoint)
    {
        try {
            URL urlex = new URL(url + endpoint);
            HttpURLConnection conn = (HttpURLConnection) urlex.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);

            if (conn.getResponseCode() != 200) {
                return null; 
            }

            InputStream in = conn.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int line;

            while ((line = in.read(data)) != -1) {
                buffer.write(data, 0, line);
            }

            in.close();
            conn.disconnect();
            return buffer.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}