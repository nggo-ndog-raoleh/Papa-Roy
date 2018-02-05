package arkavidia.ljkeyboard.Model.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by axellageraldinc on 24/01/18.
 */

public class Status {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("description")
    @Expose
    private String description;

    /**
     * No args constructor for use in serialization
     *
     */
    public Status() {
    }

    /**
     *
     * @param description
     * @param code
     */
    public Status(Integer code, String description) {
        super();
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
