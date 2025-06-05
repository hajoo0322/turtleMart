package turtleMart.review.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public record CreateReviewRequest(@NotNull Long orderItemId,
                                  @NotBlank @Size(max = 20) String title,
                                  @NotBlank @Size(max = 255) String content,
                                  @NotNull @Max(5) Integer rating,
                                  List<String> imageUrlList,
                                  List<@Valid CreateTemplateChoiceRequest> templateChoiceList) {
}
