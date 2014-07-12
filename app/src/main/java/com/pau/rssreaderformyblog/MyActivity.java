package com.pau.rssreaderformyblog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MyActivity extends Activity {

    ArrayList<String> xmlTitleList = new ArrayList<String>();
    ArrayList<String> xmlLink=new ArrayList<String>();

    public class BackgroundProcesses extends AsyncTask <Void,Void,Void> {

        private ProgressDialog dialog =new ProgressDialog(MyActivity.this);

        protected void onPostExecute(Void result){
            ListView listView1 = (ListView) findViewById(R.id.listView);
            MyCustomAdapter adapter = new MyCustomAdapter(MyActivity.this, R.layout.satirlayout, xmlTitleList);
            listView1.setAdapter(adapter);

            dialog.dismiss();
        }

        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dialog.setMessage("Yükleniyor...");

            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            xmlTitleList=getListFromXML("http://ahmettemel.wordpress.com/feed/");
            xmlLink=getLinkFromXml("http://ahmettemel.wordpress.com/feed/");


            return null;
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> xmlList) {
            super(context, textViewResourceId, xmlList);
            // TODO Auto-generated constructor stub

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            View row = convertView;


            if(row==null){
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.satirlayout, parent, false);
            }

            TextView label=(TextView)row.findViewById(R.id.title);
            label.setText(xmlTitleList.get(position));

            ImageView image =(ImageView)row.findViewById(R.id.image);
            image.setImageResource(R.drawable.ic_launcher);

            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        new BackgroundProcesses().execute();

        ListView listView1 = (ListView)findViewById(R.id.listView);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                Uri link = Uri.parse(xmlLink.get(position));

                final Intent openBrowser = new Intent(Intent.ACTION_VIEW,link);

                startActivity(openBrowser);

            }

        });
    }


   public ArrayList<String> getListFromXML(String string){
        ArrayList<String> list = new ArrayList<String>();

       try {
           URL url = new URL(string);
           DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
           DocumentBuilder dBuilder = dFactory.newDocumentBuilder();

           Document document= dBuilder.parse(new InputSource(url.openStream()));
           document.getDocumentElement().normalize();

           // item etiketine sahip elementleri NodeList olarak tutar
           NodeList nodeList = document.getElementsByTagName("item");

           for (int i=0; i< nodeList.getLength(); i++){//item etiketine sahip elemanların olduğu listeden
                                                       //title etiketine sahip olanlar almak için döngü
               Node node = nodeList.item(i);
               Element element = (Element) node;

               NodeList nodeListText = element.getElementsByTagName("title");
               Element elementText = (Element) nodeListText.item(0);

               // her title ı arrayliste ekletiyoruz.
               list.add(elementText.getChildNodes().item(0).getNodeValue());

           }
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (ParserConfigurationException e) {
           e.printStackTrace();
       } catch (SAXException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }

       return list;
   }
    public ArrayList<String> getLinkFromXml(String strng)  {

        ArrayList<String> list=new ArrayList<String>();

        try {

            URL url=new URL(strng);
            DocumentBuilderFactory dFactory=DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder=dFactory.newDocumentBuilder();

            Document document=dBuilder.parse(new InputSource(url.openStream()));
            document.getDocumentElement().normalize();

            NodeList nodeListCountry=document.getElementsByTagName("item");
            for (int i = 0; i < nodeListCountry.getLength(); i++) {
                Node node=nodeListCountry.item(i);
                Element elementMain=(Element) node;

                NodeList nodeListText=elementMain.getElementsByTagName("link");
                Element elementText=(Element) nodeListText.item(0);

                list.add(elementText.getChildNodes().item(0).getNodeValue());


            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return list;
    }

}
