package com.developer.abhinavraj.searchwidgetexample.models;

import java.io.Serializable;

/**
 * Created by kshitij on 19/1/18.
 */

public class SingleProductModel implements Serializable {

    private long prid;
    private long no_of_items;
    private String useremail, usermobile, prname, prprice, primage, prdesc;

    public SingleProductModel() {
    }

    public SingleProductModel(long prid, long no_of_items, String useremail, String usermobile, String prname, String prprice, String primage, String prdesc) {
        this.prid = prid;
        this.no_of_items = no_of_items;
        this.useremail = useremail;
        this.usermobile = usermobile;
        this.prname = prname;
        this.prprice = prprice;
        this.primage = primage;
        this.prdesc = prdesc;
    }

    public String getUsermobile() {
        return usermobile;
    }

    public void setUsermobile(String usermobile) {
        this.usermobile = usermobile;
    }

    public long getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(long no_of_items) {
        this.no_of_items = no_of_items;
    }

    public String getPrdesc() {
        return prdesc;
    }

    public void setPrdesc(String prdesc) {
        this.prdesc = prdesc;
    }

    public long getPrid() {
        return prid;
    }

    public void setPrid(long prid) {
        this.prid = prid;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getPrname() {
        return prname;
    }

    public void setPrname(String prname) {
        this.prname = prname;
    }

    public String getPrprice() {
        return prprice;
    }

    public void setPrprice(String prprice) {
        this.prprice = prprice;
    }

    public String getPrimage() {
        return primage;
    }

    public void setPrimage(String primage) {
        this.primage = primage;
    }
}
