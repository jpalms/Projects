package edu.rit.cs.model;

public class FingerTable {
    private int index, ideal, actual;
    private String ipAddress;

    public FingerTable(int index, int ideal, int actual, String ipAddress){
        this.index = index;
        this.ideal = ideal;
        this.actual = actual;
        this.ipAddress = ipAddress;
    }

    public FingerTable(){

    }

    //---------------------- Getters -------------------------

    /**
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    public int getActual() {
        return actual;
    }

    public int getIdeal() {
        return ideal;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    //----------------------- Setters -------------------------


    public void setIndex(int index) {
        this.index = index;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    public void setIdeal(int ideal) {
        this.ideal = ideal;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
