package turtleMart.review.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateReviewRequest(String title,
                                  String content,
                                  Integer rating,
                                  List<String> imageUrlList,
                                  List<@Valid UpdateTemplateChoiceRequest> templateChoiceList) {
}
