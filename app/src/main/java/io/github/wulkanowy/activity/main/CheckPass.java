package io.github.wulkanowy.activity.main;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CheckPass {

    Connection.Response page;

    public CheckPass (Connection.Response pageT){
        page = pageT;
    }

    public String start (){
        try{
            Document document = page.parse();
            Elements mesageAlert = document.getElementsByClass("ErrorMessage center");
            return mesageAlert.text();
        }
        catch (IOException e){
            return e.toString();
        }


    }

}
