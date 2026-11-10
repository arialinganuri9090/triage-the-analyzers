package team.projectpulse.ram.document;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequirementDocumentSpecsTest {

    @Mock Root<RequirementDocument> root;
    @Mock CriteriaQuery<?> query;
    @Mock CriteriaBuilder criteriaBuilder;
    @Mock Path<String> titlePath;
    @Mock Expression<String> lowerTitle;

    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        this.originalLocale = Locale.getDefault();
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(this.originalLocale);
    }

    @Test
    void testHasTitleLikeLowercasesIndependentlyOfDefaultLocale() {
        // Turkish lowercases "I" to the dotless "ı", which would never match MySQL's LOWER(title)
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        given(root.<String>get("title")).willReturn(titlePath);
        given(criteriaBuilder.lower(any())).willReturn(lowerTitle);

        RequirementDocumentSpecs.hasTitleLike("TITLE").toPredicate(root, query, criteriaBuilder);

        ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
        verify(criteriaBuilder).like(eq(lowerTitle), pattern.capture());
        assertThat(pattern.getValue()).isEqualTo("%title%");
    }
}
