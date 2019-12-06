package edu.rit.cs.model;

import java.io.Serializable;
import java.util.Iterator;

public class Partition extends Thread implements Serializable {

    private Iterator rows;
    private String result;

    public Partition(){
        this.result = "";
    }

    public Partition(Iterator rows){
        this.rows = rows;
    }

    public void setRows(Iterator rows){
        this.rows = rows;
    }

    @Override
    public void run(){
        this.setResult(Config.calcDivIndex(rows));
        System.out.println(result);
    }

    public void setResult(String result){
        this.result = result;
    }

    public String getRows(){
        return rows.toString();
    }
    public String getResult(){
        return result;
    }
}
