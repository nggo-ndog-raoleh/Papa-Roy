package arkavidia.ljkeyboard.Model;

/**
 * Created by axellageraldinc on 08/12/17.
 */

public class Template {
    private String id, judulTemplate, isiTemplate, tipeTemplate;

    public Template(String id, String judulTemplate, String isiTemplate, String tipeTemplate) {
        this.judulTemplate = judulTemplate;
        this.isiTemplate = isiTemplate;
        this.tipeTemplate = tipeTemplate;
        this.id = id;
    }

    public Template() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudulTemplate() {
        return judulTemplate;
    }

    public void setJudulTemplate(String judulTemplate) {
        this.judulTemplate = judulTemplate;
    }

    public String getIsiTemplate() {
        return isiTemplate;
    }

    public void setIsiTemplate(String isiTemplate) {
        this.isiTemplate = isiTemplate;
    }

    public String getTipeTemplate() {
        return tipeTemplate;
    }

    public void setTipeTemplate(String tipeTemplate) {
        this.tipeTemplate = tipeTemplate;
    }
}
