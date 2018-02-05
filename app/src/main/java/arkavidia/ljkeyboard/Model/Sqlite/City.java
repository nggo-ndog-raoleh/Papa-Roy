package arkavidia.ljkeyboard.Model.Sqlite;

/**
 * Created by axellageraldinc on 07/12/17.
 */

public class City {
    private String id;
    private String cityName;

    public City() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
