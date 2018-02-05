package arkavidia.ljkeyboard.Model.Retrofit.Cost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by axellageraldinc on 25/01/18.
 */

public class Query {
    @SerializedName("origin")
    @Expose
    private String origin;
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("weight")
    @Expose
    private Integer weight;
    @SerializedName("courier")
    @Expose
    private String courier;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }
}
