package arkavidia.ljkeyboard.Model.Retrofit.Cost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by axellageraldinc on 28/01/18.
 */

public class Result {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("costs")
    @Expose
    private List<Costs> costs = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Costs> getCosts() {
        return costs;
    }

    public void setCosts(List<Costs> costs) {
        this.costs = costs;
    }

}
