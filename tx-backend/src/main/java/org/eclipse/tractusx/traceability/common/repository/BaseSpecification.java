/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.common.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Getter
public abstract class BaseSpecification<T> implements Specification<T> {

    private final SearchCriteriaFilter searchCriteriaFilter;

    protected BaseSpecification(SearchCriteriaFilter searchCriteriaFilter) {
        this.searchCriteriaFilter = searchCriteriaFilter;
    }

    protected Predicate createPredicate(SearchCriteriaFilter criteria, Root<?> root, CriteriaBuilder builder) {
        if (criteria.getStrategy().equals(SearchCriteriaStrategy.EQUAL)) {
            return builder.equal(
                    root.<String>get(criteria.getKey()).as(String.class),
                    criteria.getValue());
        }
        if (criteria.getStrategy().equals(SearchCriteriaStrategy.STARTS_WITH)) {
            return builder.like(
                    root.get(criteria.getKey()),
                    criteria.getValue() + "%");
        }
        if (criteria.getStrategy().equals(SearchCriteriaStrategy.AT_LOCAL_DATE)) {
            final LocalDate localDate = LocalDate.parse(criteria.getValue());
            Predicate startingFrom = builder.greaterThanOrEqualTo(root.get(criteria.getKey()),
                    LocalDateTime.of(localDate, LocalTime.MIN));
            Predicate endingAt = builder.lessThanOrEqualTo(root.get(criteria.getKey()),
                    LocalDateTime.of(localDate, LocalTime.MAX));
            return builder.and(startingFrom, endingAt);
        }
        return null;
    }

    public static <T> Specification<T> toSpecification(List<? extends BaseSpecification<T>> specifications) {
        if (specifications.isEmpty()) {
            return null;
        }

        Map<String, List<BaseSpecification<T>>> groupedSpecifications = specifications.stream()
                .collect(groupingBy(spec -> spec.getSearchCriteriaFilter().getKey()));

        Map<FieldOperatorMap, Specification<T>> fieldSpecsByFieldName = groupedSpecifications.values().stream()
                .map(BaseSpecification::combineFieldSpecifications)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return combineSpecifications(fieldSpecsByFieldName);
    }

    // Combines all fields into one specification
    private static <T> Specification<T> combineSpecifications(Map<FieldOperatorMap, Specification<T>> fieldSpecsByFieldName) {
        List<Specification<T>> andSpecifications = extractSpecificationsWithOperator(fieldSpecsByFieldName, SearchCriteriaOperator.AND);
        List<Specification<T>> orSpecifications = extractSpecificationsWithOperator(fieldSpecsByFieldName, SearchCriteriaOperator.OR);

        return Specification.where(combineWithSpecificationsWith(andSpecifications, SearchCriteriaOperator.AND))
                .and(combineWithSpecificationsWith(orSpecifications, SearchCriteriaOperator.OR));
    }

    private static <T> List<Specification<T>> extractSpecificationsWithOperator(Map<FieldOperatorMap, Specification<T>> fieldSpecsByFieldName, SearchCriteriaOperator searchCriteriaOperator) {
        return fieldSpecsByFieldName.entrySet().stream()
                .filter(entry -> searchCriteriaOperator.equals(entry.getKey().operator))
                .map(Map.Entry::getValue)
                .toList();
    }


    // Combines specific field specifications
    private static <T> Map.Entry<FieldOperatorMap, Specification<T>> combineFieldSpecifications(List<BaseSpecification<T>> specifications) {
        // TODO: Add here date range handling if list has BEFORE_LOCAL_DATE and AFTER_LOCAL_DATE then combine those with AND
        FieldOperatorMap fieldOperatorMap = FieldOperatorMap.builder()
                .fieldName(specifications.get(0).searchCriteriaFilter.getKey())
                .operator(specifications.get(0).searchCriteriaFilter.getOperator())
                .build();

        Specification<T> result = combineWithSpecificationsWith(
                specifications.stream().map(baseSpec -> (Specification<T>) baseSpec).toList(),
                SearchCriteriaOperator.OR);

        return Map.entry(fieldOperatorMap, result);
    }

    private static <T> Specification<T> combineWithSpecificationsWith(List<Specification<T>> specifications, SearchCriteriaOperator searchCriteriaOperator) {
        if (specifications.isEmpty()) {
            return null;
        }
        Specification<T> result = specifications.get(0);
        for (int i = 1; i < specifications.size(); i++) {
            if (SearchCriteriaOperator.OR.equals(searchCriteriaOperator)) {
                result = Specification.where(result).or(specifications.get(i));
            } else {
                result = Specification.where(result).and(specifications.get(i));
            }
        }
        return result;
    }
}
