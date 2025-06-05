package turtleMart.review.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@NoArgsConstructor
@Document(indexName = "review")
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "elasticsearch/review/review-settings.json")
@Mapping(mappingPath = "elasticsearch/review/review-mappings.json")
public class ReviewDocument {

    @Id
    private Long id;

    private Long productId;

    private String title;

    private String content;

    private Integer rating;

    private ReviewDocument(Long id, Long productId, String title, String content, Integer rating){
        this.id = id;
        this.productId = productId;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public static ReviewDocument of(Long id, Long productId, String title, String content, Integer rating){
        return new ReviewDocument(id, productId, title, content, rating);
    }
}
