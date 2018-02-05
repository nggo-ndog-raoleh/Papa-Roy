package arkavidia.ljkeyboard.Model.Retrofit.City;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by axellageraldinc on 24/01/18.
 */

public class Result {
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("province_id")
    @Expose
    private String provinceId;
    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("postal_code")
    @Expose
    private String postalCode;

    /**
     * No args constructor for use in serialization
     *
     */
    public Result() {
    }

    /**
     *
     * @param provinceId
     * @param postalCode
     * @param cityId
     * @param cityName
     * @param province
     * @param type
     */
    public Result(String cityId, String provinceId, String province, String type, String cityName, String postalCode) {
        super();
        this.cityId = cityId;
        this.provinceId = provinceId;
        this.province = province;
        this.type = type;
        this.cityName = cityName;
        this.postalCode = postalCode;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
