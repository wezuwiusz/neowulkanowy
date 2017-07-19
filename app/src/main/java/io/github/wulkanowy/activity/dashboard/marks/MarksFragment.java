package io.github.wulkanowy.activity.dashboard.marks;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import io.github.wulkanowy.R;

public class MarksFragment extends Fragment {


    final String lista[] = {
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow"
    };

    public MarksFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new MarksModel(container.getContext()).execute();

        View view = inflater.inflate(R.layout.fragment_marks, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<String> array = new ArrayList<>(Arrays.asList(lista));
        ImageAdapter adapter = new ImageAdapter(view.getContext(),array);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public class MarksModel extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private Map<String, String> loginCookies;

        MarksModel(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String cookiesPath = mContext.getFilesDir().getPath() + "/cookies.txt";

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookiesPath));
                loginCookies = (Map<String, String>) ois.readObject();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.toString();
            }

            // get link to uonetplus-opiekun.vulcan.net.pl module
            String startPageUrl = "https://uonetplus.vulcan.net.pl/powiatjaroslawski/Start.mvc/Index";
            try {
                Document startPage = Jsoup.connect(startPageUrl)
                        .followRedirects(true)
                        .cookies(loginCookies)
                        .get();
                Elements studentTileLink = startPage.select(".panel.linkownia.pracownik.klient > a");
                String uonetPlusOpiekunUrl = studentTileLink.attr("href");

                // get context module cookie
                Connection.Response res = Jsoup.connect(uonetPlusOpiekunUrl)
                        .followRedirects(true)
                        .cookies(loginCookies)
                        .execute();
                loginCookies = res.cookies();

                // get marks view
                String marksPageUrl = "https://uonetplus-opiekun.vulcan.net.pl/powiatjaroslawski/005791/Oceny/Wszystkie?details=2";
                Document marksPage = Jsoup.connect(marksPageUrl)
                        .cookies(loginCookies)
                        .get();
                Elements marksRows = marksPage.select(".ocenySzczegoly-table > tbody > tr");
                for (Element element : marksRows) {
                    System.out.println("----------");
                    System.out.println("Subject: " + element.select("td:nth-child(1)").text());
                    System.out.println("Grade: " + element.select("td:nth-child(2)").text());
                    System.out.println("Description: " + element.select("td:nth-child(3)").text());
                    System.out.println("Weight: " + element.select("td:nth-child(4)").text());
                    System.out.println("Date: " + element.select("td:nth-child(5)").text());
                    System.out.println("Teacher: " + element.select("td:nth-child(6)").text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
