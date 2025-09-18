package co.com.pragma.crediya.api.mapper;


import co.com.pragma.crediya.api.dto.ApprovedApplicationsSummaryResponse;
import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ApprovedApplicationsSummaryRestMapper {

    ApprovedApplicationsSummaryResponse toResponse(ApprovedApplicationSummary approvedApplicationSummary);

}
