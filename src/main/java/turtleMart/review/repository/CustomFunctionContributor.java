package turtleMart.review.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.springframework.stereotype.Component;

import static org.hibernate.type.StandardBasicTypes.BOOLEAN;

@Slf4j
@Component
public class CustomFunctionContributor implements FunctionContributor {
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions
                .getFunctionRegistry()
                .registerPattern("match", "match(?1, ?2) against (?3 in boolean mode)",
                        functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(BOOLEAN));

        log.info("매치 쿼리 생성");
    }
}
