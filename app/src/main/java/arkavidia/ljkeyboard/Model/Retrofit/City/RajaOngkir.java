package arkavidia.ljkeyboard.Model.Retrofit.City;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import arkavidia.ljkeyboard.Model.Retrofit.City.Result;
import arkavidia.ljkeyboard.Model.Retrofit.Cost.Query;
import arkavidia.ljkeyboard.Model.Retrofit.Status;

/**
 * Created by axellageraldinc on 24/01/18.
 */

public class RajaOngkir {
    @SerializedName("query")
    @Expose
    private List<Query> query = null;
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public RajaOngkir() {
    }

    /**
     *
     * @param results
     * @param status
     * @param query
     */
    public RajaOngkir(List<Query> query, Status status, List<Result> results) {
        super();
        this.query = query;
        this.status = status;
        this.results = results;
    }

    public List<Query> getQuery() {
        return query;
    }

    public void setQuery(List<Query> query) {
        this.query = query;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
