package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class FavoriteMovie {

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("media_id")
    private int mediaId;

    private boolean favorite;

}
