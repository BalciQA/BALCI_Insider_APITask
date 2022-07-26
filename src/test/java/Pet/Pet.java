package Pet;

import java.util.List;
import com.google.gson.annotations.*;

public class Pet {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("category")
    @Expose
    private Category category;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("photoUrls")
    @Expose
    private List<String> photoUrls = null;

    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

    @SerializedName("status")
    @Expose
    private String status;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
