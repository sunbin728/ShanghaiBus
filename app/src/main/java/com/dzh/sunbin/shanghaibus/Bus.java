package com.dzh.sunbin.shanghaibus;

/**
 * Created by sunbin on 15-12-28.
 */
public class Bus {
    String name;
    String url;
    public Bus(String _name, String _url) {
        this.name = _name;
        this.url = _url;
    }

    public String GetName(){
        return name;
    }

    public String GetUrl(){
        return url;
    }

    public void SetName(String _name){
        name = _name;
    }

    public void SetUrl(String _url){
        url = _url;
    }
}
