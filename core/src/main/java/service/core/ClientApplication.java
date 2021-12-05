package service.core;

import java.util.ArrayList;

public class ClientApplication {
    private long batchId;
    private ClientInfo info;
    private ArrayList<Quotation> quotations;

    public ClientApplication() {}

    public ClientApplication(long batchId, ClientInfo info, ArrayList<Quotation> quotations){
        this.batchId = batchId;
        this.info = info;
        this.quotations = quotations;
    }

    public ClientInfo getInfo() {
        return info;
    }

    public void setInfo(ClientInfo info) {
        this.info = info;
    }

    public ArrayList<Quotation> getQuotations() {
        return quotations;
    }

    public void addQuotations(Quotation quotation) {
        this.quotations.add(quotation);
    }

    public void setQuotations(ArrayList<Quotation> quotations){
        this.quotations = quotations;
    }

    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }

    public long getBatchId() {
        return batchId;
    }
}
